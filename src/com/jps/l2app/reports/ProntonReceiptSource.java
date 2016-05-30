package com.jps.l2app.reports;

import com.jps.l2app.entities.GlobalLimit;
import com.jps.l2app.entities.ProntonTransaction;
import com.jps.l2app.entities.ProntonTransactionDetail;
import com.jps.l2app.entities.services.GlobalLimitService;
import com.jps.l2app.entities.services.ProntonTransactionService;
import com.jps.l2app.entities.services.SettingsService;
import com.jps.l2app.main.Main;
import com.jps.l2app.main.Setup;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

/**
 *
 * @author org org
 */
public class ProntonReceiptSource implements JRDataSource {

    private final ProntonTransaction transaction;
    private final Iterator<ProntonTransactionDetail> iterator;
    private ProntonTransactionDetail current;

    public ProntonReceiptSource(ProntonTransaction transaction) {
        this.transaction = transaction;
        List<ProntonTransactionDetail> details = ProntonTransactionService.getInstance().getDetails(transaction.getId());
        this.iterator = details.iterator();
    }

    @Override
    public boolean next() throws JRException {
        boolean n = iterator.hasNext();
        if (n) {
            this.current = iterator.next();
        }
        return n;
    }

    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
    DecimalFormat dformat = new DecimalFormat("0000000");
    DecimalFormat dformat1 = new DecimalFormat("##.0");

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        String name = jrf.getName();
        switch (name) {
            case "draw":
                return transaction.getDraw();
            case "date":
                return format.format(transaction.getTransactionTimeStamp());
            case "id":
                return Integer.toString(current.getId(), 36).toUpperCase();
            case "num":
                return current.getRaffleNumber();
            case "amount":
                return dformat1.format(current.getBet());
            case "total":
                return transaction.getTotal() + "";
            case "trans":
                return dformat.format(transaction.getId());
            case "raf":
                return current.getRaffle();
            case "station":
                return SettingsService.getInstance().getSettingsObject().getProntonStationName();
            case "win":
                GlobalLimit l = GlobalLimitService.getInstance().check(current.getDraw(), current.getRaffle(), current.getRaffleNumber(), null);
                double w = 0d;
                if (l != null) {
                    w = l.getWinPerPiso();
                } else {
                    Main.log("limit not found!");
                }
                double win = current.getBet() * w;
                return dformat1.format(win);
            case "cname":
                return Setup.businessName;

        }
        return "";
    }

}
