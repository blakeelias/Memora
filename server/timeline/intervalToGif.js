//Takes in a datetime, interval (in minutes), and frequency (seconds between each capture), and creates & stores a gif of the image
function intervalToGif(datetime, interval, frequency){
	//Determine start datetime
	var t1 = datetime.getTime();
	var t2 = t1 - (interval*60000);
	var time = new Date(t2);
	//Determine count of images
	var imageCount = (t1-t2)/(1000*frequency);
	//call photosNearDate function to retrieve photos, note dependency here
	var photos = photosNearDate(time, imageCount, 0)
	//require FS
	var fs = require('fs');
	//create new directory for jpg files
	var baseURL = '/photos';
	var directoryURL = baseURL + t2.toString();
	fs.mkdir(directoryURL, 0777 , function (error){
			if (error) throw error;		
		});
	//iterate through jpg files, moving and renaming each
	for (var i = 0; i < photos[1].length; i++){
		fs.rename(photos[1][i].url, directoryURL + (i+1).toString(), function (error){
			if (error) throw error;		
		});
	}
	//create regex to reference each of the moved jpg files
	var jpgFilenames = directoryURL + '%d.jpg[1-'+ photos[1].length.toString() + ']';
	var outputFilename = t2.toString + '.gif'; 
	//requre imagemagick
	var im = require('imagemagick');
	//Convert files to gif file
	im.convert([jpgFilenames, outputFilename], 
		function(error, stdout){
		  if (error) throw error;
		  console.log('stdout:', stdout);
	});
	//Update photos to include animated gif
	Photos.update(
        {
            'time_millis': time.getTime(),
            'url': directoryURL + '/' + outputFilename
        },
        { $set : {}},
        {upsert: true});
}



