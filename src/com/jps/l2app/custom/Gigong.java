
package com.jps.l2app.custom;

import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;

/**
 *
 * @author org org
 */
public class Gigong {

    public static void main(String[] args) throws InterruptedException {

        Setup.businessName = "Gigong";
        Setup.allowPrinterSetup = false;
        Setup.checkDevice = true;
        Setup.users = 20;

        Main.main(args);
        //SysPronton.main(args);
    }

}
