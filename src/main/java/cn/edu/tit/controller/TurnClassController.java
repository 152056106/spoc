package cn.edu.tit.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import cn.edu.tit.bean.Accessory;
import cn.edu.tit.bean.Course;
import cn.edu.tit.bean.RealClass;
import cn.edu.tit.bean.Student;
import cn.edu.tit.bean.Task;
import cn.edu.tit.bean.Teacher;
import cn.edu.tit.bean.Term;
import cn.edu.tit.bean.TurnClassAccessory;
import cn.edu.tit.bean.TurnClassAchievement;
import cn.edu.tit.bean.TurnClassAfterModify;
import cn.edu.tit.bean.TurnClassFeedback;
import cn.edu.tit.bean.TurnClassPlanForClass;
import cn.edu.tit.bean.TurnClassPlanForCourse;
import cn.edu.tit.bean.TurnClassTeam;
import cn.edu.tit.bean.VirtualClass;
import cn.edu.tit.common.Common;
import cn.edu.tit.iservice.IStudentService;
import cn.edu.tit.iservice.ITeacherService;
import cn.edu.tit.iservice.ITurnClassService;
import net.sf.json.JSONArray;

@RequestMapping("/turnClass")
@Controller
public class TurnClassController {

	@Autowired
	private ITurnClassService turnClassService;
	@Autowired
	private ITeacherService teacherService;

	@Autowired
	private IStudentService studentService;

	@RequestMapping(value="toTurnClassMainPage",method= {RequestMethod.GET})
	public ModelAndView toCourseSecond(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		List<Task> list = new ArrayList<>();//初步的list集合，其中的任何一个task可能对应多个虚拟班级
		List<Task> endList = new ArrayList<>();//最终返回到前台的list集合
		list = turnClassService.getAllTurnClass();
		List<String> virList = new ArrayList<>();//一个任务可能对应多个班级
		for (Task task : list) {
			String taskId = task.getTaskId();
			virList = teacherService.searchTurnTaskMapVir(taskId);//返回的是virtualClass集合
			Teacher tea = teacherService.teacherLoginByEmployeeNum(task.getPublisherId());//一个任务下的教师只查询一次
			task.setPublisherId(tea.getTeacherName());
			for (String string : virList) {
				Task t =new Task();
				t.setTaskId(task.getTaskId());
				t.setTaskTitle(task.getTaskTitle());
				t.setTaskDetail(task.getTaskDetail());
				t.setPublisherId(task.getPublisherId());
				t.setPublishTime(task.getPublishTime());
				t.setVirtualClassNum(task.getVirtualClassNum());
				t.setKnowledgePoints(task.getKnowledgePoints());
				t.setUseNum(task.getUseNum());//设置使用次数为1
				t.setWatchNum(task.getWatchNum());
				t.setCourseId(task.getCourseId());
				t.setTaskType(task.getTaskType());
				t.setStatus(task.getStatus());
				List<RealClass> listReal = new ArrayList<>();
				listReal = teacherService.getRealClassList(string);
				t.setClassList(listReal);
				System.out.println(task.toString()+"------------------------");
				endList.add(t);
			}
		}
		mv.addObject("taskList",endList);
		mv.setViewName("/jsp/TurnClassJsp/turnClassMain");
		return mv;
	}

