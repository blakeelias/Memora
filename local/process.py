from datetime import datetime
from subprocess import call, check_output
from PIL import Image

from download import get_local_file_path, getOriginalPath

def main():
    
    for line in check_output(['ls', getOriginalPath()]).split('\n'):
        photoFileName = line.split('\r')[0]
        try:
            (prefix, suffix) = photoFileName.split('.')
        except Exception as e:
            print(e)
            continue
        time, manual = getTime(prefix)
        if datetime(2014, 8, 14) < time < datetime(2014, 8, 18):
            makeSizes(getOriginalPath() + '/' + photoFileName,
                get_local_file_path(),
                {'thumbs': 158, 'previews': 632},
                newSubPath(time),
                standardizedName(time, suffix, manual))

def getTime(filePrefix):
    '''Return the time indicated by a file name's prefix string (i.e., everything before the file extension).
        Eg. for the file naemed '20140729_081534_016.jpg', you would call:

        >>> getTime('20140729_081534_016')
        datetime.datetime(2014, 7, 29, 8, 15, 34, 16000)
    '''
    manual = '_' in filePrefix
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

def makeSizes(photoPath, dirPathToSave, widths, indexDir, newName):
    for name in widths:
        makeResize(
            photoPath,
            dirPathToSave + '/' + name + '/' + indexDir,
            widths[name],
            newName)
    newDir = dirPathToSave + 'large/' + indexDir
    call(['mkdir', '-p', newDir])
    call(['mv', photoPath, newDir + '/' + newName])

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