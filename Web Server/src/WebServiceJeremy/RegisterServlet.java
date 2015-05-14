/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.*;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.http.*;

//Login, Register, Password API
//Review googles API

public class RegisterServlet extends HttpServlet {
   private String firstName;
   private String lastName;
   private String email;
   private String creditCard;
   private String password;
   private String reEnterPassword;
   public String content;
   private String apiKey = "85";
   
   
    // Processes requests for both HTTP <code>GET</code> and <code>POST</code>.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        boolean incomplete = false;
        boolean register = false;
        HttpSession session = request.getSession();
    	content = "";
    	String status = (String) session.getAttribute("status");
    	firstName = request.getParameter("firstName");
    	lastName = request.getParameter("lastName");
    	email = request.getParameter("email");      
              
    	if (!"Registered".equals(status)) {  // Assume new member 
            // Retrieve field values from web form:
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
            	String exeStatement = "INSERT INTO User VALUES ('" + email + "', '" + firstName + "', '" + lastName + "', '" + password + "', '" + creditCard + "', '" + apiKey +"')";
            	try {
            		SQLConnectionUpdate.openConnection(exeStatement);
            	} catch (SQLException e) {
            		e.printStackTrace();
            	}
            	welcomeForm();
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
        "Password: <input type=text name=password value=" + password + "></br>" +
        "Re-Enter Password: <input type=text name=password value=" + reEnterPassword + "></br>" +
        "Credit Card: <input type=text name=creditCard value=" + creditCard + "></br>" +
        "<input type=submit>" +
        "</form></br>" +
        "<a href=login>Login Page</a>";
    }	
    
    private void incompleteForm() {
    	content += "<h2>New Member Registration (please fill all fields)</h2>" +
        "<form action=register>" +
        "First Name: <input type=text name=firstName value=" + firstName + "></br>" +
        "Last Name: <input type=text name=lastName value=" + lastName + "></br>" +
        "Email: <input type=text name=email value=" + email + "></br>" +
        "Password: <input type=text name=password value=" + password + "></br>" +
        "Re-Enter Password: <input type=text name=password value=" + reEnterPassword + "></br>" +
        "Credit Card: <input type=text name=creditCard value=" + creditCard + "></br>" +
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