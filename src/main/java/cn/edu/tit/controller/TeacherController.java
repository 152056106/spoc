package cn.edu.tit.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.xpath.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import cn.edu.tit.bean.Accessory;
import cn.edu.tit.bean.Achievement;
import cn.edu.tit.bean.Attendance;
import cn.edu.tit.bean.Category;
import cn.edu.tit.bean.Course;
import cn.edu.tit.bean.CourseExpand;
import cn.edu.tit.bean.IndustryUniversityResearchProject;
import cn.edu.tit.bean.Paper;
import cn.edu.tit.bean.Prize;
import cn.edu.tit.bean.RealClass;
import cn.edu.tit.bean.Resource;
import cn.edu.tit.bean.ResourceType;
import cn.edu.tit.bean.Student;
import cn.edu.tit.bean.Task;
import cn.edu.tit.bean.Teacher;
import cn.edu.tit.bean.TeacherProject;
import cn.edu.tit.bean.Term;
import cn.edu.tit.bean.UpTask;
import cn.edu.tit.bean.VirtualClass;
import cn.edu.tit.common.Common;
import cn.edu.tit.iservice.IAchievementService;
import cn.edu.tit.iservice.IAdminService;
import cn.edu.tit.iservice.IResourceService;
import cn.edu.tit.iservice.IStudentService;
import cn.edu.tit.iservice.ITeacherService;
import net.sf.json.JSONArray;

@RequestMapping("/teacher")
@Controller
public class TeacherController {
	/**
	 * 声明变量
	 * */
	@Autowired
	private ITeacherService teacherService;
	@Autowired
	private IStudentService studentService;
	@Autowired
	public MainController mainController;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IAchievementService iAchievementService;

	private static List<Category> categories = null;//将  分类 信息作为全局变量，避免多次定义,在首次登陆教师页面时 在  方法teacherCourseList（） 处即初始化成功

	@RequestMapping(value="teacherLogin",method= {RequestMethod.GET})
	public ModelAndView teacherLogin(@RequestParam("employeeNum")String teacherId,@RequestParam("password")String password,HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		String teacherSessionId = (String) request.getSession().getAttribute("teacherId");
		/*
		 * if(teacherId.equals(teacherSessionId)) {
		 * request.getSession().setAttribute("readResult", "异地登陆异常");//返回信息
		 * System.out.println("异地登录"); mv.setViewName("/jsp/Teacher/index");//设置返回页面
		 * }else {
		 */
		
			request.getSession().invalidate();//顺序不能乱
			String identify = "teacher";
			request.getSession().setAttribute("identify", identify);
			String readResult =null;
			request.getSession().setAttribute("teacherId", null);
			String teacherPassword = null;
			try {
				Teacher teacher = teacherService.teacherLoginByEmployeeNum(teacherId);
				teacherPassword = Common.eccryptMD5(password);
				if(teacherPassword.equals(teacher.getTeacherPassword()) )
				{	
					request.getSession().setAttribute("teacherId", teacher.getEmployeeNum());
					request.getSession().setAttribute("teacher", teacher);
					mv=toTeacherPage(request); //去首页
					//清空其他身份SESSION
					
					request.getSession().removeAttribute("student");
					request.getSession().removeAttribute("studentId");
					
					request.getSession().setAttribute("readResult", null);
					mv.addObject("teacher",teacher);
				}
				else {
					request.getSession().setAttribute("readResult", "密码错误！");//返回信息

					mv.setViewName("/jsp/Teacher/index");//设置返回页面
				}
			} catch (Exception e) {
				request.getSession().setAttribute("readResult", "用户名错误！");//返回信息
					mv.setViewName("/jsp/Teacher/index");//设置返回页面

				e.printStackTrace();
			}
		/*
		 * }
		 */
		return mv;	
	}


	/**
	 * @author LiMing
	 * @param request
	 * @return
	 * @throws Exception 
	 * 课程二级页面
	 */
	@RequestMapping(value="courseList",method= {RequestMethod.GET})
	public ModelAndView toCourseSecond(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		categories = teacherService.readCategory();
		List<Course> list = new ArrayList<Course>();
		List<String> publishTime = new ArrayList<>();
		List<Teacher> teacherList = new ArrayList<>();
		list = teacherService.readCourse(null);
		if(!list.isEmpty()) {
			for (Course course : list) 
			{
				teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
				course.setTeacherList(teacherList);
				publishTime.add(course.getPublishTime().toString().substring(0,10));
			}
		}
		mv.addObject("categories", categories);
		mv.addObject("courseList", list);
		mv.addObject("publishTime",publishTime);
		mv.addObject("teacherList", teacherList);
		mv.setViewName("/jsp/CourseJsp/courseSecond");
		return mv;
	}

