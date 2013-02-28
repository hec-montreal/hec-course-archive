
/**
 * Script that is executed when the page is loaded
 */
$(document).ready(function() {	
	$('#search_form_button').button();	
	
	$.ajax({
		url : 'bundle.json',
		datatype : 'json',
		async : false,
		success : function(bundle) {
			messageBundle = bundle.data;
			localStorage.setItem("locale", bundle.locale);
			
			
			oTable = $('#search_result_table').dataTable({
				"bJQueryUI" : true,
				"bAutoWidth": false,
				"bPaginate": false,
				"bInfo": false,
				"bFilter": false,
				"sDom": 'rt',
				"aoColumns" : [
					null, // course id
					null // course title
				],
				"oLanguage": {
				      "sEmptyTable": messageBundle["result_empty"]
				    }});

			//initialise the fancy select for instructor
			$('.chosen').select2({
				  matcher: function(term, text) { 
					var wordsList = term.split(' ');
					for ( var i = 0; i < wordsList.length; i++) {
						if  (text.toUpperCase().indexOf(wordsList[i].toUpperCase())== -1 ){
							return 0;
						}						
					}
					return 1;
				},
				allowClear: true,
				minimumResultsForSearch: 15,
				formatNoMatches: function(term) { 
					return messageBundle["no_match_for_instructor"] + " " + term;
						}	
			});
			
			// if the url has a search hash, the user must be coming back (so populate search form and data table)
			if (window.location.hash === "#search") {
				oTable.fnAddData(JSON.parse(localStorage.getItem("searchResultsData")));
				var searchForm = JSON.parse(localStorage.getItem("searchForm"));

				// populate instructors list
				var instructors = JSON.parse(localStorage.getItem("instructorsList"));
				if (instructors){
					for ( var i = 0; i < instructors.length; i++) {
						$('#input_course_teacher').append(
							'<option value="' + i + '">'
								+ instructors[i]
								+ '</option>');
					}
				}
				
				$('#input_course_id').val(searchForm[0]);
				$('#input_course_title').val(searchForm[1]);
				$('#input_course_teacher').val(searchForm[2]).trigger("change");
				$('#input_course_lang').val(searchForm[4]).trigger("change");

				// resize the iframe, then set the scrollbar to what it was
				resizeIframe();			
				$(window.parent.document).scrollTop(localStorage.getItem("windowScrollPos"));
			}
			else{
				$('#search_result_frame').hide();
				populateInstructorsSelectBox();
				resizeIframe();	
			}

			bindSearch();
			bindResultLinks();
			initializeGroupDescriptions();				
			initializeInputClearing();
		}
	});
});

/**
 * Afficher un message d'erreur si un des appels ajax plante
 */ 
$('#search_form_frame').ajaxError(function(event, request, settings) {
	if (request.status != 404 && request.status != 403 && request.status != 0)
		$(this).html('<div id="error"><h3>Il y a un problème avec le serveur. Veuillez réessayer plus tard.</h3><h3>We are experiencing technical difficulties. Please try again later.</h3></div>');
		$('#search_result_frame').hide();
});