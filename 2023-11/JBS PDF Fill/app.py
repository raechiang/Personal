import sys
import pandas as pd
import os

sys.path.append('src')
import pdfrw_by_keys
from pdf_writer_office_bonita import BonitaPDFWriter

################################################################################
# Edit template_pdf_folder, template_pdf files, and the input_excel_file to    #
# change input files. You can copy and paste the file directories containing   #
# PDF and input files into those ending with "_folder". The input Excels have  #
# suffixes of "_excel_name".                                                   #
################################################################################
# Directory containing PDF templates (you may edit)
template_pdf_folder = "C:\\Users\\chian\\JupyterNBs\\Jupyter_JBS_PDF_Fill\\template pdfs\\"
# The PDF templates corresponding to the requested fill items (you may edit)
template_pdf_contract_conflict_disclosure = "JBS DPL - Client Contract  Conflict Disclosure 2023-08-22.pdf"
template_pdf_suitability_form = "JBS DPL - Client Suitability Form 2023-10-03.pdf"
# Directory containing Excel data sheet (you may edit)
excel_folder = "C:\\Users\\chian\\JupyterNBs\\Jupyter_JBS_PDF_Fill\\excels\\"
# The Excel files (you may edit)
input_excel_name = "Salesforce-Accounts-PKS-Bonita.xlsx"
office_name = "Bonita"
office_excel_name = "Bonita Springs (Beacon Pointe) Onboarding BAP Tracker_JBS 3rd PartyForm.xlsx"
# Output directory (you may edit). Does not need \\ at the end.
output_folder = "output_pdfs"

# Column Mappings
# - One-to-one columns are renamed
# - One-to-many columns are duplicated into every name in the list
cmap_ccd = {
    "AccountID-18Char": "Household ID", # Do not change
    "Primary Contact: Full Name": ["Client Name", "Client Printed Name"],
    "Secondary Contact: Full Name": ["Joint Client Name", "Joint Client Printed Name"]
}
cmap_suitability = {
    "AccountID-18Char": "Household ID", # Do not change
    "Primary Contact: Full Name": ["Account Owner", "Client Print Name"],
    "Primary Birthdate": "DOB",
    "Primary Marital Status": "Marital Status",
    "Primary Preferred Phone": "Telephone",
    "Primary Email": "Email",
    "Primary Employement Status": "Employment Status",
    "Mailing Address": ["Mailing Address", "Joint Owner Mailing Address"],
    "Legal Address": ["Physicasl Address", "Joint Owner Physical Address"],
    "Primary Employer": "Employer",
    "Secondary Contact: Full Name": ["Joint Owner", "Joint Client Print Name"],
    "Secondary Birthdate": "Joint Owner DOB",
    "Secondary Marital Status": "Joint Owner Marital Status",
    "Secondary Preferred Phone": "Joint Owner Telephone",
    "Secondary Email": "Joint Owner Email",
    "Secondary Employment Status": "Joint Owner Employment Status",
    "Secondary Employer": "Joint Owner Employer",
    "Annual Income": "Total Income",
    "Income Source": "Social Security or Other Income",
    "Risk Tolerance": "Risk Tolerance",
    #"Investment Objective (Client Profile)": "Investment Objective", # CONFLICT
    #"Investment Objectives": "Investment Objective",                 # CONFLICT
    "Time Horizon": "Time Horizon",
    "Total Net Worth": "Total Net Worth"
}

pdf_map_list = [
    {
        "cmap": cmap_ccd,
        "pdf_file": template_pdf_contract_conflict_disclosure
    },
    {
        "cmap": cmap_suitability,
        "pdf_file": template_pdf_suitability_form
    }
]

BONITA_OFFICE = "bonita"
VALID_OFFICES = [BONITA_OFFICE]

def read_from_excel(excel_loc_string, input_excel_file):
    """
    Returns a DataFrame object read from the given the directory and name of an
    Excel file.

    excel_loc_string - Directory of the Excel file.
    input_excel_file - File name of the Excel file.
    """
    df = pd.read_excel(os.path.normpath(os.path.join(os.getcwd(),excel_loc_string,input_excel_file)), dtype=object)
    df.fillna(value="", inplace=True)
    return df

