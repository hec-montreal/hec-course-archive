<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />
    <link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css" />
	<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
	<link href="css/hec-course-archive.css" rel="stylesheet">
	<link href="plugins/jquery-ui-1.9.2.custom/css/smoothness/jquery-ui-1.9.2.custom.css" rel="stylesheet"> 
	<link rel="stylesheet" href="plugins/chosen/chosen.css" />
	
	<script src="plugins/jquery-1.8.3.min.js"></script>
	<script type="text/javascript" src="plugins/jquery-ui-1.9.2.custom/js/jquery-ui-1.9.2.custom.min.js"></script>	
	<script type="text/javascript" src="plugins/dataTables-1.9.4/media/js/jquery.dataTables.min.js"></script>

    <title>Sakai-Spring</title>
</head>
<body>
<div class="portletBody">
</div>
<script  type="text/javascript">
$(document).ready(function() {
	$.ajax({
		url : 'course.json',
		data : 'courseId=' + "courseid",
		datatype : 'json',
		success : function(course) {
			$('.portletBody').append(course.title);
		},

		error : function(xhr, ajaxOptions, thrownError) {
//			$('#ajaxMessage').html(server_error_message);
//			$('#ajaxReturn').addClass("error");
		}
	});
});
</script>
</body>
</html>
