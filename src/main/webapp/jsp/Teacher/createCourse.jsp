<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta charset="UTF-8">
<link href="${pageContext.request.contextPath}/css/Admin/bootstrap.css"
	rel="stylesheet">
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery-3.2.1.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/ueditor.config.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/ueditor.all.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/zh-cn.js"></script>
<script
	src="${pageContext.request.contextPath}/js/Admin/bootstrap.min.js"></script>
<script>
	//浏览图片
	function chan(i) {
		var objUrl = getObjectURL(i.files[0]);
		if (objUrl) {
			$("#photos").attr("src", objUrl); //这里的id是要显示图片位置的Id
		}
	};
	//建立一個可存取到該file的url
	function getObjectURL(file) {
		var url = null;
		// 下面函数执行的效果是一样的，只是需要针对不同的浏览器执行不同的 js 函数而已
		if (window.createObjectURL != undefined) { // basic
			url = window.createObjectURL(file);
		} else if (window.URL != undefined) { // mozilla(firefox)
			url = window.URL.createObjectURL(file);
		} else if (window.webkitURL != undefined) { // webkit or chrome
			url = window.webkitURL.createObjectURL(file);
		}
		return url;
	} 
</script>

<script type="text/javascript">
	function OnInput() {
		var teacherNum = $("#inputSelectTeacher").val();
		var path = "";
		if (teacherNum == "") {
			path = "${pageContext.request.contextPath}/teacher/teacherForFuzzyQueryById/nullTeacherName";
		} else {
			path = "${pageContext.request.contextPath}/teacher/teacherForFuzzyQueryById/"
					+ teacherNum;
		}
		$
				.ajax({
					async : false,
					cache : false,
					url : "${pageContext.request.contextPath}/teacher/teacherForFuzzyQueryById/"
							+ teacherNum,
					type : "POST",
					dataType : "json",
					success : function(result) {
						var arr = eval(result);
						$("#teacherContent").empty();
						for (var i = 0; i < arr.length; i++) {
							$("#teacherContent")
									.append(
											"<label class='checkbox-inline' id="
													+ arr[i].employeeNum
													+ " style='width: 30%; margin-left: 2%; margin-top: 1%;'><input type='checkbox' name='unSelect' onclick='checkboxOnclick(this)'  value='"
													+ arr[i].employeeNum
													+ "'>"
													+ arr[i].teacherName
													+ "</label>");
						}
					}
				});
	}
</script>

<script type="text/javascript">
	function checkboxOnclick(checkbox) {
		if (checkbox.checked == true) {
			var value = checkbox.value;
			var content = $("#" + value).text();
			
			/* $
			.ajax({
				async : false,
				cache : false,
				url : "${pageContext.request.contextPath}/teacher/addTeacherTeam/"
						+ value,
				type : "POST",
				dataType : "json",
				success : function(result) {
					
				}
			}); */
			
			$("input[name = 'select']").each(function() {
				if ($(this).val() == value)
					$("#" + value).remove();
			});
			$("input[name = 'unSelect']").each(function() {
				if ($(this).val() == value)
					$("#" + value).remove();
			});
			$("#teacherSelectedContent")
					.append(
							"<label class='checkbox-inline' id="
									+ value
									+ "  style='width: 30%; margin-left: 2%; margin-top: 1%;'><input name='select' type='checkbox' checked onclick='checkboxBackclick(this)'  value='"
									+ value + "'>" +content + "</label>");
		}
	}
</script>

<script type="text/javascript">
	function checkboxBackclick(checkbox) {
		if (checkbox.checked == false) {
			var value = checkbox.value;
			
			/* $
			.ajax({
				async : false,
				cache : false,
				url : "${pageContext.request.contextPath}/teacher/addTeacherTeam/"
						+ value,
				type : "POST",
				dataType : "json",
				success : function(result) {
					
				}
			}); */
			
			var content = $("#" + value).text();
			$("input[name = 'select']").each(function() {
				if ($(this).val() == value)
					$("#" + value).remove();
			});
			$("input[name = 'unSelect']").each(function() {
				if ($(this).val() == value)
					$("#" + value).remove();
			});
			$("#teacherContent")
					.append(
							"<label class='checkbox-inline' id="
									+ value
									+ "  style='width: 30%; margin-left: 2%; margin-top: 1%;'><input  name='unSelect' onclick='checkboxOnclick(this)'  type='checkbox' value='"
									+ value + "'>" + content + "</label>");
		}
	}
