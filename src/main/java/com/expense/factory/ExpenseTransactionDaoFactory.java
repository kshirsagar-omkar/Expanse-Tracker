package com.expense.factory;

import com.expense.dao.ExpenseTransactionDao;
import com.expense.dao.ExpenseTransactionDaoImpl;

public class ExpenseTransactionDaoFactory {
	private ExpenseTransactionDaoFactory() {}
	
	private static ExpenseTransactionDao expenseTransactionDao = null;
	
	public static ExpenseTransactionDao getExpenseTransactionDao() {
		if(expenseTransactionDao == null) {
			expenseTransactionDao = new ExpenseTransactionDaoImpl();
		}
		return expenseTransactionDao;
	}
}
