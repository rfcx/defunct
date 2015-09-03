package org.rfcx.client_android.model;

import java.net.URI;

import android.net.Uri;

public class Event {

	private int id, audioStart;
	private String triggeredAt, latitude, longitude;
	private Uri audioUri;
	
	public Event(int id, String triggered_at2, String audio_uri2, String lat2, String lon2) {
		this.id = id;
	}
	
	public Event(int id,String triggered_at,String audio_uri,String lat,String lon,Integer audio_start) {
		this.id = id;
		this.triggeredAt = triggered_at;
		this.audioUri = Uri.parse(audio_uri.replace("https://","http://"));
		this.latitude = lat;
		this.longitude = lon;
		this.audioStart = audio_start;
	}
	
	public int getId(){
		return this.id;
	}
	
	public Uri getAudioUri() {
		return this.audioUri;
	}

	public String getLat() {
		return this.latitude;
	}

	public String getLon() {
		return this.longitude;
	}
	
	public String getTriggered() {
		return this.triggeredAt;
	}
	
	public void setLat(String _lat) {
		this.latitude = _lat;
	}
	public void setLon(String _lon) {
		this.longitude = _lon;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", triggered_at=" + triggeredAt
				+ ", audio_uri=" + audioUri + ", lat=" + latitude + ", lon=" + longitude
				+ "]";
	}

	public int getAudioStart() {
		return audioStart;
	}

	public void setAudioStart(int audio_start) {
		this.audioStart = audio_start;
	}
}
