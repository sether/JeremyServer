/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import WebServiceJeremy.ApiHandler.GENERATE_MODE;

public class RegisterServlet extends HttpServlet {
   private String firstName;
   private String lastName;
   private String email;
   private String creditCard;
   private String password;
   private String reEnterPassword;
   public String content;
   private String[] keys = new String[2];
   
   
    // Processes requests for both HTTP <code>GET</code> and <code>POST</code>.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        boolean incomplete = false;
        boolean register = false;
        boolean emailUsed = false;
        HttpSession session = request.getSession();
    	content = "";
    	String status = (String) session.getAttribute("status");
    	firstName = request.getParameter("firstName");
    	lastName = request.getParameter("lastName");
    	email = request.getParameter("email");
    	password = request.getParameter("password");
    	reEnterPassword = request.getParameter("reEnterPassword");
    	creditCard = request.getParameter("creditCard");
    	
    	
    	if (!"Registered".equals(status)) {  // Assume new member 
            register = true;
            try {
            	emailUsed = SQLConnectionUpdate.openConnectionValidationUser(email);
			} catch (Exception e) {
				e.printStackTrace();
			}
    		if(password != null){
	    		if(!password.equals(reEnterPassword)){
	        		incomplete = true;
	        		register = false;
	        	}else if(emailUsed){
	        		incomplete = true;
	        		register = false;
	        	}else{
	        		register = false;
	        	}
            }
        }
    	
        response.setContentType("text/html;charset=UTF-8");
        try {
            content += "<html><head>" +
            "<title>Register User (RegisterServlet)</title>" +
            "</head><body><font face=sans-serif>";
            if (register) {
                registerForm();         
            }
            else if (incomplete) {
            	if(emailUsed){
                	content += "<h1>Email is already registered</h1>";
            	}else if(!password.equals(reEnterPassword)){
            		content += "<h1>Make sure passwords match</h1>";
            	}
                incompleteForm();
            }
            else {
            	try {
					keys = ApiHandler.generateClientKeys(GENERATE_MODE.NEW_USER, email);
				} catch (Exception e) {
					e.printStackTrace();
				}
            	String exeStatement = "INSERT INTO User VALUES ('" + email + "', '" + firstName + "', '" + lastName + "', '" + password + "', '" + creditCard + "', '" + keys[0] + "', '" + keys[1] + "')";
            	try {
            		SQLConnectionUpdate.openConnectionUpdate(exeStatement);
            		welcomeForm();
                	session.setAttribute("status", "Registered");
                    session.setAttribute("user", email);
            	} catch (Exception e) {
            		e.printStackTrace();
            		connectionError();
            	}
            }            
            content += "</body></html>";
        }finally{
        	RequestDispatcher view = request.getRequestDispatcher("template.jsp"); // use this view
        	request.setAttribute("content", content); //set this value - the page will display the value in the content section
        	view.forward(request, response);
        }
    }
       
    private void registerForm() {
        content += "<h2>New Member Registration</h2>" +
        "<form action=register method=POST>" +
        "First Name: <input type=text name=firstName required></br>" +
        "Last Name: <input type=text name=lastName required></br>" +
        "Email: <input type='email' name=email required></br>" +
        "Password: <input type='password' name=password required></br>" +
        "Re-Enter Password: <input type='password' name=reEnterPassword required></br>" +
        "Credit Card: <input type=text name=creditCard required></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=login>Login Page</a>";
    }	
    
    private void incompleteForm() {
    	content += "<h2>New Member Registration (please fill all fields)</h2>" +
        "<form action=register method=POST>" +
        "First Name: <input type=text name=firstName value=" + firstName + " required></br>" +
        "Last Name: <input type=text name=lastName value=" + lastName + " required></br>" +
        "Email: <input type='email' name=email value=" + email + " required></br>" +
        "Password: <input type='password' name=password required></br>" +
        "Re-Enter Password: <input type='password' name=reEnterPassword required></br>" +
        "Credit Card: <input type=text name=creditCard value=" + creditCard + " required></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=login>Login Page</a>";
    }	
    
    private void welcomeForm() {
        content += "<h2>Welcome to JeremyAPI Web Service - Your Details:</h2>" +
        "First Name: " + firstName + "</br>" +
        "Last Login: " + lastName + "</br>" +
        "Email: " + email + "</br></br>" +
        "<a href=converter>Converter Page</a>";
    }	

    private void connectionError() {
        content += "<h2>Error connecting to the MySQL Database, please try again when your database is operational</h2>" +
        "<a href=index.jsp>Home Page</a>";
    }	

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
    * Returns a short description of the servlet
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}