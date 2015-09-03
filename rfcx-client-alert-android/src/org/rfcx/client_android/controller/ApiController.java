package org.rfcx.client_android.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rfcx.client_android.BaseApplication;
import org.rfcx.client_android.R;
import org.rfcx.client_android.callbacks.EventCallback;
import org.rfcx.client_android.model.Event;
import org.rfcx.client_android.parser.EventParser;

import android.util.Log;

import com.urucas.services.JSONRequestTask;
import com.urucas.services.JSONRequestTaskHandler;
import com.urucas.utils.Utils;

public class ApiController {

	private static String BASE_URL = "https://rfcx.org/api/1";
	
	public void getEvent(int event_id, final EventCallback callback) {
	
		if(!isConnected()) {
			Utils.Toast(BaseApplication.getInstance(), R.string.no_connection);
			return;
		}
		
		String url = BASE_URL + "/event/1";
		try {
			new JSONRequestTask(new JSONRequestTaskHandler() {

				@Override
				public void onSuccess(JSONObject result) {
					try {
						if(result.has("error")) {
							callback.onError(result.getString("error"));
							return;
						}
						// parse json from event
						Log.i("response",result.toString());
						Event event = EventParser.parse(result);
						callback.onSuccess(event);
						
					} catch (JSONException e) {
						e.printStackTrace();
						callback.onError("error parsing");
					}
				}

				@Override
				public void onError(String message) {
					callback.onError(message);
					Log.i("response error 1",message);
				}

				@Override
				public void onSuccess(JSONArray result) {
					Log.i("response",result.toString());
				}

			}).addParam("id", String.valueOf(event_id)).execute(url);
			
		} catch (Exception e) {
			callback.onError("error calling api");
		}
	}
	
	private boolean isConnected() {
		return Utils.isConnected(BaseApplication.getInstance().getApplicationContext());
	}

	

}
