package com.expense.dao;

import java.time.LocalDate;
import java.util.List;

import com.expense.entities.ExpenseTransaction;

public interface ExpenseTransactionDao {
	public abstract Long saveExpenseTransaction(ExpenseTransaction expenseTransaction);
	public abstract Boolean updateExpenseTransaction(ExpenseTransaction expenseTransaction);
	public abstract Boolean deleteExpenseTransaction(ExpenseTransaction expenseTransaction);
	public abstract List<ExpenseTransaction> getAllExpenseTransactionByUserId(Long userId, Integer pageNumber);
	public abstract List<ExpenseTransaction> findExpenseTransactionByCategory(String category);
	
	public abstract List<ExpenseTransaction> getAllExpenseTransactionForDay(LocalDate date);
	public abstract List<ExpenseTransaction> getAllExpenseTransactionForMonth(LocalDate date);
	public abstract List<ExpenseTransaction> getAllExpenseTransactionForYear(LocalDate date);
}
