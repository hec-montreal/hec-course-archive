
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
				minimumResultsForSearch: 15
			});
			
			// if the url has a search hash, the user must be coming back (so populate search form and data table)
			if (window.location.hash === "#search") {
				oTable.fnAddData(JSON.parse(localStorage.getItem("searchResultsData")));
				var searchForm = JSON.parse(localStorage.getItem("searchForm"));

				// populate instructors list
				var instructors = JSON.parse(localStorage.getItem("instructorsList"));
				for ( var i = 0; i < instructors.length; i++) {
					$('#input_course_teacher').append(
						'<option value="' + i + '">'
							+ instructors[i]
							+ '</option>');
				}

				$('#input_course_id').val(searchForm[0]);
				$('#input_course_title').val(searchForm[1]);
				$('#input_course_teacher').val(searchForm[2]).trigger("change");
				$('#input_course_lang').val(searchForm[4]).trigger("change");
			}
			else{
				$('#search_result_frame').hide();
				populateInstructorsSelectBox();
			}

			bindSearch();
			bindResultLinks();
			resizeIframe();	
			initializeGroupDescriptions();				
			initializeInputClearing();
		}
	});
});