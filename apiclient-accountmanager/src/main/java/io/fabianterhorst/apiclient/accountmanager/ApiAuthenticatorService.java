package io.fabianterhorst.apiclient.accountmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ApiAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        ApiAuthenticator authenticator = new ApiAuthenticator(this, getAuthActivityClass());
        return authenticator.getIBinder();
    }

    public Class getAuthActivityClass(){
        return null;
    }
}