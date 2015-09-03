package org.rfcx.client_android.activities;

import org.rfcx.client_android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HomeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		Intent intent = new Intent(HomeActivity.this,RainforestActivity.class);
		startActivity(intent);
	}
	
}
