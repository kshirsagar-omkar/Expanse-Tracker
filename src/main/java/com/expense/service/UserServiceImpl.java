package com.expense.service;

import com.expense.entities.User;
import com.expense.factory.UserDaoFactory;
import com.expense.util.PasswordUtil;

public class UserServiceImpl implements UserService{

	@Override
	public Long saveUser(User user) {
		
		//Secure the password
		try {
			String securePassword = PasswordUtil.generateSecurePassword( user.getPasswordHash() );	
			user.setPasswordHash(securePassword);
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
		
		//Save user to database
		return UserDaoFactory.getUserDao().saveUser(user);
	}

	
	
	
	
	
	
	@Override
	public User findUserById(Integer userId) {
		return UserDaoFactory.getUserDao().findUserById(userId);
	}

	
	
	
	
	
	@Override
	public User findUserByUsernameOrEmail(User user) {

		if( user == null || (user.getUsername() == null && user.getEmail() == null) ) {
			System.out.println("\nProblem in 'UserServiceImpl.findUserByUsernameOrEmail()' Method\n");
			return null;
		}
		
		return UserDaoFactory.getUserDao().findUserByUsernameOrEmail(user);
	}







	@Override
	public Boolean updateUser(User user) {
		if(user == null || user.getUserId() == null) {
			return false;
		}
		String securePassword = PasswordUtil.generateSecurePassword( user.getPasswordHash() );	
		user.setPasswordHash(securePassword);
		return UserDaoFactory.getUserDao().updateUser(user);
	}







	@Override
	public Boolean deleteUser(User user) {
		if(user == null || user.getUserId() == null) {
			return false;
		}
		return UserDaoFactory.getUserDao().deleteUser(user);
	}







	@Override
	public Boolean updatePassword(String email, String password) {
		if( email == null || password == null ) {
			return false;
		}
		return true;
	}

	
	
	
	
	
	
	
	
	
	
	
	/*
	public static void main(String[] args) {
		User user = new User();
		
		user.setUsername("omkar");
		user.setEmail("om@gmail.com");
		user.setPasswordHash("123");
		
		
		UserService userService = UserServiceFactory.getUserService();
		
		Long userId = userService.saveUser(user);
		
		if(userId != null) {
			System.out.println("Done!!");
		}else {
			System.out.println("Not Done!!!");
		}
	
		
		System.out.println(PasswordUtil.checkPassword("123", "iIfw5jo8Vd0bbCIfcCi8rQ==:4TdoRt7dVsEXrVTGR3rKfm1hNMg9EHcjFh2RadBSAv8="));
		
		HibernateUtil.closeSessionFactory();
	}
	*/
}











