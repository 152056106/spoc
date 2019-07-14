<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
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
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/ueditor.config.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/ueditor.all.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/zh-cn.js"></script>
<script
	src="${pageContext.request.contextPath}/js/backstagemanager/bootstrap.js"></script>
<style type="text/css">
.cit {
	width: 100px;
	height: 100px;
	background-color: #CC9933;
	border-radius: 50px;
	text-align: center;
}
</style>
<script type="text/javascript" charset="utf-8">
	$(function() {
		var width = $("#content").width();
		var ue = UE.getEditor('detail', {
			initialFrameWidth : width,
			initialFrameHeight : 180,
			initialFrameMargin : 0,
		})
	})
</script>
<script type="text/javascript">
	var nowStage = "";//现在所处步骤
	var complishStage = "";//完成的步骤
	function focousCommentContent() {
		$.ajax({
			async : true,
			cache : false,
			url : "${pageContext.request.contextPath}/achievement/loginJudge",
			type : "get",
			dataType : "text",
			success : function(result) {
				if (result.length != 0 && result != "null") {
					alert(result);
				}
			}
		})
	};
	$(function() {
		ll();
	})
	function ll() {
		var teamId = $("#teamId").val();
		var taskId = $("#taskId").val();
		var i;
		$
				.ajax({
					async : false,
					cache : false,
					url : "${pageContext.request.contextPath}/turnClass/statusCheckout?teamId="
							+ teamId + "&taskId=" + taskId,
					type : "get",
					dataType : "text",
					success : function(result) {
						complishStage = result;
						if ("null" == complishStage) {
							i = 0;
							nowStage = "planStage";
						}
						if ("planStage" == complishStage) {
							i = 1;
							nowStage = "demandStage";
						}
						if ("demandStage" == complishStage) {
							i = 2;
							nowStage = "designStage";
						}
						if ("designStage" == complishStage) {
							i = 3;
							nowStage = "arithmeticStage";
						}
						if ("arithmeticStage" == complishStage) {
							i = 4;
							nowStage = "testStage";
						}
						if ("all" == complishStage) {
							changeInfo("testStage");
							nowStage = "testStage";
							$("#submitBut").attr("disabled", true);
						}
						i = i + 1;
						$("#" + i).css("background-color", "#2B7BD1");
					}
				})
	}

	function changeInfo(category) {
		var teamId = $("#teamId").val();
		var taskId = $("#taskId").val();
		$
				.ajax({
					async : false,
					cache : false,
					url : "${pageContext.request.contextPath}/turnClass/getInfoForTeamDesignBeforeClass?teamId="
							+ teamId
							+ "&taskId="
							+ taskId
							+ "&category="
							+ category,
					type : "get",
					dataType : "json",
					success : function(result) {
						if (result.judge == "notNull") {
							var content = result.content;
							var time = result.time;
							var authorId = result.leader;
							time = "最后修改时间：" + time;
							$("#uploadTime").html(time);
							$('#authorId').find(
									"option[value=" + authorId + "]").attr(
									"selected", true);
							var ue = UE.getEditor('detail');
							ue.ready(function() {
								ue.setContent(content);
							})
						}
						if (result.judge == "null") {
							$("#uploadTime").html("");
							var ue = UE.getEditor('detail');
							ue.ready(function() {
								ue.setContent("");
							})
						}
					}
				})
	}
	function submitButton() {
		var path = "${pageContext.request.contextPath}/turnClass/insetPrepareForClass/"
				+ nowStage;
		$("#formContent").attr("action", path);
		$("#formContent").submit();
	}
	function nextStep() {
		if ("planStage" == nowStage) {
			$("#2").css("background-color", "#2B7BD1");
			$("#1").css("background-color", "#CC9933");
			nowStage = "demandStage";
			changeInfo(nowStage);
			return 0;
		}
		if ("demandStage" == nowStage) {
			$("#3").css("background-color", "#2B7BD1");
			$("#2").css("background-color", "#CC9933");
			nowStage = "designStage";
			changeInfo(nowStage);
			return 0;
		}
		if ("designStage" == nowStage) {
			$("#4").css("background-color", "#2B7BD1");
			$("#3").css("background-color", "#CC9933");
			nowStage = "arithmeticStage";
			changeInfo(nowStage);
			return 0;
		}
		if ("arithmeticStage" == nowStage) {
			$("#5").css("background-color", "#2B7BD1");
			$("#4").css("background-color", "#CC9933");
			nowStage = "testStage";
			changeInfo(nowStage);
			return 0;
		}
		if ("testStage" == nowStage) {
			alert("已经是最后一步");
			return 0;
		}
	}
	function priviousStep() {
		if ("planStage" == nowStage) {
			alert("已经是第一步");
			return 0;
		}
		if ("demandStage" == nowStage) {
			$("#2").css("background-color", "#CC9933");
			$("#1").css("background-color", "#2B7BD1");
			nowStage = "planStage";
			changeInfo(nowStage);
			return 0;
		}
		if ("designStage" == nowStage) {
			$("#3").css("background-color", "#CC9933");
			$("#2").css("background-color", "#2B7BD1");
			nowStage = "demandStage";
			changeInfo(nowStage);
			return 0;
		}
		if ("arithmeticStage" == nowStage) {
			$("#4").css("background-color", "#CC9933");
			$("#3").css("background-color", "#2B7BD1");
			nowStage = "designStage";
			changeInfo(nowStage);
			return 0;
		}
		if ("testStage" == nowStage) {
			$("#5").css("background-color", "#CC9933");
			$("#4").css("background-color", "#2B7BD1");
			nowStage = "arithmeticStage";
			changeInfo(nowStage);
			return 0;
		}
	}
