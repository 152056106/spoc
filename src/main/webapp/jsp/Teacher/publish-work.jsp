<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html ng-app="app">
<head>
<meta charset="utf-8">
<title></title>
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/bootstrap.min.css" />
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/fileinput.min.css" />
<link rel="stylesheet" type="text/css"
	href="https://use.fontawesome.com/releases/v5.5.0/css/all.css"
	crossorigin="anonymous">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/teacher/wui.min.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/teacher/styletime.css">
<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/css/bootstrap-table.min.css">
<script src="${pageContext.request.contextPath}/js/jquery.min.js"
	type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.js"
	type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap-table.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${pageContext.request.contextPath}/js/bootstrap-table-zh-CN.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${pageContext.request.contextPath}/js/plugins/piexif.min.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${pageContext.request.contextPath}/js/plugins/sortable.min.js"
	type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/js/fileinput.min.js"
	type="text/javascript" charset="utf-8"></script>
<script src="${pageContext.request.contextPath}/themes/fas/theme.min.js"
	type="text/javascript" charset="utf-8"></script>
<script
	src="${pageContext.request.contextPath}/themes/explorer-fa/theme.min.js"
	type="text/javascript" charset="utf-8"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/angular.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/wui-date.js" charset="utf-8"></script>

	<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/iframeResizer.contentWindow.min.js" charset="utf-8"></script>
	<script type="text/javascript"
	src="${pageContext.request.contextPath}/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
	<script type="text/javascript"
	src="${pageContext.request.contextPath}/My97DatePicker/calendar.js" charset="utf-8"></script>

