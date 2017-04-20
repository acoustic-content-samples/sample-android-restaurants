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

import android.graphics.Bitmap;

import com.ibm.wch.restaurants.content.ContentModel;

/**
 * Representation of a restaurant content item.
 */
public class RestaurantModel extends ContentModel {
    private RestaurantElementsModel elements;
    private String mJson;
    private String mDistance;
    private Bitmap mImage;

    public RestaurantElementsModel getElements() {
        return elements;
    }

    public void setElements(RestaurantElementsModel elements) {
        this.elements = elements;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        this.mDistance = distance;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        this.mImage = image;
    }

    public String getJson() {
        return mJson;
    }

    public void setJson(String mJson) {
        this.mJson = mJson;
    }
}
