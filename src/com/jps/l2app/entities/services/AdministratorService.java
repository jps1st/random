/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jps.l2app.entities.services;

import com.jps.l2app.entities.Administrator;
import com.jps.l2app.entities.services.util.AbstractEntityService;
import com.jps.l2app.utils.UIUtilities;

/**
 *
 * @author org org
 */
public class AdministratorService extends AbstractEntityService<Administrator> {

    @Override
    public Class getEntityClass() {
        return Administrator.class;
    }

    private static AdministratorService instance;

    public static AdministratorService getInstance() {
        if (instance == null) {
            instance = new AdministratorService();
        }
        return instance;
    }

    public Administrator getAdministrator() {
        try {
            Administrator find = find(1);

            if (find.getSuperPassword() == null || find.getSuperPassword().isEmpty()) {
                find.setSuperPassword(UIUtilities.getMD5EncryptedPassword("superadmin123"));
                find = update(find);
            }

            if (find != null) {
                return find;
            }
        } catch (Exception e) {
        }
        Administrator a = new Administrator();
        a.setId(1);
        a.setPassword(UIUtilities.getMD5EncryptedPassword("admin123"));
        a.setSuperPassword(UIUtilities.getMD5EncryptedPassword("superadmin123"));
        a.setUsername("admin");
        return insert(a);
    }

}
