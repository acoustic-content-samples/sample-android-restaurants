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

package com.ibm.wch.restaurants.category;

import android.util.Log;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URL;

import com.ibm.wch.restaurants.authentication.Login;

import static com.ibm.wch.restaurants.ApiUtils.readInputStream;

public class CategoryService {
    private Login mLogin;
    private CategoryChildrenModel mCategories;

    public CategoryService(Login login) {
        mLogin = login;
    }

    public CategoryChildrenModel getCategories() {
        return mCategories;
    }

    /**
     * Fetch the child categories of the specified parent category.
     * The results are put in mCategories depending on the status code.
     *
     * @param rootCategoryId ID of the parent category
     * @return HTTP status code
     */
    public int fetchCategories(String rootCategoryId) {
        mCategories = null;
        int status = 0;
        try {
            String url = mLogin.getAuthoringUrl() + "/categories/" + rootCategoryId + "/children";
            Log.d("fetchCategories", "Url: " + url);
            HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
            for (String cookie : mLogin.getCookies()) {
                c.addRequestProperty("Cookie", cookie);
            }
            c.connect();

            status = c.getResponseCode();
            Log.d("fetchCategories", "Status: " + status);

            if (status == 200) {
                String json = readInputStream(c);

                Gson gson = new Gson();
                mCategories = gson.fromJson(json, CategoryChildrenModel.class);
                Log.d("fetchCategories", "size: " + mCategories.getItems().size());
            }
        } catch (Exception ex) {
            Log.d("fetchCategories", "Failed to fetch categories", ex);
        }
        return status;
    }
}
