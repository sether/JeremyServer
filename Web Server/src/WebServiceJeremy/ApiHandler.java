package WebServiceJeremy;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;



public class ApiHandler {
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
			keyGen.initialize(2048);
			key = keyGen.generateKeyPair();
			
			// Make the keys
			privateKey = key.getPrivate().getEncoded().toString();
			publicKey = key.getPublic().getEncoded().toString();
			
			theKeys[0] = privateKey;
			theKeys[1] = publicKey;
			
			// Storing the intial API keys is handled by the action of registering
			
			System.out.println("Newly Created pub/pri keys"); // debug
			System.out.println("Private: " + privateKey + " Public: " + publicKey); // debug
			
			return theKeys;
		} else {
			System.out.println("Grabbed keys from database."); // debug
			theKeys[0] = privateKey;
			theKeys[1] = publicKey;
			
			return theKeys;
		}	
	}
	
	public static String hashSecrets(String email, String privateKey) throws NoSuchAlgorithmException, InvalidKeyException {
		String entropyString = email + privateKey;
	    SecretKey secretKey = null;

	    byte[] keyBytes = entropyString.getBytes();
	    secretKey = new SecretKeySpec(keyBytes, "HmacSHA1");

	    Mac mac = Mac.getInstance("HmacSHA1");

	    mac.init(secretKey);

	    byte[] rawHmac = mac.doFinal(entropyString.getBytes());

	    return new String(Base64.encodeBase64(mac.doFinal(rawHmac))).trim();
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
}


