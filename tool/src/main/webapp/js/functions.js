/**
 * Bind action to execute when we click on the Search button
 */
function bindSearch() {	
	$('#search_form_button').click(function() {
	
			var courseId=$('#input_course_id').val();
			var courseTitle=$('#input_course_title').val();
			var courseTeacher=$('#input_course_teacher').val();
			
			oTable = $('#search_result_table').dataTable({
			"bJQueryUI" : true,
			"sAjaxSource" : 'search.json',		
			"bAutoWidth": false,
			"bPaginate": false,
			"bInfo": false,
			"bFilter": false,
			"sDom": 'rt',
			"bDestroy": true,
			"fnServerParams": function ( aoData ) {				
				aoData.push( { "name": "courseId", "value": courseId });
				aoData.push( { "name": "courseTitle", "value": courseTitle });
				aoData.push( { "name": "courseTeacher", "value": courseTeacher });
			},
			"aoColumns" : [
			{ "sWidth": "50%" }, // course id
			{ "sWidth": "50%" } // course title
			]
		});
		$('#search_result_frame').show();
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
					$('#input_course_teacher').append('<option>' + listInstructors.data[i] + '</option>');
				}					
				$('.chosen').chosen({allow_single_deselect: true});
		}});
}