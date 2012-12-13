<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>BorderGuard Main Screen</title>
<link rel="stylesheet" href="./style.css" type="text/css">
</head>
<body>
	<p>Here we go, main entry point into our borderguard webApp</p>
	<p>
		Choose your action:<br>
		<br>
		<a href="DBInitVC">Recreate database and load initial data</a><br>
		<a href="DBInitVC?deletelck=yes">Recreate database and load initial data (kill lock)</a><br>
		<br>
		<a href="AdminUnitTypeVC?AdminUnitTypeID=4">Administrative unit type redactor</a><br>
		<a href="AdminUnitTypeReportVC">Administrative unit type report</a><br>
		<br>
		<a href="AdminUnitVC?AdminUnitID=1">Administrative unit redactor</a><br>
		<a href="AdminUnitReportVC?AdminUnitTypeID=1">Administrative unit report</a><br>
	</p>
</body>
</html>