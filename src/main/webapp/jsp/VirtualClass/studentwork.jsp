<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>  
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title></title>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/studentwork.css"/>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
		<script src="${pageContext.request.contextPath}/js/jquery.min.js" type="text/javascript" charset="utf-8"></script>
		<script src="${pageContext.request.contextPath}/js/bootstrap.js" type="text/javascript" charset="utf-8"></script>
		<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/ueditor.config.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/ueditor.all.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/ueditor/zh-cn.js"></script>
		<script type="text/javascript">
			function checkInput(form) {
				var fileInput = $('#upfile').get(0).files[0];
				
				 if(!UE.getEditor('upTaskDetail').hasContents()&&!fileInput){
					 alert("请编写作业以后再尝试提交");
					 return false;	 
				 }
			
			};
			function editstaus(){
				var show  = $('.edited').css('display');
				if(show=='block'){
					$('.edited').hide();
					$('.toedit').show();
					var editedtxt = $('#editedtext').text();
					$('#TestCode').val(editedtxt);
					$('.butt').attr('value','完成');
					$('#upTaskDetail').val(editedtxt);
					$('#trueSubmit').hide();
					$('#falseSubmit').show();
					
				}else {
					$('.edited').show();
					$('.toedit').hide();
					$('.butt').attr('value','编辑');
					var edit = $('#TestCode').val();
					$('#editedtext').text(edit);
					$('#upTaskDetail').val(edit);
					$('#trueSubmit').show();
					$('#falseSubmit').hide();
				}
				
			};
			function stopSubmit() {
				alert("请先结束编辑状态后再尝试提交");
			};
			function  toUpdatePage() {
				$("#firstUp").css("display","none");
				$("#secondUp").css("display","block");
			}
			function quitUpdate() {
				$("#firstUp").css("display","block");
				$("#secondUp").css("display","none");
				return false;
			}
			
			function quitUp() {
				 
				$("#upTaskDetail").val("");
				$("#upfile").val("");
				return false;
			}
		</script>
	</head>
	<body style="background-color: #f8f8f8;">
			<jsp:include page="/jsp/top.jsp" flush="true"/>
			<div style="width: 1200px;padding-top: 10px;margin-bottom: 10px;">
			<div style="width: 100%;height: 100px;background-color: #fff;margin-bottom: 10px;text-align:left;padding-left: 20px;padding-top: 20px">
				<div style="margin-bottom: 10px"><span style="font-size: 28px;font-weight: bold;">${courseName }</span></div>
				<div><span style="font-size: 18px;">${virtualClassName } &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;授课教师：${teacherClassName }</span></div>
			</div>
		<div class="worktitle">
			<div style="width: 10px;height: 10px;margin-top: 20px;margin-left: 20px;background-color: blue;float: left;"></div>
			<h4 style="float: left;line-height: 170%;margin-left: 20px;font-weight: bold;">${task.taskTitle }</h4>
		</div>
		<div class="workdiv">
			<div class="workcontent">
				<div style="height: 70px;">
					<div style="float: left;"><h4 style="font-weight: bold;">任务详情</h4></div>
					<!-- <div style="float: right;"><input class="btn btn-default" type="button" value="编辑"></div> -->
				</div>
				
				<div style="text-align:left ;padding-left: 36px;">
					<span  style="word-wrap:break-word; word-break:break-all; overflow: hidden; ">${task.taskDetail }</span>
				</div>
				<hr >
				<div style="height: 35px;">
					<div style="float: left;font-weight: bold;"><h4 style="font-weight: bold;">附件</h4></div>
				</div>
				<div style="height: 75px;padding-left: 36px;">
					
					<div class="accessorylist">
						<c:forEach items="${task.accessoryList }" var="accessory"  varStatus="status">
							<a href="${pageContext.request.contextPath}/teacher/resourceDownload?fileName=${accessory.accessoryName }&id=${task.taskId }&type=teacherPub">
								<button class="btn btn-default" type="submit" style="border-radius: 20px;float: left;" >${accessory.accessoryName }</button>
							</a>
						</c:forEach>
					</div>
				</div>
				<div class="timestaus" style="float: left;height: 50px;margin-top: -10px;">
					<h8>发布时间：${fn:substring(task.publishTime,0,16)}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;截止时间：${fn:substring(taskEndTime,0,16)}</h8>
				</div>
			</div>
			
		</div>
		<!-- 首次提交作业star -->
		<div class="workdiv" id="firstUp" style="margin-top: 10px;display: block;">
			<div class="workcontent">
				<form target="_top" action="${pageContext.request.contextPath}/student/toUpTask/${task.taskId }" enctype="multipart/form-data" method="post"  onsubmit="return checkInput(this)" >
					
				
					<div style="height: 70px;">
						<div style="float: left;"><h4 style="font-weight: bold;">作业详情</h4></div>
						
						<%-- <c:if test="${empty upTaskDetail && empty accessoriesName }">
								<div style="float: right;"><input onclick="editstaus()" class="btn btn-default butt" type="button" value="编辑" ></div>
						</c:if> --%>
						<input id="taskId"  name="taskId" value="${task.taskId }"  type="hidden" />
					</div>
					
					<c:if test="${not empty upTaskDetail || not empty accessoriesName }">
						<div style="text-align:left ;display: block;padding-left: 36px;" class="edited">
							<span id="editedtext" style="word-wrap:break-word; word-break:break-all; overflow: hidden; ">${upTaskDetail }</span>
						</div>
					</c:if>
					<c:if test="${!isEnd }">
						<c:if test="${empty upTaskDetail && empty accessoriesName }">
							<div class="toedit" style="display: block;width: 100%;">
								<textarea autofocus="autofocus" id="upTaskDetail"placeholder="编辑作业" name="upTaskDetail" style=" width: 100%;height: 150px;" wrap="logical">${upTaskDetail }</textarea>
							</div>
						</c:if>
					</c:if>
					<input value="${upTaskDetail }"  type="hidden" />
					<hr >
				<div style="height: 35px;">
					<div style="float: left;font-weight: bold;"><h4 style="font-weight: bold;">附件</h4></div>
				</div>
					<div style="height: 75px;padding-left: 36px;">
					
						<c:if test="${!isEnd }">
							<c:if test="${empty upTaskDetail && empty accessoriesName }">
								<input  class="btn btn-default" id="upfile"  name="file" type="file"  multiple="multiple" style="border-radius: 20px;float: left;" value="上传附件" />
							</c:if>
						</c:if>
						<c:if test="${not empty upTaskDetail || not empty accessoriesName }">
							<c:forEach items="${accessoriesName }" var="accessoriesName"  varStatus="status">
								<a href="${pageContext.request.contextPath}/teacher/resourceDownload?fileName=${accessoriesName }&id=${task.taskId }&type=studentUp">
									<button class="btn btn-default" type="button" style="border-radius: 20px;float: left;" >${accessoriesName }</button>
								</a>
							</c:forEach>
						</c:if>
					<!--	<div class="accessorylist">
							<button class="btn btn-default" type="submit" style="border-radius: 20px;float: left;">文件一</button>
							<button class="btn btn-default" type="submit" style="border-radius: 20px;float: left;">文件二</button>
						</div>
						  -->
					</div>
					<c:if test="${!isEnd }">
							<c:if test="${empty upTaskDetail && empty accessoriesName }">
								<input id="trueSubmit" class="btn btn-primary" type="submit" value="确认提交" >
								<input id="trueSubmit" onclick="return quitUp()" type="submit"  class="btn btn-primary"  value="撤销" >
							</c:if>
					</c:if>
					<c:if test="${!isEnd }">
					
					
						 <c:if test="${not empty upTaskDetail || not empty accessoriesName }">
							<input id="trueSubmit"  style="display: block" class="btn btn-warning" type="button" onclick="toUpdatePage()" value="修改作业" style="margin-left: -350px;">
						</c:if>
					</c:if>
					<c:if test="${isEnd }">
						<input id="trueSubmit"  style="display: block" class="btn btn-warning" disabled="disabled" type="button" onclick="toUpdatePage()" value="已结束" style="margin-left: -350px;">
					
					</c:if>

				</form>
			</div>
			
		</div>
			<!-- 首次提交作业end -->
			
			<!-- 修改提交作业star -->
		<div class="workdiv" id="secondUp" style="margin-top: 10px;display: none">
			<div class="workcontent">
				<form target="_top" action="${pageContext.request.contextPath}/student/toUpdateUpTask/${task.taskId }" enctype="multipart/form-data" method="post"  onsubmit="return checkInput(this)" >
					
				
					<div style="height: 70px;">
						<div style="float: left;"><h4 style="font-weight: bold;">作业详情</h4></div>
						
						<%-- <c:if test="${empty upTaskDetail && empty accessoriesName }">
								<div style="float: right;"><input onclick="editstaus()" class="btn btn-default butt" type="button" value="编辑" ></div>
						</c:if> --%>
						<input id="taskId"  name="taskId" value="${task.taskId }"  type="hidden" />
					</div>
					<div class="toedit" style="display: block;width: 100%;">
							<textarea autofocus="autofocus" id="upTaskDetail"placeholder="编辑作业" name="upTaskDetail" style=" width: 100%;height: 150px;" wrap="logical">${upTaskDetail }</textarea>
					</div>
					<hr >
				<div style="height: 35px;">
					<div style="float: left;font-weight: bold;"><h4 style="font-weight: bold;">附件</h4></div>
				</div>
					<div style="height: 75px;padding-left: 36px;">
						<input  class="btn btn-default" id="upfile"  name="file" type="file"  multiple="multiple" style="border-radius: 20px;float: left;" value="上传附件" />
					<!--	<div class="accessorylist">
							<button class="btn btn-default" type="submit" style="border-radius: 20px;float: left;">文件一</button>
							<button class="btn btn-default" type="submit" style="border-radius: 20px;float: left;">文件二</button>
						</div>
						  -->
					</div>
					
					<input id="trueSubmit" class="btn btn-primary" type="submit" value="确认修改" style="margin-left: -973px;">
					<input id="trueSubmit" onclick="return quitUpdate()" type="submit"  class="btn btn-primary"  value="取消修改" style="margin-left: 10px">
 
				</form>
			</div>
			
		</div>
			<!-- 修改提交作业end -->
			
		
		<c:if test="${not empty grade }">
		<div class="workdiv" style="margin-top: 10px;overflow: hidden;">
			<div class="workcontent">
				<div style="height: 30px;">
					<div style="float: left;"><h4 style="font-weight: bold;">评分详情</h4></div>
					<!-- <div style="float: right;"><input class="btn btn-default" type="button" value="编辑"></div> -->
				</div>
				<hr >
				<c:if test="${not empty grade }">
							<div style="text-align:left ;">
								<span style="word-wrap:break-word; word-break:break-all; overflow: hidden;font-size: 16px;font-weight: bold; ">评语：</span><span style="font-size: 14px;">${comment }</span>
							</div>
							<hr>
							<div style="text-align:left ;">
								<span style="font-size: 16px;font-weight: bold;">作业成绩：</span> <span style="font-size: 20px;color: red;font-weight: bold;">${grade }</span>
							</div>
					</c:if>
				
				
			</div>
			
		</div>
		</c:if>
		<div style="width: 100%;height: 20px;background-color: #f8f8f8;">
		</div>
	</div>
	
		<script type="text/javascript">
			// $(document).ready(function(){
			// 	$("#publishedlist").css('display','none'); 
			// 	$("#publishedlist").css('display','none'); 
			// 	
			// });
			// $(function(){     
			// 	$(".published").click(function(){
			// 		$('#publishedlist').show(); 
			// 		$('.published').css('color','blue'); 
			// 		$('#unpublishedlist').hide(); 
			// 		$('.unpublish').css('color','#000'); 
			// 	});
			// 	$(".unpublish").click(function(){
			// 		$('#publishedlist').hide(); 
			// 		$('.published').css('color','#000'); 
			// 		$('#unpublishedlist').show(); 
			// 		var show =$('#publishedlist').css('display'); 
			// 		alert(show);
			// 		$('.unpublish').css('color','blue'); 
			// 	});
			// })
			function published(){
				alert("published");
				var published = document.getElementById('publishedlist');
				var unpublished = document.getElementById('unpublishedlist');
				published.style.display="block";
				unpublished.style.display="none";
			};
			function unpublished(){
				alert("unpublished");
				var published = document.getElementById('publishedlist');
				var unpublished = document.getElementById('unpublishedlist');
				published.style.display="none";
				unpublished.style.display="block";
			}
		</script>
		<div class="goback" style="height: 30px;width: 60px;position: fixed;bottom: 170px;right: 70px;">
			<a target="_top" href="${pageContext.request.contextPath}/teacher/toClassDetail?virtualClassNum=${virtualClassNum }&virtualClassName=${virtualClassName }&courseName=${courseName }">
				<span class="glyphicon glyphicon-send" aria-hidden="true"></span>
				<span class="glyphicon-class">返回作业列表页</span>
			</a>
		</div>
		<jsp:include page="/jsp/footer.jsp" flush="true"/>
		<script type="text/javascript">

var ue = UE.getEditor('upTaskDetail',{

    initialFrameWidth :1151,//设置编辑器宽度

    initialFrameHeight:300,//设置编辑器高度
    initialFrameMargin:0,

    scaleEnabled:false
    

 });

</script>
	</body>
	
</html>
