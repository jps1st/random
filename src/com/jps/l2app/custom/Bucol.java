package com.jps.l2app.custom;

import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;

/**
 *
 * @author org org
 */
public class Bucol {

    public static void main(String[] args) throws InterruptedException {

        Setup.businessName = "BN";
        Setup.allowPrinterSetup = true;
        Setup.checkDevice = false;
        Setup.users = 40;
        Setup.s3Only = false;
        Setup.allowClient = true;
    
        Main.main(args);

    }

}
