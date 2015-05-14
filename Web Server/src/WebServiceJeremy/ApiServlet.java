/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;


//Login, Register, Password API
//Review googles API

public class ApiServlet extends HttpServlet {
    // Processes requests for both HTTP <code>GET</code> and <code>POST</code>.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, GeneralSecurityException {
    	HttpSession session = request.getSession();
    	String content = "";
    	String user = (String) session.getAttribute("user");
    	String status = (String) session.getAttribute("status");
    	
    	//determine what stage of conversion client is in
    	String stage = (String) request.getParameter("stage");
    	if(request.getAttribute("stage") != null){ // override with attribute if existing
    		stage = (String) request.getAttribute("stage");
    	}
    	
        response.setContentType("text/html;charset=UTF-8");
        
        //determine what to display
        if(session == null || status == null || !status.equals("Registered")){ //unregistered
        	content = "<h1> Log in to access this feature";
        } else {
        	// Retrieve field values from web form:
            if(stage == null){ //beginning stage
            	content = apiRequest();
            } else if(stage.equals("generated")){ //uploaded file stage
            	ApiHandler theApi = new ApiHandler();
            	//theApi.generateServerKeys();
				//content = "<h1>" + ApiHandler.generateDevKeys() + "</h1>";
            	try {
					ApiHandler.generateClientKeys(user);
				} catch (Exception e) {
					e.printStackTrace();
				}
				content = "<h2> Hello</h2>";
            }
        }
        
        RequestDispatcher view = request.getRequestDispatcher("template.jsp"); // use this view
        request.setAttribute("content", content); //set this value - the page will display the value in the content section
        view.forward(request, response);
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
			processRequest(request, response);
		} catch (Exception e) {
			// OK
		}
    }
    public String apiRequest(){
    	return  "<h2>Jeremy API Keys</h2>"
			+ "Public Key:"
			+ "<input type='text' value='api'>"
			+ "<form action='api'>"
			+ "Request Developer API Keys: <input type='text' name='generate' value=''><br>"
			+ "<input type='hidden' name='stage' value='generated'>"
			+ "<input type='submit' value='Submit'>"
			+ "</form>";
    }
}
