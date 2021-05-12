package com.mju.groupware.dao;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.mju.groupware.dto.ConstantAdminUserDao;
import com.mju.groupware.dto.Student;
import com.mju.groupware.dto.User;
import com.mju.groupware.dto.UserInfoOpen;
import com.mju.groupware.dto.WithdrawalUser;
import com.mju.groupware.security.UsersDetails;

@Service
@Repository
public class UserDaoImpl implements UserDao {
	private ConstantAdminUserDao Constant;

	public UserDaoImpl() {
		GenericXmlApplicationContext CTX = new GenericXmlApplicationContext();
		CTX.load("classpath:/xmlForProperties/UserDao.xml");
		CTX.refresh();
		this.Constant = (ConstantAdminUserDao) CTX.getBean("UserDaoID");
	}

	@Autowired
	private SqlSessionTemplate sqlSession;
	// 이름
	private String UserName;
	// 아이디
	private int UserID;
	// 로그인 아이디
	private String UserLoginID;
	// 전화번호
	private String UserPhoneNum;
	// user role
	private String UserRole;
	// 이메일
	private String UserEmail;

	private boolean EmailCheck;

	private String OpenName;
	private String OpenEmail;
	private String OpenPhoneNum;
	private String OpenMajor;
	private String OpenGrade;

	@Override
	public void InsertForSignUp(User user) {
		this.sqlSession.insert(this.Constant.getInsertUser(), user);
	}

	@Override
	public UsersDetails selectByLoginID(String userLoginID) {
		UsersDetails users = this.sqlSession.selectOne(this.Constant.getSelectByLoginID(), userLoginID);
		return users;
	}

