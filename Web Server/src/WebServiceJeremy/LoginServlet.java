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
   private String lastLoginDate;
   public String content;
   
    // Processes requests for both HTTP <code>GET</code> and <code>POST</code>.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	
        boolean incomplete = false;
        boolean login = false;
        boolean noCookiesSet = false;       // Flag to indicate presence of cookies
        Cookie firstCookie = null;          // First name cookie
        Cookie lastLoginCookie = null;
        content = "";
        firstCookie = CookieJar.getCookie(request, "email");
                
        if (firstCookie == null) {  // Assume new login 
            noCookiesSet = true;
            // Retrieve field values from web form:
            email = request.getParameter("email");      
            if (email == null)
                 login = true;
            else if (email.isEmpty() || password.isEmpty())
                incomplete = true;
        }
     
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            content += "<html><head>" +
            "<title>Login (LoginServlet)</title>" +
            "</head><body><font face=sans-serif>";
            if (login) {
                loginForm(out);         
            }
            else if (incomplete) {
                incompleteForm(out);
            }
            else {
            	content += "Logged in";
            	//Send or resend the cookie back to the browser:
	            firstCookie.setMaxAge(3600);
	            response.addCookie(firstCookie);
	            
	            // Set the last login date in a cookie; can't have '/' in a cookie
	            SimpleDateFormat myDateFormat = new SimpleDateFormat("d-MMM-yyyy");
	            lastLoginDate = myDateFormat.format(new Date());
	            lastLoginCookie = new Cookie("lastLogin", lastLoginDate);
	            lastLoginCookie.setMaxAge(3600);
	            response.addCookie(lastLoginCookie);
	                
	            // Set the session data
	            HttpSession session = request.getSession();
	            session.setAttribute("status", "Registered");
	            }
            content += "</body></html>";
        }finally{
	    	RequestDispatcher view = request.getRequestDispatcher("template.jsp"); // use this view
	    	request.setAttribute("content", content); //set this value - the page will display the value in the content section
	    	view.forward(request, response);
	    }
	}
       
    private void loginForm(PrintWriter out) {
        content += "<h2>Login</h2>" +
        "<form action=login>" +
        "Email: <input type=text name=email></br>" +
        "Password: <input type=text name=password value=" + "" + "></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=register.jsp>Register</a>";
    }	
    
    private void incompleteForm(PrintWriter out) {
    	content += "<h2>Login (please fill all fields)</h2>" +
        "<form action=lgoin>" +
        "Email: <input type=text name=email value=" + email + "></br>" +
        "Password: <input type=text name=password value=" + "" + "></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=register.jsp>Register</a>";
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
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
