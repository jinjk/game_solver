var service_address = "http://ddouu.cn:8080/img";
var col_number = 10;
var row_number = 10;

var messages = {
	"0" : "Cannot connect to server",
	"3001" : "请选择图片上传",
	"3002" : "The file size should be less that 1M",
	"4001" : "Cannot understand you image",
	"4002" : "Cannot find all the stars in you image, we need 100 (10 X 10)"
}

String.prototype.format = function() {
	var formatted = this;
	for (var i = 0; i < arguments.length; i++) {
		var regexp = new RegExp('\\{' + i + '\\}', 'gi');
		formatted = formatted.replace(regexp, arguments[i]);
	}
	return formatted;
};

$(function() {

	// ----------------- init code --------------
	$(".slider").slick();
	// ----------------- end init ---------------

	var imageData = "";

	function draw_canvases(img, data) {
		var s_width = data.icons["1"].w;
		var grid = init_canvas_grid(img.naturalWidth, s_width);

		var len = data.walls.length;
		for (var i = 0; i < len; i++) {
			var index = i;

			var step = data.walls[index];
			var canvas = $("<canvas id='chart{0}'/>".format(index)); // document.createElement("canvas");
			var block1 = $("<div class='ui-block'><div class='ui-bar ui-bar-b'></div></div>");
			block1.children('div').html(canvas);
			$(".slider").append(block1);
			$('.slider').slick('slickAdd', block1);

			canvas.attr('width', img.naturalWidth);
			canvas.attr('height', img.naturalWidth);

			var ctx = canvas.get(0).getContext("2d");

			draw_step(step, grid, data.icons, img, ctx);
		}

	}

	function init_canvas_grid(width, s_width) {
		var grid = new Array();
		margin = (width - s_width * col_number) / (col_number + 1);

		for (var i = 0; i < row_number; i++) {
			var col = new Array();
			for (var j = 0; j < col_number; j++) {
				var pos = new Object();
				pos.x = j * s_width + (j + 1) * margin;
				pos.y = i * s_width + (i + 1) * margin;
				pos.w = s_width;
				pos.h = s_width;
				col[j] = pos;
			}
			grid[i] = col;
		}

		return {
			'grid' : grid,
			'margin' : margin
		};
	}

	function draw_step(step, grid, icons, img, ctx) {
		for (var i = 0; i < step.columns.length; i++) {
			var col = step.columns[i];
			for (var j = 0; j < col.bricks.length; j++) {
				var br = col.bricks[j];
				draw_star(img, ctx, icons[br.ch], grid.grid[br.y][br.x],
						br.marked, grid.margin);
			}
		}
	}

	function draw_star(img, ctx, src_pos, des_pos, marked, margin) {
		var rate = 0.7;
		var x = des_pos.x;
		var y = des_pos.y
		var w = des_pos.w;
		var h = des_pos.h;

		if (marked) {
			ctx.beginPath();
			ctx.lineWidth = margin / 2;
			ctx.setLineDash([ 6, 12 ]);
			ctx.strokeStyle = "white";

			ctx.rect(des_pos.x, des_pos.y, des_pos.w, des_pos.h);
			ctx.stroke();

		} else {
			ctx.drawImage(img, src_pos.x, src_pos.y, src_pos.w, src_pos.h, x,
					y, w, h);
		}

		// ctx.drawImage(img, 0, 0, 100, 100,
		// 0, 0, 100, 100);
		// ctx.drawImage(img, 100, 100, 200, 200,
		// 100, 100, 200, 200);
	}

	function show_steps(file, data) {
		if (file.files && file.files[0]) {
			var reader = new FileReader();

			reader.onload = function(e) {
				$('#preview').attr('src', e.target.result);

				$('#preview').load(function() {
					draw_canvases($('#preview').get(0), data);
				});
			}
			reader.readAsDataURL(file.files[0]);

			$(':mobile-pagecontainer').pagecontainer('change', '#pagetwo');
		}
	}

	function show_message(code) {
		$("#msg_content").text(messages[code]);
		$("#message").popup("open");
	}

	$('form')
			.on(
					'submit',
					function(event) {
						event.stopPropagation(); // Stop stuff happening
						event.preventDefault(); // Totally stop stuff happening
						file = $(":file");
						file_ = file[0];

						if (!file_.name) {
							show_message(3001);
							return;
						}
						// Check size is less than 2MB
						if (file_.files[0].size > 2 * 1024 * 1024) {
							show_message(3002);
							return;
						}

						var data = new FormData();
						data.append(file_.name, file_.files[0]);

						$.mobile.loading("show", {
							text : "Loading",
							textVisible : true,
							theme : "b",
							textonly : false,
							html : ""
						});
						$
								.ajax(
										{
											// Your server script to process the
											// upload
											url : service_address,
											type : 'POST',
											// Form data
											data : data,

											// Tell jQuery not to process data
											// or worry about content-type
											// You *must* include these options!
											cache : false,
											contentType : false,
											processData : false,

											// Custom XMLHttpRequest
											xhr : function() {
												var myXhr = $.ajaxSettings
														.xhr();
												if (myXhr.upload) {
													// For handling the progress
													// of the upload
													myXhr.upload
															.addEventListener(
																	'progress',
																	function(e) {
																		if (e.lengthComputable) {
																			percentage = Math
																					.round((e.loaded / e.total) * 100);
																			$(
																					".ui-loader")
																					.children(
																							"h1")
																					.text(
																							percentage
																									+ "%");

																			if (percentage == 100) {
																				$(
																						".ui-loader")
																						.children(
																								"h1")
																						.text(
																								"waiting caculation...");
																			}
																		}
																	}, false);
												}
												return myXhr;
											},
										}).done(function(data) {
									if (console && console.log) {
										console.log("Returned value:", data);
									}
									show_steps(file_, data);
								})
								.fail(function(jqXHR, textStatus, errorThrown) {
									statusCode = jqXHR.status;
									json = jqXHR.responseText;
									data = {};

									if (json) {
										try {
											data = JSON.parse(json);
											statusCode = data.code;
										} catch (err) {
											console.log(json);
											console.log(err.message);
										}

									}

									show_message(statusCode);
								}).always(function() {
									$.mobile.loading("hide");
								});
					});
});