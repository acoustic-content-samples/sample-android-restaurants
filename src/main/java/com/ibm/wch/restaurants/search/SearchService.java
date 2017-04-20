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

package com.ibm.wch.restaurants.search;

import android.util.Log;

import com.google.gson.Gson;
import com.ibm.wch.restaurants.authentication.Login;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.ibm.wch.restaurants.ApiUtils.readInputStream;

public class SearchService {
    private Login mLogin;
    private SearchModel mResults;

    public SearchService(Login login) {
        mLogin = login;
    }

    public SearchModel getResults() {
        return mResults;
    }

    /**
     * Fetch the search results based on the specified search query.
     * The results are put in mResults depending on the status code.
     *
     * @param searchQuery
     * @return HTTP status code
     */
    public int fetchSearchResults(SearchQuery searchQuery) {
        mResults = null;
        int status = 0;
        try {
            String url = mLogin.getAuthoringUrl() + "/search?" + searchQuery.getQuery();
            Log.d("fetchSearchResults", "Url: " + url);
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            for (String cookie : mLogin.getCookies()) {
                c.addRequestProperty("Cookie", cookie);
            }
            c.connect();

            status = c.getResponseCode();
            Log.d("fetchSearchResults", "Status: " + status);

            if (status == 200) {
                String json = readInputStream(c);

                Gson gson = new Gson();
                mResults = gson.fromJson(json, SearchModel.class);
                Log.d("fetchSearchResults", "size: " + mResults.getNumFound());
            }
        } catch (Exception ex) {
            Log.d("fetchSearchResults", "Failed to fetch search results", ex);
        }
        return status;
    }
}