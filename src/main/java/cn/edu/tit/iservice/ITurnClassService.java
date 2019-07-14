package cn.edu.tit.iservice;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.edu.tit.bean.Student;
import cn.edu.tit.bean.Task;
import cn.edu.tit.bean.TurnClassAccessory;
import cn.edu.tit.bean.TurnClassAchievement;
import cn.edu.tit.bean.TurnClassAfterModify;
import cn.edu.tit.bean.TurnClassFeedback;
import cn.edu.tit.bean.TurnClassPlanForClass;
import cn.edu.tit.bean.TurnClassPlanForCourse;
import cn.edu.tit.bean.TurnClassTeam;

public interface ITurnClassService {

	List<Task> getAllTurnClass() throws Exception;

	List<TurnClassTeam> getTaskTeam(String taskId)throws Exception;

	List<Student> getAllStudentByRealClass(String realClassNum)throws Exception;

	void insertTaskTeam(TurnClassTeam tt)throws Exception;

	List<TurnClassTeam> readAllTeamForTask(String taskId)throws Exception;

	Task getTaskById(String taskId)throws Exception;

	TurnClassTeam getTeamById(String teamId)throws Exception;

	void insertTurnClassPlanForCourse(TurnClassPlanForCourse tc)throws Exception;

	void insertTurnClassAccessory(TurnClassAccessory tca)throws Exception;

	List<TurnClassPlanForClass> statusCheckout(String teamId, String taskId)throws Exception;

	void insertPrepareForClass(TurnClassPlanForClass tp)throws Exception;

	void insertFeedBack(TurnClassFeedback fb)throws Exception;

	List<TurnClassAchievement> statusCheckoutEnd(String teamId,String taskId)throws Exception;

	List<TurnClassAfterModify> statusCheckoutModify(String teamId,String taskId)throws Exception;

	void insertClassAfterModify(TurnClassAfterModify tp)throws Exception;

	void insertEndAchievement(TurnClassAchievement tp)throws Exception;

	TurnClassFeedback queryFeedBack(String taskId,String teamId) throws Exception;

	TurnClassAccessory queryTurnAccessory(String taskId, String teamId)throws Exception;

	List<TurnClassTeam> getAllTeam()throws Exception;

	List<TurnClassPlanForClass> getInfoForTeamDesignBeforeClass(String taskId, String teamId)throws Exception;

	List<TurnClassAfterModify> getInfoForTeamCourseAfterEdit(String taskId, String teamId)throws Exception;

}
