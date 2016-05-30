/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author org org
 */
public class MyStyle {

    private static double assign(double amount, List<Level> levels) {

        for (Level level : levels) {
            double diff = amount - level.deduction;
            if (diff >= 0) {
                amount = diff;
                level.financer.total += level.deduction;
            } else {
                level.financer.total += amount;
                return 0;
            }
        }
        return amount;
    }

    static class Level {

        double deduction = 0d;
        Financer financer;

        public Level(double deduction, Financer financer) {
            this.financer = financer;
            this.deduction = deduction;
        }

    }

    static class Financer {
        double total = 0d;
    }

    public static double retaineds3(double amount) {
        Financer casas = new Financer();
        Financer neto = new Financer();

        List<Level> levels = new ArrayList();

        levels.add(new Level(5, casas));
        levels.add(new Level(5, neto));

        assign(amount, levels);
        return neto.total;
        
    }

    public static double retainedL2(double amount) {
        Financer casas = new Financer();
        Financer neto = new Financer();

        List<Level> levels = new ArrayList();
        levels.add(new Level(15, casas));
        levels.add(new Level(10, neto));
        levels.add(new Level(10, casas));
        levels.add(new Level(10, neto));

        //levels.add(new Level(5, casas));
        //levels.add(new Level(5, neto));

        //double remaining = 
        assign(amount, levels);
        //casas.total += remaining;

        return neto.total;
        //Main.consoleLog("casas: " + casas.total);
        //Main.consoleLog("neto:" + neto.total);
    }

}
