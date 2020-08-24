package com.tech4lyf.sbsrvending.utility;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import android.util.Base64;
import android.util.Log;


public class RSAUtility {
	public static String encrypt(String plainText, String key){
		try{
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT)));
		    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			Log.d("Encoded", Base64.encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")),Base64.DEFAULT));
		    return Base64.encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")),Base64.DEFAULT);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}