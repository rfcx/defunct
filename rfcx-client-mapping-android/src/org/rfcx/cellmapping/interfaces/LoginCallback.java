package org.rfcx.cellmapping.interfaces;

/**
 * Created by Urucas on 9/28/14.
 */
public interface LoginCallback {

    public void onSuccess(String sid);
    public void onError(int error);
    public void onError(String message);
}
