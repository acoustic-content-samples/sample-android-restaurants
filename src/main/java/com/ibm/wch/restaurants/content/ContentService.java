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

package com.ibm.wch.restaurants.content;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.wch.restaurants.authentication.Login;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.ibm.wch.restaurants.ApiUtils.readInputStream;

public class ContentService {
    private Login mLogin;
    private String mJson;

    public ContentService(Login login) {
        mLogin = login;
    }

    public String getContentJson() {
        return mJson;
    }

    /**
     * Fetch the content with the specified ID.
     * The results are put in mJson depending on the status code.
     *
     * @param contentId ID of the content to fetch
     * @return HTTP status code
     */
    public int fetchContent(String contentId) {
        mJson = null;
        int status = 0;
        try {
            String url = mLogin.getAuthoringUrl() + "/content/" + contentId;
            Log.d("fetchContent", "Url: " + url);
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            for (String cookie : mLogin.getCookies()) {
                c.addRequestProperty("Cookie", cookie);
            }
            c.connect();

            status = c.getResponseCode();
            Log.d("fetchContent", "Status: " + status);

            if (status == 200) {
                mJson = readInputStream(c);
            }
        } catch (Exception ex) {
            Log.d("fetchContent", "Failed to fetch content", ex);
        }
        return status;
    }

    /**
     * Update the content with the specified ID using the JSON.
     * This will first fetch the latest revision before doing the update.
     *
     * @param contentId ID of the content to update
     * @param json JSON to update with
     * @return HTTP status code
     */
    public int updateContent(String contentId, String json) {
        if(fetchContent(contentId) == 200) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(mJson, JsonObject.class);
            JsonElement rev = jsonObject.get("rev");

            JsonObject updatedJsonObject = gson.fromJson(json, JsonObject.class);
            updatedJsonObject.addProperty("rev", rev.getAsString());
            return doUpdate(contentId, gson.toJson(updatedJsonObject));
        }
        return 0;
    }

    /**
     * Update the content with the specified ID using the JSON.
     * The revision should already be the latest.
     *
     * @param contentId ID of the content to update
     * @param json JSON to update with
     * @return HTTP status code
     */
    private int doUpdate(String contentId, String json) {
        Log.d("updateContent", json);
        mJson = null;
        int status = 0;
        try {
            String url = mLogin.getAuthoringUrl() + "/content/" + contentId;
            Log.d("updateContent", "Url: " + url);
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            c.setRequestMethod("PUT");
            for (String cookie : mLogin.getCookies()) {
                c.addRequestProperty("Cookie", cookie);
            }
            OutputStreamWriter out = new OutputStreamWriter(c.getOutputStream());
            out.write(json);
            out.close();
            c.connect();

            status = c.getResponseCode();
            Log.d("updateContent", "Status: " + status);

            if (status == 200) {
                mJson = readInputStream(c);
            }
        } catch (Exception ex) {
            Log.d("updateContent", "Failed to update content", ex);
        }
        return status;
    }
}
