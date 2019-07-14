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
	href="${pageContext.request.contextPath}/css/scaffolding.less">
<link
	href="${pageContext.request.contextPath}/css/achievement/achievementMain.css"
	rel="stylesheet" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/course3.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/turnClass/overturn_threelevel.css">
<script src="${pageContext.request.contextPath}/js/jquery-3.2.1.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
</head>
<body>
	<style>
.a {
	width: 313px;
	height: 0;
	background-color: #f0f0f0;
	opacity: 0;
	transition: height 0.2s linear, opacity 0.2s linear;
	-webkit-transition: height 0.2s linear, opacity 0.2s linear;
	/* Safari */
	padding-left: 26px;
	padding-top: 18px;
}

.b {
	width: 187px;
	min-height: 77px;
	height: auto;
	position: relative;
}

.b:hover .a {
	height: 100px;
	opacity: 1;
}

.xiangmu-out .rel-img img {
	width: 100%;
	height: 100%;
	overflow: hidden;
	transition: all 0.5s;
}

.xiangmu-out:hover {
	box-shadow: 0 0 15px grey;
	transform: translate(0, -10px);
}

.xiangmu-out .rel-img {
	overflow: hidden;
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
		function ajaxStudent() {
			var taskId = $("#taskId").val();
			$
					.ajax({
						async : false,
						cache : false,
						url : "${pageContext.request.contextPath}/turnClass/getRealClassAllStudent/"
								+ taskId,
						type : "POST",
						dataType : "text",
						success : function(result) {
							var arr = eval(result);
							$("#studentContent").empty();
							for (var i = 0; i < arr.length; i++) {
								$("#studentContent")
										.append(
												"<label class='checkbox-inline' id="
														+ arr[i].id
														+ " style='width: 30%; margin-left: 2%; margin-top: 1%;'><input type='checkbox' name='unSelect' onclick='checkboxOnclick(this)'  value='"
														+ arr[i].id + "'>"
														+ arr[i].name
														+ "</label>");
							}
						}
					});
		}
		function checkboxOnclick(checkbox) {
			if (checkbox.checked == true) {
				var value = checkbox.value;
				$("input[name = 'select']").each(function() {
					if ($(this).val() == value)
						$("#" + value).remove();
				});
				$("input[name = 'unSelect']").each(function() {
					if ($(this).val() == value)
						$("#" + value).remove();
				});
				var content = "";
				$
						.ajax({
							async : false,
							cache : false,
							url : "${pageContext.request.contextPath}/turnClass/getNameForStudent",
							data : {
								'num' : value
							},
							type : "POST",
							dataType : "text",
							success : function(result) {
								content = result;
							}
						});
				$("#studentSelectedContent")
						.append(
								"<label class='checkbox-inline' id="
										+ value
										+ "  style='width: 30%; margin-left: 2%; margin-top: 1%;'><input name='select' type='checkbox' checked onclick='checkboxBackclick(this)'  value='"
										+ value + "'>" + content + "</label>");
			}
		}
		function checkboxBackclick(checkbox) {
			if (checkbox.checked == false) {
				var value = checkbox.value;
				$("input[name = 'select']").each(function() {
					if ($(this).val() == value)
						$("#" + value).remove();
				});
				$("input[name = 'unSelect']").each(function() {
					if ($(this).val() == value)
						$("#" + value).remove();
				});
				var content = "";
				$
						.ajax({
							async : false,
							cache : false,
							url : "${pageContext.request.contextPath}/turnClass/getNameForStudent",
							data : {
								'num' : value
							},
							type : "POST",
							dataType : "text",
							success : function(result) {
								content = result;
							}
						});
				$("#studentContent")
						.append(
								"<label class='checkbox-inline' id="
										+ value
										+ "  style='width: 30%; margin-left: 2%; margin-top: 1%;'><input  name='unSelect' onclick='checkboxOnclick(this)'  type='checkbox' value='"
										+ value + "'>" + content + "</label>");
			}
		}

		function teamButton() {
			var id = "";
			var judge = true;
			$("input[name = 'select']").each(function() {
				id = id + $(this).val() + ",";
			});
			$("#selectStudentId").val(id);
			var name = $("#projectName").val();
			if (name == "") {
				alert("项目名称未填写");
				judge = false;
			}
			var intro = $("#projectIntro").val();
			if (intro == "") {
				alert("项目简介未填写");
				judge = false;
			}
			if (id == "") {
				alert("成员未填写");
				judge = false;
			}
			if (judge) {
				$("#formContent").submit();
			}
		}
	</script>
	<jsp:include page="/jsp/top.jsp" flush="true" />
	<main>
	<div class="main_t">
		<div class="container-fluid">
			<c:if test="${ attention != 2 }">
				<button type="button" class="btn btn-default" id="attention"
					onclick="attention()">关注</button>
			</c:if>
			<c:if test="${ attention == 2 }">
				<button type="button" class="btn btn-default" id="attention"
					onclick="attention()">已关注</button>
			</c:if>
			<h1>${course.courseName}</h1>
			<c:if test="${course.fine != null && course.fine != '' }">
				<div
					style="color: red; position: relative; left: 10%; height: 50px; float: left;">
					<span>${course.fine }</span>
				</div>
			</c:if>
			<p>
				类别：${course.courseCategory}系&nbsp;&nbsp;&nbsp;
				参与人数：${course.courseStudentNum } &nbsp;&nbsp;&nbsp;创课时间：
				<fmt:formatDate value="${course.publishTime }" pattern="yyyy年MM月dd日" />
			</p>
		</div>
	</div>
	<div class="main_m row">
		<div class="title col-md-11">
			<h2>${task.taskTitle }</h2>
		</div>
		<div class="accept_task col-md-1">
			<c:if test="${student != null }">
				<button type="button" class="btn btn-primary" data-toggle="modal"
					data-target=".bs-example-modal-lg" onclick="ajaxStudent();">接收任务</button>
			</c:if>
		</div>
	</div>
	<div class="main_b_top row">
		<h3 class="col-md-12">任务描述</h3>
		<div class="details col-md-12">${task.taskDetail }</div>
		<br> <br> <br> <br> <br> <span>附件</span>
		<ul class="more">
			<c:forEach items="${task.accessoryList }" var="accessory">
				<li class="left"><img src="img/头像.png" /> <a
					href="${pageContext.request.contextPath}/teacher/resourceDownload?fileName=${accessory.accessoryName }&id=${task.taskId }">${accessory.accessoryName }</a>
				</li>
			</c:forEach>
		</ul>
		<div class="time col-md-4">
			<p>
				发布时间：
				<fmt:formatDate value="${task.publishTime }" pattern="yyyy年MM月dd日" />
			</p>
		</div>
	</div>
	<div class="main_b">
		<c:forEach items="${listTeam }" var="item" varStatus="status">
			<a
				href="${pageContext.request.contextPath}/turnClass/toTurnClassTeamDetail?taskId=${task.taskId}&teamId=${item.teamId}">
				<div class="main_b_group row" style="margin-left: 10%;">
					<div class="group_t">
						<div class="col-md-1"></div>
						<div class="group_name col-md-3">
							<h4>小组${status.index + 1}</h4>
						</div>
						<div class="group_name col-md-6">
							<h4>${item.projectName }</h4>
						</div>
					</div>
					<div class="group_m">
						<div class="people col-md-9">
							<div class="col-md-2">
								<div class="img-circle center-block">
									<img src="../img/turnClass/turn_class_picture.jpeg"
										style="width: 100%; height: 100%" alt="">
								</div>
								<p class="p1 text-center">${item.leaderId }&nbsp&nbsp&nbsp${item.leaderName
									}</p>
								<p class="text-center">组长</p>
							</div>
							<div class="col-md-1"></div>
							<c:forEach items="${item.listStu }" var="i">
								<div class="col-md-2">
									<c:if test="${empty i.faceImg }">
										<div class="img-circle center-block">
											<img src="../img/turnClass/turn_class_picture.jpeg"
												style="width: 100%; height: 100%" alt="">
										</div>
									</c:if>
									<c:if test="${not empty i.faceImg }">
										<div class="img-circle center-block">
											<img
												src="${pageContext.request.contextPath}/jsp/showImg.jsp?path=${i.faceImg }"
												style="width: 100%; height: 100%;" alt="" />
										</div>
									</c:if>
									<p class="text-center">${i.studentId }&nbsp&nbsp&nbsp${i.studentName}</p>
								</div>

							</c:forEach>
						</div>
						<div class="group_b col-md-12" style="margin-top: -2%">
							<p class="p2 col-md-3">
								接收任务时间：
								<fmt:formatDate value="${item.acceptTime}" pattern="yyyy年MM月dd日" />
							</p>
							<p class=" p1 col-md-2">评论：24条</p>
						</div>
					</div>
				</div>
			</a>
		</c:forEach>
	</div>
	<jsp:include page="/jsp/footer.jsp" flush="true" />
	<div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog"
		aria-labelledby="myLargeModalLabel">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content" style="padding: 2%">
				<form class="form-horizontal" class="form-horizontal"
					id="formContent"
					action="${pageContext.request.contextPath}/turnClass/addTaskTeam">
					<div class="form-group">
						<label for="inputEmail3" class="col-sm-2 control-label">项目名称</label>
						<div class="col-sm-10">
							<input class="form-control" id="projectName" name="projectName"
								placeholder="项目名称">
						</div>
					</div>
					<div class="form-group">
						<label for="inputEmail3" class="col-sm-2 control-label">项目简介</label>
						<div class="col-sm-10">
							<input class="form-control" id="projectIntro" name="projectIntro"
								placeholder="项目简介">
						</div>
					</div>
					<div class="form-group">
						<label for="inputEmail3" class="col-sm-2 control-label">项目负责人</label>
						<div class="col-sm-5">
							<input class="form-control" id="compere" name="compere"
								value="${student.studentName }" readonly="readonly">
						</div>
						<div class="col-sm-5">
							<input class="form-control" id="compereId" name="compereId"
								value="${student.studentId }" readonly="readonly">
						</div>
					</div>
					<div class="form-group">
						<label for="inputEmail3" class="col-sm-2 control-label"
							style="padding-right: 20px">选择成员</label>
						<div class="col-sm-10"
							style="padding-left: 0px; width: 80%; margin-left: 2%">
							<div class="panel panel-default studentSelete"
								id="studentSelectedContent"
								style="margin-top: 1%; height: 40px; overflow-y: auto"></div>
							<div class="panel panel-default studentContent"
								id="studentContent"
								style="margin-top: 1%; height: 80px; overflow-y: auto"></div>
						</div>
						<input name="selectStudentId" id="selectStudentId"
							style="display: none" /><input name="taskId" id="taskId"
							style="display: none" value="${task.taskId }" />
					</div>
					<div class="form-group">
						<div class="col-sm-offset-2 col-sm-10">
							<button type="button" onclick="teamButton()"
								class="btn btn-primary btn-block">提交</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</body>
</html>