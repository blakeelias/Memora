import os
from subprocess import call, check_output
from pprint import pprint

#######################################################################################
#############################  EDIT CONFIGURATION BELOW   #############################
#######################################################################################

# Change this to your local path to adb, the Android Debug Bridge.
# This can be found by locating the directory where you have the Android SDK installed,
#   then appending to that path /platform-tools/adb
# Test this out by opening a terminal and typing the path to this executable.
# For example:
#
#   Blakes-MacBook-Air:local blake$ /Users/blake/Downloads/adt-bundle-mac-x86_64-20131030/sdk/platform-tools/adb
#   Android Debug Bridge version 1.0.31
#
#    -a                            - directs adb to listen on all interfaces for a connection
#    -d                            - directs command to the only connected USB device
#                                    returns an error if more than one USB device is present.
#    -e                            - directs command to the only running emulator.
#                                    returns an error if more than one emulator is running.
#    ...
#
# On Windows, slashes may need to be written as escaped back-slashes (eg. "C:\\path\\to\sdk\platform-tools\adb.exe")
#
# Fill in ADB path here:
ADB_PATH = "adb"

#######################################################################################
#############################   DO NOT EDIT BELOW HERE   ##############################
#######################################################################################

AUTO_PHOTO_PATH = "/storage/emulated/legacy/wearscript/data/"
MANUAL_PHOTO_PATH = "/mnt/sdcard/DCIM/Camera/"

def main():
	local_file_path = get_local_file_path()
	last_manual_photo_path = local_file_path + "last_manual_photo.txt"
	pull_manual_photos(MANUAL_PHOTO_PATH, local_file_path, last_manual_photo_path, False)
	pull_auto_photos(AUTO_PHOTO_PATH, local_file_path)

def get_local_file_path():
	try:
		from local_settings import LOCAL_FILE_PATH
		return LOCAL_FILE_PATH
	except:
		return os.path.abspath('.') + '/'

def getOriginalPath():
    return get_local_file_path() + 'original/'

def pull_manual_photos(manual_photo_path, local_photo_path, last_manual_photo_path, downloadVideo):
	print('-'*30)
	print('pull_manual_photos()')
	print('-'*30)
	print('last_manual_photo_path = %s' % last_manual_photo_path)
	try:
		with open(last_manual_photo_path, 'r+') as last_photo_file:
			last_photo_name = last_photo_file.readline()
	except:
		last_photo_name = ''
	print('last_photo_name = %s' % last_photo_name)

	manual_photos = check_output([ADB_PATH, "shell", "ls", manual_photo_path]).split('\n')
	print('manual_photos: ')
	pprint(manual_photos)

	for f in manual_photos:
		f = f.split('\r')[0]
		if f > last_photo_name and ('.mp4' not in f or downloadVideo):
			print('copying ' + f)
			copy_file_and_process(manual_photo_path, f, local_photo_path)
			last_photo_name = f

	print('last_photo_name = %s' % last_photo_name)
	with open(last_manual_photo_path, 'w+') as last_photo_file:
		last_photo_file.write(last_photo_name)

def pull_auto_photos(auto_photo_path, local_photo_path):
	print('-'*30)
	print('pull_auto_photos()')
	print('-'*30)
	call(['mkdir', getOriginalPath()])
	#TODO: change below line to pull and remove each individual file in the folder,
	# rather than pulling the whole folder
	call([ADB_PATH, "pull", auto_photo_path, getOriginalPath()])

def copy_file_and_process(remote_photo_path, f, local_photo_path):
	call([ADB_PATH, "pull", remote_photo_path + f, local_photo_path])

if __name__ == '__main__':
	main()