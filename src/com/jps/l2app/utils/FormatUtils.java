/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormatUtils {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm:ss");
    private static final SimpleDateFormat SQL_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SMS_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###.0");

    public static Date getDayStart(Date date) {

        try {
            return SQL_FORMAT.parse(SQL_FORMAT.format(date));
        } catch (ParseException ex) {
            Logger.getLogger(FormatUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static Date getDayEnd(Date date) {
        try {
            return SMS_FORMAT.parse(SQL_FORMAT.format(date) + " 23:59:59");
        } catch (ParseException ex) {
            Logger.getLogger(FormatUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getCurrentTime() {
        return TIME_FORMAT.format(new Date());
    }

    public static Date getPlainDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String formatStr = format.format(date);
        try {
            date = format.parse(formatStr);
        } catch (ParseException e) {
            System.err.println("Parse Exception");
        }
        return date;
    }

    public static String toCurrencyFormat(Object gross) {
        return CURRENCY_FORMAT.format(gross);
    }

    public static Set<String> rambolito(String str) {
        Set<String> com = new HashSet<>();
        com.add("" + str.charAt(0) + str.charAt(1) + str.charAt(2));
        com.add("" + str.charAt(0) + str.charAt(2) + str.charAt(1));
        com.add("" + str.charAt(1) + str.charAt(0) + str.charAt(2));
        com.add("" + str.charAt(1) + str.charAt(2) + str.charAt(0));
        com.add("" + str.charAt(2) + str.charAt(0) + str.charAt(1));
        com.add("" + str.charAt(2) + str.charAt(1) + str.charAt(0));
        return com;
    }

    public static String currencyFormat(double gross) {
        return CURRENCY_FORMAT.format(gross);
    }

}
