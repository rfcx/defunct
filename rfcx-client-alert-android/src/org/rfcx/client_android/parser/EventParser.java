package org.rfcx.client_android.parser;

import org.json.JSONException;
import org.json.JSONObject;
import org.rfcx.client_android.model.Event;

import android.util.Log;


public abstract class EventParser {

	public static Event parse(JSONObject result) {
		
		try {
			int id = 1;
			String triggered_at = result.getString("triggered_at");
			String audio_uri = result.getString("audio_uri");
			String lat = result.getJSONObject("geo").getString("lat");
			String lon = result.getJSONObject("geo").getString("lng");
			Integer audio_start = Integer.parseInt(result.getString("audio_start")); 
			
			Event ev = new Event(id, triggered_at, audio_uri, lat, lon, audio_start);
			//ev.setLat(lat);
			//ev.setLon(lon);

			Log.i("audio_uri",result.getString("audio_uri"));
			
			return ev;
			
		} catch (JSONException e) { e.printStackTrace(); }
		
		return null;
	}
	
}
