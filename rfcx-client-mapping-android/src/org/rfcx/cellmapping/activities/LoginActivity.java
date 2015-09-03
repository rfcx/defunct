package org.rfcx.cellmapping.activities;

import org.rfcx.cellmapping.CellMappingApp;
import org.rfcx.cellmapping.exceptions.UnauthorizeException;
import org.rfcx.cellmapping.interfaces.LoginCallback;
import org.rfcx.cellmapping.model.User;
import org.rfcx.cellmapping.utils.Utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class LoginActivity extends ActionBarActivity {

    private EditText nameTxt;
    private static String TAG_NAME = "RFCX Cell Mapping LoginActivity";
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        nameTxt = (EditText) findViewById(R.id.nameEdit);

        Button loginBtt = (Button) findViewById(R.id.loginButton);
        loginBtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sname = nameTxt.getText().toString().trim();
                if(sname.equals("")){
                    Toast toast = Toast.makeText(LoginActivity.this,R.string.fill,Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                login(sname);
            }
        });

        // check if there's a user already logged
        try {
            User user = CellMappingApp.getUser();
            startMainActivity();

        }catch(UnauthorizeException e){
            CellMappingApp.logout();
            Log.i(TAG_NAME, "session logout");
        }
    }

    private void login(final String sname){

        // get phone guid
        final String guid = Utils.getDeviceUUID(LoginActivity.this);
        final String carrier = Utils.getCarrier(LoginActivity.this);

        User user = new User(sname, guid);
        dialog = ProgressDialog.show(LoginActivity.this, "", getResources().getString(R.string.loginin), true);
        dialog.setCancelable(true);

        CellMappingApp.getApiController().login(LoginActivity.this, user, new LoginCallback() {
            @Override
            public void onSuccess(String sid) {
                dialog.dismiss();
                User user = new User(sname, sid, guid);
                user.setCarrier(carrier);
                saveUser(user);
            }

            @Override
            public void onError(int error) {
                dialog.dismiss();
                Utils.Toast(LoginActivity.this, error);
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    private void saveUser(User user) {
        // create persistent user
        CellMappingApp.setUser(user);
        startMainActivity();
    }

    private void startMainActivity() {

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
