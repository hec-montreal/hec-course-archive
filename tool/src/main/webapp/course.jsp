<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />
    <link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css" />
	<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
	<link href="css/hec-course-archive.css" rel="stylesheet">
	<link href="plugins/jquery-ui-1.9.2.custom/css/smoothness/jquery-ui-1.9.2.custom.css" rel="stylesheet"> 
	
	<script src="plugins/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="plugins/jquery-ui-1.9.2.custom/js/jquery-ui-1.9.2.custom.min.js"></script>	

</head>
<body>
<div id="courseView">
<h1 id="heading"></h1>

<div id="course_description_div">
<h2>Description</h2>
<div id="description_text"></div>

<table id="course_details_table">
<tr><th>Discipline</th><th>Programme</th><th>Crédit(s)</th><th>Exigences</th></tr>
<tr><td id="department"/><td id="career"/><td id="credits"/><td id="requirements"/>
</table>
</div>

<div id="course_outlines">
<h2>Liste des plans de cours disponible</h2>
<table id="course_outline_table"></table>
</div>

</div>

<script  type="text/javascript">
var courseId = getUrlParam("courseId");

$(document).ready(function() {
	$.ajax({
		url : 'course.json',
		data : 'courseId=' + courseId,
		datatype : 'json',
		success : function(course) {
			$('#heading').html(course.courseId + " - " + course.title);
			$('#description_text').html(course.description);
			$('#department').html(course.department);
			$('#career').html(course.career);
			$('#credits').html(course.credits);
			$('#requirements').html(course.requirements);
			
			var currentSession = null;
			for (var i = 0; i < course.sections.length; i++) {
				if (course.sections[i].session !== currentSession) {
					currentSession = course.sections[i].session;
					$('#course_outline_table').append("<tr class=\"ui-state-default\" role=\"columnheader\"><th colspan='3'>"+currentSession+"</th></tr>");
				}
				$('#course_outline_table').append("<tr><td>"+course.sections[i].section+"</td><td>"+course.sections[i].instructor+"</td><td class='pdf_icon_col'><a href='#'><img src='/library/image/silk/page_white_acrobat.png'></img></a></td></tr>");
			}			
		},
		error : function(xhr, ajaxOptions, thrownError) {
//			$('#ajaxMessage').html(server_error_message);
//			$('#ajaxReturn').addClass("error");
		}
	});
});

//function to get the url paramater
//found at http://stackoverflow.com/questions/831030/how-to-get-get-request-parameters-in-javascript
function getUrlParam(name){
   if(name=(new RegExp('[?&]'+encodeURIComponent(name)+'=([^&]*)')).exec(location.search))
      return decodeURIComponent(name[1]);
}
</script>
</body>
</html>