	/**
	 * 跳转到教师的课程详细页面
	 * @param request
	 * @return
	 */
	@RequestMapping(value="toCourseDetail/{courseId}")
	public String toCourseDetail(HttpServletRequest request, @PathVariable String courseId){
		// 通过courseid查询课程
		Course course = null;
		try {
			course = teacherService.getCourseById(courseId);
			request.getSession().setAttribute("course", course);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 查询教师圈教师信息
		List<Teacher> teacherList = teacherService.getTeachersByCourseId(courseId);
		request.getSession().setAttribute("teacherList", teacherList); //通过存入request在前台访问
		//request.getSession().setAttribute("course", course);
		// 查出操作者是否是manager
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		if(teacher != null){
			//查manager
			Integer manager = teacherService.getManagerByEmployeeNum(teacher.getEmployeeNum(), courseId,1);
			request.setAttribute("manager", manager);
			//查当前用户是否关注课程
			Integer attention = teacherService.getManagerByEmployeeNum(teacher.getEmployeeNum(), courseId,2);
			request.setAttribute("attention", attention);
		}

		//查操作者是否是学生
		Student student = (Student) request.getSession().getAttribute("student");
		if(student != null){
			//查出所在虚拟班级信息
			VirtualClass virtualClass = teacherService.getVirtualClassByRidAndCid(student.getClassNum(), courseId);
			request.setAttribute("virtualClass", virtualClass);
			//查当前用户是否关注课程
			Integer attention = studentService.getManagerByStudentId(student.getStudentId(), courseId,2);
			request.setAttribute("attention", attention);
		}
		request.setAttribute("student", student);
		//查询课程类别名
		String category = teacherService.getCategoryById(course.getCourseCategory());
		request.setAttribute("category", category);
		
		//修改课程创建时间格式
		String publishTime = course.getPublishTime().toString().substring(0, 10);
		request.setAttribute("publishTime", publishTime);

		try {
			//获取多媒体资源
			List<Resource> mediaRList = resourceService.showResourceByCourse(courseId);
			for (Resource resource : mediaRList) {
				resource.setPublisherId(teacherService.getTeacherNameById(resource.getPublisherId()));
			}
			if(!mediaRList.isEmpty())
				request.setAttribute("resource", mediaRList);//返回信息

			//作业库
			List<Task> taskList = teacherService.getTaskByPointAndCourse("work",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
			}
			if(!taskList.isEmpty())
				request.setAttribute("taskList", taskList);//返回信息

			//实验库
			List<Task> trialList = teacherService.getTaskByPointAndCourse("trial",courseId);
			for (Task task : trialList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
			}
			if(!trialList.isEmpty())
				request.setAttribute("taskList", trialList);//返回信息

			//获取课程成果
			List<CourseExpand> aocscList  = iAchievementService.queryCourseExpandByCourseId(courseId);
			if(!aocscList.isEmpty())
				request.setAttribute("aocscList", aocscList);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}



		return "jsp/Teacher/course_detail";
	}

	/**
	 * 跳转到创建课程界面
	 * @param request
	 * @param employeeNum
	 * @return
	 */
	@RequestMapping("toCreateCourse")
	public String toCreateCourse(HttpServletRequest request){
		try {
			List<Category> categoryList =  teacherService.readCategory();
			List<Teacher> teacherList = new ArrayList<>();
			Teacher teach = (Teacher) request.getSession().getAttribute("teacher");
			String employeeNum = teach.getEmployeeNum();
			for(Teacher teacher : teacherService.getTeachers()){
				if(!employeeNum.equals(teacher.getEmployeeNum())){ // 在选择的教师中过滤掉当前的操作者
					teacherList.add(teacher);
				}
			}
			request.getSession().setAttribute("categoryList", categoryList);
			request.getSession().setAttribute("teacherList", teacherList);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "jsp/Teacher/createCourse";
	}

	/**
	 * 通过ajax获取教师列表
	 */
	@RequestMapping(value="ajaxGetTeachers")
	public void ajaxGetTeachers(HttpServletRequest request, HttpServletResponse response){
		try {

			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=UTF-8");
			List<Teacher> teacherList = new ArrayList<>();
			Teacher teach = (Teacher) request.getSession().getAttribute("teacher");
			String employeeNum = teach.getEmployeeNum();
			for(Teacher teacher : teacherService.getTeachers()){
				if(!employeeNum.equals(teacher.getEmployeeNum())){ // 在选择的教师中过滤掉当前的操作者
					teacherList.add(teacher);
				}
			}
			JSONArray  json  =  JSONArray.fromObject(teacherList); 
			String result = json.toString();
			response.getWriter().print(result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过ajax判断课程是否重复
	 */
	@RequestMapping(value="isRepeatCourse/{courseName}")
	public void isRepeatCourse(HttpServletRequest request, HttpServletResponse response, @PathVariable String courseName){
		try {
			String result = null;
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=UTF-8");
			//查询课程
			List<Course> course  = teacherService.getCourseByName(courseName);
			if(course != null && !course.isEmpty()){
				 result  =  JSONObject.toJSONString("此课程已存在，不能重复发布，请修改课程名！"); 
			}
			response.getWriter().print(result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过ajax查找教师列表
	 */
	@RequestMapping(value="ajaxSearchTeachers")
	public void ajaxSearchTeachers(HttpServletRequest request, HttpServletResponse response){
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=UTF-8");
			String employee = request.getParameter("employeeNum"); //获取查询关键字
			List<Teacher> teachers = teacherService.vagueSearchTeachers(employee);
			List<Teacher> teacherList = new ArrayList<>();
			Teacher teach = (Teacher) request.getSession().getAttribute("teacher");
			String employeeNum = teach.getEmployeeNum();
			for(Teacher teacher : teachers){
				if(!employeeNum.equals(teacher.getEmployeeNum())){ // 在选择的教师中过滤掉当前的操作者
					teacherList.add(teacher);
				}
			}
			JSONArray  json  =  JSONArray.fromObject(teacherList); 
			String result = json.toString();
			response.getWriter().print(result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 用户关注课程
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="ajaxAttentionCourse")
	public void ajaxAttentionCourse(HttpServletRequest request, HttpServletResponse response){
		String result = "";
		//判断是否登录
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		Student student = (Student) request.getSession().getAttribute("student");
		String courseId = request.getParameter("courseId");
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=UTF-8");
			//,@RequestParam(value="courseId")String courseId
			if(teacher == null && student == null){
				result = JSONObject.toJSONString("请先登录");
			}
			//如果登录，则关注课程
			if(teacher != null){
				teacherService.teacherAttentionCourse(courseId, teacher.getEmployeeNum()); 
				result = JSONObject.toJSONString("关注成功！");
			}
			else if(student != null){
				studentService.studentAttentionCourse(courseId, student.getStudentId());
				result = JSONObject.toJSONString("关注成功！");
			}
			response.getWriter().println(result);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			if(teacher != null){
				teacherService.teacherExitAttentionCourse(courseId, teacher.getEmployeeNum()); 
			}
			else if(student != null){
				studentService.studentExitAttentionCourse(courseId, student.getStudentId());
			}
			result = JSONObject.toJSONString("取消关注成功！");
		}
		try {
			response.getWriter().println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}




	/**
	 * @author LiMing
	 * @param request
	 * @return
	 * 跳转到创建虚拟班级
	 * @throws Exception 
	 */
	@RequestMapping(value="toCreateVirtualClass")
	public ModelAndView toCreateVirtualClass(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		List<Term> listTerm = new ArrayList<Term>();
		List<RealClass> listRealClass = new ArrayList<RealClass>();
		List<Course> courseList = null;
		List<String> courseIdList = null;
		try {
			listTerm = teacherService.readTerm();
			listRealClass = teacherService.readRealClass(null);
			courseIdList =teacherService.courseIdList((String) request.getSession().getAttribute("teacherId"), 0);
			courseIdList.addAll(teacherService.courseIdList((String) request.getSession().getAttribute("teacherId"), 1));
			courseList = teacherService.courseList(courseIdList);
			mv.addObject("courseList", courseList);
			mv.addObject("listTerm",listTerm);
			mv.addObject("listRealClass", listRealClass);
			mv.setViewName("/jsp/CourseJsp/createVirtual");
		} catch (Exception e) {
			mv = toTeacherPage(request);
		}
		return mv;
	}
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 去编辑虚拟班级
	 */
	@RequestMapping(value="toEditVirtualClass")
	public ModelAndView toEditVirtualClass(HttpServletRequest request) {


		ModelAndView mv = new ModelAndView();
		List<Term> listTerm = new ArrayList<Term>();
		List<RealClass> listRealClass = new ArrayList<RealClass>();
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		VirtualClass virtualClass = teacherService.getVirtualById(virtualClassNum);
		String virtualClassName = (String) request.getSession().getAttribute("virtualClassName");
		String creator = (String) request.getSession().getAttribute("teacherId");
		Course course = (Course) request.getSession().getAttribute("course");
		try {
			listTerm = teacherService.readTerm();
			listRealClass = teacherService.getRealClassList(virtualClassNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mv.addObject("listTerm",listTerm);
		mv.addObject("listRealClass", listRealClass);
		mv.addObject("virtualClassNum", virtualClassNum);
		mv.addObject("virtualClassName", virtualClassName);
		mv.addObject("creator", creator);
		mv.addObject("course", course);
		mv.setViewName("/jsp/Teacher/editVirtualClass");
		return mv;
	}

	/**
	 * 跳转到课程简介详细模块
	 * @param request
	 * @param courseId
	 * @return
	 */
	@RequestMapping(value="toCourseIntroduce/{courseId}")
	public String toCourseIntroduce(HttpServletRequest request, @PathVariable String courseId){
		try {
			//根据id查询课程
			Course course = teacherService.getCourseById(courseId);
			request.setAttribute("courseDetail", course.getCourseDetail());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "jsp/Teacher/lesson-introduce";
	}
	
	
	
	/**
	 * 创建课程
	 * @return
	 */
	@RequestMapping(value="createCourse")
	@SuppressWarnings({ "unused", "unchecked" })
	public ModelAndView createCourse(HttpServletRequest request){
		try {
			String courseId = Common.uuid();
			Object[] obj = Common.fileFactory(request,courseId);
			List<File> files = (List<File>) obj[0];	// 获取课程图片
			Map<String, Object> formdata = (Map<String, Object>) obj[1]; // 获取课程内容
			//封装课程类
			Course course = new Course();
			course.setCourseId(courseId);
			course.setCourseName((String)formdata.get("courseName"));
			course.setCourseDetail((String)formdata.get("courseDetail"));
			course.setCourseCategory((String)formdata.get("courseCategory"));
			Timestamp publishTime = new Timestamp(System.currentTimeMillis());
			course.setPublishTime(publishTime);
			String employeeNum = (String)formdata.get("publisherId");
			String teacherStr = (String)formdata.get("selectTeacher");
			String[] teachers = teacherStr.split(",");
			course.setPublisherId(employeeNum);
			//course.setFaceImg(Common.readProperties("path")+"/"+courseId+"/"+employeeNum+"/"+files.get(0).getName());
			for(File f : files){ // 集合中只有一张图片
				String p = Common.readProperties("pre") +f.getPath().replaceAll("\\\\", "/").substring(3);
				course.setFaceImg(p);
			}
			course.setCourseStudentNum(0);
			teacherService.createCourse(course); // 添加课程
			teacherService.addOtherToMyCourse(employeeNum, courseId, 1);//把课程创建者初始化到教师圈
			//通过课程id和获取教师圈的id集合绑定教师到课程
			if(teachers != null){
				for(int i = 0; i < teachers.length; i++){
					teacherService.addOtherToMyCourse(teachers[i], courseId, 0);
				}
			}
			return toTeacherPage(request);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}

	}

	
	
	
	/**
	 * @author LiMing
	 * 创建虚拟班级
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	@RequestMapping(value="createVirtualClass",method = RequestMethod.POST)
	public ModelAndView createVirtualClass(HttpServletRequest request) throws Exception{	
		ModelAndView mv = new ModelAndView();
		VirtualClass vir = new VirtualClass();
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		Timestamp publishTime = new Timestamp(System.currentTimeMillis());
		String courseID = null;
		try {
			String virId = Common.uuid();
			Object[] obj = Common.fileFactory(request,virId);
			List<File> files = (List<File>) obj[0];	// 获取课程图片
			Map<String, Object> formdata = (Map<String, Object>) obj[1]; // 获取课程内容
			String selectTerm = (String)formdata.get("selectTerm");
			String realClassContent =  (String)formdata.get("selectClass");
			String className = (String)formdata.get("className");
			List<RealClass> realClassList = new ArrayList<RealClass>();
			List<String> realClassArray = new ArrayList<String>();	
			String courseId = (String)formdata.get("courseId");
			courseID = courseId;
			String[] sourceStrArray = realClassContent.split(",");
			for(int i = 0;i<sourceStrArray.length;i++) {
				if(!sourceStrArray[i].isEmpty())
				{
					realClassArray.add(sourceStrArray[i]);
				}
			}
			int count = 0;//班级总人数
			//将所有班级号转化为对应对象
			for (String string : realClassArray) {
				realClassList.add(teacherService.readRealClass(string).get(0));//查询出的始终只有一个
			}
			//计算总人数
			for (RealClass realClass : realClassList) {
				count+= realClass.getRealPersonNum();
			}
			vir.setVirtualClassNum(virId);
			vir.setCourseId(courseId);
			vir.setCreateTime(publishTime);
			vir.setCreatorId(teacher.getEmployeeNum());
			vir.setVirtualCourseName(teacherService.getCourseById(courseId).getCourseName());
			vir.setVirtualClassName(className);
			vir.setTerm(selectTerm);
			vir.setClassStuentNum(count);
			//修改课程总人数
			teacherService.updateCourseStudentNum(count,courseId);
			if(files.isEmpty()||files==null||files.size()==0)
			{
				vir.setFaceImg(null);

			}else {
				vir.setFaceImg(Common.readProperties("pre") +files.get(0).getPath().replaceAll("\\\\", "/").substring(3));
				//vir.setFaceImg(Common.readProperties("path")+"/"+virId+"/"+files.get(0).getName());
			}
			vir.setRealClassList(realClassList);
			teacherService.createVirtualClass(vir);
			/**********实体班和虚拟班的对应***************/
			for (int i = 0; i < realClassArray.size(); i++) {
				teacherService.mapVirtualRealClass(realClassArray.get(i),virId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mv = toCreateVirtualClass(request);
		} 
		return toTeacherPage(request);//创建虚拟班级成功返回到课程三级页面
	}
	///**
	//* @author LiMing
	//* 创建虚拟班级
	//* @throws Exception 
	//*/
	//@SuppressWarnings("unused")
	//@RequestMapping(value="createVirtualClass",method = RequestMethod.POST)
	//public String createVirtualClass(HttpServletRequest request,@RequestParam("className")String className,@RequestParam("selectTerm")String selectTerm,@RequestParam("realClassContent")String realClassContent) throws Exception{	
	//	Course course = (Course) request.getSession().getAttribute("virtualCourse");
	//	String courseId = course.getCourseId();
	//	String courseName = course.getCourseName();
	//	VirtualClass vir = new VirtualClass();
	//	try {
	//		//设置对象的属性
	//		vir.setCourseId(courseId);
	//		String uuid = Common.uuid();
	//		Timestamp publishTime = new Timestamp(System.currentTimeMillis());
	//		vir.setCreateTime(publishTime);
	//		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
	//		vir.setCreatorId(teacher.getEmployeeNum());
	//		vir.setFaceImg(course.getFaceImg());
	//		vir.setVirtualClassNum(uuid);
	//		vir.setTerm(selectTerm);
	//		vir.setVirtualCourseName(course.getCourseName());
	//		System.out.println(selectTerm+"~~~~~~~~~~~~~~~~~~~~~");
	//		//将前台得到的字符串分割
	//		String[] sourceStrArray = realClassContent.split(",");
	//		List<String> realClassArray = new ArrayList<String>();
	//		//字符串数组判空
	//		for(int i = 0;i<sourceStrArray.length;i++) {
	//			if(!sourceStrArray[i].isEmpty())
	//			{
	//				realClassArray.add(sourceStrArray[i]);
	//			}
	//		}
	//		List<RealClass> realClassList = new ArrayList<RealClass>();
	//		int count = 0;//班级总人数
	//		//将所有班级号转化为对应对象
	//		for (String string : realClassArray) {
	//			realClassList.add(teacherService.readRealClass(string).get(0));//查询出的始终只有一个
	//		}
	//		//计算总人数
	//		for (RealClass realClass : realClassList) {
	//			count+= realClass.getRealPersonNum();
	//		}
	//		vir.setVirtualClassName(className);
	//		vir.setClassStuentNum(count);
	//		vir.setRealClassList(realClassList);
	//		teacherService.createVirtualClass(vir);
	//		/**********实体班和虚拟班的对应***************/
	//		for (int i = 0; i < realClassArray.size(); i++) {
	//			teacherService.mapVirtualRealClass(realClassArray.get(i),uuid);
	//		}
	//	} catch (Exception e) {
	//		e.printStackTrace();
	//	} 
	//	return toCourseDetail(request,courseId);//创建虚拟班级成功返回到课程三级页面
	//}

	/**
	 * @author LiMing
	 * @param request
	 * @return
	 * @throws Exception 
	 * 课程二级页面查询
	 */
	@RequestMapping(value="toCourseSecondSearch")
	public ModelAndView toCourseSecondSearch(HttpServletRequest request,@RequestParam("search")String search) throws Exception {
		ModelAndView mv = new ModelAndView();
		categories = teacherService.readCategory();
		List<Course> list = new ArrayList<Course>();
		List<String> teacherNames = new ArrayList<String>();
		List<String> publishTime = new ArrayList<>();
		list = teacherService.searchCourse(search);
		if(list !=null && !list.isEmpty()) {
			for (Course course : list) 
			{
				teacherNames.add(teacherService.getTeacherNameById(course.getPublisherId()));
				publishTime.add(course.getPublishTime().toString().substring(0,10));
			}
		}
		mv.addObject("categories", categories);
		mv.addObject("courseList", list);
		mv.addObject("teacherNames", teacherNames);
		mv.addObject("publishTime",publishTime);
		mv.setViewName("/jsp/CourseJsp/courseSecond");
		return mv;
	}
	
	/**
	 * 修改课程
	 * @return
	 */
	@RequestMapping(value="modifyCourse/{courseId}")
	@SuppressWarnings({ "unused", "unchecked" })
	public String modifyCourse(HttpServletRequest request,@PathVariable String courseId){
		try {
			Object[] obj = Common.fileFactory(request,courseId);
			List<File> files = (List<File>) obj[0];	// 获取课程图片
			Map<String, Object> formdata = (Map<String, Object>) obj[1]; // 获取课程内容
			//封装课程类
			Course course = new Course();
			course.setCourseId(courseId);
			course.setCourseName((String)formdata.get("courseName"));
			course.setCourseDetail((String)formdata.get("courseDetail"));
			course.setCourseCategory((String)formdata.get("courseCategory"));
			Timestamp publishTime = new Timestamp(System.currentTimeMillis());
			course.setPublishTime(publishTime);
			String employeeNum = (String)formdata.get("publisherId");
			course.setPublisherId(employeeNum);
			if(!files.isEmpty()){
				for(File f : files){
					course.setFaceImg(Common.readProperties("path")+"/"+f.getName());
				}
			}else{
				//如果课程图片未被修改则保留原始路径
				String path = teacherService.getImgpathByCourseId(courseId);
				if(path.isEmpty()){
					path = Common.readProperties("path")+"/1.png";
				}
				course.setFaceImg(path);
			}
			teacherService.updateCourse(course); // 修改课程
			/*teacherService.addOtherToMyCourse(employeeNum, courseId, 1);//把课程创建者初始化到教师圈
			//通过课程id和获取教师圈的id集合绑定教师到课程
			if(teachers != null){
				for(int i = 0; i < teachers.length; i++){
					teacherService.addOtherToMyCourse(teachers[i], courseId, 0);
				}
			}*/
			return toCourseDetail( request,courseId);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 跳转到修改课程页面
	 * @param request
	 * @param courseId
	 * @return
	 */
	@RequestMapping(value="toModifyCourse/{courseId}")
	public ModelAndView toModifyCourse(HttpServletRequest request, @PathVariable String courseId){
		ModelAndView mv = new ModelAndView();
		try {
			// 查出课程信息
			Course course = teacherService.getCourseById(courseId);
			if(course != null){
				Teacher teacher = (Teacher) request.getSession().getAttribute("teacher"); //从session中获取教师工号
				if(teacher == null){
					mv.addObject("readResult", "请先登录");//返回信息
					mv.setViewName("/jsp/Teacher/index");//设置返回页面
					return mv;
				}
				//查找所有系部列表
				List<Category> categoryList =  teacherService.readCategory();
				mv.addObject("categoryList",categoryList);
				mv.addObject("course",course);
				mv.setViewName("jsp/Teacher/modifyCourse");
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}


	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 去添加任务页面
	 */
	@RequestMapping(value="toPublishTask")
	@SuppressWarnings({ "unused", "unchecked" })
	public String toPublishTask(HttpServletRequest request) {
		Course course;
		List<String> taskCategoryList=null;
		course = (Course) request.getSession().getAttribute("course");
		try {
			taskCategoryList = teacherService.getTaskCategory();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.getSession().setAttribute("taskCategoryList", taskCategoryList);
		request.getSession().setAttribute("courseId", course.getCourseId());
		return "/jsp/Teacher/publish-work";
	}

	/**
	 *直接一次性查出所有的数据,返回给前端，bootstrap-table自行分页
	 */
	@RequestMapping("/getTaskListPage")
	@ResponseBody
	public List<Task> getTaskListPage(HttpServletRequest request,@RequestParam(value = "taskCategory")String taskCategory){
		List<Task> list = new ArrayList<Task>();
		String courseId = (String) request.getSession().getAttribute("courseId");
		list = teacherService.getTaskListPage(courseId, taskCategory);
		return list;
	}

	@RequestMapping(value="toselectTaskList")
	public ModelAndView toselectTaskList(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/jsp/Teacher/selectTaskList");
		return  mv;
	}
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 发布任务
	 */
	@RequestMapping(value="publishTask")
	@SuppressWarnings({ "unused", "unchecked" })
	public String publishTask(HttpServletRequest request) {
		String taskId =  Common.uuid();	//设置任务id

		Object[] obj = Common.fileFactory(request,taskId);
		Map<String, Object> formdata = (Map<String, Object>) obj[1];
		List<File> returnFileList = (List<File>) obj[0]; // 要返回的文件集合
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		String virtualClassName = (String) request.getSession().getAttribute("virtualClassName");

		// 创建list集合用于获取文件上传返回路径名
		List<String> list = new ArrayList<String>();
		List<Accessory> accessories  = new ArrayList<Accessory>();
		List<Resource> resources = new ArrayList<Resource>();
		Task task=new Task();
		task.setTaskId(taskId);
		task.setTaskTitle((String) formdata.get("taskTitle"));
		task.setTaskDetail((String) formdata.get("taskDetail"));
		//task.setTaskEndTime(Timestamp.valueOf((String) formdata.get("taskEndTime")));
		//	task.setTaskType((String) formdata.get("taskType"));
		task.setPublisherId((String) request.getSession().getAttribute("teacherId"));
		task.setPublishTime(new Timestamp(System.currentTimeMillis()));
		task.setVirtualClassNum(virtualClassNum);
		task.setKnowledgePoints((String) formdata.get("knowledgePoints"));
		task.setUseNum(1);//设置使用次数为1
		//获取修改使用次数-虚拟班级人数
		/*
		 * int taskUseNum = teacherService.getTaskUserNum(virtualClassNum);
		 * task.setWatchNum(taskUseNum);
		 */
		task.setWatchNum(0);
		task.setCourseId((String) request.getSession().getAttribute("courseId"));
		System.out.println("作业类型是："+(String) formdata.get("taskCategory"));
		task.setTaskType((String) formdata.get("taskCategory"));
		task.setStatus(0);

		try {
			teacherService.createTask(task);		//创建任务
			if ("turn_class".equals(formdata.get("taskCategory"))) {
				teacherService.mapClassTaskToTurnClass(task.getVirtualClassNum(), taskId,Timestamp.valueOf((String) formdata.get("taskEndTime")),Timestamp.valueOf((String) formdata.get("preTaskEndTime")));		//映射班级任务表
				
			}else {
				teacherService.mapClassTask(task.getVirtualClassNum(), taskId,Timestamp.valueOf((String) formdata.get("taskEndTime")));		//映射班级任务表
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!returnFileList.isEmpty()) {
			for (File file : returnFileList) {
				Accessory accessory = new Accessory();
				String fileName="";
				try {
					fileName = new String(file.getName().getBytes("UTF-8"),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				accessory.setAccessoryName(fileName);
				System.out.println("name=================="+fileName);
				accessory.setAccessoryPath(file.getPath());
				accessory.setTaskId(taskId);
				accessory.setAccessoryTime(Common.TimestamptoString());
				accessories.add(accessory);
				Resource resource = new Resource();
				resource.setResourceId(Common.uuid());
				resource.setResourceName(file.getName());
				resource.setResourceDetail(null);
				resource.setPublishTime(new Timestamp(System.currentTimeMillis()));
				resource.setPublisherId((String) request.getSession().getAttribute("teacherId"));
				resource.setResourceTypeId(Common.fileType(file.getName(), teacherService));//需要判断文件类型
				resource.setResourcePath(file.getPath());
				resource.setCourseId((String) request.getSession().getAttribute("courseId"));
				resource.setSize(file.length()/1024.0+"KB");
				resources.add(resource);
			}
			try {
				teacherService.addAccessory(accessories);	//添加任务附件
				resourceService.upLoadResource(resources);//添加资源
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		request.getSession().removeAttribute("courseId");
		String courseName = (String) request.getSession().getAttribute("courseName");
		String url=null;
		try {
			url =  "redirect:/teacher/toClassDetail?virtualClassNum="+ URLEncoder.encode(virtualClassNum,"UTF-8")+"&virtualClassName="+URLEncoder.encode(virtualClassName,"UTF-8")+"&courseName="+URLEncoder.encode(courseName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}
	
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 修改任务资源
	 */
	@RequestMapping(value="updateTask")
	@SuppressWarnings({ "unused", "unchecked" })
	public ModelAndView updateTask(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		Object[] obj = Common.fileFactory(request,null);
		Map<String, Object> formdata = (Map<String, Object>) obj[1];
		List<File> returnFileList = (List<File>) obj[0]; // 要返回的文件集合
		
		// 创建list集合用于获取文件上传返回路径名
		List<String> list = new ArrayList<String>();
		List<Accessory> accessories  = new ArrayList<Accessory>();
		List<Resource> resources = new ArrayList<Resource>();
		Task task=new Task();
		task.setTaskId((String) formdata.get("taskId"));
		task.setTaskTitle((String) formdata.get("taskTitle"));
		task.setTaskDetail((String) formdata.get("taskDetail"));
		//task.setTaskEndTime(Timestamp.valueOf((String) formdata.get("taskEndTime")));
		//	task.setTaskType((String) formdata.get("taskType"));
		task.setPublisherId((String) request.getSession().getAttribute("teacherId"));
		task.setPublishTime(new Timestamp(System.currentTimeMillis()));
		task.setCourseId((String) request.getSession().getAttribute("courseId"));

		try {
			teacherService.updateTask(task);		//修改任务

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!returnFileList.isEmpty()) {
			for (File file : returnFileList) {
				Accessory accessory = new Accessory();
				String fileName="";
				try {
					 fileName = new String(file.getName().getBytes("UTF-8"),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				accessory.setAccessoryName(fileName);
				System.out.println("name=================="+fileName);
				accessory.setAccessoryPath(file.getPath());
				accessory.setTaskId((String) formdata.get("taskId"));
				accessory.setAccessoryTime(Common.TimestamptoString());
				accessories.add(accessory);
			}
			try {
				teacherService.updateAccessory(accessories);	//修改任务附件
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}

		try {
			String a = (String) formdata.get("taskType");
			if("work".equals(a)){
				a = "8";
			}
			if("trial".equals(a)){
				a = "9";
			}
			if("course_design ".equals(a)){
				a = "10";
			}
			return toCourseResource(request,a );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			request.getSession().setAttribute("img", "error");
			mv.setViewName("jsp/Teacher/teacher-update-taskResource");
			return mv;
		}

	}

	@RequestMapping(value="selectTaskToPublish")
	public String selectTaskToPublish(HttpServletRequest request) {
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		String virtualClassName = (String) request.getSession().getAttribute("virtualClassName");
		
		String taskId = request.getParameter("taskId");
		String taskEndTime = request.getParameter("taskEndTime");
		String preTaskEndTime = request.getParameter("preTaskEndTime");
		//获取修改使用次数
		//int taskUseNum = teacherService.getTaskUserNum(virtualClassNum);
		try {
			if ("turn_class".equals(request.getParameter("taskCategory"))) {
				teacherService.mapClassTaskToTurnClass(virtualClassNum, taskId,Timestamp.valueOf(taskEndTime),Timestamp.valueOf(preTaskEndTime));		//映射班级任务表
				
			}else {
				teacherService.mapClassTask(virtualClassNum, taskId,Timestamp.valueOf(taskEndTime));
				
			}
			teacherService.addUseNum(taskId);
			//teacherService.addWatchNum(taskId, taskUseNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		//映射班级任务表
		String courseName = (String) request.getSession().getAttribute("courseName");
		System.out.println("选择发布"+virtualClassName+"sdfghjkl;"+courseName);
		System.out.println("redirect:/teacher/toClassDetail?virtualClassNum="+virtualClassNum+"&virtualClassName="+virtualClassName+"&courseName="+courseName);
		
		String url=null;
		try {
			url =  "redirect:/teacher/toClassDetail?virtualClassNum="+ URLEncoder.encode(virtualClassNum,"UTF-8")+"&virtualClassName="+URLEncoder.encode(virtualClassName,"UTF-8")+"&courseName="+URLEncoder.encode(courseName,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * 跳转到修改资源页面
	 * @param request
	 * @param resourceId
	 * @param category
	 * @return
	 */
	@RequestMapping(value="toUpdateResource/{resourceId}")
	public ModelAndView toUpdateResource(HttpServletRequest request,@PathVariable String resourceId){
		ModelAndView mv = new ModelAndView();
		try {
			//获得对应的资源
			List<Resource> resource = resourceService.showResource(resourceId);
			//mv.addObject("category", category);
			if(!resource.isEmpty() && resource != null)
				mv.addObject("resource", resource.get(0));
			mv.setViewName("/jsp/Teacher/teacher-update-resource");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}
	
	/**
	 * 跳转到修改资源页面
	 * @param request
	 * @param resourceId
	 * @param category
	 * @return
	 */
	@RequestMapping(value="toSearchResource/{resourceId}")
	public ModelAndView toSearchResource(HttpServletRequest request,@PathVariable String resourceId){
		ModelAndView mv = new ModelAndView();
		Course course = (Course) request.getSession().getAttribute("course");
		//查询课程类别名
		if(course != null){
			String cate = teacherService.getCategoryById(course.getCourseCategory());
			request.setAttribute("type", cate);
		
			//修改课程创建时间格式
			String time = course.getPublishTime().toString().substring(0, 10);
			request.setAttribute("publishTime", time);
		}
		try {
			//获得对应的资源
			List<Resource> resource = resourceService.showResource(resourceId);
			//mv.addObject("category", category);
			for(Resource r : resource){
				int rindex = r.getResourcePath().lastIndexOf("\\");
				if(rindex >0)
					r.setResourcePath(r.getResourcePath().substring(rindex));
			}
			if(!resource.isEmpty() && resource != null)
				mv.addObject("resource", resource.get(0));
			mv.setViewName("/jsp/Teacher/teacher-search-resource");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}
	
	/**
	 * 跳转到修改任务资源页面
	 * @param request
	 * @param resourceId
	 * @param category
	 * @return
	 */
	@RequestMapping(value="toUpdateTaskResource/{taskId}")
	public ModelAndView toUpdateTaskResource(HttpServletRequest request,@PathVariable String taskId){
		ModelAndView mv = new ModelAndView();
		Course course = (Course) request.getSession().getAttribute("course");
		//查询课程类别名
		if(course != null){
			String cate = teacherService.getCategoryById(course.getCourseCategory());
			request.setAttribute("type", cate);
		
			//修改课程创建时间格式
			String time = course.getPublishTime().toString().substring(0, 10);
			request.setAttribute("publishTime", time);
		}
		try {
			//获得对应的资源
			Task task = teacherService.getTaskById(taskId);
			//mv.addObject("category", category);
			if(task != null)
				mv.addObject("task", task);
			mv.setViewName("/jsp/Teacher/teacher-update-taskResource");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}
	
	/**
	 * 跳转到修改任务资源页面
	 * @param request
	 * @param resourceId
	 * @param category
	 * @return
	 */
	@RequestMapping(value="toSearchTaskResource/{taskId}")
	public ModelAndView toSearchTaskResource(HttpServletRequest request,@PathVariable String taskId){
		ModelAndView mv = new ModelAndView();
		try {
			//获得对应的资源
			Task task = teacherService.getTaskById(taskId);
			task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));
			//mv.addObject("category", category);
			if(task != null)
				mv.addObject("task", task);
			if(task.getAccessoryList().size()>0)
			mv.addObject("acc",task.getAccessoryList().get(0));
			mv.setViewName("/jsp/Teacher/teacher-search-taskResource");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 修改资源
	 * @param request
	 * @return
	 */
	@RequestMapping(value="updateResource")

	public ModelAndView updateResource(HttpServletRequest request) {
		Resource resource = new Resource();
		try {
			Object[] obj = Common.fileFactory(request,null);
			Map<String, Object> formdata = (Map<String, Object>) obj[1];
			List<File> returnFileList = (List<File>) obj[0]; // 要返回的文件集合

			resource.setResourceId((String) formdata.get("resourceId"));
			resource.setCourseId((String) formdata.get("courseId"));
			resource.setPublisherId((String) request.getSession().getAttribute("teacherId"));
			resource.setPublishTime(new Timestamp(System.currentTimeMillis()));
			resource.setResourceDetail((String) formdata.get("resourceDetail"));
			resource.setResourceName((String) formdata.get("resourceName"));
			resource.setResourceTypeId(Integer.parseInt((String) formdata.get("resourceType")));
			if(!returnFileList.isEmpty())
			{
				resource.setResourcePath(returnFileList.get(0).getPath());
				resource.setSize(returnFileList.get(0).length()/1024.0+"KB");
			}
			teacherService.updateResource(resource);
			return toCourseResource(request, (String) formdata.get("resourceType"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		

	}

	@RequestMapping(value="toPublishResource/{category}/{courseId}")
	@SuppressWarnings({ "unused", "unchecked" })
	public String toPublishResource(HttpServletRequest request,@PathVariable String category,@PathVariable String courseId ) {
		String path = "/jsp/Teacher/teacher-release-resource";
		try {
			//接收类型
			// category  = (String)request.getAttribute("category");
			request.setAttribute("category", category);
			if("6".equals(category) || "7".equals(category)||"8".equals(category) || "9".equals(category)|| "10".equals(category)){
				path = "/jsp/Teacher/teacher-release-resourceTask";
				Timestamp d = new Timestamp(System.currentTimeMillis()); 
				request.setAttribute("taskEndTime", d);
			}
			//String courseId = (String)request.getAttribute("courseId");
			Course course = new Course();
			if(!"".equals(courseId) && courseId != null)
				course = teacherService.getCourseById(courseId);
			request.setAttribute("course", course);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return path;
	}

	@RequestMapping(value="publishResource")
	@SuppressWarnings({ "unused", "unchecked" })

	public ModelAndView publishResource(HttpServletRequest request) {
		try {
			String resourceId = Common.uuid();
			Object[] obj = Common.fileFactory(request,resourceId);
			Map<String, Object> formdata = (Map<String, Object>) obj[1];
			List<File> returnFileList = (List<File>) obj[0]; // 要返回的文件集合
			Resource resource = new Resource();
			resource.setResourceId(resourceId);
			resource.setCourseId((String) formdata.get("courseId"));
			resource.setPublisherId((String) request.getSession().getAttribute("teacherId"));
			resource.setPublishTime(new Timestamp(System.currentTimeMillis()));
			resource.setResourceDetail((String) formdata.get("resourceDetail"));
			resource.setResourceName((String) formdata.get("resourceName"));
			resource.setResourceTypeId(Integer.parseInt((String) formdata.get("resourceType")));
			resource.setUseNum(1);
			resource.setWatchNum(0);
			if(!returnFileList.isEmpty())
			{
				resource.setResourcePath(returnFileList.get(0).getPath());
				resource.setSize(returnFileList.get(0).length()/1024.0+"KB");
			}
			teacherService.addResource(resource);
			return toCourseResource(request, (String) formdata.get("resourceType"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			ModelAndView mv = new ModelAndView();
			mv.setViewName("/jsp/Teacher/teacher-release-resource");
			e.printStackTrace();
			return mv;
		}


	}

	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 发布任务资源
	 */
	@RequestMapping(value="publishTaskResourse")
	@SuppressWarnings({ "unused", "unchecked" })
	public ModelAndView publishTaskResourse(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		try {
			String taskId =  Common.uuid();	//设置任务id
			Object[] obj = Common.fileFactory(request,taskId);
			Map<String, Object> formdata = (Map<String, Object>) obj[1];
			List<File> returnFileList = (List<File>) obj[0]; // 要返回的文件集合
			String virtualClassNum = "001";

			// 创建list集合用于获取文件上传返回路径名
			List<String> list = new ArrayList<String>();
			List<Accessory> accessories  = new ArrayList<Accessory>();
			List<Resource> resources = new ArrayList<Resource>();
			Task task=new Task();
			task.setTaskId(taskId);
			task.setTaskTitle((String) formdata.get("taskTitle"));
			task.setTaskDetail((String) formdata.get("taskDetail"));
			//task.setTaskEndTime(Timestamp.valueOf((String) formdata.get("taskEndTime")));
			//	task.setTaskType((String) formdata.get("taskType"));
			task.setPublisherId((String) request.getSession().getAttribute("teacherId"));
			task.setPublishTime(new Timestamp(System.currentTimeMillis()));
			task.setVirtualClassNum(virtualClassNum);
			task.setUseNum(1);//设置使用次数为1
			task.setWatchNum(0);
			task.setCourseId((String) formdata.get("courseId"));
			String c = (String)formdata.get("taskCategory");
			if("6".equals(c))
				task.setTaskType("teaching_plan");
			else if("7".equals(c))
				task.setTaskType("teaching_resourse");
			else if("8".equals(c))
				task.setTaskType("work");
			else if("9".equals(c))
				task.setTaskType("trial");
			else if("10".equals(c))
				task.setTaskType("course_design");
			task.setStatus(0);

			
				teacherService.createTask(task);		//创建任务
				//teacherService.mapClassTask(task.getVirtualClassNum(), taskId,Timestamp.valueOf((String) formdata.get("taskEndTime")));		//映射班级任务表

			
			if(!returnFileList.isEmpty() && returnFileList != null) {
				for (File file : returnFileList) {
					Accessory accessory = new Accessory();
					String fileName="";
					try {
						fileName = new String(file.getName().getBytes("UTF-8"),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					accessory.setAccessoryName(fileName);
					System.out.println("name=================="+fileName);
					accessory.setAccessoryPath(file.getPath());
					accessory.setTaskId(taskId);
					accessory.setAccessoryTime(Common.TimestamptoString());
					accessories.add(accessory);
				}
				
					teacherService.addAccessory(accessories);	//添加任务附件
			}
			return toCourseResource(request, (String) formdata.get("taskCategory"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mv.setViewName("/jsp/Teacher/teacher-release-resourceTask");
			return mv;
		}
	}

	/**
	 * 添加教师的方法  excel 相关的操作,将数据插入到数据库 
	 * 使用spring的MultipartFile上传文件
	 * */
	@RequestMapping(value="DoExcel",method= {RequestMethod.POST})
	public ModelAndView DoExcel(@RequestParam(value="file_excel") MultipartFile file,HttpServletRequest request) {			
		ModelAndView mv = new ModelAndView();
		String readResult =null;
		try {
			//调用ITeacherService 下的方法，完成增加教师
			//readResult = teacherService.addTeacherInfo(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("readResult", readResult);//返回信息
		mv.setViewName("/success");//设置返回页面
		return mv;
	}

	/**
	 * @author wenli
	 * @return
	 * 教师通过点击课程显示自己所带班级
	 */
	@RequestMapping(value="teacherClassList/{courseId}",method= {RequestMethod.GET})
	public ModelAndView teacherClassList(@PathVariable String courseId) {
		ModelAndView mv = new ModelAndView();
		System.out.println(courseId+"dsfghjkljhgfds");
		List<VirtualClass> virtualClassList = null;
		try {
			virtualClassList = teacherService.virtualsForCourse(String.valueOf(courseId));//根据课程ID显示该课程所带班级
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("virtualClassList", virtualClassList);
		mv.setViewName("/jsp/Teacher/teacherClassList");
		return mv;
	}

	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 跳转到班级详情页

	 */
	@RequestMapping(value="toClassDetail",method= {RequestMethod.GET})
	public ModelAndView toClassDetail(HttpServletRequest request ,HttpServletResponse response, @RequestParam(value="virtualClassNum") String virtualClassNum,
			@RequestParam(value="virtualClassName") String virtualClassName,@RequestParam(value="courseName") String courseName ) throws Exception {
		ModelAndView mv = new ModelAndView();
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=UTF-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		System.out.println(virtualClassName+"dsfghjkl"+courseName);
		String identify = (String) request.getSession().getAttribute("identify");
		request.getSession().removeAttribute("courseName");
		request.getSession().setAttribute("virtualClassNum", virtualClassNum);
		VirtualClass virtualClass = teacherService.getVirtualById(virtualClassNum);
		Course course  = teacherService.getCourseById(virtualClass.getCourseId());
		request.getSession().setAttribute("virtualClassName", virtualClassName);
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		Student student = (Student) request.getSession().getAttribute("student");
		request.setAttribute("teacher", teacher);
		request.setAttribute("student", student);
		request.getSession().setAttribute("course", course);
		String teacherClassName = teacherService.getTeacherNameById(virtualClass.getCreatorId());
		request.getSession().setAttribute("courseName", courseName);
		request.getSession().setAttribute("teacherClassName", teacherClassName);
		mv.addObject("courseName", courseName);
		mv.addObject("virtualClassName",virtualClassName);
		mv.addObject("teacherClassName",teacherClassName);
		
		mv.addObject("identify", identify);
		mv.setViewName("/jsp/VirtualClass/classInfo");
		return mv;
	}
	/**
	 * @author wenli
	 * @param request
	 * @param virtualClassNuM
	 * @return
	 * 显示该班级所有任务列表
	 */
	@RequestMapping(value="teacherTaskList",method= {RequestMethod.GET})
	public ModelAndView teacherTaskList(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		List<String> taskIdList;
		List<Task> taskList;
		String readResult =null;
		Integer point=0;
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		try {
			taskIdList = teacherService.searchTaskId(virtualClassNum);//根据虚拟班级号获得任务列表
			if(!taskIdList.isEmpty()) {
				taskList = teacherService.TaskList(taskIdList);	//根据任务ID号获得任务实体
				for (Task task : taskList) {
					point = teacherService.searchTaskPoint(task.getTaskType());//任务实体对象加入任务分值信息
					task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));
					task.setTaskPoint(point);
				}
				mv.addObject("taskList", taskList);
			}else {
				mv.addObject("taskList", null);
			}
			Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
			Student student = (Student) request.getSession().getAttribute("student");
			mv.addObject("teacher",teacher);
			mv.addObject("student",student);
			mv.addObject("readResult", readResult);
			mv.setViewName("/jsp/Teacher/teacher-tasklist");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;	
	}
	/**
	 * @author wenli
	 * @param request
	 * @param virtualClassNum
	 * @return
	 * 根据不同任务类型显示任务列表
	 */
	@RequestMapping(value="teacherTaskListByTaskCategory",method= {RequestMethod.GET})
	public ModelAndView teacherTaskListByTaskCategory(HttpServletRequest request ,@RequestParam(value="taskCategory") String taskCategory) {
		ModelAndView mv = new ModelAndView();
		List<String> taskIdList=new ArrayList<String>();
		List<Task> taskList=new ArrayList<Task>();
		String readResult =null;
		Integer point=0;
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		try {
			taskIdList = teacherService.searchTaskId(virtualClassNum);//根据虚拟班级号获得任务列表
			if(!taskIdList.isEmpty()) {
				taskList = teacherService.teacherTaskAssortmentList(taskIdList,taskCategory);	//根据任务ID号获得任务实体
				if (!taskList.isEmpty()) {
					for (Task task : taskList) {
						point = teacherService.searchTaskPoint(task.getTaskType());//任务实体对象加入任务分值信息
						task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));
						task.setTaskPoint(point);
					}
					mv.addObject("taskList", taskList);
				}else {
					mv.addObject("taskList", null);
				}
			}else {
				mv.addObject("taskList", null);
			}
			mv.addObject("readResult", readResult);
			if(taskCategory.equals("trial")) {

				mv.setViewName("/jsp/Teacher/teacher-experiment");
			}

			if(taskCategory.equals("course_design")) {
				mv.setViewName("/jsp/Teacher/teacher-tasklist");
			}
			if(taskCategory.equals("work")) {
				mv.setViewName("/jsp/Teacher/teacher-tasklist");
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;	
	}
	@RequestMapping(value="toTaskDetail",method= {RequestMethod.GET})
	public ModelAndView toTaskDetail(HttpServletRequest request,@RequestParam(value="taskId") String taskId) {
		ModelAndView mv = new ModelAndView();
		Timestamp taskEndTime;
		String format = "yyyy-MM-dd HH:mm:ss";
		Timestamp now = new Timestamp(System.currentTimeMillis());
		Date nowTime = new java.util.Date(now.getTime());
		DateFormat df=new SimpleDateFormat(format);
		try {
			nowTime=df.parse(df.format(nowTime )) ;
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String identify = (String) request.getSession().getAttribute("identify");
		System.out.println("identify======"+identify);
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		String virtualClassName = (String) request.getSession().getAttribute("virtualClassName");
		Task task ;
		HashMap<String, Integer> studentTograde= new HashMap<String, Integer>();
		HashMap<String, UpTask> studentToUpTask= new HashMap<String, UpTask>();
		List<Student> studentUpedList = new ArrayList<Student>();
		List<Student> studentNotUpList = new ArrayList<Student>();
		List<String> accessoriesName = new ArrayList<String>();
		String upTaskDetail = null ;
		String studentId = (String) request.getSession().getAttribute("studentId");

		if(studentId!=null) {
			upTaskDetail = studentService.getUpTaskDetail(taskId, studentId);
		}
		Integer point=0;
		try {
			task = teacherService.searchTask(taskId);
			point = teacherService.searchTaskPoint(task.getTaskType());//任务实体对象加入任务分值信息
			task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));
			task.setTaskPoint(point);
			taskEndTime = teacherService.getTaskEndTime(virtualClassNum,task.getTaskId());
			String teacherClassName = (String) request.getSession().getAttribute("teacherClassName");
			String courseName =  (String) request.getSession().getAttribute("courseName");
			mv.addObject("task",task);
			mv.addObject("virtualClassName",virtualClassName);
			mv.addObject("virtualClassNum", virtualClassNum);
			mv.addObject("taskEndTime", taskEndTime);
			mv.addObject("courseName", courseName);
			mv.addObject("teacherClassName", teacherClassName);
			if(identify.equals("student")) {
				Boolean isEnd=false;
				Calendar begin = Calendar.getInstance();
				begin.setTime(nowTime);
				Date endTime = new java.util.Date(taskEndTime.getTime());

				endTime=df.parse(df.format(endTime )) ;
				Calendar end = Calendar.getInstance();
				end.setTime(endTime);
				if (begin.after(end)) {
					isEnd=true;
				}
				accessoriesName = studentService.getUpAccessories(taskId, studentId);
				String comment = null;
				Student student = null;
				Integer grade=null;
				comment = teacherService.getComment(taskId, studentId);
				grade = teacherService.getGrade(taskId, studentId);
				mv.addObject("grade", grade);
				mv.addObject("isEnd", isEnd);
				mv.addObject("comment", comment);
				mv.addObject("accessoriesName", accessoriesName);
				mv.addObject("upTaskDetail", upTaskDetail);
				mv.setViewName("/jsp/VirtualClass/studentwork");
			}else if(identify.equals("teacher")) {
				studentNotUpList =teacherService.getStudentListOfNotUp(taskId, virtualClassNum);
				studentUpedList = teacherService.getStudentListOfUped(taskId,virtualClassNum);
				for (Student student : studentUpedList) {
					studentTograde.put(student.getStudentId(), teacherService.getGrade(taskId, student.getStudentId()));
					studentToUpTask.put(student.getStudentId(), teacherService.getUpTask(taskId, student.getStudentId()));
				}
				//				for (Student s : studentNotUpList) {
				//					System.out.println(s.getStudentName());
				//				}
				mv.addObject("studentTograde", studentTograde);
				mv.addObject("studentToUpTask", studentToUpTask);
				mv.addObject("studentUpedList", studentUpedList);
				mv.addObject("studentNotUpList", studentNotUpList);
				mv.setViewName("/jsp/VirtualClass/teacherwork");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mv.setViewName("jsp/VirtualClass/content");
		}
		return mv;
	}


	/**
	 * 
	 * @author wenli
	 * @param request
	 * @param task_category
	 * @return
	 * 根据不同作业类型查找
	 */
	@RequestMapping(value="teacherTaskAssortmentList/{taskCategory}",method= {RequestMethod.GET})
	public ModelAndView teacherTaskAssortmentList(HttpServletRequest request,@PathVariable String taskCategory) {
		ModelAndView mv = new ModelAndView();
		List<String> taskIdList;
		List<Task> taskList;
		String readResult =null;
		Integer point=0;
		try {
			taskIdList = teacherService.searchTaskId("E56FE27F03344091BE8BDD698426EC22");//根据虚拟班级号获得任务列表
			taskList = teacherService.teacherTaskAssortmentList(taskIdList,taskCategory);	//根据任务ID号获得任务实体
			for (Task task : taskList) {
				point = teacherService.searchTaskPoint(task.getTaskType());//任务实体对象加入任务分值信息
				task.setTaskPoint(point);
			}
			mv.addObject("taskList", taskList);
			mv.addObject("readResult", readResult);
			mv.setViewName("/jsp/Teacher/classTask");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}
	/**
	 * @author LiMing
	 * 通过分类筛选 课程
	 * */
	@RequestMapping(value="readCourseInfoByCategory/{categoryId}",method= {RequestMethod.GET})
	public ModelAndView readCategoryInfo(HttpServletRequest request, @PathVariable String categoryId) {			
		ModelAndView mv = new ModelAndView();
		String category = categoryId;
		List<Course> list = new ArrayList<Course>();
		List<String> publishTime = new ArrayList<>();
		List<Teacher> teacherList = new ArrayList<>();
		try {
			list = teacherService.readCourseInfoByCategory(category);
			if(!list.isEmpty()) {
				for (Course course : list) 
				{
					teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
					course.setTeacherList(teacherList);
					publishTime.add(course.getPublishTime().toString().substring(0,10));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mv.addObject("categories", categories);
		mv.addObject("courseList", list);
		mv.addObject("publishTime",publishTime);
		mv.addObject("teacherList", teacherList);
		mv.addObject("categoryId",categoryId);
		mv.setViewName("/jsp/CourseJsp/courseSecond");//设置返回页面
		return mv;
	}

	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 进入我的相关课程的iframe入口
	 */
	@RequestMapping(value="toMyCourse")
	public ModelAndView toMyCourse(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/jsp/Teacher/teacherInfo/teacher_course_iframe");
		return mv;
	}
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 进入我的相关班级的iframe入口
	 */
	@RequestMapping(value="toMyClass")
	public ModelAndView toMyClass(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/jsp/Teacher/teacherInfo/teacher_class_iframe");
		return mv;
	}
	/**
	 * @author LiMing
	 * @param request
	 * @return
	 * 查找对应老师的课程列表，加入
	 * @throws Exception 
	 */
	@RequestMapping(value="toMyJoinCourse")
	public ModelAndView toMyJoinCourse(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		List<String> courseIdListByOthers;		//加入别人的课程ID号
		List<Course> courseListByOthers = null;		//别人课程实体
		List<Teacher> teacherList = null;
		List<String> teacherNames = new ArrayList<String>();
		List<String> publishTime = new ArrayList<>();
		System.out.println(request.getSession().getAttribute("teacherId"));
		try {
			courseIdListByOthers =teacherService.courseIdList((String) request.getSession().getAttribute("teacherId"), 0);
			courseListByOthers = teacherService.courseList(courseIdListByOthers);
			if(courseListByOthers!=null) {

				for (Course course : courseListByOthers ) {
					//限制显示字数
					if(!"".equals(course.getCourseDetail())){
						String de = course.getCourseDetail().replaceAll("<p>", "");
						de = de.replaceAll("</p>","");

						if(course.getCourseDetail().length()>=70){

							de = de.substring(0, 63);
						}
						course.setCourseDetail(de);
					}
					teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
					course.setTeacherList(teacherList);
					publishTime.add(course.getPublishTime().toString().substring(0,10));
				}
				for (Course course : courseListByOthers) {
					teacherNames.add(teacherService.getTeacherNameById(course.getPublisherId()));
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("teacherNames",teacherNames);
		mv.addObject("publishTime",publishTime);
		mv.addObject("courseList", courseListByOthers);
		mv.setViewName("/jsp/Teacher/teacherInfo/mycourse_create");
		return mv;
	}

	/**
	 * @author LiMing
	 * @param request
	 * @return
	 * 查找对应老师的课程列表，关注
	 * @throws Exception 
	 */
	@RequestMapping(value="toMyInterestCourse")
	public ModelAndView toMyInterestCourse(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		List<String> courseIdListByOthers;		//加入别人的课程ID号
		List<Course> courseListByOthers = null;		//别人课程实体
		List<Teacher> teacherList = null;
		List<String> publishTime = new ArrayList<>();
		List<String> teacherNames = new ArrayList<String>();
		System.out.println(request.getSession().getAttribute("teacherId"));
		try {
			courseIdListByOthers =teacherService.courseIdList((String) request.getSession().getAttribute("teacherId"), 2);
			courseListByOthers = teacherService.courseList(courseIdListByOthers);
			if(courseListByOthers!=null) {

				for (Course course : courseListByOthers ) {
					//限制显示字数
					if(!"".equals(course.getCourseDetail())){
						String de = course.getCourseDetail().replaceAll("<p>", "");
						de = de.replaceAll("</p>","");

						if(course.getCourseDetail().length()>=70){

							de = de.substring(0, 63);
						}
						course.setCourseDetail(de);
					}
					teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
					course.setTeacherList(teacherList);
					publishTime.add(course.getPublishTime().toString().substring(0,10));
				}
				for (Course course : courseListByOthers) {
					teacherNames.add(teacherService.getTeacherNameById(course.getPublisherId()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("teacherNames",teacherNames);
		mv.addObject("courseList", courseListByOthers);
		mv.addObject("publishTime",publishTime);
		mv.setViewName("/jsp/Teacher/teacherInfo/mycourse_create");
		return mv;
	}

	/**
	 * @author Huang
	 * @param request
	 * @return
	 * 查找对应老师的课程列表，创建
	 * @throws Exception 
	 */
	@RequestMapping(value="toMyCreateCourse")
	public ModelAndView toMyCreateCourse(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		List<String> courseIdListforMe ;	//自己创建的课程ID号
		List<Course> courseListforMe = null ;		//自己课程实体
		Teacher teacher = null;
		List<String> publishTime = new ArrayList<>();
		List<String> teacherIdList = null;
		List<Teacher> teacherList = null;
		//创建老师集合的目的是：课程与创建者的匹配
		List<String> teacherNames = new ArrayList<String>();
		try {
			courseIdListforMe = teacherService.courseIdList((String) request.getSession().getAttribute("teacherId"), 1);

			courseListforMe = teacherService.courseList(courseIdListforMe);
			if(courseListforMe!=null) {
				for (Course course : courseListforMe ) {
					//限制显示字数
					if(!"".equals(course.getCourseDetail())){
						String de = course.getCourseDetail().replaceAll("<p>", "");
						de = de.replaceAll("</p>","");

						if(course.getCourseDetail().length()>=70){

							de = de.substring(0, 63);
						}
						course.setCourseDetail(de);
					}
					teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
					course.setTeacherList(teacherList);
					publishTime.add(course.getPublishTime().toString().substring(0,10));
				}
				for (int i = 0; i < courseListforMe.size(); i++) {
					teacher = (Teacher) request.getSession().getAttribute("teacher");
					teacherNames.add(teacher.getTeacherName());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("teacherNames",teacherNames);
		mv.addObject("courseList", courseListforMe);
		mv.addObject("publishTime",publishTime);
		mv.setViewName("/jsp/Teacher/teacherInfo/mycourse_create");
		return mv;
	}
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 到个人中心的班级列表页
	 */
	@RequestMapping(value="toMyClassList")
	public ModelAndView toMyClassList(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		List<Term> termList = null;
		List<VirtualClass> virtualClassList= null;
		List<RealClass> realClassList = null;
		List<String> realClassIdList = null;
		String creatorId = (String) request.getSession().getAttribute("teacherId");
		virtualClassList = teacherService.getVirtualClassByCreatorId(creatorId);
		if(virtualClassList!=null) {
			for (VirtualClass virtualClass : virtualClassList) {
				realClassList = teacherService.getRealClassList(virtualClass.getVirtualClassNum());
				virtualClass.setRealClassList(realClassList);
				String term = virtualClass.getTerm();
				Term  a = teacherService.getTermById(term);
				virtualClass.setTerm(a.getStartYear()+"-"+a.getEndYear()+" 学年"+a.getTerm());
			}
		}
		try {
			termList = teacherService.readTerm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mv.addObject("termList", termList);
		mv.addObject("virtualClassList", virtualClassList);
		mv.setViewName("jsp/Teacher/teacherInfo/myclass_create");
		return mv;
	}

	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 去个人信息
	 */
	@RequestMapping(value="toMyInfo")
	public ModelAndView toMyInfo(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		mv.addObject("teacher", teacher);
		mv.setViewName("jsp/Teacher/teacherInfo/teacher_myInfo_iframe");
		return mv;
	}
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 去个人中心的基本信息
	 */
	@RequestMapping(value="toMyInfoBase")
	public ModelAndView toMyInfoBase(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		mv.addObject("teacher", teacher);
		mv.setViewName("jsp/Teacher/teacherInfo/myInfo_base");
		return mv;
	}
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 去个人中心的详细信息
	 */
	@RequestMapping(value="toMyInfoAll")
	public ModelAndView toMyInfoAll(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		mv.addObject("teacher", teacher);
		mv.setViewName("jsp/Teacher/teacherInfo/myInfo_all");
		return mv;
	}
	/**
	 * @author wenli
	 * @param request
	 * @return
	 */
	@RequestMapping(value="toMyInfoFruit")
	public ModelAndView toMyInfoFruit(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		List<Achievement> achievementList = null;
		List<IndustryUniversityResearchProject> industryUniversityResearchProjectList = null;
		List<TeacherProject> teacherProjectList = null;
		List<Paper> paperList =null;
		List<Prize> prizeList = null;
		achievementList = teacherService.achievementList();
		industryUniversityResearchProjectList = teacherService.industryUniversityResearchProjectList();
		teacherProjectList = teacherService.teacherProjectList();
		paperList = teacherService.paperList();
		prizeList = teacherService.prizeList();
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		mv.addObject("teacher", teacher);
		for (Prize prize : prizeList) {
			System.out.println(prize.getPrizeProjectName());
		}
		System.out.println(achievementList);
		System.out.println(industryUniversityResearchProjectList);
		System.out.println(teacherProjectList);
		System.out.println(paperList);
		System.out.println(prizeList);
		mv.addObject("achievementList", achievementList);
		mv.addObject("industryUniversityResearchProjectList", industryUniversityResearchProjectList);
		mv.addObject("teacherProjectList", teacherProjectList);
		mv.addObject("paperList", paperList);
		mv.addObject("prizeList", prizeList);
		mv.setViewName("jsp/Teacher/teacherInfo/myInfo_fruit");
		return mv;
	}


	/**
	 * @author WenLi
	 * @param request
	 * @return
	 * 查找对应老师的课程列表，创建
	 * @throws Exception 
	 */
	//	@RequestMapping(value="searchCourse")
	//	public JSONObject searchCourse(HttpServletRequest request,HttpServletResponse response) throws Exception {
	//		JSONObject jsonObject = new JSONObject();
	//		String content = request.getParameter("content");
	//		List<Course> courseList = null ;
	//		//通过课程名查询课程，由于课程名不重复，故只取返回集合中的第一个
	//		courseList = teacherService.readCourse(content);
	//		Course course = courseList.get(0);
	//	
	//		jsonObject.toString();
	//		response.getWriter().print(course);
	//		return jsonObject;
	//	}
	//	@RequestMapping("/picShow")
	//    public void picShow(HttpServletRequest request,HttpServletResponse response,String picName) throws IOException {
	//		String path = Common.readProperties("path");
	//        String imagePath = path+picName;
	//        response.reset();
	//        //判断文件是否存在
	//        File file = new File(imagePath);
	//        if (!file.exists()) {
	//            imagePath = path+"/"+"course1.jpg";
	//        }
	//        // 得到输出流
	//        OutputStream output = response.getOutputStream();
	//        if (imagePath.toLowerCase().endsWith(".jpg"))// 使用编码处理文件流的情况：
	//        {
	//            response.setContentType("image/jpeg;charset=GB2312");// 设定输出的类型
	//            // 得到图片的真实路径
	//            // 得到图片的文件流
	//            InputStream imageIn = new FileInputStream(new File(imagePath));
	//            // 得到输入的编码器，将文件流进行jpg格式编码
	//            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(imageIn);
	//            // 得到编码后的图片对象
	//            BufferedImage image = decoder.decodeAsBufferedImage();
	//            // 得到输出的编码器
	//            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(output);
	//            encoder.encode(image);// 对图片进行输出编码
	//            imageIn.close();// 关闭文件流
	//        }
	//        output.close();
	//    }
	@RequestMapping(value="/picShow/{faceImg}")
	@ResponseBody
	public String picShow(HttpServletRequest request,HttpServletResponse response,@PathVariable String faceImg, Model model) {
		// response.setContentType("image/*")

		System.out.println("到这了");
		FileInputStream fis = null;
		OutputStream os = null;
		try {
			fis = new FileInputStream(faceImg);
			os = response.getOutputStream();
			int count = 0;
			byte[] buffer = new byte[1024 * 8];
			while ((count = fis.read(buffer)) != -1) {
				os.write(buffer, 0, count);
				os.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			fis.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "ok";
	}
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * @throws IOException
	 * spring方式下载，当文件较小且下载复杂度不是很大时使用效率较高
	 */
	@RequestMapping(value="/resourceDownload", method= {RequestMethod.POST})
	public ResponseEntity<byte[]> download(HttpServletRequest request,HttpServletResponse response,@RequestParam(value="fileName")String fileName,@RequestParam(value="id")String id,@RequestParam(value="type")String type) throws IOException {
		request.setCharacterEncoding("utf-8");
		((ServletRequest) response).setCharacterEncoding("utf-8");  
		response.setContentType("text/html;charset=UTF-8");
		System.out.println(fileName);
		String studentId = (String) request.getSession().getAttribute("studentId");
		File file=null;
		//附件下载时通过判断类型下载，因为在上传时是通过不同身份设置了不同的路径，在下载时则不能再根据当前登陆者身份来确定唯一路径，路径的类型由前台给予
		if("teacherPub".equals(type)) {
			file = new File(Common.readProperties("path")+"/"+id+"/"+fileName);
		}else if("studentUp".equals(type)) {
			file = new File(Common.readProperties("path")+"/"+id+"/"+studentId+"/"+fileName);

		}
		
		System.out.println("文件名"+file.getName());
		byte[] body = null;
		InputStream is = new FileInputStream(file);
		body = new byte[is.available()];
		is.read(body);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attchement;filename=" + file.getName());
		HttpStatus statusCode = HttpStatus.OK;
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, statusCode);
		
		return entity;
	}
	
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * @throws IOException
	 * spring方式下载，当文件较小且下载复杂度不是很大时使用效率较高
	 */
	@RequestMapping(value="/resourceDownload2/{fileName}/{id}/{type}", method= {RequestMethod.POST})
	public ResponseEntity<byte[]> download2(HttpServletRequest request,HttpServletResponse response,@PathVariable String fileName,@PathVariable String id,@PathVariable String type) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		System.out.println(fileName);
		String studentId = (String) request.getSession().getAttribute("studentId");
		File file=null;
		//附件下载时通过判断类型下载，因为在上传时是通过不同身份设置了不同的路径，在下载时则不能再根据当前登陆者身份来确定唯一路径，路径的类型由前台给予
		if("teacherPub".equals(type)) {
			file = new File(Common.readProperties("path")+"/"+id+"/"+fileName);
		}else if("studentUp".equals(type)) {
			file = new File(Common.readProperties("path")+"/"+id+"/"+studentId+"/"+fileName);

		}
		
		System.out.println("文件名"+file.getName());
		byte[] body = null;
		InputStream is = new FileInputStream(file);
		body = new byte[is.available()];
		is.read(body);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attchement;filename=" + file.getName());
		HttpStatus statusCode = HttpStatus.OK;
		ResponseEntity<byte[]> entity = new ResponseEntity<byte[]>(body, headers, statusCode);
		
		return entity;
	}


	/**
	 * @author LiMing
	 * @param request
	 * 访问资源更新页
	 * @throws Exception 
	 */
	@RequestMapping("/toResourceMain")
	public ModelAndView toResourceMain(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		//	List<ResourceType> resourceList = new ArrayList<ResourceType>();
		//	resourceList = teacherService.readResourceCategoried();
		//	Map<Integer,String> resourceCategories = new HashMap<Integer, String>();
		// Course course = teacherService.getCourseById(courseId);
		//		String resourceName = null;
		//		for (ResourceType resourceType : resourceList) {
		//			if(resourceType.getResourceType().equals("word"))
		//			{
		//				resourceName = "文档";
		//			}
		//			if(resourceType.getResourceType().equals("audio"))
		//			{
		//				resourceName = "音频";
		//			}
		//			if(resourceType.getResourceType().equals("video"))
		//			{
		//				resourceName = "视频";
		//			}
		//			if(resourceType.getResourceType().equals("photo"))
		//			{
		//				resourceName = "图片";
		//			}
		//			if(resourceType.getResourceType().equals("excel"))
		//			{
		//				resourceName = "表格";
		//			}
		//			if(resourceType.getResourceType().equals("ppt"))
		//			{
		//				resourceName = "PPT";
		//			}
		//			if(resourceType.getResourceType().equals("pdf"))
		//			{
		//				resourceName = "PDF";
		//			}
		//			if(resourceType.getResourceType().equals("compressed"))
		//			{
		//				resourceName = "压缩文件";
		//			}
		//			if(resourceType.getResourceType().equals("other"))
		//			{
		//				resourceName = "其他";
		//			}
		//			resourceCategories.put(resourceType.getResourceTypeId(),resourceName);
		//		}
		//		mv.addObject("resourceCategories", resourceCategories);
		Course course = (Course) request.getSession().getAttribute("course");
		mv.addObject("course", course);
		mv.setViewName("/jsp/Teacher/managerResourceList");
		return mv; 
	}


	/**
	 * @author LiMing
	 * @param request
	 * 访问作业资源,作为默认Iframe的显示内容
	 */
	@RequestMapping("/toResource/{resourceType}")
	public ModelAndView toResource(@PathVariable String resourceType) throws IOException {
		ModelAndView mv = new ModelAndView();
		List<Resource> list = new ArrayList<Resource>();
		int type = Integer.parseInt(resourceType);
		//showResourceByType()为混合方法，看具体注释
		list  = resourceService.showResourceByType(type);
		mv.addObject("resource", list);
		mv.setViewName("/jsp/Teacher/managerResourceListIframe");
		return mv; 
	}

	/**
	 * @author LiMing
	 * @param request
	 * 更新资源
	 * @throws Exception 
	 */
	//	@SuppressWarnings("unchecked")
	//	@RequestMapping("/toUpdateResource/{resourceId}")
	//	public ModelAndView toUpdateResource(HttpServletRequest request,@PathVariable("resourceId")String resourceId) throws Exception {
	//		ModelAndView mv = new ModelAndView();
	//		//		Resource resource = new Resource();
	//		//		try {
	//		//			Object[] obj = Common.fileFactory(request,resourceId);
	//		//			Map<String, Object> formdata = (Map<String, Object>) obj[1];
	//		//			List<File> returnFileList = (List<File>) obj[0]; // 要返回的文件集合
	//		//			resource.setResourceId(resourceId);
	//		//			resource.setPublishTime(new Timestamp(System.currentTimeMillis()));
	//		//			resource.setResourceDetail((String) formdata.get("resourceDetail"));
	//		//			resource.setResourceName((String) formdata.get("resourceName"));
	//		//			if(!returnFileList.isEmpty())
	//		//			{
	//		//				resource.setResourcePath(returnFileList.get(0).getPath());
	//		//				resource.setSize(returnFileList.get(0).length()/1024.0+"KB");
	//		//				resource.setResourceTypeId(Common.fileType(returnFileList.get(0).getName(), teacherService));//需要判断文件类型	
	//		//			}
	//		//			teacherService.addResource(resource);
	//		//		} catch (Exception e) {
	//		//			// TODO Auto-generated catch block
	//		//			e.printStackTrace();
	//		//		}
	//		//		resourceService.updateResource(resourceId, resourceName, resourceDetail, null, null, null, null, null, null, null, null, null);
	//		mv = toResourceMain(request);
	//		return mv; 
	//	}

	/**
	 * @author LiMing
	 * @param request
	 * 删除资源
	 * @throws Exception 
	 */
	@RequestMapping("/toDeleteResource/{resourceId}")
	public void toDeleteResource(HttpServletResponse response,@PathVariable String resourceId) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		String msg = null;
		msg  = resourceService.deleteResourceById(resourceId);
		String result = JSONObject.toJSONString(msg);
		response.getWriter().println(result);
	}
	
	/**
	 * @author LiMing
	 * @param request
	 * 删除任务资源
	 * @throws Exception 
	 */
	@RequestMapping("/toDeleteTaskResource/{resourceId}")
	public void toDeleteTaskResource(HttpServletResponse response,@PathVariable String resourceId) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		String msg = null;
		try {
			
			//删除相关附件
			teacherService.deleteTaskAccByTid(resourceId);
			//删除任务资源
			teacherService.deleteTask(resourceId);
			msg = "删除成功";
			String result = JSONObject.toJSONString(msg);
			response.getWriter().println(result);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			msg = "删除成功";
			String result = JSONObject.toJSONString(msg);
			response.getWriter().println(result);
		}
		
	}

	/**
	 * @author LiMing
	 * @param request
	 * 返回需要更新的资源，将对象显示在模态框
	 * @throws Exception 
	 */
	@RequestMapping("/toModalResource/{resourceId}")
	public void tomodalResource(HttpServletRequest request, HttpServletResponse response,@PathVariable String resourceId) throws Exception {
		String msg = null;
		List<Resource> list = new ArrayList<Resource>();
		list  = resourceService.showResource(resourceId);//此时resourceId 不为空，将按照条件查询。返回只有一个对象的集合
		JSONArray  json  =  JSONArray.fromObject(list); 
		String result = json.toString();
		response.getWriter().print(result);
	}

	/**
	 * 跳转到教师主页
	 * @param request
	 * @return
	 */
	@RequestMapping(value="toTeacherPage")
	public ModelAndView toTeacherPage(HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		try {
			//获取教师信息
			Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
			mv.addObject("teacher",teacher);
			mv.setViewName("jsp/Teacher/teacherInfo/centreForTeacher"); //跳转教师个人页
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mv.addObject("readResult", "异常");//返回信息
			mv.setViewName("/jsp/Teacher/index");//设置返回页面
		}
		return mv;
	}



	/**
	 * 跳转到教师主页
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/toUpdateTaskPage/{taskId}")
	public ModelAndView toUpdateTaskPage(HttpServletRequest request,@PathVariable String taskId) throws Exception{
		ModelAndView mv = new ModelAndView();
		Task task = new Task();
		Course course = (Course) request.getSession().getAttribute("course");
		Resource resource = new Resource();
		try {
			task = teacherService.searchTask(taskId);
			task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));
			mv.addObject("task",task);
			mv.addObject("course", course);//返回信息
			mv.setViewName("jsp/Teacher/teacher-update-task"); 
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject("readResult", "异常");//返回信息
			mv = toResourceMain(request);
		}
		return mv;
	}



	/**
	 * 退出
	 * @param request
	 * @return
	 */
	@RequestMapping(value="quit")
	public ModelAndView quit(HttpServletRequest request,HttpServletResponse response){
		request.getSession().invalidate();
		request.getSession().removeAttribute("identify");
		request.getSession().removeAttribute("teacher");
		request.getSession().removeAttribute("teacherId");
		request.getSession().removeAttribute("student");
		request.getSession().removeAttribute("studentId");
		

		try {
			
			return mainController.toMain(request);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return mainController.toMain(request);
		}
	}

	/**
	 * @author LiMing
	 * @param request
	 * @throws Exception 
	 * 资源分类跳转，用于资源更新时的按照分类查找资源
	 * 1.教案库
	 * 2.教学资源库
	 * 3.多媒体资源
	 * 4.作业库
	 * 5.实验库
	 * 6.课程设计库
	 * 7.全部资源
	 * @return 返回对应资源
	 */
	@RequestMapping("/toResourceCategory/{category}")
	public ModelAndView toResourceCategory(HttpServletRequest request,@PathVariable String category) throws Exception{
		ModelAndView mv = new ModelAndView();
		Course course = (Course) request.getSession().getAttribute("course");
		String courseId = course.getCourseId();
		List<Resource> resourceList = null;//返回前台数据
		List<Task> taskT = null;//task暂存数据
		List<Task> taskList = null;//返回前台数据
		//查询信息switch
		switch (category) {
		case "1"://教案库
			mv.setViewName("jsp/Teacher/managerResourceListIframeResource"); 
			break;
		case "2"://教学资源库
			mv.setViewName("jsp/Teacher/managerResourceListIframeResource"); 
			break;
		case "3"://多媒体资源
			resourceList = resourceService.showResourceByCourse(courseId);
			for (Resource resource : resourceList) {
				resource.setPublisherId(teacherService.getTeacherNameById(resource.getPublisherId()));
			}
			mv.addObject("resource", resourceList);//返回信息
			mv.setViewName("jsp/Teacher/managerResourceListIframeResource"); 
			break;
		case "4"://作业库
			taskList = teacherService.getTaskByPointAndCourse("work",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
			}
			mv.addObject("taskList", taskList);//返回信息
			mv.setViewName("jsp/Teacher/managerResourceListIframeTask"); 
			break;
		case "5"://实验库
			taskList = teacherService.getTaskByPointAndCourse("trial",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
			}
			mv.addObject("taskList", taskList);//返回信息
			mv.setViewName("jsp/Teacher/managerResourceListIframeTask"); 
			break;
		case "6"://课程设计库
			mv.setViewName("jsp/Teacher/managerResourceListIframeResource"); 
			break;
		case "7"://全部资源
			//多媒体资源
			resourceList = resourceService.showResourceByCourse(courseId);
			for (Resource resource : resourceList) {
				resource.setPublisherId(teacherService.getTeacherNameById(resource.getPublisherId()));
			}
			/********************************************/
			mv.addObject("resource", resourceList);//返回信息
			/********************************************/
			//作业库
			taskList = teacherService.getTaskByPointAndCourse("work",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
			}
			//实验库
			taskT = teacherService.getTaskByPointAndCourse("trial",courseId);
			for (Task task : taskT) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
			}
			taskList.addAll(taskT);
			mv.addObject("taskList", taskList);//返回信息	
			mv.setViewName("jsp/Teacher/managerResourceListIframeAll"); 
			break;
		default:
			break;
		}
		mv.addObject("course", course);//返回信息
		return mv;
	}

	/**
	 * @author LiMing
	 * @param request
	 * @throws Exception 
	 * 资源分类跳转，用于资源更新时的按照分类查找资源
	 * 1.教案库
	 * 2.教学资源库
	 * 3.多媒体资源
	 * 4.作业库
	 * 5.实验库
	 * 6.课程设计库
	 * 7.全部资源
	 * @return 返回对应资源
	 */
	@RequestMapping("/toCourseResource/{category}")
	public ModelAndView toCourseResource(HttpServletRequest request,@PathVariable String category) throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.addObject("category", category);
		Course course = (Course) request.getSession().getAttribute("course");
		String courseId = "";
		List<Teacher> teacherList = new ArrayList<>();
		boolean isThisTeacher = false;
		Teacher user =  (Teacher) request.getSession().getAttribute("teacher");
		if(course != null){
			courseId = course.getCourseId();
			//通过courseid查询教师圈id
			teacherList = teacherService.getTeachersByCourseId(courseId);
		}
		//比较此时的用户是否是点开的课程的教师
		if(!teacherList.isEmpty() && user != null){
			for(Teacher t : teacherList){
				if(t.getEmployeeNum().equals(user.getEmployeeNum())){
					isThisTeacher = true;
					break;
				}
			}
		}
		if(isThisTeacher){
			request.setAttribute("isTeacher", 1);
		}
		
		//查询课程类别名
			if(course != null){
				String cate = teacherService.getCategoryById(course.getCourseCategory());
				request.setAttribute("type", cate);
			
				//修改课程创建时间格式
				String time = course.getPublishTime().toString().substring(0, 10);
				request.setAttribute("publishTime", time);
			}
		List<Resource> resourceList = new ArrayList<>();//返回前台数据
		List<Task> taskList = new ArrayList<>();//返回前台数据
		//查询信息switch
		switch (category) {
		case "0":{
			mv.addObject("resourceName", "全部");
			//多媒体资源
			resourceList = resourceService.showResourceByCourse(courseId);
			for (Resource resource : resourceList) {
				resource.setPublisherId(teacherService.getTeacherNameById(resource.getPublisherId()));
				//限制显示字数
				if(!"".equals(resource.getResourceDetail()) && resource.getResourceDetail() != null){
					String de = resource.getResourceDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");
					
					if(resource.getResourceDetail().length()>=30){
						
						de = de.substring(0, 15);
					}
					resource.setResourceDetail(de);
				}
			}
			mv.addObject("resource", resourceList);//返回信息
			//作业库
			taskList = teacherService.getTaskByPointAndCourse("work",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");
					
					if(task.getTaskDetail().length()>=30){
						
						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			
			//教案库
			List<Task> teaching_plan = teacherService.getTaskByPointAndCourse("teaching_plan",courseId);
			for (Task task : teaching_plan) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");
					
					if(task.getTaskDetail().length()>=30){
						
						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
				taskList.add(task);
			}
			//教学资源库
			List<Task> teaching_resourse = teacherService.getTaskByPointAndCourse("teaching_resourse",courseId);
			for (Task task : teaching_resourse) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");
					
					if(task.getTaskDetail().length()>=30){
						
						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
				taskList.add(task);
			}
			//课设库
			List<Task> curriculum_design = teacherService.getTaskByPointAndCourse("course_design",courseId);
			for (Task task : curriculum_design) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");
					
					if(task.getTaskDetail().length()>=30){
						
						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
				taskList.add(task);
			}
			//实验库
			List<Task> trailList = teacherService.getTaskByPointAndCourse("trial",courseId);
			for (Task task : trailList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");
					
					if(task.getTaskDetail().length()>=30){
						
						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
				taskList.add(task);
			}
			mv.addObject("taskList", taskList);//返回信息
			break;

		}
		case "6":{
			//教案库
			taskList = teacherService.getTaskByPointAndCourse("teaching_plan",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(task.getTaskDetail().length()>=30){

						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			if(taskList != null && !taskList.isEmpty())
				mv.addObject("taskList", taskList);//返回信息
			else 
				mv.addObject("taskList", null);//返回信息
			mv.addObject("resourceName", "教案");
			break;
		}
		case "7":{
			//教学资源库
			taskList = teacherService.getTaskByPointAndCourse("teaching_resourse",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(task.getTaskDetail().length()>=30){

						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			if(taskList != null && !taskList.isEmpty())
				mv.addObject("taskList", taskList);//返回信息
			else 
				mv.addObject("taskList", null);//返回信息
			mv.addObject("resourceName", "教学资源");
			
			
			break;
		}
		case "5":{
			//多媒体资源
			resourceList = resourceService.showResourceByCourse(courseId);
			for (Resource resource : resourceList) {
				resource.setPublisherId(teacherService.getTeacherNameById(resource.getPublisherId()));
				//限制显示字数
				if(!"".equals(resource.getResourceDetail()) && resource.getResourceDetail() != null){
					String de = resource.getResourceDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(resource.getResourceDetail().length()>=30){

						de = de.substring(0, 15);
					}
					resource.setResourceDetail(de);
				}
			}
			if(resourceList != null && !resourceList.isEmpty())
				mv.addObject("resource", resourceList);//返回信息
			else 
				mv.addObject("resource", null);//返回信息
			mv.addObject("resourceName", "多媒体资源");
			break;
		}
		case "8":{
			//作业库
			taskList = teacherService.getTaskByPointAndCourse("work",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(task.getTaskDetail().length()>=30){

						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			if(taskList != null && !taskList.isEmpty())
				mv.addObject("taskList", taskList);//返回信息
			else 
				mv.addObject("taskList", null);//返回信息
			mv.addObject("resourceName", "作业");
			break;
		}
		case "9"://实验库
			taskList = teacherService.getTaskByPointAndCourse("trial",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(task.getTaskDetail().length()>=30){

						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			if(taskList != null && !taskList.isEmpty())
				mv.addObject("taskList", taskList);//返回信息
			else 
				mv.addObject("taskList", null);//返回信息
			mv.addObject("resourceName", "实验");
			break;
		case "10":
			//课程设计库
			taskList = teacherService.getTaskByPointAndCourse("course_design",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(task.getTaskDetail().length()>=30){

						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			if(taskList != null && !taskList.isEmpty())
				mv.addObject("taskList", taskList);//返回信息
			else 
				mv.addObject("taskList", null);//返回信息
			mv.addObject("resourceName", "课程设计");
			break;
		default:
			break;
		}

		List<String> publisherList = new ArrayList<>();
		List<String> timeList = new ArrayList<>();
		if(!resourceList.isEmpty()){
			for(Resource re:resourceList){
				//修改时间格式
				String publishTime = re.getPublishTime().toString().substring(0, 10);
				timeList.add(publishTime);
			}
		}
		else if(!taskList.isEmpty()){
			for(Task ta:taskList){
				//修改时间格式
				String publishTime = ta.getPublishTime().toString().substring(0, 10);
				timeList.add(publishTime);
			}
		}
		mv.addObject("time", timeList);
		mv.setViewName("/jsp/Teacher/courseResource");
		mv.addObject("course", course);//返回信息
		return mv;
	}

	/**
	 * 课程详细页面的列表显示
	 * @param request
	 * @param category
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/toCourseResourceFrame/{category}/{courseId}")
	public ModelAndView toCourseResourceFrame(HttpServletRequest request,@PathVariable String category,@PathVariable String courseId) throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.addObject("category", category);
		List<Teacher> teacherList = new ArrayList<>();
		boolean isThisTeacher = false;
		Teacher user =  (Teacher) request.getSession().getAttribute("teacher");
		if(courseId != null){
			//通过courseid查询教师圈id
			teacherList = teacherService.getTeachersByCourseId(courseId);
		}
		//比较此时的用户是否是点开的课程的教师
		if(!teacherList.isEmpty() && user != null){
			for(Teacher t : teacherList){
				if(t.getEmployeeNum().equals(user.getEmployeeNum())){
					isThisTeacher = true;
					break;
				}
			}
		}
		if(isThisTeacher){
			request.setAttribute("isTeacher", 1);
		}
		List<Resource> resourceList = new ArrayList<>();//返回前台数据
		List<Task> taskList = new ArrayList<>();//返回前台数据
		//查询信息switch
		switch (category) {
		case "6":{
			//教案库
			mv.addObject("resourceName", "教案");
			break;
		}
		case "7":{
			//教学资源库
			mv.addObject("resourceName", "教学资源");
			break;
		}
		case "5":{
			//多媒体资源
			resourceList = resourceService.showResourceByCourse(courseId);
			for (Resource resource : resourceList) {
				resource.setPublisherId(teacherService.getTeacherNameById(resource.getPublisherId()));
				//限制显示字数
				if(!"".equals(resource.getResourceDetail()) && resource.getResourceDetail() != null){
					String de = resource.getResourceDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(resource.getResourceDetail().length()>=30){

						de = de.substring(0, 15);
					}
					resource.setResourceDetail(de);
				}
			}
			if(resourceList != null && !resourceList.isEmpty())
				mv.addObject("resource", resourceList);//返回信息
			else 
				mv.addObject("resource", null);//返回信息
			mv.addObject("resourceName", "多媒体资源");
			break;
		}
		case "8":{
			//作业库
			taskList = teacherService.getTaskByPointAndCourse("work",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(task.getTaskDetail().length()>=30){

						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			if(taskList != null && !taskList.isEmpty())
				mv.addObject("taskList", taskList);//返回信息
			else 
				mv.addObject("taskList", null);//返回信息
			mv.addObject("resourceName", "作业");
			break;
		}
		case "9"://实验库
			taskList = teacherService.getTaskByPointAndCourse("trial",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(task.getTaskDetail().length()>=30){

						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			if(taskList != null && !taskList.isEmpty())
				mv.addObject("taskList", taskList);//返回信息
			else 
				mv.addObject("taskList", null);//返回信息
			mv.addObject("resourceName", "实验");
			break;
		case "10"://课程设计库
			taskList = teacherService.getTaskByPointAndCourse("course_design",courseId);
			for (Task task : taskList) {
				task.setPublisherId(teacherService.getTeacherNameById(task.getPublisherId()));
				//限制显示字数
				if(!"".equals(task.getTaskDetail())){
					String de = task.getTaskDetail().replaceAll("<p>", "");
					de = de.replaceAll("</p>","");

					if(task.getTaskDetail().length()>=30){

						de = de.substring(0, 15);
					}
					task.setTaskDetail(de);
				}
			}
			if(taskList != null && !taskList.isEmpty())
				mv.addObject("taskList", taskList);//返回信息
			else 
				mv.addObject("taskList", null);//返回信息
			mv.addObject("resourceName", "课程设计");
			break;
		default:
			break;
		}

		List<String> publisherList = new ArrayList<>();
		List<String> timeList = new ArrayList<>();
		if(!resourceList.isEmpty()){
			for(Resource re:resourceList){
				//修改时间格式
				String publishTime = re.getPublishTime().toString().substring(0, 10);
				timeList.add(publishTime);
			}
		}
		else if(!taskList.isEmpty()){
			for(Task ta:taskList){
				//修改时间格式
				String publishTime = ta.getPublishTime().toString().substring(0, 10);
				timeList.add(publishTime);
			}
		}
		mv.addObject("time", timeList);
		mv.setViewName("/jsp/Teacher/resource_frame");
		return mv;
	}


	/**
	 * 跳转到资源页
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/toUpdateResourcePage/{resourceId}")
	public ModelAndView toUpdateResourcePage(HttpServletRequest request,@PathVariable String resourceId) throws Exception{
		ModelAndView mv = new ModelAndView();
		List<Resource> list = new ArrayList<Resource>();
		Course course = (Course) request.getSession().getAttribute("course");
		Resource resource = new Resource();
		try {
			list = resourceService.showResource(resourceId);
			resource = list.get(0);
			mv.addObject("resource",resource);
			mv.addObject("course", course);//返回信息
			mv.setViewName("jsp/Teacher/teacher-update-resource"); 
		} catch (Exception e) {
			e.printStackTrace();
			mv.addObject("readResult", "异常");//返回信息
			mv = toResourceMain(request);
		}
		return mv;
	}

	@RequestMapping("/toteacherTaskList")
	public ModelAndView toteacherTaskList(HttpServletRequest request,@RequestParam(value="taskCategory") String taskCategory) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("taskCategory", taskCategory);
		mv.setViewName("/jsp/VirtualClass/allfragram");
		return mv;

	}
	/**
	 * @author WENLI
	 * @param request
	 * @return
	 * 获得班级考勤详情
	 */
	@RequestMapping("/toteacherAttenceAnalyse")
	public ModelAndView toteacherAttenceAnalyse(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView();
		String virtualClassNum  = (String) request.getSession().getAttribute("virtualClassNum");
		List<Attendance>attendanceList = new ArrayList<Attendance>();
		HashMap<String, List<Student>>leaveStudentMap = new HashMap<String, List<Student>>();
		HashMap<String, List<Student>>truancyStudentMap = new HashMap<String, List<Student>>();
		attendanceList = teacherService.getAttendanceDetail(virtualClassNum);
		for (Attendance attendance : attendanceList) {
			List<Student>leaveStudentList = new ArrayList<Student>();
			List<Student>truancyStudentList = new ArrayList<Student>();
			leaveStudentList = teacherService.getLeaveStudent(attendance.getAttendanceId());
			truancyStudentList = teacherService.getTruancyStudent(attendance.getAttendanceId());
			leaveStudentMap.put(attendance.getAttendanceId(), leaveStudentList);
			truancyStudentMap.put(attendance.getAttendanceId(), truancyStudentList);
		}
		mv.addObject("attendanceList", attendanceList);
		mv.addObject("leaveStudentMap", leaveStudentMap);
		mv.addObject("truancyStudentMap", truancyStudentMap);
		mv.setViewName("/jsp/Teacher/attence");
		return mv;

	}

	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 到班级作业列表页
	 */
	@RequestMapping("/toteacherTaskListContent/{taskCategory}/{status}")
	public ModelAndView toteacherTaskListContent(HttpServletRequest request ,@PathVariable String taskCategory,@PathVariable String status) {
		ModelAndView mv = new ModelAndView();
		Integer upNum =0;
		List<String> taskIdList;
		List<Task> taskList=new ArrayList<Task>();
		String readResult =null;
		Integer point=0;
		String upTaskDetail = null;
		String identify = (String) request.getSession().getAttribute("identify");
		Student student = (Student) request.getSession().getAttribute("student");
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		Timestamp now = new Timestamp(System.currentTimeMillis());
		HashMap<String, String> upTaskDetail2taskList = new HashMap<String, String>();
		HashMap<String, List<String>> accessoriesName2taskList  = new HashMap<String, List<String>>();
		HashMap<String, Integer> grade2taskList  = new HashMap<String, Integer>();
		HashMap<String, Boolean> isEnd2taskList = new HashMap<String, Boolean>();
		HashMap<String, String> timeEnd2taskList = new HashMap<String, String>();//计算时间差
		List<String> accessoriesName = new ArrayList<String>();
		//upTaskDetail = studentService.getUpTaskDetail(taskId, studentId);
		//accessoriesName = studentService.getUpAccessories(taskId, studentId);
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");//获取当前虚拟班级
		VirtualClass virtualClass = teacherService.getVirtualById(virtualClassNum);//获取虚拟班级实体
		Integer studentNum = virtualClass.getClassStuentNum();	//获取班级总人数
		try {
			taskIdList = teacherService.searchTaskId(virtualClassNum);//根据虚拟班级号获得任务列表
			if(!taskIdList.isEmpty()) {
				if(taskCategory.equals("all")) {

					taskList = teacherService.TaskList(taskIdList);	//根据任务ID号获得任务实体
					if("ing".equals(status)) {
						System.out.println("正在进行");
						Iterator<Task> it = taskList.iterator();
						while (it.hasNext()) {
							Task task = (Task) it.next();
							if(!Common.isEffectiveDate(now, task.getPublishTime(), teacherService.getTaskEndTime(virtualClassNum,task.getTaskId()))){
								it.remove();
							}
						}
					}else if("ed".equals(status)) {
						System.out.println("已结束");
						Iterator<Task> it = taskList.iterator();
						while (it.hasNext()) {
							Task task = (Task) it.next();
							if(Common.isEffectiveDate(now, task.getPublishTime(), teacherService.getTaskEndTime(virtualClassNum,task.getTaskId()))){
								it.remove();
							}
						}
					}

				}else {
					taskList = teacherService.teacherTaskAssortmentList(taskIdList,taskCategory);	//根据任务ID号获得任务实体
					if("ing".equals(status)) {
						Iterator<Task> it = taskList.iterator();
						while (it.hasNext()) {
							Task task = (Task) it.next();
							if(!Common.isEffectiveDate(now, task.getPublishTime(), teacherService.getTaskEndTime(virtualClassNum,task.getTaskId()))){
								it.remove();
							}
						}
					}else if("ed".equals(status)) {
						Iterator<Task> it = taskList.iterator();
						while (it.hasNext()) {
							Task task = (Task) it.next();
							if(Common.isEffectiveDate(now, task.getPublishTime(), teacherService.getTaskEndTime(virtualClassNum,task.getTaskId()))){
								it.remove();
							}
						}
					}
				}

				for (Task task : taskList) {
					point = teacherService.searchTaskPoint(task.getTaskType());//任务实体对象加入任务分值信息
					task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));
					upNum = teacherService.getUpNum(virtualClassNum, task.getTaskId());
					task.setUpNum(upNum);
					task.setTaskPoint(point);

					isEnd2taskList.put(task.getTaskId(),Common.isEffectiveDate(now, task.getPublishTime(), teacherService.getTaskEndTime(virtualClassNum,task.getTaskId())));
					timeEnd2taskList.put(task.getTaskId(), Common.timeDifference(now, teacherService.getTaskEndTime(virtualClassNum,task.getTaskId())));
					if(student!=null) {
						upTaskDetail2taskList.put(task.getTaskId(), studentService.getUpTaskDetail(task.getTaskId(), student.getStudentId()));
						accessoriesName2taskList.put(task.getTaskId(),studentService.getUpAccessories(task.getTaskId(), student.getStudentId()) );
						grade2taskList.put(task.getTaskId(), teacherService.getGrade(task.getTaskId(), student.getStudentId()));
					}

				}
				mv.addObject("taskList", taskList);
			}else {
				mv.addObject("taskList", null);
			}
			mv.addObject("timeEnd2taskList", timeEnd2taskList);
			mv.addObject("isEnd2taskList", isEnd2taskList);
			mv.addObject("identify", identify);
			mv.addObject("upTaskDetail2taskList", upTaskDetail2taskList);
			mv.addObject("accessoriesName2taskList", accessoriesName2taskList);
			mv.addObject("grade2taskList", grade2taskList);
			mv.addObject("teacher",teacher);
			mv.addObject("student",student);
			mv.addObject("readResult", readResult);
			mv.addObject("studentNum", studentNum);
			mv.setViewName("/jsp/VirtualClass/content");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;	

	}
	@RequestMapping("toteacherTaskListByTaskCategory")
	public ModelAndView toteacherTaskListByTaskCategory(HttpServletRequest request  ,@RequestParam(value="taskCategory") String taskCategory) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("taskCategory", taskCategory);
		mv.setViewName("/jsp/VirtualClass/otherfragram");
		return mv;
	}
	@RequestMapping("toteacherOtherTaskListContent")
	public ModelAndView toteacherOtherTaskListContent(HttpServletRequest request  ,@RequestParam(value="taskCategory") String taskCategory) {
		ModelAndView mv = new ModelAndView();
		mv.addObject("taskCategory", taskCategory);
		List<String> taskIdList=new ArrayList<String>();
		List<Task> taskList=new ArrayList<Task>();
		String readResult =null;
		Integer point=0;
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		try {
			taskIdList = teacherService.searchTaskId(virtualClassNum);//根据虚拟班级号获得任务列表
			if(!taskIdList.isEmpty()) {
				taskList = teacherService.teacherTaskAssortmentList(taskIdList,taskCategory);	//根据任务ID号获得任务实体
				if (!taskList.isEmpty()) {
					for (Task task : taskList) {
						point = teacherService.searchTaskPoint(task.getTaskType());//任务实体对象加入任务分值信息
						task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));
						task.setTaskPoint(point);
					}
					mv.addObject("taskList", taskList);
				}else {
					mv.addObject("taskList", null);
				}
			}else {
				mv.addObject("taskList", null);
			}
			mv.addObject("readResult", readResult);

			mv.setViewName("/jsp/VirtualClass/content");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;	
	}
	@RequestMapping("toCommentWork")
	public ModelAndView toCommentWork(HttpServletRequest request,@RequestParam(value="taskId") String taskId,@RequestParam(value="studentId") String studentId) {
		ModelAndView mv = new ModelAndView();
		Integer point=0;
		List<String> accessoriesName = new ArrayList<String>();
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		String virtualClassName = (String) request.getSession().getAttribute("virtualClassName");
		mv.addObject("taskId", taskId);
		request.getSession().setAttribute("studentId", studentId);
		Integer grade=null;
		String comment = null;
		Student student = null;
		Timestamp taskEndTime=null; 
		comment = teacherService.getComment(taskId, studentId);
		grade = teacherService.getGrade(taskId, studentId);
		System.out.println("分数是："+grade);
		mv.addObject("grade", grade);
		mv.addObject("comment", comment);
		try {
			student = teacherService.searchStudent(studentId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mv.addObject("student1", student);
		String upTaskDetail = null;

		try {
			Task task = teacherService.searchTask(taskId);
			task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));

			point = teacherService.searchTaskPoint(task.getTaskType());//任务实体对象加入任务分值信息
			task.setTaskPoint(point);
			taskEndTime = teacherService.getTaskEndTime(virtualClassNum,task.getTaskId());

			mv.addObject("task",task);
			mv.addObject("virtualClassName",virtualClassName);
			mv.addObject("virtualClassNum", virtualClassNum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(studentId!=null) {
			upTaskDetail = studentService.getUpTaskDetail(taskId, studentId);
		}

		accessoriesName = studentService.getUpAccessories(taskId, studentId);
		String teacherClassName = (String) request.getSession().getAttribute("teacherClassName");
		String courseName =  (String) request.getSession().getAttribute("courseName");
		mv.addObject("courseName", courseName);
		mv.addObject("teacherClassName", teacherClassName);
		mv.addObject("upTaskDetail", upTaskDetail);
		mv.addObject("taskEndTime", taskEndTime);
		mv.addObject("accessoriesName", accessoriesName);
		mv.setViewName("/jsp/VirtualClass/gradeWork");
		return mv;
	}
	@RequestMapping("commendWork")
	public String commendWork(HttpServletRequest request,@RequestParam(value="taskId") String taskId,@RequestParam(value="studentId") String studentId) {
		ModelAndView mv= new ModelAndView();
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");
		String virtualClassName = (String) request.getSession().getAttribute("virtualClassName");
		Integer grade = Integer.parseInt( request.getParameter("grade"));
		String comment = request.getParameter("comment");
		Timestamp commentTime = new Timestamp(System.currentTimeMillis());
		teacherService.setGradeAndComment(comment, grade, studentId, taskId,commentTime);

		return "redirect:/teacher/toTaskDetail?taskId="+taskId;

	}
	/**
	 * @author WENLI
	 * @throws IOException 
	 */
	@RequestMapping("ajaxIsCreated")
	public void ajaxIsCreated(HttpServletRequest request,@RequestParam("taskId")String taskId,HttpServletResponse response) throws IOException {
		String virtualClassNum = (String) request.getSession().getAttribute("virtualClassNum");

		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=UTF-8");
		//用时间来判断该任务是否存在于班级任务表中
		Timestamp time = teacherService.getTaskEndTime(virtualClassNum, taskId);
		PrintWriter out = response.getWriter();//给页面响应
		System.out.println("进入AJAX方法了");
		System.out.println("任务是否存在"+time);
		if(time==null) {
			out.print(JSONObject.toJSONString("false"));
		}else {
			out.print(JSONObject.toJSONString("true"));
		}
	}

	//	
	//	/**
	//	 * @author LiMing
	//	 * @param request
	//	 * @throws Exception 
	//	 * 模糊查询实体班级
	//	 * @return 返回对应资源
	//	 */
	//	@RequestMapping("/readRealClassToSelect/{realClass}")
	//	public ModelAndView readRealClassToSelect(@RequestParam(value="realClass") String realClass,HttpServletRequest request,@PathVariable String category) throws Exception{
	//		ModelAndView mv = new ModelAndView();
	//		List<RealClass> list = new ArrayList<RealClass>();
	//		try {
	//			list = teacherService.readRealClassToSelect(realClass);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		mv.addObject("listRealClass",list);//返回信息
	//		return mv;
	//	}

	/**
	 * @author LiMing
	 * @param request
	 * @throws Exception 
	 * 模糊查询实体班级
	 * @return 返回对应资源
	 */
	@RequestMapping("/readRealClassToSelect/{realClass}")
	public void readRealClassToSelect( HttpServletResponse response,@PathVariable(value="realClass") String realClass,HttpServletRequest request) throws Exception{
		List<RealClass> list = new ArrayList<RealClass>();
		if("nullRealClassName".equals(realClass))
		{
			list = teacherService.readRealClassToSelect("");
		}else {
			list = teacherService.readRealClassToSelect(realClass);
		}
		try {
			JSONArray  json  =  JSONArray.fromObject(list); //将获取的List集合存入 JSONArray中
			String result = json.toString();
			response.getWriter().print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//		/**
	//		 * @author LiMing
	//		 * 通过ajax获取班级列表
	//		 */
	//		@RequestMapping(value="ajaxGetRealClass")
	//		public void ajaxGetRealClass(HttpServletRequest request, @PathVariable(value="realClass") String realClass,HttpServletResponse response){
	//			try {
	//				List<RealClass> realClassList = new ArrayList<RealClass>();
	//				realClassList = teacherService.readRealClass(null);
	//				JSONArray  json  =  JSONArray.fromObject(realClassList); //将获取的List集合存入 JSONArray中
	//				String result = json.toString();
	//				response.getWriter().print(result);
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}
	@RequestMapping("ajaxGetTaskPerview")
	public void ajaxGetTaskPerview(HttpServletRequest request,HttpServletResponse response,@RequestParam("taskId")String taskId) {
		Task task = new Task();

		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=UTF-8");
			task = teacherService.searchTask(taskId);
			task.setAccessoryList(teacherService.searchAccessory(task.getTaskId()));
			JSONArray json = JSONArray.fromObject(task);
			String result = json.toString();
			response.getWriter().print(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/**
	 * @throws Exception 
	 * */
	@RequestMapping(value="/selectVirtualClassByTerm/{termId}")
	public ModelAndView selectVirtualClassByTerm(HttpServletRequest request,@PathVariable(value="termId") String termId) throws Exception {	
		ModelAndView mv = new ModelAndView();
		Term term = new Term();
		List<RealClass> realClass = new ArrayList<RealClass>();
		List<Term> termList = new ArrayList<Term>();
		List<VirtualClass> virtualList = new ArrayList<VirtualClass>();
		termList = teacherService.readTerm();//获取学期信息
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		virtualList = teacherService.getVirtualClassByCreatorId(teacher.getEmployeeNum());//获取虚拟班级列表
		if(virtualList!=null) {
			for (VirtualClass virtualClass : virtualList) {
				realClass = teacherService.getRealClassList(virtualClass.getVirtualClassNum());
				virtualClass.setRealClassList(realClass);
			}
		}
		//去除集合中的重复部分
		Iterator<VirtualClass> vi = virtualList.iterator();
		while(vi.hasNext()) {
			VirtualClass vii = vi.next();
			if(!vii.getTerm().equals(termId))
			{
				vi.remove();
			}
		}
		if(virtualList!=null) {
			for (VirtualClass virtualClass : virtualList) {
				term = studentService.readTermById(virtualClass.getTerm());
				virtualClass.setTerm(term.getStartYear()+"-"+term.getEndYear()+"	"+term.getTerm());
			}
		}
		mv.addObject("listTerm", termList);//返回信息
		mv.addObject("virtualClassList", virtualList);//返回信息
		mv.setViewName("/jsp/Teacher/teacherInfo/myclass_create");;//设置返回页面
		return mv;
	}

	/**
	 * 跳转到课程详细简介
	 * @param request
	 * @param courseDetail
	 * @return
	 */
	@RequestMapping(value="/toCourseIntroduceFrame/{courseId}")
	public ModelAndView toCourseIntroduceFrame(HttpServletRequest request, @PathVariable String courseId){
		ModelAndView mv = new ModelAndView();
		Course course = new Course();
		try {
			course = teacherService.getCourseById(courseId);
			mv.addObject("courseDetail", course.getCourseDetail());
			mv.setViewName("/jsp/Teacher/course_detail_introduce");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 跳转到课程详细教师圈
	 * @param request
	 * @param courseDetail
	 * @return
	 */
	@RequestMapping(value="/toCourseTeacherFrame/{courseId}")
	public ModelAndView toCourseTeacherFrame(HttpServletRequest request, @PathVariable String courseId){
		ModelAndView mv = new ModelAndView();
		// 查询教师圈教师信息
		List<Teacher> teacherList = teacherService.getTeachersByCourseId(courseId);
		request.getSession().setAttribute("teacherList", teacherList); //通过存入request在前台访问
		try {
			mv.addObject("teacherList", teacherList);
			mv.setViewName("/jsp/Teacher/course_detail_teachers");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return mv;
	}

	/**
	 * 根据教师ID的模糊查询
	 * */
	@RequestMapping("teacherForFuzzyQueryById/{teacherNum}")
	public void teacherForFuzzyQueryById(HttpServletRequest request,HttpServletResponse response,@PathVariable("teacherNum")String teacherNum) {
		List<Teacher> list = new ArrayList<Teacher>();
		try {
			if("nullTeacherName".equals(teacherNum))
			{
				list = teacherService.teacherForFuzzyQueryById("");
			}else {
				list = teacherService.teacherForFuzzyQueryById(teacherNum);
			}
			JSONArray  json  =  JSONArray.fromObject(list); //将获取的List集合存入 JSONArray中
			String result = json.toString();
			response.getWriter().print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	/**
	 * @author WENLI
	 * @param request
	 * @return
	 * 去教师分析成绩
	 */
	@RequestMapping("toAnalyseGrade")
	public ModelAndView toAnalyseGrade(HttpServletRequest request) {
		ModelAndView mv= new ModelAndView();
		String virtualClassNum=(String) request.getSession().getAttribute("virtualClassNum");
		int workPublishNum=0;	//作业发布次数
		int trialPublishNum=0;	//实验发布次数
		int courseDesignPublishNum=0;	//课设发布次数
		int turnClassPublishNum=0;	//翻转课堂发布次数
		int attencePublishNum=0;	//考勤发布次数
		//成绩汇总中作业成绩汇总
		List<HashMap<String, Integer>> studentWorkSumGrade = new ArrayList<HashMap<String, Integer>>();
		//成绩汇总中作业成绩汇总
		List<HashMap<String, Integer>> studentTrialSumGrade = new ArrayList<HashMap<String, Integer>>();
		//成绩汇总中作业成绩汇总
		List<HashMap<String, Integer>> studentCourseDesignSumGrade = new ArrayList<HashMap<String, Integer>>();
		//成绩汇总中作业成绩汇总
		List<HashMap<String, Integer>> studentTurnClassSumGrade = new ArrayList<HashMap<String, Integer>>();
		//成绩汇总中作业成绩汇总
		List<HashMap<String, Integer>> studentAttenceSumGrade = new ArrayList<HashMap<String, Integer>>();
		workPublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "work");
		trialPublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "trial");
		courseDesignPublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "course_design");
		turnClassPublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "turn_class");
		attencePublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "attence");
		mv.addObject("workPublishNum", workPublishNum);
		mv.addObject("trialPublishNum", trialPublishNum);
		mv.addObject("courseDesignPublishNum", courseDesignPublishNum);
		mv.addObject("turnClassPublishNum", turnClassPublishNum);
		mv.addObject("attencePublishNum", attencePublishNum);
		mv.setViewName("/jsp/Teacher/gradeAnalyse");
		return mv;
	}
	@RequestMapping("ajaxGetStudentGradeList")
	public void ajaxGetStudentGradeList(HttpServletRequest request,HttpServletResponse response) {
		String virtualClassNum=(String) request.getSession().getAttribute("virtualClassNum");
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//定义用于成绩绑定的所有HASHMAP
		HashMap<String, Integer> studentWorkGrade= new HashMap<String, Integer>();
		HashMap<String, Integer> studentTrialGrade= new HashMap<String, Integer>();
		HashMap<String, Integer> studentCourseDesignGrade= new HashMap<String, Integer>();
		HashMap<String, Integer> studentTurnClassGrade= new HashMap<String, Integer>();
		HashMap<String, Integer> studentAttenceGrade= new HashMap<String, Integer>();
		HashMap<String, Integer> studentWorkGradeNum= new HashMap<String, Integer>();
		HashMap<String, Integer> studentTrialGradeNum= new HashMap<String, Integer>();
		HashMap<String, Integer> studentCourseDesignGradeNum= new HashMap<String, Integer>();
		HashMap<String, Integer> studentTurnClassGradeNum= new HashMap<String, Integer>();
		HashMap<String, Integer> studentAttenceGradeNum= new HashMap<String, Integer>();
		List<Student> studentList = new ArrayList<Student>();
		studentList = teacherService.getStudentList(virtualClassNum);
		//根据学生ID绑定所有HASHMAP的值
		for (Student student : studentList) {
			studentWorkGrade.put(student.getStudentId(), teacherService.getStudentGrade(student.getStudentId(), virtualClassNum, "work"));
			studentTrialGrade.put(student.getStudentId(), teacherService.getStudentGrade(student.getStudentId(), virtualClassNum, "trial"));
			studentCourseDesignGrade.put(student.getStudentId(), teacherService.getStudentGrade(student.getStudentId(), virtualClassNum, "course_design"));
			studentTurnClassGrade.put(student.getStudentId(), teacherService.getStudentGrade(student.getStudentId(), virtualClassNum, "turn_class"));
			studentAttenceGrade.put(student.getStudentId(), teacherService.getStudentGrade(student.getStudentId(), virtualClassNum, "attence"));
			studentWorkGradeNum.put(student.getStudentId(), teacherService.getStudentGradeNum(student.getStudentId(), virtualClassNum, "work"));
			studentTrialGradeNum.put(student.getStudentId(), teacherService.getStudentGradeNum(student.getStudentId(), virtualClassNum, "trial"));
			studentCourseDesignGradeNum.put(student.getStudentId(), teacherService.getStudentGradeNum(student.getStudentId(), virtualClassNum, "course_design"));
			studentTurnClassGradeNum.put(student.getStudentId(), teacherService.getStudentGradeNum(student.getStudentId(), virtualClassNum, "turn_class"));
			studentAttenceGradeNum.put(student.getStudentId(), teacherService.getStudentGradeNum(student.getStudentId(), virtualClassNum, "attence"));
		}

		int max=0;
		System.out.println("已经进来了...");
		//定义用于传递的JSONARRAY数组
		com.alibaba.fastjson.JSONArray arr=new com.alibaba.fastjson.JSONArray();
		//填充JSON数组内容
		for (Student student : studentList) {
			JSONObject ob=new JSONObject();
			ob.put("studentName", student.getStudentName());
			ob.put("studentId", student.getStudentId());
			ob.put("workGrade", studentWorkGrade.get(student.getStudentId())+"/"+studentWorkGradeNum.get(student.getStudentId()));
			ob.put("trialGrade", studentTrialGrade.get(student.getStudentId())+"/"+studentTrialGradeNum.get(student.getStudentId()));
			ob.put("courseDesignGrade", studentCourseDesignGrade.get(student.getStudentId())+"/"+studentCourseDesignGradeNum.get(student.getStudentId()));
			ob.put("turnClassGrade", studentTurnClassGrade.get(student.getStudentId())+"/"+studentTurnClassGradeNum.get(student.getStudentId()));
			ob.put("attenceGrade", studentAttenceGrade.get(student.getStudentId())+"/"+studentAttenceGradeNum.get(student.getStudentId()));
			ob.put("workGradeNum", studentWorkGradeNum.get(student.getStudentId()));
			ob.put("trialGradeNum", studentTrialGradeNum.get(student.getStudentId()));
			ob.put("courseDesignGradeNum", studentCourseDesignGradeNum.get(student.getStudentId()));
			ob.put("turnClassGradeNum", studentTurnClassGradeNum.get(student.getStudentId()));
			ob.put("attenceGradeNum", studentAttenceGradeNum.get(student.getStudentId()));
			ob.put("sumGrade",studentWorkGrade.get(student.getStudentId())
					+studentTrialGrade.get(student.getStudentId())
					+studentCourseDesignGrade.get(student.getStudentId())
					+studentTurnClassGrade.get(student.getStudentId())
					+studentAttenceGrade.get(student.getStudentId()));
			//装进数组
			arr.add(ob);
		}
		//根据成绩排序
		List<JSONObject>list = com.alibaba.fastjson.JSONArray.parseArray(arr.toJSONString(),JSONObject.class);
		System.out.println("排序前："+arr);
		Collections.sort(list, new Comparator<JSONObject>() {
			@Override
			public int compare(JSONObject o1, JSONObject o2) {
				int a = o1.getInteger("sumGrade");
				int b = o2.getInteger("sumGrade");
				if (a > b) {
					return -1;
				} else if(a == b) {
					return 0;
				} else
					return 1;
			}
		});
		com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(list.toString());
		System.out.println("排序后：" + jsonArray);
		//排序完毕之后绑定学生排名
		if(jsonArray.size()>0){
			for(int i=0;i<jsonArray.size();i++){
				JSONObject ob = jsonArray.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
				ob.put("studentRank", i+1);
			}
		}
		String result = jsonArray.toString();
		try {
			response.getWriter().print(result);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	@RequestMapping("toTeacherMyInfo")
	public ModelAndView toTeacherMyInfo(HttpServletRequest request ,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		mv.addObject("teacher", teacher);
		mv.setViewName("jsp/teacher_personal_center/index");
		return mv;
	}
	@SuppressWarnings("unused")
	@RequestMapping("toTeacherMyCourse")
	public ModelAndView toTeacherMyCourse(HttpServletRequest request ,HttpServletResponse response) {
		
		ModelAndView mv = new ModelAndView();
		List<String> courseIdListForMe ;	//自己创建的课程ID号
		List<Course> courseListForMe = null ;		//自己课程实体
		
		List<String> courseIdListByJoin;		//加入别人的课程ID号
		List<Course> courseListByJoin = null;		//别人课程实体
		
		List<String> courseIdListByInterset;		//加入别人的课程ID号
		List<Course> courseListByInterset = null;		//别人课程实体
		
		Teacher teacher = null;
		List<String> publishTimeForMe = new ArrayList<>();
		List<String> publishTimeByJoin = new ArrayList<>();
		List<String> publishTimeByInterset = new ArrayList<>();
		
		List<String> teacherIdList = null;
		List<Teacher> teacherList = null;
		//创建老师集合的目的是：课程与创建者的匹配
		List<String> teacherNamesForMe = new ArrayList<String>();
		List<String> teacherNamesByJoin = new ArrayList<String>();
		List<String> teacherNamesByInterset = new ArrayList<String>();
		try {
			courseIdListForMe = teacherService.courseIdList((String) request.getSession().getAttribute("teacherId"), 1);
			courseListForMe = teacherService.courseList(courseIdListForMe);
			courseIdListByJoin =teacherService.courseIdList((String) request.getSession().getAttribute("teacherId"), 0);
			courseListByJoin = teacherService.courseList(courseIdListByJoin);
			//填充我创建的课程实体
			if(courseListForMe!=null) {
				for (Course course : courseListForMe ) {
					//限制显示字数
					if(!"".equals(course.getCourseDetail())){
						String de = course.getCourseDetail().replaceAll("<p>", "");
						de = de.replaceAll("</p>","");
						
						if(course.getCourseDetail().length()>=70){
							
							de = de.substring(0, 63);
						}
						course.setCourseDetail(de);
					}
					teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
					course.setTeacherList(teacherList);
					publishTimeForMe.add(course.getPublishTime().toString().substring(0,10));
				}
				for (int i = 0; i < courseListForMe.size(); i++) {
					teacher = (Teacher) request.getSession().getAttribute("teacher");
					teacherNamesForMe.add(teacher.getTeacherName());
				}
			}//end
			//填充我加入的课程实体
			if(courseListByJoin!=null) {

				for (Course course : courseListByJoin ) {
					//限制显示字数
					if(!"".equals(course.getCourseDetail())){
						String de = course.getCourseDetail().replaceAll("<p>", "");
						de = de.replaceAll("</p>","");
						
						if(course.getCourseDetail().length()>=70){
							
							de = de.substring(0, 63);
						}
						course.setCourseDetail(de);
					}
					teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
					course.setTeacherList(teacherList);
					publishTimeByJoin.add(course.getPublishTime().toString().substring(0,10));
				}
				for (Course course : courseListByJoin) {
					teacherNamesByJoin.add(teacherService.getTeacherNameById(course.getPublisherId()));
				}
			}//end
			//填充我创建的课程实体
			if(courseListByInterset!=null) {
				for (Course course : courseListByInterset ) {
					//限制显示字数
					if(!"".equals(course.getCourseDetail())){
						String de = course.getCourseDetail().replaceAll("<p>", "");
						de = de.replaceAll("</p>","");
						
						if(course.getCourseDetail().length()>=70){
							
							de = de.substring(0, 63);
						}
						course.setCourseDetail(de);
					}
					teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
					course.setTeacherList(teacherList);
					publishTimeByInterset.add(course.getPublishTime().toString().substring(0,10));
				}
				for (int i = 0; i < courseListByInterset.size(); i++) {
					teacher = (Teacher) request.getSession().getAttribute("teacher");
					teacherNamesByInterset.add(teacher.getTeacherName());
				}
			}//end
		} catch (Exception e) {
			e.printStackTrace();
		}
		//设置返回我创建的课程前台数据
		mv.addObject("teacherNamesForMe",teacherNamesForMe);
		mv.addObject("courseListForMe", courseListForMe);
		mv.addObject("publishTimeForMe",publishTimeForMe);
		
		
		//设置返回我加入的课程前台数据
		mv.addObject("teacherNamesByJoin",teacherNamesByJoin);
		mv.addObject("courseListByJoin", courseListByJoin);
		mv.addObject("publishTimeByJoin",publishTimeByJoin);
		
		//设置返回我加入的课程前台数据
		mv.addObject("teacherNamesByInterset",teacherNamesByInterset);
		mv.addObject("courseListByInterset", courseListByInterset);
		mv.addObject("publishTimeByInterset",publishTimeByInterset);
		
		
		mv.addObject("teacher", teacher);
		
		mv.setViewName("jsp/teacher_personal_center/mycourse");
		return mv;
	}
	@SuppressWarnings("unused")
	@RequestMapping("toTeacherMyClass")
	public ModelAndView toTeacherMyClass(HttpServletRequest request ,HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		List<Term> termList = null;
		List<VirtualClass> virtualClassList= null;
		List<RealClass> realClassList = null;
		List<String> realClassIdList = null;
		String creatorId = (String) request.getSession().getAttribute("teacherId");
		virtualClassList = teacherService.getVirtualClassByCreatorId(creatorId);
		if(virtualClassList!=null) {
			for (VirtualClass virtualClass : virtualClassList) {
				realClassList = teacherService.getRealClassList(virtualClass.getVirtualClassNum());
				virtualClass.setRealClassList(realClassList);
				String term = virtualClass.getTerm();
				Term  a = teacherService.getTermById(term);
				virtualClass.setTerm(a.getStartYear()+"-"+a.getEndYear()+" 学年"+a.getTerm());
			}
		}
		try {
			termList = teacherService.readTerm();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mv.addObject("termList", termList);
		mv.addObject("virtualClassList", virtualClassList);
		
		Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
		mv.addObject("teacher", teacher);
		
		mv.setViewName("jsp/teacher_personal_center/myclass");
		return mv;

	}

	@RequestMapping("toMyAchievement")
	public ModelAndView toMyAchievement() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/jsp/Teacher/teacherInfo/myAchievement");
		return mv;
	}	


	@RequestMapping("queryCourseByPartName/{courseName}")
	public void queryCourseByPartName(HttpServletRequest request,HttpServletResponse response,@PathVariable("courseName")String courseName) {
		List<Course> list = new ArrayList<Course>();
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			if("nullCourseName".equals(courseName))
			{
				list = teacherService.queryCourseByPartName("");
			}else {
				list = teacherService.queryCourseByPartName(courseName);
			}
			JSONArray  json  =  JSONArray.fromObject(list); //将获取的List集合存入 JSONArray中
			String result = json.toString();
			response.getWriter().print(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	@RequestMapping("getTeacherNameById/{teacherId}")
	public void getTeacherNameById(HttpServletRequest request,HttpServletResponse response,@PathVariable("teacherId")String teacherId) {
		String name = "";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			name = teacherService.getTeacherNameById(teacherId);
			response.getWriter().print(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("getStudentNameById/{studentId}")
	public void getStudentNameById(HttpServletRequest request,HttpServletResponse response,@PathVariable("studentId")String studentId) {
		
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			Student stu = studentService.studentLoginByEmployeeNum(studentId);
			response.getWriter().print(stu.getStudentName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	/**
	 * 添加教师团队成员辅助函数，方便之后排序
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping("addTeacherTeam/{id}")
	public ModelAndView addTeacherTeam(HttpServletRequest request,HttpServletResponse response,@PathVariable String id) {
		ModelAndView mv = new ModelAndView();
		try {
			if(isLogin(request)){
				request.getSession().setAttribute("readResult", "请先登录！");//返回信息
				mv.setViewName("/jsp/Teacher/index");//设置返回页面
				return mv;
			}
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
			Teacher teacher = (Teacher) request.getSession().getAttribute("teacher");
			//获取manager
			Integer manager  = teacherService.getMaxManager(teacher.getEmployeeNum());
			if(manager == null){
				manager = 2;
			}
			else{
				manager +=1;
			}
			//添加
			teacherService.addTeamMember(teacher.getEmployeeNum(),id,manager);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	/**
	 * 判断登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value="isLogin")
	private boolean isLogin(HttpServletRequest request) {
			if(request.getSession().getAttribute("teacher") == null && request.getSession().getAttribute("student") == null){
				
				return true;
			}
		return false;
	}
}
