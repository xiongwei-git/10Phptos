#encoding:UTF-8
import json,math
import re
import urllib.request

#路径
savePath = 'D://wallsplashDownload//'

def gGetFileName(url):
    if url==None: return None
    if url=="" : return ""
    arr=url.split("/")
    name = arr[len(arr)-1]
    if name.endswith("jpg"):return name
    if name.endswith("JPG"):return name
    name = name+".jpg"
    return name

#根据url下载文件，文件名自动从url获取
def gDownload(imageUrl,savePath):
    fileName = gGetFileName(imageUrl)
    gDownloadWithFilename(imageUrl,savePath,fileName)

def gDownloadWithFilename(imageUrl,savePath,file):
    try:
        fp = urllib.request.urlopen(imageUrl)
        data = fp.read()
        fp.close()
        file=open(savePath + file,'w+b')
        file.write(data)
        print("下载成功："+ imageUrl)
        file.close()
    except IOError:
        print("下载失败:"+ imageUrl)

url = "http://wallsplash.lanora.io/pictures/"
stdout=urllib.request.urlopen(url)
pictureInfo= stdout.read().decode('utf-8')
#print(pictureInfo)
jsonData = json.loads(pictureInfo)
#输出JSON数据
total = jsonData["total"]
print("总数为: ", total+1)

for i in range(0,3):
    imageUrl = jsonData['data'][i]['image_src'];
    print(imageUrl)
    #print(gGetFileName(imageUrl))
    gDownload(imageUrl,savePath)
