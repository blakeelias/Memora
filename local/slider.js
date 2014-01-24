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
			/*$(this).text(String(position));*/
		}
	});

	$(window).resize(function(){
		var newPos = slider.offset().left + slider.width()*position;
		knob.offset({ left: newPos});
	});

});

