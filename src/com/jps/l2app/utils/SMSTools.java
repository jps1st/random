package com.jps.l2app.utils;

import com.jps.l2app.entities.RawSMS;
import com.jps.l2app.entities.services.EmployeeService;
import com.jps.l2app.entities.services.RawSMSService;
import com.jps.l2app.main.Main;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SMSTools {

    static final boolean TOA_NATIONAL = true;
    static final String SMSTEXTSIGN = ">";
    static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yy HH:mm:ss");

    /**
     * Convert a char array to a string
     *
     * @param chararray character array
     * @return string
     */
    public static String convertCharArray2String(char[] chararray) {
        int i, l;
        String text = new String();

        l = chararray.length;
        for (i = 0; i < l; i++) {
            text = text + chararray[i];
        } //for

        return text;
    } //convertChar2String

    /**
     * Create the main part part of a PDU, for sending via AT commands to the
     * mobile phone. The mobile phone complete this part of a PDU with some data
     * of the mobile phone and sends it then to the base station. This method
     * creates a "SMS-SBMIT type" message according to 3 GPP 23.040.
     *
     * @param number dialing number
     * @param nationalNumberType national/international dialing number
     * @param message message to send (SMS)
     * @return PDU
     */
    public static byte[] getPDUPart(String number, boolean nationalNumberType, String message) {
        byte[] pdu;
        byte[] no = convertDialNumber(number);
        byte[] msg = compress(convertUnicode2GSM(message));
        int l = no.length;
        int m = msg.length;
        pdu = new byte[4 + l + 4 + m];

        pdu[0] = 0x11;                      // message-type-indicator (MTI), message flags 00010001 [b8 ... b1]
        // b4 || b3: validity-period-format (VPF): field present - relative format
        // b1 || b0: message type: 01 for "SMS-SUBMIT (in the direction MS to SC)"
        pdu[1] = 0x00;                      // message reference number with default value

        pdu[2] = (byte) number.length();     // destination address, set length of dialing number
        if (nationalNumberType) {
            pdu[3] = (byte) 0x81;              // indicator for a national number
        } else {
            pdu[3] = (byte) 0x91;              // indicator for a international number
        }
        System.arraycopy(no, 0, pdu, 4, l); // set dialing number

        pdu[4 + l] = 0x00;                  // protocol identifier with GSM 03.40 default value
        pdu[4 + l + 1] = 0x00;              // data coding scheme, use GSM 03.38 character set (= default value)
        pdu[4 + l + 2] = (byte) 0xAA;        // message validity period = 4 days

        // message (= SMS content)
        pdu[4 + l + 3] = (byte) message.length();      // user-data-length, set length of message in characters before compression 
        System.arraycopy(msg, 0, pdu, 4 + l + 4, m);  // set message

        return pdu;
    } //getPDUPart

    public static byte[] getPDUPart_1(String number, boolean nationalNumberType, String message) {
        byte[] pdu;
        byte[] no = convertDialNumber(number);
        byte[] msg = compress(convertUnicode2GSM(message));
        int l = no.length;
        int m = msg.length;
        pdu = new byte[4 + l + 4 + m];

        pdu[0] = 0x01;    //changed to 01                  // message-type-indicator (MTI), message flags 00010001 [b8 ... b1]
        // b4 || b3: validity-period-format (VPF): field present - relative format
        // b1 || b0: message type: 01 for "SMS-SUBMIT (in the direction MS to SC)"
        pdu[1] = 0x00;                      // message reference number with default value

        pdu[2] = (byte) number.length();     // destination address, set length of dialing number
        if (nationalNumberType) {
            pdu[3] = (byte) 0x81;              // indicator for a national number
        } else {
            pdu[3] = (byte) 0x91;              // indicator for a international number
        }
        System.arraycopy(no, 0, pdu, 4, l); // set dialing number

        pdu[4 + l] = 0x00;                  // protocol identifier with GSM 03.40 default value
        pdu[4 + l + 1] = 0x00;              // data coding scheme, use GSM 03.38 character set (= default value)
        //pdu[4 + l + 2] = (byte) 0xAA;        // message validity period = 4 days

        // message (= SMS content)
        //pdu[4 + l + 3] = (byte) message.length();      // user-data-length, set length of message in characters before compression 
        pdu[4 + l + 2] = (byte) message.length();      // omitted to remove validity period
        System.arraycopy(msg, 0, pdu, 4 + l + 4, m);  // set message

        return pdu;
    } //getPDUPart

    /**
     * Convert a dialing number into the GSM format
     *
     * @param number dialing number
     * @return coded dialing number
     */
    public static byte[] convertDialNumber(String number) {
        int l = number.length();
        int j = 0;  // index in addr
        int n;      // length of converted dial number
        byte[] data;

        // calculate length of converted dialing number
        n = l / 2;
        if (l % 2 != 0) {
            n++;
        }
        data = new byte[n];
        for (int i = 0; i < n; i++) {
            switch (number.charAt(j)) {
                case '0':
                    data[i] += 0x00;
                    break;
                case '1':
                    data[i] += 0x01;
                    break;
                case '2':
                    data[i] += 0x02;
                    break;
                case '3':
                    data[i] += 0x03;
                    break;
                case '4':
                    data[i] += 0x04;
                    break;
                case '5':
                    data[i] += 0x05;
                    break;
                case '6':
                    data[i] += 0x06;
                    break;
                case '7':
                    data[i] += 0x07;
                    break;
                case '8':
                    data[i] += 0x08;
                    break;
                case '9':
                    data[i] += 0x09;
                    break;
            } // switch
            if (j + 1 < l) {
                switch (number.charAt(j + 1)) {
                    case '0':
                        data[i] += 0x00;
                        break;
                    case '1':
                        data[i] += 0x10;
                        break;
                    case '2':
                        data[i] += 0x20;
                        break;
                    case '3':
                        data[i] += 0x30;
                        break;
                    case '4':
                        data[i] += 0x40;
                        break;
                    case '5':
                        data[i] += 0x50;
                        break;
                    case '6':
                        data[i] += 0x60;
                        break;
                    case '7':
                        data[i] += 0x70;
                        break;
                    case '8':
                        data[i] += 0x80;
                        break;
                    case '9':
                        data[i] += 0x90;
                        break;
                } //switch
            } //if
            else {
                data[i] += 0xF0;
            } //else
            j += 2;
        }  //for
        return data;
    } //convertDialNumber

    /**
     * Convert the address field (dialing number) from the GSM format
     *
     * @param number dialing number
     * @return decoded dialing number
     */
    public static String decodeAddressField(String number) {

        int len;      // length of originating adress
        int n;        // length of converted dial number
        String s, orgAdr = "";

        s = number.substring(0, 2);         // get raw length [digits] of originating adress
        len = Integer.parseInt(s, 16);      // calculate length [digits] of originating adress

        int origLength = len;
        if (len % 2 == 1) {
            len++; //add the padding to the length if odd
        }

        number = number.substring(2, number.length());

        s = number.substring(0, 2);         // get raw length [digits] of originating adress
        if (s.compareTo("91") == 0) {
            orgAdr = "+";
        }

        number = number.substring(2, number.length());

        //----- digit swap procedure for the number
        n = 0;
        do {
            orgAdr += number.substring(n + 1, n + 2) + number.substring(n, n + 1);
            n += 2;
        } while (n < number.length());

        if (origLength != len) {
            orgAdr = orgAdr.substring(0, origLength);
        } //if

        return orgAdr;
    } //decodeAddressField

    /**
     * Convert a Unicode text string into the GSM standard alphabet
     *
     * @param msg text string in ASCII
     * @return text string in GSM standard alphabet
     */
    public static byte[] convertUnicode2GSM(String msg) {
        byte[] data = new byte[msg.length()];

        for (int i = 0; i < msg.length(); i++) {

            switch (msg.charAt(i)) {
                //<editor-fold defaultstate="collapsed" desc="switch">
                case '@':
                    data[i] = 0x00;
                    break;
                case '$':
                    data[i] = 0x02;
                    break;
                case '\n':
                    data[i] = 0x0A;
                    break;
                case '\r':
                    data[i] = 0x0D;
                    break;
                case '_':
                    data[i] = 0x11;
                    break;
                case 'ß':
                    data[i] = 0x1E;
                    break;
                case ' ':
                    data[i] = 0x20;
                    break;
                case '!':
                    data[i] = 0x21;
                    break;
                case '\"':
                    data[i] = 0x22;
                    break;
                case '#':
                    data[i] = 0x23;
                    break;
                case '%':
                    data[i] = 0x25;
                    break;
                case '&':
                    data[i] = 0x26;
                    break;
                case '\'':
                    data[i] = 0x27;
                    break;
                case '(':
                    data[i] = 0x28;
                    break;
                case ')':
                    data[i] = 0x29;
                    break;
                case '*':
                    data[i] = 0x2A;
                    break;
                case '+':
                    data[i] = 0x2B;
                    break;
                case ',':
                    data[i] = 0x2C;
                    break;
                case '-':
                    data[i] = 0x2D;
                    break;
                case '.':
                    data[i] = 0x2E;
                    break;
                case '/':
                    data[i] = 0x2F;
                    break;
                case '0':
                    data[i] = 0x30;
                    break;
                case '1':
                    data[i] = 0x31;
                    break;
                case '2':
                    data[i] = 0x32;
                    break;
                case '3':
                    data[i] = 0x33;
                    break;
                case '4':
                    data[i] = 0x34;
                    break;
                case '5':
                    data[i] = 0x35;
                    break;
                case '6':
                    data[i] = 0x36;
                    break;
                case '7':
                    data[i] = 0x37;
                    break;
                case '8':
                    data[i] = 0x38;
                    break;
                case '9':
                    data[i] = 0x39;
                    break;
                case ':':
                    data[i] = 0x3A;
                    break;
                case ';':
                    data[i] = 0x3B;
                    break;
                case '<':
                    data[i] = 0x3C;
                    break;
                case '=':
                    data[i] = 0x3D;
                    break;
                case '>':
                    data[i] = 0x3E;
                    break;
                case '?':
                    data[i] = 0x3F;
                    break;
                case 'A':
                    data[i] = 0x41;
                    break;
                case 'B':
                    data[i] = 0x42;
                    break;
                case 'C':
                    data[i] = 0x43;
                    break;
                case 'D':
                    data[i] = 0x44;
                    break;
                case 'E':
                    data[i] = 0x45;
                    break;
                case 'F':
                    data[i] = 0x46;
                    break;
                case 'G':
                    data[i] = 0x47;
                    break;
                case 'H':
                    data[i] = 0x48;
                    break;
                case 'I':
                    data[i] = 0x49;
                    break;
                case 'J':
                    data[i] = 0x4A;
                    break;
                case 'K':
                    data[i] = 0x4B;
                    break;
                case 'L':
                    data[i] = 0x4C;
                    break;
                case 'M':
                    data[i] = 0x4D;
                    break;
                case 'N':
                    data[i] = 0x4E;
                    break;
                case 'O':
                    data[i] = 0x4F;
                    break;
                case 'P':
                    data[i] = 0x50;
                    break;
                case 'Q':
                    data[i] = 0x51;
                    break;
                case 'R':
                    data[i] = 0x52;
                    break;
                case 'S':
                    data[i] = 0x53;
                    break;
                case 'T':
                    data[i] = 0x54;
                    break;
                case 'U':
                    data[i] = 0x55;
                    break;
                case 'V':
                    data[i] = 0x56;
                    break;
                case 'W':
                    data[i] = 0x57;
                    break;
                case 'X':
                    data[i] = 0x58;
                    break;
                case 'Y':
                    data[i] = 0x59;
                    break;
                case 'Z':
                    data[i] = 0x5A;
                    break;
                case 'Ä':
                    data[i] = 0x5B;
                    break;
                case 'Ö':
                    data[i] = 0x5C;
                    break;
                case 'Ü':
                    data[i] = 0x5E;
                    break;
                case '§':
                    data[i] = 0x5F;
                    break;
                case 'a':
                    data[i] = 0x61;
                    break;
                case 'b':
                    data[i] = 0x62;
                    break;
                case 'c':
                    data[i] = 0x63;
                    break;
                case 'd':
                    data[i] = 0x64;
                    break;
                case 'e':
                    data[i] = 0x65;
                    break;
                case 'f':
                    data[i] = 0x66;
                    break;
                case 'g':
                    data[i] = 0x67;
                    break;
                case 'h':
                    data[i] = 0x68;
                    break;
                case 'i':
                    data[i] = 0x69;
                    break;
                case 'j':
                    data[i] = 0x6A;
                    break;
                case 'k':
                    data[i] = 0x6B;
                    break;
                case 'l':
                    data[i] = 0x6C;
                    break;
                case 'm':
                    data[i] = 0x6D;
                    break;
                case 'n':
                    data[i] = 0x6E;
                    break;
                case 'o':
                    data[i] = 0x6F;
                    break;
                case 'p':
                    data[i] = 0x70;
                    break;
                case 'q':
                    data[i] = 0x71;
                    break;
                case 'r':
                    data[i] = 0x72;
                    break;
                case 's':
                    data[i] = 0x73;
                    break;
                case 't':
                    data[i] = 0x74;
                    break;
                case 'u':
                    data[i] = 0x75;
                    break;
                case 'v':
                    data[i] = 0x76;
                    break;
                case 'w':
                    data[i] = 0x77;
                    break;
                case 'x':
                    data[i] = 0x78;
                    break;
                case 'y':
                    data[i] = 0x79;
                    break;
                case 'z':
                    data[i] = 0x7A;
                    break;
                case 'ä':
                    data[i] = 0x7B;
                    break;
                case 'ö':
                    data[i] = 0x7C;
                    break;
                case 'ü':
                    data[i] = 0x7E;
                    break;
                default:
                    data[i] = '?';
                    break;  // found unknown character -> '?'
                //</editor-fold>
            }  //switch

        }  //for
        return data;
    }  //convertUnicode2GSM

    /**
     * Convert one GSM standard alphabet character into a Unicode character
     *
     * @param b one GSM standard alphabet character
     * @return one Unicode character
     */
    public static char convertGSM2Unicode(int b) {
        char c;

        if ((b >= 0x41) && (b <= 0x5A)) {    // character is between "A" and "Z"
            c = (char) b;
            return c;
        }  //if
        if ((b >= 0x61) && (b <= 0x7A)) {    // character is between "a" and "z"
            c = (char) b;
            return c;
        }  //if
        if ((b >= 0x30) && (b <= 0x39)) {    // character is between "0" and "9"
            c = (char) b;
            return c;
        }  //if

        switch (b) {
            case 0x00:
                c = '@';
                break;
            case 0x02:
                c = '$';
                break;
            case 0x0A:
                c = '\n';
                break;
            case 0x0D:
                c = '\r';
                break;
            case 0x11:
                c = '_';
                break;
            case 0x1E:
                c = 'ß';
                break;
            case 0x20:
                c = ' ';
                break;
            case 0x21:
                c = '!';
                break;
            case 0x22:
                c = '\"';
                break;
            case 0x23:
                c = '#';
                break;
            case 0x25:
                c = '%';
                break;
            case 0x26:
                c = '&';
                break;
            case 0x27:
                c = '\'';
                break;
            case 0x28:
                c = '(';
                break;
            case 0x29:
                c = ')';
                break;
            case 0x2A:
                c = '*';
                break;
            case 0x2B:
                c = '+';
                break;
            case 0x2C:
                c = ',';
                break;
            case 0x2D:
                c = '-';
                break;
            case 0x2E:
                c = '.';
                break;
            case 0x2F:
                c = '/';
                break;
            case 0x3A:
                c = ':';
                break;
            case 0x3B:
                c = ';';
                break;
            case 0x3C:
                c = '<';
                break;
            case 0x3D:
                c = '=';
                break;
            case 0x3E:
                c = '>';
                break;
            case 0x3F:
                c = '?';
                break;
            case 0x5B:
                c = 'Ä';
                break;
            case 0x5C:
                c = 'Ö';
                break;
            case 0x5E:
                c = 'Ü';
                break;
            case 0x5F:
                c = '§';
                break;
            case 0x7B:
                c = 'ä';
                break;
            case 0x7C:
                c = 'ö';
                break;
            case 0x7E:
                c = 'ü';
                break;
            default:
                c = ' ';
                break;
        }  //switch
        return c;
    }  //convertGSM2Unicode

    /**
     * Compress a readable text message into the GSM standard alphabet (1
     * character -> 7 bit data)
     *
     * @param data text string in Unicode
     * @return text string in GSM standard alphabet
     */
    public static byte[] compress(byte[] data) {
        int l;
        int n;  // length of compressed data
        byte[] comp;

        // calculate length of message
        l = data.length;
        n = (l * 7) / 8;
        if ((l * 7) % 8 != 0) {
            n++;
        } //if

        comp = new byte[n];
        int j = 0;   // index in data
        int s = 0;   // shift from next data byte
        for (int i = 0; i < n; i++) {
            comp[i] = (byte) ((data[j] & 0x7F) >>> s);
            s++;
            if (j + 1 < l) {
                comp[i] += (byte) ((data[j + 1] << (8 - s)) & 0xFF);
            }  //if
            if (s < 7) {
                j++;
            } //if
            else {
                s = 0;
                j += 2;
            }  //else
        } //for
        return comp;
    } //compress

    /**
     * Extracts from a given SMS the Text
     *
     * @param data SMS string
     * @return text date, time and SMS text string in Unicode specification of
     * the coding: TS 23.040 test pattern data:
     * 0791947101670000040C9194617011078800004010023112914016D737DB7C0EBBCF2069D8BD66B340CDBA3B3D4603
     * ADDRESS OF DELIVERING SMSC NUMBER IS : +491710760000 TYPE OF NR. : (0x10)
     * International NPI :	(0x01) ISDN/Telephone (E.164/163) MESSAGE HEADER
     * FLAGS (0x04) MESSAGE TYPE :	SMS DELIVER ORIGINATING ADDRESS NUMBER IS :
     * +491607117088 TYPE OF NR. : (0x10) International NPI : (0x01)
     * ISDN/Telephone (E.164/163) PROTOCOL IDENTIFIER (0x00) DATA CODING SCHEME
     * (0x00) COMPRESSION : OFF MESSAGE CLASS : NONE ALPHABET USED : 7 bit
     * default SMSC TIMESTAMP : 20.01.04 13:21:19 GMT+1,00 USER DATA PART OF SM
     * USER DATA LENGTH : 22 septets USER DATA (TEXT) : Wolfgang Rankl, Munich
     */
    public static String getSMSText(String data) {

        String s;
        String date;
        String time;
        String timezone;
        String orgnumber;
        boolean udhi = false;

        int i, x; //helper

        //<editor-fold defaultstate="collapsed" desc="smsc">
        s = data.substring(0, 2);          // get length [byte] of delivering SMSC number
        x = Integer.parseInt(s, 16);       // calculate length [byte] of delivering SMSC number
        s = data.substring(0, 2 + x * 2);      // get raw delivering SMSC number, this line is optional for debugging reasons
        i = 2 + x * 2;                     // set index to message header flags
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="pdu header">
        s = data.substring(i, i + 2);        // get message header flags
        Main.log("PDU Header: " + s);
        udhi = (Integer.parseInt(s, 16) & (1L << 6)) != 0;
        i = i + 2;                         // set index to length [digits] of originating adress
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="originating address number of digits">
        s = data.substring(i, i + 2);      // get length [digits] of originating adress
        x = Integer.parseInt(s, 16);       // calculate length [digits] of originating adress
        Main.log("digits: " + x);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="get originating address">
        s = data.substring(i + 3, i + 4);      // get type of number
        if (x % 2 != 0) {
            x++; //add padding for the address
        }
        if (s.compareTo("1") == 0) {       // it is a national (0x81) or  international (0x91) number
            s = data.substring(i, i + x + 4);//add 1 for padding
            Main.log("raworgnum: " + s);
            orgnumber = decodeAddressField(s);
        } // if
        else {                             // it is a unknown type of number
            s = data.substring(i, i + 2) + data.substring(i + 4, i + 4 + x);    // get raw originating adress, this line is optional for debugging reasons
            orgnumber = decodeAddressField(s);
        } // else
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="TP-DCS">
        i = i + 6 + x;   // set index to data coding scheme - 2 digits each for TP-PID, sender address digits and addresstype
        Main.log(data.subSequence(0, i));
        s = data.substring(i, i + 2);   // get raw data coding scheme, this line is optional for debugging reasons
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="timestamp">
        i = i + 2;                     // set index to date and time (= TP-Service-Centre-Time-Stamp (TP-SCTS))
        s = data.substring(i, i + 14);   // get raw date and time, this line is optional for debugging reasons

        date = s.substring(1, 2) + s.substring(0, 1);                 // get year
        date = s.substring(3, 4) + s.substring(2, 3) + "." + date;     // get month
        date = s.substring(5, 6) + s.substring(4, 5) + "." + date;     // get day

        time = s.substring(11, 12) + s.substring(10, 11);              // get hour
        time = s.substring(9, 10) + s.substring(8, 9) + ":" + time; // get minute
        time = s.substring(7, 8) + s.substring(6, 7) + ":" + time; // get second

        timezone = s.substring(13, 14) + s.substring(12, 13);          // get time zone
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="TP-UDL">
        i = i + 14;                                // set index to length of user data (=SMS)
        s = data.substring(i, i + 2);                // get length of user data (=SMS)
        x = Integer.parseInt(s, 16);               // calculate length [characters] of user data (=SMS)
        //</editor-fold>

        i = i + 2; //set index to TP-UD
        String tp_ud = data.substring(i, data.length());
        Main.log("tp_ud = " + tp_ud);

        //if udhi is 1 then extract udh
        int udl = 0;
        if (udhi) {

            udl = Integer.parseInt(tp_ud.substring(0, 2), 16);

            String udh = tp_ud.substring(2, 2 + udl * 2); //get the whole udh except udl
            //tp_ud = tp_ud.substring(2 + octetDigits); //crop udh and retain message.
            Main.log("udh = " + udh);
            int udh_i = 0;

            String iei = udh.substring(udh_i, udh_i + 2);
            Main.log("iei = " + iei);
            boolean bit_8 = iei.equals("00");
            boolean bit_16 = iei.equals("08");
            udh_i += 2; //after iei

            int udhl = Integer.parseInt(udh.substring(udh_i, udh_i + 2), 16);
            boolean valid = (bit_8 && udhl == 3) || (bit_16 && udhl == 4);
            if (!valid) {
                System.err.println("Invalid udhl+iei.");
                System.err.println(data);
                System.exit(0);
            }

            int csmsRefOctets = 1;
            if (bit_16) {
                csmsRefOctets = 2;
                Main.log("bit_16");
            } else {
                Main.log("bit_8");
            }

            udh_i += 2;

            String csmsRefNumRaw = udh.substring(udh_i, udh_i + csmsRefOctets);
            Main.log("csmsRefNumRaw = " + csmsRefNumRaw);
            int csmsRefNum = Integer.parseInt(csmsRefNumRaw, 16);
            Main.log("csmsRefNum = " + csmsRefNum);
            udh_i += 2 * csmsRefOctets; //csms is 2 octet str

            int parts = Integer.parseInt(udh.substring(udh_i, udh_i + 2), 16);
            Main.log("parts = " + parts);
            udh_i += 2;

            int seqN = Integer.parseInt(udh.substring(udh_i, udh_i + 2), 16);
            Main.log("seqN = " + seqN);

        }

        String smsTxt = extractUDStr(tp_ud);

        if (udl > 0) {
            int skip = udl
                    + 1 //udhl octet
                    + 1; //eigth octet created from incoding 7bit text to 8bit
            Main.log("skip = " + skip);
            byte[] bytes = smsTxt.getBytes();
            byte[] newBytes = new byte[bytes.length - (skip)];

            for (i = 0; i < newBytes.length; i++) {
                newBytes[i] = bytes[i + skip];
            }

            smsTxt = new String(newBytes);
        }

        data = orgnumber + " " + date + " " + time + " +" + timezone + " " + SMSTEXTSIGN + smsTxt;

        return data;
    }  //getSMSText

    public static RawSMS getRawSMS(String data) {

        data = data.trim();

        String s;
        String date;
        String time;
        String timezone;
        String orgnumber;
        int i, x; //helper

        //<editor-fold defaultstate="collapsed" desc="smsc">
        s = data.substring(0, 2);          // get length [byte] of delivering SMSC number
        x = Integer.parseInt(s, 16);       // calculate length [byte] of delivering SMSC number
        s = data.substring(0, 2 + x * 2);      // get raw delivering SMSC number, this line is optional for debugging reasons
        i = 2 + x * 2;                     // set index to message header flags
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="pdu header">
        s = data.substring(i, i + 2);        // get message header flags

        boolean udhi = (Integer.parseInt(s, 16) & (1L << 6)) != 0;
        i = i + 2;                         // set index to length [digits] of originating adress
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="originating address number of digits">
        s = data.substring(i, i + 2);      // get length [digits] of originating adress
        x = Integer.parseInt(s, 16);       // calculate length [digits] of originating adress
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="get originating address">
        s = data.substring(i + 3, i + 4);      // get type of number
        if (x % 2 != 0) {
            x++; //add padding for the address
        }
        if (s.compareTo("1") == 0) {       // it is a national (0x81) or  international (0x91) number
            s = data.substring(i, i + x + 4);//add 1 for padding
            orgnumber = decodeAddressField(s);
        } // if
        else {                             // it is a unknown type of number
            s = data.substring(i, i + 2) + data.substring(i + 4, i + 4 + x);    // get raw originating adress, this line is optional for debugging reasons
            orgnumber = decodeAddressField(s);
        } // else
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="TP-DCS">
        i = i + 6 + x;   // set index to data coding scheme - 2 digits each for TP-PID, sender address digits and addresstype
        Main.log(data.subSequence(0, i));
        s = data.substring(i, i + 2);   // get raw data coding scheme, this line is optional for debugging reasons
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="timestamp">
        i = i + 2;                     // set index to date and time (= TP-Service-Centre-Time-Stamp (TP-SCTS))
        s = data.substring(i, i + 14);   // get raw date and time, this line is optional for debugging reasons
        String rawTimeStamp = s;

        date = s.substring(1, 2) + s.substring(0, 1);                 // get year
        date = s.substring(3, 4) + s.substring(2, 3) + "." + date;     // get month
        date = s.substring(5, 6) + s.substring(4, 5) + "." + date;     // get day

        String sec = s.substring(11, 12) + s.substring(10, 11);              // get hour
        String min = s.substring(9, 10) + s.substring(8, 9); // get minute
        String hr = s.substring(7, 8) + s.substring(6, 7); // get second

        time = hr + ":" + min + ":" + sec;
        timezone = s.substring(13, 14) + s.substring(12, 13);          // get time zone

        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="TP-UDL">
        i = i + 14;                                // set index to length of user data (=SMS)
        s = data.substring(i, i + 2);                // get length of user data (=SMS)
        x = Integer.parseInt(s, 16);               // calculate length [characters] of user data (=SMS)
        //</editor-fold>

        i = i + 2; //set index to TP-UD
        String tp_ud = data.substring(i, data.length());

        //if udhi is 1 then extract udh
        int udl = 0;
        String csmsRefNumRaw = null;
        int multiParts = 0;
        int multiPartNum = 0;
        if (udhi) {

            //<editor-fold defaultstate="collapsed" desc="udhi_procedure">
            udl = Integer.parseInt(tp_ud.substring(0, 2), 16);

            String udh = tp_ud.substring(2, 2 + udl * 2); //get the whole udh except udl
            int udh_i = 0;

            String iei = udh.substring(udh_i, udh_i + 2);

            boolean bit_8 = iei.equals("00");
            boolean bit_16 = iei.equals("08");
            udh_i += 2; //after iei

            int udhl = Integer.parseInt(udh.substring(udh_i, udh_i + 2), 16);
            boolean valid = (bit_8 && udhl == 3) || (bit_16 && udhl == 4);
            if (!valid) {
                System.err.println("Error: invalid udhl");
            }

            int csmsRefOctets = 1;
            if (bit_16) {
                csmsRefOctets = 2;
                //Main.log("bit_16");
            } else {
                //Main.log("bit_8");
            }

            udh_i += 2;

            csmsRefNumRaw = udh.substring(udh_i, udh_i + csmsRefOctets);

            udh_i += 2 * csmsRefOctets; //csms is 2 octet str

            multiParts = Integer.parseInt(udh.substring(udh_i, udh_i + 2), 16);

            udh_i += 2;

            multiPartNum = Integer.parseInt(udh.substring(udh_i, udh_i + 2), 16);

            //</editor-fold>
        }

        String smsTxt = extractUDStr(tp_ud);

        if (udl > 0) {
            int skip = udl
                    + 1 //udhl octet
                    + 1; //eigth octet created from incoding 7bit text to 8bit
            byte[] bytes = smsTxt.getBytes();
            byte[] newBytes = new byte[bytes.length - (skip)];

            for (i = 0; i < newBytes.length; i++) {
                newBytes[i] = bytes[i + skip];
            }

            smsTxt = new String(newBytes);
        }

        RawSMS r = new RawSMS();

        orgnumber = RawSMSService.extractEssentialMobileNum(orgnumber);
        
        r.setHeader(rawTimeStamp + "" + orgnumber);
        r.setMessage(smsTxt.replaceAll("@", ""));//the algorithm is imperfect. still prints '@' at the end sometimes.

        r.setSender(orgnumber);
        r.setSubmitError(false);

        if (udhi) {
            r.setMultipart(udhi);
            r.setMultipartId(csmsRefNumRaw);
            r.setMulti_parts(multiParts);
            r.setMulti_partNum(multiPartNum);
            r.setProcessStatus(RawSMS.PROC_STAT_INCOMPLETE);
        } else {
            r.setProcessStatus(RawSMS.PROC_STAT_UNPROCESSED);
        }

        try {

            String dateparse = date + " " + time;
            Date parsedDate = dateFormatter.parse(dateparse);
            r.setReceivedDate(parsedDate);

        } catch (ParseException e) {
            e.printStackTrace();
            r.setReceivedDate(new Date());
        }

        return r;
    }

    public static String extractUDStr(String data) {
        String s;
        byte sms[] = new byte[data.length() / 2];
        for (int n = 0; n < data.length() / 2; n++) {
            s = data.substring(n * 2, n * 2 + 2);
            sms[n] = (byte) (0x000000FF & Integer.parseInt(s, 16));
        }  //for

        return expand(sms);
    }

    /**
     * Expands a compressed GSM message in a readable text message (7 bit data
     * -> 1 character)
     *
     * @param indata text string in GSM standard alphabet
     * @return text string in Unicode
     */
    public static String expand(byte[] indata) {
        int x, n, y, Bytebefore, Bitshift;
        String msg = new String("");
        byte data[] = new byte[indata.length + 1];

        for (n = 1; n < data.length; n++) {
            data[n] = indata[n - 1];
        } // for

        Bytebefore = 0;
        for (n = 1; n < data.length; n++) {
            x = (int) (0x000000FF & data[n]);   // get a byte from the SMS
            Bitshift = (n - 1) % 7;               // calculate number of neccssary bit shifts
            y = x;
            y = y << Bitshift;                  // shift to get a conversion 7 bit compact GSM -> Unicode
            y = y | Bytebefore;                 // add bits from the byte before this byte
            y = y & 0x0000007F;                 // delete all bits except bit 7 ... 1 of the byte
            msg = msg + convertGSM2Unicode(y);  // conversion: 7 bit GSM character -> Unicode
            if (Bitshift == 6) {
                Bitshift = 1;
                y = x;
                y = y >>> Bitshift;                 // shift to get a conversion 7 bit compact GSM -> Unicode
                y = y & 0x0000007F;                 // delete all bits except bit 7 ... 1 of the byte
                msg = msg + convertGSM2Unicode(y);  // conversion: 7 bit GSM character -> Unicode
                Bytebefore = 0;
            } else {
                Bytebefore = x;
                Bitshift = 7 - Bitshift;
                Bytebefore = Bytebefore >>> Bitshift;  // shift to get a conversion 7 bit compact GSM -> Unicode
                Bytebefore = Bytebefore & 0x000000FF;  // mask for one byte
            }  //else
        }  //for
        return msg;
    }  //expand

    /**
     * Convert data into a hex string
     *
     * @param data to convert
     * @return in hex string converted data
     */
    public static char[] toHexString(byte[] data) {
        int l = data.length;
        char[] hex = new char[2 * l];

        int j = 0; // index in hex
        for (int i = 0; i < data.length; i++) {
            switch (data[i] & 0xF0) {
                case 0x00:
                    hex[j] = '0';
                    break;
                case 0x10:
                    hex[j] = '1';
                    break;
                case 0x20:
                    hex[j] = '2';
                    break;
                case 0x30:
                    hex[j] = '3';
                    break;
                case 0x40:
                    hex[j] = '4';
                    break;
                case 0x50:
                    hex[j] = '5';
                    break;
                case 0x60:
                    hex[j] = '6';
                    break;
                case 0x70:
                    hex[j] = '7';
                    break;
                case 0x80:
                    hex[j] = '8';
                    break;
                case 0x90:
                    hex[j] = '9';
                    break;
                case 0xA0:
                    hex[j] = 'A';
                    break;
                case 0xB0:
                    hex[j] = 'B';
                    break;
                case 0xC0:
                    hex[j] = 'C';
                    break;
                case 0xD0:
                    hex[j] = 'D';
                    break;
                case 0xE0:
                    hex[j] = 'E';
                    break;
                case 0xF0:
                    hex[j] = 'F';
                    break;
            } //switch
            j++;
            switch (data[i] & 0x0F) {
                case 0x00:
                    hex[j] = '0';
                    break;
                case 0x01:
                    hex[j] = '1';
                    break;
                case 0x02:
                    hex[j] = '2';
                    break;
                case 0x03:
                    hex[j] = '3';
                    break;
                case 0x04:
                    hex[j] = '4';
                    break;
                case 0x05:
                    hex[j] = '5';
                    break;
                case 0x06:
                    hex[j] = '6';
                    break;
                case 0x07:
                    hex[j] = '7';
                    break;
                case 0x08:
                    hex[j] = '8';
                    break;
                case 0x09:
                    hex[j] = '9';
                    break;
                case 0x0A:
                    hex[j] = 'A';
                    break;
                case 0x0B:
                    hex[j] = 'B';
                    break;
                case 0x0C:
                    hex[j] = 'C';
                    break;
                case 0x0D:
                    hex[j] = 'D';
                    break;
                case 0x0E:
                    hex[j] = 'E';
                    break;
                case 0x0F:
                    hex[j] = 'F';
                    break;
            } //switch
            j++;
        }  //for
        return hex;
    } //toHexString

} //SMSTools

