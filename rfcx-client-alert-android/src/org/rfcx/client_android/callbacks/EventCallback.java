package org.rfcx.client_android.callbacks;

import org.rfcx.client_android.model.Event;

public interface EventCallback {

	public void onSuccess(Event event);
	public void onError(String message);
}
