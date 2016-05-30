package com.jps.l2app.utils;

import com.jps.l2app.entities.DigitTotal;
import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.EmployeeTotals;
import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.RawSMS;
import com.jps.l2app.entities.Settings;
import com.jps.l2app.entities.Submission;
import com.jps.l2app.entities.SubmissionRaw;
import com.jps.l2app.entities.services.DigitTotalService;
import com.jps.l2app.entities.services.EmployeeService;
import com.jps.l2app.entities.services.EmployeeTotalsService;
import com.jps.l2app.entities.services.RawSMSService;
import com.jps.l2app.entities.services.SettingsService;
import com.jps.l2app.entities.services.SubmissionRawService;
import com.jps.l2app.entities.services.util.DbaseManager;
import com.jps.l2app.exceptions.CutoffException;
import com.jps.l2app.main.Main;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import pint.InterpretWorker;
import pint.Pint;
import pint.SynTaxErrorException;

public class ProcessorService {

    public static void processMultipartSMS(String sender) {
        List<RawSMS> parents = RawSMSService.getInstance().getMultipartParents(sender);
        for (RawSMS parent : parents) {
            ProcessorService.processMultipartParent(parent);
        }
    }

    public static void processMultipartParent(RawSMS parent) {

        EntityManager m = DbaseManager.getNewEntityManager();
        List<RawSMS> rs = m.createQuery(
                "SELECT c FROM RawSMS c "
                + "WHERE c.sender LIKE ?3 "
                + "AND c.processStatus = -1 "
                + "AND c.multipart = TRUE "
                + "AND c.multipartId LIKE ?1 "
                + "AND c.multi_parts = ?2 "
                + "AND c.id != ?4 "
                + "ORDER BY c.multi_partNum", RawSMS.class)
                .setParameter(1, parent.getMultipartId())
                .setParameter(2, parent.getMulti_parts())
                .setParameter(3, parent.getSender())
                .setParameter(4, parent.getId())
                .getResultList();

        m.close();

        RawSMS current = parent; //#1
        int currentIndex = 2;   //start at #2
        List<RawSMS> parts = new ArrayList<>();
        for (RawSMS s : rs) {

            if (isTimeDiffAcceptable(s, current) && s.getMulti_partNum() != currentIndex) {
                continue;
            }

            parts.add(s);
            current = s;
            currentIndex++;

        }

        if (parts.size() + 1 != parent.getMulti_parts()) {
            return; //not all parts have been received
        }

        StringBuilder messageBuilder = new StringBuilder(parent.getMessage());

        for (RawSMS s : parts) {
            messageBuilder.append(s.getMessage());
            RawSMSService.getInstance().remove(s);
        }

        String messageCombined = messageBuilder.toString();
        parent.setProcessStatus(RawSMS.PROC_STAT_UNPROCESSED);
        parent.setMessage(messageCombined);
        parent.setMulti_partsFound(currentIndex);
        parent.setMulti_parent(parent);

        parent = RawSMSService.getInstance().update(parent);

        interpret(parent);

    }

    public static void interpret(final RawSMS sms) {

        Main.log("init_pinter: " + sms.getId());
        InterpretWorker w = new InterpretWorker() {

            @Override
            public void perform() {
                Main.log("pinting: " + sms.getId());
                performInterpret(sms);
            }

        };

        Thread daemon = new Thread(w);
        daemon.setDaemon(true);
        daemon.start();

    }

    public static void performInterpret(RawSMS sms) {

        //check if expired
        if (isExpired(sms)) {
            sms.setProcessStatus(RawSMS.PROC_STAT_EXPIRED);
            RawSMSService.getInstance().update(sms);
            return;
        }

        //discontinue if emp not registered
        Employee employee = EmployeeService.getInstance().getEmployee(sms.getSender());
        if (employee == null) {
            return;
        }

        sms.setEmployee(employee);

        //check syntax > return back syntax error && flag non submit
        CommonTree root;

        try {
            root = new Pint().parse(sms.getMessage().toLowerCase());
        } catch (SynTaxErrorException ex) {
            handleSyntaxError(sms);
            return;
        }

        //cutoff check
        String draw = root.getText();
        String tid = root.getChild(0).getChild(0).toString();
        try {
            cutOffCheck(draw, tid, sms.getReceivedDate());
        } catch (CutoffException ex) {
            handleError(sms, ex.getMessage());
            return;
        }

        //already submitted check
        if (SubmissionRawService.getInstance().alreadySubmitted(draw, tid, sms.getSender(), sms.getReceivedDate())) {
            handleAlreadySubmitted(sms, draw, tid);
            return;
        }

        //each submission:
        List<Submission> submits;
        try {
            //primary checks if digit-bet combo is valid acc to raffle
            //supplies each with raffle, digit and bet values only
            submits = collectSubmissions(root, draw);

        } catch (SynTaxErrorException ex) {
            handleError(sms, "Invalid bet: " + draw + ".t" + tid + "." + ex.getMessage());
            return;
        }

        //all goes well?
        //update submissionraw
        EntityManager m = DbaseManager.getNewEntityManager();
        m.getTransaction().begin();

        SubmissionRaw raw = m.merge(new SubmissionRaw(draw, tid, employee, sms.getReceivedDate()));

        double totalBet = 0;
        //TODO: check global limits here. reject sms if over limit
        for (Submission submit : submits) {

            submit = m.merge(submit);

            submit.setReceivedDate(sms.getReceivedDate());
            submit.setSubmissionId(tid);
            submit.setSender(employee);
            submit.setSubmissionRaw(raw);

            totalBet += submit.getBetValue();

            //<editor-fold defaultstate="collapsed" desc="update raffle digit total">
            DigitTotal dt = m.merge(DigitTotalService.getInstance().getDigitTotals(submit));
            submit.setDigitTotal(dt);
            dt.setNeedsRefresh(true);
            //</editor-fold> 

            //<editor-fold defaultstate="collapsed" desc="update employee totals">
            EmployeeTotals et = m.merge(EmployeeTotalsService.getInstance().getEmpTotals(submit));
            submit.setEmployeeTotals(et);
            et.setNeedsRefresh(true);
            //</editor-fold>

        }

        raw.setTotalValue(totalBet);

        //update sms for success
        sms = m.merge(sms);
        sms.setProcessStatus(RawSMS.PROC_STAT_OK);

        //setup reply
        String rmsg = draw + ".t" + tid + "=" + totalBet + " THANK YOU!";
        Main.log(rmsg);
        QueuedResponse r = m.merge(new QueuedResponse(rmsg, true, employee));

        sms.setResponse(r);

        m.getTransaction().commit();
        m.close();

    }

