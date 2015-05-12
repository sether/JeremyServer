/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
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

public class ConverterServlet extends HttpServlet {
    // Processes requests for both HTTP <code>GET</code> and <code>POST</code>.
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	HttpSession session = request.getSession();
    	String content = "";
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
            	content = getUploadForm();
            } else if(stage.equals("upload")){ //uploaded file stage
            	content = "<h1>" + request.getAttribute("uploadedFileName") + "</h1>";
            	content += IOUtils.toString((InputStream) request.getAttribute("uploadedFile"), "UTF-8");
            }
        }
        
        RequestDispatcher view = request.getRequestDispatcher("template.jsp"); // use this view
        request.setAttribute("content", content); //set this value - the page will display the value in the content section
        request.setAttribute("loginLink", LoginStatusTemp.displayLoginWidget(session));
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
        processRequest(request, response);
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                if (item.isFormField()) {
                	request.setAttribute(item.getFieldName(), item.getString());
                } else {
                	request.setAttribute("uploadedFile", item.getInputStream());
                	request.setAttribute("uploadedFileName", item.getName());
                }
            }
            
            processRequest(request, response);
        } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
        }
    }
    
    public String getUploadForm(){
    	return  "<h2>CSV Upload</h2>"
    			+ "<form action='converter' method='post' enctype='multipart/form-data'>"
    			+ "Table Name: <input type='text' name='tname' value='table_name'><br>"
    			+ "Delimiter: <input type='text' name='delimiter' value=','><br>"
    			+ "Date Format: <input type='text' name='dformat' value='dd/MM/yyyy'><br>"
    			+ "CSV File: <br><input type='file' id='input' name='file'><br><br>"
                + "<input type='hidden' name='stage' value='upload'>"
    			+ "<input type='submit' value='Submit'>"
    			+ "</form>";
    }
}
