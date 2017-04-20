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

package com.ibm.wch.restaurants.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ibm.wch.restaurants.MainActivity;
import com.ibm.wch.restaurants.R;
import com.ibm.wch.restaurants.WchConfig;
import com.ibm.wch.restaurants.authentication.Login;
import com.ibm.wch.restaurants.authentication.LoginDelegate;
import com.ibm.wch.restaurants.content.ContentService;
import com.ibm.wch.restaurants.element.ImageElement;
import com.ibm.wch.restaurants.element.Rendition;
import com.ibm.wch.restaurants.restaurant.RestaurantElementsModel;
import com.ibm.wch.restaurants.restaurant.RestaurantModel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class RestaurantFragment extends Fragment implements LoginDelegate {
    private static final int CALL_REQUEST_CODE = 14;

    private String mContentId;
    private RestaurantModel mRestaurant;
    private ImageView mImageView;
    private TextView mDescriptionTextView;
    private TextView mAddressTextView;
    private TextView mWebsiteTextView;
    private TextView mSunHoursTextView;
    private TextView mMonHoursTextView;
    private TextView mTueHoursTextView;
    private TextView mWedHoursTextView;
    private TextView mThuHoursTextView;
    private TextView mFriHoursTextView;
    private TextView mSatHoursTextView;
    private Button mCallButton;
    private Button mMapsButton;
    private Button mWebsiteButton;
    private TextView mRatingTextView;
    private boolean mRestaurantLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentId = getArguments().getString("contentId");
        Log.d("viewRestaurant", "contentId: " + mContentId);

        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        mWebsiteTextView = (TextView) view.findViewById(R.id.websiteTextView);
        mDescriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        mAddressTextView = (TextView) view.findViewById(R.id.addressTextView);
        mSunHoursTextView = (TextView) view.findViewById(R.id.sunHoursTextView);
        mMonHoursTextView = (TextView) view.findViewById(R.id.monHoursTextView);
        mTueHoursTextView = (TextView) view.findViewById(R.id.tueHoursTextView);
        mWedHoursTextView = (TextView) view.findViewById(R.id.wedHoursTextView);
        mThuHoursTextView = (TextView) view.findViewById(R.id.thuHoursTextView);
        mFriHoursTextView = (TextView) view.findViewById(R.id.friHoursTextView);
        mSatHoursTextView = (TextView) view.findViewById(R.id.satHoursTextView);
        mCallButton = (Button) view.findViewById(R.id.callButton);
        mCallButton.setOnClickListener(onCallButtonClick());
        mMapsButton = (Button) view.findViewById(R.id.mapsButton);
        mMapsButton.setOnClickListener(onMapsButtonClick());
        mWebsiteButton = (Button) view.findViewById(R.id.websiteButton);
        mWebsiteButton.setOnClickListener(onWebsiteClick());
        mRatingTextView = (TextView) view.findViewById(R.id.ratingTextView);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(onRatingChange());

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.CALL_PHONE
            }, CALL_REQUEST_CODE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!mRestaurantLoaded) {
            loadRestaurant();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mRestaurant != null) {
            ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
            if(actionBar != null) {
                actionBar.setTitle(mRestaurant.getName());
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private View.OnClickListener onCallButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT < 23 ||
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE ) == PackageManager.PERMISSION_GRANTED) {
                    if (mRestaurant != null && mRestaurant.getElements().hasPhone()) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + mRestaurant.getElements().getPhone()
                                .getValue()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    }
                }
            }
        };
    }

    private View.OnClickListener onMapsButtonClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRestaurant != null && mRestaurant.getElements().hasAddress()) {
                    String url = "http://maps.google.com/maps?q=" +
                            mRestaurant.getElements().getAddress().getValue();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                }
            }
        };
    }

    private View.OnClickListener onWebsiteClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRestaurant != null && mRestaurant.getElements().hasWebsite()) {
                    String url = mRestaurant.getElements().getWebsite().getLinkURL();
                    if(!url.startsWith("http")) {
                        url = "http://" + url;
                    }
                    Log.d("restaurantWebsite", url);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                }
            }
        };
    }

    /**
     * Load the details of the restaurant specified.
     */
    private void loadRestaurant() {
        MainActivity activity = (MainActivity) this.getActivity();
        Login login = activity.getLogin();

        if (login.isLoggedIn()) {
            new RestaurantTask(login, this).execute();
        } else {
            Log.d("viewRestaurant", "Not logged in");
            login.doLogin(getActivity(), this);
        }
    }

    /**
     * Task to load the restaurant and update the details.
     */
    private class RestaurantTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog mDialog;
        LoginDelegate mDelegate;
        ContentService mContentService;
        Login mLogin;

        RestaurantTask(Login login, LoginDelegate delegate) {
            mLogin = login;
            mContentService = new ContentService(login);
            mDelegate = delegate;
        }

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait),
                    getString(R.string.loading_restaurant));
        }

        protected Integer doInBackground(String... params) {
            return mContentService.fetchContent(mContentId);
        }

        protected void onPostExecute(Integer status) {
            if (status == 200) {
                Log.d("viewRestaurant", mContentService.getContentJson());
                Gson gson = new Gson();
                mRestaurant = gson.fromJson(mContentService.getContentJson(),
                        RestaurantModel.class);
                mRestaurant.setJson(mContentService.getContentJson());
                updateRestaurantDetails(mLogin);
                mRestaurantLoaded = true;
            } else if (status == 403) {
                mLogin.doLogin(getActivity(), mDelegate);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.unable_to_connect_to_service));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(android.R.string.yes),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new RestaurantTask(mLogin, mDelegate).execute();
                    }
                });
                builder.setNegativeButton(getString(android.R.string.no),
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
     * Update the interface to display all the restaurant's details.
     *
     * @param login
     */
    private void updateRestaurantDetails(Login login) {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(mRestaurant.getName());
        }
        RestaurantElementsModel elements = mRestaurant.getElements();
        if(elements.hasSummary()) {
            mDescriptionTextView.setText(elements.getSummary().getValue());
        }

        if(elements.hasAddress()) {
            mAddressTextView.setText(elements.getAddress().getValue());
        }
        else {
            mMapsButton.setVisibility(View.GONE);
        }

        if(elements.hasWebsite()) {
            mWebsiteTextView.setText(elements.getWebsite().getLinkURL());
        }
        else {
            mWebsiteButton.setVisibility(View.GONE);
        }

        if(elements.hasOpeningHours1()) {
            mSunHoursTextView.setText(elements.getOpeningHours1().getValue());
        }
        if(elements.hasOpeningHours2()) {
            mMonHoursTextView.setText(elements.getOpeningHours2().getValue());
        }
        if(elements.hasOpeningHours3()) {
            mTueHoursTextView.setText(elements.getOpeningHours3().getValue());
        }
        if(elements.hasOpeningHours4()) {
            mWedHoursTextView.setText(elements.getOpeningHours4().getValue());
        }
        if(elements.hasOpeningHours5()) {
            mThuHoursTextView.setText(elements.getOpeningHours5().getValue());
        }
        if(elements.hasOpeningHours6()) {
            mFriHoursTextView.setText(elements.getOpeningHours6().getValue());
        }
        if(elements.hasOpeningHours7()) {
            mSatHoursTextView.setText(elements.getOpeningHours7().getValue());
        }

        if(elements.hasPhone()) {
            String format = getString(R.string.call_number);
            mCallButton.setText(String.format(format, elements.getPhone().getValue()));
        }
        else {
            mCallButton.setVisibility(View.GONE);
        }

        if(elements.hasRating() && elements.hasTotalRatings() && elements.getTotalRatings().getValue() > 0) {
            updateRatingInterface(elements.getRating().getValue(),
                    elements.getTotalRatings().getValue());
        }

        if(elements.hasPhoto()) {
            ImageElement image = elements.getPhoto();
            if(image.hasRendition(WchConfig.RESTAURANT_RENDITION_PROFILE)) {
                Rendition rendition = image.getRendition(WchConfig.RESTAURANT_RENDITION_PROFILE);
                new ImageTask(login).execute(rendition.getSource());
            }
        }
    }

    /**
     * Task to load the mobile rendition for the restaurant.
     */
    private class ImageTask extends AsyncTask<String, Void, Integer> {
        private Login mLogin;
        private Bitmap mBitmap;

        ImageTask(Login login) {
            mLogin = login;
        }
        protected Integer doInBackground(String... params) {
            mBitmap = null;
            int status = 0;
            try {
                String url = mLogin.getBaseUrl() + params[0];
                Log.d("restaurantImage", "Rendition url: " + url);
                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                for (String cookie : mLogin.getCookies()) {
                    c.addRequestProperty("Cookie", cookie);
                }
                c.connect();

                status = c.getResponseCode();
                Log.d("restaurantImage", "Status: " + status);

                if (status == 200) {
                    InputStream in = c.getInputStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                }
            }
            catch (Exception e) {
                Log.e("restaurantImage", "Failed to load rendition", e);
                e.printStackTrace();
            }
            return status;
        }

        protected void onPostExecute(Integer status) {
            if (status == 200) {
                mImageView.setImageBitmap(mBitmap);
            }
        }
    }

    private RatingBar.OnRatingBarChangeListener onRatingChange() {
        return new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(mRestaurant != null && mRestaurant.getElements().hasRating() &&
                        mRestaurant.getElements().hasTotalRatings()) {
                    double rating = mRestaurant.getElements().getRating().getValue();
                    double totalRatings = mRestaurant.getElements().getTotalRatings().getValue();
                    Log.d("restaurantRating", "original: " + rating + ", totalRatings: " + totalRatings);
                    rating = rating * totalRatings + v;
                    totalRatings ++;
                    rating = rating / totalRatings;
                    Log.d("restaurantRating", "new: " + rating + ", totalRatings: " + totalRatings);

                    updateRatingInterface(rating, totalRatings);

                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(mRestaurant.getJson(), JsonObject.class);
                    JsonObject elementsObject = jsonObject.getAsJsonObject("elements");
                    if(elementsObject != null) {
                        JsonObject ratingObject = elementsObject.getAsJsonObject("rating");
                        JsonObject totalRatingsObject = elementsObject.getAsJsonObject("totalRatings");
                        if (ratingObject != null && totalRatingsObject != null) {
                            Log.d("restaurantRating", "Original rating: " + ratingObject.toString()
                                    + ", total: " + totalRatingsObject.toString());
                            ratingObject.addProperty("value", rating);
                            totalRatingsObject.addProperty("value", totalRatings);
                            Log.d("restaurantRating", "New rating: " + ratingObject.toString()
                                    + ", total: " + totalRatingsObject.toString());

                            String json = gson.toJson(jsonObject);
                            new RateTask().execute(json);
                        }
                    }
                }
            }
        };
    }

    /**
     * Update the ratings currently shown in the interface.
     *
     * @param rating
     * @param totalRatings
     */
    private void updateRatingInterface(double rating, double totalRatings) {
        String format = getString(R.string.rating);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        mRatingTextView.setText(String.format(format, decimalFormat.format(rating),
                Integer.toString(WchConfig.MAX_RESTAURANT_RATING),
                Integer.toString((int)totalRatings)));
    }

    /**
     * Task to update the content's ratings based on your input.
     */
    private class RateTask extends AsyncTask<String, Void, Integer> {
        ContentService mContentService;

        RateTask() {
            MainActivity activity = (MainActivity) getActivity();
            mContentService = new ContentService(activity.getLogin());
        }

        protected Integer doInBackground(String... params) {
            return mContentService.updateContent(mContentId, params[0]);
        }
    }

    @Override
    public void loginSuccess() {
        loadRestaurant();
    }
}
