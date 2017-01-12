<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>  
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>user-center</title>
</head>
<body>
	user-center登陆页面
	<form action="/user-center/login" method="POST">
		用户名：<input name="userName" value="admin">
		密码：<input name="password" value="admin">
		<input type="hidden" name="oldUrl" value="${oldUrl}">
		<button type="submit">登陆</button>
	</form>
</body>
</html>