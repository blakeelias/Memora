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

		setThumbs();

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
				updatePix(position);
			},
			stop: function()
			{
				var str = timeFromPos(position).toUTCString();
				$("#current_picture img").attr("alt", str);
			}
		});

		$("#slider_body").click(function(event)
		{
			setKnobPos(event.pageX);
		});

		$(window).resize(function(){
			setKnobPos(slider.offset().left +  Math.round(slider.width()*position));
		});
	});

	function timeFromPos(pos)
	{
		var d = new Date();
		d.setTime(pos*24*60*60*1000);
		//This is hardcoded to Jan 22, 2014 for testing purposes.
		d.setFullYear(2014);
		d.setMonth(0);
		d.setDate(22);
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
		var a = slider.offset().left;
		var w = slider.width();
		var newPos = a + pos*w + knob.width()/2;
		knob.offset({ left: newPos});
	}
	//Doesn't work yet and not using this
	function setThumbs(picIndex)
	{
		var length = 1234;
		var previousPix = Math.min(8, picIndex - 8);
		var futurePix = Math.min(8, length - picIndex);
		for(var i = picIndex - previousPix; i < 8 + previousPix; i++)
		{
			$("#previous_pictures " + String(i)).attr("src", "photos/" + past[i]);
		}

		for(var i = picIndex - previousPix; i < 8 + previousPix; i++)
		{
			$("#previous_pictures " + String(i)).attr("src", "photos/" + past[i]);
		}
	}
}