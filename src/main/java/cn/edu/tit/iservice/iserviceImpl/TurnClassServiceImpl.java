package cn.edu.tit.iservice.iserviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.tit.bean.Student;
import cn.edu.tit.bean.Task;
import cn.edu.tit.bean.TurnClassAccessory;
import cn.edu.tit.bean.TurnClassAchievement;
import cn.edu.tit.bean.TurnClassAfterModify;
import cn.edu.tit.bean.TurnClassFeedback;
import cn.edu.tit.bean.TurnClassPlanForClass;
import cn.edu.tit.bean.TurnClassPlanForCourse;
import cn.edu.tit.bean.TurnClassTeam;
import cn.edu.tit.idao.ITurnClassDao;
import cn.edu.tit.iservice.ITurnClassService;

@Service
public class TurnClassServiceImpl implements ITurnClassService{
	@Autowired
	private  ITurnClassDao turnClassDao ;

	@Override
	public List<Task> getAllTurnClass() throws Exception{
		List<Task> list = new ArrayList<>();
		try {
			list = turnClassDao.getAllTurnClass();
		} catch (Exception e) {
			list = null;
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<TurnClassTeam> getTaskTeam(String taskId) throws Exception {
		List<TurnClassTeam> list = new ArrayList<>();
		try {
			list = turnClassDao.readAllTeamForTask(taskId);
		} catch (Exception e) {
			list = null;
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Student> getAllStudentByRealClass(String realClassNum) throws Exception {
		return turnClassDao.getAllStudentByRealClass(realClassNum);
	}

	@Override
	public void insertTaskTeam(TurnClassTeam tt) throws Exception {
		turnClassDao.insertTaskTeam(tt);
	}

	@Override
	public List<TurnClassTeam> readAllTeamForTask(String taskId) throws Exception {
		// TODO Auto-generated method stub
		return turnClassDao.readAllTeamForTask(taskId);
	}

	@Override
	public Task getTaskById(String taskId) throws Exception {
		// TODO Auto-generated method stub
		return turnClassDao.getTaskById(taskId);
	}

	@Override
	public TurnClassTeam getTeamById(String teamId) throws Exception {
		// TODO Auto-generated method stub
		return turnClassDao.getTeamById(teamId);
	}

	@Override
	public void insertTurnClassPlanForCourse(TurnClassPlanForCourse tc) throws Exception {
		try {
			turnClassDao.insertTurnClassPlanForCourse(tc);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void insertTurnClassAccessory(TurnClassAccessory tca) throws Exception {
		try {
			turnClassDao.insertTurnClassAccessory(tca);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public List<TurnClassPlanForClass> statusCheckout(String teamId, String taskId) throws Exception {
		return turnClassDao.statusCheckout(teamId,taskId);
	}

	@Override
	public void insertPrepareForClass(TurnClassPlanForClass tp) throws Exception {
		turnClassDao.insertPrepareForClass(tp);
		// TODO Auto-generated method stub
	}

	@Override
	public void insertFeedBack(TurnClassFeedback fb) throws Exception {
		turnClassDao.insertFeedBack(fb);
	}

	@Override
	public List<TurnClassAchievement> statusCheckoutEnd(String teamId, String taskId) throws Exception {
		return turnClassDao.statusCheckoutEnd(teamId,taskId);
	}

	@Override
	public List<TurnClassAfterModify> statusCheckoutModify(String teamId, String taskId) throws Exception {
		return turnClassDao.statusCheckoutModify(teamId,taskId);
	}

	@Override
	public void insertClassAfterModify(TurnClassAfterModify tp) throws Exception {
		turnClassDao.insertClassAfterModify(tp);		
	}

	@Override
	public void insertEndAchievement(TurnClassAchievement tp) throws Exception {
		turnClassDao.insertEndAchievement(tp);				
	}

	@Override
	public TurnClassFeedback queryFeedBack(String taskId,String teamId) throws Exception {
		return turnClassDao.queryFeedBack(taskId,teamId);
	}

	@Override
	public TurnClassAccessory queryTurnAccessory(String taskId, String teamId) throws Exception {
		return turnClassDao.queryTurnAccessory(taskId,teamId);
	}

	@Override
	public List<TurnClassTeam> getAllTeam() throws Exception {
		return turnClassDao.getAllTeam();
	}

	@Override
	public List<TurnClassPlanForClass> getInfoForTeamDesignBeforeClass(String taskId, String teamId) throws Exception {
		return turnClassDao.getInfoForTeamDesignBeforeClass(taskId,teamId);
	}

	@Override
	public List<TurnClassAfterModify> getInfoForTeamCourseAfterEdit(String taskId, String teamId) throws Exception {
		return turnClassDao.getInfoForTeamCourseAfterEdit(taskId,teamId);
	}

}
