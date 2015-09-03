package org.rfcx.cellmapping.interfaces;

/**
 * Created by Urucas on 9/28/14.
 */
public interface CheckinsCallback {

    public void onSucess();
    public void onError(int error);
    public void onError(String error);
}
