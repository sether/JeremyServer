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
	
	public static boolean openConnectionValidationLogin(String emailTest, String passwordTest) throws Exception{
		boolean validUser = false;
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()){
				String email = rs.getString("email");
				String password = rs.getString("password");
				if(email.equalsIgnoreCase(emailTest) && password.equals(passwordTest)){
					validUser = true;
				}
			}
			statement.close();
	    }catch (Exception e){
	    	throw(e);
	    }
		return validUser;
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
	
	public static String openConnectionGetPrivateKey(String user) throws Exception{
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()){
				String userEmail = rs.getString("email");
				String privateKey = rs.getString("privateApiKey");
				if(userEmail.equalsIgnoreCase(user)){
					return privateKey;
				}
			}
			statement.close();
	    }catch (Exception e){
	    	throw(e);
	    }
		return null;
	}
	
	public static String openConnectionGetPublicKey(String user) throws Exception{
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()){
				String userEmail = rs.getString("email");
				String publicKey = rs.getString("publicApiKey");
				if(userEmail.equalsIgnoreCase(user)){
					return publicKey;
				}
			}
			statement.close();
	    }catch (Exception e){
	    	throw(e);
	    }
		return null;
	}
	
	public static String[] openConnectionGetPrivateKeyApi(String publicKey) throws Exception{
		try{
			// index[0] = Users email, index[1] = Private Api Key 
			String[] privKeyEmail = new String[2];
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()){
				String dbPublicKey = rs.getString("publicApiKey");
				if(dbPublicKey.equalsIgnoreCase(publicKey)){
					privKeyEmail[0] = rs.getString("email");
					privKeyEmail[1] = rs.getString("privateApiKey");
					
					return privKeyEmail;
				}
			}
			statement.close();
	    }catch (Exception e){
	    	throw(e);
	    }
		return null;
	}
}
