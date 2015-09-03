package org.rfcx.cellmapping;

import org.rfcx.cellmapping.controller.ApiController;
import org.rfcx.cellmapping.controller.PersistentController;
import org.rfcx.cellmapping.exceptions.UnauthorizeException;
import org.rfcx.cellmapping.model.User;
import org.rfcx.cellmapping.utils.Utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class CellMappingApp extends Application {

    private static PersistentController controller;
    private static ApiController api;
    private static CellMappingApp app;
    private static SharedPreferences preferences;
    private static User user;

    public CellMappingApp(){
        super();
        app = this;
    }

    public static SharedPreferences getPreferences() {
        if(preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(app.getApplicationContext());
        }
        return preferences;
    }

    public static User getUser() throws UnauthorizeException {
        if(user == null) {
            String sid = CellMappingApp.getPreferences().getString("sid", null);
            String sname = CellMappingApp.getPreferences().getString("sname", null);
            String guid = Utils.getDeviceUUID(app.getApplicationContext());
            if(sid == null || sname == null) {
                throw new UnauthorizeException();
            }
            user = new User(sname, sid, guid);
        }
        return user;
    }

    public static void setUser(User _user) {
        user = _user;
        SharedPreferences pref = CellMappingApp.getPreferences();
        pref.edit().putString("sid", user.getSid())
                   .putString("sname", user.getName())
                .putString("uuid", user.getGUID()).commit();
    }

    public static void logout() {
        CellMappingApp.getPreferences().edit().clear().commit();
    }

    public static CellMappingApp getApp(){
        if(app == null){
            app = new CellMappingApp();
        }
        return app;
    }

    public static PersistentController getController(){
        if(controller == null){
            controller = new PersistentController();
        }
        return controller;
    }

    public static ApiController getApiController() {
        if(api == null) {
            api = new ApiController();
        }
        return api;
    }

}
