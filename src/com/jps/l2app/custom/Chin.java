
package com.jps.l2app.custom;

import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;
   
/**
 *
 * @author org org
 */
public class Chin {

    public static void main(String[] args) throws InterruptedException {

        Setup.businessName = "Chin";
        Setup.allowPrinterSetup = false;
        Setup.checkDevice = false;
        Setup.users = 40;
        Setup.s3Only = false;
        Setup.allowClient = false;
        //Setup.systemType = SystemType.CLIENT;
        Main.main(args);
        //SysPronton.main(args);
    }

}
