
package com.jps.l2app.custom;

import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;

/**
 *
 * @author org org
 */
public class Magtuba {

    public static void main(String[] args) throws InterruptedException {

        Setup.businessName = "Magtuba";
        Setup.allowPrinterSetup = false;
        Setup.checkDevice = true;
        Setup.users = 40;
        Setup.allowClient = false;

        Main.main(args);
        //SysPronton.main(args);
    }

}
