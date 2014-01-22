from subprocess import call, check_output

ADB_PATH = "/Users/blake/Downloads/adt-bundle-mac-x86_64-20131030/sdk/platform-tools/adb"
REMOTE_PHOTO_PATH = "/mnt/sdcard/DCIM/me/photos/"
MANUAL_PHOTO_PATH = "/mnt/sdcard/DCIM/Camera/"
LOCAL_PHOTO_PATH = "/Users/blake/Dropbox/me_photos/"
LAST_MANUAL_PHOTO_PATH = "./last_manual_photo.txt"

def main():
	pull_manual_photos()
	pull_auto_photos()

def pull_manual_photos():
	with open(LAST_MANUAL_PHOTO_PATH, 'r+') as last_photo_file:
		last_photo_name = last_photo_file.readline()

	manual_photos = check_output([ADB_PATH, "shell", "ls", MANUAL_PHOTO_PATH]).split('\n')
	for f in manual_photos:
		if f > last_photo_name:
			print("copying " + f)
			call([ADB_PATH, "pull", MANUAL_PHOTO_PATH + f, LOCAL_PHOTO_PATH])
			last_photo_name = f

	with open(LAST_MANUAL_PHOTO_PATH, 'w') as last_photo_file:
		last_photo_file.write(last_photo_name)

def pull_auto_photos():
	auto_photos = check_output([ADB_PATH, "shell", "ls", REMOTE_PHOTO_PATH])
	for f in auto_photos:
		call([ADB_PATH, "pull", REMOTE_PHOTO_PATH + f, LOCAL_PHOTO_PATH])
		call([ADB_PATH, "shell", "rm", REMOTE_PHOTO_PATH + f])

if __name__ == '__main__':
	main()