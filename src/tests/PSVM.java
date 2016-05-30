/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

/**
 *
 * @author admin
 */
public class PSVM {

    public static void main(String[] args) {
        String tx = "215";
        int min = 2;

     

        String rn = tx.substring(0, min );
        System.out.println("rn = " + rn);
        String betstr = tx.substring(min, tx.length() );
        System.out.println("betstr = " + betstr);
        double bet = Double.parseDouble(betstr);
    }

}
