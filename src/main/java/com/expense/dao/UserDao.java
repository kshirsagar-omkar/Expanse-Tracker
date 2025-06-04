package com.expense.dao;

import com.expense.entities.User;

public interface UserDao {
	
	public abstract Long saveUser(User user);
	public abstract User findUserById(Integer userId);
	public abstract User findUserByUsernameOrEmail(User user);
	public abstract Boolean updateUser(User user);
	public abstract Boolean deleteUser(User user);
	public abstract Boolean updatePassword(String email, String password);
	
}
