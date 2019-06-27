package cn.edu.tit.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.edu.tit.bean.Course;
import cn.edu.tit.bean.CourseExpand;
import cn.edu.tit.bean.IURP;
import cn.edu.tit.bean.Teacher;
import cn.edu.tit.iservice.IAchievementService;
import cn.edu.tit.iservice.ITeacherService;

@RequestMapping("/main")
@Controller
public class MainController {
	@Autowired
	public ITeacherService teacherService;
	
	@Autowired
	private IAchievementService iAchievementService;
	/**
	 * 跳转到主页
	 * @param request
	 * @return
	 */
	@RequestMapping(value="toMain")
	public ModelAndView toMain(HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		try {

			//按时间查询前12个课程信息

			//按时间查询前8个课程信息

			List<Course> courseList = teacherService.getCourseByLimit();
			List<String> publishTime = new ArrayList<>();
			//获取教师团队
			List<Teacher> teacherList = new ArrayList<>();
			for(Course course : courseList){
				teacherList = teacherService.getTeachersByCourseId(course.getCourseId());
				course.setTeacherList(teacherList);
				publishTime.add(course.getPublishTime().toString().substring(0,10));
			}
			mv.addObject("courseList",courseList);
			//获取课程成果
			List<CourseExpand> aocscList  = iAchievementService.queryCourseExpand();
			mv.addObject("aocscList", aocscList);
			//mv.addObject("teacherNameList",teacherNameList);

			
			List<IURP> iURPList =new ArrayList<>();
			List<IURP> iURPList2 =new ArrayList<>();
				iURPList = iAchievementService.queryIURP();
				for(IURP i : iURPList){
					int ind = i.getIntroduction().length();
					if(ind >= 10)
						i.setIntroduction(i.getIntroduction().substring(0, 9));
					int in = i.getProjectName().length();
					if(in >= 10){
						i.setProjectName(i.getProjectName().substring(0,9));
					}
				}
				if(iURPList.size()>=6){
					for(int i = 0;i<6;i++){
						iURPList2.add(iURPList.get(i));
					}
					mv.addObject("iURPList",iURPList2);
				}
				else
					mv.addObject("iURPList",iURPList);
			
			mv.addObject("publishTime",publishTime);
			mv.addObject("teacherList",teacherList);

			mv.setViewName("/jsp/main");
			mv.addObject("teacher",request.getSession().getAttribute("teacher"));
			mv.addObject("student",request.getSession().getAttribute("student"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		return mv;
	}
}
