/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.ProntonTransaction;
import com.jps.l2app.entities.ProntonTransactionDetail;
import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.main.Main;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import com.jps.l2app.utils.UIUtilities;

/**
 *
 * @author org org
 */
public class ProntonTransactionService extends AbstractEntityService<ProntonTransaction> {

    private static ProntonTransactionService instance;

    public static ProntonTransactionService getInstance() {
        if (instance == null) {
            instance = new ProntonTransactionService();

        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return ProntonTransaction.class;
    }

    private final static SimpleDateFormat startFormatter = new SimpleDateFormat("dd-MMM-yyyy");

    public List<ProntonTransaction> search(String text, Date parse) {

        try {
            parse = startFormatter.parse(startFormatter.format(parse));
        } catch (ParseException ex) {
            Logger.getLogger(ProntonTransactionDetailService.class.getName()).log(Level.SEVERE, null, ex);
        }

        EntityManager m = getNewEntityManager();

        try {
            int parseInt = Integer.parseInt(text);
            List<ProntonTransaction> rs = m.createQuery("SELECT c FROM ProntonTransaction c WHERE c.transactionTimeStamp > ?2 AND c.id = ?1 ORDER BY c.id", ProntonTransaction.class)
                    .setParameter(1, parseInt)
                    .setParameter(2, parse)
                    .getResultList();

            m.close();
            return rs;
        } catch (NumberFormatException e) {
        }

        List<ProntonTransaction> rs = m.createQuery("SELECT c FROM ProntonTransaction c WHERE c.transactionTimeStamp > ?2 AND c.draw LIKE ?1 ORDER BY c.id", ProntonTransaction.class)
                .setParameter(2, parse)
                .setParameter(1, "%" + text.trim() + "%").getResultList();

        m.close();
        return rs;
    }

    public List<ProntonTransactionDetail> getDetails(int id) {
        EntityManager m = getNewEntityManager();
        List<ProntonTransactionDetail> rs = m.createQuery("SELECT c FROM ProntonTransactionDetail c WHERE c.transaction.id = ?1 ORDER BY c.id", ProntonTransactionDetail.class).setParameter(1, id).getResultList();
        m.close();
        return rs;
    }

    public String createSubmitText(ProntonTransaction transaction) {
        EntityManager m = getNewEntityManager();
        List<ProntonTransactionDetail> resultList = m.createQuery("SELECT c FROM ProntonTransactionDetail c WHERE c.transaction.id = ?1 ORDER BY c.raffle", ProntonTransactionDetail.class).setParameter(1, transaction.getId()).getResultList();
        m.close();

        String whole = transaction.getDraw() + ".T" + transaction.getId() + ".";

        String currentRaffle = null;
        for (ProntonTransactionDetail d : resultList) {

            if (currentRaffle == null || !currentRaffle.equals(d.getRaffle())) {
                currentRaffle = d.getRaffle();
                whole += currentRaffle + ".";
            }

            whole += d.getRaffleNumber();
            whole += ((int) (d.getBet())) + ".";

        }
        whole += "E";
        return whole;

    }

    public void resendPending() {

        Date parse = new Date();

        try {
            parse = startFormatter.parse(startFormatter.format(parse));
        } catch (ParseException ex) {
            Logger.getLogger(ProntonTransactionDetailService.class.getName()).log(Level.SEVERE, null, ex);
        }

        EntityManager m = getNewEntityManager();
        List<ProntonTransaction> rs = m.createQuery("SELECT c FROM ProntonTransaction c WHERE c.transactionTimeStamp > ?1 AND c.serverResponse IS NULL", ProntonTransaction.class)
                .setParameter(1, parse).getResultList();

        long now = new Date().getTime();

        for (ProntonTransaction found : rs) {

            long time = found.getSubmitSMS().getQueuedDate().getTime();

            if (now < time) {
                UIUtilities.addWarningMessage("Warning! System time was modified! Invalid system time detected! Will exit after 10 seconds.");
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                }
                System.exit(1);
            }

            long duration = now - time;
            if (duration > (1000 * 60 * 5)) { //5minutes response
                QueuedResponse submitSMS = found.getSubmitSMS();
                submitSMS.setSendStatus(QueuedResponse.UNPROCESSED);
                submitSMS.setSendTo(SettingsService.getInstance().getSettingsObject().getServerMobileNumber());
                submitSMS.setQueuedDate(new Date());
                QueuedResponseService.getInstance().update(submitSMS);
                Main.log("resubmit: " + submitSMS.getId());
            }

        }

    }

}
