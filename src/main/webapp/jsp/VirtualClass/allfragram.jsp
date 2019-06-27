<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title></title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/classInfo.css"/>
		<script src="${pageContext.request.contextPath}/js/jquery.min.js" type="text/javascript" charset="utf-8"></script>
		<script type="text/javascript">
		$(document).ready(function() {
			$("#statusBackcolor ul li").click(function() {
		        $(this).siblings('li').removeClass('active');  
		 
		        $(this).addClass('active');                          
		 
		    });
		});
			function reinitIframe() { 
				var iframe = document.getElementById("stateContent");
				 try 
				{ 
				var bHeight = iframe.contentWindow.document.body.scrollHeight; 
				/* 
				var dHeight = iframe.contentWindow.document.documentElement.scrollHeight;
				var height = Math.max(bHeight, dHeight); 
				iframe.height = height; */ 
				iframe.height = bHeight; 
				} 
				catch (ex) { } 
			}
			
		</script>
	</head>
	<body style="background-color: #F8F8F8;">
		
		<div
			style="width: 100%; height: 50px; margin: 0 auto; background-color: #fff;">
			<div style="width: 20px; height: 50px; background: #015293;float: left;"></div>
			<c:if test="${taskCategory  eq 'work'}">
			
			<span style="line-height: 55px;font-size: 18px;margin-left: 20px;font-weight: bold;">作业</span>
			</c:if>
			<c:if test="${taskCategory  eq 'course_design'}">
			
			<span style="line-height: 55px;font-size: 18px;margin-left: 20px;font-weight: bold;">课程设计</span>
			</c:if>
			<c:if test="${taskCategory  eq 'trial'}">
			
			<span style="line-height: 55px;font-size: 18px;margin-left: 20px;font-weight: bold;">实验</span>
			</c:if>
			<c:if test="${taskCategory  eq 'challenge'}">
			
			<span style="line-height: 55px;font-size: 18px;margin-left: 20px;font-weight: bold;">挑战</span>
			</c:if>
			<c:if test="${taskCategory  eq 'turn_class'}">
			
			<span style="line-height: 55px;font-size: 18px;margin-left: 20px;font-weight: bold;">翻转</span>
			</c:if>
			<c:if test="${taskCategory  eq 'all'}">
			
			<span style="line-height: 55px;font-size: 18px;margin-left: 20px;font-weight: bold;">全部</span>
			</c:if>
			
			
		</div>
		<div style="width: 100%;height: 15px;background-color: #F8F8F8"></div>
		<div id="statusBackcolor" class="state"  >
			<ul class="nav nav-tabs">
			  <li role="presentation" class="active"><a target="stateContent" href="${pageContext.request.contextPath}/teacher/toteacherTaskListContent/${taskCategory }/every">全部</a></li>
			  <li role="presentation"><a target="stateContent"  href="${pageContext.request.contextPath}/teacher/toteacherTaskListContent/${taskCategory }/ing">正在进行</a></li>
			  <li role="presentation"><a  target="stateContent" href="${pageContext.request.contextPath}/teacher/toteacherTaskListContent/${taskCategory }/ed">已结束</a></li>
			</ul>
		</div>
		<div class="statefiframe">
			<iframe onload="reinitIframe()" id="stateContent" name="stateContent" src="${pageContext.request.contextPath}/teacher/toteacherTaskListContent/${taskCategory }/every" width="900px" height="100%"  frameborder="no" border="0" scrolling="no"  >
				
			</iframe>
		</div>
	</body>
</html>
