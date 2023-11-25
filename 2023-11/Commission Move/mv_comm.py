import sys
import pandas as pd
import json
import re
import datetime
import os

import pytesseract
from pdf2image import convert_from_bytes

pytesseract.pytesseract.tesseract_cmd = r'C:\\Program Files\\Tesseract-OCR\\tesseract.exe'

def get_default_files():
    """
    It will use these files by default.

    in_file - The input file that you want to take information from.
    master_file - The master Excel that you want to add to.
    col_file - The JSON file that contains column mappings from input to output
               template.
    """
    # You can change these two
    in_file = "Allianz Statement 09.21.2023.xlsx"
    master_file = "Commission_Header.xlsx"
    # You can change this one
    col_file = "col_config_allianz.json"

    return in_file, master_file, col_file

ALLIANZ = "allianz"
JHANCOCK = "jhancock"
VALID_META_NAMES = [ALLIANZ, JHANCOCK]

def read_col_json(col_file):
    """
    Reads the file of name col_file and loads its JSON. If the file passes
    some checks, it will return the parsed JSON dictionary. Otherwise, it will
    return an empty dictionary.

    col_file - The name of the column configuration JSON file. At its most basic
               form, it requires two top-level keys, "src_meta" and "col_map",
               and both have their own dictionaries.
               The "src_meta" must contain at least two keys, "name" and
               "offset", which correspond to the name of the source commission
               and the row of the header (by Excel numbering), respectively.
               Excel spreadsheets start with row number 1, and this app will
               assume that the input here follows that convention.
               The "col_map" must contain values corresponding to the column
               names from the input file as keys that map to the column names
               from the master Excel as values.
               For more details, please refer to the user guide.
    """
    # Open file and read JSON
    with open(col_file, 'r') as f:
        jcols = json.load(f)

    # Return JSON dict if it passes some early checks
    if validate_jcols_keys(jcols, col_file):
        return jcols

    return {}

def validate_jcols_keys(jcols, col_file):
    """
    Validates the col_config.json file. At minimum, there must be two keys at
    the top-level: "src_meta" and "col_map". The values in src_meta requires
    the two keys, "name" and "offset", which correspond to the name of the
    source of the commission and its starting row of the header (by Excel
    numbering). The name has to be in VALID_META_NAMES, because different
    commission sources need to be handled differently, and these are hard-coded
    at the moment. The offset must be a number, preferably an integer, but if
    it is not, it will attempt to cast it and a warning will be shown so you
    can see what it was cast to. Since it is using Excel numbering, the row
    must be 1 or greater.

    jcols - The JSON dictionary read from col_file.
    col_file - The name of the column configuration JSON file.
    """
    # Check length
    if len(jcols) != 2:
        print("Error: Please verify the top level of \"{}\" contains two dictionaries, \"src_meta\" and \"col_map\".".format(
              col_file))
        return False

    # Check top keys are src_meta and col_map
    top_keys = jcols.keys()
    if ("src_meta" not in top_keys) | ("col_map" not in top_keys):
        print("Error: Please verify the top level of \"{}\" contains the keys \"src_meta\" and \"col_map\".".format(
              col_file))
        return False

    # Check src_meta "name":str and "offset":(int>0)
    if len(jcols['src_meta']) < 2:
        print("Error: Please verify that src_meta contains at least two keys, \"name\" and \"offset\".")
        return False
    else:
        meta_keys = jcols['src_meta'].keys()
        if ("name" not in meta_keys) | ("offset" not in meta_keys):
            print("Error: Please verify that src_meta contains at least the keys, \"name\" and \"offset\".")
            return False
        name = jcols['src_meta']['name']
        offset = jcols['src_meta']['offset']

        if isinstance(name, str):
            if name.lower() not in VALID_META_NAMES:
                print("Error: {} is not a valid name.".format(name))
                return False
        else:
            print("Error: src_meta's name key must map to a string.")
            print("\tGiven \"name\": {}".format(name))
            return False

        # The offset is an index and thus must be an integer
        if not isinstance(offset, int):
            # if it's not an integer, we must deal with it accordingly
            if isinstance(offset, float):
                # Found float
                print("Warning: src_meta's offset is a float of {}. This will be cast to {}.".format(
                      offset, int(offset)))
                offset = int(offset)
            elif isinstance(offset, str):
                # Found string
                if re.match(r"\+?[0-9]+\.+[0-9]*", offset):
                    # If it's a numeric string with a decimal, cast it to float then int
                    print("Warning: src_meta's offset is a numeric string of \"{}\". This will be cast to {}.".format(
                          offset, int(float(offset))))
                    offset = int(float(offset))
                elif re.match(r"\+?[0-9]+", offset):
                    # If it's a numeric string of an integer, cast it to int
                    print("Warning: src_meta's offset is a numeric string of \"{}\". This will be cast to {}.".format(
                          offset, int(offset)))
                    offset = int(offset)
                else:
                    # If it's not a numeric string, error
                    print("Error: src_meta's offset is mapped to a non-numeric or negative string.")
                    print("\tGiven \"src_meta\": {}".format(offset))
                    return False
            else:
                # If it's not a float or a string, error
                print("Error: src_meta's offset key must map to a positive number or number string.")
                print("\tGiven \"offset\": {}".format(offset))
                return False
            jcols['src_meta']['offset'] = offset

        # offset corresponds to an Excel row for a header
        # check if the index > 0
        if offset <= 0:
            print("Error: src_meta's offset must be greater than 0.")
            print("\t Given \"offset\": {}".format(offset))
            return False

    return True

