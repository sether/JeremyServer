/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;


//Login, Register, Password API
//Review googles API

public class LoginServlet extends HttpServlet {
   private String email;
   private String password;
   public String content;
   
    // Processes requests for both HTTP <code>GET</code> and <code>POST</code>.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	boolean login = false;
    	boolean incomplete = false;
    	boolean validUser = false;
        HttpSession session = request.getSession();
    	content = "";
    	String status = (String) session.getAttribute("status");

        email = request.getParameter("email");
        password = request.getParameter("password");
    	if (!"Registered".equals(status)) {  // Assume new login 
    		try {
    			validUser = SQLConnectionUpdate.openConnectionValidationLogin(email, password);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		login = true;
    		if(password != null){
                incomplete = true;
            	login = false;
	        	if(validUser){
	            	incomplete = false;
	                login = false;
	        	}
	    	}
   		}
     
        response.setContentType("text/html;charset=UTF-8");
        try {
            content += "<html><head>" +
            "<title>Login (LoginServlet)</title>" +
            "</head><body><font face=sans-serif>";
            if (login) {
                loginForm();         
            }else if (incomplete) {
                incompleteForm();
            }else {
            	displayMember();
	            session.setAttribute("status", "Registered");
	            session.setAttribute("user", email);
	        }
            content += "</body></html>";
        }finally{
	    	RequestDispatcher view = request.getRequestDispatcher("template.jsp"); // use this view
	    	request.setAttribute("content", content); //set this value - the page will display the value in the content section
	    	view.forward(request, response);
	    }
	}
       
    private void loginForm() {
        content += "<h2>Login</h2>" +
        "<form action=login method=POST>" +
        "Email: <input type='email' name=email required></br>" +
        "Password: <input type='password' name=password required></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=register>Register</a>";
    }	
    
    private void incompleteForm() {
    	content += "<h1>Invalid Login</h1>" +
    	"<h2>Login</h2>" +
        "<form action=login method=POST>" +
        "Email: <input type='email' name=email value=" + email + " required></br>" +
        "Password: <input type='password' name=password required></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=register>Register</a>";
    }	
    /**
     * On successful creation of the user, generate inital API keys for the user.
     */
    private void displayMember() {
    	content += "<h2>Login</h2>" +
    	        "<form action=login method=POST>" +
    	        "Logged In" +
    	        "</form></br>";
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
