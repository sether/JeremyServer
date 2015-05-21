package WebServiceJeremy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.sql.*;
import java.util.Arrays;

public class SaltHash {

	private final static int ITERATION_NUMBER = 1000;

	public SaltHash() {
	}

	public boolean authenticate(Connection con, String login, String password)
			throws SQLException, NoSuchAlgorithmException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			boolean userExist = true;
			if (login == null || password == null) {
				userExist = false;
				login = "";
				password = "";
			}

			ps = con.prepareStatement("SELECT PASSWORD, SALT FROM CREDENTIAL WHERE LOGIN = ?");
			ps.setString(1, login);
			rs = ps.executeQuery();
			String digest, salt;
			if (rs.next()) {
				digest = rs.getString("PASSWORD");
				salt = rs.getString("SALT");
				if (digest == null || salt == null) {
					throw new SQLException(
							"Database inconsistant Salt or Digested Password altered");
				}
				if (rs.next()) {
					throw new SQLException(
							"Database inconsistent two CREDENTIALS with the same LOGIN");
				}
			} else {
				digest = "000000000000000000000000000=";
				salt = "00000000000=";
				userExist = false;
			}

			byte[] bDigest = base64ToByte(digest);
			byte[] bSalt = base64ToByte(salt);
			byte[] proposedDigest = getHash(ITERATION_NUMBER, password, bSalt);
			return Arrays.equals(proposedDigest, bDigest) && userExist;
		} catch (IOException ex) {
			throw new SQLException(
					"Database inconsistant Salt or Digested Password altered");
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

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

	public byte[] base64ToByte(String data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}

	public String byteToBase64(byte[] data) {
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}
}