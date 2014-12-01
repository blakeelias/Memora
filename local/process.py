from datetime import datetime
from subprocess import call, check_output
import os
from PIL import Image

from pymongo import MongoClient

from download import get_local_file_path, getOriginalPath
from local_settings import PROCESSED_STORE_PATH

def main():
    client = MongoClient('mongodb://memora:memora@dogen.mongohq.com:10010/memora-sandbox')
    db = client['memora-sandbox']
    processDirectory(getOriginalPath(), db)

def processDirectory(path, db, subPath=''):
    for line in check_output(['ls', path + subPath]).split('\n'):
        fileName = line.split('\r')[0]
        if fileName != '' and os.path.isdir(path + subPath + fileName):
            processDirectory(path, db, subPath + fileName + '/')
        else:
            if 'jpg' in fileName:
                try:
                    (prefix, suffix) = fileName.split('.')
                except Exception as e:
                    print(e)
                    continue
                time, manual = getTime(prefix, source='Narrative', subPath=subPath)
                db.photos.insert({
                    'time': time,
                    'path': path + subPath + fileName
                })
                '''makeSizes(path + subPath + fileName,
                    time,
                    {'thumbs': 158, 'previews': 632},
                    PROCESSED_STORE_PATH,
                    newSubPath(time),
                    standardizedName(time, suffix, manual))'''

def getTime(filePrefix, source='Glass', subPath = ''):
    '''Return the time indicated by a file name's prefix string (i.e., everything before the file extension).
        Eg. for the file naemed '20140729_081534_016.jpg', you would call:

        >>> getTime('20140729_081534_016')
        datetime.datetime(2014, 7, 29, 8, 15, 34, 16000)
    '''
    manual = '_' in filePrefix
    if source == 'Glass':
        if manual:
            chunks = filePrefix.split('_')
            if len(chunks) == 3:
                # Indicates Android photo names, eg. "20140729_081534_016.jpg"
                
                # put three trailing 0s to convert milliseconds (10^-3 s) into microseconds (10^-6 s)
                timeWithMicros = filePrefix + '000'
                time = datetime.strptime(timeWithMicros, '%Y%m%d_%H%M%S_%f')
        else:
            millis = int(filePrefix)
            time = datetime.fromtimestamp(millis / 1000.0)
    elif source == 'Narrative':
        # Narrative filenames
        # 2014/11/03/213441.jpg
        # Represents November 3, 2014 21:34:41
        time = datetime.strptime(subPath + filePrefix, '%Y/%m/%d/%H%M%S')
    
    return time, manual

def getFileName(path):
    return path.split('/')[-1]

def rename(photo, time):
    newPath = get_local_file_path() + newSubPath(time) + '/'
    call(['mkdir', newPath])
    call(['mv', path, newPath + getFileName(path)])

def newSubPath(time):
    return '/'.join([
        str(time.year),
        str(time.month),
        str(time.day)
        ])

def getPath(photo):
    return '/'.join(photo.split('/')[:-1])

def makeSizes(photoPath, time, widths, dirPathToSave, indexDir, newName):
    for name in widths:
        makeResize(
            photoPath,
            dirPathToSave + '/' + name + '/' + indexDir,
            widths[name],
            newName)
    newDir = dirPathToSave + 'large/' + indexDir
    call(['mkdir', '-p', newDir])
    call(['cp', photoPath, newDir + '/' + newName])

def makeResize(photoPath, resizePath, width, newName):
    #print('resizing photo: ' + photoPath)
    try:
        im = Image.open(photoPath)
        size = (width, im.size[1] * (width*1.0 / im.size[0]))
        im.thumbnail(size, Image.ANTIALIAS)
        call(['mkdir', '-p', resizePath])
        newPath = resizePath + '/' + newName
        im.save(newPath, "JPEG")
        print('resized photo: %s' % newPath)
    except Exception as e:
        print('exception in resizing file: ' + photoPath)
        print(e)

def standardizedName(time, suffix, manual):
    # requires Python version >= 3.3
    #return str(int(time.timestamp())) + '.' + suffix

    return time.strftime('%Y%m%d_%H%M%S_%f' + ('manual' if manual else '')) + '.' + suffix

if __name__ == '__main__':
    main()