/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.ProntonTransactionDetail;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import com.jps.l2app.utils.UIUtilities;
import com.jps.l2app.utils.FormatUtils;

/**
 *
 * @author org org
 */
public class ProntonTransactionDetailService extends AbstractEntityService<ProntonTransactionDetail> {

    private static ProntonTransactionDetailService instance;

    public static ProntonTransactionDetailService getInstance() {
        if (instance == null) {
            instance = new ProntonTransactionDetailService();

        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return ProntonTransactionDetail.class;
    }

    private String getSortedInstance(String str) {
        // put the characters into an array
        Character[] chars = new Character[str.length()];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = str.charAt(i);
        }

// sort the array
        Arrays.sort(chars, new Comparator<Character>() {
            public int compare(Character c1, Character c2) {
                int cmp = Character.compare(
                        Character.toLowerCase(c1.charValue()),
                        Character.toLowerCase(c2.charValue())
                );
                if (cmp != 0) {
                    return cmp;
                }
                return Character.compare(c1.charValue(), c2.charValue());
            }
        });

// rebuild the string
        StringBuilder sb = new StringBuilder(chars.length);
        for (char c : chars) {
            sb.append(c);
        }
        str = sb.toString();
        return str;
    }

    private final static SimpleDateFormat startFormatter = new SimpleDateFormat("dd-MMM-yyyy");

    public List<ProntonTransactionDetail> search(String draw, String raffle, int type, String key, Date d) {

        try {
            d = startFormatter.parse(startFormatter.format(d));
        } catch (ParseException ex) {
            Logger.getLogger(ProntonTransactionDetailService.class.getName()).log(Level.SEVERE, null, ex);
        }

        EntityManager m = getNewEntityManager();

        key = key.trim();

        if (draw.equals("ALL")) {
            draw = "%%";
        }

        if (raffle.equals("ALL")) {
            raffle = "%%";
        }

        if (type == 1) {

            try {
                int id = Integer.parseInt(key, 36);
                List<ProntonTransactionDetail> rs = m.createQuery("SELECT c FROM ProntonTransactionDetail c WHERE c.id = ?3", ProntonTransactionDetail.class)
                        .setParameter(3, id).getResultList();
                return rs;

            } catch (NumberFormatException e) {
                UIUtilities.addWarningMessage("Invalid ID number");
                return new ArrayList();
            }

        }

        List<ProntonTransactionDetail> rs = m.createQuery("SELECT c FROM ProntonTransactionDetail c "
                + "WHERE c.draw LIKE ?1 "
                + "AND c.raffle LIKE ?2 AND c.raffle NOT LIKE '%r%' "
                + "AND c.raffleNumber LIKE ?3 "
                + "AND c.transaction.transactionTimeStamp > ?4", ProntonTransactionDetail.class)
                .setParameter(1, "%" + draw + "%")
                .setParameter(2, raffle)
                .setParameter(3, "%" + key + "%")
                .setParameter(4, d)
                .getResultList();

        List<ProntonTransactionDetail> rs1 = m.createQuery("SELECT c FROM ProntonTransactionDetail c "
                + "WHERE c.draw LIKE ?1 "
                + "AND c.raffle LIKE ?2 "
                + "AND c.raffle LIKE '%r%' "
                + "AND c.transaction.transactionTimeStamp > ?3", ProntonTransactionDetail.class)
                .setParameter(1, "%" + draw + "%")
                .setParameter(2, raffle)
                .setParameter(3, d)
                .getResultList();

        if (key.isEmpty()) {
            rs.addAll(rs1);
        } else {

            String sortedKey = getSortedInstance(key).toLowerCase();
            List<ProntonTransactionDetail> toAdd = new ArrayList<>();
            for (ProntonTransactionDetail det : rs1) {
                if (getSortedInstance(det.getRaffleNumber()).toLowerCase().equals(sortedKey)) {
                    toAdd.add(det);
                }
            }

            rs.addAll(toAdd);

        }

        return rs;

    }

    public List<String> getRaffleNumbers(Date d, String draw, String raffle) {
        EntityManager m = getNewEntityManager();

        Date low = FormatUtils.getDayStart(new Date());
        Date high = FormatUtils.getDayEnd(new Date());

        List<String> raffleNumbers = m.createQuery("SELECT DISTINCT(c.raffleNumber) FROM ProntonTransactionDetail c WHERE c.raffle LIKE ?1 AND c.transaction.draw LIKE ?2 AND (c.transaction.transactionTimeStamp > ?3 AND c.transaction.transactionTimeStamp < ?4) ORDER BY c.raffleNumber", String.class)
                .setParameter(1, raffle)
                .setParameter(2, draw)
                .setParameter(3, low)
                .setParameter(4, high).getResultList();
        m.close();
        return raffleNumbers;
    }

    public double getRaffleNumberTotal(Date d, String draw, String raffle, String raffleNumber) {

        SimpleDateFormat f1 = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat f2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");

        Date low = new Date();
        Date high = new Date();

        try {
            String orig = f1.format(d);
            low = f1.parse(orig);

            high = f2.parse(orig + " 23:59:59");

        } catch (ParseException ex) {
            Logger.getLogger(ProntonTransactionDetailService.class.getName()).log(Level.SEVERE, null, ex);
        }

        EntityManager m = getNewEntityManager();
        Double singleResult = m.createQuery("SELECT SUM(c.bet) FROM ProntonTransactionDetail c WHERE c.raffle LIKE ?1 AND c.transaction.draw LIKE ?2 AND (c.transaction.transactionTimeStamp > ?3 AND c.transaction.transactionTimeStamp < ?4) AND c.raffleNumber LIKE ?5", Double.class)
                .setParameter(1, raffle)
                .setParameter(2, draw)
                .setParameter(3, low)
                .setParameter(4, high)
                .setParameter(5, raffleNumber)
                .getSingleResult();
        m.close();

        return singleResult;

    }

}
