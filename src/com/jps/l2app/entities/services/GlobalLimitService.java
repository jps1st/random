/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.ForwardFinancer;
import com.jps.l2app.entities.GlobalLimit;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.main.Main;
import com.jps.l2app.utils.FormatUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;

/**
 *
 * @author org org
 */
public class GlobalLimitService extends AbstractEntityService<GlobalLimit> {

    private static GlobalLimitService instance;

    public static GlobalLimitService getInstance() {
        if (instance == null) {
            instance = new GlobalLimitService();
        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return GlobalLimit.class;
    }

    public List<GlobalLimit> getAll() {
        EntityManager m = getNewEntityManager();
        List rs = m.createQuery("SELECT c FROM GlobalLimit c").getResultList();
        m.close();
        return rs;
    }

    public List<GlobalLimit> getAll(String raffleType, int draw) {
        EntityManager m = getNewEntityManager();
        List rs = m.createQuery("SELECT c FROM GlobalLimit c WHERE c.raffle LIKE ?1 AND c.draw = ?2 AND c.financer IS NULL ORDER BY c.raffleNumber")
                .setParameter(1, raffleType)
                .setParameter(2, draw)
                .getResultList();
        m.close();
        return rs;
    }

    public List<GlobalLimit> getAll(String raffleType, int draw, ForwardFinancer f) {
        EntityManager m = getNewEntityManager();
        List rs = m.createQuery("SELECT c FROM GlobalLimit c WHERE c.raffle LIKE ?1 AND c.draw = ?2 AND c.financer = ?3 ORDER BY c.raffleNumber")
                .setParameter(1, raffleType)
                .setParameter(2, draw)
                .setParameter(3, f)
                .getResultList();
        m.close();
        return rs;
    }

    public GlobalLimit getLimit(int draw, String raffle, String digit, ForwardFinancer f) {
        EntityManager m = getNewEntityManager();
        List<GlobalLimit> rs;

        if (f == null) {
            rs = m.createQuery("SELECT c FROM GlobalLimit c WHERE c.financer IS NULL AND c.draw = ?1 AND c.raffle LIKE ?2 AND c.raffleNumber LIKE ?3", GlobalLimit.class)
                    .setParameter(1, draw).setParameter(2, raffle).setParameter(3, digit).getResultList();
        } else {
            rs = m.createQuery("SELECT c FROM GlobalLimit c WHERE c.financer = ?1 AND c.draw = ?2 AND c.raffle LIKE ?3 AND c.raffleNumber LIKE ?4", GlobalLimit.class)
                    .setParameter(1, f).setParameter(2, draw).setParameter(3, raffle).setParameter(4, digit).getResultList();
        }

        if (rs.isEmpty()) {
            return null;
        }
        return rs.get(0);
    }

    public GlobalLimit check(String draw, String raffle, String raffleNumber, ForwardFinancer f) {

        int drawi = 0;

        if (draw.contains("1")) {
            drawi = 1;
        } else if (draw.contains("2")) {
            drawi = 2;
        } else if (draw.contains("3")) {
            drawi = 3;
        }

        return check(drawi, raffle, raffleNumber, f);

    }

    public GlobalLimit check(int draw, String raffle, String raffleNumber, ForwardFinancer f) {

        EntityManager m = getNewEntityManager();
        List<GlobalLimit> rs;

        if (f == null) {
            rs = m.createQuery("SELECT c FROM GlobalLimit c WHERE c.financer IS NULL AND c.draw = ?1 AND c.raffle LIKE ?2", GlobalLimit.class)
                    .setParameter(1, draw).setParameter(2, raffle).getResultList();
        } else {
            rs = m.createQuery("SELECT c FROM GlobalLimit c WHERE c.financer = ?1 AND c.draw = ?2 AND c.raffle LIKE ?3", GlobalLimit.class)
                    .setParameter(1, f).setParameter(2, draw).setParameter(3, raffle).getResultList();
        }

        if (rs.isEmpty()) {
            if (draw > 0) {
                return check(0, raffle, raffleNumber, f);
            } else {
                return null;
            }
        }

        List<String> numbers = new ArrayList<>();
        if (raffle.equalsIgnoreCase("s3r")) {
            numbers.addAll(FormatUtils.rambolito(raffleNumber));
        } else {
            numbers.add(raffleNumber);
        }

        for (String number : numbers) {
            GlobalLimit all = null;
            for (GlobalLimit r : rs) {
                String limitN = r.getRaffleNumber();
                if (limitN.equalsIgnoreCase(number)) {
                    return r;
                } else if (limitN.startsWith("%") && number.endsWith(limitN.replaceAll("%", ""))) {
                    return r;
                } else if (limitN.endsWith("%") && number.startsWith(limitN.replaceAll("%", ""))) {
                    return r;
                } else if (limitN.contains("-")) {

                    String[] split = limitN.split("-");
                    double low = Double.parseDouble(split[0]);
                    double high = Double.parseDouble(split[1]);

                    long rafNum = Long.parseLong(number);
                    if (rafNum >= low && rafNum <= high) {
                        return r;
                    }

                } else if (limitN.equalsIgnoreCase("all")) {
                    all = r;
                }
            }

            if (all != null) {
                return all;
            }

        }

        if (draw > 0) {
            return check(0, raffle, raffleNumber, f);
        }

        return null;

    }

    public GlobalLimit checkExistence(String draw, String raffle, String raffleNumber) {
        int d = Integer.parseInt(draw.substring(1));
        Main.log("checkExistence = d" + d);
        return checkExistence(d, raffle, raffleNumber);
    }

    public GlobalLimit checkExistence(int draw, String raffle, String raffleNumber) {
        EntityManager m = getNewEntityManager();
        List<GlobalLimit> rs;

        if (raffle.toLowerCase().matches("s3r")) {

            rs = m.createQuery("SELECT c FROM GlobalLimit c WHERE c.draw = ?3 AND c.raffle LIKE ?1 AND c.raffleNumber IN ?2", GlobalLimit.class)
                    .setParameter(1, raffle)
                    .setParameter(2, FormatUtils.rambolito(raffleNumber))
                    .setParameter(3, draw)
                    .getResultList();

        } else {
            rs = m.createQuery("SELECT c FROM GlobalLimit c WHERE c.draw = ?3 AND c.raffle LIKE ?1 AND c.raffleNumber LIKE ?2")
                    .setParameter(1, raffle)
                    .setParameter(2, raffleNumber)
                    .setParameter(3, draw)
                    .getResultList();
        }

        m.close();

        if (rs.isEmpty()) {
            return null;
        }

        return rs.get(0);
    }

    public double getTodaysTotal(String draw, String raffle, String raffleNumber) {

        EntityManager m = getNewEntityManager();
        Date date = FormatUtils.getPlainDate(new Date());
        boolean rambolito = raffle.toLowerCase().matches("s3r");
        try {

            Double total;

            if (!rambolito) {
                total = m.createQuery("SELECT SUM(c.bet) FROM ProntonTransactionDetail c WHERE c.draw LIKE ?1 AND c.raffle LIKE ?2 AND c.transaction.transactionTimeStamp > ?3 AND c.raffleNumber LIKE ?4", Double.class)
                        .setParameter(1, draw)
                        .setParameter(2, raffle)
                        .setParameter(3, date)
                        .setParameter(4, raffleNumber)
                        .getSingleResult();
            } else {
                Set<String> rambolitos = rambolito ? FormatUtils.rambolito(raffleNumber) : new HashSet();
                total = m.createQuery("SELECT SUM(c.bet) FROM ProntonTransactionDetail c WHERE c.draw LIKE ?1 AND c.raffle LIKE ?2 AND c.transaction.transactionTimeStamp > ?3 AND c.raffleNumber IN ?4", Double.class)
                        .setParameter(1, draw)
                        .setParameter(2, raffle)
                        .setParameter(3, date)
                        .setParameter(4, rambolitos)
                        .getSingleResult();
            }

            m.close();
            return total;
        } catch (Exception e) {
        }
        return 0d;
    }

}
