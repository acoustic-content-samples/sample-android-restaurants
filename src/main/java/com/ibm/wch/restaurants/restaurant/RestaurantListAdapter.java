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
package com.ibm.wch.restaurants.restaurant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ibm.wch.restaurants.R;

import java.util.List;

public class RestaurantListAdapter extends BaseAdapter {
    private Context mContext;
    private List<RestaurantModel> mRestaurants;

    public RestaurantListAdapter(List<RestaurantModel> restaurants, Context context) {
        super();
        mRestaurants = restaurants;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mRestaurants.size();
    }

    @Override
    public RestaurantModel getItem(int position) {
        return (null == mRestaurants) ? null : mRestaurants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)  {
        RestaurantListItem item;
        if(convertView == null) {
            item = (RestaurantListItem)View.inflate(mContext, R.layout.restaurant_list_item, null);
        }
        else {
            item = (RestaurantListItem)convertView;
        }
        item.setRestaurant(mRestaurants.get(position));
        return item;
    }

    public List<RestaurantModel> getRestaurants() {
        return mRestaurants;
    }
}