<script type="text/javascript">
	
	var $table;
	var $tasktype;
	$(document).ready(function() {
		
		$tasktype=$("#taskCategorySelect").val();
		$("#selectTypeSelect li").click(function() {
			var text =$.trim( $(this).html());
		
			if(text=="作业"){
				$("#typeNameSelect").html("作业");
				$("#taskCategorySelect").val("work");
				$("#preSelectTime").css("display","none");
			}
			if(text=="实验"){
				$("#typeNameSelect").html("实验");
				$("#taskCategorySelect").val("trial");
				$("#preSelectTime").css("display","none");
			}
			if(text=="课程设计"){
				$("#typeNameSelect").html("课程设计");
				$("#taskCategorySelect").val("course_design");
				$("#preSelectTime").css("display","none");
			}
			if(text=="翻转"){
				$("#typeNameSelect").html("翻转");
				$("#taskCategorySelect").val("turn_class");
				$("#preSelectTime").css("display","block");
				
			}
		
			$tasktype=$("#taskCategorySelect").val();
			$('#taskListTable').bootstrapTable(
					'refresh', 
					{url : "${pageContext.request.contextPath}/teacher/getTaskListPage?taskCategory="+$tasktype
					});
		});
		$("#selectTypeEdit li").click(function() {
			var text =$.trim( $(this).html());
			if(text=="作业"){
				$("#typeNameEdit").html("作业");
				$("#taskCategoryEdit").val("work");
				$("#preEditTime").css("display","none");
			}
			if(text=="实验"){
				$("#typeNameEdit").html("实验");
				$("#taskCategoryEdit").val("trial");
				$("#preEditTime").css("display","none");
			}
			if(text=="课程设计"){
				$("#typeNameEdit").html("课程设计");
				$("#taskCategoryEdit").val("course_design");
				$("#preEditTime").css("display","none");
			}
			if(text=="翻转"){
				$("#typeNameEdit").html("翻转");
				$("#taskCategoryEdit").val("turn_class");
				$("#preEditTime").css("display","block");
				
			}
			
			
			$tasktype=$("#taskCategoryEdit").val();
			
		});
		
		$("#editTime .wui-date-editor input").attr("name", "taskEndTime");
		$("#editTime .wui-date-editor input").attr("id", "taskEndTimeEdit");
		$("#selectTime .wui-date-editor input").attr("name", "taskEndTime");
		$("#selectTime .wui-date-editor input").attr("id", "taskEndTimeSelect");
		
		
		
		$table = $('#taskListTable').bootstrapTable({
			method : 'get',
			url : "${pageContext.request.contextPath}/teacher/getTaskListPage?taskCategory="+$tasktype,//请求路径
			dataType : "json",
			striped : true, //是否显示行间隔色
			pageNumber : 1, //初始化加载第一页
			pagination : true,//是否分页
			striped: true,  //是否显示行间隔色
			sidePagination : 'client',//server:服务器端分页|client：前端分页
			pageSize : 5,//单页记录数
			pageList : [ 5, 10, 20, 30 ],//可选择单页记录数
			showRefresh : false,//刷新按钮
			clickToSelect: true,                //是否启用点击选中行
			singleSelect  : true,
			queryParams : function(params) {//上传服务器的参数
				var temp = {//如果是在服务器端实现分页，limit、offset这两个参数是必须的
					limit : params.limit, // 每页显示数量
					offset : params.offset // SQL语句起始索引
					//page : (params.offset / params.limit) + 1, //当前页码 
					/* Name : $('#search_name').val(),
					Tel : $('#search_tel').val() */
				};
				return temp;
			},
			columns : [ {
                checkbox: true,
                visible: true  
            },{
				field : 'taskId',
				title : '任务Id',
				visible: false 
			}, {
				field : 'taskTitle',
				title : '任务标题',

			}, {
				field : 'knowledgePoints',
				title : '任务知识点',

			}, {
				field : 'publisherId',
				title : '发布者',

			}, {
				field : 'taskType',
				title : '分类',

			},/*  {
				field : 'courseId',
				title : '课程',
				visible: false
			}, */ {
				field : 'useNum',
				title : '使用次数'

			},{
				field : 'operate',
				title : '预览',
				formatter: btnGroup,    // 自定义方法，添加按钮组
				events: {               // 注册按钮组事件
		            'click #preview': function (event, value, row, index) {
		               $.ajax({
							async : false,
							cache : false,
							url : "${pageContext.request.contextPath}/teacher/ajaxGetTaskPerview?taskId="+row.taskId,
							type : "POST",
							dataType : "json",
							success : function(result) {
								var arr = result;
								console.log(result);
								
								var accessoryList = result[0].accessoryList;
								$("#autoCreateMoTai").empty();
								$("#autoCreateMoTai").append(

										"<div class='workcontent' style='overflow:hidden'>"+
											"<div style='height: 30px;'>"+
									"<div style='float: left;text-align: center;width:100%;margin-top: 10px;'>"+
										"<span style='font-size:18px;font-weight: bold;'>任务预览</span>"+
									"</div>"+
									"<button type='button' style='margin-top: -20px;margin-right: 20px;' class='close' data-dismiss='modal' aria-label='Close'><span aria-hidden='true'>&times;</span></button>"+
								"</div>"+
								"<hr>"+
								"<div style='height: 70px;padding:20px;margin-top: -30px;'>"+
								"<div style='float: left;'><h4 style='font-weight: bold;'>任务要求</h4></div>"+
							"</div>"+
								"<div style='text-align:left ;padding-left:36px'>"+
									"<span style='word-wrap:break-word; word-break:break-all; overflow: hidden; '>"+result[0].taskDetail+"</span>"+
								"</div>"+
								"<hr>"+
								"<div style='height: 70px;padding:20px;margin-top: -30px;'>"+
								"<div style='float: left;'><h4 style='font-weight: bold;'>附件</h4></div>"+
							"</div>"+
								"<div style='height: 90px;margin-top: 20px;margin-top: -48px;'>"+
									"<div id='autoAccessoryList' class='accessorylist'>"+
									 
									"</div>"+
								"</div>"+
							"</div>"
										
								);
								for(var i=0;i<accessoryList.length;i++){
									$("#autoAccessoryList").append(
					                "<a href='${pageContext.request.contextPath}/teacher/resourceDownload?fileName="+accessoryList[i].accessoryName+"&id="+result[0].taskId+"&type=teacherPub'>"+
										"<button class='btn btn-default' type='submit' style='border-radius: 20px;float: left;margin-left: 10px;' >"+accessoryList[i].accessoryName+"</button>"+
									"</a>");
					           }
								
								$("#btuClick").trigger("click");
								/* for (var i = 0; i < arr.length; i++) {
									$("#realClassLi")
											.append(
													"<input type='checkbox' value='"+arr[i].realClassNum+"' name='realClass'/>"
															+ arr[i].realClassNum);
								} */
							}
						});
		            }
		        }
			}],
			onClickRow:function (row,$element) {
				$("#taskId").val(row.taskId);
				$.ajax({
					        url:"${pageContext.request.contextPath}/teacher/ajaxIsCreated",//请求地址
					        type:"post",//请求方式
							async:false,
					        data:"taskId="+row.taskId,//发送信息
					        dataType:"text",//服务器响应信息类型，不写则为默认
					        success:function(responseContent){  //success:function(responseContent)为回调函数   responseContent为接收响应信息
								var flag = eval(responseContent);
								 if(flag=='true'){
									$("#selectErrorMsg").css("display","block");
								
					                $("#selectErrorMsg").html("<strong>警告！</strong>该任务已经在该班级发布，如果仍然选择该任务，可能会丢失之前的任务数据。请谨慎选择")
					            } else if(flag=='false'){
									$("#selectErrorMsg").css("display","none");
								}
					        }
				});
			},
			onUncheck:function(row,$element){
				$("#selectErrorMsg").css("display","none");
			},
			onCheck:function(row){
				
				 $("#taskId").val(row.taskId);
				 $.ajax({
						        url:"${pageContext.request.contextPath}/teacher/ajaxIsCreated",//请求地址
						        type:"post",//请求方式
								async:false,
						        data:"taskId="+row.taskId,//发送信息
						        dataType:"text",//服务器响应信息类型，不写则为默认
						        success:function(responseContent){  //success:function(responseContent)为回调函数   responseContent为接收响应信息
									
									var flag = eval(responseContent);
									 if(flag=='true'){
										$("#selectErrorMsg").css("display","block");
										
						                $("#selectErrorMsg").html("<strong>警告！</strong>该任务已经在该班级发布，如果仍然选择该任务，可能会丢失之前的任务数据。请谨慎选择")
						            } else if(flag=='false'){
										$("#selectErrorMsg").css("display","none");
									}
						        }
					});
		      }

		});
	});
	function btnGroup () {   // 自定义方法，添加操作按钮
	    // data-target="xxx" 为点击按钮弹出指定名字的模态框
	    let html =
	        '<a href="####" class="btn btn-info" id="preview" data-toggle="modal" data-target="#editrole" title="修改权限">' +
	        '<span >预览</span></a>' 
	    return html
	};
	
	
	function InitMainTable() {
		
		$table = $('#taskListTable').bootstrapTable({
			method : 'get',
			url : "${pageContext.request.contextPath}/teacher/getTaskListPage?taskCategory="+$tasktype,//请求路径
			dataType : "json",
			striped : true, //是否显示行间隔色
			pageNumber : 1, //初始化加载第一页
			pagination : true,//是否分页
			striped: true,  //是否显示行间隔色
			sidePagination : 'client',//server:服务器端分页|client：前端分页
			pageSize : 5,//单页记录数
			pageList : [ 5, 10, 20, 30 ],//可选择单页记录数
			showRefresh : false,//刷新按钮
			clickToSelect: true,                //是否启用点击选中行
			singleSelect  : true,
			queryParams : function(params) {//上传服务器的参数
				var temp = {//如果是在服务器端实现分页，limit、offset这两个参数是必须的
					limit : params.limit, // 每页显示数量
					offset : params.offset // SQL语句起始索引
					//page : (params.offset / params.limit) + 1, //当前页码 
					/* Name : $('#search_name').val(),
					Tel : $('#search_tel').val() */
				};
				return temp;
			},
			columns : [ {
                checkbox: true,
                visible: true  
            }, /* {
				field : 'taskId',
				title : '任务Id',
				visible: false 
			}, */{
				field : 'taskTitle',
				title : '任务标题',

			}, {
				field : 'taskDetail',
				title : '任务描述',

			}, {
				field : 'publisherId',
				title : '发布者',

			}, {
				field : 'taskType',
				title : '分类',

			},/*  {
				field : 'courseId',
				title : '课程',
				visible: false
			}, */ {
				field : 'useNum',
				title : '使用次数'

			},{
				field : 'operate',
				title : '预览',
				formatter: btnGroup,    // 自定义方法，添加按钮组

			},  ],
			onClickRow:function (row,$element) {
				$("#taskId").val(row.taskId);
				$.ajax({
					        url:"${pageContext.request.contextPath}/teacher/ajaxIsCreated",//请求地址
					        type:"post",//请求方式
							async:false,
					        data:"taskId="+row.taskId,//发送信息
					        dataType:"text",//服务器响应信息类型，不写则为默认
					        success:function(responseContent){  //success:function(responseContent)为回调函数   responseContent为接收响应信息
								
								var flag = eval(responseContent);
								 if(flag=='true'){
									$("#selectErrorMsg").css("display","block");
								
					                $("#selectErrorMsg").html("<strong>警告！</strong>该任务已经在该班级发布，如果仍然选择该任务，可能会丢失之前的任务数据。请谨慎选择")
					            } else if(flag=='false'){
									$("#selectErrorMsg").css("display","none");
								}
					        }
				});
				   
			},
			onUncheck:function(row,$element){
				$("#selectErrorMsg").css("display","none");
			},
			onCheck:function(row){
				
				 $("#taskId").val(row.taskId);
				 $.ajax({
						        url:"${pageContext.request.contextPath}/teacher/ajaxIsCreated",//请求地址
						        type:"post",//请求方式
								async:false,
						        data:"taskId="+row.taskId,//发送信息
						        dataType:"text",//服务器响应信息类型，不写则为默认
						        success:function(responseContent){  //success:function(responseContent)为回调函数   responseContent为接收响应信息
									
									var flag = eval(responseContent);
									 if(flag=='true'){
										$("#selectErrorMsg").css("display","block");
										
						                $("#selectErrorMsg").html("<strong>警告！</strong>该任务已经在该班级发布，如果仍然选择该任务，可能会丢失之前的任务数据。请谨慎选择")
						            } else if(flag=='false'){
										$("#selectErrorMsg").css("display","none");
									}
						        }
					});
		      }

		});
	};
	var autoTextarea = function(elem, extra, maxHeight) {
		extra = extra || 0;
		var isFirefox = !!document.getBoxObjectFor
				|| 'mozInnerScreenX' in window, isOpera = !!window.opera
				&& !!window.opera.toString().indexOf('Opera'), addEvent = function(
				type, callback) {
			elem.addEventListener ? elem
					.addEventListener(type, callback, false) : elem
					.attachEvent('on' + type, callback);
		}, getStyle = elem.currentStyle ? function(name) {
			var val = elem.currentStyle[name];
			if (name === 'height' && val.search(/px/i) !== 1) {
				var rect = elem.getBoundingClientRect();
				return rect.bottom - rect.top
						- parseFloat(getStyle('paddingTop'))
						- parseFloat(getStyle('paddingBottom')) + 'px';
			}
			;
			return val;
		} : function(name) {
			return getComputedStyle(elem, null)[name];
		}, minHeight = parseFloat(getStyle('height'));
		elem.style.resize = 'none';
		var change = function() {
			var scrollTop, height, padding = 0, style = elem.style;
			if (elem._length === elem.value.length)
				return;
			elem._length = elem.value.length;
			if (!isFirefox && !isOpera) {
				padding = parseInt(getStyle('paddingTop'))
						+ parseInt(getStyle('paddingBottom'));
			}
			;
			scrollTop = document.body.scrollTop
					|| document.documentElement.scrollTop;
			elem.style.height = minHeight + 'px';
			if (elem.scrollHeight > minHeight) {
				if (maxHeight && elem.scrollHeight > maxHeight) {
					height = maxHeight - padding;
					style.overflowY = 'auto';
				} else {
					height = elem.scrollHeight - padding;
					style.overflowY = 'hidden';
				}
				;
				style.height = height + extra + 'px';
				scrollTop += parseInt(style.height) - elem.currHeight;
				document.body.scrollTop = scrollTop;
				document.documentElement.scrollTop = scrollTop;
				elem.currHeight = parseInt(style.height);
			}
			;
		};
		addEvent('propertychange', change);
		addEvent('input', change);
		addEvent('focus', change);
		change();
	};

	function selectfunction() {

		$(".selectTaskButton").css("background-color", "#015293");
		$(".selectTaskButton").css("color", "#fff");
		$(".editTaskButton").css("background-color", "#fff");
		$(".editTaskButton").css("color", "#000000");
		$(".editTask").css("display", "none");
		$(".selectTask").css("display", "block");
	};

	function editfunction() {
		$(".editTaskButton").css("background-color", "#015293");
		$(".editTaskButton").css("color", "#fff");
		$(".selectTaskButton").css("background-color", "#fff");
		$(".selectTaskButton").css("color", "#000000");
		$(".editTask").css("display", "block");
		$(".selectTask").css("display", "none");
	};
	function testud() {

		var startTime = $(".wui-date-editor input").val()

		$("#publishTime").val(startTime);

	};
	function load() {

		$("#textarea").val('');
		$("#accessory").css("data-show-preview", true);
	};
	function checkInputEdit(form) {
		 if($("#taskTitleEdit").val()==""){
			 alert("请输入任务标题");
			 $("#taskTitleEdit").focus();
			 return false;	 
		 }
		 if($("#taskDetail").val()==""&&!$('#accessory').get(0).files[0]){
			 alert("请输入作业描述或者选择上传的任务附件");
			 return false;	
		 }
		 if($("#taskEndTimeEdit").val()==""){
			 debugger;
			 alert("请选择任务截至时间");
			 $("#taskEndTimeEdit").focus();
			 return false;	
		 }
	};
	function checkInputSelect(form) {
		 if($("#taskId").val()==""){
			 alert("请从任务库选择一个待发布的作业");
			 return false;	 
		 }
	
		 if($("#taskEndTimeEdit").val()==""){
			 alert("请选择任务截至时间");
			 $("#taskEndTimeEdit").focus();
			 return false;	
		 }
	}
	
		
	/*用window.onload调用myfun()*/

	// $(".selectTaskButton").click(function(){
	// 	
	// 	 $(".selectTaskButton").css("background-color","#015293");
	// 	$(".selectTaskButton").css("color","#fff");
	// 	$(".editTaskButton").css("background-color","#fff");
	// 	$(".editTaskButton").css("background-color","#000000"); */
	// });
	//  $(".editTaskButton").click(function(){
	// 	$(".editTaskButton").css("background-color","#015293");
	// 	$(".editTaskButton").css("color","#fff");
	// 	$(".selectTaskButton").css("background-color","#fff");
	// 	$(".selectTaskButton").css("color","#000000");
	// })
