// EventSources
function updateProgress(service, event) {
      var data = JSON.parse(event.data);
      var elem = "#" + service + "-upload-progress";
      if (data.processing) {
        $(elem).show();
        var progress = Math.round(data.progress * 100);
        $(elem + " .progress-bar").text(progress + "%");
        $(elem + " .progress-bar").attr("aria-valuenow", progress + "");
        $(elem + " .progress-bar").css("width", progress + "%");
      } else {
        $(elem).hide();
      }
}

var sourceYoutube = new EventSource("/import/youtube");
sourceYoutube.onmessage = function(event) {
    updateProgress("youtube", event);
};

var sourceYoutube = new EventSource("/import/amazonmusic");
sourceYoutube.onmessage = function(event) {
    updateProgress("amazonmusic", event);
};

var sourceYoutube = new EventSource("/import/spotify");
sourceYoutube.onmessage = function(event) {
    updateProgress("spotify", event);
};