def read_input_excel(in_file, jcols):
    """
    Reads the in_file Excel into a DataFrame, saving all data points as objects,
    allegedly. If the keys of col_map from jcols exist in the DataFrame's
    columns, then the DataFrame is returned. Otherwise, an empty DataFrame is
    returned.

    in_file - The input Excel file to read and copy data from.
    jcols - The JSON dictionary read from col_file.
    """
    # Read Excel starting from offset-1
    # All data is saved as objects
    df = pd.read_excel(in_file, header = jcols['src_meta']['offset'] - 1, dtype=object)

    # Check the input column names from col_map exist in the df columns
    if set(jcols['col_map'].keys()).issubset(set(df.columns)):
        return df
    else:
        print("Error: Please verify that the columns in the column configuration file exist in the input Excel sheet and that the header row is correct.")
        print("\tcol_map keys:", set(jcols['col_map'].keys()) - set(df.columns))
        print("\tExcel columns from offset {}: {}".format(jcols['src_meta']['offset'],
                                                              df.columns))
        return pd.DataFrame()

def pdf_to_slist(pdf_path, target_page):
    """
    Converts the target_page of the PDF found in pdf_path into an image and then
    parses the text into a string. Finally, returns the list of the strings,
    split by newlines.

    pdf_path - The file path to the PDF.
    target_page - The single page to parse.
    """
    with open(pdf_path, "rb") as pdf_file:
        pdf_image = convert_from_bytes(pdf_file.read(),
                                       first_page=target_page,
                                       last_page=target_page,
                                       fmt="jpeg")
    pdf_as_string = str(((pytesseract.image_to_string(pdf_image[0]))))

    return pdf_as_string.split('\n')

def parse_to_df(table, name, pdf_column_names):
    """
    Converts a table into a DataFrame. The table must consist of data in the
    form of a NAME column followed by other columns, whose order correlates
    pdf_column_names. Missing values will likely not be parsed correctly, and
    every column must be separated by whitespace.

    table - A list of strings which are full lines of a row.
    name - The element of the table.
    pdf_column_names - A list of column names whose order matches exactly the
        columns in the original PDF file.
    """
    item_list = []
    for item in table:
        row = []
        # Add the name to our row and remove it from the item feed
        row.append(name)
        nameless_item = item.split(name)
        # Extend the list with the list of the remainder of the lines,
        # which must be split by spaces
        row.extend(nameless_item[-1].strip().split(' '))
        # Add the row to the item_list
        item_list.append(row)
    # Convert the item_list into a DataFrame, using pdf_column_names as the
    # column names
    return pd.DataFrame(item_list, columns=pdf_column_names, dtype=object)

