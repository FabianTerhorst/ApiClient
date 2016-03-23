package io.fabianterhorst.apiclient.accountmanager;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ApiAuthenticatorActivity extends AppCompatActivity {

    private AccountAuthenticatorResponse mAccountAuthenticatorResponse = null;
    private Bundle mResultBundle = null;
    private String mAccountType, mAccountName, mAuthToken, mAccountPassword, mAuthTokenType;
    private AccountManager mAccountManager;

    /**
     * Retrieves the AccountAuthenticatorResponse from either the intent of the savedInstanceState, if the
     * savedInstanceState is non-zero.
     * @param savedInstanceState the save instance data of this Activity, may be null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountAuthenticatorResponse =
                getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (mAccountAuthenticatorResponse != null) {
            mAccountAuthenticatorResponse.onRequestContinued();
        }

        mAccountManager = AccountManager.get(getBaseContext());
        mAccountType = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
        mAccountName = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(AccountManager.AUTHENTICATOR_ATTRIBUTES_NAME);
    }

    public void login(){
        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, mAccountName);
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
        data.putString(AccountManager.KEY_AUTHTOKEN, mAuthToken);
        data.putString(AccountManager.KEY_PASSWORD, mAccountPassword);
        Intent res = new Intent();
        res.putExtras(data);
        finishLogin(res);
    }

    private void finishLogin(Intent intent) {

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(AccountManager.KEY_PASSWORD);
        Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(AccountManager.ACTION_AUTHENTICATOR_INTENT, false)) {
            String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, mAuthTokenType, authToken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Set the result that is to be sent as the result of the request that caused this
     * Activity to be launched. If result is null or this method is never called then
     * the request will be canceled.
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        mResultBundle = result;
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    public void finish() {
        if (mAccountAuthenticatorResponse != null) {
            // send the result bundle back if set, otherwise send an error.
            if (mResultBundle != null) {
                mAccountAuthenticatorResponse.onResult(mResultBundle);
            } else {
                mAccountAuthenticatorResponse.onError(AccountManager.ERROR_CODE_CANCELED,
                        "canceled");
            }
            mAccountAuthenticatorResponse = null;
        }
        super.finish();
    }

    public void setAccountPassword(String accountPassword) {
        this.mAccountPassword = accountPassword;
    }

    public boolean addNewAccount(){
        return getIntent().getBooleanExtra(AccountManager.ACTION_AUTHENTICATOR_INTENT, false);
    }

    public void setAuthToken(String authToken) {
        this.mAuthToken = authToken;
    }

    public void setAccountName(String accountName) {
        this.mAccountName = accountName;
    }

    public void setAccountType(String accountType) {
        this.mAccountType = accountType;
    }

    public void setAuthTokenType(String mAuthTokenType) {
        this.mAuthTokenType = mAuthTokenType;
    }

    public String getAccountType() {
        return mAccountType;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public String getAccountPassword() {
        return mAccountPassword;
    }

    public Class getTarget(){
        return null;
    }

    public AccountManager getAccountManager() {
        return mAccountManager;
    }
}
