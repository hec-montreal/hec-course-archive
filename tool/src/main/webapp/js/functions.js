/**
 * Bind action to execute when we click on the Search button
 */
function bindSearch() {
	$(document).keypress(function(e) {
		if (e.which == 13) {
			launchSearch();
		}
	});

	$('#search_form_button').click(function() {
		launchSearch();
	});
}

/**
 * Launch the search process
 */
function launchSearch() {

	$('#search_result_frame').show();
	oTable.fnClearTable();
	var instructorSelected = '';
	if (!$('#s2id_input_course_teacher a.select2-choice').hasClass('select2-default')) {
		instructorSelected = $('#s2id_input_course_teacher a.select2-choice span').text();
	}

	$(
			'<td id=\"loader-container" valign="top" colspan="2"><span data-bundle-key=\"message_loading\">'
					+ messageBundle["msg_loading"]
					+ '</span><div class=\"icon-loader\"></div></td>')
			.insertAfter('.dataTables_empty');
	$('.dataTables_empty').hide();

	$
			.ajax({
				url : 'search.json',
				datatype : 'json',
				data : 'courseId=' + $('#input_course_id').val()
						+ '&courseTitle='
						+ encodeURIComponent($('#input_course_title').val())
						+ '&courseInstructor='
						+ encodeURIComponent(instructorSelected)
						+ '&courseCareerGroup='
						+ $('#input_course_career').val()
						+ '&courseLanguage='
						+ $('#input_course_lang').val(),
				success : function(searchResults) {
					oTable.fnAddData(searchResults.aaData);

					// save search form and results in localStorage to make back
					// button work
					localStorage.setItem("searchForm", JSON.stringify([
							$('#input_course_id').val(),
							$('#input_course_title').val(),
							$('#input_course_teacher').val(),
							$('#input_course_career').val(),
							$('#input_course_lang').val() ]));
					localStorage.setItem("searchResultsData", JSON
							.stringify(oTable.fnGetData()));
					localStorage.setItem("instructorSelected",instructorSelected);

					window.location.hash = "search";
					resizeIframe();
					$('.dataTables_empty').show();
					$('#loader-container').hide();
				}
			});
}

function bindResultLinks() {
	// bind click listener for table row to populate form
	$('#search_result_table tr').live("click", function() {
		var courseId = oTable.fnGetData(this, 0);
		window.location.href = "course.jsp?courseId=" + courseId;
	});
}

/**
 * Populate the select box that list the available instructors
 */
function populateInstructorsSelectBox() {
	$
			.ajax({
				url : 'instructors.json',
				datatype : 'json',
				success : function(listInstructors) {
					for ( var i = 0; i < listInstructors.data.length; i++) {
						$('#input_course_teacher').append(
							'<option value="' + i + '">'
								+ listInstructors.data[i]
								+ '</option>');
					}

					localStorage.setItem("instructorsList", JSON.stringify(listInstructors.data));
					// update the list
					$('#input_course_teacher').trigger("liszt:updated");
				}
			});
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

function initializeGroupDescriptions() {

	// used to keep the language specific descriptions of the
	// careers/departments for display later
	var careerDescriptionsMap = {};
	var departmentDescriptionsMap = {};

	$
			.ajax({
				url : '/direct/portalManager/getCareers/'
						+ localStorage.getItem("locale") + '.json',
				datatype : 'json',
				success : function(listItems) {
					for ( var i = 0; i < listItems.portalManager_collection.length; i++) {
						careerDescriptionsMap[listItems.portalManager_collection[i].itemGroup] = listItems.portalManager_collection[i].description;
						$('#input_course_career')
						.append(
								'<option value="' + listItems.portalManager_collection[i].itemGroup + '">'
										+ listItems.portalManager_collection[i].description
										+ '</option>');
					}
					if (window.location.hash === "#search") {
						var searchForm = JSON.parse(localStorage
								.getItem("searchForm"));
						$('#input_course_career').val(searchForm[3]).trigger("change");
					}
					
					localStorage.setItem("careerDescriptionsMap", JSON
							.stringify(careerDescriptionsMap));

				}
			});

	$
			.ajax({
				url : '/direct/portalManager/getDepartments/'
						+ localStorage.getItem("locale") + '.json',
				datatype : 'json',
				success : function(listItems) {
					for ( var j = 0; j < listItems.portalManager_collection.length; j++) {
						departmentDescriptionsMap[listItems.portalManager_collection[j].itemGroup] = listItems.portalManager_collection[j].description;
					}
					localStorage.setItem("departmentDescriptionsMap", JSON
							.stringify(departmentDescriptionsMap));
				}
			});
}

function initializeInputClearing() {
	$('.ui-icon-delete').hide();

	// set the clear cross at the end of the non empty input fields
	if ($('#input_course_id').val()){
		$('#input_course_id').parent().find(".ui-icon-delete").show();
	}
	if ($('#input_course_title').val()){
		$('#input_course_title').parent().find(".ui-icon-delete").show();
	}
	$('.ui-icon-delete').click(function(){
        $(this).parent().find("input").val("");
        $(this).hide();
		});
	
	 $("input").change(function(){
		$(this).parent().find(".ui-icon-delete").show();
		});
}
