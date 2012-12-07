<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>BorderGuard Main Screen</title>
</head>
<body>
	<p>Here we go, main entry point into our borderguard webApp</p>
	<p>
		Choose your action:<br>
		<br>
		<a href="DBInit">Recreate database and load initial data</a><br>
		<a href="DBInit?deletelck=yes">Recreate database and load initial data (kill lock)</a><br>
		<br>
		<a href="AdminUnitType?AdminUnitTypeID=4">Administrative unit type redactor</a><br>
		<a href="AdminUnit?AdminUnitID=1">Administrative unit redactor</a><br>
		<a href="AdminUnitReport">Administrative unit report</a><br>
		<a href="ArmyUnit">Army unit redactor</a><br>
	</p>
</body>
</html>