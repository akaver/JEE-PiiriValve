<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Haldusüksuste tüüpide alluvusraport</title>
<link rel="stylesheet" href="./style.css" type="text/css">
<link rel="stylesheet" href="./jquery.treeview.css" />
<link rel="stylesheet"
	href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/themes/base/jquery-ui.css" />
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
<script
	src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min.js"></script>
<script src="./jquery.cookie.js" type="text/javascript"></script>
<script src="./jquery.treeview.js" type="text/javascript"></script>
<script src="./jquery.treeview.async.js" type="text/javascript"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#black").treeview({
			url : "AdminUnitTypeTreeViewVC"
		})
	});
</script>
</head>
<body>
	<div id="main">
		<p>Haldusüksuste tüüpide aruanne</p>
		<p>
			<a href="IndexVC">Tagasi</a>
		</p>
		<hr>
		<!-- here is the magic -->
		<ul id="black">
		</ul>
	</div>
</body>
</html>