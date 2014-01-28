if (Meteor.isClient) {

    var picw = 2528;
    var pich = 1856;
    var scale = 8;
    var current_picture;

	$(document).ready(function()
	{
		
		var knob = $( "#slider_knob" );
		var slider = $( "#slider_body" );

		knob.draggable(
		{
			containment: "parent",	
			axis: "x",
			drag: function() 
			{
	        	
			},
			stop: function()
			{
				updatePhotosFromSlider();
			}
		});

		$("#slider_body").click(function(event)
		{
			setKnobPos(event.pageX);
            updatePhotosFromSlider();
		});

		$(window).resize(function(){
			setKnobPos(slider.offset().left +  Math.round(slider.width()*knobPosition));
		});
	});

    function updatePhotosFromSlider() {
        knobPosition = getKnobPos();
        updatePix(photosNearDate(timeFromPos(knobPosition),8,8));
    }

	function timeFromPos(pos)
	{
		var d = new Date();
		d.setTime(pos*24*60*60*1000 + d.getTimezoneOffset()*60*1000);
		//This is hardcoded to Jan 22, 2014 for testing purposes.
		d.setFullYear(2014);
		d.setMonth(0);
		d.setDate(22);
		return d;
	}

	function getKnobPos()
	{
		var knob = $( "#slider_knob" );
		var slider = $( "#slider_body" );
		
        return (knob.offset().left - slider.offset().left) / (slider.width() - knob.width());
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
			newPos = slider.offset().left + Math.round(posn*slider.width()) + knob.width()/2;
		}
		
		knob.offset({ left: newPos });
	}

	function snapFromTime(date)
	{
		var knob = $( "#slider_knob" );
		var slider = $( "#slider_body" );
		var msecs = (((date.getHours()*60) + date.getMinutes())*60 + date.getSeconds())*1000 + date.getMilliseconds(); 
		var pos = msecs/(60*60*24*1000);
		setKnobPos(pos);
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
            $("#" + beforeOrAfterID + " #" + String(i)).attr("src", "");
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