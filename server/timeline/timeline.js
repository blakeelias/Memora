Photos = new Meteor.Collection("photos")

function filenameToTimestamp(filename) {
    var tokens = filename.split('\.jpg')[0].split('_');

    var yearMonthDay = tokens[tokens.length - 3];
    var hourMinuteSecond = tokens[tokens.length - 2];
    var millis = tokens[tokens.length - 1];

    var year = yearMonthDay.substring(0, 4);
    var month = parseInt(yearMonthDay.substring(4, 6)) - 1;
    var day = yearMonthDay.substring(6, 8);

    var hours = hourMinuteSecond.substring(0, 2);
    var minutes = hourMinuteSecond.substring(2, 4);
    var seconds = hourMinuteSecond.substring(4, 6);

    return new Date(year, month, day, hours, minutes, seconds, millis);
}

function getFilesInPublicFolder(folder) {
    /**
     * Return list of filenames that are contained in '<app>/public/<folder>/', where <folder>
     * is the value of the folder argument passed to this function.
     *
     * Only to be run on server, not client.
     */
    var fs = Npm.require('fs');
    return fs.readdirSync('/Users/blake/Pictures/' + folder + '/');
}

if (Meteor.isClient) {
    Template.hello.greeting = function () {
    return "Welcome to timeline.";
    };

    Template.hello.events({
    'click input' : function () {
        // template data, if any, is available in 'this'
        if (typeof console !== 'undefined')
        console.log("You pressed the button");
    }
    });
}

if (Meteor.isServer) {
    Meteor.startup(function () {
    var baseURL = 'Narrative Clip 2';
    var filenames = getFilesInPublicFolder(baseURL).filter(function(filename) {
        return filename.indexOf('.jpg') > 0;
    });
    console.log('in startup');
    console.log('filenames: ');
    console.log(filenames);

    for (i in filenames) {
        console.log(filenames[i]);
        Photos.update(
        {
            'time_millis': filenameToTimestamp(filenames[i]),
            'url': baseURL + '/' + filenames[i]
        },
        { $set : {}},
        {upsert: true});
    }
    });

    function addPhotos(publicPath) {
        var allFiles = getFilesInPublicFolder(publicPath);
    }
}