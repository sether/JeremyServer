/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

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
        HttpSession session = request.getSession();
    	content = "";
    	String status = (String) session.getAttribute("status");
    	
        if (!"Registered".equals(status)) {  // Assume new login 
            // Retrieve field values from web form:
            email = request.getParameter("email");
            password = request.getParameter("password");
            if (email == null)
                 login = true;
            else if (email.isEmpty() || password.isEmpty())
                incomplete = true;
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
        "<form action=login>" +
        "Email: <input type=text name=email></br>" +
        "Password: <input type=text name=password value=" + "" + "></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=register>Register</a>";
    }	
    
    private void incompleteForm() {
    	content += "<h2>Login (please fill all fields)</h2>" +
        "<form action=login>" +
        "Email: <input type=text name=email value=" + email + "></br>" +
        "Password: <input type=text name=password value=" + "" + "></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=register>Register</a>";
    }	

    private void displayMember() {
    	content += "<h2>Login</h2>" +
    	        "<form action=login>" +
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
