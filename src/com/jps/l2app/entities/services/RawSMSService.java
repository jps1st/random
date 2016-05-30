package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Employee;
import com.jps.l2app.entities.EmployeeTotals;
import com.jps.l2app.entities.QueuedResponse;
import com.jps.l2app.entities.DigitTotal;
import com.jps.l2app.entities.RawSMS;
import com.jps.l2app.entities.Settings;
import com.jps.l2app.entities.Submission;
import com.jps.l2app.entities.SubmissionRaw;
import com.jps.l2app.utils.Utils;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.SwingWorker;
import com.jps.l2app.exceptions.CutoffException;
import com.jps.l2app.exceptions.InvalidPasskeyException;
import com.jps.l2app.exceptions.InvalidTotalBetException;
import com.jps.l2app.exceptions.ResentSubmissionException;
import com.jps.l2app.exceptions.SyntaxErrorException;
import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;
import com.jps.l2app.utils.FormatUtils;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author org org
 */
public class RawSMSService extends AbstractEntityService<RawSMS> {

    private static RawSMSService instance;

    public static RawSMSService getInstance() {
        if (instance == null) {
            instance = new RawSMSService();
        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return RawSMS.class;
    }

    public List<RawSMS> getLatestSMSList() {
        EntityManager m = getNewEntityManager();
        List rs = m.createQuery("SELECT c FROM RawSMS c ORDER BY c.receivedDate DESC").setMaxResults(100).getResultList();
        m.close();
        return rs;
    }

    public List<RawSMS> getUnprocessed() {
        EntityManager m = getNewEntityManager();
        List<RawSMS> rs = m.createQuery("SELECT c FROM RawSMS c WHERE c.processStatus = 0", RawSMS.class).getResultList();

        for (RawSMS rawSMS : rs) {
            m.refresh(rawSMS);
        }

        m.close();
        return rs;
    }

    public List<RawSMS> getMultipartParents(String sender) {
        EntityManager m = getNewEntityManager();
        List<RawSMS> rs = m.createQuery("SELECT c FROM RawSMS c WHERE c.sender LIKE ?1 AND c.multipart = TRUE AND c.processStatus = -1 AND c.multi_partNum = 1", RawSMS.class)
                .setParameter(1, "%" + RawSMSService.extractEssentialMobileNum(sender))
                .getResultList();
        m.close();
        return rs;
    }

    public RawSMS insertIfNotDuplicate(RawSMS sms) {

        if (sms == null) {
            return null;
        }

        EntityManager m = getNewEntityManager();
        Long r = m.createQuery("SELECT COUNT(c) FROM RawSMS c WHERE c.header LIKE ?1", Long.class)
                .setParameter(1, sms.getHeader())
                .getSingleResult();
        m.close();

        if (r > 0) {
            return null;
        }

        sms = insert(sms);

        return sms;

    }

    SwingWorker multipartProcessor = null;

    public long getUnprocessedCount() {
        EntityManager m = getNewEntityManager();
        long rs = m.createQuery("SELECT COUNT(c) FROM RawSMS c WHERE c.processStatus = 0 ORDER BY c.sender, c.receivedDate", Long.class).getSingleResult();
        m.close();
        return rs;
    }

    public void cleanUnprocessedFromUnRegisteredNums() {
        EntityManager m = getNewEntityManager();
        List<RawSMS> rs = m.createQuery("SELECT c FROM RawSMS c WHERE c.processStatus = 0 ORDER BY c.sender, c.receivedDate", RawSMS.class).getResultList();
        for (RawSMS rawSMS : rs) {
            boolean exists = EmployeeService.getInstance().checkMobileNumber(rawSMS.getSender());
            if (!exists) {
                rawSMS.setProcessStatus(RawSMS.PROC_STAT_ERROR);
                RawSMSService.getInstance().update(rawSMS);
            }
        }
        m.close();
    }

    public List<RawSMS> getSMSFromEmp(Employee e, Date start, Date end, String key) {
        EntityManager m = getNewEntityManager();
        key = key.trim();
        List<RawSMS> rs;
        if (e.getShortName().equalsIgnoreCase("unregistered")) {
            rs = m.createQuery("SELECT c FROM RawSMS c WHERE c.receivedDate >= ?1 AND c.receivedDate <= ?2 AND c.employee IS NULL AND c.message LIKE ?3 ORDER BY c.id", RawSMS.class)
                    .setParameter(1, start)
                    .setParameter(2, end)
                    .setParameter(3, "%" + key + "%")
                    .setMaxResults(500)
                    .getResultList();
        } else if (e.getShortName().equalsIgnoreCase("all")) {
            rs = m.createQuery("SELECT c FROM RawSMS c WHERE c.receivedDate >= ?1 AND c.receivedDate <= ?2 AND c.message LIKE ?3 ORDER BY c.id", RawSMS.class)
                    .setParameter(1, start)
                    .setParameter(2, end)
                    .setParameter(3, "%" + key + "%")
                    .setMaxResults(500)
                    .getResultList();
        } else {
            rs = m.createQuery("SELECT c FROM RawSMS c WHERE c.receivedDate >= ?1 AND c.receivedDate <= ?2 AND c.employee.id = ?3 AND c.message LIKE ?4 ORDER BY c.id", RawSMS.class)
                    .setParameter(1, start)
                    .setParameter(2, end)
                    .setParameter(3, e.getId())
                    .setParameter(4, "%" + key + "%")
                    .setMaxResults(500)
                    .getResultList();
        }

        for (RawSMS r : rs) {
            m.refresh(r);
        }

        m.close();
        return rs;
    }

    private class AnalyzedResult {

        String draw;
        String textNumber;

        List<Submission> submission;
        double total = 0d;

        public AnalyzedResult(String draw, String subId, List<Submission> submission, double total) {
            this.draw = draw;
            this.textNumber = subId;
            this.submission = submission;
            this.total = total;

        }
    }

    @Deprecated
    private AnalyzedResult analyzeRawSubmission(RawSMS submission, Employee employee) throws SyntaxErrorException,
             ResentSubmissionException,  CutoffException {

        Date submitDateTime = null;

        //<editor-fold defaultstate="collapsed" desc="extact the whole data as string and get submitDateTime as byproduct">
        if (submitDateTime == null) {
            submitDateTime = submission.getReceivedDate();
        }

        String message = submission.getMessage();
        message = transformMessage(message);
        Main.log("whole = " + message);

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="extract detail units and check for min length">
        List<String> detailUnitsList = new ArrayList();
        for (String string : splitRawSMSStr(message)) {
            if (!string.isEmpty()) {
                detailUnitsList.add(string);
            }
        }

        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="get headers: get draw, subno and check if subno valid">
        String draw = detailUnitsList.get(0); //first detail unit is draw
        String subno = detailUnitsList.get(1); //second detailUnit is submission no
        boolean valid = subno.startsWith("t"); //expected to pass from draw check already
        if (!valid) {
            throw new SyntaxErrorException("ERROR: No text ID/Number.");
        }
        //</editor-fold>

        boolean endFound = detailUnitsList.get(detailUnitsList.size() - 1).startsWith("e");
        if (!endFound) {
            throw new SyntaxErrorException(draw + "." + subno + " NO END.");
        }

        //<editor-fold defaultstate="collapsed" desc="cutoff check">
        Settings settingsObject = SettingsService.getInstance().getSettingsObject();
        Date cutoffTime = SettingsService.getInstance().getCutoffTime(draw, submitDateTime);

        if (submitDateTime.after(cutoffTime)) {
            throw new CutoffException(draw + "." + subno + " rejected. Time limit reached.");
        }

        if (!settingsObject.isAllowAdvanceSubmit()) {

            if (!SettingsService.getInstance().onSchedule(draw, submitDateTime)) {
                throw new CutoffException(draw + "." + subno + " rejected. Advance submits not allowed.");
            }

        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="extract details or collection data">
        List<String> details = new ArrayList<>();
        for (int i = 2; i < detailUnitsList.size() - 1; i++) {
            details.add(detailUnitsList.get(i));
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="analyze each">
        List<Submission> submissions = new ArrayList<>();
        double analyzedTotalBet = 0;

        int min_expected_digits = -1;
        String raffle = null;
        boolean number;
        boolean negative;
        for (String det : details) {

            number = false;
            negative = false;

            try {
                long n = Long.parseLong(det);
                number = true;
                negative = n < 0;
            } catch (NumberFormatException ex) {
            }

            if (!number) {
                //<editor-fold defaultstate="collapsed" desc="if nan/raffle">
                min_expected_digits = -1;

                switch (det.toLowerCase()) {
                    case "l2":
                    case "s2":
                        min_expected_digits = 2;
                        break;
                    case "l3":
                        min_expected_digits = 3;
                        break;
                    case "s3":
                    case "s3r":
                        min_expected_digits = 3;
                        break;
                    case "4d":
                        min_expected_digits = 4;
                        break;
                    case "p42":
                    case "p45":
                    case "p49":
                    case "p55":
                    case "p3":
                        min_expected_digits = 6;
                        break;
                }

                if (min_expected_digits != -1) {
                    raffle = det;
                    continue;
                }

                throw new SyntaxErrorException(draw + "." + subno + " " + det + " Error: raffle type invalid.");
                //</editor-fold>
            }

            if (negative) {
                throw new SyntaxErrorException(draw + "." + subno + " " + det + " Error: Negative bet not supported.");
            }

            if (raffle == null) {
                throw new SyntaxErrorException(draw + "." + subno + " " + det + " Error: Raffle Type Missing.");
            }

            if (det.length() < min_expected_digits) {
                throw new SyntaxErrorException(draw + "." + subno + " " + raffle + "." + det + " Error: Invalid digits. ");
            }

            double bet = Utils.getBet(det, min_expected_digits);
            if (bet > 10000) {
                throw new SyntaxErrorException(draw + "." + subno + " " + raffle + "." + det + " Error: Bet greater than 10k.");
            }

            String rafn = Utils.getDigits(det, min_expected_digits);
            Submission buffer = new Submission();
            buffer.setSubmissionId(subno);
            buffer.setDraw(draw);
            buffer.setRaffleType(raffle);
            buffer.setReceivedDate(submitDateTime);
            buffer.setSender(employee);
            buffer.setBetValue(bet);
            buffer.setRaffleNumber(rafn);
            submissions.add(buffer);

            analyzedTotalBet += bet;

        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="resubmission check">
        if (employee.isPronton()) {

            //reset all submits regarding this subno
            SubmissionService.getInstance().removePreviousProntonSubmissions(employee, subno, draw, submitDateTime);
            SubmissionRawService.getInstance().removeSubmissionsRaw(employee, subno, draw, submitDateTime);

        } else {
            boolean alreadySubmitted = SubmissionRawService.getInstance().alreadySubmitted(draw, subno, employee.getMobileNumber(), submitDateTime);
            if (alreadySubmitted) {
                throw new ResentSubmissionException(draw + "." + subno + " already submitted.");
            }
        }

        //</editor-fold>
        return new AnalyzedResult(draw, subno, submissions, analyzedTotalBet);
    }

    /**
     * This assumes that all sms are received in the same order as they are
     * sent. In all test cases, split sms seem to arrive in the same order they
     * are sent. Please change this if future experiments prove otherwise.
     *
     * The byproduct is a list of sms that are not submissions.
     *
     * @param unprocessed
     * @return
     */
    private class GroupBySubmissionResult {

        List<RawSMS> groupedSubmissions;
        List<RawSMS> notSubmissions;

        public GroupBySubmissionResult(List<RawSMS> groupedSubmissions, List<RawSMS> notSubmissions) {
            this.groupedSubmissions = groupedSubmissions;
            this.notSubmissions = notSubmissions;
        }

    }

    /**
     * The lists inside the resulting object are always not null.
     *
     * @param unprocessed
     * @return
     */
    private GroupBySubmissionResult groupBySubmission(List<RawSMS> unprocessed) {
        //<editor-fold defaultstate="collapsed" desc="extract submissions raw">

        List<RawSMS> submissions = new ArrayList<>();
        List<RawSMS> notSubmissions = new ArrayList<>();

        for (RawSMS raw : unprocessed) {

            String message = raw.getMessage();
            message = transformMessage(message);

            boolean headFound = checkIfHeadFound(message);
            boolean endFound = checkIfEndFound(message);

            if (headFound && endFound) {
                submissions.add(raw);
                continue;
            }

            //reply only to headFound&&!endFound, the rest ignore
            if (headFound && !endFound) {
                int indexOf = message.indexOf(".", 3);
                String submitHeader = message.substring(0, indexOf);
                raw.setResponse(QueuedResponseService.getInstance().sendNoEndError(raw.getSender(), submitHeader));
                raw.setSubmitError(true);

            }

            notSubmissions.add(raw);

        }
        //</editor-fold>
        return new GroupBySubmissionResult(submissions, notSubmissions);
    }

    private String transformMessage(String message) {
        message = message
                .replace('#', '.')
                .replace('*', '.')
                .replace(',', '.')
                .replace("-", "")
                .replace("=", "");
        message = message.replaceAll("\n", "")
                .replaceAll("\r", "")
                .replaceAll(" ", "")
                .replaceAll("\t", "")
                .replaceAll("\u000C", "")
                .toLowerCase();
        return message;
    }

    private boolean checkIfEndFound(String eval) {

        String[] tmp = splitRawSMSStr(eval);
        if (tmp.length == 0) {
            return false;
        }
        return tmp[tmp.length - 1].startsWith("e");

    }

    private String[] splitRawSMSStr(String eval) {
        return eval.trim().toLowerCase().replace(".", "%").split("%");
    }

    private boolean checkIfHeadFound(String eval) {
        eval = eval.trim().toLowerCase();
        return eval.startsWith("d1.t") | eval.startsWith("d2.t") | eval.startsWith("d3.t");
    }

    public static String extractEssentialMobileNum(String mobileNo) {
        if (mobileNo.length() < 10) {
            return mobileNo;
        }

        int length = mobileNo.length();
        int start = length - 10;
        return mobileNo.substring(start);
    }

}
