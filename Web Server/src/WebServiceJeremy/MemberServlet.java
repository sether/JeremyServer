/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.*;
import javax.servlet.http.*;


//Login, Register, Password API
//Review googles API

public class MemberServlet extends HttpServlet {
	private String firstName;
	private String lastName;
	private String email;
	private String creditCard;
	private String password;
	private String publicApiKey;
	private String privateApiKey;
	public String content;
	
    // Processes requests for both HTTP <code>GET</code> and <code>POST</code>.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	HttpSession session = request.getSession();
    	String user = (String) session.getAttribute("user");
    	System.out.println(user);
    	content = "";
    	try{
    		String connectionURL = "jdbc:mysql://localhost:3306/JeremyAPIDatabase";
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection connection = DriverManager.getConnection(connectionURL, "root", "");
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()){
				String emailRS = rs.getString("email");
				if(emailRS.equalsIgnoreCase(user)){
					email = emailRS;
					password = rs.getString("password");	
					firstName = rs.getString("firstName");
					lastName = rs.getString("lastName");
					creditCard = rs.getString("creditCard");
					publicApiKey = rs.getString("publicApiKey");
					privateApiKey = rs.getString("privateApiKey");
				}
			}
			statement.close();
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
    	response.setContentType("text/html;charset=UTF-8");
        try {
            content += "<html><head>" +
            "<title>Member (MemberServlet)</title>" +
            "</head><body><font face=sans-serif>";
            baseMemberForm();
            content += "</body></html>";
        }finally{
	    	RequestDispatcher view = request.getRequestDispatcher("template.jsp"); // use this view
	    	request.setAttribute("content", content); //set this value - the page will display the value in the content section
	    	view.forward(request, response);
	    }
	}
       
    private void baseMemberForm() {
        content += "<h2>Member</h2>" +
        "<form action= memberView>" +
        "Email: " + email + "</br>" +
        "First Name: " + firstName + "</br>" +
        "Last Name: " + lastName + "</br>" +
        "Password: " + password + "</br>" +
        "Credit Card: " + creditCard + "</br>" +
        "Public API Key: " + publicApiKey + "</br>" +
        "Private API Key: " + privateApiKey + "</br>" +
        "<input type=submit values= generateAPIKey>" +
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
