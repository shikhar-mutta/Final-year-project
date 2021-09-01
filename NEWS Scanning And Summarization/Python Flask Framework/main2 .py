import pytesseract as tess
import io
tess.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'
from PIL import Image
from flask import Flask,request,Response
from nltk.tokenize import word_tokenize, sent_tokenize
import nltk
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer
import cv2
#nltk.download("stopwords")
#nltk.download("punkt")

app = Flask(__name__)
@app.route("/upload",methods=['POST'])

def upload():
    img = request.files.get('image')
    s = tess.image_to_string(Image.open(img))
    text = s
    stopWords = set(stopwords.words("english")) 
    words = word_tokenize(text) 

    freqTable = dict() 
    for word in words: 
        word = word.lower() 
        if word in stopWords: 
            continue
        if word in freqTable: 
            freqTable[word] += 1
        else: 
            freqTable[word] = 1

    sentences = sent_tokenize(text) 
    sentenceValue = dict() 

    for sentence in sentences: 
        for word, freq in freqTable.items(): 
            if word in sentence.lower(): 
                if sentence in sentenceValue: 
                    sentenceValue[sentence] += freq 
                else: 
                    sentenceValue[sentence] = freq 

    sumValues = 0
    for sentence in sentenceValue: 
        sumValues += sentenceValue[sentence] 

    average = int(sumValues / len(sentenceValue)) 

    summary = ""
    for sentence in sentences: 
        if (sentence in sentenceValue) and (sentenceValue[sentence] > (1.2 * average)): 
            summary += " " + sentence 
    return {'summary': summary}

app.run(host ="192.168.0.107", port = 5000)
if __name__ == "__main__":
    app.run()
