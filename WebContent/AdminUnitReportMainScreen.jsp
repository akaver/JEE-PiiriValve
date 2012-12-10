<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" 
                                                  prefix="fn" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Haldusüksuste alluvusraport</title>
<link rel="stylesheet" href="./style.css" type="text/css">
<link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" />
<script type="text/javascript" src="./jquery-1.8.3.js"></script>
<script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>

<script type="text/javascript">
	function openInfo(adminUnitID) {
		$('#adminUnitID'+adminUnitID).dialog();
	}
</script>
</head>
<body>
<form method="post" action="" name="AdminUnitForm" id="AdminUnitForm">
		<table width="450" >
			<tr>
				<td colspan="2"><h3>Haldusüksuste alluvusraport</h3></td>
			</tr>
			<tr>
				<td>
					<c:if test="${not empty errors}">
							<tr>
								<td colspan="2">
									<div style="color: red">
										<c:forEach var="error" items="${errors}">
											<c:out value="${error}"></c:out>
											<br />
										</c:forEach>
									</div>
								</td>
							</tr>
					</c:if>
				</td>
			</tr>
			<tr>
				<td width="40%">Kuupäev</td>
				<td width="60%">Liik</td>
			</tr>
			<tr>
				<td><input name="SearchDate" type="text" size="30"
					value="${formData.searchDate}"></td>
				<td>
					<div>
						<select name="AdminUnitType_adminUnitTypeID">
							<c:forEach var="entry" items="${formData.adminUnitTypeList}">
								<c:set var="selected" value="" />
								<c:if test="${entry.adminUnitTypeID == formData.adminUnitType.adminUnitTypeID}">
									<c:set var="selected" value="selected=\"selected\""/>
								</c:if>
								<option value="${entry.adminUnitTypeID}" ${selected}>${entry.name}</option>
							</c:forEach>
						</select>
					</div>
					<div><input name="RefreshButton" type="submit" value="Värskenda"></div>
				</td>
			</tr>
			<c:forEach var="subordinationSet" items="${formData.adminUnitMasterList}">
				<tr>
					<td colspan="2">
						<table class="allBorders" width="100%">
							<tr><td class="allBorders" bgcolor="#CCCCCC">${subordinationSet.name}</td></tr>
							<c:forEach var="subordinate" items="${subordinationSet.adminUnitSubordinatesList}">
								<tr>
									<td class="allBorders">
										<div>${subordinate.name}</div>
										<div><button name="LookButton" type="button" onclick="openInfo(${subordinate.adminUnitID})">Vaata</button></div>
										<div style="display:none" id="adminUnitID${subordinate.adminUnitID}" title="${subordinate.name}">
											Nimi: ${subordinate.name}<br>
											Kood: ${subordinate.code}<br>
											Tüüp: ${subordinate.adminUnitTypeString}<br>
											Kuulub: ${subordinationSet.name}<br>
											<c:if test="${fn:length(subordinate.adminUnitSubordinatesList) > 0}">
											Alluvad:<br>
											</c:if>
											<c:forEach var="subsubordinate" items="${subordinate.adminUnitSubordinatesList}">
												- ${subsubordinate.name}<br>
											</c:forEach>
											<c:if test="${fn:length(subordinate.comment) > 0}">
												Kommentaar: ${subordinate.comment}												
											</c:if>
										</div>
									</td>
								</tr>
							</c:forEach>
							<c:if test="${fn:length(subordinationSet.adminUnitSubordinatesList) == 0 }">
								<tr>
									<td class="allBorders">
										<div>-</div>
									</td>
								</tr>
							</c:if>							
						</table>
					</td>
				</tr>
				<br>
			</c:forEach>
			<tr>
				<td colspan="2">
					<input name="BackButton" type="submit" value="Tagasi" style="float:right">
				</td>
			</tr>
		</table>
	</form>
</body>
</html>