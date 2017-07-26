/**
 * Copyright IBM Corp. 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.wch.restaurants.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.ibm.wch.restaurants.ApiUtils;
import com.ibm.wch.restaurants.R;
import com.ibm.wch.restaurants.WchConfig;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Login {
    private boolean mLoggingIn = false;
    private boolean mLoggedIn = false;
    private String mBaseUrl;
    private List<String> mCookies;

    /**
     * Login
     *
     * @param activity
     * @param delegate
     */
    public void doLogin(Activity activity, LoginDelegate delegate) {
        if (!isLoggingIn()) {
            new LoginTask(activity, delegate).execute();
        } else {
            Log.d("login", "Already logging in");
        }
    }

    /**
     * Task to login and perform post login operations.
     */
    private class LoginTask extends AsyncTask<String, Void, Void> {
        ProgressDialog mDialog;
        LoginDelegate mDelegate;
        Activity mActivity;

        LoginTask(Activity activity, LoginDelegate delegate) {
            mDelegate = delegate;
            mActivity = activity;
        }

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(mActivity, mActivity.getString(R.string.please_wait),
                    mActivity.getString(R.string.loading));
        }

        protected Void doInBackground(String... params) {
            login();
            return null;
        }

        protected void onPostExecute(Void result) {
            if (isLoggedIn()) {
                mDelegate.loginSuccess();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage(mActivity.getString(R.string.unable_to_connect_to_service));
                builder.setCancelable(true);
                builder.setPositiveButton(mActivity.getString(android.R.string.yes),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new LoginTask(mActivity, mDelegate).execute();
                    }
                });
                builder.setNegativeButton(mActivity.getString(android.R.string.no),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            mDialog.dismiss();
        }
    }

    /**
     * Login using the WCH API.
     *
     * @return True if logged in
     */
    private boolean login() {
        Log.d("login", "Logging in...");
        mLoggingIn = true;
        mLoggedIn = false;
        try {
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            HttpURLConnection c = (HttpURLConnection) new URL(WchConfig.LOGIN_URL).openConnection();
            String encoded = new String(Base64.encode((WchConfig.USERNAME + ":" +
                    WchConfig.PASSWORD).getBytes(), Base64.DEFAULT));
            c.setRequestProperty("Authorization", "Basic " + encoded);
            c.setUseCaches(false);
            c.connect();

            int status = c.getResponseCode();
            Log.d("login", "Status: " + status);

            if (status == 200) {
                String json = ApiUtils.readInputStream(c);

                Gson gson = new Gson();
                LoginTenantModel[] loginTenants = gson.fromJson(json, LoginTenantModel[].class);

                if (loginTenants.length > 0) {
                    mCookies = c.getHeaderFields().get("Set-Cookie");
                    LoginTenantModel loginTenant = loginTenants[0];
                    mLoggedIn = true;
                    mBaseUrl = loginTenant.getBaseUrl();
                    Log.d("login", "tenantId: " + loginTenant.getTenantId());
                }
            }
        } catch (Exception ex) {
            Log.d("login", "Login failed", ex);
        } finally {
            mLoggingIn = false;
        }
        return mLoggedIn;
    }

    /**
     * Get the base URL according to the login.
     *
     * @return Base URL
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    /**
     * Get the full authoring URL including the version.
     *
     * @return Authoring URL
     */
    public String getAuthoringUrl() {
        return mBaseUrl + "/authoring/" + WchConfig.API_VERSION;
    }

    /**
     * Get the cookies from the login.
     *
     * @return Cookies
     */
    public List<String> getCookies() {
        return mCookies;
    }

    /**
     * Check if logged in.
     *
     * @return True if logged in
     */
    public boolean isLoggedIn() {
        return mLoggedIn;
    }

    /**
     * Check if currently logging in.
     *
     * @return True if login is in progress.
     */
    public boolean isLoggingIn() {
        return mLoggingIn;
    }
}
