package com.expense.factory;

import com.expense.service.UserService;
import com.expense.service.UserServiceImpl;

public class UserServiceFactory {
	private UserServiceFactory() {}
	
	private static UserService userService= null;
	
	public static UserService getUserService() {
		if(userService == null) {
			userService = new UserServiceImpl();
		}
		return userService;
	}
}
