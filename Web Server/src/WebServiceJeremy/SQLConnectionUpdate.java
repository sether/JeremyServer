package WebServiceJeremy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnectionUpdate {

	public static void openConnection(String executeStatement) throws SQLException{
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
			   e.printStackTrace();
		   } catch (InstantiationException e) {
			e.printStackTrace();
		   } catch (IllegalAccessException e) {
			   e.printStackTrace();
		   } catch (ClassNotFoundException e) {
			   e.printStackTrace();
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
