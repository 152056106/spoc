<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>太原工业学院SPOC-MOOC平台</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/base.css" type="text/css">
    <link
	href="${pageContext.request.contextPath}/css/achievement/achievementMain.css"
	rel="stylesheet" />
    <style>
    	.title p{
    		font-size: 10px;
    		opacity: 0.7;
    	}
    	
    	.xiangmu-out .rel-img img{
    		width: 100%;
    		height: 100%;
    		overflow: hidden;
    		transition:all 0.5s;
    	}
   .xiangmu-out:hover{
    box-shadow: 0 0 15px grey;
    transform: translate(0,-10px);
}

.xiangmu-out .rel-img{
	overflow: hidden;
}

.xiangmu-out:hover .rel-img img{
    transform: scale(1.10);
    
}
    	
    </style>
    <script src="${pageContext.request.contextPath}/js/jquery.min%20(1).js" ></script>
    <!-- <script type="text/javascript">
        $(function (){
            var order=1;
            window.setInterval(lunbo,1000);
            function lunbo(){
                if(order==4){
                    order=0;
                }
                $(".banner-img img").eq(order).css("display","block").siblings().css("display","none");
                $(".square .square1").eq(order).css("background-color","#FF6600").siblings().css("background-color","white");
                order++;
            }
        });
    </script> -->
    <link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/bootstrap.min.css">
<script
	src="${pageContext.request.contextPath}/js/Admin/jquery-1.10.2.js"></script>
	<script
	src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
	<script
	src="${pageContext.request.contextPath}/js/adminJs/bootstrap.min.js"></script>
	<script
	src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