def parse_jh_list(pdf_line_list, name_line_num, table_start, table_end, pdf_column_names):
    """
    Parses the table of data from the line of strings list. For JohnHancock
    files, they seem to have a well-formed table with columns after the Name
    column separated by spaces. This function grabs the name_line using the
    name_line_num and grabs the table of data using the table_start and
    table_end, which are also both line numbers. The name is assumed to be the
    final position on the name line, following the "NAME:" indicator. The
    table will then be converted into a DataFrame using parse_to_df().

    pdf_line_list - List of strings corresponding to lines in the PDF.
    name_line_num - The line number of the NAME label in the PDF.
    table_start - The line number of the start of the data of the table.
    table_end - The line number of the end of the data of the table.
    pdf_column_names - The column names of the PDF, in the correct order.
    """
    name_line = ""
    table = []
    line_num = 1
    for line in pdf_line_list:
        if line_num == name_line_num:
            name_line = line.strip()
        elif (line_num >= table_start) & (line_num <= table_end):
            table.append(line.strip())
        line_num += 1
    # Parse name
    name = name_line.split('NAME:')[-1].strip()
    print("... READ NAME AS:", name)
    # Convert to df
    print("\n... READ TABLE AS:")
    for item in table:
        print(item)
    return parse_to_df(table, name, pdf_column_names)

def read_input_file_data(col_file, in_file):
    """
    Reads input files data.

    col_file - The name of the column configuration JSON file.
    in_file - The name of the input file to read and move data from.
    """
    # Read JSON
    jcols = read_col_json(col_file)

    if len(jcols) == 2:
        extension = in_file.split('.')[-1]
        if extension.lower() == "xlsx":
            input_df = read_input_excel(in_file, jcols)
        elif extension.lower() == "pdf":
            if jcols['src_meta']['name'].lower() == JHANCOCK:
                # Checks that these keys exist in src_meta
                req_keys = ['name', 'offset', 'text_name_line', 'text_table_start', 'text_table_end', 'column_names']
                for k in jcols['src_meta'].keys():
                    if k not in req_keys:
                        print(k)
                        raise Exception("col_config was not set up properly for JHancock PDF files.")
                # Convert PDF to list of strings
                pdf_slist = pdf_to_slist(in_file, jcols['src_meta']['offset'])
                # Parse data from list of strings
                input_df = parse_jh_list(pdf_slist, jcols['src_meta']['text_name_line'],
                                        jcols['src_meta']['text_table_start'],
                                        jcols['src_meta']['text_table_end'],
                                        jcols['src_meta']['column_names'])
            else:
                raise Exception("PDF received, but src_meta's name was not JHancock. Please make a request for a new source implementation if needed.")
        else:
            raise Exception("the input file {} does not have an accepted file extension (xlsx or pdf).".format(in_file))
        if len(input_df) != 0:
            return jcols, input_df
        else:
            raise Exception("the input file {} could not be read.".format(in_file))
    else:
        raise Exception("the column configuration JSON file was invalid.")

def get_rename_cols(jcols):
    """
    Returns a dictionary of only the column mappings that have a one-to-one
    mapping. These have one string key mapping to one string value.

    jcols - The JSON dictionary read from col_file.
    """
    renames = {}
    for k,v in jcols['col_map'].items():
        if isinstance(v, str):
            renames[k] = v
    return renames

def process_allianz(jcols, df):
    """
    Returns a DataFrame mapping the Commission Amount to either First Year or
    Renewal, depending on the Description. If the Description is "First Year",
    then the Commission Amount becomes "First Year", and the "Renewal" is left
    blank. If the Description is "Renewal Premium", then the Commission Amount
    becomes "Renewal". The alternative option for each case is just added as an
    empty field.

    jcols - The JSON dictionary read from col_file.
    df - The input DataFrame to adjust the columns of.
    """
    if len(df['Description'].unique()) == 1:
        description = df['Description'].unique()[0]
        if description.lower() == "first year":
            # If Description has First Year
            # then Commision Amount goes to First Year
            df = df.rename(columns={"Commission Amount": "First Year"})
            df['Renewal'] = pd.Series(dtype='object')
        elif description.lower() == "renewal premium":
            # If Description has Renewal Premium
            # then Commission Amount goes to Renewal
            df = df.rename(columns={"Commission Amount": "Renewal"})
            df['First Year'] = pd.Series(dtype='object')
        return df
    else:
        print("Warning: Allianz Description column contains more than one value.")
        print("\tFailed to determine appropriate destination for Commission Amount.")

