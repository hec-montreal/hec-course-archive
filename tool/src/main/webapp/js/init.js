
/**
 * Script that is executed when the page is loaded
 */
$(document).ready(function() {
	resizeIframe(475); //default value for sakai tools
	
	$('#search_form_button').button();					

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
		]
	});

	//initialise the fancy select for instructor
	$('.chosen').chosen({allow_single_deselect: true});
	populateInstructorsSelectBox();
	
	// if the url has a search hash, the user must be coming back (so populate search form and data table)
	if (window.location.hash === "#search") {
		oTable.fnAddData(JSON.parse(localStorage.getItem("searchResultsData")));
		var searchForm = JSON.parse(localStorage.getItem("searchForm"));
		
		$('#input_course_id').val(searchForm[0]);
		$('#input_course_title').val(searchForm[1]);
		// instructor dropdown is re-populated in populateInstructorsSelectBox() callback 
	}

	bindSearch();
	bindResultLinks();
});
