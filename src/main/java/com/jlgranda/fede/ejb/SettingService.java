/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jlgranda.fede.ejb;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jpapi.controller.BussinesEntityHome;
import org.jpapi.model.Setting;
import org.jpapi.model.StatusType;
import org.jpapi.util.Dates;
import org.picketlink.idm.IdentityManager;

/**
 *
 * @author jlgranda
 */
@Stateless
public class SettingService extends BussinesEntityHome<Setting> {
    private static final long serialVersionUID = 4575953142699951180L;
    
    @PersistenceContext
    EntityManager em;
    
    IdentityManager identityManager = null;
    
    @PostConstruct
    private void init(){
        setEntityManager(em);
    }

    public Setting findByName(String name){
        List<Setting> settings = this.findByNamedQuery("Setting.findByName", name);
        return settings.isEmpty() ? new Setting(name, name) : settings.get(0);
    }
    
    
    @Override
    public Setting createInstance() {

        Setting _instance = new Setting();
        _instance.setCreatedOn(Dates.now());
        _instance.setLastUpdate(Dates.now());
        _instance.setStatus(StatusType.ACTIVE.toString());
        _instance.setActivationTime(Dates.now());
        _instance.setExpirationTime(Dates.addDays(Dates.now(), 364));
        return _instance;
    }
}
