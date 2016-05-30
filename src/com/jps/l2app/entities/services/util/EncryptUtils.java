/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services.util;

import com.jps.l2app.main.Main;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author org org
 */
public class EncryptUtils {

    public static final String DEFAULT_ENCODING = "UTF-8";
    private static final BASE64Encoder enc = new BASE64Encoder();
    private static final BASE64Decoder dec = new BASE64Decoder();

    public static String base64encode(String text) {
        try {
            String rez = enc.encode(text.getBytes(DEFAULT_ENCODING));
            return rez;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }//base64encode

    public static String base64decode(String text) {

        try {
            return new String(dec.decodeBuffer(text), DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }

    }//base64decode

    private static final String XOR_KEY = "2015_266a1fe4-fba1-4fb3-a961-cb473fbbc5dc";

    public static String encrypt(String str) {
        String xorMessage = xorMessage(str, XOR_KEY);
        String encoded = base64encode(xorMessage);
        return encoded;
    }

    public static String decrypt(String str) {
        String encoded = base64decode(str);
        String xorMessage = xorMessage(encoded, XOR_KEY);
        return xorMessage;
    }

    public static void main(String[] args) {
        
        String text = "Test text;";
        String encrypt = encrypt(text);
        Main.log("encrypt = " + encrypt);
        String decrypt = decrypt(encrypt);
        Main.log("decrypt = " + decrypt);
//        
//
//        String txt = "some text to be encrypted";
//        String key = "key phrase used for XOR-ing";
//
//        Main.log(txt + " XOR-ed to: " + (txt = xorMessage(txt, key)));
//        String encoded = base64encode(txt);
//        Main.log(" is encoded to: " + encoded + " and that is decoding to: " + (txt = base64decode(encoded)));
//        System.out.print("XOR-ing back to original: " + xorMessage(txt, key));

    }

    public static String xorMessage(String message, String key) {
        try {
            if (message == null || key == null) {
                return null;
            }

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char) (mesg[i] ^ keys[i % kl]);
            }//for i
            mesg = null;
            keys = null;
            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }//xorMessage

}
