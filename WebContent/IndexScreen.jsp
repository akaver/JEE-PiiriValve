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
	<form method="post" action="">
		<div class="milHeading">Piirivalve - Anu Kuusmaa ja Andres Käver</div>
		<br>
		<div>
			<div class="withMargin">Vali tegevus:<br> <br> <a href="DBInitVC">Loo andmebaas
				ja lae testandmed</a><br> <a href="DBInitVC?deletelck=yes">Loo
				andmebaas ja lae testandmed (kill lock)</a><br> <br></div>

			<div class="milContent">
				Administratiivüksuse tüüp:<br> <select name="AdminUnitTypeID">
					<c:forEach var="entry" items="${formData.adminUnitTypeList}">
						<option value="${entry.adminUnitTypeID}">${entry.name}</option>
					</c:forEach>
				</select> <input type="submit" name="ViewAdminUnitType" value="Vaata/Muuda">
				<input type="submit" name="AddAdminUnitType" value="Lisa uus">
				<input type="submit" name="ReportAdminUnitType" value="Aruanne">
			</div>
			
			<div class="milContent">
				Administratiivüksus: <br> <select name="AdminUnitID">
					<c:forEach var="entry" items="${formData.adminUnitList}">
						<option value="${entry.adminUnitID}">${entry.name}</option>
					</c:forEach>
				</select> <input type="submit" name="ViewAdminUnit" value="Vaata/Muuda">
				<input type="submit" name="AddAdminUnit" value="Lisa uus"> <input
					type="submit" name="ReportAdminUnit" value="Aruanne">
			</div>
		</div>
	</form>
</body>
</html>