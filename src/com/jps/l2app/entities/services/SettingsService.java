package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Settings;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author org org
 */
public class SettingsService extends AbstractEntityService<Settings> {

    private static SettingsService instance;

    public static SettingsService getInstance() {
        if (instance == null) {
            instance = new SettingsService();
            // instance.init();
        }
        return instance;
    }

    @Override
    public Class getEntityClass() {
        return Settings.class;
    }

    public void enableAutoReply(boolean value) {

        Settings find = getSettingsObject();
        find.setAutoReplyEnabled(value);
        update(find);

    }

    public boolean isAutoReplyEnabled() {

        Settings find = getSettingsObject();
        EntityManager m = getNewEntityManager();
        find = m.merge(find);
        m.refresh(find);
        m.close();
        return find.isAutoReplyEnabled();

    }

    public Settings getSettingsObject() {

        EntityManager m = getNewEntityManager();
        List<Settings> rs = m.createQuery("SELECT c FROM Settings c ORDER BY c.id", Settings.class).getResultList();
        m.close();
        if (rs.isEmpty()) {

            Settings e = new Settings();
            return insert(e);

        }

        return rs.get(0);
    }
    
    public Date getCutoffTime(String draw, Date submitDateTime) {

        Settings settingsObject = SettingsService.getInstance().getSettingsObject();

        String cutOffTime;

        switch (draw.toLowerCase()) {
            case "d1":
                cutOffTime = settingsObject.getDraw1Cutoff();
                break;
            case "d2":
                cutOffTime = settingsObject.getDraw2Cutoff();
                break;
            case "d3":
                cutOffTime = settingsObject.getDraw3Cutoff();
                break;
            default:
                return null;
        }

        try {
            SimpleDateFormat cutofFormatter = new SimpleDateFormat("h:mm a");

            Calendar cutOff = Calendar.getInstance();
            cutOff.setTime(cutofFormatter.parse(cutOffTime));

            Calendar submitTime = Calendar.getInstance();
            submitTime.setTime(submitDateTime);

            cutOff.set(submitTime.get(Calendar.YEAR), submitTime.get(Calendar.MONTH), submitTime.get(Calendar.DATE));
            return cutOff.getTime(); //submitted after

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;

    }

    public boolean onSchedule(String draw, Date date) {
        if (draw.equalsIgnoreCase("d2")) {
            return date.after(getCutoffTime("d1", date)) && date.before(getCutoffTime("d2", date));
        } else if (draw.equalsIgnoreCase("d3")) {
            return date.after(getCutoffTime("d2", date)) && date.before(getCutoffTime("d3", date));
        }
        return true;
    }

}
