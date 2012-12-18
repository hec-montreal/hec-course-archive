/*****************************Initialisation of frame sizes  **********************************/
var iframeHeight = 500;

var frame = parent.document.getElementById(window.name);
$(frame).css('height', iframeHeight);

/**
 * Script that is executed when the page is loaded
 */
$(document)
		.ready(
				function() {			
					$('#search_form_button').button();
					oTable = $('#search_result_table').dataTable({
		"bJQueryUI" : true,
		"sAjaxSource" : 'stub.json',		
		"bAutoWidth": false,
		"bPaginate": false,
		"bInfo": false,
		"bFilter": false,
		  "sDom": 'rt',
		"aoColumns" : [
			 { "sWidth": "50%" }, // course id
			{ "sWidth": "50%" } // title
		]
	});
	// bind click listener for table row to populate form
	$('#search_result_table tr').live("click", function() {
		
	});
					
					/* var language = getLanguage();
					$('#tabs').tab();
					$('.collapse').collapse('toggle');
					$('.dropdown-toggle').dropdown();
					
					getBundle(language);
					bindSearch();		
					initCourseListing('career',
							'/direct/portalManager/getCareers/' + language + '.json');
					initCourseListing('department',
							'/direct/portalManager/getDepartments/' + language + '.json');										
					filterCatalogDescriptions();
					bindChangeLanguage();
					bindTabsSwitch();
					updateLabelsFromBundle(); */
				});

/**
 * Afficher qu'un div avec un message d'erreur si un des appels ajax plante
 */ 
$('#main').ajaxError(function(event, request, settings) {
	if (request.status != 404)
		$(this).html('<div id="error"><h3>Il y a un problème avec le serveur. Veuillez réessayer plus tard.</h3><h3>We are experiencing technical difficulties. Please try again later.</h3></div>');
});