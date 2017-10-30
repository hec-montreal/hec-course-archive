<jsp:directive.include file="/templates/includes.jsp" />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />
    <link media="all" href="/library/skin/morpheus-default/tool.css" rel="stylesheet" type="text/css" />
	<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
	<link href="css/hec-course-archive.css" rel="stylesheet">
	<link href="plugins/jquery-ui-1.9.2.custom/css/smoothness/jquery-ui-1.9.2.custom.css" rel="stylesheet"> 
	
	<script src="plugins/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="plugins/jquery-ui-1.9.2.custom/js/jquery-ui-1.9.2.custom.min.js"></script>	

</head>
<body onLoad="window.parent.scroll(0, 0);">
<div id="courseView" class="portletBody ">
<div class="course_info_frame">
<div class="right">
<a href="#" onClick="history.back();return false;"><c:out value="${msgs.back_button_label}"/></a>
</div>

<h1 id="heading"></h1>

<div id="course_description_div" class="content">
<h2><c:out value="${msgs.description_label}"/></h2>
<div id="description_text"></div><br/>

<table id="course_details_table">
<tr class="ui-state-default" role="columnheader">
<th id="header_department"><c:out value="${msgs.department_label}"/></th>
<th id="header_career"><c:out value="${msgs.career_label}"/></th>
<th id="header_credits"><c:out value="${msgs.credits_label}"/></th>
<th id="header_requirements"><c:out value="${msgs.requirements_label}"/></th></tr>
<tr><td id="department"/><td id="career"/><td id="credits"/><td id="requirements"/>
</table>
</div>

<div id="course_outlines" class="content">
<h2><c:out value="${msgs.course_outline_list_label}"/></h2>
<table id="course_outline_table"></table>
</div>
</div>
</div>

<script  type="text/javascript">
var courseId = getUrlParam("courseId");
var instructor = getUrlParam("instructor");

$(document).ready(function() {
	
	
$.ajax({
		url : 'course_sections.json?' + 'courseId=' + courseId,
		datatype : 'json',
		success : function(sections) {
			var currentSession = null;
			// the list must be ordered by session, then section, else this will not display them correctly
			for (var i = 0; i < sections.data.length; i++) {
				if (sections.data[i].session !== currentSession) {
					currentSession = sections.data[i].session;
					$('#course_outline_table').append("<tr class=\"ui-state-default\" role=\"columnheader\"><th colspan='3'>"+currentSession+"</th></tr>");
				}				
			
				//if the instructor we select in the search is teaching the session, we highlight this section
				var classInstructor = '';
				if (localStorage.getItem("instructorSelected") !== "" && sections.data[i].instructor.indexOf(localStorage.getItem("instructorSelected")) !== -1) {
					classInstructor = " class='selectedInstructor' ";
				}				
				$('#course_outline_table').append("<tr" + classInstructor + " data-pdf-url='" + sections.data[i].pdf_url + "'><td>"+sections.data[i].section+"</td><td>"+sections.data[i].instructor+"</td><td class='pdf_icon_col'><img src='/library/image/silk/page_white_acrobat.png'></img></td></tr>");
			}
			
			$('#course_outline_table tr').click(function() {
				window.open($(this).attr('data-pdf-url'));
			});
		
		$.ajax({
			url : '/direct/portalManager/getOfficialCourseDescription.json?courseId=' + courseId,
			datatype : 'json',
			success : function(course) {
			    if (course == null)
			        $('#description_text').html(msgs.course_inactive);
				var JSONdepartmentGroupDescription = JSON.parse(localStorage.getItem("departmentDescriptionsMap"));
				var JSONcareerGroupDescription = JSON.parse(localStorage.getItem("careerDescriptionsMap"));
				var departmentGroupDescription = JSONdepartmentGroupDescription[course.departmentGroup];
				var careerGroupDescription = JSONcareerGroupDescription[course.careerGroup];
			
				$('#heading').html(course.hyphenatedCourseId + " - " + course.title);
				$('#description_text').html(course.description);
				$('#department').html(departmentGroupDescription);
				$('#career').html(careerGroupDescription);
				$('#credits').html(course.credits);
				$('#requirements').html(course.requirements);	
				resizeIframe();		
			} //end sucess for getting catalog descriptions
			
		});
	} //end sucess for getting sections		
	});
});

//function to get the url paramater
//found at http://stackoverflow.com/questions/831030/how-to-get-get-request-parameters-in-javascript
function getUrlParam(name){
   if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
      return decodeURIComponent(name[1]);
}
</script>
<script  type="text/javascript" src="js/functions.js"></script>
</body>
</html>
