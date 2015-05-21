package WebServiceJeremy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.sql.*;
import java.util.Arrays;

import com.jeremy.CSVHandler;
import com.jeremy.TableData;

/**
 * Class to salt and hash passwords used by the Web Service
 * 
 * @author Ryan Kavanagh
 * @version 1.0
 */
public class SaltHash {

	private final static int ITERATION_NUMBER = 1000;

	/**
	 * The class's constructor method
	 * 
	 * <br>
	 * <b>USAGE:</b></br>
	 * <pre>
	 *  Salthhash sh = new SalthHash()
	 * </pre>
	 */
	public SaltHash() {
	}

	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 * 
	 * @param iterationNb - the number of iterations of the algorithm
	 * @param password - the password to encrypt
	 * @param salt - the string used to encrypt and decrypt the password
	 * @return input - the password that has been converted back to a string from byte code
	 * <br>
	 * <b>USAGE:</b></br>
	 * <pre>
	 * boolean validUser = false;
	 * SaltHash sh = new SaltHash();
	 * try {
	 * 	Class.forName(&quot;com.mysql.jdbc.Driver&quot;).newInstance();
	 * 	connection = DriverManager.getConnection(connectionURL, &quot;root&quot;, &quot;&quot;);
	 * 	statement = connection.createStatement();
	 * 	ResultSet rs = statement
	 * 			.executeQuery(&quot;SELECT EMAIL, PASSWORD, SALT FROM User&quot;);
	 * 	while (rs.next()) {
	 * 		String email = rs.getString(&quot;email&quot;);
	 * 		String password = rs.getString(&quot;password&quot;);
	 * 		String salt = rs.getString(&quot;salt&quot;);
	 * 		byte[] bDigest = sh.base64ToByte(password);
	 * 		byte[] bSalt = sh.base64ToByte(salt);
	 * 		byte[] proposedDigest = sh.getHash(1000, passwordTest, bSalt);
	 * 		if (email.equalsIgnoreCase(emailTest)
	 * 				&amp;&amp; Arrays.equals(proposedDigest, bDigest)) {
	 * 			validUser = true;
	 * 		}
	 * 	}
	 * 	statement.close();
	 * 	rs.close();
	 * 	connection.close();
	 * } catch (NoSuchAlgorithmException e) {
	 * 	throw (e);
	 * }
	 * </pre>
	 * @throws NoSuchAlgorithmException
	 */
	public byte[] getHash(int iterationNb, String password, byte[] salt)
			throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = null;
		try {
			input = digest.digest(password.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < iterationNb; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return input;
	}

	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data - the String data that is to be converted to byte data 
	 * @return decoder - String data converted to byte data
	 * <br>
	 * <b>USAGE:</b></br>
	 * <pre>
	 * boolean validUser = false;
	 * SaltHash sh = new SaltHash();
	 * String email = &quot;&quot;;
	 * String password = &quot;&quot;;
	 * String salt = &quot;&quot;;
	 * byte[] bDigest = sh.base64ToByte(password);
	 * byte[] bSalt = sh.base64ToByte(salt);
	 * byte[] proposedDigest = sh.getHash(1000, passwordTest, bSalt);
	 * 
	 * </pre>
	 * @throws IOException
	 */
	public byte[] base64ToByte(String data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}

	/**
	 * From a byte[] returns a base 64 representation
	 * 
	 * @param data - the byte data that is to be converted to String data
	 * @return decoder - byte data converted to String data
	 * <br>
	 * <b>USAGE:</b></br>
	 * <pre>
	 * SaltHash sh = new SaltHash();
	 * try {
	 * 	SecureRandom random = SecureRandom.getInstance(&quot;SHA1PRNG&quot;);
	 * 	byte[] bSalt = new byte[8];
	 * 	random.nextBytes(bSalt);
	 * 	byte[] bDigest;
	 * 	bDigest = sh.getHash(1000, password, bSalt);
	 * 	password = sh.byteToBase64(bDigest);
	 * 	salt = sh.byteToBase64(bSalt);
	 * } catch (NoSuchAlgorithmException e1) {
	 * 	e1.printStackTrace();
	 * }
	 * </pre>
	 * @throws IOException
	 */
	public String byteToBase64(byte[] data) {
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}
}