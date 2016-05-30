package com.jps.l2app.main;

import java.util.ArrayList;
import java.util.List;

public class Setup {
    
    public static final String VERSION = "2016.01";
    
    public static String dbaseUser = "root";
    public static String dbaseHost = "localhost";
    
    public static final String FONT_NAME = "Lucida Sans Unicode";
    public static int fontSize = 14; 
    
    public static String businessName = null;

    public static boolean allowPrinterSetup = true;
    public static boolean checkDevice = false;
    public static int users = 0;

    public static boolean s3Only = false;
    public static boolean master_mode = false;
    public static boolean allowClient;

    public static final List<String> DEV_IDS = new ArrayList();
    
    public static SystemType systemType;
    public static boolean diag = false;
    
    static void registerIds() {
        //bucol
        add("012207003963276");
        add("867622013741262");
        add("867622013761450");
        add("867622013763142");
        add("867622013757755");
        add("867622013763613");
        add("867622013738904");
        add("867622013165710");
        add("867622013746733");
        add("867622013748507");
        add("867622013762847");
        add("867622013750115");
        add("867622013745818");
        add("867622013760437");
        add("867622013748630");
        add("867622013026599");
        add("867622013164598");
        add("867622013762664");
        add("867622013749000");
        //nm
        add("867622013166023");
        add("867622013757839");
        add("867622013756484");
        add("867622012795582");
        //lr
        add("500102112125350");//LR stations
        add("867622013749323");
        //neto
        add("353143030803481");
        add("867622013746113");
        add("867622013750735");
        //gigong
        add("867622013024842");
        add("867622013024545");
        //
        add("866762028372496");
        add("865904028446620");
        add("866762028404885");
        add("866762028372785");
        add("866762028358362");
        add("865904028546312");
        
        add("012207000287786");
        add("865904028504956");
        
        add("012207002825799");
        add("29f1f02c-793e-4e62-b868-1c169a8feb50");
        
        //john
        add("867273023440139");
        add("867273023453249");
        
        //bucol
        add("867273023448728");
        add("860547001256162");

    }

    public static void add(String string) {
        DEV_IDS.add(string);
    }

}
