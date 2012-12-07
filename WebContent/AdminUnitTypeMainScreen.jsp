<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Admin Unit Type Editor</title>
<link rel="stylesheet" href="./style.css" type="text/css">
</head>
<body>
	<form method="post" action="" name="AdminUnitTypeForm">
		<table width="800" border="0" cellspacing="0" cellpadding="2">
			<tr>
				<td colspan="2"><h3>Admin Unit Type Editor</h3></td>
			</tr>
			<tr>
				<td width="50%"><table width="100%">
						<tr>
							<td width="100px">Code</td>
							<td><input name="AdminUnitTypeCode" type="text" size="10"
								value="${formData.adminUnitType.code}"></td>
						</tr>
						<tr>
							<td>Name</td>
							<td><input name="AdminUnitTypeName" type="text" size="30"
								value="${formData.adminUnitType.name}"></td>
						</tr>
						<tr>
							<td valign="top">Comment</td>
							<td><textarea name="AdminUnitTypeComment" cols="35"
									rows="10">${formData.adminUnitType.comment}</textarea></td>
						</tr>
						<tr>
							<td>Subordinate of</td>
							<td><select name="AdminUnitTypeMaster_adminUnitTypeID">
									<c:forEach var="entry"
										items="${formData.adminUnitTypeMasterListWithZero}">
										<c:set var="selected" value="" />
										<c:if
											test="${entry.adminUnitTypeID == formData.adminUnitTypeMaster.adminUnitTypeID}">
											<c:set var="selected" value="selected=\"selected\"" />
										</c:if>
										<option value="${entry.adminUnitTypeID}" ${selected}>${entry.name}</option>
									</c:forEach>
							</select></td>
						</tr>
					</table></td>
				<td style="position: relative"><table width="100%" class="borderedTable">
						<tr>
							<td class="allBorders" bgcolor="#CCCCCC">Subordinates</td>
						</tr>

						<c:set var="counter" value="0" />
						<c:forEach var="entry"
							items="${formData.adminUnitTypesSubordinateList}">
							<tr>
								<td class="allBorders">
									<div>${entry.name}</div>
									<div>
										<input name="RemoveButton_${counter}" type="submit"
											value="Remove">
									</div>
								</td>
							</tr>
							<c:set var="counter" value="${counter+1}" />
						</c:forEach>
						<tr>
							<td class="allBorders">
								<div>
									<c:set var="counter" value="0" />
									<select name="AdminUnitType_NewSubordinateNo">
										<c:forEach var="entry"
											items="${formData.adminUnitTypesSubordinateListPossible}">
											<option value="${counter}" ${selected}>${entry.name}</option>
											<c:set var="counter" value="${counter+1}" />
										</c:forEach>
									</select>
								</div>
								<div>
									<input name="AddSubordinateButton" type="submit" value="Add">
								</div>
							</td>
						</tr>
					</table></td>
			</tr>
			<tr>
				<td colspan="2" align="right"><input name="SubmitButton"
					type="submit" value="Submit"> <input name="CancelButton"
					type="submit" value="Cancel"></td>
			</tr>
		</table>
	</form>
</body>
</html>