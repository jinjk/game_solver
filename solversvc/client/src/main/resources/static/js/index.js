var service_address = "http://localhost:1580/img";

String.prototype.format = function() {
    var formatted = this;
    for (var i = 0; i < arguments.length; i++) {
        var regexp = new RegExp('\\{'+i+'\\}', 'gi');
        formatted = formatted.replace(regexp, arguments[i]);
    }
    return formatted;
};

$(function() {

	var imageData = "";

	function add_canvas(img, i) {
		var canvas = $("<canvas id='chart{0}'/>".format(i)); // document.createElement("canvas");
		var block = $("<div class='ui-block'><div class='ui-bar ui-bar-a'></div></div>");

		block.children("div").html(canvas);
		$("#listview").append(block);
		
		canvas.attr('width', img.width);
		canvas.attr('height', img.height);

		var ctx = canvas.get(0).getContext("2d");
		ctx.drawImage(img, 0, 0);
		

	}

	function read_image(file) {
		if (file.files && file.files[0]) {
			var reader = new FileReader();

			reader.onload = function(e) {
				$('#preview').attr('src', e.target.result);
				
				$('#preview').load(function() {
					add_canvas($('#preview').get(0), 1);
				});
			}
			reader.readAsDataURL(file.files[0]);
			
			$(':mobile-pagecontainer').pagecontainer('change', '#pagetwo');
		}
	}

	$(':file').on('change', function(event) {
		read_image(event.target);
	});

	$('form').on('submit', function(event) {
		event.stopPropagation(); // Stop stuff happening
		event.preventDefault(); // Totally stop stuff happening

		file_ = $(":file")[0];
		if (!file_.name) {
			alert("请选择图片上传")
			return;
		}

		var data = new FormData();
		data.append(file_.name, file_.files[0]);

		$.mobile.loading("show");
		$.ajax({
			// Your server script to process the upload
			url : service_address,
			type : 'POST',
			// Form data
			data : data,

			// Tell jQuery not to process data or worry about content-type
			// You *must* include these options!
			cache : false,
			contentType : false,
			processData : false,

			// Custom XMLHttpRequest
			xhr : function() {
				var myXhr = $.ajaxSettings.xhr();
				if (myXhr.upload) {
					// For handling the progress of the upload
					myXhr.upload.addEventListener('progress', function(e) {
						if (e.lengthComputable) {
							// $('progress').attr({
							// value : e.loaded,
							// max : e.total,
							// });
						}
					}, false);
				}
				return myXhr;
			},
		}).done(function(data) {
			if (console && console.log) {
				console.log("Returned value:", data);
			}
			$(':mobile-pagecontainer').pagecontainer('change', '#pagetwo');
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
			$.mobile.loading("hide");
		});
	});
});