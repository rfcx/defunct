package org.rfcx.client_android.activities;

import org.rfcx.client_android.BaseApplication;
import org.rfcx.client_android.R;
import org.rfcx.client_android.callbacks.EventCallback;
import org.rfcx.client_android.model.Event;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RainforestActivity extends FragmentActivity {
	private GoogleMap map;
	private MediaPlayer mediaPlayer;
	private SupportMapFragment fm;
	private Event ev;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rainforest);

		// Get a handle to the Map Fragment
		fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(
				R.id.map);
		map = fm.getMap();

		MapsInitializer.initialize(this);

		BaseApplication.getAPIController().getEvent(1, new EventCallback() {

			@Override
			public void onSuccess(Event event) {
				// TODO Auto-generated method stub
				showData(event);
				Log.i("Event", event.toString());
				ev = event;
			}

			@Override
			public void onError(String message) {
				Log.i("response error", message);
			}
		});
		
		Button playbutton = (Button)findViewById(R.id.playBtt);
		playbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startPlayer(ev);
			}
		});
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
	}

	private void showData(Event e) {

		TextView lonTxt = (TextView) findViewById(R.id.LonTxt);
		lonTxt.setText(e.getLon());

		TextView latTxt = (TextView) findViewById(R.id.LatTxt);
		latTxt.setText(e.getLat());

		LatLng somewhere = new LatLng(Double.parseDouble(e.getLat()),
				Double.parseDouble(e.getLon()));

		if (map != null) {
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			map.setMyLocationEnabled(true);
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(somewhere, 13));

			map.addMarker(new MarkerOptions().title("There's chainsaws here")
					.snippet("Right here, go get them!").position(somewhere));
		}
	}

	private void startPlayer(final Event event) {
		// TODO UI For This Media Player
		try {
			mediaPlayer = MediaPlayer.create(getApplicationContext(), event.getAudioUri() );
		}
		catch (Exception e){
			e.printStackTrace();
		}
		

		if (mediaPlayer != null) {

			Log.i("MediaPlayer", "Not Null!");

			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mediaPlayer, int what,
						int extra) {
					// TODO Auto-generated method stub
					Log.e("ERROR", mediaPlayer.toString());
					mediaPlayer.release();
					return false;
				}
			});

			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					Log.i("Chainsaw Start", "" + event.getAudioStart());
					mediaPlayer.seekTo(event.getAudioStart());
					mediaPlayer.start();
				}
			});
		} else {
			Log.i("MediaPlayer", "Null!");

		}

	}
}
