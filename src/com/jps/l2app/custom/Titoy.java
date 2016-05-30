
package com.jps.l2app.custom;

import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;

/**
 *
 * @author org org
 */
public class Titoy {

    public static void main(String[] args) throws InterruptedException {

        Setup.businessName = "TIYOY";
        Setup.allowPrinterSetup = false;
        Setup.checkDevice = true;
        Setup.users = 45;

        Main.main(args);
        //SysPronton.main(args);
    }

}
