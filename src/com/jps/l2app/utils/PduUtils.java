/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.utils;

import com.jps.l2app.entities.RawSMS;
import com.jps.l2app.entities.services.RawSMSService;
import com.jps.l2app.devices.AT_COMMANDS;
import com.jps.l2app.main.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PduUtils {

    private static final int SEPTETS_CONCATENATED_SMS = 152;
    private static final int SEPTETS_SINGLE_SMS = 160;

    public static RawSMS interpret(String pduData) {
        throw new UnsupportedOperationException();
    }

    public static void sendSMS(String number, String msg) {

        number = "63" + RawSMSService.extractEssentialMobileNum(number);

        byte[] pduPart = SMSTools.getPDUPart_1(number, false, msg);
        char[] toHexString = SMSTools.toHexString(pduPart);

        String pduData = new String(toHexString);
        int octets = pduData.length() / 2;

        //String sendn = AT_COMMANDS.SEND_PDU + number + "\n";
        String sendn = AT_COMMANDS.SEND_PDU + octets + "\n";
        Main.log("sendn: " + sendn);

        pduData = "00" + pduData;
        String sendm = AT_COMMANDS.SENDM + pduData + "\n";
        Main.log("sendm: " + sendm);

    }

    public static void main(String[] args) {

        String message = "rquick";
        Main.log("message.length() = " + message.length());
        String number = "639125176246";
        List<String> rawPDUs = getRawPDUs(message, number);
        for (String rawPDU : rawPDUs) {
            Main.log("rawPDU. " + ((rawPDU.length() / 2) - 1) + ": " + rawPDU);
        }

    }

    /**
     *
     * @param message
     * @param number - Must be international: 639125176246
     * @return
     */
    public static List<String> getRawPDUs(String message, String number) {

        byte[] mbytes = SMSTools.convertUnicode2GSM(message);

        List<String> preOutputBuff = new ArrayList();
        String tp_mti = "01";

        if (mbytes.length > SEPTETS_SINGLE_SMS) {
            tp_mti = "41";

            int cmsref_oct1 = (int) (Math.random() * 255); //octet1 of ref number
            String cmsref1 = Integer.toHexString(cmsref_oct1);
            cmsref1 = cmsref1.length() == 1 ? "0".concat(cmsref1) : cmsref1;
            int cmsref_oct2 = (int) (Math.random() * 255); //octet2 of ref number
            String cmsref2 = Integer.toHexString(cmsref_oct2);
            cmsref2 = cmsref2.length() == 1 ? "0".concat(cmsref2) : cmsref2;

            int parts = 1 + (mbytes.length / SEPTETS_CONCATENATED_SMS);
            for (int i = 0; i < parts; i++) {

                StringBuilder messagePDU = new StringBuilder();
                //<editor-fold defaultstate="collapsed" desc="udh">
                messagePDU.append("06") //udhl - user data header length
                        .append("08") //information element identifier: concatenated sms using 16bit ref number
                        .append("04"); //length of the header excluding the first two fields

                messagePDU.append(cmsref1).append(cmsref2); //cmsref

                String partsStr = Integer.toHexString(parts);
                partsStr = partsStr.length() == 1 ? "0".concat(partsStr) : partsStr;
                messagePDU.append(partsStr);

                String partNumStr = Integer.toHexString(i + 1);
                partNumStr = partNumStr.length() == 1 ? "0".concat(partNumStr) : partNumStr;
                messagePDU.append(partNumStr);
                //</editor-fold>

                int s = i * SEPTETS_CONCATENATED_SMS;
                int e = SEPTETS_CONCATENATED_SMS + s;
                e = e > mbytes.length ? mbytes.length : e;

                byte[] ud = Arrays.copyOfRange(mbytes, s, e);
                preOutputBuff.add(createMessagePDU(messagePDU.toString(), ud));
            }

        } else {
            preOutputBuff.add(createMessagePDU(mbytes, 0));
        }

        number = transformNumberToPDU(number);

        StringBuilder header = new StringBuilder("00");//this octet is excluded from the head count
        header.append(tp_mti);
        header.append("000C91");
        header.append(number);
        header.append("0000");
        String headerStr = header.toString();

        List<String> output = new ArrayList<>();
        for (String tp_du : preOutputBuff) {
            String rawPdu = headerStr + tp_du;
            output.add(rawPdu);
        }

        return output;

    }

    private static String transformNumberToPDU(String number) {
        String pNumber = "";
        for (int i = 0; i < number.length(); i++) {
            int index;
            if ((i + 1) % 2 == 0) {
                index = i - 1;
            } else {
                index = i + 1;
            }
            pNumber += number.charAt(index);

        }
        return pNumber;
    }

    private static String createMessagePDU(String header, byte[] mbytes) throws NumberFormatException {

        int headerBits = (header.length() / 2) * 8;

        int prependBits = headerBits % 7 == 0 ? 0 : 7 - (headerBits % 7);
        Main.log("prependBits = " + prependBits);
        String whole = "";

        for (int i = 0; i < prependBits; i++) {
            whole += "0";
        }

        for (byte i : mbytes) {
            String binStr = Integer.toBinaryString(i);
            if (binStr.length() < 7) {
                int pads = 7 - binStr.length();
                for (int j = 0; j < pads; j++) {
                    binStr = "0".concat(binStr);
                }
            }

            // Main.log(binStr);
            whole = binStr + whole;
        }

        int l = whole.length();
        if (l % 8 != 0) {

            int pads = 8 - (l % 8);
            StringBuilder b = new StringBuilder();

            // Main.log("pads = " + pads);
            for (int i = 0; i < pads; i++) {
                b.append("0");
            }
            whole = b.toString() + whole;
        }

        int length = (whole.length() + headerBits) / 7;
        Main.log("Valid pduL? = " + (((whole.length() + headerBits) % 7) == 0));

        String udL = Integer.toHexString(length);
        udL = udL.length() == 1 ? "0".concat(udL) : udL;

        List<String> userData = new ArrayList<>();

        for (int i = 0; i < whole.length(); i += 8) {
            String substring = whole.substring(i, i + 8);
            int b = Integer.parseInt(substring, 2);
            String hex = Integer.toHexString(b);
            hex = hex.length() == 1 ? "0".concat(hex) : hex;
            userData.add(hex);
            //Main.log("hex=" + hex);
        }

        String userDataStr = udL + header;
        for (int i = userData.size() - 1; i >= 0; i--) {
            String get = userData.get(i);
            userDataStr += get;
        }

        userDataStr = userDataStr.toUpperCase();
        return userDataStr;
    }

    private static String createMessagePDU(byte[] mbytes, int prependBits) throws NumberFormatException {
        String whole = "";

        for (int i = 0; i < prependBits; i++) {
            whole += "0";
        }

        for (byte i : mbytes) {
            String binStr = Integer.toBinaryString(i);
            if (binStr.length() < 7) {
                int pads = 7 - binStr.length();
                for (int j = 0; j < pads; j++) {
                    binStr = "0".concat(binStr);
                }
            }

            // Main.log(binStr);
            whole = binStr + whole;
        }

        int l = whole.length();
        if (l % 8 != 0) {

            int pads = 8 - (l % 8);
            StringBuilder b = new StringBuilder();

            // Main.log("pads = " + pads);
            for (int i = 0; i < pads; i++) {
                b.append("0");
            }
            whole = b.toString() + whole;
        }

        String udL = Integer.toHexString(whole.length() / 7);
        udL = udL.length() == 1 ? "0".concat(udL) : udL;

        List<String> userData = new ArrayList<>();

        for (int i = 0; i < whole.length(); i += 8) {
            String substring = whole.substring(i, i + 8);
            int b = Integer.parseInt(substring, 2);
            String hex = Integer.toHexString(b);
            hex = hex.length() == 1 ? "0".concat(hex) : hex;
            userData.add(hex);
            //Main.log("hex=" + hex);
        }

        String userDataStr = udL;
        for (int i = userData.size() - 1; i >= 0; i--) {
            String get = userData.get(i);
            userDataStr += get;
        }

        userDataStr = userDataStr.toUpperCase();
        return userDataStr;
    }

}
