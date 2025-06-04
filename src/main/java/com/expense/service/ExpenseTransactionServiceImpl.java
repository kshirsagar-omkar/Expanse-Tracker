package com.expense.service;

import java.util.List;

import com.expense.entities.ExpenseTransaction;
import com.expense.factory.ExpenseTransactionDaoFactory;

public class ExpenseTransactionServiceImpl implements ExpenseTransactionService{

	@Override
	public Long saveExpenseTransaction(ExpenseTransaction expenseTransaction) {
		if(expenseTransaction == null) {
			return null;
		}
		//convert category to lower case for efficient searching
		expenseTransaction.setCategory( expenseTransaction.getCategory().toLowerCase() );
				
		return ExpenseTransactionDaoFactory.getExpenseTransactionDao().saveExpenseTransaction(expenseTransaction);
	}
	
	
	

	@Override
	public Boolean updateExpenseTransaction(ExpenseTransaction expenseTransaction) {
		if(expenseTransaction == null || expenseTransaction.getTransactionId() == null) {
			return false;
		}
		//convert category to lower case for efficient searching
		expenseTransaction.setCategory( expenseTransaction.getCategory().toLowerCase() );
				
		return ExpenseTransactionDaoFactory.getExpenseTransactionDao().updateExpenseTransaction(expenseTransaction);
	}

	
	
	
	
	@Override
	public Boolean deleteExpenseTransaction(ExpenseTransaction expenseTransaction) {
		if(expenseTransaction == null || expenseTransaction.getTransactionId() == null) {
			return false;
		}				
		return ExpenseTransactionDaoFactory.getExpenseTransactionDao().deleteExpenseTransaction(expenseTransaction);
	}

	
	
	
	
	@Override
	public List<ExpenseTransaction> getAllExpenseTransactionByUserId(Long userId, Integer pageNumber) {
		if(userId == null) {
			return null;
		}
		//decrementing pageNumber by 1 because the page number start from 1 for front end side
		//And pagination works from 0
		return ExpenseTransactionDaoFactory.getExpenseTransactionDao().getAllExpenseTransactionByUserId(userId, --pageNumber);
	}
	
	
	
	
	
	
	
	

	@Override
	public List<ExpenseTransaction> findExpenseTransactionByCategory(String category) {
		if(category == null) {
			return null;
		}
		return ExpenseTransactionDaoFactory.getExpenseTransactionDao().findExpenseTransactionByCategory(category);
	}
	
	
	
	
	
	
	
	
	
	
//	
//	public static void main(String []args) {
//		
//		
////		ExpenseTransaction es = new ExpenseTransaction();
////		
////		es.setTransactionId(1l);
////		es.setCategory("Cake");
////		es.setAmount(350.00);
////		es.setNote("pappa ch b'day");
////		es.setTransactionType(TransactionType.Expense);
////		es.setTransactionDate(LocalDate.now());
////		
////		User user = new User();
////		user.setUserId(1L);
////		
////		es.setUser(user);
////		
////		
////		
////		
////		Long tid = ExpenseTransactionServiceFactory.getExpenseTransactionService().saveExpenseTransaction(es);
////		
////		if(tid != null) {
////			System.out.println("record save successfully : " + tid);
////		}else {
////			System.out.println("unable to save record!!");
////		}
////		
////		
////		
////		
////		Boolean status= ExpenseTransactionServiceFactory.getExpenseTransactionService().updateExpenseTransaction(es);
////		
////		if(status == true) {
////			System.out.println("record update successfully : " + status);
////		}else {
////			System.out.println("unable to update record!!");
////		}
//		
//		
//		
//		Scanner sc = new Scanner(System.in);
//		
//		while(true) {
//			
//		
//			System.out.print("Enter category or exit :");
//			String choice = sc.next();
//			
//			if(choice.equals("exit")) {
//				break;
//			}
//			
//			List<ExpenseTransaction> l = ExpenseTransactionServiceFactory.getExpenseTransactionService().findExpenseTransactionByCategory(choice);
//			
//			for(ExpenseTransaction es : l) {
//				System.out.println(es);
//				System.out.println("----------\n---------");
//			}
//		}
//		sc.close();
//		
//		
//		
//	}
	
	
	

}
















