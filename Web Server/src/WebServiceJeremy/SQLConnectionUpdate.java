package WebServiceJeremy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Class to connect and interact with the Web Service database
 * 
 * @author Ryan Kavanagh, Anthony Howse, Alex Brown
 * @version 1.0
 */
public class SQLConnectionUpdate {
	private static Connection connection = null;
	private static Statement statement = null;
	private static String connectionURL = "jdbc:mysql://localhost:3306/JeremyAPIDatabase";

	/**
	 * Used to check if the entered supplied email already exists in the database
	 * 
	 * @param emailTest - the supplied email to check against
	 * @return usedEmail - boolean indicating whether the email has been used or not
	 * <br>
	 * <b>USAGE:</b></br>
	 * <pre>
	 * String email = &quot;r@k.com&quot;;
	 * Boolean emailUsed = false;
	 * try {
	 * 	emailUsed = SQLConnectionUpdate.openConnectionValidationUser(email);
	 * } catch (Exception e) {
	 * 	e.printStackTrace();
	 * }
	 * </pre>
	 * @throws Exception
	 */
	public static boolean openConnectionValidationUser(String emailTest)
			throws Exception {
		boolean usedEmail = false;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()) {
				String email = rs.getString("email");
				if (email.equalsIgnoreCase(emailTest)) {
					usedEmail = true;
				}
			}
			statement.close();
		} catch (Exception e) {
			throw (e);
		}
		return usedEmail;
	}

	/**
	 * Used to check if the entered supplied email and password are valid login credentials
	 * 
	 * @param emailTest - the supplied email to check against
	 * @param passwordTest - the supplied password to check against
	 * @return validUser - boolean indicating whether the user's login credentials are valid or not
	 * <br>
	 * <b>USAGE:</b></br>
	 * <pre>
	 * String email = &quot;r@k.com&quot;;
	 * String password = &quot;taste&quot;;
	 * Boolean validUser = false;
	 * try {
	 * 	validUser = SQLConnectionUpdate.openConnectionValidationLogin(email, password);
	 * } catch (Exception e) {
	 * 	e.printStackTrace();
	 * }
	 * </pre>
	 * @throws Exception
	 */
	public static boolean openConnectionValidationLogin(String emailTest,
			String passwordTest) throws Exception {
		boolean validUser = false;
		SaltHash sh = new SaltHash();
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT EMAIL, PASSWORD, SALT FROM User");
			while (rs.next()) {
				String email = rs.getString("email");
				String password = rs.getString("password");
				String salt = rs.getString("salt");
				byte[] bDigest = sh.base64ToByte(password);
				byte[] bSalt = sh.base64ToByte(salt);
				byte[] proposedDigest = sh.getHash(1000, passwordTest, bSalt);
				if (email.equalsIgnoreCase(emailTest)
						&& Arrays.equals(proposedDigest, bDigest)) {
					validUser = true;
				}
			}
			statement.close();
			rs.close();
			connection.close();
		} catch (Exception e) {
			throw (e);
		}
		return validUser;
	}

	/**
	 * Used to execute a statement to interact with the database
	 * 
	 * @param executeStatement - the statement to be executed
	 * 
	 * <br>
	 * <b>USAGE:</b></br>
	 * <pre>
	 * String firstName = &quot;Ryan&quot;;
	 * String lastName = &quot;Kavanagh&quot;;
	 * String email = &quot;r@k.com&quot;;
	 * String creditCard = &quot;123123123&quot;;
	 * String password = &quot;taste&quot;;
	 * String salt = &quot;test&quot;;
	 * String[] keys = new String[2];
	 * keys[0] = &quot;t13&quot;;
	 * keys[1] = &quot;t23&quot;;
	 * String exeStatement = &quot;INSERT INTO User VALUES ('&quot; + email + &quot;', '&quot; + firstName
	 * 		+ &quot;', '&quot; + lastName + &quot;', '&quot; + password + &quot;', '&quot; + salt + &quot;', '&quot;
	 * 		+ creditCard + &quot;', '&quot; + keys[0] + &quot;', '&quot; + keys[1] + &quot;')&quot;;
	 * try {
	 * 	SQLConnectionUpdate.openConnectionUpdate(exeStatement);
	 * } catch (Exception e) {
	 * 	e.printStackTrace();
	 * }
	 * </pre>
	 * @throws Exception
	 */
	public static void openConnectionUpdate(String executeStatement)
			throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			statement.executeUpdate(executeStatement);
			statement.close();
		} catch (SQLException e) {
			throw (e);
		} catch (InstantiationException e) {
			throw (e);
		} catch (IllegalAccessException e) {
			throw (e);
		} catch (ClassNotFoundException e) {
			throw (e);
		} finally {
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				throw (se);
			}
		}
	}

	public static String openConnectionGetPublicKey(String email)
			throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()) {
				String userEmail = rs.getString("email");
				String publicKey = rs.getString("publicApiKey");
				if (userEmail.equalsIgnoreCase(email)) {
					return publicKey;
				}
			}
			statement.close();
		} catch (Exception e) {
			throw (e);
		}
		return null;
	}

	public static String openConnectionGetPrivateKey(String email)
			throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()) {
				String userEmail = rs.getString("email");
				String privateKey = rs.getString("privateApiKey");
				if (userEmail.equalsIgnoreCase(email)) {
					return privateKey;
				}
			}
			statement.close();
		} catch (Exception e) {
			throw (e);
		}
		return null;
	}

	public static String[] openConnectionGetPrivateKeyApi(String publicKey)
			throws Exception {
		try {
			// index[0] = Users email, index[1] = Private Api Key
			String[] privKeyEmail = new String[2];
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM User");
			while (rs.next()) {
				String dbPublicKey = rs.getString("publicApiKey");
				if (dbPublicKey.equalsIgnoreCase(publicKey)) {
					privKeyEmail[0] = rs.getString("email");
					privKeyEmail[1] = rs.getString("privateApiKey");

					return privKeyEmail;
				}
			}
			statement.close();
		} catch (Exception e) {
			throw (e);
		}
		return null;
	}

	public static String openConnectionGetUserEmail(String publicAPIKey)
			throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(connectionURL, "root", "");
			statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery("SELECT * FROM User where publicApiKey = '"
							+ publicAPIKey + "'");
			while (rs.next()) {
				return rs.getString("email");
			}
			statement.close();
		} catch (Exception e) {
			throw (e);
		}
		return null;
	}
}
