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

import com.ibm.wch.restaurants.WchConfig;

public class SearchQuery {
    private String mName;
    private String mCategory;
    private int mOffset = 0;

    /**
     * Get the query parameters based on the configured options.
     *
     * @return Search query
     */
    public String getQuery() {
        String query = "defType=edismax&wt=json&fq=classification:(content)" +
                "&fq=type:(%22" + WchConfig.RESTAURANT_CONTENT_TYPE_NAME + "%22)" +
                "&name%20asc&rows=100&start=" + mOffset +
                "&qf=name^20+type^10+description+creator+lastModifier+tags+categories+text";
        if(mName == null || mName.equals("")) {
            query += "&q=*:*";
        }
        else {
            query += "&q=((*" + mName + "*))";
        }
        if(mCategory != null) {
            query += "&fq=categoryLeaves:(%22" + mCategory + "%22)";
        }
        return query;
    }

    public String getName() {
        return mName;
    }

    public SearchQuery setName(String mName) {
        this.mName = mName;
        return this;
    }

    public String getCategory() {
        return mCategory;
    }

    public SearchQuery setCategory(String category) {
        this.mCategory = category;
        return this;
    }

    public int getOffset() {
        return mOffset;
    }

    public SearchQuery setOffset(int mOffset) {
        this.mOffset = mOffset;
        return this;
    }
}
