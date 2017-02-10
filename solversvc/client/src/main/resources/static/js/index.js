var service_address = "http://localhost:1580/img";
$(function() {
	$('form').on('submit', function(event) {
		event.stopPropagation(); // Stop stuff happening
		event.preventDefault(); // Totally stop stuff happening
		
		file_ = $(":file")[0];
		console.log(file_);
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

			$("#charts").html(JSON.stringify(data));
			$(':mobile-pagecontainer').pagecontainer('change', '#pagetwo');
		}).fail(function(jqXHR, textStatus, errorThrown) {
		}).always(function() {
			$.mobile.loading("hide");
		});
	});
});