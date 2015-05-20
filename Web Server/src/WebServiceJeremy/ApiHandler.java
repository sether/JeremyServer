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
	 * Hashes a "unique" string using a string that adds entropy with the HmacSHA1 algorithm.
	 * The server will use this when the client sends their own hashed version of a string so we can compare they are the same.
	 * Thus allowing us to assume the person is who they say they are.
	 * @param email - The users email
	 * @param privateKey - The private key of the user
	 * @return Base64 Encoded String of the HmacSHA1 encoded string.
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public static String hashSecrets(String email, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
		// Create an "Sting of entropy" which is just a String containing the email + private key
		String entropyString = email + privateKey;
	    SecretKey secretKey = null;

	    // Convert the entropyString to a byte array to use it in the algorithm.
	    byte[] keyBytes = entropyString.getBytes();
	    // Encode with HmacSHA1 
	    secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

	    // Setup the crpyto stuff
	    Mac mac = Mac.getInstance("HmacSHA1");
	    mac.init(secretKey);
	    byte[] rawHmac = mac.doFinal(entropyString.getBytes());
	    
	    // Return a Base64 representation of the string encoded in HmacSHA1
	    return new String(Base64.encodeBase64(rawHmac)).trim();
	}
	
	/**
	 * 
	 * @param clientHash - What the client came up with when they hashed their info.
	 * @param publicApiKey - The public api key which identifies the client.
	 * @return success - If both the client and server hashes match, we're pretty sure they are who they say they are.
	 * @throws Exception
	 */
	public static boolean compareHashes(String clientHash, String publicApiKey) throws Exception {
		boolean success = false;
		// index[0] = Users email, index[1] = Private Api Key 
		String[] emailAndprivateKey = SQLConnectionUpdate.openConnectionGetPrivateKeyApi(publicApiKey);
		
		// Using the info we looked up with the public api key, create a server made hash
		// using the same information the client did.
		String serverHash = hashSecrets(emailAndprivateKey[0], emailAndprivateKey[1]);
		
		return success = serverHash.equals(clientHash) ? true : false;
	}
	
	/**
	 * A method used to generate API Keys for the client and return them so they can be viewed on the members page.
	 * 
	 * @param mode - The generation mode (Regenerate is assumes you are making a new set of keys)
	 * @param email - The email used to identify the user.
	 * @return String[] - The Public and Private API Keys. - [0] = Public Key, [1] = Private Key
	 * @throws Exception
	 */
	public static String[] generateClientKeys(GENERATE_MODE mode, String email) throws Exception {
		String publicKey = null;
		String privateKey = null;		
		String theKeys[] = new String[2];
		
		publicKey = SQLConnectionUpdate.openConnectionGetPublicKey(email);
		privateKey = SQLConnectionUpdate.openConnectionGetPrivateKey(email);
		
		if (publicKey == null || privateKey == null ) {
			// Assume its a new user.
			return theKeys = createKeys();
		} else if (mode == GENERATE_MODE.REGENERATE) {
			// Generate new API keys action performed on the members page.
			return theKeys = regenerateKeys(email);
		} else {
			// We found the keys in the database
			theKeys[0] = publicKey;
			theKeys[1] = privateKey;
			
			return theKeys;
		}	
	}
	
	/**
	 * Helper method to create a random set of private and public api keys, Uses the RSA algorithm to create the pair.
	 * @return String[] - The Public and Private API Keys. - [0] = Public Key, [1] = Private Key
	 * @throws NoSuchAlgorithmException
	 */
	public static String[] createKeys() throws NoSuchAlgorithmException {
		String[] keyPair = new String[2];
		String publicKey = null;
		String privateKey = null;
		KeyPairGenerator keyGen = null;
		KeyPair key = null;
		
		// We use 2048bit RSA encryption and generate a key pair (Public/Private Api Key)
		keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		key = keyGen.generateKeyPair();
		
		
		byte[] pubKeyArray = key.getPublic().getEncoded();
		
		byte[] priKeyArray = key.getPrivate().getEncoded();
		
		// Load keys into the Array
		// 0 is always public, 1 is always private.
		keyPair[0] = new String(Base64.encodeBase64(pubKeyArray)).trim();
		keyPair[1] = new String(Base64.encodeBase64(priKeyArray)).trim();
		
		return keyPair;
	}
	
	/**
	 * Method that creates a new set of API Keys and updates the previous keys to the new ones.
	 * @param email - the users email (Emails are non changing)
	 * @return - String[] theKeys, So we can display the key values.
	 * @throws Exception
	 */
	public static String[] regenerateKeys(String email) throws Exception {
		String[] theKeys = createKeys();
		String statement = "UPDATE USER SET publicApiKey='" + theKeys[0] + "', privateApiKey='" + theKeys[1] + "' WHERE email='" + email + "'";
		SQLConnectionUpdate.openConnectionUpdate(statement);
		
		return theKeys;
	}
}