def process_jhancock(df):
    """
    Returns the original df concatenated with the two new columns, 'Renewal' and
    'First Year'. The columns will be filled according to the 'Earn Type*'
    column. If the Earn Type* is REN or RN-EX, then the Amount will be the same
    as the Renewal. All other cases are treated as First Year.

    df - The original JHancock DataFrame.
    """
    def interpret_earn_type(earn_type, amount):
        if (earn_type == "REN") | (earn_type == "RN-EX"):
            # If Earn Type* is REN or RN-EX, then Amount is Renewal
            return [amount, ""]
        else:
            # If Earn Type* is FY, FY-EX, FYC, SI, VB, then Amount is First Year
            # WARNING: tesseract might not be able to tell whether SI is SI, SL,
            #          Sl, S1, etc. so everything that wasn't REN or RN-EX will
            #          be treated as First Year
            return ["", amount]

    amount_df = df.apply(lambda x: interpret_earn_type(x['Earn Type*'], x['Amount']),
                         axis=1, result_type='expand')
    amount_df.columns = ['Renewal', 'First Year']
    return pd.concat([df, amount_df], axis=1)

def process_df(jcols, df):
    """
    Renames the one-to-one column mappings and processes unique mappings
    according to the commission source.

    Currently, the supported sources are Allianz and JHancock.

    jcols - The JSON dictionary of input column names and output names.
    df - The dataframe containing information from the original input
         Excel file that will be copied to  the master workbook.
    """
    # Find and resolve simple (one-to-one) column mappings
    rename_cols = get_rename_cols(jcols)
    df = df.rename(columns=rename_cols)

    # Unique map for each source
    if jcols['src_meta']['name'].lower() == ALLIANZ:
        df = process_allianz(jcols, df)
    elif jcols['src_meta']['name'].lower() == JHANCOCK:
        df = process_jhancock(df)

    return df

def update(input_df, master_df):
    """
    Returns the concatenation of the input_df to the master_df. Only the input's
    columns that also exist in the master_df are added.

    input_df - The input DataFrame to add to the master_df.
    master_df - The DataFrame being added to.
    """
    intersection = list(set(master_df.columns).intersection(set(input_df.columns)))
    print("... Adding to columns: ", intersection)
    return pd.concat([master_df, input_df[intersection]], ignore_index=True)

def gen_file_name(master_file):
    """
    Returns a file name string using the first element of the master file's
    name, split by the extension, '.xlsx'. It simply adds the current date and
    time to the end of the file name.

    master_file - The file name of the master Excel.
    """
    return master_file.split('.xlsx')[0] + "-" + (datetime.datetime.now()).strftime('%Y%m%d%H%M%S') + ".xlsx"

def save_new_master(new_df, new_file):
    """
    Saves the new_df to Excel, using the new_file name given. If the file
    already exists, the DataFrame will not be saved.

    new_df - The DataFrame to save to Excel.
    new_file - The name of the file to save the DataFrame as.
    """
    if os.path.isfile(new_file):
        print("Error: This file name already exists in this directory. The new file was not generated.")
    else:
        print("... Saving to {}".format(new_file))
        new_df.to_excel(new_file, index=False)

def force_date_string(df):
    """
    Forces any datetime64[ns] data type column with "date" in its name into a
    string (object) column of the form YYYY-MM-DD.

    The reason for this is that the pd.to_excel() method seems to add the time
    portion when writing to the Excel. For example, "2023-11-06" in the
    DataFrame would be "2023-11-06 00:00:00" in the outputted Excel. While it is
    true that the client could change the columns in Excel using its Number
    formatting, I wanted to minimize forcing the client to manage that, so
    datetimes columns with "date" in the name are explicitly forced into a
    string date format, if it can be done here.
    """
    dt_cols = list(df.select_dtypes(include='datetime64[ns]').columns)
    for dt_col in dt_cols:
        if ("date" in dt_col.lower()):
            df[dt_col] = df[dt_col].dt.strftime('%Y-%m-%d')
    return df

def main(*args):
    # Collects file names
    input_file, master_file, col_file = get_default_files()

    print("Using the following file inputs:")
    print("\tinput: {}\n\tmaster: {}\n\tcolumns: {}\n".format(
        input_file, master_file, col_file
        ))

    # Quick check of file names
    if (input_file == master_file):
        print("Error: Input file and master file cannot share the same name.")
        raise Exception("the input file and master file have the same name. Please use different files.")

    # Read input files
    jcols, input_df = read_input_file_data(col_file, input_file)
    # Read master file and convert date datetime objects to strings
    master_df = force_date_string(pd.read_excel(master_file, dtype=object))
    # Process input_df and convert date datetime objects to strings
    input_df = force_date_string(process_df(jcols, input_df))
    # Add the input_df data to the master_df
    new_master_df = update(input_df, master_df)
    # Save the updated master_df
    save_new_master(new_master_df, gen_file_name(master_file))

if __name__ == "__main__":
    main(*sys.argv[1:])
