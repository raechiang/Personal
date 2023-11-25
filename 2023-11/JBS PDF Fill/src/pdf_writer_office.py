import sys
import pandas as pd
import os

class OfficePDFWriter:
    """
    Represents a PDF writer for an office into the Client 3rd Party Auth PDF.
    """
    def __init__(self, office_excel_path, office_excel_name, output_dir_string):
        """
        Initializes class variables.

        office_excel_path - The full file path to the office Excel sheet.
        office_excel_name - The name of the office Excel sheet.
        output_dir_string - The name of the output folder.
        """
        self.office_file = office_excel_path + office_excel_name
        self.template_pdf_loc_prefix = "template pdfs\\"
        self.template_pdf_3rd_party_auth = "JBS DPL - Client 3rd Party Auth 2023-09-7.pdf"
        self.output_dir_string = output_dir_string
        self.issues_list = []
        self.office_df = pd.read_excel(self.office_file, dtype=object)
        self.files_by_household = {}

    def add_to_issues(self, message):
        """
        Adds a new message to the issues list.

        message - New message to add.
        """
        self.issues_list.append(message)

    def make_output_folder(self):
        """
        Makes the output folder for the PDFs.
        """
        if not os.path.exists(self.output_dir_string):
            os.makedirs(self.output_dir_string)

    def print_issues(self):
        """
        Prints issues from the issue list.
        """
        print("Issues from Office Writer...")
        for issue in self.issues_list:
            print(issue)

    def add_to_household_files(self, hid, filepath):
        """
        Adds the filepath to the dictionary of files_by_household. If the hid is
        new, then it makes a new entry with a new list containing the filepath.
        If the hid is already in the list, it appends the filepath to the list.

        hid - The Household ID.
        filepath - The path and name of the associated file.
        """
        if hid in self.files_by_household:
            self.files_by_household[hid] = self.files_by_household[hid].append(filepath)
        else:
            self.files_by_household[hid] = [filepath]
