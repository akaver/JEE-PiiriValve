<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Admin Unit Type Editor</title>
</head>
<body>
	<form method="post">
		<table width="800" border="0" cellspacing="0" cellpadding="2">
			<tr>
				<td colspan="2">Admin Unit Type Editor</td>
			</tr>
			<tr>
				<td width="50%"><table width="100%" border="0" cellspacing="0"
						cellpadding="4">
						<tr>
							<td width="100px">Code</td>
							<td><input name="AdminUnitCode" type="text" size="10" value="${formData.adminUnitType.code}"></td>
						</tr>
						<tr>
							<td>Name</td>
							<td><input name="AdminUnitName" type="text" size="30" value="${formData.adminUnitType.name}"></td>
						</tr>
						<tr>
							<td valign="top">Comment</td>
							<td><textarea name="AdminUnitTypeComment" cols="35"
									rows="10">${formData.adminUnitType.comment}</textarea></td>
						</tr>
						<tr>
							<td>Subordinate of</td>
							<td><select name="MasterAdminUnitType">
									<option value="1">State</option>
									<option value="2" selected>County</option>
							</select></td>
						</tr>
					</table></td>
				<td><table width="100%" border="0" cellspacing="0"
						cellpadding="4">
						<tr>
							<td colspan="2" bgcolor="#CCCCCC">Subordinates</td>
						</tr>
						<tr>
							<td align="left">village1</td>
							<td align="right"><input name="RemoveButton_1" type="submit"
								value="Remove"></td>
						</tr>
						<tr>
							<td align="left">village2</td>
							<td align="right"><input name="RemoveButton_2" type="submit"
								value="Remove"></td>
						</tr>
						<tr>
							<td colspan="2" align="right"><input
								name="AddSubordinateButton" type="submit" value="Add"></td>
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