package com.expense.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

import com.expense.entities.ExpenseTransaction;
import com.expense.entities.User;
import com.expense.enums.TransactionType;
import com.expense.factory.ExpenseTransactionServiceFactory;
import com.expense.factory.UserServiceFactory;
import com.expense.service.ExpenseTransactionService;
import com.expense.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/transactions/*")
public class TransactionController extends HttpServlet {
    
    private ExpenseTransactionService transactionService;
    private UserService userService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        transactionService = ExpenseTransactionServiceFactory.getExpenseTransactionService();
        userService = UserServiceFactory.getUserService();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setJsonResponse(response);
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || "/".equals(pathInfo)) {
                handleGetUserTransactions(request, response);
            } else if (pathInfo.startsWith("/category/")) {
                handleGetTransactionsByCategory(request, response);
            } else {
                sendErrorResponse(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setJsonResponse(response);
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/add".equals(pathInfo) || pathInfo == null || "/".equals(pathInfo)) {
                handleAddTransaction(request, response);
            } else {
                sendErrorResponse(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setJsonResponse(response);
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/update".equals(pathInfo)) {
                handleUpdateTransaction(request, response);
            } else {
                sendErrorResponse(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setJsonResponse(response);
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                handleDeleteTransaction(request, response);
            } else {
                sendErrorResponse(response, 404, "Invalid transaction ID");
            }
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    private void handleGetUserTransactions(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, 401, "User not logged in");
            return;
        }
        
        Long userId = (Long) session.getAttribute("userId");
        String pageParam = request.getParameter("page");
        Integer pageNumber = pageParam != null ? Integer.parseInt(pageParam) : 1;
        
        try {
            List<ExpenseTransaction> transactions = transactionService.getAllExpenseTransactionByUserId(userId, pageNumber);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.add("transactions", gson.toJsonTree(transactions));
            responseJson.addProperty("page", pageNumber);
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Failed to fetch transactions: " + e.getMessage());
        }
    }
    
    private void handleGetTransactionsByCategory(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String pathInfo = request.getPathInfo();
        String category = pathInfo.substring("/category/".length());
        
        try {
            List<ExpenseTransaction> transactions = transactionService.findExpenseTransactionByCategory(category);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.add("transactions", gson.toJsonTree(transactions));
            responseJson.addProperty("category", category);
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Failed to fetch transactions by category: " + e.getMessage());
        }
    }
    
    private void handleAddTransaction(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, 401, "User not logged in");
            return;
        }
        
        JsonObject requestJson = parseRequestBody(request);
        Long userId = (Long) session.getAttribute("userId");
        
        if (!requestJson.has("amount") || !requestJson.has("transactionType")) {
            sendErrorResponse(response, 400, "Missing required fields");
            return;
        }
        
        try {
            User user = userService.findUserById(userId.intValue());
            if (user == null) {
                sendErrorResponse(response, 404, "User not found");
                return;
            }
            
            ExpenseTransaction transaction = new ExpenseTransaction();
            transaction.setAmount(requestJson.get("amount").getAsDouble());
            transaction.setTransactionType(TransactionType.valueOf(requestJson.get("transactionType").getAsString()));
            transaction.setUser(user);
            
            if (requestJson.has("note")) {
                transaction.setNote(requestJson.get("note").getAsString());
            }
            if (requestJson.has("category")) {
                transaction.setCategory(requestJson.get("category").getAsString());
            }
            if (requestJson.has("transactionDate")) {
                transaction.setTransactionDate(LocalDate.parse(requestJson.get("transactionDate").getAsString()));
            } else {
                transaction.setTransactionDate(LocalDate.now());
            }
            
            Long transactionId = transactionService.saveExpenseTransaction(transaction);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.addProperty("transactionId", transactionId);
            responseJson.addProperty("message", "Transaction added successfully");
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 400, "Failed to add transaction: " + e.getMessage());
        }
    }
    
    private void handleUpdateTransaction(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, 401, "User not logged in");
            return;
        }
        
        JsonObject requestJson = parseRequestBody(request);
        
        if (!requestJson.has("transactionId")) {
            sendErrorResponse(response, 400, "Missing transaction ID");
            return;
        }
        
        try {
            // Create transaction object with updated fields
            ExpenseTransaction transaction = new ExpenseTransaction();
            transaction.setTransactionId(requestJson.get("transactionId").getAsLong());
            
            if (requestJson.has("amount")) {
                transaction.setAmount(requestJson.get("amount").getAsDouble());
            }
            if (requestJson.has("transactionType")) {
                transaction.setTransactionType(TransactionType.valueOf(requestJson.get("transactionType").getAsString()));
            }
            if (requestJson.has("note")) {
                transaction.setNote(requestJson.get("note").getAsString());
            }
            if (requestJson.has("category")) {
                transaction.setCategory(requestJson.get("category").getAsString());
            }
            if (requestJson.has("transactionDate")) {
                transaction.setTransactionDate(LocalDate.parse(requestJson.get("transactionDate").getAsString()));
            }
            
            Boolean updated = transactionService.updateExpenseTransaction(transaction);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", updated);
            responseJson.addProperty("message", updated ? "Transaction updated successfully" : "Update failed");
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Failed to update transaction: " + e.getMessage());
        }
    }
    
    private void handleDeleteTransaction(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, 401, "User not logged in");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        Long transactionId = Long.parseLong(pathInfo.substring(1)); // Remove leading "/"
        
        try {
            ExpenseTransaction transaction = new ExpenseTransaction();
            transaction.setTransactionId(transactionId);
            
            Boolean deleted = transactionService.deleteExpenseTransaction(transaction);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", deleted);
            responseJson.addProperty("message", deleted ? "Transaction deleted successfully" : "Delete failed");
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Failed to delete transaction: " + e.getMessage());
        }
    }
    
    private JsonObject parseRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return JsonParser.parseString(sb.toString()).getAsJsonObject();
    }
    
    private void setJsonResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }
    
    private void sendSuccessResponse(HttpServletResponse response, JsonObject data) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(data));
        out.flush();
    }
    
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        JsonObject errorJson = new JsonObject();
        errorJson.addProperty("success", false);
        errorJson.addProperty("error", message);
        
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(errorJson));
        out.flush();
    }
}