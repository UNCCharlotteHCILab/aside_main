$(document).ready(function() {

	if (window.console) {
  		console.log("Welcome to your Play application's JavaScript!");
	}

	$(".expandable").hide();

	$(".info-head").click(function() {
		$(this).closest(".info-section").find(".info-content").slideToggle();
	});

	$("#example-xss").click(function() {
		$("#example-code").slideToggle();
	});
	$("#encode-html").click(function() {
		$("#html-code").slideToggle();
	});
	$("#encode-url").click(function() {
		$("#url-code").slideToggle();
	});
});