package com.expense.service;

import java.util.List;

import com.expense.entities.ExpenseTransaction;

public interface ExpenseTransactionService {
	public abstract Long saveExpenseTransaction(ExpenseTransaction expenseTransaction);
	public abstract Boolean updateExpenseTransaction(ExpenseTransaction expenseTransaction);
	public abstract Boolean deleteExpenseTransaction(ExpenseTransaction expenseTransaction);
	public abstract List<ExpenseTransaction> getAllExpenseTransactionByUserId(Long userId, Integer pageNumber);
	public abstract List<ExpenseTransaction> findExpenseTransactionByCategory(String category);
}