def get_processed_df(df, cmap):
    """
    Returns the DataFrame processed using the column map instructions.
    Most columns are simply renamed, but cmap columns that map to a list object
    are duplicated.

    df - The DataFrame to process.
    cmap - The column mappings, which provide guidance in column name
           replacement and duplication.
    """
    #new_df = df[list(cmap.keys())].copy()
    intersection = list(set(df.columns).intersection(set(cmap.keys())))
    new_df = df[intersection].copy()
    # Clean nan and report missing ones
    hasnan_householdID = new_df[new_df['AccountID-18Char'].isna()].copy()
    for row_index, row in hasnan_householdID.iterrows():
        print("[{}] {}: \'AccountID-18Char\' is missing.".format(
            row_index, row['Primary Contact: Full Name']))
    new_df.drop(index=hasnan_householdID.index, inplace=True)
    rename_cols = {}
    for key, value in cmap.items():
        if isinstance(value, list):
            old_ser = new_df[key]
            new_df.drop(key, axis=1, inplace=True)
            # Deal with list values in cmap by duplicating columns
            for v in value:
                new_df[v] = old_ser
        else:
            # Collect the regular strings for simple renames
            rename_cols[key] = value
    # Rename columns
    new_df.rename(columns=rename_cols, inplace=True)
    new_df.reset_index(inplace=True, drop=True)
    # Drop duplicate households
    new_df.drop_duplicates(subset=['Household ID'], keep='first', inplace=True)
    # Generate file name
    new_df['Fsubstring'] = df.apply(lambda x: x['Primary Contact: Full Name'].split(' ')[-1] + ' ' + str(x['AccountID-18Char']), axis=1)

    return new_df

def write_many_pdfs(template_loc_prefix, template_file, df, output_loc_string):
    """
    Fills a given template PDF file with every row of the Dataframe. In other
    words, if there are 10 rows of data in the DataFrame, then 10 new PDF files
    will be created.

    template_loc_prefix - Directory of the template PDF.
    template_file - Name of the PDF file.
    df - The DataFrame containing the data to populate the template PDF with.
    output_loc_string - The name of the output folder.
    """
    files_by_household = {}
    i = 0
    for ddict in df.to_dict('records'):
        #outfpath = output_loc_string + "//" + gen_file_name(i, ddict['Fsubstring'], template_file)
        outfpath = output_loc_string + "//" + pdfrw_by_keys.gen_file_name(ddict['Fsubstring'], template_file)
        pdfrw_by_keys.fill_pdf(template_loc_prefix, template_file, outfpath, ddict)
        if ddict['Household ID'] in files_by_household:
            files_by_household[ddict['Household ID']] = files_by_household[ddict['Household ID']].append(outfpath)
        else:
            files_by_household[ddict['Household ID']] = [outfpath]
        i += 1
    return files_by_household

def combine_file_dict(left, right):
    """
    Combines two dictionaries together by extending their list items if the key
    already exists in the dictionary or by adding an item with a new key.

    left - One dictionary.
    right - The other dictionary.
    """
    if len(left) == 0:
        return right.copy()

    new_file_dict = left.copy()
    for k,v in right.items():
        if k in new_file_dict:
            new_file_dict[k].extend(v)
        else:
            new_file_dict[k] = v
    return new_file_dict

def main(*args):
    files_by_household = {}
    output_pdfs_split_folder = "output_pdfs_split"

    # Make CCD and Suitability Forms using the input excel
    input_df = read_from_excel(excel_folder, input_excel_name)

    # Make 3 folders labeled with 1, 2, and 3. One folder for each PDF form
    for i in range(1,4):
        if not os.path.exists(output_pdfs_split_folder + str(i)):
            os.makedirs(output_pdfs_split_folder + str(i))

    # Make CCD and Suitability Forms
    for map_dict in pdf_map_list:
        print("Making files for", map_dict['pdf_file'])
        new_df = get_processed_df(input_df, map_dict['cmap'])
        # Depending on the form, append the order prefix
        if map_dict['pdf_file'] == template_pdf_suitability_form:
            prefix = "1"
        elif map_dict['pdf_file'] == template_pdf_contract_conflict_disclosure:
            prefix = "3"
        # Keep track of new files made for each Household
        new_file_dict = write_many_pdfs(template_pdf_folder, map_dict['pdf_file'], new_df, output_pdfs_split_folder + prefix)
        files_by_household = combine_file_dict(files_by_household, new_file_dict)
    print("CCD and Suitability forms created.")

    # Generate 3PA Form depending on the office
    if office_name.lower() == BONITA_OFFICE:
        # Make Bonita 3PA Forms for each Household
        prefix = "2"
        bonita_writer = BonitaPDFWriter(excel_folder, office_excel_name, output_pdfs_split_folder + prefix)
        new_file_dict = bonita_writer.write_pdfs()
        # Keep track of new files made for each Household
        files_by_household = combine_file_dict(files_by_household, new_file_dict)
        print("Bonita 3PA forms created.")

    # Make the output combined PDF folder
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # Merge PDFs by Household in order of Suitability, 3PA, Conflict
    for v in files_by_household.values():
        file_name = v[0].split('//')[-1]
        file_name = file_name.split('--')[0].strip() + ".pdf"
        file_name = output_folder + "//" + file_name
        fpath_list = sorted(v)
        pdfrw_by_keys.merge_pdf_files_pdfrw(fpath_list, file_name)

if __name__ == "__main__":
    main(*sys.argv[1:])
