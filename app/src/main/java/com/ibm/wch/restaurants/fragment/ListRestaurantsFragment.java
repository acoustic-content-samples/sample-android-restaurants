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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.ibm.wch.restaurants.MainActivity;
import com.ibm.wch.restaurants.R;
import com.ibm.wch.restaurants.WchConfig;
import com.ibm.wch.restaurants.authentication.Login;
import com.ibm.wch.restaurants.authentication.LoginDelegate;
import com.ibm.wch.restaurants.element.ImageElement;
import com.ibm.wch.restaurants.element.Rendition;
import com.ibm.wch.restaurants.element.ValueElement;
import com.ibm.wch.restaurants.restaurant.RestaurantListAdapter;
import com.ibm.wch.restaurants.restaurant.RestaurantModel;
import com.ibm.wch.restaurants.search.SearchDocumentModel;
import com.ibm.wch.restaurants.search.SearchQuery;
import com.ibm.wch.restaurants.search.SearchService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListRestaurantsFragment extends ListFragment implements LoginDelegate,
        LocationListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    private static final int LOCATION_REQUEST_CODE = 12;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private SearchQuery mSearchQuery;
    private RestaurantListAdapter mRestaurantListAdapter;
    private Location mLocation;
    private boolean mRestaurantsLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String name = getArguments().getString("name");
        String category = getArguments().getString("category");
        Log.d("listRestaurants", "Name: " + name + ", category: " + category);
        mSearchQuery = new SearchQuery().setName(name).setCategory(category);

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).addConnectionCallbacks(this)
                .addApi(LocationServices.API).addOnConnectionFailedListener(this).build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000)
                .setFastestInterval(1000);
        mGoogleApiClient.connect();

        return inflater.inflate(R.layout.fragment_list_restaurants, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!mRestaurantsLoaded) {
            performSearch();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getString(R.string.search_results));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        RestaurantFragment fragment = new RestaurantFragment();
        Bundle args = new Bundle();
        args.putString("contentId", mRestaurantListAdapter.getItem(position).getId());
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_main, fragment)
                .addToBackStack(getString(R.string.search_results)).commit();
    }

    /**
     * Perform the search.
     */
    private void performSearch() {
        MainActivity activity = (MainActivity) this.getActivity();
        Login login = activity.getLogin();

        if (login.isLoggedIn()) {
            new SearchTask(login, this).execute();
        } else {
            Log.d("listRestaurants", "Not logged in");
            login.doLogin(getActivity(), this);
        }
    }

    /**
     * Task to search for the restaurants and populate them in the list.
     */
    private class SearchTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog mDialog;
        LoginDelegate mDelegate;
        SearchService mSearchService;
        Login mLogin;

        SearchTask(Login login, LoginDelegate delegate) {
            mLogin = login;
            mSearchService = new SearchService(login);
            mDelegate = delegate;
        }

        protected void onPreExecute() {
            mDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait),
                    getString(R.string.searching_restaurants));
        }

        protected Integer doInBackground(String... params) {
            return mSearchService.fetchSearchResults(mSearchQuery);
        }

        protected void onPostExecute(Integer status) {
            if (status == 200) {
                List<RestaurantModel> restaurants = new ArrayList<>();
                if(mSearchService.getResults().getNumFound() > 0) {
                    for(SearchDocumentModel document : mSearchService.getResults().getDocuments()) {
                        Gson gson = new Gson();
                        Log.d("listRestaurants", document.getDocument());
                        RestaurantModel restaurant = gson.fromJson(document.getDocument(),
                                RestaurantModel.class);
                        restaurants.add(restaurant);
                    }

                    mRestaurantListAdapter = new RestaurantListAdapter(restaurants, getActivity());
                    setListAdapter(mRestaurantListAdapter);

                    if(mLocation != null) {
                        updateDistances(mLocation);
                    }
                    loadImages(mLogin);
                    mRestaurantsLoaded = true;
                }
            } else if (status == 403) {
                mLogin.doLogin(getActivity(), mDelegate);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.unable_to_connect_to_service));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(android.R.string.yes),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new SearchTask(mLogin, mDelegate).execute();
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
     * Update the distance from the specified location for all the restaurant results.
     *
     * @param location
     */
    private void updateDistances(Location location) {
        mLocation = location;
        Log.d("listRestaurants", "updateDistances: " + location.getLatitude() + ", " +
                location.getLongitude());
        if(mRestaurantListAdapter != null && !mRestaurantListAdapter.getRestaurants().isEmpty()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            for(int i = 0; i < mRestaurantListAdapter.getRestaurants().size(); i++) {
                new DistanceTask(location, i).execute();
            }
        }
    }

    /**
     * Task to lookup coordinates of each restaurant and calculate the distance to it.
     */
    private class DistanceTask extends AsyncTask<String, Void, String> {
        Geocoder mGeocoder;
        Location mLocation;
        int mIndex;

        DistanceTask(Location location, int index) {
            mLocation = location;
            mIndex = index;
        }

        protected void onPreExecute() {
            mGeocoder = new Geocoder(getContext(), Locale.getDefault());
        }

        protected String doInBackground(String... params) {
            double currentLongitude = mLocation.getLongitude();
            double currentLatitude = mLocation.getLatitude();
            RestaurantModel restaurant = mRestaurantListAdapter.getItem(mIndex);
            ValueElement address = restaurant.getElements().getAddress();

            String distance = null;
            if(address != null) {
                try {
                    Log.d("listRestaurants", "Getting distance from " + address.getValue());
                    List<Address> addresses = mGeocoder.getFromLocationName(address.getValue() +
                            " " + WchConfig.COUNTRY_OF_ALL_RESTAURANTS, 1);
                    if(!addresses.isEmpty()) {
                        double latitude = addresses.get(0).getLatitude();
                        double longitude = addresses.get(0).getLongitude();

                        float[] results = new float[1];
                        Location.distanceBetween(currentLatitude, currentLongitude, latitude,
                                longitude, results);
                        DecimalFormat decimalFormat = new DecimalFormat("#.#");
                        distance = decimalFormat.format(results[0] / 1000) + "km";
                    }
                }
                catch (IOException ex) {
                    Log.d("listRestaurants", "Failed to retrieve distance", ex);
                }
            }
            return distance;
        }

        protected void onPostExecute(String distance) {
            if(distance != null) {
                mRestaurantListAdapter.getItem(mIndex).setDistance(distance);
                mRestaurantListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Load the thumbnail rendition for the restaurant results.
     *
     * @param login
     */
    private void loadImages(Login login) {
        if(mRestaurantListAdapter != null && !mRestaurantListAdapter.getRestaurants().isEmpty()) {
            for(int i = 0; i < mRestaurantListAdapter.getRestaurants().size(); i++) {
                RestaurantModel restaurant = mRestaurantListAdapter.getItem(i);
                mRestaurantListAdapter.getItem(i).setImage(null);
                if(restaurant.getElements().hasPhoto()) {
                    ImageElement image = restaurant.getElements().getPhoto();
                    if(image.hasRendition(WchConfig.LIST_RESTAURANT_RENDITION_PROFILE)) {
                        Rendition rendition = image.getRendition(WchConfig.LIST_RESTAURANT_RENDITION_PROFILE);
                        new ImageTask(login, i).execute(rendition.getSource());
                    }
                }
            }
        }
    }

    /**
     * Task to load a thumbnail for a restaurant.
     */
    private class ImageTask extends AsyncTask<String, Void, Integer> {
        private Login mLogin;
        private Bitmap mBitmap;
        int mIndex;

        ImageTask(Login login, int index) {
            mLogin = login;
            mIndex = index;
        }

        protected Integer doInBackground(String... params) {
            mBitmap = null;
            int status = 0;
            try {
                String url = mLogin.getBaseUrl() + params[0];
                Log.d("listRestaurantImage", "Rendition url: " + url);
                HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                for (String cookie : mLogin.getCookies()) {
                    c.addRequestProperty("Cookie", cookie);
                }
                c.connect();

                status = c.getResponseCode();
                Log.d("listRestaurantImage", "Status: " + status);

                if (status == 200) {
                    InputStream in = c.getInputStream();
                    mBitmap = BitmapFactory.decodeStream(in);
                }
            }
            catch (Exception e) {
                Log.e("listRestaurantImage", "Failed to load rendition", e);
                e.printStackTrace();
            }
            return status;
        }

        protected void onPostExecute(Integer status) {
            if (status == 200) {
                mRestaurantListAdapter.getItem(mIndex).setImage(mBitmap);
                mRestaurantListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if((Build.VERSION.SDK_INT < 23 ||
                (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(location != null) {
                updateDistances(location);
            }
            else {
                Log.d("listRestaurants", "Requesting location");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateDistances(location);
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    @Override
    public void loginSuccess() {
        performSearch();
    }
}