	@Override
	public boolean SelctForIDConfirm(User user) {
		User Output = this.sqlSession.selectOne(this.Constant.getSelctForIDConfirm(), user);

		if (Output == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean SelectPwdForConfirmToFindPwd(User user) {
		User Output = this.sqlSession.selectOne(this.Constant.getSelectPwdForConfirmToFindPwd(), user);

		if (Output == null) {
			return false;
		} else {
			return true;
		}

	}

	@Override
	public int SelectUserID(Student student) {
		return this.sqlSession.selectOne(this.Constant.getSelectUserID(), student);
	}

	@Override
	public boolean SelectForShowPassword(User user) {
		User Output = this.sqlSession.selectOne(this.Constant.getSelectForShowPassword(), user);
		if (Output == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean SelectForEmailDuplicateCheck(User user) {
		User Output = sqlSession.selectOne(this.Constant.getSelectForEmailDuplicateCheck(), user.getUserEmail());

		if (Output == null) {
			EmailCheck = false;
		} else {
			EmailCheck = true;
		}

		return EmailCheck;
	}

	@Override
	public void UpdateLoginDate(User user) {
		this.sqlSession.selectOne(this.Constant.getUpdateLoginDate(), user);
	}

	@Override
	public String SelectCurrentPwd(String id) {
		return this.sqlSession.selectOne(this.Constant.getSelectCurrentPwd(), id);
	}

	@Override
	public void UpdatePwd(User user) {
		this.sqlSession.update(this.Constant.getUpdatePwd(), user);
	}

	@Override
	public boolean SelectForPwdCheckBeforeModify(String id, String pwd) {

		// 추후 entity로 이동해야한다.
		BCryptPasswordEncoder Encoder = new BCryptPasswordEncoder();
		String Output = this.sqlSession.selectOne(this.Constant.getSelectForPwdCheckBeforeModify(), id);
		if (Output == null) {
			return false;
		} else {
			if (Encoder.matches(pwd, Output)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public ArrayList<String> SelectUserProfileInfo(String id) {
		// 정보를 저장하기 위한 ArrayList
		ArrayList<String> Info = new ArrayList<String>();
		// 학생정보를 가져오는 query문 실행
		List<User> Output = this.sqlSession.selectList(this.Constant.getSelectUserInfo(), id);

		if (Output == null) {

		} else {
			for (User Item : Output) {
				UserID = Item.getUserID();
				UserName = Item.getUserName();
				UserRole = Item.getUserRole();
			}

			// 이름 0
			Info.add(UserName);
			// 아이디 1
			Info.add(Integer.toString(UserID));
			// role 2
			Info.add(UserRole);
		}
		return Info;
	}

	@Override
	public void updateUserPhoneNumber(User user) {
		sqlSession.update(this.Constant.getUpdateUserPhoneNum(), user);
	}

	@Override
	public void updateUserMajor(User user) {
		sqlSession.update(this.Constant.getUpdateUserMajor(), user);
	}

	@Override
	public void updateUserColleges(User user) {
		sqlSession.update(this.Constant.getUpdateUserColleges(), user);
	}

	@Override
	public ArrayList<String> SelectUserInformation(String userId) {
		ArrayList<String> UserInfo = new ArrayList<String>();
		List<User> Output = sqlSession.selectList(this.Constant.getSelectUserInformation(), userId);
		if (Output == null) {

		} else {
			for (User Item : Output) {
				UserID = Item.getUserID();
				UserLoginID = Item.getUserLoginID().toString();
			}

			// 아이디
			UserInfo.add(Integer.toString(UserID));
			// 로그인 아이디(학번)
			UserInfo.add(UserLoginID);
		}
		return UserInfo;
	}

	@Override
	public void UpdateTemporaryPwd(User user) {
		sqlSession.update(this.Constant.getUpdateTemporaryPwd(), user);
	}

	@Override
	public void UpdateWithdrawlUser(String id) {
		sqlSession.update(this.Constant.getUpdateWithdrawal(), id);
	}

	@Override
	public void UpdateDoWithdrawalRecoveryByAdmin(String id) {
		sqlSession.update(this.Constant.getUpdateDoWithdrawalRecoveryByAdmin(), id);
	}

	@Override
	public void UpdateDormantOneToZero(String id) {
		sqlSession.update(this.Constant.getUpdateDormantOneToZero(), id);
	}

	@Override
	public void UpdateUserRole(String id, String role) {
		User user = new User();
		user.setUserLoginID(id);
		user.setUserRole(role);
		user.setAuthority(this.Constant.getROLE_USER());
		sqlSession.update(this.Constant.getUpdateUserRole(), user);
	}

	@Override
	public void UpdateAdminRole(String id, String role) {
		User user = new User();
		user.setUserLoginID(id);
		user.setUserRole(role);
		user.setAuthority(this.Constant.getROLE_ADMIN());
		sqlSession.update(this.Constant.getUpdateAdminRole(), user);
	}

	@Override
	public ArrayList<String> SelectMyPageUserInfo(String userId) {
		ArrayList<String> Info = new ArrayList<String>();
		List<User> Output = this.sqlSession.selectList(this.Constant.getSelectMyPageInfo(), userId);
		if (Output == null) {

		} else {
			for (User Item : Output) {
				UserID = Item.getUserID();
				UserLoginID = Item.getUserLoginID();
				UserName = Item.getUserName();
				UserPhoneNum = Item.getUserPhoneNum();
				UserEmail = Item.getUserEmail();
			}
			Info.add(Integer.toString(UserID));
			Info.add(UserLoginID);
			Info.add(UserName);
			Info.add(UserPhoneNum);
			Info.add(UserEmail);
		}
		return Info;
	}

	@Override
	public ArrayList<String> SelectMyPageUserInfoByID(String mysqlID) {
		ArrayList<String> Info = new ArrayList<String>();
		List<User> Output = this.sqlSession.selectList(this.Constant.getSelectMyPageInfoByID(), mysqlID);
		if (Output == null) {
		} else {
			for (User Item : Output) {
				UserLoginID = Item.getUserLoginID();
				UserName = Item.getUserName();
				UserPhoneNum = Item.getUserPhoneNum();
				UserEmail = Item.getUserEmail();
				OpenName = Item.getOpenName();
				OpenEmail = Item.getOpenEmail();
				OpenPhoneNum = Item.getOpenPhoneNum();
				OpenMajor = Item.getOpenMajor();
				OpenGrade = Item.getOpenGrade();
			}
			Info.add(UserLoginID);
			Info.add(UserName);
			Info.add(UserPhoneNum);
			Info.add(UserEmail);
			Info.add(OpenName);
			Info.add(OpenEmail);
			Info.add(OpenPhoneNum);
			Info.add(OpenMajor);
			Info.add(OpenGrade);
		}
		return Info;
	}

	@Override
	public void UpdateUserName(User user) {
		this.sqlSession.update(this.Constant.getUpdateUserName(), user);
	}

	@Override
	public void UpdateOpenName(User user) {
		this.sqlSession.update(this.Constant.getUpdateOpenName(), user);
	}

	@Override
	public void UpdateOpenEmail(User user) {
		this.sqlSession.update(this.Constant.getUpdateOpenEmail(), user);
	}

	@Override
	public void UpdatePhoneNum(User user) {
		this.sqlSession.update(this.Constant.getUpdateOpenPhoneNum(), user);
	}

	@Override
	public void UpdateOpenMajor(User user) {
		sqlSession.update(this.Constant.getUpdateOpenMajor(), user);
	}

	@Override
	public void UpdateOpenGrade(User user) {
		sqlSession.update(this.Constant.getUpdateOpenGrade(), user);
	}

	@Override
	public User SelectUserInfo(String userLoginID) {
		User Output = sqlSession.selectOne(this.Constant.getSelectUserInfoForWithdrawal(), userLoginID);
		return Output;
	}

	@Override
	public void InsertWithdrawalUser(User userInfo) {
		sqlSession.insert(this.Constant.getInsertWithdrawalUser(), userInfo);
	}

	@Override
	public void DeleteWithdrawalUser(User user) {
		sqlSession.delete(this.Constant.getDeleteWithdrawalUser(), user);
	}

	@Override
	public void DeleteWithdrawalUserList(WithdrawalUser withdrawalUser) {
		sqlSession.delete(this.Constant.getDeleteWithdrawalUserList(), withdrawalUser);
	}

	@Override
	public List<UserInfoOpen> SelectOpenInfo(String userID) {
		List<UserInfoOpen> UserInfoOpen = this.sqlSession.selectList(this.Constant.getSelectOpenInfo(),userID);
		return UserInfoOpen;
	}

	@Override
	public int SelectUserIDFromBoardController(String userLoginID) {
		int userID = this.sqlSession.selectOne(this.Constant.getSelectUserIDFromBoardController(), userLoginID);
		System.out.println(userID);
		return userID;
	}
}