<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Haldusüksuse redaktor</title>
<link rel="stylesheet" href="./style.css" type="text/css">
<script type="text/javascript">
	function makeReload() {
		document.forms["AdminUnitForm"].submit();
	}
</script>
</head>
<body>
<form method="post" action="" name="AdminUnitForm" id="AdminUnitForm">
		<table width="800" >
			<tr>
				<td colspan="2"><h3>Haldusüksuse redaktor</h3></td>
			</tr>
			<tr>
				<td width="50%"><table width="100%">
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
						<tr>
							<td width="100px">Kood</td>
							<td><input name="AdminUnitCode" type="text" size="10"
								value="${formData.adminUnit.code}"></td>
						</tr>
						<tr>
							<td>Nimi</td>
							<td><input name="AdminUnitName" type="text" size="30"
								value="${formData.adminUnit.name}"></td>
						</tr>
						<tr>
							<td valign="top">Kommentaar</td>
							<td><textarea name="AdminUnitComment" cols="42"
									rows="10">${formData.adminUnit.comment}</textarea></td>
						</tr>
						<tr>
							<td valign="top">Liik</td>
							<td>
<%-- 								<div id="AdminUnitType">${formData.adminUnitType.name}</div> --%>
								<div>
									<select name="AdminUnitType_adminUnitTypeID" onchange="makeReload()">
										<c:forEach var="entry" items="${formData.adminUnitTypeList}">
										<c:set var="selected" value="" />
										<c:if
											test="${entry.adminUnitTypeID == formData.adminUnitType.adminUnitTypeID}">
											<c:set var="selected" value="selected=\"selected\"" />
										</c:if>
										<option value="${entry.adminUnitTypeID}" ${selected}>${entry.name}</option>
										</c:forEach>										
									</select>
								</div>
<!-- 								<div><input name="ChangeButton" type="submit" value="Change"></div> -->
							</td>
						</tr>
						<tr>
							<td>Allub</td>
							<td><select name="AdminUnitMaster_adminUnitID">
									<c:forEach var="entry"
										items="${formData.adminUnitMasterListWithZero}">
										<c:set var="selected" value="" />
										<c:if
											test="${entry.adminUnitID == formData.adminUnitMaster.adminUnitID}">
											<c:set var="selected" value="selected=\"selected\"" />
										</c:if>
										<c:if
											test="${entry.adminUnitID!=formData.adminUnit.adminUnitID}">
											<option value="${entry.adminUnitID}" ${selected}>${entry.name}</option>
										</c:if>
									</c:forEach>
							</select></td>
						</tr>
					</table></td>
				<td style="position: relative"><table width="100%"
						class="borderedTable">
						<tr>
							<td class="allBorders" bgcolor="#CCCCCC">Alluvad</td>
						</tr>

						<c:set var="counter" value="0" />
						<c:forEach var="entry"
							items="${formData.adminUnitsSubordinateList}">
							<tr>
								<td class="allBorders">
									<div>${entry.name}</div>
									<div>
										<input name="RemoveButton_${counter}" type="submit"
											value="Eemalda">
									</div>
								</td>
							</tr>
							<c:set var="counter" value="${counter+1}" />
						</c:forEach>
						<c:if
							test="${formData.adminUnitsSubordinateListPossible.size()!=0}">
							<tr>
								<td class="allBorders">
									<div>
										<c:set var="counter" value="0" />
										<select name="AdminUnit_NewSubordinateNo">
											<c:forEach var="entry"
												items="${formData.adminUnitsSubordinateListPossible}">
												<option value="${counter}" ${selected}>${entry.name}</option>
												<c:set var="counter" value="${counter+1}" />
											</c:forEach>
										</select>
									</div>
									<div>
										<input name="AddSubordinateButton" type="submit" value="Lisa">
									</div>
								</td>
							</tr>
						</c:if>
					</table></td>
			</tr>
			<tr>
				<td colspan="2" align="right"><input name="SubmitButton"
					type="submit" value="Salvesta"> <input name="CancelButton"
					type="submit" value="Loobu"></td>
			</tr>
		</table>
	</form>
</body>
</html>