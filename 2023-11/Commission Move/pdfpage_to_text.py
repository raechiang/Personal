import sys

import pytesseract
from pdf2image import convert_from_bytes

pytesseract.pytesseract.tesseract_cmd = r'C:\\Program Files\\Tesseract-OCR\\tesseract.exe'

def get_info():
    """
    Returns relevant information to turn the PDF page into text. The pdf_loc is
    the file directory containing the PDF file. The pdf_path is the file and the
    whole path to the file. The target_page is the single page number to parse
    text from. The text_out_file is just the name of the output file to write
    the text to.
    """
    # Change these
    pdf_loc = ""
    pdf_path = pdf_loc + "JohnHancock Statement 09.16.2023" + ".pdf"
    target_page = 4
    # Optionally change the output file name
    text_out_file = "pdfpage_text.txt"
    return pdf_path, target_page, text_out_file

def write_pdf_to_text(pdf_path, target_page, text_out_file):
    """
    Writes the string interpreted from the PDF image into a text file.

    pdf_path - The whole filepath of the PDF.
    target_page - The single page to read and parse text from.
    text_out_file - The name of the output text file.
    """
    with open(pdf_path, "rb") as pdf_file:
        pdf_image = convert_from_bytes(pdf_file.read(), first_page=target_page, last_page=target_page, fmt="jpeg")
    with open(text_out_file, "a") as out_file:
        text = str(((pytesseract.image_to_string(pdf_image[0]))))
        out_file.write(text)
        print("Wrote text out to {}.".format(text_out_file))

def main(*args):
    pdf_path, target_page, text_out_file = get_info()
    write_pdf_to_text(pdf_path, target_page, text_out_file)

if __name__ == "__main__":
    main(*sys.argv[1:])
