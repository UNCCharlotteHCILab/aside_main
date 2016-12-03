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
	$("#validate-alpha").click(function() {
		$("#alpha-code").slideToggle();
	});
	$("#validate-credit").click(function() {
		$("#credit-code").slideToggle();
	});
	$("#validate-email").click(function() {
		$("#email-code").slideToggle();
	});
	$("#validate-http").click(function() {
		$("#http-code").slideToggle();
	});
	$("#validate-ssn").click(function() {
		$("#ssn-code").slideToggle();
	});
	$("#validate-url").click(function() {
		$("#url-code").slideToggle();
	});
	$("#example-sql").click(function() {
		$("#example-code").slideToggle();
	});
	$("#example-prepared").click(function() {
		$("#prepared-code").slideToggle();
	});
	$("#encode-html").click(function() {
		$("#html-code").slideToggle();
	});
	$("#encode-url").click(function() {
		$("#url-code").slideToggle();
	});
});