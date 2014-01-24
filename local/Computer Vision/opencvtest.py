import cv2
import numpy as np
from matplotlib import pyplot as plt
from subprocess import call, check_output

pix = ['20140111_183239_637_9d51ecd_20140111_190041_307.jpg', '20140111_201536_081_686cff9_20140111_211437_730.jpg', '20140111_201536_081_686cff9_20140111_214337_656.jpg',
		'20140111_201536_081_686cff9_20140111_214537_741.jpg', '20140111_201536_081_686cff9_20140111_223237_787.jpg', '20140111_201536_081_686cff9_20140111_223638_041.jpg',
		'20140111_225728_101_686cff9_20140111_233529_605.jpg', '20140111_225728_101_686cff9_20140112_004729_893.jpg', '20140111_225728_101_686cff9_20140112_005629_858.jpg']

def imageBlur(filename):
	kernel_size = 3
	scale = 1
	delta = 0
	ddepth = cv2.CV_16S
	window_name = "Laplace Demo"

	img = cv2.imread(filename, 0)
	this = img.copy()
	this = cv2.blur(img, (4,4))
	this = cv2.Laplacian(this, ddepth, this, kernel_size, scale, delta)
	this = cv2.convertScaleAbs(this)
	#hist = cv2.calcHist([img],[0],None,[256],[0,256])
	#return int((hist[255][0] + hist[254][0] + hist[253][0])/3)
	#plt.plot(hist)
	#plt.show()
	cv2.namedWindow('image', cv2.WINDOW_NORMAL)
	cv2.imshow('image',this)
	cv2.waitKey(0)
	cv2.destroyAllWindows()

auto_photos = check_output(["ls"]).split('\n')
'''
for f in auto_photos:
	if 'jpg' in f: 
		value = imageBlur(f)
		if value < 200:
			print f
'''

for f in pix:
	imageBlur(f)

