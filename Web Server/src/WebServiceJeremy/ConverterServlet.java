package WebServiceJeremy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.jeremy.FileController;
import com.jeremy.FileController.OutputType;
import com.jeremy.FileUtility;
import com.jeremy.SQLHandler.SQLType;

//TODO requires validation. Trouble spots include restricted words for sql and the id column ranges and booleans

public class ConverterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpSession session;
	
    /***
     * The main method of this class. It contains the logic that determines what should be displayed to the
     * client.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    	session = request.getSession();
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
            	String fileName = (String) request.getAttribute("uploadedFileName");
            	
            	//create temp file and write received string to it
            	content = "<h1>Converted Files: " + fileName + "</h1>";
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
            		content += convertCSV(fc, OutputType.XML, fileName, csvString.length());
            	}
            	
            	//xsd
            	if(request.getAttribute("schemaCheck") != null){
            		content += convertCSV(fc, OutputType.XML_SCHEMA, fileName, csvString.length());
            	}
            	
            	//json
            	if(request.getAttribute("jsonCheck") != null){
            		content += convertCSV(fc, OutputType.JSON, fileName, csvString.length());
            	}
            	
            	if(request.getAttribute("sqlCheck") != null){
            		String dbName = (String) request.getAttribute("dbName");
            		int idCol = (Integer.valueOf((String) request.getAttribute("dbIdCol")));
            		boolean genIdCol = (request.getAttribute("dbIdCol") == null);
            		
            		//mysql
                	if(request.getAttribute("mySQL") != null){
                		content += convertCSVDB(fc, fileName, csvString.length(), SQLType.MYSQL, idCol, genIdCol, dbName);
                	}
                	
                	//mssql
                	if(request.getAttribute("msSQL") != null){
                		content += convertCSVDB(fc, fileName, csvString.length(), SQLType.SQLSERVER, idCol, genIdCol, dbName);
                	}
                	
                	//postgre
                	if(request.getAttribute("postSQL") != null){
                		content += convertCSVDB(fc, fileName, csvString.length(), SQLType.POSTGRESQL, idCol, genIdCol, dbName);
                	}
            	}
            	
            }
        }
        
        //handle response
        response.setContentType("text/html;charset=UTF-8");
        RequestDispatcher view = request.getRequestDispatcher("template.jsp"); // use this view
        request.setAttribute("content", content); //set this value - the page will display the value in the content section
        view.forward(request, response);
    }


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
    * Handles the HTTP <code>POST</code> method. Has a loop to extract files from requests.
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
    
    /***
     * A string constructing method to display the csv import form. This form uses a javascript file called
     * jeremy.js. This file contains functions for showing and hiding divs in response to checkboxes.
     * @return
     */
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
    
    /***
     * A method to handle hyperlink requests for files.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void processFileRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{    	
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
    
    /***
     * Outputs and logs non SQL file conversions
     * @param fc
     * @param ot
     * @param fName
     * @param fLength
     * @return
     * @throws IOException
     */
    private String convertCSV(FileController fc, OutputType ot, String fName, int fLength) throws IOException{
    	String suff = "";
    	String pref = "";
    	String hlinkText = "";
    	
    	if(ot == OutputType.XML){
    		suff = ".xml";
    		pref = "xml_";
    		hlinkText = "XML File";
    	} else if(ot == OutputType.XML_SCHEMA){
    		suff = ".xsd";
    		pref = "xsd_";
    		hlinkText = "XSD File";
    	} else if(ot == OutputType.JSON){
    		suff = ".json";
    		pref = "json_";
    		hlinkText = "JSON File";
    	}
    	
    	File file = File.createTempFile(pref, suff);
		file.deleteOnExit();
		fc.outputData(file, ot);
		
		//log conversion
		logToDataBase((String) session.getAttribute("user"), ot.toString(), fName, fLength);
		
		//return link
		return "<a href='converter?file=" + file.getName() + "'>" + hlinkText + "</a><br>";
    }
    
    /***
     * Outputs and logs SQL file conversions
     * @param fc
     * @param fName
     * @param fLength
     * @param stype
     * @param idCol
     * @param genIdCol
     * @param dbName
     * @return
     * @throws IOException
     */
    private String convertCSVDB(FileController fc, String fName, int fLength, SQLType stype, int idCol, boolean genIdCol, String dbName) throws IOException{
    	String suff = ".sql";
    	String pref = "";
    	String hlinkText = "";
    	
    	if(stype == SQLType.MYSQL){
    		pref = "mySQL_";
    		hlinkText = "MySQL File";
    	} else if (stype == SQLType.SQLSERVER){
    		pref = "msSQL_";
    		 hlinkText = "Microsoft SQL File";
    	} else if (stype == SQLType.POSTGRESQL){
    		pref = "pgSQL_";
    		hlinkText = "Postgre SQL File";
    	}
		
		File file = File.createTempFile(pref, suff);
		file.deleteOnExit();
		fc.outputToSQLFile(file, (String) dbName, stype, genIdCol, idCol);
		
		//log conversion
		logToDataBase((String) session.getAttribute("user"), stype.toString(), fName, fLength);
		
		//return link
		return "<a href='converter?file=" + file.getName() + "'>" + hlinkText + "</a><br>";
    }
    
    /***
     * A method for logging conversion to the database
     * @param email
     * @param conversionType
     * @param fileName
     * @param fileCharacters
     * @return
     */
    private Response logToDataBase(String email, String conversionType, String fileName, int fileCharacters) {
		String statement = "";
		String restEnum = "HTML";
		
		String description = "The Jeremy API was used to convert a csv file to a " + conversionType + " file via the HTML interface.";
		
		statement = "INSERT INTO APIEvents (email, publicApiKey, fileName, size, methodType, description) VALUES('" + email + "', '" + "null" + "', '" + fileName + ".csv', " + fileCharacters +", '"+ restEnum + "', '" + description + "');";
		
		try {
			SQLConnectionUpdate.openConnectionUpdate(statement);
		} catch (Exception e) {
			System.out.println("Error with statement: " +  statement + "\nError: " + e);
			return Response.status(500).entity("Could not link api key with account").build();
		}
		return null;
	}
}
