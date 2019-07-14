<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="UTF-8" name="viewport"
	content="width=device-width, initial-scale=1,maximum-scale=1, user-scalable=no">
<title>${course. courseName}</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/course3.css">
<script
	src="${pageContext.request.contextPath}/js/backstagemanager/jquery-3.2.1.js"></script>
<script
	src="${pageContext.request.contextPath}/js/backstagemanager/bootstrap.js"></script>
<style type="text/css">
.description {
	display: -webkit-box;
	-webkit-box-orient: vertical;
	overflow: hidden;
	overflow-x: hidden;
	overflow-y: hidden;
	font-size: 15px;
	word-break: break-all;
	text-overflow: ellipsis;
	-webkit-line-clamp: 1;
	color: gray;
}

a {
	text-decoration: none;
	color: black;
}

a:hover {
	text-decoration: none;
}
</style>
<script type="text/javascript">
	function attention() {
		//alert('${course.courseId}');
		$
				.ajax({
					async : false,
					cache : false,
					url : "${pageContext.request.contextPath}/teacher/ajaxAttentionCourse",
					data : {
						'courseId' : '${course.courseId}'
					},
					type : "POST",
					dataType : "text",
					success : function(result) {
						alert(eval(result));
						if (eval(result) == "关注成功！") {
							$("#attention").html("已关注");
						} else {
							$("#attention").html("关注");
						}
					}
				});
	}
</script>
</head>
<body>
	<jsp:include page="/jsp/top.jsp" flush="true" />
	<main>
	<div style="width: 80%; margin-left: 10%">
		<div class="col-md-12" style="margin-top:1%;background-color: white;">
			<h3>课堂翻转</h3>
		</div>
		<div class="col-md-12" style="margin-top:1%">
			<c:forEach items="${taskList }" var="item">
				<div style="background-color: white;width: 32%;float:left;margin-left:1%;margin-top:1%;min-height:150px">
					<a
						href="${pageContext.request.contextPath}/turnClass/toTurnClassTeam?taskId=${item.taskId }">
						<div clas="col-md-12">
							<h3 class="text-center">
								<b>${item.taskTitle }</b>
							</h3>
							<p class="text-center">
								<c:forEach items="${item.classList }" var="i">
							${i.realClassNum }
						</c:forEach>
							</p>
							<p class="description">
								<b>${item.taskDetail }</b>
							</p>
							<div class="col-md-12" style="padding: 0px">
								<p class="pull-left">发布人：${item.publisherId }</p>
								<p class="pull-right">
									<fmt:formatDate value="${item.publishTime }"
										pattern="yyyy年MM月dd日" />
								</p>
							</div>
						</div>
					</a>
				</div>
			</c:forEach>
		</div>
	</div>
</body>
</html>