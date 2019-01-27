




// EventSources

var sourceYoutube = new EventSource("/import/youtube");
sourceYoutube.onmessage = function(event) {
  var data = JSON.parse(event.data);
  console.log(event);
  if (data.processing) {
    $("#youtube-upload-progress").show();
    $("#youtube-upload-progress").text("Progress: " + (data.progress * 100) + "%");
  } else {
    $("#youtube-upload-progress").hide();
  }
};

