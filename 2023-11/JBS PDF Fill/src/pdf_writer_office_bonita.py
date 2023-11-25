from pdf_writer_office import OfficePDFWriter
import pdfrw_by_keys

class BonitaPDFWriter(OfficePDFWriter):
    """
    Represents the PDF Writer for the Bonita office.
    """
    def __init__(self, office_excel_path, office_excel_name, output_folder):
        """
        Initializes Bonita office-specific variables and parent class's
        file and folder variables.

        office_excel_path - The full file path to the office Excel sheet.
        office_excel_name - The name of the office Excel sheet.
        output_dir_string - The name of the output folder.
        """
        super().__init__(office_excel_path, office_excel_name, output_folder)
        self.bonita_info = {
            "IIA Address": "9240 Bonita Beach Road, Suite 2211, Bonita Springs, FL 34135",
            "IIA Telephone": "239-676-5676"
        }
        self.rename_cols = {
            'Contract / Policy #': "Investment Account",
            'IAR': "Investment Advisor",
            'IAR Email': "IIA Email",
            'AccountID-18Char': 'Household ID'
        }
        self.relevant_cols = [
            'AccountID-18Char', 'Contract / Policy #',
            'First Name', 'Last Name',
            'Source/Carrier', 'Product Name', 'If Other, Please Specify',
            'IAR', 'IAR Email'
        ]

    def make_filtered_df(self):
        """
        Filters the office_df to only relevant columns and removes rows that
        have missing values in the 'AccountsID-18Char' and 'Contract / Policy #'
        columns. It will add the rows with those missing values to the
        issues_list and fill the rest of the missing values with empty strings.
        """
        filtered_df = self.office_df[self.relevant_cols].copy()
        hasnan_accounts = filtered_df[filtered_df['AccountID-18Char'].isna() | filtered_df['Contract / Policy #'].isna()].copy()
        for row_index, row in hasnan_accounts.iterrows():
            self.add_to_issues("[{}] {} {}: \'AccountID-18Char\' and/or \'Contract / Policy #\' is missing.".format(
                row_index, row['First Name'], row['Last Name']
            ))

        filtered_df.drop(index=hasnan_accounts.index, inplace=True)
        filtered_df.fillna(value="", inplace=True)
        return filtered_df

    def make_processed_df(self, df):
        """
        Processes the passed DataFrame df using Bonita-specific operations and
        returns it as a new DataFrame. Bonita has some simple renames, where
        each column maps one-to-one to a field of a different name, which are
        stored in rename_cols. The 'Account Owner(s)' column must be generated
        by concatenating the 'First Name' and 'Last Name' columns. 'Investment
        Description' is made using a rule: if the 'Source/Carrier' column is
        'other', then 'If Other, Please Specify' will be the first part of the
        string; otherwise, 'Source/Carrier' will be the first part of the
        string. Then, the 'Product Name' is concatenated to the end.

        df - The DataFrame to process.
        """
        def make_investment_description(src_carrier, prod_name, other_spec):
            """
            This will make the 'Investment Description', written as '<s1> <s2>',
            where s1 is either the 'Source/Carrier' data or the 'If Other,
            Please Specify' data, and s2 is the 'Product Name'.

            src_carrier - Data from the 'Source/Carrier' column.
            prod_name - Data from the 'Product Name' column.
            other_spec - Data from the 'If Other, Please Specify' column.
            """
            # (New col) "Investment Description"
            s1 = src_carrier.strip()
            # ('Source/Carrier' | 'If Other...') + 'Product Name' make "Investment Description".
            # Rule: If "Source/Carrier" == "other": Use the data in 'If Other, Please Specify' as 'Source/Carrier'.
            if s1.lower() == "other":
                # Use other_spec for s1
                s1 = other_spec.strip()
            if prod_name == "":
                # No prod_name given
                return(s1)
            return "{} {}".format(s1, prod_name.strip())

        # Do simple renames
        # (Rename) 'Contract / Policy #': "Investment Account"
        # (Rename) 'IAR': "Investment Advisor"
        # (Rename) 'IAR Email': "IIA Email"
        new_df = df.rename(self.rename_cols, axis=1)
        # Make the new columns
        # (New col) 'First Name' + 'Last Name' make "Account Owner(s)"
        new_df['Account Owner'] = new_df['First Name'] + ' ' + new_df['Last Name']
        new_df['Investment Description'] = new_df.apply(
            lambda x: make_investment_description(x['Source/Carrier'], x['Product Name'], x['If Other, Please Specify']),
            axis=1)
        return new_df

    def make_household_ddicts(self, hid, household_accounts):
        """
        Generates a list of dictionaries using the household_accounts DataFrame.
        Each dictionary will populate one PDF form. Each form can hold up to
        three accounts. Each form per household will be numbered starting from
        1, and each Account Owner and its information will be numbered from 1 to
        3. There are only two fields at the end of the 3PA Form for the account
        owner's printed name, so if there are more than two account owners for
        the accounts listed in the form, then it will add the household sheets
        to the issues so that the user can handle them.

        hid - Must be the Household ID that corresponds to the accounts given in
              household_accounts.
        household_accounts - Must be the accounts that correspond to the
                             Household ID.
        """
        def init_ddict(fsubstring):
            """
            Initializes the dictionary of fields and data as an empty dictionary
            and then adds the Household ID and file substring ('Fsubstring'),
            which will be used to help build the file's name. Each dictionary is
            also updated with the static Bonita office information.

            fsubstring - The file name substring.
            """
            new_ddict = {}
            new_ddict['Household ID'] = hid
            new_ddict['Fsubstring'] = fsubstring
            new_ddict.update(self.bonita_info)
            return new_ddict
        def add_accounts_to_issues(hid, account_owners_set):
            """
            Adds the accounts to the issues list with a message indicating that
            there are more than two clients stored in this dictionary (and thus
            in one PDF form).

            hid - Household ID.
            account_owners_set - The account owners with conflict.
            """
            issue_str = "There are more than two clients in this household's sheet:"
            issue_str += "\n\tHousehold Account ID: {}".format(hid)
            issue_str += "\n\tAccounts: {}".format(account_owners_set)
            self.add_to_issues(issue_str)

        ddict_list = []
        form_number = 1
        fname_substring = str(list(household_accounts['Last Name'])[0]) + ' ' + str(hid).strip()
        ddict = init_ddict(fname_substring + ' ' + str(form_number))
        i = 1 # Counts the number of accounts
        empty_len = len(ddict)
        account_owners = []
        for row_index, row in household_accounts.iterrows():
            # Investment Description i
            # Investment Account i
            # Account Owner\(s\) i
            inv_des_str = "Investment Description " + str(i)
            inv_acc_str = "Investment Account " + str(i)
            acc_own_str = "Account Owner\\(s\\) " + str(i)
            new_dict = {
                inv_des_str: str(row['Investment Description']).strip(),
                inv_acc_str: str(row['Investment Account']).strip(),
                acc_own_str: str(row['Account Owner']).strip()
            }
            # Add the new row info to the dictionary
            ddict.update(new_dict)
            # Collect the account owners
            account_owners.append(str(row['Account Owner']).strip())
            if i == 1:
                # Add the shared info from the row only once
                shared_info = {
                    'IIA Email': str(row['IIA Email']),
                    'Investment Advisor': str(row['Investment Advisor'])
                }
                ddict.update(shared_info)
                i += 1
            elif i == 2:
                # No extra action needed
                i += 1
            elif i == 3:
                # Check how many account owners we have
                account_owners_set = set(account_owners)
                if len(account_owners_set) > 2:
                    # Flag issue if there are more than 2 owners in a form
                    add_accounts_to_issues(hid, account_owners_set)
                else:
                    # Otherwise, put them in the client signature fields
                    account_owners_list = list(account_owners_set)
                    ddict['Client Print Name'] = account_owners_list[0]
                    if len(account_owners_list) > 1:
                        # If there is more than one, then add the Joint owner
                        ddict['Joint Client Print Name'] = account_owners_list[1]
                # Dict is full, so add it to the list of dicts
                ddict_list.append(ddict)
                # Add 1 to the form counter and start a new dict
                form_number += 1
                ddict = init_ddict(fname_substring + ' ' + str(form_number))
                # Restart the account counter and list of owners
                i = 1
                account_owners = []
        # Finally, since within the loop, it will only add the dictionary to the
        # list if it reached the maximum number of accounts (3), we have to add
        # the dictionary manually to the end if it had not yet reached 3
        # accounts
        if len(ddict) != empty_len:
            # If the ddict is not "empty" (equal to the initial dict len), then
            # the current ddict has not yet been added to the aggregate list

            # Once again, check for too many account owners and, if there are
            # too many on the form, then add it to the issues list. Otherwise,
            # add them to the fields at the bottom of the form
            account_owners_set = set(account_owners)
            if len(account_owners_set) > 2:
                add_accounts_to_issues(hid, account_owners_set)
            else:
                account_owners_list = list(account_owners_set)
                ddict['Client Print Name'] = account_owners_list[0]
                if len(account_owners_list) > 1:
                    ddict['Joint Client Print Name'] = account_owners_list[1]
            # Add the ddict to the list
            ddict_list.append(ddict)
        return ddict_list

    def write_pdfs(self):
        """
        Writes 3PA PDFs and returns the list of the file names that it wrote,
        organized by Household ID, in a dictionary.
        """
        # Per Household account, 'AccountID-18Char'
        # Only 3 Investment Accounts per PDF
        grand_ddict_list = []
        # Process the DF according to Bonita-specific rules
        processed_df = self.make_processed_df(self.make_filtered_df())
        # Collect all the unique Household IDs
        householdIDs = list(processed_df['Household ID'].unique())
        for hid in householdIDs:
            # Make dictionaries to fill the PDF forms with, grouping the forms
            # by the Household ID
            ddict_list = self.make_household_ddicts(hid, processed_df[processed_df['Household ID'] == hid])
            grand_ddict_list.extend(ddict_list)

        self.make_output_folder()

        # Fill the PDFs with the generated dictionaries and track all of the new
        # files using a dictionary with Household ID as the key
        for ddict in grand_ddict_list:
            out_fname = self.output_dir_string + "//" + pdfrw_by_keys.gen_file_name(ddict['Fsubstring'], self.template_pdf_3rd_party_auth)
            pdfrw_by_keys.fill_pdf(self.template_pdf_loc_prefix,
                                   self.template_pdf_3rd_party_auth,
                                   out_fname,
                                   ddict)
            self.add_to_household_files(ddict['Household ID'], out_fname)

        # Print the issues found
        self.print_issues()

        # Return the dictionary of created files
        return self.files_by_household