</script>
</head>
<body style="background-color: white;">
	<div class="col-md-12">
		<div class="col-md-1"></div>
		<div class="col-md-2">
			<div class="cit" id="1">
				<h3 style="padding-top: 37%">计划</h3>
			</div>
		</div>
		<div class="col-md-2">
			<div class="cit" id="2">
				<h3 style="padding-top: 37%">需求</h3>
			</div>
		</div>
		<div class="col-md-2">
			<div class="cit" id="3">
				<h3 style="padding-top: 37%">设计方案</h3>
			</div>
		</div>
		<div class="col-md-2">
			<div class="cit" id="4">
				<h3 style="padding-top: 37%">关键算法</h3>
			</div>
		</div>
		<div class="col-md-2">
			<div class="cit" id="5">
				<h3 style="padding-top: 37%">测试情况</h3>
			</div>
		</div>
		<div
			style="height: 5px; width: 70%; margin-left: 10%; margin-top: 6%; background-color: #0099CC;"></div>
	</div>
	<form id="formContent" action="" enctype="multipart/form-data">
		<input name="taskId" id="taskId" value="${task.taskId }"
			style="display: none"> <input name="teamId" id="teamId"
			value="${team.teamId }" style="display: none">
		<div class="col-md-12" id="content" style="margin-top: 2%">
			<textarea id="detail" name="detail" type="text"></textarea>
		</div>
		<div class="col-md-12" style="margin-top: 2%">
			<div class="col-sm-3" style="padding-left: 0px;">
				<select class="form-control" id="authorId" name="authorId">
					<option value="${team.leaderId }">${team.leaderName }</option>
					<c:forEach items="${team.listStu }" var="l">
						<option value="${l.studentId }">${l.studentName}</option>
					</c:forEach>
				</select>
			</div>
			<div class="col-md-12" style="padding: 0px; margin-top: 2%;">
				<p class="pull-left" id="uploadTime"></p>
				<div class="col-md-4 pull-right" style="margin-top: -1%">
					<div class="btn-group" role="group" aria-label="...">
						<button type="button" class="btn btn-default"
							onclick="priviousStep()">上一步</button>
						<button type="button" id="submitBut" onclick="submitButton()"
							class="btn btn-default">保存</button>
						<button type="button" class="btn btn-success" onclick="nextStep()">下一步</button>
					</div>
				</div>
			</div>
		</div>
	</form>
	<div class="col-md-12" style="height: 12px; background-color: #F0F0F0"></div>
	<div class="col-md-12" style="padding: 2%">
		<h3 style="margin-top: -1%; margin-left: -1%">学生评论</h3>
		<div id="stuCommentContentList" style="margin-top: 2%">
			<c:if test="${not empty commentListStu }">
				<c:forEach items="${commentListStu }" var="item" varStatus="states">
					<div class="col-md-12 panel panel-default" style="padding: 2%">
						<div class="col-md-3 text-center" style="">
							<img style="width: 80px; height: 80px;"
								src="${pageContext.request.contextPath}/jsp/showImg.jsp?path=${item.authorPicture }"
								alt="..." class="img-circle"> <br>
							<div class="col-md-12 " style="color: #B9B9B9; margin-top: 5%">${item.authorName }</div>
						</div>
						<div class="col-md-9"
							style="font-size: 12px; letter-spacing: 1px; line-height: 23px;">
							<div style="width: 105%; height: 100%">
								<span
									style="word-wrap: break-word; word-break: break-all; overflow: hidden;">${item.commentContent }</span>
							</div>
						</div>
						<p
							style="margin: 0px; margin-top: 12%; font-size: 10px; color: #B9B9B9;"
							class="text-right">${item.uploadTime }</p>
					</div>
				</c:forEach>
			</c:if>
		</div>
		<h4>我的评论</h4>
		<div class="publishComment col-md-12 "
			style="padding: 0px; margin: 0px;">
			<div class="col-md-12"
				style="font-size: 12px; letter-spacing: 1px; padding: 0px; margin: 0px; line-height: 23px;">
				<textarea class="form-control" id="addCommentContentStu" rows="5"
					name="addCommentContentStu" style="resize: none; height: 30%"
					placeholder="添加评论" onclick="focousCommentContent();"></textarea>
				<input type="text" style="display: none" id="IURPID" name="IURPID"
					value="${IURP.projectId }">
				<button class="btn btn-default btn-sm active pull-right"
					type="button" onclick="submitCommentButStu();"
					style="position: relative; left: -20px; top: -40px;">发表</button>
			</div>
		</div>
	</div>
</body>
</html>