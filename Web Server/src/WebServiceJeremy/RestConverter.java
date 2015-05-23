package WebServiceJeremy;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.jeremy.FileController;
import com.jeremy.FileUtility;
import com.jeremy.FileController.OutputType;

@Path("/restconverter")
public class RestConverter {
	
	
	@POST
	@Consumes("application/x-www-form-urlencoded")
	@Path("/xml")
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
	@Path("/json")
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
		
		//get the users email
		String userEmail = AuthorizeUser(encodedEmail, publicAPIKey);
		
		//check its not empty
		if (userEmail == null || userEmail.equalsIgnoreCase("")) {
			return Response.status(403).entity("Hashed email not correct. Please use Base64 Encoding.").build();
		}
		
		//check if user is valid and Authorize them
		if (publicAPIKey == null || publicAPIKey.equals("")) {
			//return not a user
			return Response.status(403).entity("Not a valid API key").build();
		}
		if (encodedEmail == null || encodedEmail.equals("")) {
			return Response.status(403).entity("Encoded email is empty").build();
		}
		
		//make sure there is a table name
		if (tableName == null || tableName.equals("")) {
			//if not, error
			return Response.status(406).entity("Please specify a table name").build();
		}
		
		//make sure the csv data is not empty or null
		if (csvData == null || csvData.equals("")) {
			//if it is, error
			return Response.status(406).entity("Please supply CSV data").build();
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
			//create temporary file
			File inputFile = File.createTempFile("csvInput-", ".tmp");
			
			//write csv data to file
			FileUtility.writeFile(inputFile, csvData);
			
			//use the api to read in that file
			fc.readFile(inputFile);
			
			//already checked for null or empty. add the table name
			fc.setTableName(tableName);

			//set up file name depending on output type
			String fileNamePrefix = "tempFile";
			if (fileOutputType == OutputType.JSON) {
				fileNamePrefix = "jsonOutput-";
			} else if (fileOutputType == OutputType.XML) {
				fileNamePrefix = "xmlOutput-";
			}
			
			//create a new temporary file
			File outputFile = File.createTempFile(fileNamePrefix, ".tmp");
			
			//use the api to output the csv data to the type specified to the new file
			fc.outputData(outputFile, fileOutputType);
			
			//read in the file data and make it the result
			result = new Scanner(outputFile).useDelimiter("\\Z").next();
			
			//delete the temporary files so as to use less space
			inputFile.delete();
			outputFile.delete();
		} catch (IOException e) {
			//return error if failed
			return Response.status(500).entity("Failed creation: Internal Error").build();
		}
		
		//log the usage to a database for a user
		Response dataBaseResponse = logToDataBase(userEmail, publicAPIKey, fileOutputType, tableName, csvData.length());
		
		//if there is a response from that database
		if (dataBaseResponse != null) {
			//return errored response and do not continue
			return dataBaseResponse;
		}
		
		//return the created data with a successful creation code
	    return Response.status(201).entity(result).build();
	}
	
	private String AuthorizeUser(String encodedEmail, String publicAPIKey) {	
		try {
			String result = SQLConnectionUpdate.openConnectionGetUserEmail(publicAPIKey);
			if (result != null) {
				if (ApiHandler.compareHashes(encodedEmail, publicAPIKey)) {
					return result;
				} else {
					return null;
				}
				
 
			} else {
				return null;
			}
			
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	private Response logToDataBase(String email, String publicAPIKey, OutputType conversionType, String fileName, int fileCharacters) {
		String statement = "";
		String restEnum = "REST";
		
		String description = "The Jeremy API was used to convert a csv file to a " + conversionType + " file via the REST interface.";
		
		statement = "INSERT INTO APIEvents (email, publicApiKey, fileName, size, methodType, description) VALUES('" + email + "', '" + publicAPIKey + "', '" + fileName + ".csv', " + fileCharacters +", '"+ restEnum + "', '" + description + "');";
		
		try {
			SQLConnectionUpdate.openConnectionUpdate(statement);
		} catch (Exception e) {
			//System.out.println("Error with statement: " +  statement + "\nError: " + e);
			return Response.status(500).entity("Could not link Public API key with account").build();
		}
		return null;
	}
	
}