	@RequestMapping(value="toTurnClassTeamDetail",method= {RequestMethod.GET})
	public ModelAndView toTurnClassDetail(HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		ModelAndView mv = new ModelAndView();
		Task task = turnClassService.getTaskById(taskId);
		List<Accessory> taskAccessory = new ArrayList<>();
		taskAccessory = teacherService.searchAccessory(taskId);
		task.setAccessoryList(taskAccessory);
		String courseId = task.getCourseId();
		Course course = teacherService.getCourseById(courseId);
		TurnClassTeam tct = new TurnClassTeam();
		tct = turnClassService.getTeamById(teamId);
		List<Student> listStudent = new ArrayList<>();
		if(tct!=null)
		{	
			String memberId = tct.getMemberId();
			String[] a = memberId.split(",");
			for (int i = 0; i < a.length; i++) {
				listStudent.add(studentService.studentLoginByEmployeeNum(a[i]));
			}
			mv.addObject("listStudent",listStudent);
			tct.setListStu(listStudent);
		}
		mv.addObject("taskTeam",tct);
		mv.addObject("task",task);
		mv.addObject("course",course);
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
			Integer attention = studentService.getManagerByStudentId(student.getStudentId(), courseId,2);
			request.setAttribute("attention", attention);
		}
		mv.setViewName("/jsp/TurnClassJsp/turnClassTeamDetail");
		return mv;
	}

	@RequestMapping(value="toTurnClassTeam",method= {RequestMethod.GET})
	public ModelAndView toTurnClassTeam(HttpServletRequest request,@RequestParam("taskId")String taskId) throws Exception {
		ModelAndView mv = new ModelAndView();
		Task task = teacherService.getTaskById(taskId);
		task.setTaskEndTime(teacherService.getTaskEndTime(task.getVirtualClassNum(), taskId));
		List<Accessory> taskAccessory = new ArrayList<>();
		taskAccessory = teacherService.searchAccessory(taskId);
		task.setAccessoryList(taskAccessory);
		Course course = teacherService.getCourseById(task.getCourseId());
		String courseId = course.getCourseId();
		List<TurnClassTeam> teamList = new ArrayList<>();
		teamList = turnClassService.getTaskTeam(taskId);
		mv.addObject("task",task);
		mv.addObject("course",course);
		mv.addObject("teamList",teamList);
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
			Integer attention = studentService.getManagerByStudentId(student.getStudentId(), courseId,2);
			request.setAttribute("attention", attention);
		}
		List<TurnClassTeam> listTeam = new ArrayList<>();
		listTeam = turnClassService.readAllTeamForTask(taskId);
		List<Student> listStudent = null;
		for (TurnClassTeam turnClassTeam : listTeam) {
			listStudent = new ArrayList<>();
			String[] a = turnClassTeam.getMemberId().split(",");
			for (int i = 0; i < a.length; i++) {
				listStudent.add(studentService.studentLoginByEmployeeNum(a[i]));
			}
			turnClassTeam.setListStu(listStudent);
		}
		mv.addObject("listTeam",listTeam);
		mv.setViewName("/jsp/TurnClassJsp/turnClassTeam");
		return mv;
	}

	@RequestMapping(value="toDesignBeforeClass",method= {RequestMethod.GET})
	public ModelAndView toDesignBeforeClass(HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		ModelAndView mv = new ModelAndView();
		Task task = turnClassService.getTaskById(taskId);
		TurnClassTeam tct = turnClassService.getTeamById(teamId);
		List<Student> listStudent = new ArrayList<>();
		String[] a = tct.getMemberId().split(",");
		for (int i = 0; i < a.length; i++) {
			listStudent.add(studentService.studentLoginByEmployeeNum(a[i]));
		}
		tct.setListStu(listStudent);
		mv.addObject("task",task);
		mv.addObject("team",tct);
		mv.setViewName("/jsp/TurnClassJsp/prepareForCourse");
		mv.setViewName("/jsp/TurnClassJsp/designBeforeClass");
		return mv;
	}

	@RequestMapping(value="toPrepareForCourse",method= {RequestMethod.GET})
	public ModelAndView toPrepareForCourse(HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		ModelAndView mv = new ModelAndView();
		Task task = turnClassService.getTaskById(taskId);
		TurnClassTeam tct = turnClassService.getTeamById(teamId);
		List<Student> listStudent = new ArrayList<>();
		String[] a = tct.getMemberId().split(",");
		for (int i = 0; i < a.length; i++) {
			listStudent.add(studentService.studentLoginByEmployeeNum(a[i]));
		}
		tct.setListStu(listStudent);
		mv.addObject("task",task);
		mv.addObject("team",tct);
		mv.setViewName("/jsp/TurnClassJsp/prepareForCourse");
		return mv;
	}

	@RequestMapping(value="toCourseFeedback",method= {RequestMethod.GET})
	public ModelAndView toCourseFeedback(HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		ModelAndView mv = new ModelAndView();
		Task task = turnClassService.getTaskById(taskId);
		TurnClassFeedback tc = null;
		tc = turnClassService.queryFeedBack(taskId,teamId);
		TurnClassAccessory ac  =new TurnClassAccessory();
		ac = turnClassService.queryTurnAccessory(taskId,teamId);
		TurnClassTeam tct = turnClassService.getTeamById(teamId);
		List<Student> listStudent = new ArrayList<>();
		String[] a = tct.getMemberId().split(",");
		for (int i = 0; i < a.length; i++) {
			if(tc!=null&&tc.getAuthorId().equals(a[i]))
			{
				continue;
			}
			listStudent.add(studentService.studentLoginByEmployeeNum(a[i]));
		}
		tct.setListStu(listStudent);
		mv.addObject("feedBack",tc);
		mv.addObject("feedBackAccessory",ac);
		mv.addObject("task",task);
		mv.addObject("team",tct);
		mv.setViewName("/jsp/TurnClassJsp/courseFeedback");
		return mv;
	}


	@RequestMapping(value="toEndAchievement",method= {RequestMethod.GET})
	public ModelAndView toEndAchievement(HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		ModelAndView mv = new ModelAndView();
		Task task = turnClassService.getTaskById(taskId);
		TurnClassTeam tct = turnClassService.getTeamById(teamId);
		List<Student> listStudent = new ArrayList<>();
		String[] a = tct.getMemberId().split(",");
		for (int i = 0; i < a.length; i++) {
			listStudent.add(studentService.studentLoginByEmployeeNum(a[i]));
		}
		tct.setListStu(listStudent);
		mv.addObject("task",task);
		mv.addObject("team",tct);
		mv.setViewName("/jsp/TurnClassJsp/endAchievement");
		return mv;
	}

	@RequestMapping(value="toCourseAfterEdit",method= {RequestMethod.GET})
	public ModelAndView toCourseAfterEdit(HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		ModelAndView mv = new ModelAndView();
		Task task = turnClassService.getTaskById(taskId);
		TurnClassTeam tct = turnClassService.getTeamById(teamId);
		List<Student> listStudent = new ArrayList<>();
		String[] a = tct.getMemberId().split(",");
		for (int i = 0; i < a.length; i++) {
			listStudent.add(studentService.studentLoginByEmployeeNum(a[i]));
		}
		tct.setListStu(listStudent);
		mv.addObject("task",task);
		mv.addObject("team",tct);
		mv.setViewName("/jsp/TurnClassJsp/courseAfterEdit");
		return mv;
	}

	@RequestMapping(value="getRealClassAllStudent/{taskId}")
	public void getRealClassAllStudent(@PathVariable String taskId,HttpServletRequest request,HttpServletResponse response) throws Exception {
		Student stu = (Student) request.getSession().getAttribute("student");
		List<TurnClassTeam> listTeam =  turnClassService.getTaskTeam(taskId);//获取所有的任务下队伍
		List<Student> listStudent = new ArrayList<>();//此集合存放所有的已经被拉入团队的成员
		listStudent.add(stu);//将此时的登录者放入集合当中,决定了listStudent 永远不为空
		if(listTeam.size()!=0)//如果返回的队伍集合不为空，则遍历之，将之变为对应的对象
		{
			for (TurnClassTeam team : listTeam) {
				listStudent.add(studentService.studentLoginByEmployeeNum(team.getLeaderId()));
				String[] a = team.getMemberId().split(",");
				for (int i = 0; i < a.length; i++) {
					listStudent.add(studentService.studentLoginByEmployeeNum(a[i]));
				}
			}
		}
		List<Student> allStudent = new ArrayList<>();
		try {
			allStudent = turnClassService.getAllStudentByRealClass(stu.getClassNum());//获取全部的班级学生
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		com.alibaba.fastjson.JSONArray arr=new com.alibaba.fastjson.JSONArray();
		Iterator<Student> iterator = allStudent.iterator();//遍历集合
		Iterator<Student> iterator2 = listStudent.iterator();//遍历集合
		while (iterator.hasNext()) {//外部遍历allStudent
			Student integer = iterator.next();
			String id2 = integer.getStudentId();//获取当前外部集合中的对象数据
			while (iterator2.hasNext()) {//内部遍历的是listStudent
				Student i = iterator2.next();
				String id1 = i.getStudentId();
				if (id2.equals(id1)) {
					iterator.remove();
					continue;
				}
			}
			JSONObject ob=new JSONObject();
			ob.put("id", integer.getStudentId());
			ob.put("name", integer.getStudentName());
			arr.add(ob);
		}
		String result = arr.toString();
		try {
			response.getWriter().print(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	@RequestMapping(value="getNameForStudent")
	public void getNameForStudent(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String num = request.getParameter("num");
		Student stu = new Student();
		try {
			stu = studentService.studentLoginByEmployeeNum(num);
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String result = stu.getStudentName().toString();
		try {
			response.getWriter().print(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@RequestMapping(value="statusCheckout")
	public void statusCheckout(HttpServletRequest request,HttpServletResponse response,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		List<TurnClassPlanForClass> list = new ArrayList<>();
		Integer planStage;//计划阶段
		Integer demandStage;//需求阶段
		Integer designStage;//设计阶段
		Integer arithmeticStage;//关键算法技术阶段
		Integer testStage;//测试阶段
		String result = "";
		try {
			list = turnClassService.statusCheckout(teamId,taskId);
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if(list.size()==0)
		{
			result = "null";
		}
		if(list.size()!=0)
		{
			for (TurnClassPlanForClass turnClassPlanForClass : list) {
				planStage =	turnClassPlanForClass.getPlanStage();
				demandStage = turnClassPlanForClass.getDemandStage();
				designStage =  turnClassPlanForClass.getDesignStage();
				arithmeticStage = turnClassPlanForClass.getArithmeticStage();
				testStage = turnClassPlanForClass.getTestStage();
				/**
				 * 下面的if 顺序严格不可变
				 * */
				if(planStage==1&&planStage!=null)
				{
					result = "planStage";
				}
				if(demandStage==1&&demandStage!=null)
				{
					result = "demandStage";
				}
				if(designStage==1&&designStage!=null)
				{
					result = "designStage";
				}
				if(arithmeticStage==1&&arithmeticStage!=null)
				{
					result = "arithmeticStage";
				}
				if(testStage==1&&testStage!=null)
				{
					result = "all";
				}
			}
		}
		try {
			response.getWriter().print(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	@RequestMapping(value="statusCheckoutEnd")
	public void statusCheckoutEnd(HttpServletRequest request,HttpServletResponse response,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		List<TurnClassAchievement> list = new ArrayList<>();
		Integer planStage;//计划阶段
		Integer demandStage;//需求阶段
		Integer designStage;//设计阶段
		Integer arithmeticStage;//关键算法技术阶段
		Integer testStage;//测试阶段
		String result = "";
		try {
			list = turnClassService.statusCheckoutEnd(teamId,taskId);
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if(list.size()==0)
		{
			result = "null";
		}
		if(list.size()!=0)
		{
			for (TurnClassAchievement turnClassPlanForClass : list) {
				planStage =	turnClassPlanForClass.getPlanStage();
				demandStage = turnClassPlanForClass.getDemandStage();
				designStage =  turnClassPlanForClass.getDesignStage();
				arithmeticStage = turnClassPlanForClass.getArithmeticStage();
				testStage = turnClassPlanForClass.getTestStage();
				/**
				 * 下面的if 顺序严格不可变
				 * */
				if(planStage==1&&planStage!=null)
				{
					result = "planStage";
				}
				if(demandStage==1&&demandStage!=null)
				{
					result = "demandStage";
				}
				if(designStage==1&&designStage!=null)
				{
					result = "designStage";
				}
				if(arithmeticStage==1&&arithmeticStage!=null)
				{
					result = "arithmeticStage";
				}
				if(testStage==1&&testStage!=null)
				{
					result = "all";
				}
			}
		}
		try {
			response.getWriter().print(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	@RequestMapping(value="statusCheckoutModify")
	public void statusCheckoutModify(HttpServletRequest request,HttpServletResponse response,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId) throws Exception {
		List<TurnClassAfterModify> list = new ArrayList<>();
		Integer planStage;//计划阶段
		Integer demandStage;//需求阶段
		Integer designStage;//设计阶段
		Integer arithmeticStage;//关键算法技术阶段
		Integer testStage;//测试阶段
		String result = "";
		try {
			list = turnClassService.statusCheckoutModify(teamId,taskId);
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if(list.size()==0)
		{
			result = "null";
		}
		if(list.size()!=0)
		{
			for (TurnClassAfterModify turnClassPlanForClass : list) {
				planStage =	turnClassPlanForClass.getPlanStage();
				demandStage = turnClassPlanForClass.getDemandStage();
				designStage =  turnClassPlanForClass.getDesignStage();
				arithmeticStage = turnClassPlanForClass.getArithmeticStage();
				testStage = turnClassPlanForClass.getTestStage();
				/**
				 * 下面的if 顺序严格不可变
				 * */
				if(planStage==1&&planStage!=null)
				{
					result = "planStage";
				}
				if(demandStage==1&&demandStage!=null)
				{
					result = "demandStage";
				}
				if(designStage==1&&designStage!=null)
				{
					result = "designStage";
				}
				if(arithmeticStage==1&&arithmeticStage!=null)
				{
					result = "arithmeticStage";
				}
				if(testStage==1&&testStage!=null)
				{
					result = "all";
				}
			}
		}
		try {
			response.getWriter().print(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	@RequestMapping(value="addTaskTeam",method= {RequestMethod.GET})
	public ModelAndView addTaskTeam(HttpServletRequest request,@RequestParam("projectIntro")String projectIntro,@RequestParam("projectName")String projectName,@RequestParam("taskId")String taskId,@RequestParam("compere")String compere,@RequestParam("compereId")String compereId,@RequestParam("selectStudentId")String selectStudentId) throws Exception {
		ModelAndView mv = new ModelAndView();
		TurnClassTeam tt =  new TurnClassTeam();
		try {
			tt.setAcceptTime(new Timestamp(System.currentTimeMillis()));
			tt.setProjectName(projectName);
			tt.setLeaderId(compereId);
			tt.setLeaderName(compere);
			tt.setMemberId(selectStudentId);
			tt.setMemberName(null);
			tt.setTaskId(taskId);
			tt.setProjectIntro(projectIntro);
			tt.setTeamId(Common.uuid());
			tt.setVirtualClassId(null);
			tt.setStatus("正在进行");
			turnClassService.insertTaskTeam(tt);
		} catch (Exception e) {
			mv = toTurnClassTeam(request,taskId);
		}
		mv = toTurnClassTeam(request,taskId);
		return mv;
	}

	@RequestMapping(value="getStudentTurnClass",method= {RequestMethod.GET})
	public ModelAndView getStudentTurnClass(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		List<TurnClassTeam> teamList = new ArrayList<>();
		Student stu = (Student) request.getSession().getAttribute("student");
		String stuId = stu.getStudentId();
		List<String> taskIdList = new ArrayList<>();//暂存taskId
		List<Task> taskList = new ArrayList<>();
		try {
			teamList = turnClassService.getAllTeam();
			//便利team集合，筛选此学生拥有的任务
			for (TurnClassTeam turnClassTeam : teamList) {
				String leaderId = turnClassTeam.getLeaderId();
				String[] a = turnClassTeam.getMemberId().split(",");
				if(leaderId.equals(stuId))
				{
					taskIdList.add(turnClassTeam.getTaskId());//存储taskId
					continue;
				}
				for (int i = 0; i < a.length; i++) {
					String string = a[i];
					if(string.equals(stuId))
					{
						taskIdList.add(turnClassTeam.getTaskId());//存储taskId
						continue;
					}
				}
			}
			for (String string : taskIdList) {
				Task task = turnClassService.getTaskById(string);
				taskList.add(task);
			}
		} catch (Exception e) {
			mv.addObject("taskList",null);
		}
		mv.addObject("taskList",taskList);
		mv.setViewName("/jsp/VirtualClass/content");
		return mv;
	}

	@RequestMapping(value="insetPrepareForCourse")
	public ModelAndView insetPrepareForCourse(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		Student stu = (Student) request.getSession().getAttribute("student");
		String taskId = "";
		String teamId = "";
		try {
			String projectId = Common.uuid();
			Object[] obj = Common.fileFactory(request,projectId);
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>) obj[0];	// 获取课程图片
			@SuppressWarnings("unchecked")
			Map<String, Object> formdata = (Map<String, Object>) obj[1]; // 获取课程内容
			TurnClassPlanForCourse tc = new TurnClassPlanForCourse();
			tc.setAuthorId(stu.getStudentId());
			tc.setAuthorName(stu.getStudentName());
			tc.setAuthorPicture(null);
			tc.setContent(null);
			tc.setId(projectId);
			tc.setTeamId((String)formdata.get("teamId"));
			taskId = (String)formdata.get("taskId");
			tc.setTaskId((String)formdata.get("taskId"));
			tc.setUploadTime(new Timestamp(System.currentTimeMillis()));
			turnClassService.insertTurnClassPlanForCourse(tc);
			TurnClassAccessory tca = null;
			for (int i = 0; i < files.size(); i++) {
				String accessoryId = Common.uuid();
				tca = new TurnClassAccessory();
				tca.setAccessoryCategory(null);
				tca.setAccessoryId(accessoryId);
				tca.setAccessoryName(files.get(i).getName());
				if(i==0)
				{
					String authorId = (String)formdata.get("pptAuthor");
					tca.setAuthorId(authorId);
					tca.setAccessoryPath(Common.readProperties("path")+"/"+accessoryId+"/"+authorId+"/"+files.get(i).getName());
					tca.setUnique("ppt");
				}
				if(i==1)
				{
					String authorId = (String)formdata.get("techAuthor");
					tca.setAuthorId(authorId);
					tca.setAccessoryPath(Common.readProperties("path")+"/"+accessoryId+"/"+authorId+"/"+files.get(i).getName());
					tca.setUnique("tech");
				}
				if(i==2)
				{String authorId = (String)formdata.get("codeAuthor");
				tca.setAuthorId(authorId);
				tca.setAccessoryPath(Common.readProperties("path")+"/"+accessoryId+"/"+authorId+"/"+files.get(i).getName());
				tca.setUnique("code");
				}
				tca.setTaskId((String)formdata.get("taskId"));
				tca.setUploadTime(new Timestamp(System.currentTimeMillis()));
				tca.setStage("prepareForCourse");
				teamId = (String)formdata.get("teamId");
				tca.setTeamId((String)formdata.get("teamId"));
				turnClassService.insertTurnClassAccessory(tca);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv = toPrepareForCourse(request,taskId,teamId);
		return mv;
	}

	@RequestMapping(value="insetPrepareForClass/{stage}",method= {RequestMethod.GET})
	public ModelAndView insetPrepareForClass(@PathVariable String stage,HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId,@RequestParam("detail")String detail,@RequestParam("authorId")String authorId) throws Exception {
		ModelAndView mv = new ModelAndView();
		TurnClassPlanForClass tp = new TurnClassPlanForClass();
		tp.setAccessoryPath(null);
		if(stage.equals("planStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(0);
			tp.setDesignStage(0);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("1");
		}
		if(stage.equals("demandStage"))
		{
			tp.setDemandStage(1);
			tp.setPlanStage(1);
			tp.setDesignStage(0);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("2");
		}if(stage.equals("designStage"))
		{	
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("3");
		}if(stage.equals("arithmeticStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(1);
			tp.setTestStage(0);
			tp.setStage("4");
		}if(stage.equals("testStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(1);
			tp.setTestStage(1);
			tp.setStage("5");
		}
		tp.setAuthorId(authorId);
		tp.setAuthorName(null);
		tp.setAuthorPicture(null);
		tp.setContent(detail);
		tp.setId(Common.uuid());
		tp.setTaskId(taskId);
		tp.setTeamId(teamId);
		tp.setUploadTime(new Timestamp(System.currentTimeMillis()));
		turnClassService.insertPrepareForClass(tp);
		mv = toDesignBeforeClass(request,taskId,teamId);
		return mv;
	}


	@RequestMapping(value="insertEndAchievement/{stage}",method= {RequestMethod.GET})
	public ModelAndView insertEndAchievement(@PathVariable String stage,HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId,@RequestParam("detail")String detail,@RequestParam("authorId")String authorId) throws Exception {
		ModelAndView mv = new ModelAndView();
		TurnClassAchievement tp = new TurnClassAchievement();
		tp.setAccessoryPath(null);
		if(stage.equals("planStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(0);
			tp.setDesignStage(0);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("1");
		}
		if(stage.equals("demandStage"))
		{
			tp.setDemandStage(1);
			tp.setPlanStage(1);
			tp.setDesignStage(0);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("2");
		}if(stage.equals("designStage"))
		{	
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("3");
		}if(stage.equals("arithmeticStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(1);
			tp.setTestStage(0);
			tp.setStage("4");
		}if(stage.equals("testStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(1);
			tp.setTestStage(1);
			tp.setStage("5");
		}
		tp.setAuthorId(authorId);
		tp.setAuthorName(null);
		tp.setAuthorPicture(null);
		tp.setContent(detail);
		tp.setId(Common.uuid());
		tp.setTaskId(taskId);
		tp.setTeamId(teamId);
		tp.setUploadTime(new Timestamp(System.currentTimeMillis()));
		turnClassService.insertEndAchievement(tp);
		mv = toEndAchievement(request,taskId,teamId);
		return mv;
	}



	@RequestMapping(value="insertClassAfterModify/{stage}",method= {RequestMethod.GET})
	public ModelAndView insertClassAfterModify(@PathVariable String stage,HttpServletRequest request,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId,@RequestParam("detail")String detail,@RequestParam("authorId")String authorId) throws Exception {
		ModelAndView mv = new ModelAndView();
		TurnClassAfterModify tp = new TurnClassAfterModify();
		tp.setAccessoryPath(null);
		if(stage.equals("planStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(0);
			tp.setDesignStage(0);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("1");
		}
		if(stage.equals("demandStage"))
		{
			tp.setDemandStage(1);
			tp.setPlanStage(1);
			tp.setDesignStage(0);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("2");
		}if(stage.equals("designStage"))
		{	
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(0);
			tp.setTestStage(0);
			tp.setStage("3");
		}if(stage.equals("arithmeticStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(1);
			tp.setTestStage(0);
			tp.setStage("4");
		}if(stage.equals("testStage"))
		{
			tp.setPlanStage(1);
			tp.setDemandStage(1);
			tp.setDesignStage(1);
			tp.setArithmeticStage(1);
			tp.setTestStage(1);
			tp.setStage("5");
		}
		tp.setAuthorId(authorId);
		tp.setAuthorName(null);
		tp.setAuthorPicture(null);
		tp.setContent(detail);
		tp.setId(Common.uuid());
		tp.setTaskId(taskId);
		tp.setTeamId(teamId);
		tp.setUploadTime(new Timestamp(System.currentTimeMillis()));
		turnClassService.insertClassAfterModify(tp);
		mv = toCourseAfterEdit(request,taskId,teamId);
		return mv;
	}


	@RequestMapping(value="insertFeedBack")
	public ModelAndView insertFeedBack(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		String teamId = "";
		String taskId = "";
		try {
			String projectId = Common.uuid();
			Object[] obj = Common.fileFactory(request,projectId);
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>) obj[0];	// 获取课程图片
			@SuppressWarnings("unchecked")
			Map<String, Object> formdata = (Map<String, Object>) obj[1]; // 获取课程内容
			TurnClassFeedback fb = new TurnClassFeedback();
			String tcId = (String)formdata.get("tcId");
			if(!tcId.equals("")||tcId!=null)
			{
				projectId = tcId;
			}
			String accessoryId = Common.uuid();
			fb.setAccessoryId(accessoryId);
			String authorId = (String)formdata.get("authorId");
			fb.setAuthorId(authorId);
			fb.setContent((String)formdata.get("detail"));
			fb.setId(projectId);
			taskId = (String)formdata.get("taskId");
			fb.setTaskId((String)formdata.get("taskId"));
			fb.setUploadTime(new Timestamp(System.currentTimeMillis()));
			teamId = (String)formdata.get("teamId");
			fb.setTeamId(teamId);
			turnClassService.insertFeedBack(fb);
			TurnClassAccessory tca = new TurnClassAccessory();
			tca.setAccessoryCategory(null);
			tca.setAccessoryId(accessoryId);
			tca.setAccessoryName(files.get(0).getName());
			tca.setAccessoryPath(Common.readProperties("path")+"/"+accessoryId+"/"+authorId+"/"+files.get(0).getName());
			tca.setAuthorId(authorId);
			tca.setStage(null);
			tca.setTeamId(teamId);
			tca.setUnique(null);
			tca.setTaskId(projectId);
			tca.setUploadTime(new Timestamp(System.currentTimeMillis()));
			turnClassService.insertTurnClassAccessory(tca);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv = toCourseFeedback(request,taskId,teamId);
		return mv;
	}

	/**
	 * 前台获去已经存储的信息
	 * */
	@RequestMapping(value="getInfoForTeamDesignBeforeClass",method= {RequestMethod.GET})
	public void getInfoForTeamDesignBeforeClass(HttpServletRequest request,HttpServletResponse response,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId,@RequestParam("category")String category) throws Exception {
		TurnClassPlanForClass tc = null;
		List<TurnClassPlanForClass> list = new ArrayList<>();
		list = turnClassService.getInfoForTeamDesignBeforeClass(taskId,teamId);
		switch (category) {
		case "planStage":
			for (TurnClassPlanForClass turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getArithmeticStage()==0&&turnClassPlanForClass.getDesignStage()==0&&turnClassPlanForClass.getDemandStage()==0&&turnClassPlanForClass.getPlanStage()==1&&turnClassPlanForClass.getTestStage()==0)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		case "demandStage":
			for (TurnClassPlanForClass turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getArithmeticStage()==0&&turnClassPlanForClass.getDesignStage()==0&&turnClassPlanForClass.getDemandStage()==1&&turnClassPlanForClass.getPlanStage()==1&&turnClassPlanForClass.getTestStage()==0)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		case "designStage":
			for (TurnClassPlanForClass turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getArithmeticStage()==0&&turnClassPlanForClass.getDesignStage()==1&&turnClassPlanForClass.getDemandStage()==1&&turnClassPlanForClass.getPlanStage()==1&&turnClassPlanForClass.getTestStage()==0)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		case "arithmeticStage":
			for (TurnClassPlanForClass turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getArithmeticStage()==1&&turnClassPlanForClass.getDesignStage()==1&&turnClassPlanForClass.getDemandStage()==1&&turnClassPlanForClass.getPlanStage()==1&&turnClassPlanForClass.getTestStage()==0)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		case "testStage":
			for (TurnClassPlanForClass turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getTestStage()==1&&turnClassPlanForClass.getArithmeticStage()==1&&turnClassPlanForClass.getDesignStage()==1&&turnClassPlanForClass.getDemandStage()==1&&turnClassPlanForClass.getPlanStage()==1)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		default:
			break;
		}
		String result="";
		if(tc!=null)
		{
			JSONObject ob=new JSONObject();
			ob.put("judge","notNull");
			ob.put("content",tc.getContent());
			ob.put("time",tc.getUploadTime().toString());
			Student leader = studentService.studentLoginByEmployeeNum(tc.getAuthorId());
			ob.put("leader", leader.getStudentId());
			try {
				request.setCharacterEncoding("utf-8");
				response.setContentType("application/json;charset=UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			result = ob.toString();
		}
		if(tc==null)
		{			
			JSONObject ob=new JSONObject();
			ob.put("judge","null");
			try {
				request.setCharacterEncoding("utf-8");
				response.setContentType("application/json;charset=UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			result = ob.toString();
		}
		try {
			response.getWriter().print(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 前台获去已经存储的信息
	 * */
	@RequestMapping(value="getInfoForTeamCourseAfterEdit",method= {RequestMethod.GET})
	public void getInfoForTeamCourseAfterEdit(HttpServletRequest request,HttpServletResponse response,@RequestParam("taskId")String taskId,@RequestParam("teamId")String teamId,@RequestParam("category")String category) throws Exception {
		TurnClassAfterModify tc = null;
		List<TurnClassAfterModify> list = new ArrayList<>();
		list = turnClassService.getInfoForTeamCourseAfterEdit(taskId,teamId);
		switch (category) {
		case "planStage":
			for (TurnClassAfterModify turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getArithmeticStage()==0&&turnClassPlanForClass.getDesignStage()==0&&turnClassPlanForClass.getDemandStage()==0&&turnClassPlanForClass.getPlanStage()==1&&turnClassPlanForClass.getTestStage()==0)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		case "demandStage":
			for (TurnClassAfterModify turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getArithmeticStage()==0&&turnClassPlanForClass.getDesignStage()==0&&turnClassPlanForClass.getDemandStage()==1&&turnClassPlanForClass.getPlanStage()==1&&turnClassPlanForClass.getTestStage()==0)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		case "designStage":
			for (TurnClassAfterModify turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getArithmeticStage()==0&&turnClassPlanForClass.getDesignStage()==1&&turnClassPlanForClass.getDemandStage()==1&&turnClassPlanForClass.getPlanStage()==1&&turnClassPlanForClass.getTestStage()==0)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		case "arithmeticStage":
			for (TurnClassAfterModify turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getArithmeticStage()==1&&turnClassPlanForClass.getDesignStage()==1&&turnClassPlanForClass.getDemandStage()==1&&turnClassPlanForClass.getPlanStage()==1&&turnClassPlanForClass.getTestStage()==0)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		case "testStage":
			for (TurnClassAfterModify turnClassPlanForClass : list) {
				if(turnClassPlanForClass.getTestStage()==1&&turnClassPlanForClass.getArithmeticStage()==1&&turnClassPlanForClass.getDesignStage()==1&&turnClassPlanForClass.getDemandStage()==1&&turnClassPlanForClass.getPlanStage()==1)
				{
					tc = turnClassPlanForClass;
				}
			}
			break;
		default:
			break;
		}
		String result="";
		if(tc!=null)
		{
			JSONObject ob=new JSONObject();
			ob.put("judge","notNull");
			ob.put("content",tc.getContent());
			ob.put("time",tc.getUploadTime().toString());
			Student leader = studentService.studentLoginByEmployeeNum(tc.getAuthorId());
			ob.put("leader", leader.getStudentId());
			try {
				request.setCharacterEncoding("utf-8");
				response.setContentType("application/json;charset=UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			result = ob.toString();
		}
		if(tc==null)
		{			
			JSONObject ob=new JSONObject();
			ob.put("judge","null");
			try {
				request.setCharacterEncoding("utf-8");
				response.setContentType("application/json;charset=UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			result = ob.toString();
		}
		try {
			response.getWriter().print(result);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
