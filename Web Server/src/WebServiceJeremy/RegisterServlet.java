/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.*;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import javax.servlet.*;
import javax.servlet.http.*;

import java.sql.Connection;
import java.sql.Statement;


//Login, Register, Password API
//Review googles API

public class RegisterServlet extends HttpServlet {
   private String firstName;
   private String lastName;
   private String email;
   private String creditCard;
   private String lastLoginDate;
   public String content;
   
   public void openConnection() throws SQLException{
	   Connection connection = null;
	   Statement statement = null;
	   String valuesMarker = "";
	   String createUser = "INSERT INTO User(email, firstname, lastname, password, creditCardNumber, apiKey) values (" + valuesMarker + ")";
	   String connectionURL = "jdbc:mysql://localhost:3306/JeremyAPIDatabase";
	   try {
		   connection = DriverManager.getConnection(connectionURL, "root", "");
		   statement = connection.createStatement();
		   statement.executeUpdate(createUser);
		   statement.close();
	   } catch (SQLException se) {
		   throw(se);
	   } finally {
		   try {
			   if (connection != null)
				   connection.close();
		   } catch (SQLException se) {
			   throw(se);
		   }
	   }	
   }
   
    // Processes requests for both HTTP <code>GET</code> and <code>POST</code>.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	
        boolean incomplete = false;
        boolean register = false;
        boolean noCookiesSet = false;       // Flag to indicate presence of cookies
        Cookie firstCookie = null;          // First name cookie
        Cookie lastLoginCookie = null;      // Last login cookie  
        content = "";
        firstCookie = CookieJar.getCookie(request, "firstName");
                
        if (firstCookie == null) {  // Assume new member 
            noCookiesSet = true;
            // Retrieve field values from web form:
            firstName = request.getParameter("firstName");
            lastName = request.getParameter("lastName");
            email = request.getParameter("email");      
            if (firstName == null && lastName == null && email == null)
                 register = true;
            else if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty())
                incomplete = true;
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
                incompleteForm();
            }
            else {                          // The form has been filled out correctly
                if (noCookiesSet) {  
                    /* The new member has just filled out the form;
                       Cookie not yet set - now we will create them */
                     firstCookie = new Cookie("firstName", firstName);
                     welcomeForm();
                     
                }
                else { // Cookies have been set; visitor returning. Retrieve values from cookies
                    firstName = CookieJar.getCookieValue(request, "firstName");
                    lastLoginDate = CookieJar.getCookieValue(request, "lastLogin");      
                    welcomeBackForm();
                }
                
                // Send or resend the cookie back to the browser:
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
       
    private void registerForm() {
        content += "<h2>New Member Registration</h2>" +
        "<form action=register>" +
        "First Name: <input type=text name=firstName></br>" +
        "Last Name: <input type=text name=lastName></br>" +
        "Email: <input type=text name=email></br>" +
        "Credit Card: <input type=text name=creditCard value=" + creditCard + "></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=index.jsp>Home</a>";
    }	
    
    private void incompleteForm() {
    	content += "<h2>New Member Registration (please fill all fields)</h2>" +
        "<form action=register>" +
        "First Name: <input type=text name=firstName value=" + firstName + "></br>" +
        "Last Name: <input type=text name=lastName value=" + lastName + "></br>" +
        "Email: <input type=text name=email value=" + email + "></br>" +
        "Credit Card: <input type=text name=creditCard value=" + creditCard + "></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=index.jsp>Home</a>";
    }	
    	
    private void welcomeBackForm() {
        content += "<h2>Welcome Back to MyStore</h2>" +
        "First Name: " + firstName + "</br>" +
        "Last login: " + lastLoginDate + "</br></br>" +
        "<a href=converter>Converter Page</a>";
    }	
    
    private void welcomeForm() {
        content += "<h2>Welcome to MyStore - Your Details:</h2>" +
        "First Name: " + firstName + "</br>" +
        "Last Login: " + lastName + "</br>" +
        "Email: " + email + "</br></br>" +
        "<a href=converter>Converter Page</a>";
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