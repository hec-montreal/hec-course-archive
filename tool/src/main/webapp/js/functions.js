/**
 * Bind action to execute when we click on the Search button
 */
function bindSearch() {	
	$('#search_form_button').click(function() {
		oTable.fnClearTable();
		var instructorSelected = '';
		if (!$('#input_course_teacher_chzn > a').hasClass('chzn-default')){
			instructorSelected = $('#input_course_teacher_chzn > a > span').text() ;
		}

	$.ajax({
		url : 'search.json',
		datatype : 'json',
		data : 'courseId=' + $('#input_course_id').val() + '&courseTitle=' + encodeURIComponent($('#input_course_title').val()) + '&courseInstructor=' + instructorSelected,		
		success : function(searchResults) {	
			oTable.fnAddData(searchResults.aaData);

			// save search form and results in localStorage to make back button work
			localStorage.setItem("searchForm", JSON.stringify([$('#input_course_id').val(), $('#input_course_title').val(), $('#input_course_teacher').val()]));
			localStorage.setItem("searchResultsData", JSON.stringify(oTable.fnGetData()));
		
			window.location.hash = "search";
			resizeIframe();
		}});
	});		
}

function bindResultLinks() {
	// bind click listener for table row to populate form
	$('#search_result_table tr').live("click", function() {
		var courseId = oTable.fnGetData(this, 0);
		window.location.href = "course.jsp?courseId="+courseId;
	});
}

/**
 * Populate the select box that list the available instructors
 */
function populateInstructorsSelectBox(){
	$.ajax({
		url : 'instructors.json',
		datatype : 'json',
		success : function(listInstructors) {			
			for ( var i = 0; i < listInstructors.data.length; i++) {
				$('#input_course_teacher').append('<option value="' + i + '">' + listInstructors.data[i] + '</option>');
			}					

			if (window.location.hash === "#search") {
				var searchForm = JSON.parse(localStorage.getItem("searchForm"));
				$('#input_course_teacher').val(searchForm[2]);
			}
			
			//update the list
			$('#input_course_teacher').trigger("liszt:updated");
		}});
}

function resizeIframe(height) {
	if (!height && $('.portletBody').outerHeight() > 475) {
		height = $('.portletBody').outerHeight();
	} else {
		height = 475; // this is the default height for a sakai tool
	}
	
	var frame = parent.document.getElementById(window.name);
	$(frame).css('height', height);
}