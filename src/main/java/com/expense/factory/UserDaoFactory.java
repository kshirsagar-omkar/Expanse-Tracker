package com.expense.factory;

import com.expense.dao.UserDao;
import com.expense.dao.UserDaoImpl;

public class UserDaoFactory {
	private UserDaoFactory() {}
	
	private static UserDao userDao = null;
	
	public static UserDao getUserDao() {
		if(userDao == null) {
			userDao = new UserDaoImpl();
		}
		return userDao;
	}
}
