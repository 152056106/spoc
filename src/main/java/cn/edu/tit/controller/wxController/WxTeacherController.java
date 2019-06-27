package cn.edu.tit.controller.wxController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.edu.tit.bean.AOCSC;
import cn.edu.tit.bean.Accessory;
import cn.edu.tit.bean.Attendance;
import cn.edu.tit.bean.Category;
import cn.edu.tit.bean.Course;
import cn.edu.tit.bean.CourseExpand;
import cn.edu.tit.bean.GDFCS;
import cn.edu.tit.bean.IURP;
import cn.edu.tit.bean.RealClass;
import cn.edu.tit.bean.Resource;
import cn.edu.tit.bean.SIAE;
import cn.edu.tit.bean.Student;
import cn.edu.tit.bean.Task;
import cn.edu.tit.bean.Teacher;
import cn.edu.tit.bean.Term;
import cn.edu.tit.bean.UpTask;
import cn.edu.tit.bean.VirtualClass;
import cn.edu.tit.common.Common;
import cn.edu.tit.iservice.IAchievementService;
import cn.edu.tit.iservice.IResourceService;
import cn.edu.tit.iservice.IStudentService;
import cn.edu.tit.iservice.ITeacherService;

/**
 * 提供给微信的接口
 * 
 * @author 20586
 *
 */
@RequestMapping("WxTeacherController")
@RestController
public class WxTeacherController {

	@Autowired
	private ITeacherService teacherService;

