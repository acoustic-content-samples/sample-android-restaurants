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

package com.ibm.wch.restaurants;

public final class WchConfig {
    public static final String USERNAME = "[ENTER YOUR USERNAME]";
    public static final String PASSWORD = "[ENTER YOUR PASSWORD]";

    /**
     * ID of the "Cuisine" taxonomy if using the wchtools to import is 96ff4a8c19d6a321fa7aa6ee03f0468a
     */
    public static final String CUISINE_TAXONOMY_ID = "[ENTER ID OF CUISINE TAXONOMY]";

    public static final String API_VERSION = "v1";
    public static final String LOGIN_URL = "https://content-us-1.content-cms.com/api/login/" +
            API_VERSION + "/basicauth";

    public static final String RESTAURANT_CONTENT_TYPE_NAME = "restaurant";
    public static final String RESTAURANT_RENDITION_PROFILE = "mobile";
    public static final String LIST_RESTAURANT_RENDITION_PROFILE = "thumbnail";
    public static final int MAX_RESTAURANT_RATING = 5;

    /**
     * Set country (e.g. Australia) if restaurant's addresses don't include the country
     */
    public static final String COUNTRY_OF_ALL_RESTAURANTS = "";
}
