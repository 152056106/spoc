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
<script type="text/javascript" charset="utf-8">
	$(function() {
		var width = $("#content").width();
		var ue = UE.getEditor('detail', {
			initialFrameWidth : width,
			initialFrameHeight : 180,
			initialFrameMargin : 0,
		})
		changeSelect();
	})
</script>
<script type="text/javascript">
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

	function buttonSubmit() {
		var detail = $("#detail").html();
		var judge = true;
		if (detail == "" && judge) {
			alert("内容为空，请填写");
			judge = false;
		}
		if (judge) {
			$("#formContent").submit();
		}
	}

	function filesize(ele) {
		var size = (ele.files[0].size / 1024).toFixed(2);
		if (size >= 1024) {
			$(ele).val("");
			alert("文件大小超过1MB，重新编辑后上传");
		}
	}

	function changeSelect() {
		var authorId = $("#oldAuthorId").val();
		if (authorId != "") {
			$('#authorId').find("option[value=" + authorId + "]").attr(
					"selected", true);
		}
	}
</script>
</head>
<body style="background-color: white;">
	<c:if test="${empty feedBack }">
		<form id="formContent" method="post"
			action="${pageContext.request.contextPath}/turnClass/insertFeedBack"
			enctype="multipart/form-data">
			<div class="col-md-12" id="content" style="margin-top: 2%">
				<textarea id="detail" name="detail" type="text"></textarea>
			</div>
			<input id="oldAuthorId" style="display: none"> <input
				name="taskId" value="${task.taskId }" style="display: none">
			<input name="teamId" value="${team.teamId }" style="display: none">
			<div class="col-md-12" style="margin-top: 2%">
				<label for="inputEmail3" class="col-sm-1 control-label">上传视频</label>
				<input type="file" id="pptFile" name="pptFile" class="col-md-3"
					style="padding: 0px" onchange="filesize(this)">
				<div class="col-sm-3" style="padding-left: 0px;">
					<select class="form-control" id="authorId" name="authorId">
						<option value="${team.leaderId }">${team.leaderName }</option>
						<c:forEach items="${team.listStu }" var="l">
							<option value="${l.studentId }">${l.studentName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-md-12" style="padding: 0px; margin-top: 2%;">
					<div class="col-md-4 pull-right" style="margin-top: -1%">
						<div class="btn-group" role="group" aria-label="...">
							<button type="button" onclick="buttonSubmit()"
								class="btn btn-default">保存</button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</c:if>
	<c:if test="${not empty feedBack }">
		<form id="formContent" method="post"
			action="${pageContext.request.contextPath}/turnClass/insertFeedBack"
			enctype="multipart/form-data">
			<div class="col-md-12" id="content" style="margin-top: 2%">
				<textarea id="detail" name="detail" type="text">${feedBack.content }</textarea>
			</div>
			<input name="taskId" value="${task.taskId }" style="display: none">
			<input name="teamId" value="${team.teamId }" style="display: none">
			<input id="oldAuthorId" value="${feedBack.authorId }"
				style="display: none">
			<div class="col-md-12" style="margin-top: 2%">
				<label for="inputEmail3" class="col-sm-1 control-label"
					style="margin-top: 6px">视频文件</label>
				<div style="height: 40px; padding-left: 36px;">
					<a
						href="${pageContext.request.contextPath}/turnClass/resourceDownload?path=${feedBack.accessoryId }">
						<button class="btn btn-default" type="submit"
							style="border-radius: 20px; float: left;">文件</button>
					</a>
				</div>
				<!-- 				<input type="file" id="pptFile" name="pptFile" class="col-md-3"
					style="padding: 0px" onchange="filesize(this)"> -->
				<div class="col-sm-3" style="padding-left: 0px;">
					<select class="form-control" id="authorId" name="authorId">
						<option value="${team.leaderId }">${team.leaderName }</option>
						<c:forEach items="${team.listStu }" var="l">
							<option value="${l.studentId }">${l.studentName}</option>
						</c:forEach>
					</select>
				</div>
				<div class="col-md-12" style="padding: 0px; margin-top: 2%;">
					<p class="pull-left">最后修改时间：${feedBack.uploadTime }</p>
					<div class="col-md-4 pull-right" style="margin-top: -1%">
						<div class="btn-group" role="group" aria-label="...">
							<button type="button" onclick="buttonSubmit()"
								class="btn btn-default">保存</button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</c:if>
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