</head>
<body style="background: #f0f0f0;">
      <jsp:include page="/jsp/top.jsp" flush="true" />
      <div class="banner" style="height: 334px;">
      	<div class="container" style="width: 100%;padding: 0;">
					<div class="carousel slide" id="slidershow" data-ride="carousel" data-interval="2000">
						<!--计数器-->
						<ol class="carousel-indicators">
							<li class="active" data-target="#slidershow" data-slide-to="0"></li>
							<li data-target="#slidershow" data-slide-to="1"></li>
							<li data-target="#slidershow" data-slide-to="2"></li>
							<li data-target="#slidershow" data-slide-to="3"></li>
						</ol>
						<!--图片容器-->
						<div class="carousel-inner">
							<div class="item active">
								<img src="${pageContext.request.contextPath}/images/banner_img01.jpg"/>
								<!--添加对应标题和内容-->
								
							</div>
							<div class="item">
								<img src="${pageContext.request.contextPath}/images/banner_img02.jpg"/>
								<!--添加对应标题和内容-->
								
							</div>
							<div class="item">
								<img src="${pageContext.request.contextPath}/images/banner_img03.jpg"/>
								<!--添加对应标题和内容-->
								
							</div>
							<div class="item">
								<img src="${pageContext.request.contextPath}/images/banner_img04.jpg"/>
								<!--添加对应标题和内容-->
								
							</div>
							<!--轮播导航-->
							<a href="#slidershow" data-slide="prev" class="left carousel-control" role="button">
								
							</a>
							<a href="#slidershow" data-slide="next" class="right carousel-control" role="button">
								
							</a>
						</div>
					</div>
				</div>
      
      
      
      
      
      
          <%-- <div class="banner-img">
              <img src="${pageContext.request.contextPath}/images/banner_img01.jpg" alt="" style="width: 1400px;height:600px;overflow: hidden;"/>
              <img src="${pageContext.request.contextPath}/images/banner_img02.jpg" alt="" style="width: 1400px;height:600px;overflow: hidden;"/>
              <img src="${pageContext.request.contextPath}/images/banner_img03.jpg" alt="" style="width: 1400px;height:600px;overflow: hidden;"/>
              <img src="${pageContext.request.contextPath}/images/banner_img04.jpg" alt="" style="width: 1400px;height:600px;overflow: hidden;"/>
          </div>
          <div class="square">
              <div class="square1"></div>
              <div class="square1"></div>
              <div class="square1"></div>
              <div class="square1"></div>
          </div> --%>
      </div>
      <main>
          <!--基地-->
          <!--课程-->
          <div class="course">
              课程
              <span style="float: right;font-size: 16px;font-family: "微软雅黑 Light";line-height: 40px;margin-right: 1%;"><a href="<c:url value='/teacher/courseList' />">更多</a></span>
                 <div class="container" style="margin: 0;padding: 0;width: 100%">
      				 <div class="row">
                  <c:forEach items="${courseList }" var="course" varStatus="status" begin="0" end="3">
                   <div class="col-xs-3 col-md-3 " style="height: 394px;">
                  	<a href="<c:url value='/teacher/toCourseDetail/${course.courseId }'/>">
                          <div  class="jidixiangmu-li xiangmu-out" style="width: 100%;min-height: 345px;">
                              <div class="rel-img"  style="width: 100%;height: 200px;"><img src="${course.faceImg }" alt="" style="height: 200px;"></div>
                              <div class="info">
                                  <div class="title">
                                      <p style="font-family: 微软雅黑;margin-top:10px; font-size: 20px; font-weight: 20px;opacity: 1;">${course.courseName }</p>
                                      <p style="margin-bottom: -1%;"><span style="font-size: 13px;font-weight: 600;">教师团队：</span>
                                      	<c:forEach items="${course.teacherList }" var="teacher">
                							${teacher.teacherName }
                						</c:forEach>
                                      </p>
                                      <p style="margin-bottom: -1%;"><span style="font-size: 13px;font-weight: 600;">创建时间：</span>${publishTime[status.index] }</p>
                                      <p style="margin-bottom: -1%;"><span style="font-size: 13px;font-weight: 600;">课程人数：</span>${course.courseStudentNum }</p>
                                  </div>
                              </div>
                          </div>
                       </a>
                      </div>
                  </c:forEach>
                  </div>
      			<div class="row">
                  <c:forEach items="${courseList }" var="course" varStatus="status" begin="4" end="7">
                  <div class="col-xs-3 col-md-3 " style="height: 394px;">
                  	<a href="<c:url value='/teacher/toCourseDetail/${course.courseId }'/>">
                          <div  class="jidixiangmu-li xiangmu-out" style="width: 100%;min-height: 345px;">
                              <div class="rel-img"  style="width: 100%;height: 200px;"><img src="${course.faceImg }" alt="" style="height: 200px;"></div>
                              <div class="info">
                                  <div class="title">
                                      <p style="font-family: 微软雅黑;margin-top:10px; font-size: 20px; font-weight: 20px;opacity: 1;">${course.courseName }</p>
                                      <p style="margin-bottom: -1%;"><span style="font-size: 13px;font-weight: 600;">教师团队：</span>
                                      	<c:forEach items="${course.teacherList }" var="teacher">
                							${teacher.teacherName }
                						</c:forEach>
                                      </p>
                                      <p style="margin-bottom: -1%;"><span style="font-size: 13px;font-weight: 600;">创建时间：</span>${publishTime[status.index] }</p>
                                      <p style="margin-bottom: -1%;"><span style="font-size: 13px;font-weight: 600;">课程人数：</span>${course.courseStudentNum }</p>
                                  </div>
                              </div>
                          </div>
                       </a>
                      </div>
                  </c:forEach>
                      <!-- <td>
                          <div  class="jidixiangmu-li">
                              <div class="rel-img"><img src="../img/tu4.png" alt="" style="width: 300px;height: 200px;"></div>
                              <div class="info">
                                  <div class="title">
                                      <p>课程名：岩石学</p>
                                      <p>创建教师：邵龙义、鲁静</p>
                                      <p>创建时间：2006-12-22</p>
                                      <p>课程人数：1980</p>
                                  </div>
                              </div>
                          </div>
                      </td>
                      <td>
                          <div  class="jidixiangmu-li">
                              <div class="rel-img"><img src="../img/tu5.png" alt="" style="width: 300px;height: 200px;"></div>
                              <div class="info">
                                  <div class="title">
                                      <p>课程名：传感技术与应用</p>
                                      <p>创建教师：张晓琳、唐文彦</p>
                                      <p>创建时间：2006-12-22</p>
                                      <p>课程人数：1800</p>
                                  </div>
                              </div>
                          </div>
                      </td>
                      <td>
                          <div  class="jidixiangmu-li">
                              <div class="rel-img"><img src="../img/tu6.jpg" alt="" style="width: 300px;height: 200px;"></div>
                              <div class="info">
                                  <div class="title">
                                      <p>课程名：土木工程概论</p>
                                      <p>创建教师：熊峰</p>
                                      <p>创建时间：2006-12-22</p>
                                      <p>课程人数：2300</p>
                                  </div>
                              </div>
                          </div>
                      </td>
                      <td>
                          <div  class="jidixiangmu-li">
                              <div class="rel-img"><img src="../img/tu7.jpg" alt="" style="width: 300px;height: 200px;"></div>
                              <div class="info">
                                  <div class="title">
                                      <p>课程名：模拟电子技术基础</p>
                                      <p>创建教师：许菲、吴友宇、华剑</p>
                                      <p>创建时间：2006-12-22</p>
                                      <p>课程人数：2170</p>
                                  </div>
                              </div>
                          </div>
                      </td> -->
                 </div>
                 </div>
          </div>

          <!--明星学生-->
          <div class="touxiang">
              明星学生
              <span><a href="">更多</a></span>
              <nav>
                  <ul>
                      <li><a href="">
                          <div class="touxiang-out">
                              <div class="touxiang-int">
                                  <img style="margin-bottom: 13px;" src="${pageContext.request.contextPath}/images/touxiang_09.png" alt="">
                              </div>
                              <div class="touxiang_name">
                                  <p>刘文静</p>
                                  <p>计算机工程系</p>
                              </div>
                          </div>
                      </a></li>
                      <li><a href="">
                          <div class="touxiang-out">
                              <div class="touxiang-int">
                                  <img style="margin-bottom: 13px;" src="${pageContext.request.contextPath}/images/touxiang_10.png" alt="">
                              </div>
                              <div class="touxiang_name">
                                  <p>蒋丽君</p>
                                  <p>机械工程系</p>
                              </div>
                          </div>
                      </a></li>
                      <li><a href="">
                          <div class="touxiang-out">
                              <div class="touxiang-int">
                                  <img style="margin-bottom: 13px;" src="${pageContext.request.contextPath}/images/touxiang_03.png" alt="">
                              </div>
                              <div class="touxiang_name">
                                  <p>侯品然</p>
                                  <p>电子工程系</p>
                              </div>
                          </div>
                      </a></li>
                      <li><a href="">
                          <div class="touxiang-out">
                              <div class="touxiang-int">
                                  <img style="margin-bottom: 13px;" src="${pageContext.request.contextPath}/images/touxiang_04.png" alt="">
                              </div>
                              <div class="touxiang_name">
                                  <p>肖福聚</p>
                                  <p>计算机工程系</p>
                              </div>
                          </div>
                      </a></li>
                      <li><a href="">
                          <div class="touxiang-out">
                              <div class="touxiang-int">
                                  <img style="margin-bottom: 13px;" src="${pageContext.request.contextPath}/images/touxiang_05.png" alt="">
                              </div>
                              <div class="touxiang_name">
                                  <p>吴正谢</p>
                                  <p>自动化系</p>
                              </div>
                          </div>
                      </a></li>
                      <li><a href="">
                          <div class="touxiang-out">
                              <div class="touxiang-int">
                                  <img style="margin-bottom: 13px;" src="${pageContext.request.contextPath}/images/touxiang_06.png" alt="">
                              </div>
                              <div class="touxiang_name">
                                  <p>陈宇</p>
                                  <p>理学系</p>
                              </div>
                          </div>
                      </a></li>
                      <li><a href="">
                          <div class="touxiang-out">
                              <div class="touxiang-int">
                                  <img style="margin-bottom: 13px;" src="${pageContext.request.contextPath}/images/touxiang_07.png" alt="">
                              </div>
                              <div class="touxiang_name">
                                  <p>唐德天</p>
                                  <p>体育系</p>
                              </div>
                          </div>
                      </a></li>
                      <li><a href="">
                          <div class="touxiang-out">
                              <div class="touxiang-int">
                                  <img style="margin-bottom: 13px;" src="${pageContext.request.contextPath}/images/touxiang_08.png" alt="">
                              </div>
                              <div class="touxiang_name">
                                  <p>张晓玉</p>
                                  <p>经济与管理系</p>
                              </div>
                          </div>
                      </a></li>
                  </ul>
              </nav>
          </div>
	<c:if test="${aocscList!=null }">
          <!--学生成果-->
          <div class="chengguo">
              学生成果
              <span><a href="${pageContext.request.contextPath}/achievement/toAchievementMainPage">更多</a></span>
              
              <div class="row">
                  <c:forEach items="${aocscList }" var="item" varStatus="status" begin="0" end="3">
                   <div class="col-xs-3 col-md-3" ">
                  	<a
							href="${pageContext.request.contextPath}/achievement/toDetailCourseExpand?achievementId=${item.achievementId }">
							<div class="chengguo-out"
								style="padding: 0; height: 378px; margin-top: 1%;">
								<div class="doorPlank xiangmu-out"
									style="padding: 1.5rem; background-color: white; height: 100%;width: 91%;">
									<div class="rel-img" style="width: 100%; height: 58%;">
										<img src="${pageContext.request.contextPath}/img/fire.png"
											style="position: absolute; margin-left: 0; margin-top: 0;width: 23px;height: 23px;">
										<img alt="" style="width: 100%; height: 100%;"
											src="${pageContext.request.contextPath}/images/tu2.jpg" />
									</div>
									<div class="info" style="width: 100%; height: 80px;">
										<div class="infoContent">
											<h3 class="achievementTitle">
												<b>${item.achievementName }</b>
											</h3>
											<p class="description">${item.introduction }</p>
											<p class="description">
												<b>${item.achievementCategory }</b>推荐&nbsp&nbsp<span
													class="publishTime">发布于：${item.finishTime }</span>
											</p>
											<p class="publishTime" style="float: right; color: gray">浏览量:${item.browseVolume }</p>
										</div>
									</div>
								</div>
							</div>
						</a>
                      </div>
                  </c:forEach>
                  </div>
                  <div class="row">
                  <c:forEach items="${aocscList }" var="item" varStatus="status" begin="4" end="7">
                   <div class="col-xs-3 col-md-3" ">
                  	<a
							href="${pageContext.request.contextPath}/achievement/toDetailCourseExpand?achievementId=${item.achievementId }">
							<div class="chengguo-out"
								style="padding: 0; height: 378px; margin-top: 1%;">
								<div class="doorPlank xiangmu-out"
									style="padding: 1.5rem; background-color: white; height: 100%;width: 91%;">
									<div class="rel-img" style="width: 100%; height: 58%;">
										<img src="${pageContext.request.contextPath}/img/fire.png"
											style="position: absolute; margin-left: 0; margin-top: 0;width: 23px;height: 23px;">
										<img alt="" style="width: 100%; height: 100%;"
											src="${pageContext.request.contextPath}/images/tu2.jpg" />
									</div>
									<div class="info" style="width: 100%; height: 80px;">
										<div class="infoContent">
											<h3 class="achievementTitle">
												<b>${item.achievementName }</b>
											</h3>
											<p class="description">${item.introduction }</p>
											<p class="description">
												<b>${item.achievementCategory }</b>推荐&nbsp&nbsp<span
													class="publishTime">发布于：${item.finishTime }</span>
											</p>
											<p class="publishTime" style="float: right; color: gray">浏览量:${item.browseVolume }</p>
										</div>
									</div>
								</div>
							</div>
						</a>
                      </div>
                  </c:forEach>
                  </div>
              
          </div>
          </c:if>
		<c:if test="${iURPList!=null }">
          <!--产学研项目-->
          <div class="xiangmu" style="padding-left: 20px;padding-bottom: 0%">
            产学研
              <span><a href="">更多</a></span>
              <%-- <table>
                  <tr>
                      <td style="width: 31%;">
                          <div  class="xiangmu-out" style="width: 95%;height:355px;">
                              <div class="rel-img" style="width: 100%; height: 58%;"><img src="${pageContext.request.contextPath}/images/chanxueyan_01.jpg" alt="" style="width: 100%; height: 100%;"></div>
                              <div class="info">
                                  <div class="title" style="margin-top: 10px;">
                                      <p style="font-family: 微软雅黑; font-size: 20px; font-weight: 20px;opacity: 1;">协同创新项目</p>
                                      <p style="margin-bottom: -1px;">团队名称：科研团队</p>
                                      <p style="margin-bottom: -1px;">获奖情况：国家一等奖</p>
                                      <p style="margin-bottom: -1px;">完成时间：2006-12-22</p>
                                  </div>
                              </div>
                          </div>
                      </td>
                      <td style="width: 31%;">
                          <div  class="xiangmu-out" style="width: 95%;height:355px;">
                              <div class="rel-img" style="width: 100%; height: 58%;"><img src="${pageContext.request.contextPath}/images/chanxueyan_02.jpg" alt="" style="width: 100%; height: 100%;"></div>
                              <div class="info">
                                  <div class="title" style="margin-top: 10px;">
                                     <p style="font-family: 微软雅黑; font-size: 20px; font-weight: 20px;opacity: 1;">WISDOM项目</p>
                                      <p style="margin-bottom: -1px;">团队名称：科研团队</p>
                                      <p style="margin-bottom: -1px;">获奖情况：国家二等奖</p>
                                      <p style="margin-bottom: -1px;">完成时间：2006-12-22</p>
                                  </div>
                              </div>
                          </div>
                      </td>
                      <td style="width: 31%;">
                          <div  class="xiangmu-out" style="width: 95%;height:355px;">
                              <div class="rel-img" style="width: 100%; height: 58%;"><img src="${pageContext.request.contextPath}/images/chanxueyan_03.jpg" alt="" style="width: 100%; height: 100%;"></div>
                              <div class="info">
                                  <div class="title" style="margin-top: 10px;">
                                      <p style="font-family: 微软雅黑; font-size: 20px; font-weight: 20px;opacity: 1;">技术转化项目</p>
                                      <p style="margin-bottom: -1px;">团队名称：科研团队</p>
                                      <p style="margin-bottom: -1px;">获奖情况：国家一等奖</p>
                                      <p style="margin-bottom: -1px;">完成时间：2006-12-22</p>
                                  </div>
                              </div>
                          </div>
                      </td>
                  </tr>
                  <tr>
                      <td style="width: 31%;">
                          <div  class="xiangmu-out" style="width: 95%;height:355px;">
                              <div class="rel-img" style="width: 100%; height: 58%;"><img src="${pageContext.request.contextPath}/images/chanxueyan_04.jpg" alt="" style="width: 100%; height: 100%;"></div>
                              <div class="info">
                                  <div class="title" style="margin-top: 10px;">
                                      <p style="font-family: 微软雅黑; font-size: 20px; font-weight: 20px;opacity: 1;">管理创新项目</p>
                                      <p style="margin-bottom: -1px;">团队名称：科研团队</p>
                                      <p style="margin-bottom: -1px;">获奖情况：国家二等奖</p>
                                      <p style="margin-bottom: -1px;">完成时间：2006-12-22</p>
                                  </div>
                              </div>
                          </div>
                      </td>
                      <td style="width: 31%;">
                          <div  class="xiangmu-out" style="width: 95%;height:355px;">
                              <div class="rel-img" style="width: 100%; height: 58%;"><img src="${pageContext.request.contextPath}/images/chanxueyan_05.jpg" alt="" style="width: 100%; height: 100%;"></div>
                              <div class="info">
                                  <div class="title" style="margin-top: 10px;">
                                     <p style="font-family: 微软雅黑; font-size: 20px; font-weight: 20px;opacity: 1;">国家火炬项目</p>
                                      <p style="margin-bottom: -1px;">团队名称：科研团队</p>
                                      <p style="margin-bottom: -1px;">获奖情况：国家三等奖</p>
                                      <p style="margin-bottom: -1px;">完成时间：2006-12-22</p>
                                  </div>
                              </div>
                          </div>
                      </td>--%>
                      <table>
                      <tr>
                      <c:forEach items="${iURPList }" var="item" varStatus="status">
                      
                      <td style="width: 31%;">
                      <a
							href="${pageContext.request.contextPath}/achievement/toDetailIURP?achievementId=${item.projectId }">
                          <div  class="xiangmu-out" style="width: 95%;height:355px;">
                              <div class="rel-img" style="width: 100%; height: 51%;"><img src="${item.firstPicture }" alt="" style="width: 100%; height: 100%;"></div>
                              <div class="info">
                                  <div class="title" style="margin-top: 10px;">
                                      <p style="font-family: 微软雅黑; font-size: 20px; font-weight: 20px;opacity: 1;">科技创新项目</p>
                                      <p style="margin-bottom: -1px;">团队名称：${item.projectName }</p>
                                      <p style="margin-bottom: -1px;">简介：${item.introduction }...</p>
                                      <p style="margin-bottom: -1px;">完成时间：${item.uploadTime }</p>
                                       <p style="margin-bottom: -1px;">浏览量:${item.browseVolume }</p>
                                  </div>
                              </div>
                          </div>
                          </a>
                      </td>
                      
                      </c:forEach>
                  </tr>
              </table> 
              <%-- <div class="col-md-12 bottomColumn" style="min-height: 400px;margin:0px">
				<c:if test="${!empty iURPList}">
					<c:forEach items="${iURPList }" var="item" varStatus="status">
						<a
							href="${pageContext.request.contextPath}/achievement/toDetailIURP?achievementId=${item.projectId }">
							<div class="chengguo-out col-md-4"
								style="padding: 8px; height: 330px; margin-top: 1%;">
								<div class="doorPlank xiangmu-out"
									style="padding: 2.5rem; background-color: white; height: 100%">
									<div class="rel-img">
										<img src="${pageContext.request.contextPath}/img/fire.png"
											style="position: absolute; margin-left: 0; margin-top: 0;    width: 23px;
    height: 23px;">
										<img alt="" style="width: 100%; height: 150px;"
											src="${pageContext.request.contextPath}/jsp/showImg.jsp?path=${item.firstPicture }" />
									</div>
									<div class="info" style="width: 100%; height: 80px;">
										<div class="infoContent">
											<h4 class="achievementTitle titleStyle">
												<b>${item.projectName }</b>
											</h4>
											<p class="description">
												<b>产学研</b>
											</p>
											<p class="description" style="height: 40px">${item.introduction }</p>
											<hr style="margin: 4px">
											<div>
												<p class="publishTime" style="float: left; color: gray">
													发布于：
													<fmt:formatDate value="${item.uploadTime }"
														pattern="yyyy-MM-dd" />
												</p>
												<p class="publishTime" style="float: right; color: gray">浏览量:${item.browseVolume }</p>
											</div>
										</div>
									</div>
								</div>
							</div>
						</a>
					</c:forEach>
				</c:if>
			</div> --%>
          </div>
	</c:if>
          <!--老师排行榜-->
          <div class="teacher" style="height: 857px;">
              教师排行榜
              <span><a href="">更多</a></span>
              <%-- <nav>
                  <ul>
                      <li>
                          <div class="teacher-out">
                              <div class="rel-img"><img src="${pageContext.request.contextPath}/images/touxiang_01.png" alt="" style="width: 73px;height: 73px;"></div>
                              <div class="infor">
                                  <div class="title">
                                      <p>赵忠文</p>
                                      <p>&nbsp;&nbsp;教授</p>
                                  </div>
                              </div>
                          </div>
                      </li>
                      <li>
                          <div class="teacher-out">
                              <div class="rel-img"><img src="${pageContext.request.contextPath}/images/touxiang_02.png" alt="" style="width: 73px;height: 73px;"></div>
                              <div class="infor">
                                  <div class="title">
                                      <p>陈&nbsp;&nbsp;&nbsp;&nbsp;远</p>
                                      <p>&nbsp;&nbsp;教授</p>
                                  </div>
                              </div>
                          </div>
                      </li>
                      <li>
                          <div class="teacher-out">
                              <div class="rel-img"><img src="${pageContext.request.contextPath}/images/touxian_11.png" alt="" style="width: 73px;height: 73px;"></div>
                              <div class="infor">
                                  <div class="title">
                                      <p>周&nbsp;&nbsp;&nbsp;&nbsp;昀</p>
                                      <p>副教授</p>
                                  </div>
                              </div>
                          </div>
                      </li>
                      <li>
                          <div class="teacher-out">
                              <div class="rel-img"><img src="${pageContext.request.contextPath}/images/touxiang_12.png" alt="" style="width: 73px;height: 73px;"></div>
                              <div class="infor">
                                  <div class="title">
                                      <p>崔常山</p>
                                      <p>副教授</p>
                                  </div>
                              </div>
                          </div>
                      </li>
                      <li>
                          <div class="teacher-out">
                              <div class="rel-img"><img src="${pageContext.request.contextPath}/images/touxiang_13.png" alt="" style="width: 73px;height: 73px;"></div>
                              <div class="infor">
                                  <div class="title">
                                      <p>刘纪孔</p>
                                      <p>&nbsp;&nbsp;教授</p>
                                  </div>
                              </div>
                          </div>
                      </li>
                      <li>
                          <div class="teacher-out">
                              <div class="rel-img"><img src="${pageContext.request.contextPath}/images/touxiang_14.png" alt="" style="width: 73px;height: 73px;"></div>
                              <div class="infor">
                                  <div class="title">
                                      <p>陈&nbsp;&nbsp;&nbsp;&nbsp;睿</p>
                                      <p>&nbsp;&nbsp;教授</p>
                                  </div>
                              </div>
                          </div>
                      </li>
                      <li>
                          <div class="teacher-out">
                              <div class="rel-img"><img src="${pageContext.request.contextPath}/images/touxiang_15.png" alt="" style="width: 73px;height: 73px;"></div>
                              <div class="infor">
                                  <div class="title">
                                      <p>刘&nbsp;&nbsp;&nbsp;&nbsp;瑜</p>
                                      <p>&nbsp;&nbsp;教授</p>
                                  </div>
                              </div>
                          </div>
                      </li>
                      <li>
                          <div class="teacher-out">
                              <div class="rel-img"><img src="${pageContext.request.contextPath}/images/touxiang_16.png" alt="" style="width: 73px;height: 73px;"></div>
                              <div class="infor">
                                  <div class="title">
                                      <p>舢&nbsp;&nbsp;&nbsp;&nbsp;板</p>
                                      <p>副教授</p>
                                  </div>
                              </div>
                          </div>
                      </li>

                  </ul>
              </nav> --%>
              
              <iframe style="margin-top: -2px;width:94%; height:99%;"
					src="${pageContext.request.contextPath}/jsp/Teacher/main_teachers.jsp"
					frameborder="0" width="93%" height="87%"
					scrolling="auto"></iframe>
          </div>

          <!--论坛热帖-->
          <div class="post" style="margin-bottom: 55px;">
              热帖
              <span><a href="">更多</a></span>
              <nav>
                  <ul>
                      <li>
                          <div class="post-out"><img src="${pageContext.request.contextPath}/images/tu77.jpg" alt=""><a href="">小梦梦</a> <span>发表于2018年12月11日</span></div>
                          <div class="post-inner">
                              <a href="">面CPP开发内核需要掌握到什么程度？</a> <span>C++课程</span>
                          </div><hr>
                      </li>
                      <li>
                          <div class="post-out"><img src="${pageContext.request.contextPath}/images/tu77.jpg" alt=""><a href="">点点鬼</a> <span>发表于2018年12月13日</span></div>
                          <div class="post-inner">
                              <a href="">请教去重方法 输入是字符串 输出是字符串 谢谢各位大神！</a> <span>C++课程</span>
                          </div><hr>
                      </li>
                      <li>
                          <div class="post-out"><img src="${pageContext.request.contextPath}/images/tu77.jpg" alt=""><a href="">郝海今天发糖了吗？</a> <span>发表于2018年12月15日</span></div>
                          <div class="post-inner">
                              <a href="">求问，视觉算法工程师 需要看什么书？</a> <span>C++课程</span>
                          </div><hr>
                      </li>
                      <li>
                          <div class="post-out"><img src="${pageContext.request.contextPath}/images/tu77.jpg" alt=""><a href="">红酥手</a> <span>发表于2018年12月16日</span></div>
                          <div class="post-inner">
                              <a href="">这句话啥意思，可以举个例子吗？</a> <span>C/C++课程</span>
                          </div><hr>
                      </li>
                      <li>
                          <div class="post-out"><img src="${pageContext.request.contextPath}/images/tu77.jpg" alt=""><a href="">Believer123</a> <span>发表于2018年12月17日</span></div>
                          <div class="post-inner">
                              <a href="">有二维数组A[10][20]，每个元素占4个存储单元。。。</a> <span>C/C++课程</span>
                          </div><hr>
                      </li>
                      <li>
                          <div class="post-out"><img src="${pageContext.request.contextPath}/images/tu77.jpg" alt=""><a href="">yzpseu</a> <span>发表于2018年12月17日</span></div>
                          <div class="post-inner">
                              <a href="">Java并发编程的艺术思维导图</a> <span>JAVA课程</span>
                          </div><hr>
                      </li>
                      <li>
                          <div class="post-out"><img src="${pageContext.request.contextPath}/images/tu77.jpg" alt=""><a href="">董成荣</a> <span>发表于2018年12月20日</span></div>
                          <div class="post-inner">
                              <a href="">python中那个API可以让x轴自动伸缩？</a> <span>Python课程</span>
                          </div><hr>
                      </li>
                      <li>
                          <div class="post-out"><img src="${pageContext.request.contextPath}/images/tu77.jpg" alt=""><a href="">燕舞s</a> <span>发表于2018年12月21日</span></div>
                          <div class="post-inner">
                              <a href="">公司的统计的数据库里字段全是数字怎么办？</a> <span>数据库课程</span>
                          </div><hr>
                      </li>
                  </ul>
              </nav>
          </div>
      </main>
       <jsp:include page="/jsp/footer.jsp" flush="true"/>
</body>
</html>