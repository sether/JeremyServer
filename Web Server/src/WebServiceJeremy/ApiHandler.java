package WebServiceJeremy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;

import com.mysql.jdbc.interceptors.SessionAssociationInterceptor;



/** Jeremy Server API Setup
* Write public and private server keys to files
* check if they're there, if they aren't we are on a new server so set it up
* if they are there load them up
*/

/** Jeremy Dev/User API Setup
 * Grab the email of the person in the database
 * check if they have an api key in the db
 * if they don't, make a pub/pri pair for them and add the public key to the db
 * otherwise just use it.
 * @throws NoSuchAlgorithmException 
 * 
 */
public class ApiHandler {
	static byte[] jeremyPublic;
	
	String test = "test";
	byte[] privateKeyDev = null;
	byte[] publicKeyDev = null;
	

	public ApiHandler() {

	}
	
	/**
	 * Takes unique data as a String (the full rest url call is good enough), hashes it with the private key the user has received.
	 * 
	 * theString - For our purposes we are using the users email as the unique string of data
	 * privateKey - The private key value
	 * publicKey - The public key, for user identification
	 * @return
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	/**
	public static String calculate() throws GeneralSecurityException, UnsupportedEncodingException {
		//String theString = databaseGetUsersEmail();
		//String privateKey = databaseGetUsersPrivateKey();
	    SecretKey secretKey = null;

	    byte[] keyBytes = privateKey.getBytes();
	    secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

	    Mac mac = Mac.getInstance("HmacSHA1");

	    mac.init(secretKey);

	    byte[] text = theString.getBytes();

	    return new String(Base64.encodeBase64(mac.doFinal(text))).trim();
	}
	 * @throws Exception 
	*/
	
	// Generating the Keys
	public static String[] generateClientKeys(String user) throws Exception {		
		String privateKey = null;
		String publicKey = null;
		KeyPairGenerator keyGen = null;
		KeyPair key = null;
		String theKeys[] = new String[2];
		
		privateKey = SQLConnectionUpdate.openConnectionGetPrivateKey(user);
		publicKey = SQLConnectionUpdate.openConnectionGetPublicKey(user);
		
		if (publicKey == null && privateKey == null) {
			keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(512);
			key = keyGen.generateKeyPair();
			
			// Make the keys
			privateKey = key.getPrivate().getEncoded().toString();
			publicKey = key.getPublic().getEncoded().toString();
			
			theKeys[0] = privateKey;
			theKeys[1] = publicKey;
			
			// Store the keys in the database
			
			
			System.out.println("Newly Created pub/pri keys");
			System.out.println("Private: " + privateKey + " Public: " + publicKey);
			
			return theKeys;
		} else {
			System.out.println("Grabbed keys from database.");
			theKeys[0] = privateKey;
			theKeys[1] = publicKey;
			
			return theKeys;
		}

			
		//KeyFactory fact = KeyFactory.getInstance("RSA");
		//RSAPublicKeySpec pub = fact.getKeySpec(key.getPublic(), RSAPublicKeySpec.class);
		//RSAPrivateKeySpec priv = fact.getKeySpec(key.getPrivate(), RSAPrivateKeySpec.class);

		//saveToFile("public.key", pub.getModulus(), pub.getPublicExponent());
		//saveToFile("private.key", priv.getModulus(), priv.getPrivateExponent());
		
	}
	
	public void saveToFile(String fileName, BigInteger mod, BigInteger exp) throws IOException {
		ObjectOutputStream oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
		try {
			oout.writeObject(mod);
			oout.writeObject(exp);
		} catch (Exception e) {
			throw new IOException("Unexpected error", e);
		} finally {
			oout.close();
		}
	}
	
	public PublicKey readKeyFromFile(String keyFileName) throws IOException {
		  InputStream in = ApiHandler.class.getResourceAsStream(keyFileName);
		  ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
		  try {
		    BigInteger m = (BigInteger) oin.readObject();
		    BigInteger e = (BigInteger) oin.readObject();
		    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    PublicKey pubKey = fact.generatePublic(keySpec);
		    return pubKey;
		  } catch (Exception e) {
		    throw new RuntimeException("Spurious serialisation error", e);
		  } finally {
		    oin.close();
		  }
		}
	
	public byte[] rsaEncrypt(byte[] data) throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException {
		  PublicKey pubKey;
		  
		  pubKey = readKeyFromFile("/public.key");
		  Cipher cipher = Cipher.getInstance("RSA");
		  cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		  byte[] cipherData = cipher.doFinal(data);
		  return cipherData;
		}
	
	public static String getJeremyPublicKey() {
		return jeremyPublic.toString();
	}

}


