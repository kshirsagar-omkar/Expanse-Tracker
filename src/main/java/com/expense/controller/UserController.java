package com.expense.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.expense.entities.User;
import com.expense.service.UserService;
import com.expense.factory.UserServiceFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/user/*")
public class UserController extends HttpServlet {
    
    private UserService userService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        userService = UserServiceFactory.getUserService();
        gson = new Gson();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        setJsonResponse(response);
        String pathInfo = request.getPathInfo();
        
        try {
            if ("/register".equals(pathInfo)) {
                handleRegister(request, response);
            } else if ("/login".equals(pathInfo)) {
                handleLogin(request, response);
            } else if ("/update-password".equals(pathInfo)) {
                handleUpdatePassword(request, response);
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
            if ("/profile".equals(pathInfo)) {
                handleUpdateProfile(request, response);
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
            if ("/delete".equals(pathInfo)) {
                handleDeleteUser(request, response);
            } else {
                sendErrorResponse(response, 404, "Endpoint not found");
            }
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Internal server error: " + e.getMessage());
        }
    }
    
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        JsonObject requestJson = parseRequestBody(request);
        
        if (!requestJson.has("username") || !requestJson.has("email") || !requestJson.has("password")) {
            sendErrorResponse(response, 400, "Missing required fields");
            return;
        }
        
        User user = new User();
        user.setUsername(requestJson.get("username").getAsString());
        user.setEmail(requestJson.get("email").getAsString());
        user.setPasswordHash(requestJson.get("password").getAsString()); // In real app, hash this
        
        try {
            Long userId = userService.saveUser(user);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", true);
            responseJson.addProperty("userId", userId);
            responseJson.addProperty("message", "User registered successfully");
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 400, "Registration failed: " + e.getMessage());
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        JsonObject requestJson = parseRequestBody(request);
        
        if (!requestJson.has("usernameOrEmail") || !requestJson.has("password")) {
            sendErrorResponse(response, 400, "Missing username/email or password");
            return;
        }
        
        User searchUser = new User();
        String usernameOrEmail = requestJson.get("usernameOrEmail").getAsString();
        String password = requestJson.get("password").getAsString();
        
        // Set both username and email to search by either
        if (usernameOrEmail.contains("@")) {
            searchUser.setEmail(usernameOrEmail);
        } else {
            searchUser.setUsername(usernameOrEmail);
        }
        
        try {
            User foundUser = userService.findUserByUsernameOrEmail(searchUser);
            
            if (foundUser != null && foundUser.getPasswordHash().equals(password)) {
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("userId", foundUser.getUserId());
                session.setAttribute("username", foundUser.getUsername());
                
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("success", true);
                responseJson.addProperty("userId", foundUser.getUserId());
                responseJson.addProperty("username", foundUser.getUsername());
                responseJson.addProperty("email", foundUser.getEmail());
                responseJson.addProperty("message", "Login successful");
                
                sendSuccessResponse(response, responseJson);
            } else {
                sendErrorResponse(response, 401, "Invalid credentials");
            }
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Login failed: " + e.getMessage());
        }
    }
    
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, 401, "User not logged in");
            return;
        }
        
        JsonObject requestJson = parseRequestBody(request);
        Long userId = (Long) session.getAttribute("userId");
        
        try {
            User existingUser = userService.findUserById(userId.intValue());
            if (existingUser == null) {
                sendErrorResponse(response, 404, "User not found");
                return;
            }
            
            if (requestJson.has("username")) {
                existingUser.setUsername(requestJson.get("username").getAsString());
            }
            if (requestJson.has("email")) {
                existingUser.setEmail(requestJson.get("email").getAsString());
            }
            
            Boolean updated = userService.updateUser(existingUser);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", updated);
            responseJson.addProperty("message", updated ? "Profile updated successfully" : "Update failed");
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Update failed: " + e.getMessage());
        }
    }
    
    private void handleUpdatePassword(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        JsonObject requestJson = parseRequestBody(request);
        
        if (!requestJson.has("email") || !requestJson.has("newPassword")) {
            sendErrorResponse(response, 400, "Missing email or new password");
            return;
        }
        
        String email = requestJson.get("email").getAsString();
        String newPassword = requestJson.get("newPassword").getAsString();
        
        try {
            Boolean updated = userService.updatePassword(email, newPassword);
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", updated);
            responseJson.addProperty("message", updated ? "Password updated successfully" : "Password update failed");
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Password update failed: " + e.getMessage());
        }
    }
    
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            sendErrorResponse(response, 401, "User not logged in");
            return;
        }
        
        Long userId = (Long) session.getAttribute("userId");
        
        try {
            User userToDelete = userService.findUserById(userId.intValue());
            if (userToDelete == null) {
                sendErrorResponse(response, 404, "User not found");
                return;
            }
            
            Boolean deleted = userService.deleteUser(userToDelete);
            
            if (deleted) {
                session.invalidate(); // Invalidate session after deletion
            }
            
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("success", deleted);
            responseJson.addProperty("message", deleted ? "Account deleted successfully" : "Account deletion failed");
            
            sendSuccessResponse(response, responseJson);
            
        } catch (Exception e) {
            sendErrorResponse(response, 500, "Account deletion failed: " + e.getMessage());
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