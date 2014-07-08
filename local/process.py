from datetime import datetime
from subprocess import call, check_output
import Image

from download import get_local_file_path, getOriginalPath

def main():
    
    for photoName in check_output(['ls', getOriginalPath()]).split('\n'):
        photoName = photoName.split('\r')[0]
        time = getTime(photoName)
        makeSizes(getOriginalPath() + '/' + photoName, get_local_file_path(), {'thumbs': 158, 'previews': 632}, newSubPath(time))


def getTime(fileName):
    millis = int(fileName.split('.')[0])
    time = datetime.fromtimestamp(millis / 1000.0)
    return time

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

def makeSizes(photoPath, dirPathToSave, widths, indexDir):
    for name in widths:
        makeResize(
            photoPath,
            dirPathToSave + '/' + name + '/' + indexDir,
            widths[name])
    newDir = dirPathToSave + 'large/' + indexDir
    call(['mkdir', '-p', newDir])
    call(['mv', photoPath, newDir])

def makeResize(photoPath, resizePath, width):
    print('resizing photo: ' + photoPath)
    try:
        im = Image.open(photoPath)
        size = (width, im.size[1] * (width*1.0 / im.size[0]))
        im.thumbnail(size, Image.ANTIALIAS)
        name = getFileName(photoPath)
        call(['mkdir', '-p', resizePath])
        im.save(resizePath + '/' + name, "JPEG")
    except Exception as e:
        print('exception in resizing file: ' + photoPath)
        print(e)

if __name__ == '__main__':
    main()