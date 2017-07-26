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
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ibm.wch.restaurants.R;
import com.ibm.wch.restaurants.element.ValueElement;

/**
 * Single restaurant item in the results list.
 */
public class RestaurantListItem extends RelativeLayout {
    private RestaurantModel mRestaurant;
    private TextView mNameTextView;
    private TextView mDescriptionTextView;
    private TextView mAddressTextView;
    private TextView mHoursTextView;
    private TextView mDistanceTextView;
    private ImageView mImageView;

    public RestaurantListItem(Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mNameTextView = (TextView) findViewById(R.id.nameTextView);
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        mAddressTextView = (TextView) findViewById(R.id.addressTextView);
        mHoursTextView = (TextView) findViewById(R.id.hoursTextView);
        mDistanceTextView = (TextView) findViewById(R.id.distanceTextView);
        mImageView = (ImageView) findViewById(R.id.imageView);
    }

    public void setRestaurant(RestaurantModel restaurant) {
        mRestaurant = restaurant;
        mNameTextView.setText(mRestaurant.getName());
        RestaurantElementsModel elements = restaurant.getElements();
        if(elements.hasSummary()) {
            mDescriptionTextView.setText(elements.getSummary().getValue());
        }

        if(elements.hasAddress()) {
            mAddressTextView.setText(elements.getAddress().getValue());
        }

        ValueElement hours = elements.getHoursToday();
        if(hours != null && hours.getValue() != null) {
            mHoursTextView.setText(hours.getValue());
        }

        String distance = mRestaurant.getDistance() != null ? mRestaurant.getDistance() : "";
        mDistanceTextView.setText(distance);

        if(mRestaurant.getImage() != null) {
            mImageView.setImageBitmap(mRestaurant.getImage());
        }
        else {
            mImageView.setImageResource(R.drawable.ibm_logo);
        }
    }

    public RestaurantModel getRestaurant() {
        return mRestaurant;
    }
}