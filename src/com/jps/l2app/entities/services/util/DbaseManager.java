/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services.util;

import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.jps.l2app.utils.UIUtilities;

public class DbaseManager {

    private static final String PASS_FILE_NAME = ".ini";

    private static EntityManagerFactory factory;

    private static EntityManagerFactory getEntityManagerFactory() {

        if (factory == null) {
            HashMap p = new HashMap();
            String password = "root";
            try {
                password = getRootPassword();
            } catch (IOException ex) {
                Logger.getLogger(DbaseManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            p.put("javax.persistence.jdbc.password", password);
            p.put("javax.persistence.jdbc.user", Setup.dbaseUser);
            p.put("javax.persistence.jdbc.url", "jdbc:mysql://" + Setup.dbaseHost + ":3306/l2app_schema?zeroDateTimeBehavior=convertToNull");

            factory = Persistence.createEntityManagerFactory("l2appPU", p);
            initDatabase();
        }
        return factory;
    }

    public static EntityManager getNewEntityManager() {
        try {
            return getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
            UIUtilities.addWarningMessage("Could not connect to database!");
            factory = null;
            resetPassword();
            return getNewEntityManager();
        }
    }

    private static void initDatabase() {
        System.out.print("Initializing Database Records...");
        repairTableScript();
        Main.log("Done.");
    }

    private static String getRootPassword() throws IOException {
        File passf = new File(PASS_FILE_NAME);
        String pass;
        if (!passf.exists()) {
            passf.createNewFile();

            String message = "Please enter root password.";
            pass = UIUtilities.getSingleInput(message);
            if (pass == null) {
                System.exit(0);
            }

            pass = EncryptUtils.encrypt(pass);

            try (FileWriter writer = new FileWriter(passf)) {
                writer.append(pass);
                writer.flush();
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(passf))) {
            pass = br.readLine();
        }

        pass = EncryptUtils.decrypt(pass);

        return pass;
    }

    private static void resetPassword() {
        File passf = new File(PASS_FILE_NAME);
        passf.delete();
    }

    private static void repairTableScript() {
        try {
            EntityManager m = getEntityManagerFactory().createEntityManager();
            m.createNativeQuery("repair table administrator").getSingleResult();
            m.createNativeQuery("repair table betlimit").getSingleResult();
            m.createNativeQuery("repair table employee").getSingleResult();
            m.createNativeQuery("repair table employeetotals").getSingleResult();
            m.createNativeQuery("repair table globallimit").getSingleResult();
            m.createNativeQuery("repair table prontontransaction").getSingleResult();
            m.createNativeQuery("repair table prontontransactiondetail").getSingleResult();
            m.createNativeQuery("repair table queuedresponse").getSingleResult();
            m.createNativeQuery("repair table raffledigittotal").getSingleResult();
            m.createNativeQuery("repair table rafflewin").getSingleResult();
            m.createNativeQuery("repair table rawsms").getSingleResult();
            m.createNativeQuery("repair table sequence").getSingleResult();
            m.createNativeQuery("repair table settings").getSingleResult();
            m.createNativeQuery("repair table submission").getSingleResult();
            m.createNativeQuery("repair table submissionraw").getSingleResult();
            m.close();
        } catch (Exception e) {
        }
    }

}
