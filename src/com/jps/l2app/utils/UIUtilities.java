/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.utils;

import com.jps.l2app.main.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

//version 1
public class UIUtilities {

    public static String getMD5EncryptedPassword(String pass) {
        String ret = null;
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(pass.getBytes(), 0, pass.length());
            ret = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Main.log("error: getMD5EncryptedPassword");
        }
        return ret;
    }

    public static void copyFile(File source, File dest) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public static void addInfoMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void addWarningMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    public static boolean getConfirmation(String message) {
        return JOptionPane.showConfirmDialog(null, message, "Confirmation", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
    }

    public static String getSingleInput(String message) {
        String in = JOptionPane.showInputDialog(message);
        return in;
    }

    private static SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

    public static String showTimeOnly(Date d) {
        return formatter.format(d);
    }

}
