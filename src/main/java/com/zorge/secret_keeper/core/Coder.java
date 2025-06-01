package com.zorge.secret_keeper.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Coder {

	private byte keyToByte(final byte[] key) {
		
		byte out = 0;
		for(int i = 0; i < key.length; ++i)
			out += key[i];
		return out;
	}

	private byte[] keyToIV(final byte[] key) {
		
		byte byteKey = keyToByte(key);
		
        byte[] iv = new byte[16];        
        for(int i = 0; i < 16; ++i, ++byteKey)
        	iv[i] = byteKey;
		return iv;
	}

	private byte[] md5(final String txt) throws NoSuchAlgorithmException {
		
		MessageDigest md = MessageDigest.getInstance("MD5");
		return md.digest(txt.getBytes());
	}
	
	private byte[] IVtoSecretKey(final byte[] iv) {
		
        byte[] keyBytes = new byte[32];
        for(int i = 0; i < 16; ++i) { 
        	keyBytes[i] = iv[i];
        	keyBytes[i + 16] = iv[i];
        }
        return keyBytes;
	}
	
	/**
	 * Encrypt data.
	 * 
	 * @param data
	 * @param psw
	 * @return
	 * @throws Exception
	 */
	public byte[] encrypt(final String data, final String psw) throws Exception {
		
		if(data == null || psw == null)
			throw new Exception("Input is null.");
		if(psw.isEmpty())
			throw new Exception("Password can not be empty.");
		
		byte[] key = md5(psw);
		byte[] iv = keyToIV(key);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		byte[] keyBytes = IVtoSecretKey(iv);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] out = cipher.doFinal(data.getBytes());
		return out;
	}
	
	/**
	 * Decrypt data.
	 * 
	 * @param data
	 * @param psw
	 * @return
	 * @throws Exception
	 */
	public byte[] decrypt(final byte[] data, final String psw) throws Exception {
		
		if(data == null || psw == null)
			throw new Exception("Input is null.");
		if(psw.isEmpty())
			throw new Exception("Password can not be empty.");
		
		byte[] key = md5(psw);
		byte[] iv = keyToIV(key);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
		byte[] keyBytes = IVtoSecretKey(iv);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		byte[] out = cipher.doFinal(data);
		return out;
	}
}
