package WebServiceJeremy;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.jeremy.FileController;
import com.jeremy.FileUtility;
import com.jeremy.FileController.OutputType;

//Sets the path to base URL + /hello
@Path("/restconverter")
public class RestConverter {
	
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/converttoxml")
	public Response convertToXML(@FormParam("csvData") String csvData /*Required*/,
			@FormParam("encodedEmail") String encodedEmail /*Required*/,
			@FormParam("publicAPIKey") String publicAPIKey /*Required*/,
			@FormParam("tableName") String tableName /*Required*/,
			@FormParam("useFirstLineAsHeaders") boolean useFirstLineAsHeaders /*Defaults to false*/,
			@FormParam("dateFormat") String dateFormat /*Defaults to 'dd/MM/yyyy'*/,
			@FormParam("delimiter") String columnDelimiter /*Defaults to ','*/) {
		
		return convertFile(OutputType.XML, csvData, encodedEmail, publicAPIKey, tableName, useFirstLineAsHeaders, dateFormat, columnDelimiter);
	}
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/converttojson")
	public Response convertToJSON(@FormParam("csvData") String csvData /*Required*/,
			@FormParam("encodedEmail") String encodedEmail /*Required*/,
			@FormParam("publicAPIKey") String publicAPIKey /*Required*/,
			@FormParam("tableName") String tableName /*Required*/,
			@FormParam("useFirstLineAsHeaders") boolean useFirstLineAsHeaders /*Defaults to false*/,
			@FormParam("dateFormat") String dateFormat /*Defaults to 'dd/MM/yyyy'*/,
			@FormParam("delimiter") String columnDelimiter /*Defaults to ','*/) {
		
		return convertFile(OutputType.JSON, csvData, encodedEmail, publicAPIKey, tableName, useFirstLineAsHeaders, dateFormat, columnDelimiter);
	}

	//return correct http response codes based on: http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
	
	private Response convertFile(OutputType fileOutputType,
			String csvData,
			String encodedEmail,
			String publicAPIKey,
			String tableName, 
			boolean useFirstLineAsHeaders,
			String dateFormat,
			String columnDelimiter){
		
		//check if user is valid and Authorize them
		if (publicAPIKey == null || publicAPIKey.equals("") || encodedEmail == null || encodedEmail.equals("") || !AuthorizeUser(encodedEmail, publicAPIKey)) {
			//return not a user
			return Response.status(401).entity("Not a valid API key").build();
		}
		
		//make sure there is a table name
		if (tableName == null || tableName.equals("")) {
			//if not, error
			return Response.status(406).entity("Please specify a table name").build();
		}
		
		//make sure the csv data is not empty or null
		if (csvData == null || csvData.equals("")) {
			//if it is, error
			return Response.status(406).entity("Not a valid API key").build();
		}
		
		FileController fc = new FileController();
		
		//Wont be null, defaults to false
		fc.setFirstLineUsedAsColumnHeader(useFirstLineAsHeaders);
		
		//check if there is a column delimiter to use
		if (columnDelimiter != null && !columnDelimiter.equals("")) {
			//if so, set as the column delimiter in the FileController object
			fc.setColumnDelimiter(columnDelimiter);
		}
		
		//check if there is a dateFormat to use
		if (dateFormat != null && !dateFormat.equals("")) {
			//if so, set as the date Format in the FileController object
			fc.setDateFormat(dateFormat);
		}
		
		//set up results
		String result = "";
		try {
			//create file
			File inputFile = File.createTempFile("csvInput-", ".tmp");
			FileUtility.writeFile(inputFile, csvData);
			
			fc.readFile(inputFile);
			
			if (tableName != null && !tableName.equals("")) {
				fc.setTableName(tableName);
			}
			
			File outputFile = File.createTempFile("jsonOutput-", ".tmp");
			fc.outputData(outputFile, fileOutputType);
			
			
			result += new Scanner(outputFile).useDelimiter("\\Z").next();
			inputFile.delete();
			outputFile.delete();
		} catch (IOException e) {
			//e.printStackTrace();
			return Response.status(500).entity("failed creation: IOError").build();
		}
		logToDataBase();
	    return Response.status(201).entity(result).build();
		
	}
	
	private boolean AuthorizeUser(String encodedEmail, String publicAPIKey) {
		// TODO Authorize used to Anthony's API interface
		return true;
	}
	
	private void logToDataBase() {
		/*try {
			SQLConnectionUpdate.openConnection("");
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}
	
}
