package cn.edu.tit.idao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import cn.edu.tit.bean.Student;
import cn.edu.tit.bean.Task;
import cn.edu.tit.bean.TurnClassAccessory;
import cn.edu.tit.bean.TurnClassAchievement;
import cn.edu.tit.bean.TurnClassAfterModify;
import cn.edu.tit.bean.TurnClassFeedback;
import cn.edu.tit.bean.TurnClassPlanForClass;
import cn.edu.tit.bean.TurnClassPlanForCourse;
import cn.edu.tit.bean.TurnClassTeam;

@Component
public interface ITurnClassDao {

	List<Task> getAllTurnClass() throws Exception;

	List<Student> getAllStudentByRealClass(@Param("realClassNum")String realClassNum)throws Exception;

	void insertTaskTeam(TurnClassTeam tt)throws Exception;

	List<TurnClassTeam> readAllTeamForTask(@Param("taskId")String taskId)throws Exception;

	Task getTaskById(@Param("taskId")String taskId)throws Exception;

	TurnClassTeam getTeamById(@Param("teamId")String teamId)throws Exception;

	void insertTurnClassPlanForCourse(TurnClassPlanForCourse tc)throws Exception;

	void insertTurnClassAccessory(TurnClassAccessory tca)throws Exception;

	List<TurnClassPlanForClass> statusCheckout(@Param("teamId")String teamId, @Param("taskId")String taskId)throws Exception;

	List<TurnClassAchievement> statusCheckoutEnd(@Param("teamId")String teamId, @Param("taskId")String taskId)throws Exception;
	
	List<TurnClassAfterModify> statusCheckoutModify(@Param("teamId")String teamId, @Param("taskId")String taskId)throws Exception;
	
	void insertPrepareForClass(TurnClassPlanForClass tp)throws Exception;
	
	void insertClassAfterModify(TurnClassAfterModify tp)throws Exception;
	
	void insertEndAchievement(TurnClassAchievement tp)throws Exception;

	void insertFeedBack(TurnClassFeedback fb)throws Exception;

	TurnClassFeedback queryFeedBack(@Param("taskId")String taskId,@Param("teamId")String teamId)throws Exception;

	TurnClassAccessory queryTurnAccessory(@Param("taskId")String taskId, @Param("teamId")String teamId);

	List<TurnClassTeam> getAllTeam()throws Exception;

	List<TurnClassPlanForClass> getInfoForTeamDesignBeforeClass(@Param("taskId")String taskId, @Param("teamId")String teamId)throws Exception;

	List<TurnClassAfterModify> getInfoForTeamCourseAfterEdit(@Param("taskId")String taskId, @Param("teamId")String teamId)throws Exception;


}
