package com.嘤嘤嘤.qwq.MailBox.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    
    public static String Hex(String str){
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(str.getBytes("UTF8"));
            byte s[] = m.digest();
            String result = "";
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }
            return result;
	} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            System.out.println(e);
	}
        return "";
    }

}
