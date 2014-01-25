Photos = new Meteor.Collection("photos")

if (Meteor.isClient) {
  Template.hello.greeting = function () {
    return "Welcome to timeline.";
  };

  Template.hello.events({
    'click input' : function () {
      // template data, if any, is available in 'this'
      if (typeof console !== 'undefined')
        console.log("You pressed the button");

      Template.hello.greeting = function () {
        return Photos.find().map(function (doc, index, cursor) {
          return 
        });
      }
    }
  });
}

if (Meteor.isServer) {
  Meteor.startup(function () {
    var baseURL = '/photos/';
    var filenames = [
      '20140122_143251_742_v0.1.1_20140122_143354_247.jpg',
      '20140122_143251_742_v0.1.1_20140122_143453_561.jpg',
      '20140122_143251_742_v0.1.1_20140122_143553_643.jpg',
      '20140122_143251_742_v0.1.1_20140122_143653_669.jpg',
    ];
    for (i in filenames) {
      Photos.insert({
        'time_millis': filenameToTimestamp(filenames[i]),
        'url': baseURL + filenames[i]
      });
    }
  });
}

function filenameToTimestamp(filename) {
  var tokens = filename.split('\.jpg')[0].split('_');

  var yearMonthDay = tokens[tokens.length - 3];
  var hourMinuteSecond = tokens[tokens.length - 2];
  var millis = tokens[tokens.length - 1];

  var year = yearMonthDay.substring(0, 4);
  var month = yearMonthDay.substring(4, 6);
  var day = yearMonthDay.substring(6, 8);

  var hours = hourMinuteSecond.substring(0, 2);
  var minutes = hourMinuteSecond.substring(2, 4);
  var seconds = hourMinuteSecond.substring(4, 6);

  return new Date(year, month, day, hours, minutes, seconds, millis);
}