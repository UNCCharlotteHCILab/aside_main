$(document).ready(function() {

	if (window.console) {
  		console.log("Welcome to your Play application's JavaScript!");
	}

	$(".expandable").hide();

	$(".info-head").click(function() {
		$(this).closest(".info-section").find(".info-content").slideToggle();
	});

	$("#example-sql").click(function() {
		$("#example-code").slideToggle();
	});
	$("#example-prepared").click(function() {
		$("#prepared-code").slideToggle();
	});
});