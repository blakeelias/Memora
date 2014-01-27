past = [
"20140111_183239_637_9d51ecd_20140111_183321_424.jpg",
"20140111_183239_637_9d51ecd_20140111_183340_923.jpg",
"20140111_183239_637_9d51ecd_20140111_183400_922.jpg",
"20140111_183239_637_9d51ecd_20140111_183420_884.jpg",
"20140111_183239_637_9d51ecd_20140111_183500_976.jpg",
"20140111_183239_637_9d51ecd_20140111_183520_894.jpg",
"20140111_183239_637_9d51ecd_20140111_183540_934.jpg",
"20140111_183239_637_9d51ecd_20140111_183600_971.jpg",
"20140111_183239_637_9d51ecd_20140111_183620_905.jpg",
"20140111_183239_637_9d51ecd_20140111_183641_022.jpg"]
future = [
"20140111_183239_637_9d51ecd_20140111_183700_894.jpg",
"20140111_183239_637_9d51ecd_20140111_183740_982.jpg",
"20140111_183239_637_9d51ecd_20140111_183800_943.jpg",
"20140111_183239_637_9d51ecd_20140111_183820_882.jpg",
"20140111_183239_637_9d51ecd_20140111_183841_056.jpg",
"20140111_183239_637_9d51ecd_20140111_183900_924.jpg",
"20140111_183239_637_9d51ecd_20140111_183920_876.jpg",
"20140111_183239_637_9d51ecd_20140111_183941_061.jpg",
"20140111_183239_637_9d51ecd_20140111_184001_486.jpg",
"20140111_183239_637_9d51ecd_20140111_184020_864.jpg"]

if (Meteor.isClient) {

    var picw = 2528;
    var pich = 1856;
    var scale = 8;
    var current_picture;

	$(document).ready(function()
	{
		
		var knob = $( "#slider_knob" );
		var slider = $( "#slider_body" );
		
		var sliderLeft = 0;
		var knobLeft = 0;
		var position = 0;

		knob.draggable(
		{
			containment: "parent",	
			axis: "x",
			drag: function() 
			{
	        	sliderLeft = slider.offset().left;
				knobLeft = knob.offset().left;
				knobRel = knobLeft - sliderLeft;
				position = knobRel / (slider.width() - knob.width()/2);
				//updatePix(position);
			},
			stop: function()
			{
				updatePix(photosNearDate(timeFromPos(position),8,8));
			}
		});

		$("#slider_body").click(function(event)
		{
			setKnobPos(event.pageX);
		});

		$(window).resize(function(){
			var stuff = setKnobPos(slider.offset().left +  Math.round(slider.width()*position));
		});
	});

	function timeFromPos(pos)
	{
		var d = new Date();
		d.setTime(pos*24*60*60*1000 + d.getTimezoneOffset()*60*1000);
		//This is hardcoded to Jan 22, 2014 for testing purposes.
		d.setFullYear(2014);
		d.setMonth(0);
		d.setDate(22);
		console.log(d.toString());
		return d;
	}

	function setKnobPos(pos)
	{
		var slider = $("#slider_body");
		var knob = $("#slider_knob");
		var newPos = 2;
		
		if(pos % 1 === 0) 
		{
			var maxPos = slider.width() + slider.offset().left - knob.width();
			var minPos = slider.offset().left;
			newPos = Math.max(minPos, Math.min(maxPos, pos - knob.width()/2));
		}
		else
		{
			var posn = Math.max(0, Math.min(pos, 1));
			newPos = slider.offset().left; + posn*slider.width() + knob.width()/2;
		}
		
		knob.offset({ left: newPos });
	}

	function snapFromTime(date)
	{
		var knob = $( "#slider_knob" );
		var slider = $( "#slider_body" );
		var msecs = (((date.getHours()*60) + date.getMinutes())*60 + date.getSeconds())*1000 + date.getMilliseconds(); 
		var pos = msecs/(60*60*24*1000);
		//var a = slider.offset().left;
		//var w = slider.width();
		//var newPos = a + pos*w + knob.width()/2;
		knob.offset({ left: pos});
	}
	//Doesn't work yet and not using this
	function setThumbs(thumbList, beforeOrAfterID, numSpaces)
	{
		//for(var i = numSpaces - thumbList.length; i < numSpaces; i++)
        for (var i = 0; i < thumbList.length; i++)
		{
			$("#" + beforeOrAfterID + " #" + String(i + numSpaces - thumbList.length)).attr("src", thumbList[i]['url']);
		}
        for(var i = 0; i < numSpaces - thumbList.length; i++)
        {
            $("#" + beforeOrAfterID + " #" + String(i)).attr("src", "/spacer.gif");
        }
	}

	function updatePix(photoList)
	{
		snapFromTime(photoList[0]["time_millis"]);
		$("#current_picture img").attr("src", photoList[0]["url"]);

        var beforePhotos = photoList[1];
        var afterPhotos = photoList[2];

        setThumbs(beforePhotos, "previous_pictures", 8);
        setThumbs(afterPhotos, "future_pictures", 8);
	}

    function photosNearDate(date, nBefore, nAfter) {
        /**
         * Returns a list 
         * [thisPhoto, beforePhotos, afterPhotos]
         *     thisPhoto: {url: <url>, time_millis: <date>}   --   the photo whose time_millis is closest to the argument "date"
         *     beforePhotos: list of the (up to) nBefore nearest photos to thisPhoto on the timeline that come before it
         *     afterPhotos: list of the (up to) nAfter nearest photos to thisPhoto on the timeline that come after it
         */

        var before = Photos.find({time_millis: {$lte: date}}, {limit: nBefore + 1, sort: {time_millis: -1}}).fetch();
        var after = Photos.find({time_millis: {$gte: date}}, {limit: nAfter + 1, sort: {time_millis: 1}}).fetch();

        var thisPhoto, beforePhotos, afterPhotos;

        if (before.length == 0 && after.length == 0) {
            thisPhoto = {};
            beforePhotos = before;
            afterPhotos = after;
        }
        else if (before.length == 0) {
            thisPhoto = after[0];
            beforePhotos = before;
            afterPhotos = after.slice(1);
        }
        else if (after.length == 0) {
            thisPhoto = before[0];
            beforePhotos = before.slice(1).reverse();
            afterPhotos = after;
        }
        else {
            var justBefore = before[0];
            var justAfter = after[0];
            if (justBefore == justAfter) {
                thisPhoto = justBefore;
                beforePhotos = before.slice(1).reverse();
                afterPhotos = after.slice(1);
            }
            else {
                var timeBefore = date - justBefore['time_millis'];
                var timeAfter = justAfter['time_millis'] - date;

                if (timeBefore < timeAfter) { // jump thisPhoto to justBefore
                    thisPhoto = justBefore;
                    beforePhotos = before.slice(1).reverse();
                    afterPhotos = after.slice(0, Math.min(after.length, nAfter));
                }
                else { // timeAfter <= timeBefore; jump thisPhoto to justAfter
                    thisPhoto = justAfter;
                    beforePhotos = before.slice(0, Math.min(before.length, nBefore)).reverse();
                    afterPhotos = after.slice(1);
                }
            }
        }
        return [thisPhoto, beforePhotos, afterPhotos];
    }
}