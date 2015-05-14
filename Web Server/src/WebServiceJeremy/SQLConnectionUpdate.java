package WebServiceJeremy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnectionUpdate {

	public static void openConnection(String executeStatement) throws Exception{
		   Connection connection = null;
		   Statement statement = null;
		   String connectionURL = "jdbc:mysql://localhost:3306/JeremyAPIDatabase";
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
