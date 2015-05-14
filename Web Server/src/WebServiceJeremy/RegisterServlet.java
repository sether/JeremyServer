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
   private String password;
   private String reEnterPassword;
   public String content;
   private String valuesMarker = "";
   
   public void openConnection() throws SQLException{
	   Connection connection = null;
	   Statement statement = null;
	   String createUser = "INSERT INTO User VALUES (" + valuesMarker + ")";
	   String connectionURL = "jdbc:mysql://localhost:3306/JeremyAPIDatabase";
	   try {
		   Class.forName("com.mysql.jdbc.Driver").newInstance();
		   connection = DriverManager.getConnection(connectionURL, "root", "");
		   statement = connection.createStatement();
		   System.out.println(createUser);
		   statement.executeUpdate(createUser);
		   statement.close();
	   } catch (SQLException se) {
		   throw(se);
	   } catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
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
            	String apiKey = "85";
            	valuesMarker = "'" + email + "', '" + firstName + "', '" + lastName + "', '" + password + "', '" + creditCard + "', '" + apiKey +"'";
            	try {
            		openConnection();
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
    * Returns a short description of the servlet.
    */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}