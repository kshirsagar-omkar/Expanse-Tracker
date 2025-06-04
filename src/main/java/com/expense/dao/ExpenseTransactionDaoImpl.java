package com.expense.dao;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.expense.entities.ExpenseTransaction;
import com.expense.util.HibernateUtil;

public class ExpenseTransactionDaoImpl implements ExpenseTransactionDao{

	@Override
	public Long saveExpenseTransaction(ExpenseTransaction expenseTransaction) {
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			Long expenseTransactionId = (Long) session.save(expenseTransaction) ;

			transaction.commit();
			return expenseTransactionId;
			
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
	public Boolean updateExpenseTransaction(ExpenseTransaction expenseTransaction) {
		Session session = null;
		Transaction transaction = null;
	
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			
			session.update(expenseTransaction);
			
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
	public Boolean deleteExpenseTransaction(ExpenseTransaction expenseTransaction) {
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();			
			transaction = session.beginTransaction();
			
			session.delete(expenseTransaction);
			
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
	public List<ExpenseTransaction> getAllExpenseTransactionByUserId(Long userId, Integer pageNumber) {

		Integer maxResultCount = 3;

		pageNumber *= maxResultCount;

		try(Session session = HibernateUtil.getSessionFactory().openSession()){
			
			String hql = "FROM ExpenseTransaction WHERE user.userId = :inputUserId"; 
			
			return session.createQuery(hql).setParameter("inputUserId", userId).setMaxResults(maxResultCount).setFirstResult(pageNumber).getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		
	}




	@Override
	public List<ExpenseTransaction> findExpenseTransactionByCategory(String category) {
		try(Session session = HibernateUtil.getSessionFactory().openSession()){
			
			String hql = "FROM ExpenseTransaction WHERE category LIKE :inputCategory"; 
			
			return session.createQuery(hql).setParameter("inputCategory", "%" + category + "%").getResultList();
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}




	@Override
	public List<ExpenseTransaction> getAllExpenseTransactionForDay(LocalDate date) {
		
		try(Session session = HibernateUtil.getSessionFactory().openSession()){
			
			String hql = "SELECT * FROM expense_transaction WHERE EXTRACT(YEAR FROM transaction_date) = :inputYear AND EXTRACT(MONTH FROM transaction_date) = :inputMonth AND EXTRACT(DAY FROM transaction_date) = :inputDay;";
			
			Integer day = date.getDayOfMonth();
			Integer month = date.getMonthValue();
			Integer year = date.getYear();  
		        
		        
			return session.createQuery(hql).setParameter("inputYear", year).setParameter("inputMonth", month).setParameter("inputDay", day).getResultList();
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}




	@Override
	public List<ExpenseTransaction> getAllExpenseTransactionForMonth(LocalDate date) {
		try(Session session = HibernateUtil.getSessionFactory().openSession()){
			
			String hql = "SELECT * FROM expense_transaction WHERE EXTRACT(YEAR FROM transaction_date) = :inputYear AND EXTRACT(MONTH FROM transaction_date) = :inputMonth";
			
			Integer month = date.getMonthValue();
			Integer year = date.getYear();  
		        
		        
			return session.createQuery(hql).setParameter("inputYear", year).setParameter("inputMonth", month).getResultList();
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}




	@Override
	public List<ExpenseTransaction> getAllExpenseTransactionForYear(LocalDate date) {
		try(Session session = HibernateUtil.getSessionFactory().openSession()){
			
			String hql = "SELECT * FROM expense_transaction WHERE EXTRACT(YEAR FROM transaction_date) = :inputYear";

			Integer year = date.getYear();     
		        
			return session.createQuery(hql).setParameter("inputYear", year).getResultList();
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	
	
	
	
}



