    private static void handleResponse(RawSMS sms, String msg, int status) {
        Main.log(msg);
        EntityManager m = DbaseManager.getNewEntityManager();
        m.getTransaction().begin();
        QueuedResponse response = m.merge(new QueuedResponse(msg, true, sms.getEmployee()));
        sms = m.merge(sms);
        sms.setProcessStatus(status);
        sms.setResponse(response);
        m.getTransaction().commit();
        m.close();
    }

    private static void handleError(RawSMS sms, String errmsg) {
        handleResponse(sms, errmsg, RawSMS.PROC_STAT_ERROR);
    }

    private static void handleSyntaxError(RawSMS sms) {
        handleError(sms, "Format error.");
    }

    private static void handleAlreadySubmitted(RawSMS sms, String draw, String tid) {
        handleError(sms, "Error: " + draw + ".t" + tid + " already submitted.");
    }

    public static void cutOffCheck(String draw, String tid, Date submitDateTime) throws CutoffException {
        Settings settingsObject = SettingsService.getInstance().getSettingsObject();
        Date cutoffTime = SettingsService.getInstance().getCutoffTime(draw, submitDateTime);

        if (submitDateTime.after(cutoffTime)) {
            throw new CutoffException(draw + ".t" + tid + " rejected. Time limit reached.");
        }

        if (!settingsObject.isAllowAdvanceSubmit()) {

            if (!SettingsService.getInstance().onSchedule(draw, submitDateTime)) {
                throw new CutoffException(draw + ".t" + tid + " rejected. Advance submits not allowed.");
            }

        }
    }

    public static boolean isExpired(RawSMS sms) {
        long lifeTime = new Date().getTime() - sms.getReceivedDate().getTime();
        return lifeTime > (1000 * 60 * 60 * 6);//expire in 6h
    }

    public static boolean isTimeDiffAcceptable(RawSMS s1, RawSMS s2) {

        long diff = s1.getReceivedDate().getTime() - s2.getReceivedDate().getTime();

        if (diff < 0) {
            diff = diff * - 1;
        }

        long seconds = diff / 1000;

        return seconds < 60 * 10; //ten minutes

    }

    public static List<Submission> collectSubmissions(Tree root, String draw) throws SynTaxErrorException {
        List<Submission> buffer = new ArrayList();
        for (int x = 1; x < root.getChildCount() - 1; x++) {
            buffer.addAll(collect(root.getChild(x), draw));
        }
        return buffer;
    }

    private static Collection<? extends Submission> collect(Tree child, String draw) throws SynTaxErrorException {
        List<Submission> buffer = new ArrayList();
        String raffle = child.getText();

        int min = 0;
        switch (raffle) {
            case "l2":
            case "s2":
                min = 2;
                break;
            case "l3":
            case "s3":
            case "s3r":
                min = 3;
                break;
            case "p3":
                min = 6;
                break;
            case "4d":
                min = 4;
                break;
        }

        for (int x = 0; x < child.getChildCount(); x++) {

            String tx = child.getChild(x).toString();

            if (tx.length() < min + 1) {//include at least a single digit amount
                throw new SynTaxErrorException(raffle + "." + tx);
            }

            System.out.println(tx);

            String rn = tx.substring(0, min);
      
            String betstr = tx.substring(min, tx.length());
  
            double bet = Double.parseDouble(betstr);

            buffer.add(new Submission(draw, raffle, rn, bet));

        }

        return buffer;

    }

}
