package org.rfcx.cellmapping.controller;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rfcx.cellmapping.R;
import org.rfcx.cellmapping.activities.LoginActivity;
import org.rfcx.cellmapping.activities.MainActivity;
import org.rfcx.cellmapping.interfaces.CheckinsCallback;
import org.rfcx.cellmapping.interfaces.LoginCallback;
import org.rfcx.cellmapping.model.Poi;
import org.rfcx.cellmapping.model.User;
import org.rfcx.cellmapping.tasks.JSONRequestTask;
import org.rfcx.cellmapping.tasks.JSONRequestTaskHandler;
import org.rfcx.cellmapping.utils.Utils;

import java.util.ArrayList;


public class ApiController {

    private static final String BASEURL = "http://192.168.43.15:8080/v1/mapping";

    public void login(Context context, User user, LoginCallback callback) {

        if(!Utils.isConnected(context)) {
            callback.onError(R.string.notconnected);
            return;
        }
        String jsonData;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", user.getName());
            jsonObject.put("carrier", user.getCarrier());
            jsonObject.put("guid", user.getGUID());

            jsonData = jsonObject.toString();

        }catch(JSONException e){
            callback.onError(R.string.loginerror);
            return;
        }

        String url = BASEURL + "/register";
        new JSONRequestTask(new JSONRequestTaskHandler() {
            @Override
            public void onSuccess(JSONObject response) {


            }

            @Override
            public void onSuccess(JSONArray result) {

            }

            @Override
            public void onError(String message) {

            }

        }).addParam("user", jsonData)
          .execute(url);

    }

    public void sync(Context context, ArrayList<Poi> pois, CheckinsCallback callback) {

        if(!Utils.isConnected(context)) {
            callback.onError(R.string.notconnected);
            return;
        }
    }

}
