package org.rfcx.client_android;

import org.rfcx.client_android.controller.ApiController;

import android.app.Application;

public class BaseApplication extends Application {

	
	private static BaseApplication _instance;
	private static ApiController _apicontroller;
	
	public BaseApplication() {
		super();
		_instance = this;
	}

	public static BaseApplication getInstance() {
		return _instance;
	}
	
	public static ApiController getAPIController() {
		if(_apicontroller == null) {
			_apicontroller = new ApiController();
		}
		return _apicontroller;
	}
}
