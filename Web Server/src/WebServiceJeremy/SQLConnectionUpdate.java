package WebServiceJeremy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnectionUpdate {
	   private static Connection connection = null;
	   private static Statement statement = null;
	   private static String connectionURL = "jdbc:mysql://localhost:3306/JeremyAPIDatabase";
	
	public static boolean openConnectionValidationUser(String emailTest) throws Exception{
		boolean usedEmail = false;
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()){
				String email = rs.getString("email");
				if(email.equalsIgnoreCase(emailTest)){
					usedEmail = true;
				}
			}
			statement.close();
	    }catch (Exception e){
	    	throw(e);
	    }
		return usedEmail;
	}
	
	public static void openConnectionUpdate(String executeStatement) throws Exception{
		   try {
			   Class.forName("com.mysql.jdbc.Driver").newInstance();
			   connection = DriverManager.getConnection(connectionURL, "root", "");
			   statement = connection.createStatement();
			   statement.executeUpdate(executeStatement);
			   statement.close();
		   } catch (SQLException e) {
			   throw(e);
		   } catch (InstantiationException e) {
			   throw(e);
		   } catch (IllegalAccessException e) {
			   throw(e);
		   } catch (ClassNotFoundException e) {
			   throw(e);
		   } finally {
			   try {
				   if (connection != null)
					   connection.close();
			   } catch (SQLException se) {
				   throw(se);
			   }
		   }	
	   }
}
