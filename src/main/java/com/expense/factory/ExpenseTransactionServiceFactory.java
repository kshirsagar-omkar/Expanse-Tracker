package com.expense.factory;

import com.expense.service.ExpenseTransactionService;
import com.expense.service.ExpenseTransactionServiceImpl;

public class ExpenseTransactionServiceFactory {
	private ExpenseTransactionServiceFactory() {}
	
	private static ExpenseTransactionService expenseTransactionService= null;
	
	public static ExpenseTransactionService getExpenseTransactionService() {
		if(expenseTransactionService == null) {
			expenseTransactionService = new ExpenseTransactionServiceImpl();
		}
		return expenseTransactionService;
	}
}
