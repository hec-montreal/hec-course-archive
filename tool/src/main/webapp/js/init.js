var frame = parent.document.getElementById(window.name);
$(frame).css('height', 1000);

/**
 * Script that is executed when the page is loaded
 */
$(document)
		.ready(
				function() {
					$('#search_form_button').button();
					$('.chosen').chosen({allow_single_deselect: true});
					$('#search_result_table').dataTable();
					$('#search_result_frame').hide();
					bindSearch();
				});
