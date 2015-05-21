package WebServiceJeremy;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * Class to connect and interact with the Web Service database
 * 
 * @author Ryan Kavanagh
 * @version 1.0
 */
public class SQLConnectionUpdate {
	private static Connection connection = null;
	private static Statement statement = null;
	private static String connectionURL = "jdbc:mysql://localhost:3306/JeremyAPIDatabase";

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
	 * The class's constructor method
	 * 
	 * @param executeStatement - the statement to be executed
	 * 
	 * <br>
	 * <b>USAGE:</b></br>
	 * <pre>
	 * private String firstName = &quot;Ryan&quot;;
	 * private String lastName = &quot;Kavanagh&quot;;
	 * private String email = &quot;r@k.com&quot;;
	 * private String creditCard = &quot;123123123&quot;;
	 * private String password = &quot;taste&quot;;
	 * private String salt = &quot;test&quot;;
	 * private String[] keys = new String[2];
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
