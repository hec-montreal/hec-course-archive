var frame = parent.document.getElementById(window.name);
$(frame).css('height', 1000);

/**
 * Script that is executed when the page is loaded
 */
$(document)
		.ready(
				function() {
					$('#search_form_button').button();					
					populateInstructorsSelectBox();
					oTable = $('#search_result_table').dataTable();
					$('#search_result_frame').hide();
					bindSearch();
				
					// bind click listener for table row to populate form
					$('#search_result_table tr').live("click", function() {
						var courseId = oTable.fnGetData(this, 0);
						
						window.location.href = "course.jsp?courseId="+courseId;
					});
				});