</script>

</head>
<body style="background-color: #F1F3F4;">
	<div
		style="width: 100%; height: 50px; margin: 0 auto; background-color: #fff;">

		<div style="width: 20px; height: 50px; background: #015293;float: left;"></div>
		<span style="line-height: 55px;font-size: 18px;margin-left: 20px;font-weight: bold;">发布任务</span>

	</div>
	<div class="selectstyle"
		style="width: 100%; height: 50px; margin: 10px auto; background-color: #fff;">
		<div class="selectTaskButton" onclick="selectfunction()"
			style="width: 50%; height: 50px; float: left; background-color: #015293; text-align: center; color: #fff;">
			<span style="line-height: 50px; font-size: 16px;">选择任务</span>
		</div>
		<div class="editTaskButton" onclick="editfunction()"
			style="width: 50%; height: 50px; float: left; text-align: center; color: #000000;">
			<span style="line-height: 50px; font-size: 16px;">自定义任务</span>
		</div>

	</div>


	<div class="editTask"
		style="width: 100%; height: 100%; margin: 0px auto; background-color: #fff; padding: 30px 0; display: none;">
		<div class="editTaskContent" style="margin: 30px 50px;">
			<form action="${pageContext.request.contextPath}/teacher/publishTask"
				id="publish" enctype="multipart/form-data" method="post"
				onsubmit="return checkInputEdit(this)" target="_top">
				<div style="height: 1px; width: 100%;">
					<div class="input-group"
						style="float: left; width: 60%; margin-right: 2%;">
						<span class="input-group-addon" id="basic-addon3"
							style="font-size: 20px; padding: 0 50px; font-weight: bold;">任务标题</span>
						<input type="text" name="taskTitle" class="form-control"
							id="taskTitleEdit" aria-describedby="basic-addon3">
					</div>
					<div class="input-group" style="float: left; width: 30%;">
						<span class="input-group-addon" id="basic-addon3"
							style="font-size: 20px; padding: 0 50px; font-weight: bold;">任务类型</span>

						<div class="btn-group">
							<button id="basic-url" type="button"
								class=" form-control btn btn-primary  dropdown-toggle"
								data-toggle="dropdown" aria-haspopup="true"
								aria-expanded="false">
								<span class="caret"></span><span id="typeNameEdit"
									style="font-size: 18px; margin-left:;">作业</span>
							</button>
							<ul id="selectTypeEdit" class="dropdown-menu">
								<c:forEach items="${taskCategoryList }" var="taskCategory">
									<li style="font-size: 18px; padding-left: 20px"><c:if
											test="${taskCategory=='work' }">作业</c:if> <c:if
											test="${taskCategory=='trial' }">实验</c:if> <c:if
											test="${taskCategory=='course_design' }">课程设计</c:if>
											<c:if
											test="${taskCategory=='turn_class' }">翻转</c:if></li>

								</c:forEach>


							</ul>
						</div>
					</div>
					<input type="text" id="taskCategoryEdit" name="taskCategory"
						hidden="hidden" value="work" />
				</div>
				<div class="input-group"
						style=" width: 100%; margin-right: 2%;padding-top: 20px; margin-top: 10px;">
						<span class="input-group-addon" id="basic-addon3"
							style="font-size: 20px; padding: 0 49px; font-weight: bold;">&nbsp;&nbsp;知识点&nbsp;&nbsp;</span>
						<input type="text" name="knowledgePoints" class="form-control" placeholder="输入任务相关知识点内容"
							id="knowledgePoints" aria-describedby="basic-addon3">
				</div>
				<div class="taskdetail"
					style="width: 100%; padding-top: 10px; margin-top: 10px;">
					<div class="panel panel-primary">
						<div class="panel-heading">
							<h3 class="panel-title">编辑任务</h3>
						</div>

						<textarea id="taskDetail" name="taskDetail" class="form-control"
							style="width: 100%; height: 100px; resize: none;"
							placeholder="编辑任务"></textarea>

					</div>
				</div>

				<div class="form-group">
					添加任务附件
					<div class="file-loading">
						<input id="accessory" name="file" class="file" type="file"
							multiple="multiple" data-show-preview="false" style="width: 70%"
							data-show-caption="true">
					</div>
				</div>

				<div id="preEditTime" class="input-group"
					style="width: 60%; margin-right: 2%;display: none">
					<span class="input-group-addon" id="basic-addon3"
						style="font-size: 20px; padding: 0 50px; font-weight: bold;">课前截至时间</span>
					<input type="text" style="height: 30px;width: 185px"  class="Wdate form-control" name="preTaskEndTime" id="taskEndTimeSelect" onclick="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'%y-%M-%d %H:%m:%s'})" placeholder="请输入时间"/>
				</div>
				<div id="editTime" class="input-group"
					style="width: 60%; margin-right: 2%;">
					<span class="input-group-addon" id="basic-addon3"
						style="font-size: 20px; padding: 0 50px; font-weight: bold;">任务截至时间</span>
					
				<!--  需要限制时间用这个 -->	
				<!-- <input type="text" style="height: 30px;width: 185px" class="Wdate form-control" name="taskEndTime" id="taskEndTimeSelect" onclick="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'%y-%M-%d %H:%m:%s'})" value="请输入时间"/>  -->
				<input type="text" style="height: 30px;width: 185px" class="Wdate form-control" name="taskEndTime" id="taskEndTimeSelect" onclick="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss'})" value="请输入时间"/> 
				
				</div>
				

				<!-- <div class="wui-content">
					<div class="wui-area">
						<h4 class="h4">选择年月日 时分秒</h4>
						<button onclick="testud()">测试</button>
						<p>输出格式yyyy-mm-dd hh:mm:ss</p>
						<wui-date 
							format="yyyy-mm-dd hh:mm:ss" 
							placeholder="请选择或输入日期" 
							id="date4" 
							btns="{'ok':'确定','now':'此刻'}" 
							ng-model="date4"
						>
						</wui-date>
					</div> -->

				<div class="toSubmit"
					style="width: 100%; height: 50px; margin-top: 50px;">
					<div style="width: 20%; height: 50px; margin: 0 auto;">
						<input class="btn btn-primary" type="submit" value="提交"
							style="float: left; margin-right: 25%;"> <input
							class="btn btn-default" type="submit" value="取消"
							style="float: left;">
					</div>
				</div>
			</form>
		</div>
	</div>
	<div class="selectTask"
		style="width: 100%; height: 100%; margin: 0px auto; background-color: #fff; padding: 30px 0;">
		<div class="selectTaskContent" style="margin: 30px 50px;">

		<form action="${pageContext.request.contextPath}/teacher/selectTaskToPublish"
				method="post" onsubmit = "return checkInputSelect(this)" target="_top">
			<span>这是一个快速发布优质作业的重要途径，当然您也可以选择点击右侧自定义标签自定义作业</span>
			<div class="input-group" style="float: left; width: 30%;">
				<span class="input-group-addon" id="basic-addon3"
					style="font-size: 20px; padding: 0 50px; font-weight: bold;">任务类型</span>	
				<div class="btn-group">
							<button id="basic-url" type="button"
								class=" form-control btn btn-primary  dropdown-toggle"
								data-toggle="dropdown" aria-haspopup="true"
								aria-expanded="false">
								<span class="caret"></span><span id="typeNameSelect"
									style="font-size: 18px; margin-left:;">作业 </span>
							</button>
							<ul id="selectTypeSelect" class="dropdown-menu">
								<c:forEach items="${taskCategoryList }" var="taskCategory">
									<li style="font-size: 18px;padding-left: 20px">
										<c:if test="${taskCategory=='work' }">作业</c:if>
										<c:if test="${taskCategory=='trial' }">实验</c:if>
										<c:if test="${taskCategory=='course_design' }">课程设计</c:if>
										<c:if test="${taskCategory=='turn_class' }">翻转</c:if>
									</li>
								</c:forEach>
							</ul>
				</div>
				<input type="text" id="taskCategorySelect" name="taskCategory"
						hidden="hidden" value="work" />

				</div>

				<div class="selectTaskList" style="margin-top: 60px;">
					<!-- <iframe id="seleTaskItems" src="toselectTaskList" width="100%" height="500px">

					</iframe> -->
					<div class="alert alert-danger" id="selectErrorMsg" style="display: none" role="alert"></div>
				<table id="taskListTable"></table>
			</div>
			<input name="taskId" id="taskId"  type="text" hidden="hidden" value=""/>
			<div id="preSelectTime" class="input-group"
					style="width: 60%; margin-right: 2%;display: none">
					<span class="input-group-addon" id="basic-addon3"
						style="font-size: 20px; padding: 0 50px; font-weight: bold;">课前截至时间</span>
					<input type="text" style="height: 30px;width: 185px"  class="Wdate form-control" name="preTaskEndTime" id="taskEndTimeSelect" onclick="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'%y-%M-%d %H:%m:%s'})" placeholder="请输入时间"/>
				</div>
			<div id="selectTime" class="input-group"
				style="width: 68%; margin-right: 2%;">
				<span class="input-group-addon" id="basic-addon3"
					style="font-size: 20px; padding: 0 50px; font-weight: bold;">截至时间</span>
				<!--  需要限制时间用这个 -->	
				<!-- <input type="text" style="height: 30px;width: 185px" class="Wdate form-control" name="taskEndTime" id="taskEndTimeEdit" onclick="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'%y-%M-%d %H:%m:%s'})" value="请输入时间"/>  -->
				<input type="text" style="height: 30px;width: 185px" class="Wdate form-control" name="taskEndTime" id="taskEndTimeEdit" onclick="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss'})" value="请输入时间"/> 
		</div>
			
				
				
				<div class="toSubmit"
					style="width: 100%; height: 50px; margin-top: 50px;">
					<div style="width: 20%; height: 50px; margin: 0 auto;">
						<input class="btn btn-primary" type="submit" value="提交"
							style="float: left; margin-right: 25%;"> <input
							class="btn btn-default" type="submit" value="取消"
							style="float: left;">
					</div>

				</div>
			</form>
		</div>

	</div>
	<!-- Large modal -->
	<button id="btuClick" style="display: none" type="button"
		class="btn btn-primary" data-toggle="modal"
		data-target=".bs-example-modal-lg">Large modal</button>

	<div class="modal fade bs-example-modal-lg" tabindex="-1" role="dialog"
		aria-labelledby="myLargeModalLabel">
		<div class="modal-dialog modal-lg" role="document">
			<div id="autoCreateMoTai" class="modal-content"></div>
		</div>
	</div>

	<script>
		var text = document.getElementById("taskDetail");
		autoTextarea(text); // 调用
	</script>
	<script type="text/javascript">
		var app = angular.module('app', [ "wui.date" ]);
	</script>
</body>
</html>