	@Autowired
	private IStudentService studentService;
	@Autowired
	private IResourceService resourceService;
	@Autowired
	private IAchievementService iAchievementService;

	
	/**
	 * 微信端登录
	 * 
	 * @param request
	 * @param userId
	 * @param password
	 */
	@RequestMapping(value = "userLogin")
	public Map<String, Object> userLogin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "id", required = false) String userid,
			@RequestParam(value = "password", required = false) String password) {
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		response.setContentType("text/plain;charset=UTF-8");
		String readResult = null;
		String userPassword = null;
		if (userid == null || password == null) {
			return null;
		} else {
			try {
				Teacher teacher = teacherService.teacherLoginByEmployeeNum(userid);
				Student student = studentService.studentLoginByEmployeeNum(userid);
				userPassword = Common.eccryptMD5(password);
				if (teacher != null) {
					if (userPassword.equals(teacher.getTeacherPassword())) {
						request.getSession().setAttribute("user", teacher);
						request.getSession().setAttribute("userId", teacher.getEmployeeNum());
						ret.put("user", teacher);
						System.out.println("ttahcer========================================");
						return ret;
					} else {
						ret.put("status", "ERROR");
						ret.put("msg", "密码错误");
						return ret;
					}
				} else if (student != null) {
					if (userPassword.equals(student.getStudentPassword())) {
						ret.put("user", student);
						request.getSession().setAttribute("user", student);
						request.getSession().setAttribute("userId", student.getStudentId());
						System.out.println("ttahcer========================================");
						return ret;

					} else {
						ret.put("status", "ERROR");
						ret.put("msg", "密码错误");
						return ret;
					}
				} else {
					ret.put("status", "ERROR");
					ret.put("msg", "用户名错误");
					return ret;
				}

			} catch (Exception e) {
				e.printStackTrace();
				ret.put("status", "ERROR");
				ret.put("msg", "用户名或密码错误");
				return ret;
			}
		}
	}

	/**
	 * 创建课程
	 * 
	 * @param request
	 * @param teachers
	 * @return
	 */
	@RequestMapping(value = "createCourse")
	@SuppressWarnings({ "unused", "unchecked" })
	@ResponseBody
	public Map<String, Object> createCourse(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		String courseId = Common.uuid();
		try {
			Object[] obj = Common.fileFactory(request, courseId);
			List<File> files = (List<File>) obj[0]; // 获取课程图片
			Map<String, Object> formdata = (Map<String, Object>) obj[1]; // 获取课程内容
			// 封装课程类
			Course course = new Course();
			course.setCourseId(courseId);
			course.setCourseName((String) formdata.get("courseName"));
			// 校验课程名
			if (teacherService.getCourseByName(course.getCourseName()) != null) {
				ret.put("msg", "课程已存在");
				ret.put("status", "ERROR");
				return ret;
			}
			course.setCourseDetail((String) formdata.get("courseDetail"));
			course.setCourseCategory((String) formdata.get("courseCategory"));
			Timestamp publishTime = new Timestamp(System.currentTimeMillis());
			course.setPublishTime(publishTime);
			String employeeNum = (String) formdata.get("publisherId");
			String teacher = (String) formdata.get("teachers");
			String[] teachers = teacher.split(",");
			course.setPublisherId(employeeNum);
			for (File f : files) {
				course.setFaceImg(Common.readProperties("path") + "/" + f.getName());
			}
			teacherService.createCourse(course); // 添加课程
			teacherService.addOtherToMyCourse(employeeNum, courseId, 1);// 把课程创建者初始化到教师圈
			// 通过课程id和获取教师圈的id集合绑定教师到课程
			if (teachers != null) {
				for (int i = 0; i < teachers.length; i++) {
					teacherService.addOtherToMyCourse(teachers[i], courseId, 0);
				}
			}
			ret.put("status", "OK");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "ERROR");
			return ret;
		}
	}

	/**
	 * 创建课程（不加图片）
	 * 
	 * @param request
	 * @param teachers
	 * @return
	 */
	@RequestMapping(value = "createCourseNoImg")
	@SuppressWarnings({ "unused", "unchecked" })
	@ResponseBody
	public Map<String, Object> createCourseNoImg(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		String courseId = Common.uuid();
		try {
			// 封装课程类
			Course course = new Course();
			course.setCourseId(courseId);
			course.setCourseName(request.getParameter("courseName"));
			// 校验课程名
			if (teacherService.getCourseByName(course.getCourseName()) != null) {
				ret.put("msg", "课程已存在");
				ret.put("status", "ERROR");
				return ret;
			}
			course.setCourseDetail(request.getParameter("courseDetail"));
			course.setCourseCategory(request.getParameter("courseCategory"));
			Timestamp publishTime = new Timestamp(System.currentTimeMillis());
			course.setPublishTime(publishTime);
			String employeeNum = request.getParameter("publisherId");
			String teacher = request.getParameter("teachers");
			String[] teachers = teacher.split(",");
			course.setPublisherId(employeeNum);
			teacherService.createCourse(course); // 添加课程
			teacherService.addOtherToMyCourse(employeeNum, courseId, 1);// 把课程创建者初始化到教师圈
			// 通过课程id和获取教师圈的id集合绑定教师到课程
			if (teachers != null) {
				for (int i = 0; i < teachers.length; i++) {
					teacherService.addOtherToMyCourse(teachers[i], courseId, 0);
				}
			}
			ret.put("status", "OK");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "ERROR");
			return ret;
		}
	}

	/**
	 * 创建班级
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "createClass")
	@ResponseBody
	public Map<String, Object> createClass(HttpServletRequest request) {
		try {
			try {
				request.setCharacterEncoding("utf-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			VirtualClass virtualClass = new VirtualClass();
			// 接收参数
			String virtualClassNUm = Common.uuid();
			virtualClass.setVirtualClassNum(virtualClassNUm);
			virtualClass.setVirtualClassName(request.getParameter("virtualClassName"));
			// 校验班级名
			if (teacherService.getCourseByName(virtualClass.getVirtualClassName()) != null) {
				ret.put("msg", "班级已存在");
				ret.put("status", "ERROR");
				return ret;
			}
			virtualClass.setCreatorId(request.getParameter("creator"));
			virtualClass.setCourseId(request.getParameter("courseId"));
			virtualClass.setCreateTime(new Timestamp(System.currentTimeMillis()));
			virtualClass.setTerm(request.getParameter("term"));
			String[] realClassNums = request.getParameter("realClassNums").split(",");// ====================error
			// String[] realClassNums = null;
			// 绑定自然班和虚拟班的关系
			int count = 0; // 班级总数
			List<RealClass> realClassList = new ArrayList<RealClass>();
			if (realClassNums.length != 0) {

				for (String realClassNum : realClassNums) {
					realClassList.add(teacherService.readRealClass(realClassNum).get(0)); // 根据自然班级号获取对应的自然班级
				}
				// 计算总人数
				for (RealClass realClass : realClassList) {
					count += realClass.getRealPersonNum();
				}
				virtualClass.setClassStuentNum(count);
				teacherService.createVirtualClass(virtualClass); // 创建班级
				// 绑定创建的虚拟班级和其对应的自然班
				for (String realClassNum : realClassNums) {
					teacherService.mapVirtualRealClass(realClassNum, virtualClassNUm);
				}
				ret.put("status", "OK");
				return ret;
			} else {
				ret.put("msg", "请选择班级");
				ret.put("status", "ERROR");
				return ret;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("msg", "添加异常，请检查信息重试！");
			ret.put("status", "ERROR");
			return ret;
		}
	}

	/**
	 * 修改课程
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "wxModifyCourse")
	public Map<String, Object> wxModifyCourse(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			Object[] obj = Common.fileFactory(request, "");
			List<File> files = (List<File>) obj[0]; // 获取课程图片
			Map<String, Object> formdata = (Map<String, Object>) obj[1]; // 获取课程内容
			// 封装课程类
			Course course = new Course();
			course.setCourseId((String) formdata.get("courseId"));
			course.setCourseName((String) formdata.get("courseName"));
			// 校验课程名
			if (teacherService.getCourseByName(course.getCourseName()) != null) {
				ret.put("msg", "课程已存在");
				ret.put("status", "ERROR");
				return ret;
			}
			course.setCourseDetail((String) formdata.get("courseDetail"));
			course.setCourseCategory((String) formdata.get("courseCategory"));
			Timestamp publishTime = new Timestamp(System.currentTimeMillis());
			course.setPublishTime(publishTime);
			String employeeNum = (String) formdata.get("publisherId");
			course.setPublisherId(employeeNum);
			for (File f : files) {
				course.setFaceImg(Common.readProperties("path") + "/" + f.getName());
			}
			teacherService.updateCourse(course); // 修改课程
			// teacherService.addOtherToMyCourse(employeeNum, courseId,
			// 1);//把课程创建者初始化到教师圈
			// //通过课程id和获取教师圈的id集合绑定教师到课程
			// if(teachers != null){
			// for(int i = 0; i < teachers.length; i++){
			// teacherService.addOtherToMyCourse(teachers[i], courseId, 0);
			// }
			// }
			ret.put("status", "OK");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "ERROR");
			return ret;
		}
	}

	/**
	 * 修改班级
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "wxModifyClass")
	@ResponseBody
	public Map<String, Object> modifyClass(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<String, Object>();
		try {
			VirtualClass virtualClass = new VirtualClass();
			// 接收参数
			String virtualClassNUm = request.getParameter("virtualClassNum");
			virtualClass.setVirtualClassName(request.getParameter("virtualClassName"));
			// 校验班级名
			if (teacherService.getCourseByName(virtualClass.getVirtualClassName()) != null) {
				ret.put("msg", "班级已存在");
				ret.put("status", "ERROR");
				return ret;
			}
			virtualClass.setCreatorId("1");
			virtualClass.setCourseId(request.getParameter("courseId"));
			virtualClass.setTerm(request.getParameter("term"));
			String[] realClassNums = request.getParameter("realClassNums").split(",");
			teacherService.createVirtualClass(virtualClass); // 创建班级
			// 绑定自然班和虚拟班的关系
			// if(realClassNums.length != 0){
			// for(String realClassNum : realClassNums){
			// teacherService.mapVirtualRealClass(realClassNum,
			// virtualClassNUm);
			// }
			// ret.put("status", "OK");
			// return ret;
			// }
			// else{
			// ret.put("msg", "请选择班级");
			// ret.put("status","ERROR");
			// return ret;
			// }
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("msg", "添加异常，请检查信息重试！");
			ret.put("status", "ERROR");
			return ret;
		}
	}

	/**
	 * 教师显示“我的课程”信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "wxShowTeacherCourse", method = { RequestMethod.GET })
	public Map<String, Object> wxShowTeacherCoure(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			// 得到老师创建的课程
			String employeeNum = request.getParameter("employeeNum");
			List<String> createCourseIdList = teacherService.courseIdList(employeeNum, 1);
			List<Course> createCourse = new ArrayList<>();
			if (createCourseIdList != null) {
				for (String cid : createCourseIdList) {
					Course course = teacherService.getCourseById(cid);
					createCourse.add(course);
				}
			}
			// 得到老师加入的课程
			List<String> joinCourseIdList = teacherService.courseIdList(employeeNum, 0);
			List<Course> joinCourse = new ArrayList<>();
			if (joinCourseIdList != null) {
				for (String cid : joinCourseIdList) {
					Course course = teacherService.getCourseById(cid);
					joinCourse.add(course);
				}
			}
			ret.put("createCourse", createCourse);
			ret.put("joinCourse", joinCourse);
			ret.put("status", "OK");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "异常，请重试");
			return ret;
		}
	}

	/**
	 * 学生显示“我的课程”页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "wxShowStudentCourse")
	public Map<String, Object> wxShowStudentCourse(HttpServletRequest request,
			@RequestParam(value = "studentId") String studentId) {
		Map<String, Object> ret = new HashMap<>();
		try {
			// 通过学生id获取自然班级号
			String realClassNum = teacherService.getrealClassNumBySid(studentId);
			// 通过学生所在的realclass查出其参与的课程
			List<Course> joinCourses = teacherService.getStudentJoinCourseByrealNum(realClassNum);
			// 查出学生关注的课程集合
			List<Course> attentionCourse = teacherService.getAttentionCourse(studentId);
			ret.put("joinCourses", joinCourses);
			ret.put("attentionCourse", attentionCourse);
			ret.put("status", "OK");
			return ret;
		} catch (Exception e) {  
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "ERROR");
			return ret;
		}
	}

	/**
	 * 获取教师列表
	 */
	@RequestMapping(value = "getTeachers")
	public Map<String, Object> getTeachers(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "employeeNum") String employeeNum) {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<Teacher> teacherList = new ArrayList<>();
			List<Teacher> teachers = teacherService.getTeachers();
			if (teachers != null) {
				for (Teacher teacher : teachers) {
					if (!teacher.getEmployeeNum().equals(employeeNum)) { // 在选择的教师中过滤掉当前的操作者
						teacherList.add(teacher);
					}
				}
			}
			ret.put("teacherList", teacherList);
			ret.put("status", "OK");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "ERROR");
			return ret;
		}
	}

	/**
	 * 获取系部
	 * 
	 * @param request
	 * @param employeeNum
	 * @return
	 */
	@RequestMapping("getCategory")
	public Map<String, Object> toCreateCourse(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		try {
			// 查找所有系部列表
			List<Category> categoryList = teacherService.readCategory();
			ret.put("categoryList", categoryList);
			ret.put("status", "OK");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "ERROR");
		}
		return ret;
	}

	/**
	 * 显示教师创建的班级列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "showTeacherCreateClass")
	public Map<String, Object> showTeacherCreateClass(HttpServletRequest request,
			@RequestParam(value = "employeeNum") String employeeNum) {
		Map<String, Object> ret = new HashMap<>();
		try {
			// 获取教师创建的所有班级
			List<VirtualClass> virtualClassList = teacherService.getTeacherCreateClass(employeeNum);
			//获取自然班级列表
			if(virtualClassList.contains(null) || virtualClassList != null){
				for(int i = 0; i < virtualClassList.size(); i++){
					// 根据虚拟班级id获取自然班级列表
					virtualClassList.get(i).setRealClassList(teacherService.getRealClassList(virtualClassList.get(i).getVirtualClassNum()));
					String term = virtualClassList.get(i).getTerm();
					Term  a = teacherService.getTermById(term);
					virtualClassList.get(i).setTerm(a.getStartYear()+"-"+a.getEndYear()+" 学年"+a.getTerm());
				}
			}
			ret.put("virtualClassList", virtualClassList);
			ret.put("status", "OK");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "ERROR");
		}
		return ret;
	}

	/**
	 * 查出学生所属虚拟班级列表
	 * 
	 * @param request
	 * @param studentId
	 * @return
	 */
	@RequestMapping(value = "showStudentJoinClass")
	public Map<String, Object> showStudentJoinClass(HttpServletRequest request,
			@RequestParam(value = "studentId") String studentId) {
		Map<String, Object> ret = new HashMap<>();
		try {
			// 通过学生id获取自然班级号
			String realClassNum = teacherService.getrealClassNumBySid(studentId);
			// 通过学生所在的realclass查出virtualclass
			List<VirtualClass> virtualClassList = teacherService.getVirtualClassNumByreal(realClassNum);
			//获取自然班级列表
			if(virtualClassList.contains(null) || virtualClassList != null){
				for(int i = 0; i < virtualClassList.size(); i++){
					// 根据虚拟班级id获取自然班级列表
					virtualClassList.get(i).setRealClassList(teacherService.getRealClassList(virtualClassList.get(i).getVirtualClassNum()));
					String employeeNum = virtualClassList.get(i).getCreatorId(); 
					virtualClassList.get(i).setCreatorId(teacherService.getTeacherNameById(employeeNum));
					String term = virtualClassList.get(i).getTerm();
					Term  a = teacherService.getTermById(term);
					virtualClassList.get(i).setTerm(a.getStartYear()+"-"+a.getEndYear()+" 学年"+a.getTerm());
				}
			}
			ret.put("virtualClassList", virtualClassList);
			ret.put("status", "OK");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "ERROR");
			return ret;
		}
	}

	/**
	*直接一次性查出所有的数据,返回给前端
	*/
	@RequestMapping("/getTaskListPage")
	@ResponseBody
	public  Map<String, Object> getTaskListPage(HttpServletRequest request,@RequestParam(value = "taskCategory")String taskCategory){
		Map<String, Object> ret = new HashMap<>();
		List<Task> list = new ArrayList<Task>();
		String courseId = (String) request.getSession().getAttribute("courseId");
		list = teacherService.getTaskListPage(courseId, taskCategory);
		ret.put("taskList", list);
		return ret;
	} 
	
	/**
	 * 提供课程详细
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "toCourseDetail")
	public Map<String, Object> toCourseDetail(HttpServletRequest request, @RequestParam(value = "courseId") String courseId) {
		Map<String, Object> ret = new HashMap<>();
		try {
			// 通过courseid查询课程
			Course course = teacherService.getCourseById(courseId);
			// 查询教师圈教师信息
			List<Teacher> teacherList = teacherService.getTeachersByCourseId(courseId);
			request.getSession().setAttribute("course", course);
			ret.put("course", course);
			ret.put("teacherList", teacherList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 获取课程资源
	 * @param request
	 * @param courseId
	 * @return
	 */
	@RequestMapping(value="toTeachResource")
	public Map<String, Object> toTeachResource(HttpServletRequest request,  @Param(value="courseId") String courseId){
		Map<String, Object> ret = new HashMap<>();
		try {
			//通过课程id查询音频类型资源
			Object[] obj = resourceService.showResourceByTypeAndCId(2, courseId);
			ret.put("audioresourceList", obj[0]);
			ret.put("audioteacherNames", obj[1]);
			//通过课程id查询视频类型资源
			Object[] obj2 = resourceService.showResourceByTypeAndCId(3, courseId);
			ret.put("videoresourceList", obj2[0]);
			ret.put("videoteacherNames", obj2[1]);
			//通过课程id查询图片类型资源
			Object[] obj3 = resourceService.showResourceByTypeAndCId(4, courseId);
			ret.put("imgresourceList", obj3[0]);
			ret.put("imgteacherNames", obj3[1]);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
			return ret;
		
	}

	/**
	 * @author LiMing
	 * @param request
	 * @return 获取创建虚拟班级数据
	 * @throws Exception
	 */
	@RequestMapping(value = "toCreateVirtualClass")
	public Map<String, Object> toCreateVirtualClass(@RequestParam(value = "courseId") String courseId,
			HttpServletRequest request) throws Exception {
		Map<String, Object> ret = new HashMap<>();
		try {
			List<Term> listTerm = new ArrayList<Term>();
			List<RealClass> listRealClass = new ArrayList<RealClass>();
			Course course = new Course();

			// course = teacherService.readCourseByCourseId(courseId);
			listTerm = teacherService.readTerm(); // 获取学期信息
			listRealClass = teacherService.readRealClass(null); // 获取自然班级列表
			request.getSession().setAttribute("virtualCourse", course);// 将course放入SESSION
			ret.put("listTerm", listTerm);
			ret.put("listRealClass", listRealClass);
			ret.put("courseId", courseId);
			ret.put("status", "OK");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 跳转到首页
	 * @param request
	 * @return
	 */
	@RequestMapping(value="toMain")
	public Map<String, Object> toMain(HttpServletRequest request){
		Map<String, Object> ret = new HashMap<>();
		try {
			//按时间查询前12个课程信息
			List<Course> courseList = teacherService.getCourseByLimit();
			//获取创课教师
			List<String> teacherNameList = new ArrayList<>();
			for(Course course : courseList){
				teacherNameList.add(teacherService.getTeacherNameById(course.getPublisherId()));
			}
			ret.put("courseList",courseList);
			ret.put("teacherNameList",teacherNameList);
			ret.put("teacher",request.getSession().getAttribute("teacher"));
			ret.put("student",request.getSession().getAttribute("student"));
			ret.put("status", "OK");
		} catch (Exception e) {
			// TODO: handle exception
			ret.put("status", "ERROR");
			e.printStackTrace();
			return ret;
		}
		return ret;
	}
	
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 跳转到添加任务发布页面
	 */
	@RequestMapping(value="toPublishTask")
	@SuppressWarnings({ "unused", "unchecked" })
	public Map<String, Object> toPublishTask(HttpServletRequest request,@RequestParam("courseId") String courseId) {
		Map<String, Object> ret = new HashMap<>();
		//Course course;
		try {
			Course course = teacherService.getCourseById(courseId);
			List<String> taskCategoryList=new ArrayList<>();
			//course = (Course) request.getSession().getAttribute("course");
			taskCategoryList = teacherService.getTaskCategory();
			request.getSession().setAttribute("taskCategoryList", taskCategoryList);
			request.getSession().setAttribute("courseId", course.getCourseId());
			ret.put("taskCategoryList", taskCategoryList);
			ret.put("courseId", course.getCourseId());
		} catch (Exception e) {
			ret.put("status", "ERROR");
			e.printStackTrace();
			return ret;
		}
		return ret;
	}
	
	/**
	 * @author LiMing
	 * 通过分类筛选 课程
	 * */
	@RequestMapping(value="readCourseInfoByCategory",method= {RequestMethod.GET})
	public Map<String, Object> readCategoryInfo(@RequestParam(value="categoryId") String categoryId) {		
		List<Category> categories = new ArrayList<>();
		Map<String, Object> ret = new HashMap<>();
		String category = categoryId;
		List<Course> list = new ArrayList<Course>();
		List<String> teacherNames = new ArrayList<String>();
		try {
			categories = teacherService.readCategory();
			list = teacherService.readCourseInfoByCategory(category);
			for (Course course : list) {
				teacherNames.add(teacherService.getTeacherNameById(course.getPublisherId()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ret.put("teacherNames",teacherNames);
		ret.put("categories",categories);
		ret.put("courseList", list);
		return ret;
	}
	
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 到班级作业列表页
	 */
	@RequestMapping("/toteacherTaskListContent")
	public Map<String, Object> toteacherTaskListContent(HttpServletRequest request ) {
		Map<String, Object> ret = new HashMap<>();
		Integer upNum =0;
		List<String> taskIdList;
		List<Task> taskList=new ArrayList<Task>();
		String readResult =null;
		Integer poit=0;
		String upTaskDetail = null;
		Timestamp now = new Timestamp(System.currentTimeMillis());
		HashMap<String, String> upTaskDetail2taskList = new HashMap<String, String>();
		HashMap<String, List<String>> accessoriesName2taskList  = new HashMap<String, List<String>>();
		HashMap<String, Integer> grade2taskList  = new HashMap<String, Integer>();
		HashMap<String, Boolean> isEnd2taskList = new HashMap<String, Boolean>();
		HashMap<String, String> timeEnd2taskList = new HashMap<String, String>();//计算时间差
		List<String> accessoriesName = new ArrayList<String>();
		//upTaskDetail = studentService.getUpTaskDetail(taskId, studentId);
		//accessoriesName = studentService.getUpAccessories(taskId, studentId);
		String virtualClassNum = (String) request.getParameter("virtualClassNum");//获取当前虚拟班级
		System.out.println("virtualClassNum========="+virtualClassNum);
		VirtualClass virtualClass = teacherService.getVirtualById(virtualClassNum);//获取虚拟班级实体
		Integer studentNum = virtualClass.getClassStuentNum();	//获取班级总人数
		try {
			taskIdList = teacherService.searchTaskId(virtualClassNum);//根据虚拟班级号获得任务列表
			taskList = teacherService.TaskList(taskIdList);	//根据任务ID号获得任务实体
			for(Task t : taskList){
				upNum = teacherService.getUpNum(virtualClassNum, t.getTaskId());
				t.setUpNum(upNum);
				t.setTaskEndTime(teacherService.getTaskEndTime(virtualClassNum, t.getTaskId()));
			}
			ret.put("taskList", taskList);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * @author wenli
	 * @param request
	 * @return
	 * 发布任务
	 */
	@RequestMapping(value="publishTask")
	@SuppressWarnings({ "unused", "unchecked" })
	public Map<String, Object> publishTask(HttpServletRequest request) {
		
		String taskId =  Common.uuid();	//设置任务id
		Map<String, Object> ret = new HashMap<>();
		Object[] obj = Common.fileFactory(request,taskId);
		Map<String, Object> formdata = (Map<String, Object>) obj[1];
		List<File> returnFileList = (List<File>) obj[0]; // 要返回的文件集合
		String virtualClassNum = (String) formdata.get("virtualClassNum");
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
		task.setPublisherId((String) formdata.get("userId"));
		task.setPublishTime(new Timestamp(System.currentTimeMillis()));
		task.setVirtualClassNum(virtualClassNum);
		task.setUseNum(1);//设置使用次数为1
		task.setCourseId((String) formdata.get("courseId"));
		System.out.println("作业类型是："+(String) formdata.get("taskCategory"));
		task.setTaskType((String) formdata.get("taskCategory"));
		task.setStatus(0);

		try {
			teacherService.createTask(task);		//创建任务
			teacherService.mapClassTask(task.getVirtualClassNum(), taskId,Timestamp.valueOf((String) formdata.get("taskEndTime")));		//映射班级任务表

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!returnFileList.isEmpty()) {
			for (File file : returnFileList) {
				Accessory accessory = new Accessory();
				accessory.setAccessoryName(file.getName());
				accessory.setAccessoryPath(file.getPath());
				accessory.setTaskId(taskId);
				accessory.setAccessoryTime(Common.TimestamptoString());
				accessories.add(accessory);
//				Resource resource = new Resource();
//				resource.setResourceId(Common.uuid());
//				resource.setResourceName(file.getName());
//				resource.setResourceDetail(null);
//				resource.setPublishTime(new Timestamp(System.currentTimeMillis()));
//				resource.setPublisherId((String) request.getSession().getAttribute("teacherId"));
//				resource.setResourceTypeId(Common.fileType(file.getName(), teacherService));//需要判断文件类型
//				resource.setResourcePath(file.getPath());
//				resource.setCourseId((String) formdata.get("courseId"));
//				resource.setSize(file.length()/1024.0+"KB");
//				resources.add(resource);
			}
			try {
				teacherService.addAccessory(accessories);	//添加任务附件
//				resourceService.upLoadResource(resources);//添加资源
				ret.put("status", "OK");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ret.put("status", e.getMessage());
				ret.put("s",(String) formdata.get("userId") );
			}
			
		}
		return ret;
	}
	
	/**
	 * 提交作业
	 * @param request
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value="toUpTask")
	public Map<String, Object> toUpTask(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		
		Object[] obj = Common.fileFactory(request,null);
		Map<String, Object> formdata = (Map<String, Object>) obj[1];
		List<File> returnFileList = (List<File>) obj[0]; // 要返回的文件集合
		// 创建list集合用于获取文件上传返回路径名
		String studentId = (String) formdata.get("studentId");
		String virtualClassNum = (String) formdata.get("virtualClassNum");
		String term = teacherService.getVirtualById(virtualClassNum).getTerm();
		List<String> list = new ArrayList<String>();
		List<Accessory> accessories  = new ArrayList<Accessory>();
		UpTask upTask = new UpTask();
		upTask.setTaskId((String) formdata.get("taskId"));
		upTask.setStudentId(studentId);
		upTask.setTerm(term);
		Timestamp t  = new Timestamp(System.currentTimeMillis());
		upTask.setUpTime(t);
		upTask.setUpTaskDetail((String) formdata.get("upTaskDetail"));
		studentService.upTask(upTask, virtualClassNum);
		
		if(!returnFileList.isEmpty()) {
			for (File file : returnFileList) {
				Accessory accessory = new Accessory();
				accessory.setAccessoryName(file.getName());
				accessory.setAccessoryPath(file.getPath());
				accessory.setTaskId((String) formdata.get("taskId"));
				accessory.setAccessoryTime(Common.TimestamptoString());
				accessories.add(accessory);
			}
			try {
				studentService.upAccessory(accessories, studentId);
				ret.put("status", "OK");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ret.put("status", "error");
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * 提交作业--无附件
	 * @param request
	 * @param taskId
	 * @return
	 */
	@RequestMapping(value="upTaskNoAcc")
	public Map<String, Object> upTaskNoAcc(HttpServletRequest request) {
		Map<String, Object> ret = new HashMap<>();
		
		try {
			// 创建list集合用于获取文件上传返回路径名
			String studentId = request.getParameter("studentId");
			String virtualClassNum = request.getParameter("virtualClassNum");
			String term = teacherService.getVirtualById(virtualClassNum).getTerm();
			UpTask upTask = new UpTask();
			upTask.setTaskId(request.getParameter("taskId"));
			upTask.setStudentId(studentId);
			upTask.setTerm(term);
			Timestamp t  = new Timestamp(System.currentTimeMillis());
			upTask.setUpTime(t);
			upTask.setUpTaskDetail(request.getParameter("upTaskDetail"));
			studentService.upTask(upTask, virtualClassNum);
			ret.put("status", "OK");
		} catch (Exception e) {
			// TODO: handle exception
			ret.put("status", "error");
			e.printStackTrace();
		}
		return ret;
	}
	
	@RequestMapping(value="getUpTaskById")
	public Map<String, Object> getUpTaskById(HttpServletRequest request,@RequestParam(value="studentId") String studentId,
			@RequestParam(value="taskId") String taskId) {
		Map<String, Object> ret = new HashMap<>();
		String upTaskDetail = null;
		List<String> accessoriesName = new ArrayList<String>();
		try {
			if(studentId!=null) {
				upTaskDetail = studentService.getUpTaskDetail(taskId, studentId);
			}
			
			accessoriesName = studentService.getUpAccessories(taskId, studentId);
			ret.put("uptask", upTaskDetail);
			ret.put("upacc", accessoriesName);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ret;
	}
	
	/**
	 * 老师改分，评语
	 * @param request
	 * @param taskId
	 * @param studentId
	 * @return
	 */
	@RequestMapping("commendWork")
	public Map<String, Object>  commendWork(HttpServletRequest request,@RequestParam(value="taskId") String taskId,
			@RequestParam(value="studentId") String studentId,@RequestParam(value="grade") String grad,
			@RequestParam(value="comment") String comment) {
		Map<String, Object> ret = new HashMap<>();
		try {
			
			Integer grade = Integer.parseInt( grad);
			Timestamp commentTime = new Timestamp(System.currentTimeMillis());
			teacherService.setGradeAndComment(comment, grade, studentId, taskId,commentTime);
			ret.put("status", "Ok");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status","error");
		}
		return ret;

	}
	
	/**
	 * 获取已提交和未提交作业学生列表
	 * @return
	 */
	@RequestMapping(value="getTaskSatuation")
	public Map<String, Object> getTaskSatuation(@RequestParam(value="taskId") String taskId, @RequestParam(value="virtualClassNum") String virtualClassNum){
		Map<String, Object> ret = new HashMap<>();
		List<Student> studentUpedList = new ArrayList<Student>();
		List<Student> studentNotUpList = new ArrayList<Student>();
		try {
			studentNotUpList =teacherService.getStudentListOfNotUp(taskId, virtualClassNum);
			studentUpedList = teacherService.getStudentListOfUped(taskId,virtualClassNum);
			ret.put("studentNotUpList", studentNotUpList);
			ret.put("studentUpedList", studentUpedList);
			ret.put("status", "success");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
		}
		return ret;
	}
	
	/**
	 * 附件下载
	 */
	@RequestMapping("/resourceDownload")
	public ResponseEntity<byte[]> download(HttpServletRequest request,@RequestParam(value="fileName")String fileName,@RequestParam(value="id")String id) throws IOException {

		System.out.println(fileName);
		String studentId = (String) request.getSession().getAttribute("studentId");
		File file=null;
		if("".equals(studentId)||studentId==null) {
			file = new File(Common.readProperties("path")+"/"+id+"/"+fileName);
		}else {
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
	 * 返回全部作业
	 */
	@RequestMapping("getAllTask")
	public Map<String, Object> getAllTask(@RequestParam(value="userId")String userId){
		Map<String, Object> ret = new HashMap<>();
		try {
			//判断是学生还是老师
			Teacher teacher = teacherService.teacherLoginByEmployeeNum(userId);
			Student student = studentService.studentLoginByEmployeeNum(userId);
			if(teacher!=null){
				//获取所有发布的课程
				List<Task> teacher_task = teacherService.getTaskByUserId(userId);
				for(Task t : teacher_task){
				}
				ret.put("teacherTask", teacher_task);
			}
			else if(student != null){
				//获得虚拟班级号
				List<VirtualClass> virtualClass = teacherService.getVirtualClassNumByreal(student.getClassNum());
				for(VirtualClass v : virtualClass){
					List<String> taskId = teacherService.searchTaskId(v.getVirtualClassNum());
					List<Task> student_task = teacherService.TaskList(taskId);
					ret.put("studentTask", student_task);
				}
			}
			else{
				ret.put("status", "error");
			}
			} catch (Exception e) {
			// TODO: handle exception
				e.printStackTrace();
				ret.put("status", "error");
		}
		return ret;
	}
	

	
	/**
	 * 获得成绩和评语
	 */
	@RequestMapping("getcommendWork")
	public Map<String, Object> getcommendWork(@RequestParam(value="taskId") String taskId,@RequestParam(value="studentId") String studentId){
		Map<String, Object> ret = new HashMap<>();
		String comment = null;
		Integer grade=null;
		try {
			comment = teacherService.getComment(taskId, studentId);
			grade = teacherService.getGrade(taskId, studentId);
			ret.put("comment", comment);
			ret.put("grade", grade);
			ret.put("status", "Ok");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
		}
		return ret;
	}
	/**
	 * @author WENLI
	 * @param request
	 * @return
	 * 分析成绩
	 */
	@RequestMapping("analyseGrade")
	public Map<String, Object> analyseGrade(HttpServletRequest request,HttpServletResponse response,@RequestParam(value="virtualClassNum") String virtualClassNum) {
		Map<String, Object> ret = new HashMap<>();
		int workPublishNum=0;	//作业发布次数
		int trialPublishNum=0;	//实验发布次数
		int courseDesignPublishNum=0;	//课设发布次数
		int turnClassPublishNum=0;	//翻转课堂发布次数
		int attencePublishNum=0;	//考勤发布次数
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
			workPublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "work");
			trialPublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "trial");
			courseDesignPublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "course_design");
			turnClassPublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "turn_class");
			attencePublishNum = teacherService.gettaskTypePublishNum(virtualClassNum, "attence");
			ret.put("workPublishNum", workPublishNum);
			ret.put("trialPublishNum", trialPublishNum);
			ret.put("courseDesignPublishNum", courseDesignPublishNum);
			ret.put("turnClassPublishNum", turnClassPublishNum);
			ret.put("attencePublishNum", attencePublishNum);
			
			//获得学生成绩
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
			        ret.put("grade", jsonArray);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
		
	}
	
	/**
	 * 学生成绩分析
	 * @param request
	 * @return
	 */
	@RequestMapping(value="toPersonAccomplishment")
	public Map<String, Object> toPersonAccomplishment(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="studentId") String studentId,@RequestParam(value="virtualClassNum") String virtualClassNum) {
		Map<String, Object> ret = new HashMap<>();
		int gradeWork=0;
		int gradeTrial=0;
		int gradeCourseDesign=0;
		int gradeTurnClass=0;
		int gradeAttence=0;
		
		int upNumWork = 0;
		int upNumTrial = 0;
		int upNumCourseDesign = 0;
		int upNumTurnClass = 0;
		int upNumAttence = 0;
		try {

			request.setCharacterEncoding("utf-8");
			response.setContentType("application/json;charset=UTF-8");
			gradeWork = teacherService.getStudentGrade(studentId, virtualClassNum, "work");
			gradeTrial = teacherService.getStudentGrade(studentId, virtualClassNum, "trial");
			gradeCourseDesign = teacherService.getStudentGrade(studentId, virtualClassNum, "course_design");
			gradeTurnClass = teacherService.getStudentGrade(studentId, virtualClassNum, "turn_class");
			gradeAttence = teacherService.getStudentGrade(studentId, virtualClassNum, "attence");

			upNumWork = teacherService.getStudentGradeNum(studentId, virtualClassNum, "work");
			upNumTrial = teacherService.getStudentGradeNum(studentId, virtualClassNum, "trial");
			upNumCourseDesign = teacherService.getStudentGradeNum(studentId, virtualClassNum, "course_design");
			upNumTurnClass = teacherService.getStudentGradeNum(studentId, virtualClassNum, "turn_class");
			upNumAttence = teacherService.getStudentGradeNum(studentId, virtualClassNum, "attence");
			
			/*int minGradeWork = 0;
			int maxGradeWork = 0;
			int minGradeTrial = 0;
			int maxGradeTrial = 0;
			int minGradeTurnClass = 0;
			int maxGradeTurnClass = 0;
			int minGradeCourseDesign = 0;
			int maxGradeCourseDesign = 0;
			
			  minGradeWork = studentService.getMinGradeInCategory(virtualClassNum, "work");
			  maxGradeWork = studentService.getMaxGradeInCategory(virtualClassNum, "work");
			  minGradeTrial = studentService.getMinGradeInCategory(virtualClassNum,"trial"); 
			  maxGradeTrial = studentService.getMaxGradeInCategory(virtualClassNum, "trial");
			  minGradeTurnClass = studentService.getMinGradeInCategory(virtualClassNum, "turn_class"); 
			  maxGradeTurnClass =studentService.getMaxGradeInCategory(virtualClassNum, "turn_class");
			  minGradeCourseDesign = studentService.getMinGradeInCategory(virtualClassNum, "course_design");
			  maxGradeCourseDesign = studentService.getMaxGradeInCategory(virtualClassNum, "course_design");
			 
			
			ret.put("minGradeWork",minGradeWork );	
			ret.put("maxGradeWork",maxGradeWork );	
			ret.put("minGradeTrial",minGradeTrial );	
			ret.put("maxGradeTrial",maxGradeTrial );	
			ret.put("minGradeTurnClass",minGradeTurnClass );	
			ret.put("maxGradeTurnClass",maxGradeTurnClass );	
			ret.put("minGradeCourseDesign",minGradeCourseDesign );	
			ret.put("maxGradeCourseDesign",maxGradeCourseDesign );		
			*/
			List<Task> workList = new ArrayList<Task>();
			JSONArray workArr =  new JSONArray();
			List<Task> trialList = new ArrayList<Task>();
			JSONArray trialArr =  new JSONArray();
			List<Task> turn_classList = new ArrayList<Task>();
			JSONArray turn_classArr =  new JSONArray();
			List<Task> course_designList = new ArrayList<Task>();
			JSONArray course_designArr =  new JSONArray();
			
			workList = teacherService.getTaskByCategory(virtualClassNum, "work");
			trialList = teacherService.getTaskByCategory(virtualClassNum, "trial");
			turn_classList = teacherService.getTaskByCategory(virtualClassNum, "turn_class");
			course_designList = teacherService.getTaskByCategory(virtualClassNum, "course_design");
			
			workArr = taskSort(workList, studentId);
			trialArr = taskSort(trialList, studentId);
			turn_classArr = taskSort(turn_classList, studentId);
			course_designArr = taskSort(course_designList, studentId);
			
			
			ret.put("gradeWork",gradeWork );
			ret.put("gradeTrial",gradeTrial );
			ret.put("gradeCourseDesign",gradeCourseDesign );
			ret.put("gradeTurnClass",gradeTurnClass );
			ret.put("gradeAttence",gradeAttence );
			ret.put("upNumWork",upNumWork );
			ret.put("upNumTrial",upNumTrial );
			ret.put("upNumCourseDesign",upNumCourseDesign );
			ret.put("upNumTurnClass",upNumTurnClass );
			ret.put("upNumAttence",upNumAttence );
			ret.put("work", workArr);
			ret.put("trial", trialArr);
			ret.put("turn_class", turn_classArr);
			ret.put("course_design", course_designArr);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
		
	}
	
	/**
	 * 获取某个班级的考勤列表
	 * @param request
	 * @param virtualClassNum
	 * @return
	 */
	@RequestMapping(value="getAttendanceByClass")
	public Map<String, Object> getAttendanceByClass(@RequestParam(value="virtualClassNum") String virtualClassNum){
		Map<String, Object> ret = new HashMap<>();
		try {
			List<Attendance> attendanceList = new ArrayList<>();
			attendanceList = teacherService.getAttendanceDetail(virtualClassNum);
			ret.put("attendanceList", attendanceList);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
	}
	
	/**
	 * 显示已打卡，和未打卡的学生
	 * @return
	 */
	@RequestMapping(value="getStuAttSituation")
	public Map<String, Object> getStuAttSituation(@RequestParam(value="attendanceId") String attendanceId){
		Map<String, Object> ret = new HashMap<>();
		try {
			//获得已打卡的学生
			List<Student> attendedList = new ArrayList<>();
			attendedList = teacherService.getStuAttended(attendanceId);
			Map<String, Object> attendedMap = new HashMap<>();
			//获取请假的学生
			List<Student> studentLeaveList = teacherService.getStuLeaveList(attendanceId);//获取班上所有的学生
			//获得缺勤的学生
			List<Student> studentTruancyList = teacherService.getStuTruancyList(attendanceId);//获取班上所有的学生
			
			for(Student student : attendedList){
				//获取打卡时间
				String atttime = teacherService.getAttTime(student.getStudentId(),attendanceId);
				if(!"".equals(atttime)&&atttime != null){
					atttime = atttime.toString().substring(0, 16);
					String sid = student.getStudentId();
					student.setStudentId(atttime);
					attendedMap.put(sid, student);
				}
			}
			ret.put("attendedMap", attendedMap);
			ret.put("studentLeaveList", studentLeaveList);
			ret.put("studentTruancyList", studentTruancyList);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
	}

	
	/**
	 * 结束时显示已打卡，和未打卡的学生
	 * @return
	 */
	@RequestMapping(value="EndGetStuAttSituation")
	public Map<String, Object> EndGetStuAttSituation(@RequestParam(value="attendanceId") String attendanceId,
			@RequestParam(value="virtualClassNum") String virtualClassNum){
		Map<String, Object> ret = new HashMap<>();
		try {
			//获得已打卡的学生
			List<Student> attendedList = new ArrayList<>();
			attendedList = teacherService.getStuAttended(attendanceId);
			Map<String, Object> attendedMap = new HashMap<>();
			//获取未打卡的学生
			List<Student> studentList = teacherService.getStudentList(virtualClassNum);//获取班上所有的学生
			List<Student> disattended = new ArrayList<>();
			//做差集得到未打卡的
			if(studentList != null && !studentList.isEmpty()){
				for(Student s : studentList){
					boolean flag = true;
					for(Student stu : attendedList){
						if(s.getStudentId().equals(stu.getStudentId())){
							flag = false;
							break;
						}
					}
					if(flag)
						disattended.add(s);
				}
			}
			for(Student student : attendedList){
				//获取打卡时间
				String atttime = teacherService.getAttTime(student.getStudentId(),attendanceId);
				if(!"".equals(atttime)&&atttime != null){
					atttime = atttime.toString().substring(0, 16);
					String sid = student.getStudentId();
					student.setStudentId(atttime);
					attendedMap.put(sid, student);
				}
			}
			ret.put("attendedMap", attendedMap);
			ret.put("disattended", disattended);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}

	}
	
	
	
	
	/**
	 * 教师开启打卡
	 * @return
	 */
	@RequestMapping(value="startAttend")
	public Map<String, Object> startAttend(@RequestParam(value="employeeNum") String employeeNum,
			@RequestParam(value="virtualClassNum") String virtualClassNum){
		Map<String, Object> ret = new HashMap<>();
		try {
			//判断此班级是否有正在进行的打卡
			boolean isAttend = teacherService.isAttend(virtualClassNum);
			if(isAttend){
				Attendance att = new Attendance();
				att.setAttendanceId(Common.uuid());
				att.setVirtualClassNum(virtualClassNum);
				//设置第几次打卡
				if(teacherService.getLastAttIndex(virtualClassNum) != null)
					att.setAttIndex(teacherService.getLastAttIndex(virtualClassNum)+1);
				else
					att.setAttIndex(1);
				Timestamp attTime = new Timestamp(System.currentTimeMillis());
				att.setAttendanceTime(attTime);
				att.setAttendanceNum(0);
				att.setTotalNum(teacherService.getTaskUserNum(virtualClassNum));
				att.setLeaveNum(0);
				att.setTruancyNum(0);
				att.setPublishId(employeeNum);
				//添加
				teacherService.addAttendance(att);
				ret.put("att", att);
				ret.put("status", "ok");
			}
			else{
				ret.put("msg", "不能多次开启打卡！");
			}
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
		
	}
	
	/**
	 * 教师关闭打卡
	 * @return
	 */
	@RequestMapping(value="endAttend")
	public Map<String, Object> endAttend(@RequestParam(value="attendanceId") String attendanceId){
		Map<String, Object> ret = new HashMap<>();
		try {
			
			teacherService.endAttendance(attendanceId);
			ret.put("status", "ok");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
		
	}
	
	/**
	 * 获取当前打卡id
	 * @return
	 */
	@RequestMapping(value="getCurrentAttend")
	public Map<String, Object> getCurrentAttend(@RequestParam(value="virtualClassNum") String virtualClassNum){
		Map<String, Object> ret = new HashMap<>();
		try {
			//添加
			String attendanceId = teacherService.getCurrentAttend(virtualClassNum);
			ret.put("attendanceId", attendanceId);
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
		
	}
	
	/**
	 * 学生打卡
	 * @return
	 */
	@RequestMapping(value="attend")
	public Map<String, Object> attend(@RequestParam(value="studentId") String studentId,
			@RequestParam(value="attendanceId") String attendanceId){
		Map<String, Object> ret = new HashMap<>();
		try {
			Timestamp atttime = new Timestamp(System.currentTimeMillis());
			teacherService.attend(studentId,attendanceId,atttime);
			ret.put("status", "ok");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("msg", "不能重复打卡！");
			ret.put("status", "error");
			return ret;
		}
	}
	
	/**
	 * 显示某学生打卡记录
	 * @return
	 */
	@RequestMapping(value="showStuAtt")
	public Map<String, Object> showStuAtt(@RequestParam(value="studentId") String studentId){
		Map<String, Object> ret = new HashMap<>();
		try {
			//获取所有打卡记录
			List<Attendance> attList = teacherService.getALLAtt();
			List<Attendance> stuAtted = new ArrayList<>();
			List<Attendance> stuNoAtted = new ArrayList<>();
			if(attList != null && !attList.isEmpty()){
				for(Attendance a : attList){
					VirtualClass vclass = teacherService.getVirtualById(a.getVirtualClassNum());
					//将打卡记录的班级号改为班级名
					if(vclass != null)
						a.setVirtualClassNum(vclass.getVirtualClassName());
					String sid = teacherService.getAttRecordById(studentId,a.getAttendanceId());
					if(!"".equals(sid) && sid != null){
						stuAtted.add(a);
					}
					else{
						stuNoAtted.add(a);
					}
				}
			}
			ret.put("stuAtted", stuAtted);
			ret.put("stuNoAtted", stuNoAtted);
			ret.put("status", "ok");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("img", "不能重复打卡！");
			ret.put("status", "error");
			return ret;
		}
	}
	
	/**
	 * 打卡结束，标记未打卡的人请假还是缺勤
	 * @return
	 */
	@RequestMapping(value="signLeaveOrTruancy")
	public Map<String, Object> signLeaveOrTruancy(@RequestBody String data){
		JSONObject res = JSON.parseObject(data);
		Map<String, Object> ret = new HashMap<>();
		try {
			JSONArray noAttAryy = res.getJSONArray("noAttList");
			String attId = res.getString("attendanceId");
			for(int i = 0;i<noAttAryy.size();i++){
				JSONObject s =  noAttAryy.getJSONObject(i);
				if("-1".equals(s.getString("status"))){
					//设置为缺勤
					teacherService.setTruancy(s.getString("studentId"),attId);
				}
				else if("99".equals(s.getString("status"))){
					//设置为请假
					teacherService.setLeave(s.getString("studentId"),attId);
				}
			}
			
			ret.put("status", "ok");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
	}

	/**
	 * 选择作业发布
	 * @param request
	 * @return
	 */
	@RequestMapping(value="selectTaskToPublish")
	public Map<String, Object> selectTaskToPublish(HttpServletRequest request,@RequestParam(value="taskId") String taskId
			,@RequestParam(value="taskEndTime") String taskEndTime,@RequestParam(value="virtualClassNum") String virtualClassNum) {
		Map<String, Object> ret = new HashMap<>();
		//获取修改使用次数
		//int taskUseNum = teacherService.getTaskUserNum(virtualClassNum);
		try {
			teacherService.mapClassTask(virtualClassNum, taskId,Timestamp.valueOf(taskEndTime));
			teacherService.addUseNum(taskId);
			//teacherService.addWatchNum(taskId, taskUseNum);
			ret.put("status", "ok");
			return ret;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ret.put("status", "error");
			return ret;
		}
		
	}
	
	
	/**
	 * @description 获取成果数据集合
	 * @param category
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "achs")
	public JSONObject getAchievements(@RequestParam("category") String category,
			@RequestParam("pageNum") String pageNum, @RequestParam("pageSize") String pageSize) {
		JSONObject result = new JSONObject();

		// @RequestParam("category") String category,@RequestParam("pageNum") String
		// pageNum ,@RequestParam("pageSize") String pageSize
		try {
			// 大学生竞赛成果
			List<AOCSC> aocscList = new ArrayList<>();
			// 课程拓展
			List<CourseExpand> courseExpandList = new ArrayList<>();
			// 大学生毕业设计
			List<GDFCS> gdfcsList = new ArrayList<>();
			// 大学生创新创业
			List<SIAE> siaeList = new ArrayList<>();
			// 产学研类
			List<IURP> iURPList = new ArrayList<>();
			switch (category) {
			case "AOCSC":
				aocscList = iAchievementService.queryAOCSC();
				result.put("AOCSC", aocscList);
				break;
			case "CourseExpand":
				courseExpandList = iAchievementService.queryCourseExpand();
				result.put("CourseExpand", courseExpandList);
				break;
			case "GDFCS":
				gdfcsList = iAchievementService.queryGDFCS();
				result.put("GDFCS", gdfcsList);
				break;
			case "SIAE":
				siaeList = iAchievementService.querySIAE();
				result.put("SIAE", siaeList);
				break;
			case "IURP":
				iURPList = iAchievementService.queryIURP();
				result.put("IURP", iURPList);
				break;
			}
			result.put("status", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
		}
		return result;
	}

	/**
	 * @description 获取成果数据集合
	 * @param category
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "courseAchs")
	public JSONObject getCourseAch(@RequestParam("courseId") String courseId) {
		JSONObject result = new JSONObject();
		try {
			// 课程拓展
			List<CourseExpand> courseExpandList = iAchievementService.queryCourseExpandByCourseId(courseId);
			result.put("courseExpandList", courseExpandList);
			result.put("status", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			//logger.error(e.toString() + "\n", e);
			result.put("status", "error");
		}
		return result;
	}

	
	private JSONArray taskSort(List<Task> taskList, String studentId){
		
		HashMap<String, Integer>studentGradeMap = new HashMap<String, Integer>();
		HashMap<String , Integer>maxGrade = new HashMap<String, Integer>();
		HashMap<String , Integer>minGrade = new HashMap<String, Integer>();
		for (Task task : taskList) {
			studentGradeMap.put(task.getTaskId(),  teacherService.getGrade(task.getTaskId(), studentId));
			maxGrade.put(task.getTaskId(), studentService.getMaxGradeInTask(task.getTaskId()));
			minGrade.put(task.getTaskId(), studentService.getMinGradeInTask(task.getTaskId()));
		}
		JSONArray arr =  new JSONArray();
		for (Task task : taskList) {
			JSONObject ob= new JSONObject();
			ob.put("taskId", task.getTaskId());
			ob.put("taskTitle", task.getTaskTitle());
			ob.put("mygrade", studentGradeMap.get(task.getTaskId()));
			ob.put("minGrade", minGrade.get(task.getTaskId()));
			ob.put("maxGrade", maxGrade.get(task.getTaskId()));
			arr.add(ob);
		}
		return arr;
	}
	
}
