/**File:    RegisterServlet.java
 * Purpose: Register user details; set cookies and session data
 * Listing: 24.NNN
 */
package WebServiceJeremy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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

import com.jeremy.FileController;
import com.jeremy.FileUtility;
import com.jeremy.SQLHandler.SQLType;


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
    	
        
        
        //determine what to display
        if(session == null || status == null || !status.equals("Registered")){ //unregistered
        	content = "<h1> Log in to access this feature";
        } else {
        	// Retrieve field values from web form:
            if(stage == null){ //beginning stage
            	if(request.getParameter("file") != null){ //check if file requested
            		processFileRequest(request, response);
            		return;
            	} else { //otherwise display form
            		content = getUploadForm();
            	}
            } else if(stage.equals("upload")){ //uploaded file stage
            	//create temp file and write received string to it
            	content = "<h1>Converted Files</h1>";
            	content += "<p>Right click the links below and select \"Save As\"</p><br>";
            	File csvFile = File.createTempFile("csvFile", ".csv");
            	csvFile.deleteOnExit();
            	
            	String csvString = IOUtils.toString((InputStream) request.getAttribute("uploadedFile"), "UTF-8");
            	FileUtility.writeFile(csvFile, csvString);
            	
            	//read file into file controller
            	FileController fc = new FileController();
            	fc.setFirstLineUsedAsColumnHeader(request.getAttribute("firstRowNames") != null);
            	fc.setColumnDelimiter((String) request.getAttribute("delimiter"));
            	fc.setDateFormat((String) request.getAttribute("dformat"));
            	fc.readFile(csvFile);
            	fc.setTableName((String) request.getAttribute("tname"));
            	
            	//parse checkboxes
            	//xml
            	if(request.getAttribute("xmlCheck") != null){
            		File xmlFile = File.createTempFile("xml_", ".xml");
            		xmlFile.deleteOnExit();
            		fc.csvToXML(csvFile, xmlFile);
            		content += "<a href='converter?file=" + xmlFile.getName() + "'>XML File</a><br>";
            	}
            	
            	//xsd
            	if(request.getAttribute("schemaCheck") != null){
            		File xsdFile = File.createTempFile("xsd_", ".xsd");
            		xsdFile.deleteOnExit();
            		fc.csvToXMLSCHEMA(csvFile, xsdFile);
            		content += "<a href='converter?file=" + xsdFile.getName() + "'>XSD File</a><br>";
            	}
            	
            	//json
            	if(request.getAttribute("jsonCheck") != null){
            		File jsonFile = File.createTempFile("json_", ".json");
            		jsonFile.deleteOnExit();
            		fc.csvToJSON(csvFile, jsonFile);
            		content += "<a href='converter?file=" + jsonFile.getName() + "'>JSON File</a><br>";
            	}
            	
            	//mysql
            	if(request.getAttribute("mySQL") != null){
            		File mySQLFile = File.createTempFile("mysql_", ".sql");
            		mySQLFile.deleteOnExit();
            		fc.csvToSQLFile(csvFile, mySQLFile, (String) request.getAttribute("dbName"), SQLType.MYSQL, 
            				(request.getAttribute("dbIdCol") == null), (Integer.valueOf((String) request.getAttribute("dbIdCol"))));
            		content += "<a href='converter?file=" + mySQLFile.getName() + "'>MySQL File</a><br>";
            	}
            	
            	//mssql
            	if(request.getAttribute("msSQL") != null){
            		File msSQLFile = File.createTempFile("mssql_", ".sql");
            		msSQLFile.deleteOnExit();
            		fc.csvToSQLFile(csvFile, msSQLFile, (String) request.getAttribute("dbName"), SQLType.SQLSERVER, 
            				(request.getAttribute("dbIdCol") == null), (Integer.valueOf((String) request.getAttribute("dbIdCol"))));
            		content += "<a href='converter?file=" + msSQLFile.getName() + "'>Microsoft SQL File</a><br>";
            	}
            	
            	//postgre
            	if(request.getAttribute("postSQL") != null){
            		File pgSQLFile = File.createTempFile("postgre_", ".sql");
            		pgSQLFile.deleteOnExit();
            		fc.csvToSQLFile(csvFile, pgSQLFile, (String) request.getAttribute("dbName"), SQLType.POSTGRESQL, 
            				(request.getAttribute("dbIdCol") == null), (Integer.valueOf((String) request.getAttribute("dbIdCol"))));
            		content += "<a href='converter?file=" + pgSQLFile.getName() + "'>Postgre SQL File</a><br>";
            	}
            }
        }
        
        //handle response
        response.setContentType("text/html;charset=UTF-8");
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
    	return  "<script src='js/jeremy.js'></script>"
    			+ "<h2>CSV Import Options</h2>"
    			+ "<form action='converter' method='post' enctype='multipart/form-data'>"
    			+ "Table Name: <input type='text' name='tname' value='table_name'><br>"
    			+ "Delimiter: <input type='text' name='delimiter' value=','><br>"
    			+ "Date Format: <input type='text' name='dformat' value='dd/MM/yyyy'><br>"
    			+ "CSV File:<br><input type='file' id='input' name='file'><br><br>"
                + "<input type='hidden' name='stage' value='upload'>"
    			
                //FORMATTING
                + "<h2>Formatting</h2>"
    			+ "<input type='checkbox' name='firstRowNames'>First row as column names<br><br>"
    			
                //FILE TYPE SELECTION
    			+ "<h2>File Output Selection</h2>"
    			+ "<input type='checkbox' name='xmlCheck'>XML File<br>"
    			+ "<input type='checkbox' name='schemaCheck'>XSD Schema File<br>"
    			+ "<input type='checkbox' name='jsonCheck'>JSON File<br>"
    			+ "<input type='checkbox' name='sqlCheck' onclick='toggleDiv(this, \"sqlOptions\")'>SQL File/s<br><br>"
    			+ "<div id='sqlOptions' style='display:none;'> "
    			
    			//SQL CHECKBOX OPTIONS
    			+ "<h2>SQL Type Selection</h2>"
    			+ "<input type='checkbox' name='msSQL'>Microsoft SQL File<br>"
    			+ "<input type='checkbox' name='mySQL'>MySQL File<br>"
    			+ "<input type='checkbox' name='postSQL'>Postgre SQL File<br><br>"
    			
    			//SQL FILE OPTIONS
    			+ "<h2>SQL Generation Options</h2>"
    			+ "Database Name: <input type='text' name='dbName' value='db_name'><br>"
    			+ "<div id='idColumn' style='display:block;'> "
    			+ "ID Column: <input type='text' name='dbIdCol' value='0'><br>"
    			+ "</div>"
    			+ "<input type='checkbox' name='addIdCol' onclick='toggleDivInv(this, \"idColumn\")'>Add ID Column<br><br>"
    			+ "</div>"
    			
    			//SUBMIT BUTTON
    			+ "<input type='submit' value='Submit'>"
    			+ "</form>";
    }
    
    public void processFileRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{    	
    	File file = new File(System.getProperty("java.io.tmpdir") + request.getParameter("file"));
    	FileInputStream inStream = new FileInputStream(file);
    	
    	// get mime type of file
    	ServletContext context = getServletContext();
        String mimeType = context.getMimeType(file.getPath());
    	
        // modifies response
        response.setContentType(mimeType);
        response.setContentLength((int) file.length());
         
        // set header as download
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", file.getName());
        response.setHeader(headerKey, headerValue);
         
        // create out stream for file
        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        inStream.close();
        outStream.close(); 
    }
}
