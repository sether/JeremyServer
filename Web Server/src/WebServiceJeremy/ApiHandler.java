package WebServiceJeremy;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;


/**
 * 
 * @author Anthony Howse
 *
 */
public class ApiHandler {
	public enum GENERATE_MODE {
		NEW_USER,
		REGENERATE
	}
	
	/**
	 * 
	 * @param email
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String hashSecrets(String email, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
		String entropyString = email + privateKey;
	    SecretKey secretKey = null;

	    byte[] keyBytes = entropyString.getBytes();
	    secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

	    Mac mac = Mac.getInstance("HmacSHA1");

	    mac.init(secretKey);

	    byte[] rawHmac = mac.doFinal(entropyString.getBytes());

	    return new String(Base64.encodeBase64(rawHmac)).trim();
	}
	
	/**
	 * 
	 * @param clientHash
	 * @param publicApiKey
	 * @return
	 * @throws Exception
	 */
	public static boolean compareHashes(String clientHash, String publicApiKey) throws Exception {
		boolean success = false;
		// index[0] = Users email, index[1] = Private Api Key 
		String[] privateKeyAndEmail = SQLConnectionUpdate.openConnectionGetPrivateKeyApi(publicApiKey);
		
		String serverHash = hashSecrets(privateKeyAndEmail[0], privateKeyAndEmail[1]);
		
		return success = serverHash.equals(clientHash) ? true : false;
	}
	
	/**
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public static String[] generateClientKeys(GENERATE_MODE mode, String email) throws Exception {
		String publicKey = null;
		String privateKey = null;		
		String theKeys[] = new String[2];
		
		publicKey = SQLConnectionUpdate.openConnectionGetPublicKey(email);
		privateKey = SQLConnectionUpdate.openConnectionGetPrivateKey(email);
		
		if (publicKey == null || privateKey == null ) {
			return theKeys = createKeys();
		} else if (mode == GENERATE_MODE.REGENERATE) {
			return theKeys = regenerateKeys(user);
		} else {
			theKeys[0] = publicKey;
			theKeys[1] = privateKey;
			
			return theKeys;
		}	
	}
	
	/**
	 * Helper method
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String[] createKeys() throws NoSuchAlgorithmException {
		String[] keyPair = new String[2];
		String publicKey = null;
		String privateKey = null;
		KeyPairGenerator keyGen = null;
		KeyPair key = null;
		
		keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(2048);
		key = keyGen.generateKeyPair();
		
		// Make the keys
		publicKey = key.getPublic().getEncoded().toString();	
		privateKey = key.getPrivate().getEncoded().toString();
		
		// Load keys into the Array
		keyPair[0] = publicKey;
		keyPair[1] = privateKey;
		
		return keyPair;
	}
	
	public static String[] regenerateKeys(String email) throws Exception {
		String[] theKeys = createKeys();
		String statement = "UPDATE USERS SET publicApiKey=" + theKeys[0] + ", privateApiKey=" + theKeys[1] + " WHERE email=" + email;
		SQLConnectionUpdate.openConnectionUpdate(statement);
		
		return theKeys;
	}
}


