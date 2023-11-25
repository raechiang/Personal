import os
from pdfrw import PdfReader, PdfWriter, PdfDict, PdfName, PdfObject

ANNOT_KEY = '/Annots'
ANNOT_FIELD_KEY = '/T'
ANNOT_VAL_KEY = '/V'
ANNOT_RECT_KEY = '/Rect'
SUBTYPE_KEY = '/Subtype'
WIDGET_SUBTYPE_KEY = '/Widget'

def read_pdf_keys(template_pdf):
    """
    Returns the keys (named fields) found in the PDF.

    template_pdf - The PDF that needs fields to be filled.

    Adopted from https://akdux.com/python/2020/10/31/python-fill-pdf-files/
    """
    page_keys = {}
    p_num = 0
    for page in template_pdf.pages:
        annotations = page[ANNOT_KEY]
        page_keys[p_num] = []
        if annotations != None:
            for annotation in annotations:
                if annotation[SUBTYPE_KEY] == WIDGET_SUBTYPE_KEY:
                    if annotation[ANNOT_FIELD_KEY]:
                        key = annotation[ANNOT_FIELD_KEY][1:-1]
                        page_keys[p_num].append(key)
                        print(key)
        p_num += 1
    return page_keys

def print_pdf_keys(page_keys):
    """
    Prints the keys given a dictionary of PDF page keys.

    page_keys - A dictionary of page keys.
    """
    for key,value in page_keys.items():
        print("Page #{}".format(key))
        for item in value:
            print("\t{}".format(item))
        print("")

def gen_file_name(fsubstring, template_file):
    """
    Generates a filename with the format of:
    "<account/client surname> <household ID> -- <template PDF name>.pdf"

    fsubstring - String of the account surname and ID.
    template_file - Name of the PDF file.
    """
    return fsubstring + "--" + template_file.split('.pdf')[0] + ".pdf"

def fill_pdf(template_loc_prefix, template_file, output_pdf, data_dict):
    """
    Fills the template PDF given a dictionary of data and saves it as a new PDF
    file.

    template_loc_prefix - Directory of the template PDF.
    template_file - Name of the PDF file.
    output_pdf - Name of new output PDF file.
    data_dict - Dictionary containing data to write into the PDF fields.

    Adoted from https://akdux.com/python/2020/10/31/python-fill-pdf-files/
    """
    template_pdf = PdfReader(os.path.normpath(os.path.join(os.getcwd(),template_loc_prefix,template_file)))
    for page in template_pdf.pages:
        annotations = page[ANNOT_KEY]
        if annotations != None:
            for annotation in annotations:
                if annotation[SUBTYPE_KEY] == WIDGET_SUBTYPE_KEY:
                    if annotation[ANNOT_FIELD_KEY]:
                        key = annotation[ANNOT_FIELD_KEY][1:-1]
                        if key in data_dict.keys():
                            if type(data_dict[key]) == bool:
                                if data_dict[key] == True:
                                    annotation.update(PdfDict(
                                        AS=PdfName('Yes')))
                            else:
                                # if Joint Owner doesn't exist, skip joint owner
                                # Note this only applies to the sustainability form
                                # because the other forms don't have "joint owner" fields
                                if "joint owner" in key.lower():
                                    if data_dict['Joint Owner'] != "":
                                        annotation.update(
                                            PdfDict(V='{}'.format(data_dict[key]))
                                        )
                                        annotation.update(PdfDict(AP=''))
                                else:
                                    annotation.update(
                                        PdfDict(V='{}'.format(data_dict[key]))
                                    )
                                    annotation.update(PdfDict(AP=''))
    template_pdf.Root.AcroForm.update(PdfDict(NeedAppearances=PdfObject('true')))
    PdfWriter().write(output_pdf, template_pdf)

def merge_pdf_files_pdfrw(pdf_files, output_filename):
    """
    Merges the pages of PDF files that have been filled with pdfrw.

    pdf_files - List of PDF files to merge, in that order.
    output_filename - The name of the output PDF file.

    Adopted from https://stackoverflow.com/questions/57008782/pypdf2-pdffilemerger-loosing-pdf-module-in-merged-file
    """
    output = PdfWriter()
    num = 0
    output_acroform = None
    for pdf in pdf_files:
        input = PdfReader(pdf,verbose=False)
        output.addpages(input.pages)
        if PdfName('AcroForm') in input[PdfName('Root')].keys():  # Not all PDFs have an AcroForm node
            source_acroform = input[PdfName('Root')][PdfName('AcroForm')]
            if PdfName('Fields') in source_acroform:
                output_formfields = source_acroform[PdfName('Fields')]
            else:
                output_formfields = []
            num2 = 0
            for form_field in output_formfields:
                key = PdfName('T')
                old_name = form_field[key].replace('(','').replace(')','')  # Field names are in the "(name)" format
                form_field[key] = 'FILE_{n}_FIELD_{m}_{on}'.format(n=num, m=num2, on=old_name)
                num2 += 1
            if output_acroform == None:
                # copy the first AcroForm node
                output_acroform = source_acroform
            else:
                for key in source_acroform.keys():
                    # Add new AcroForms keys if output_acroform already existing
                    if key not in output_acroform:
                        output_acroform[key] = source_acroform[key]
                # Add missing font entries in /DR node of source file
                if (PdfName('DR') in source_acroform.keys()) and (PdfName('Font') in source_acroform[PdfName('DR')].keys()):
                    if PdfName('Font') not in output_acroform[PdfName('DR')].keys():
                        # if output_acroform is missing entirely the /Font node under an existing /DR, simply add it
                        output_acroform[PdfName('DR')][PdfName('Font')] = source_acroform[PdfName('DR')][PdfName('Font')]
                    else:
                        # else add new fonts only
                        for font_key in source_acroform[PdfName('DR')][PdfName('Font')].keys():
                            if font_key not in output_acroform[PdfName('DR')][PdfName('Font')]:
                                output_acroform[PdfName('DR')][PdfName('Font')][font_key] = source_acroform[PdfName('DR')][PdfName('Font')][font_key]
            if PdfName('Fields') not in output_acroform:
                output_acroform[PdfName('Fields')] = output_formfields
            else:
                # Add new fields
                output_acroform[PdfName('Fields')] += output_formfields
        num +=1
    output.trailer[PdfName('Root')][PdfName('AcroForm')] = output_acroform
    output.write(output_filename)
