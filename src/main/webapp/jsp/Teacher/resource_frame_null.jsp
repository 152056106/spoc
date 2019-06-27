<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html lang="zh-CN" style="padding-top: 25px;">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
<title></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/scaffolding.less">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/course3.css">
<script
	src="${pageContext.request.contextPath}/js/Admin/jquery-1.10.2.js"></script>
	<script
	src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script
	src="${pageContext.request.contextPath}/js/adminJs/bootstrap.min.js"></script>
	<script
	src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
</head>

<body>


<main>

<div class="main_b">
<div style="    width: 100%;
    height: 100%;
    text-align: center;
    font-size: 92px;
    color: #fff;">
	暂无资源
</div>
</div>
</main>
<footer></footer> 

</body>
</html>