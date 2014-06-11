//Takes in a datetime, interval (in minutes), and frequency (seconds between each capture), and creates & stores a gif of the image
function intervalToGif(datetime, interval, frequency){
	//Determine start datetime
	var t1 = datetime.getTime();
	var t2 = t1 - (interval*60000);
	var time = new Date(t2);
	//Determine count of images
	var imageCount = (t1-t2)/(1000*frequency);
	//call photosNearDate function to retrieve photos, note dependency here
	var photos = photosNearDate(time, imageCount, 0);
	//retrieve earliest photo url
	var photo1url = photos[1][0].url;
	var baseURL = '/photos';
	var jpgFilenames = baseURL + 'image-%d.jpg[1-5]'//TODO: implement regex calling each file in the interval based on the filename, not sure how to do this
	var outputFilename = placeholder.gif //TODO: determine output filename and location, also unclear
	//Import Node imagemagick
	var im = require('imagemagick');
	//Convert files to gif file
	im.convert([jpgFilenames, outputFilename], 
		function(err, stdout){
		  if (err) throw err;
		  console.log('stdout:', stdout);
	});
	//Update photos to include animated gif
	Photos.update(
        {
            'time_millis': time.getTime(),
            'url': baseURL + '/' + outputFilename
        },
        { $set : {}},
        {upsert: true});
}



