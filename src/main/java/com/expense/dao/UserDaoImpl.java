package com.expense.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.expense.entities.User;
import com.expense.factory.UserDaoFactory;
import com.expense.util.HibernateUtil;

public class UserDaoImpl implements UserDao{

	@Override
	public Long saveUser(User user) {
		
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Long userId = (Long) session.save(user) ;

			transaction.commit();
			return userId;
			
		}catch(Exception e) {
			e.printStackTrace();
			if(transaction != null) {
				transaction.rollback();
			}
			return null;
		}finally {
			if(session != null) {
				session.close();
			}
		}
	}

	
	
	
	@Override
	public User findUserById(Integer userId) {
		
		
		try(Session session = HibernateUtil.getSessionFactory().openSession()) {
			
			return session.get(User.class, userId);
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	
	
	
	
	@Override
	public User findUserByUsernameOrEmail(User user) {

		try(Session session = HibernateUtil.getSessionFactory().openSession()) {
			
			return session.createQuery("FROM User WHERE username = :inputUsername OR email = :inputEmail", User.class)
										.setParameter("inputUsername", user.getUsername())
										.setParameter("inputEmail", user.getEmail())
										.uniqueResultOptional()
										.orElse(null);
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}

	
	
	



	@Override
	public Boolean updateUser(User user) {
		Session session = null;
		Transaction transaction = null;
	
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			session.update(user);
			
			transaction.commit();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			if(transaction != null) {
				transaction.rollback();
			}
			return false;
		}finally {
			if(session != null) {
				session.close();
			}
		}
	
	}




	@Override
	public Boolean deleteUser(User user) {
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();			
			transaction = session.beginTransaction();
			
			session.delete(user);
			
			transaction.commit();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			if(transaction != null) {
				transaction.rollback();
			}
			return false;
		}finally {
			if(session != null) {
				session.close();			
			}
		}
	}




	@Override
	public Boolean updatePassword(String email, String password) {
		
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();			
			transaction = session.beginTransaction();
			
			String hql = "UPDATE users SET password_hash = :inputPassword WHERE email = :inputEmail";
			
			if( session.createQuery(hql)
					.setParameter("inputPassword", password)
					.setParameter("inputEmail", email)
					.executeUpdate() == 0) {
				return false;
			}
			
			transaction.commit();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			if(transaction != null) {
				transaction.rollback();
			}
			return false;
		}finally {
			if(session != null) {
				session.close();			
			}
		}

	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		
		
		User user = new User();
		user.setUserId(1l);
		user.setUsername("om");
		user.setEmail("om@gmail.com");
		user.setPasswordHash("123");
		
		UserDao userDao = UserDaoFactory.getUserDao();
		
		System.out.println(userDao.updateUser(user));
		
	}
	
	
	
	
	
	
	
}

























