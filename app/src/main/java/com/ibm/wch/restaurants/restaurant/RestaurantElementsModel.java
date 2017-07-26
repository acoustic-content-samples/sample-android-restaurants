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

import com.ibm.wch.restaurants.element.CategoryElement;
import com.ibm.wch.restaurants.element.ImageElement;
import com.ibm.wch.restaurants.element.LinkElement;
import com.ibm.wch.restaurants.element.NumberElement;
import com.ibm.wch.restaurants.element.ValueElement;

import java.util.Calendar;

/**
 * Representation of the elements in the restaurant content type.
 */
public class RestaurantElementsModel {
    private ValueElement summary;
    private ValueElement address;
    private ValueElement phone;
    private ImageElement photo;
    private CategoryElement cuisine;
    private LinkElement website;
    private ValueElement openingHours1;
    private ValueElement openingHours2;
    private ValueElement openingHours3;
    private ValueElement openingHours4;
    private ValueElement openingHours5;
    private ValueElement openingHours6;
    private ValueElement openingHours7;
    private NumberElement rating;
    private NumberElement totalRatings;

    public ValueElement getSummary() {
        return summary;
    }

    public void setSummary(ValueElement summary) {
        this.summary = summary;
    }

    public boolean hasSummary() {
        return summary != null;
    }

    public ValueElement getAddress() {
        return address;
    }

    public void setAddress(ValueElement address) {
        this.address = address;
    }

    public boolean hasAddress() {
        return address != null;
    }

    public ValueElement getPhone() {
        return phone;
    }

    public void setPhone(ValueElement phone) {
        this.phone = phone;
    }

    public boolean hasPhone() {
        return phone != null;
    }

    public ImageElement getPhoto() {
        return photo;
    }

    public void setPhoto(ImageElement photo) {
        this.photo = photo;
    }

    public boolean hasPhoto() {
        return photo != null;
    }

    public NumberElement getRating() {
        return rating;
    }

    public void setRating(NumberElement rating) {
        this.rating = rating;
    }

    public boolean hasRating() {
        return rating != null;
    }

    public NumberElement getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(NumberElement totalRatings) {
        this.totalRatings = totalRatings;
    }

    public boolean hasTotalRatings() {
        return totalRatings != null;
    }

    public CategoryElement getCuisine() {
        return cuisine;
    }

    public void setCuisine(CategoryElement cuisine) {
        this.cuisine = cuisine;
    }

    public boolean hasCuisine() {
        return cuisine != null;
    }

    public LinkElement getWebsite() {
        return website;
    }

    public void setWebsite(LinkElement website) {
        this.website = website;
    }

    public boolean hasWebsite() {
        return website != null;
    }

    public ValueElement getOpeningHours1() {
        return openingHours1;
    }

    public void setOpeningHours1(ValueElement openingHours1) {
        this.openingHours1 = openingHours1;
    }

    public boolean hasOpeningHours1() {
        return openingHours1 != null;
    }

    public ValueElement getOpeningHours2() {
        return openingHours2;
    }

    public void setOpeningHours2(ValueElement openingHours2) {
        this.openingHours2 = openingHours2;
    }

    public boolean hasOpeningHours2() {
        return openingHours2 != null;
    }

    public ValueElement getOpeningHours3() {
        return openingHours3;
    }

    public void setOpeningHours3(ValueElement openingHours3) {
        this.openingHours3 = openingHours3;
    }

    public boolean hasOpeningHours3() {
        return openingHours3 != null;
    }

    public ValueElement getOpeningHours4() {
        return openingHours4;
    }

    public void setOpeningHours4(ValueElement openingHours4) {
        this.openingHours4 = openingHours4;
    }

    public boolean hasOpeningHours4() {
        return openingHours4 != null;
    }

    public ValueElement getOpeningHours5() {
        return openingHours5;
    }

    public void setOpeningHours5(ValueElement openingHours5) {
        this.openingHours5 = openingHours5;
    }

    public boolean hasOpeningHours5() {
        return openingHours5 != null;
    }

    public ValueElement getOpeningHours6() {
        return openingHours6;
    }

    public void setOpeningHours6(ValueElement openingHours6) {
        this.openingHours6 = openingHours6;
    }

    public boolean hasOpeningHours6() {
        return openingHours6 != null;
    }

    public ValueElement getOpeningHours7() {
        return openingHours7;
    }

    public void setOpeningHours7(ValueElement openingHours7) {
        this.openingHours7 = openingHours7;
    }

    public boolean hasOpeningHours7() {
        return openingHours7 != null;
    }

    public ValueElement getHoursToday() {
        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return getOpeningHours1();
            case Calendar.MONDAY:
                return getOpeningHours2();
            case Calendar.TUESDAY:
                return getOpeningHours3();
            case Calendar.WEDNESDAY:
                return getOpeningHours4();
            case Calendar.THURSDAY:
                return getOpeningHours5();
            case Calendar.FRIDAY:
                return getOpeningHours6();
            case Calendar.SATURDAY:
                return getOpeningHours7();
        }
        return null;
    }
}