</script>

<script type="text/javascript">
	function submitForm() {
		var selectClass = "";
		var flag = true;
		$("input[name = 'select']").each(function() {
			selectClass = selectClass + $(this).val() + ",";
			$("#selectTeacher").val(selectClass);
		});
		if ($("#courseName").val() == null || $("#courseName").val() == '') {
			alert("课程名不能为空！");
			flag = false;
		}
		if (!UE.getEditor('courseDetail').hasContents()) {
			alert("课程描述不能为空！");
			flag = false;
		}
		if (selectClass == null || selectClass == '') {
			alert("教师团队不能为空！");
			flag = false;
		}

		if (flag) {

			$("#formContent").submit();
		} else {
			return false;
		}
	};
	//进行Iframe的自动撑开,让所有父页面的Iframe都自动适应包含页高度 
	
</script>

<script type="text/javascript">

var ue = UE.getEditor('courseDetail',{

    initialFrameWidth :1151,//设置编辑器宽度

    initialFrameHeight:400,//设置编辑器高度
    initialFrameMargin:0,

    scaleEnabled:true
    

 });

</script>
</head>
<body >
 <jsp:include page="/jsp/top.jsp" flush="true"/>

	<div class="main" style="width: 80%; margin-left: 10%;padding-bottom: 10px;margin-bottom: 50px;">
		<form action="${pageContext.request.contextPath}/teacher/createCourse"
			id="formContent" method="post" enctype="multipart/form-data"
			onsubmit="return submitForm()">
			<input type="hidden" name="publisherId"
				value="${teacher.employeeNum }">
			<div class="form-group">
				<label class="col-sm-2 control-label text-right"
					style="margin-top: 1%">课程名称：</label>
				<div class="col-sm-10" style="padding-left: 0px; width: 75%">
					<input class="form-control" id="courseName" name="courseName"
						placeholder="课程名称">
				</div>
			</div>
			<br> <br>
			<div class="form-group" style="margin-top: 1%">
				<label class="col-sm-2 control-label text-right"
					style="margin-top: 1%">添加图片：</label>
				<ul class="col-sm-10 phoneUl"
					style="padding-left: 0px; width: 75%; margin: 0px; list-style: none">
					<label for="faceImg" style="cursor: pointer">
						<li><input type="file" id="faceImg" style="display: none;"
							onchange="chan(this)" name="faceImg"></li>
						<li><img id="photos"
							src="${pageContext.request.contextPath}/images/add.png"
							class="img-thumbnail" width="100" height="100" /></li>
					</label>
				</ul>
			</div>
			<br> <br>
			<div class="form-group">
				<label class="col-sm-2 control-label text-right"
					style="margin-top: 1%">课程名称：</label>
				<div class="col-sm-10" style="padding-left: 0px; width: 75%">
					<select class="form-control" id="courseCategory"
						name="courseCategory">
						<c:forEach items="${categoryList }" var="category">
							<option value="${category.categoryId }">${category.categoryName }</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<br> <br> <br> <br> <br>
			<div class="form-group">
				<label class="col-sm-2 control-label text-right"
					style="margin-top: 1%">课程介绍：</label>
				<div class="col-sm-10" style="padding-left: 0px; width: 75%">
					<textarea id="courseDetail" name="courseDetail" type="text"
						placeholder="" style="margin: 0"></textarea>
				</div>
			</div>
			<br>
			<div class="form-group">
				<label for="inputEmail3" class="col-sm-2 control-label text-right"
					style="margin-top: 2%">选择老师：</label>
				<div class="col-sm-10" style="padding-left: 0px; width: 75%">
					<div class="panel panel-default teacherSelectedContent"
						id="teacherSelectedContent"
						style="margin-top: 1%; height: 40px; overflow-y: auto; margin-bottom: 1%"></div>
					<input class="form-control" oninput="OnInput (event)"
						id="inputSelectTeacher" name="inputSelectTeacher"
						placeholder="筛选老师">
					<div class="panel panel-default teacherContent" id="teacherContent"
						style="margin-top: 1%; height: 80px; overflow-y: auto">
						<c:forEach items="${teacherList }" var="item">
							<label class="checkbox-inline" id="${item.employeeNum }"
								style="width: 30%; margin-left: 2%; margin-top: 1%;"> <input
								name="unSelect" type="checkbox" value="${item.employeeNum }"
								onclick="checkboxOnclick(this)"> ${item.teacherName }
							</label>
						</c:forEach>
					</div>
					<button type="submit" class="btn btn-primary btn-block">提交</button>
				</div>
				<input name="selectTeacher" id="selectTeacher" style="display: none" />
			</div>
			<%-- <div class="input1">
					<span>课程名称：</span> <input name="courseName" type="text"
						placeholder="请输入课程名称："
						style="width: 60%; height: 30px; float: left; margin-left: 2%;">
				</div>
				<div class="input1">
					<ul class="phoneUl">
					<span style="float: left;" >添加图片：</span> 
					<label for="faceImg" style="cursor: pointer;margin-left:2%">
						<li>
							<input type="file" id="faceImg" style="display: none;" onchange="chan(this)"
								name="faceImg">
						</li>
						<li><img id="photos" src="${pageContext.request.contextPath}/images/add.png" width="100" height="100" border="1px solid red;"/></li>
					</label>
					</ul>
				</div> --%>
			<%-- <div class="input3">
					<span>课程分类：</span> <select name="courseCategory" id="category">
						<c:forEach items="${categoryList }" var="category">
							<option value="${category.categoryId }">${category.categoryName }</option>
						</c:forEach>
					</select>
				</div> --%>
			<%-- 

				<hr>
				<div class="input1">
					<span>课程介绍：</span>
					<textarea id="courseDetail" name="courseDetail" type="text" placeholder=""
						style="width:700px; height: 200px; float: left; margin-left: 9%;" ></textarea>
				</div>
				
		
				
				<!-- 拟态框star -->
					<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" data-backdrop="static">
	  					<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="search_teacher" style="height: 50px;">
									<div class="form-group">
									    <input type="text" class="form-control" id="employeeNum" placeholder="教师工号" style="width: 174px;
    											margin-right: 18px;margin-left: 16px;float: left;">
									 </div>
									 <button id="search" type="button" class="btn btn-default">搜索</button>
								</div>
								<div style="height: auto; width: auto;min-height: 42px;">
									<ul id="teacherul" style="list-style-type:none;margin-left: 17px;min-height: 15px;height: auto;">
									<!-- <li id="teacher"></li> -->
									
								</ul>
								</div>
								<div class="back_up" style="height: auto; width: auto;min-height: 42px;">
									<input type="hidden" class="back_up_input" /> 
									<ul id="back_up_ul" style="list-style-type:none;margin-left: 17px;min-height: 15px;height: auto;">
								</ul>
									
								</div>
								<div style="clear:both"></div>
								<div class="modal-footer">
									<button id="close" type="button" class="btn btn-default"
										data-dismiss="modal">关闭</button>
									<button type="button" id="confirm" class="btn btn-primary" data-dismiss="modal">确定</button>
								</div>
							</div>
						</div>
					</div>
				<!-- 拟态框end -->
				
				<div class="teacher-friend">
					<h3>教师圈：</h3>
					<div class="friend">
						<ul id="selectedRealClassUI" style="list-style-type: none;">
							<li id="selectedTeachers" name=selectedTeachers
								style="float: left; margin-left: 2%; width: 30%;">
									<span>教师:</span>
									<input value="${teacher.employeeNum }" name="selectedTeacherContent" style='width: 50%;' id="selectedTeacherContent"/>
								</li>
						</ul>
						<div class="add">
							<button type="button" id="pull" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal" data-whatever="@mdo">拉入教师</button>
						</div>
						<input value="" name="teacherContent" id="teacherContent"
							type="hidden" />
					</div>
				</div>
				<hr>
				<div class="confirm_close">
					<ul>
						<li><input id="create" class="btn btn-default" style="width: 84px;" type="submit" value="创建" /></li>
						<li><input class="btn btn-default" style="width: 84px;" value="取消" /></li>
					</ul>
				</div> --%>
		</form>
	</div>

	<%-- <jsp:include page="/jsp/footer.jsp" flush="true"/> --%>

	<!-- bootstrup -->
<jsp:include page="/jsp/footer2.jsp" flush="true"/>
</body>

</html>
