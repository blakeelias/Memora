import os
from subprocess import call, check_output

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
ADB_PATH = "/Users/blake/Downloads/adt-bundle-mac-x86_64-20131030/sdk/platform-tools/adb"

#######################################################################################
#############################   DO NOT EDIT BELOW HERE   ##############################
#######################################################################################

REMOTE_PHOTO_PATH = "/mnt/sdcard/DCIM/me/photos/"
MANUAL_PHOTO_PATH = "/mnt/sdcard/DCIM/Camera/"
LAST_MANUAL_PHOTO_PATH = "./last_manual_photo.txt"

def main():
	file_path = get_file_path()
	pull_manual_photos(file_path)
	pull_auto_photos(file_path)

def get_file_path():
	return os.path.abspath('.')

def pull_manual_photos(local_photo_path):
	with open(LAST_MANUAL_PHOTO_PATH, 'r+') as last_photo_file:
		last_photo_name = last_photo_file.readline()

	manual_photos = check_output([ADB_PATH, "shell", "ls", MANUAL_PHOTO_PATH]).split('\n')
	for f in manual_photos:
		f = f.split('\r')[0]
		if f > last_photo_name:
			print('copying ' + f)
			call([ADB_PATH, "pull", MANUAL_PHOTO_PATH + f, local_photo_path])
			last_photo_name = f

	with open(LAST_MANUAL_PHOTO_PATH, 'w') as last_photo_file:
		last_photo_file.write(last_photo_name)

def pull_auto_photos(local_photo_path):
	auto_photos = check_output([ADB_PATH, "shell", "ls", REMOTE_PHOTO_PATH]).split('\n')
	for f in auto_photos:
		f = f.split('\r')[0]
		print('copying ' + f)
		call([ADB_PATH, "pull", REMOTE_PHOTO_PATH + f, local_photo_path])
		call([ADB_PATH, "shell", "rm", REMOTE_PHOTO_PATH + f])

if __name__ == '__main__':
	main()