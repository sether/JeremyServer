package WebServiceJeremy;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.ws.rs.Consumes;
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
	
	//TODO: how to handle API settings such as use first line as heading
	//maybe just have multiple methods?
	//headers
	//TODO: What to log and how?
	//To a database? To a file? prolly database.
	
	@POST
	@Path("/ConvertTOXML")
	@Consumes(MediaType.TEXT_PLAIN)
	public Response convertToXML(String incomingCSV) {
		
		FileController fc = new FileController();
		String result = "";// = "Data post: ";
		
		try {
			File inputFile = File.createTempFile("csvInput-", ".tmp");
			FileUtility.writeFile(inputFile, incomingCSV);
			
			fc.readFile(inputFile);
			
			File outputFile = File.createTempFile("jsonOutput-", ".tmp");
			fc.outputData(outputFile, OutputType.XML);
			
			
			result += new Scanner(outputFile).useDelimiter("\\Z").next();
			inputFile.delete();
			outputFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(200).entity("failed creation: " + e).build();
		}
		
		return Response.status(201).entity(result).build();
	}
	
	@POST
	@Path("/ConvertTOJSON")
	@Consumes(MediaType.TEXT_PLAIN)
	public String convertToJSON(String incomingCSV) {
		
		FileController fc = new FileController();
		String result = "Data post: ";
		
		try {
			File inputFile = File.createTempFile("csvInput-", ".tmp");
			FileUtility.writeFile(inputFile, incomingCSV);
			
			fc.readFile(inputFile);
			
			File outputFile = File.createTempFile("jsonOutput-", ".tmp");
			fc.outputData(outputFile, OutputType.JSON);
			
			
			result += new Scanner(outputFile).useDelimiter("\\Z").next();
			inputFile.delete();
			outputFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
