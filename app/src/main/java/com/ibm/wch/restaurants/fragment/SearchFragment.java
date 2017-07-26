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

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ibm.wch.restaurants.MainActivity;
import com.ibm.wch.restaurants.R;
import com.ibm.wch.restaurants.WchConfig;
import com.ibm.wch.restaurants.authentication.Login;
import com.ibm.wch.restaurants.authentication.LoginDelegate;
import com.ibm.wch.restaurants.category.CategoryModel;
import com.ibm.wch.restaurants.category.CategoryService;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements LoginDelegate {
    private Spinner mCuisinesSpinner;
    private EditText mNameEditText;
    private boolean mCuisinesLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mCuisinesSpinner = (Spinner) view.findViewById(R.id.categoriesSpinner);
        mNameEditText = (EditText) view.findViewById(R.id.nameEditText);

        Button button = (Button) view.findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!mCuisinesLoaded) {
            loadCuisines();
        }
    }

    /**
     * Perform search with current settings.
     */
    private void search() {
        ListRestaurantsFragment fragment = new ListRestaurantsFragment();
        Bundle args = new Bundle();
        args.putString("name", mNameEditText.getText().toString());
        String category = null;
        CategoryModel categoryModel = ((CategoryModel) mCuisinesSpinner.getSelectedItem());
        if(categoryModel != null && categoryModel.getId() != null) {
            category = categoryModel.getName();
        }
        args.putString("category", category);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.content_main, fragment)
                .addToBackStack(getString(R.string.app_name)).commit();
    }

    /**
     * Load the cuisines.
     */
    private void loadCuisines() {
        MainActivity activity = (MainActivity)getActivity();
        Login login = activity.getLogin();
        if (login.isLoggedIn()) {
            new CuisinesTask(login, this).execute();
        } else {
            Log.d("loadCuisines", "Not logged in");
            login.doLogin(getActivity(), this);
        }
    }

    /**
     * Task to load the cuisine categories to populate the cuisine selector.
     */
    private class CuisinesTask extends AsyncTask<String, Void, Integer> {
        LoginDelegate mDelegate;
        CategoryService mCategoryService;
        Login mLogin;

        CuisinesTask(Login login, LoginDelegate delegate) {
            mLogin = login;
            mCategoryService = new CategoryService(login);
            mDelegate = delegate;
        }

        protected void onPreExecute() {
            List<CategoryModel> categories = new ArrayList<>();
            categories.add(new CategoryModel(null, getString(R.string.loading_cuisines)));
            ArrayAdapter<CategoryModel> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, categories);
            mCuisinesSpinner.setAdapter(adapter);
        }

        protected Integer doInBackground(String... params) {
            return mCategoryService.fetchCategories(WchConfig.CUISINE_TAXONOMY_ID);
        }

        protected void onPostExecute(Integer status) {
            if (status == 200) {
                List<CategoryModel> categories = mCategoryService.getCategories().getItems();
                categories.add(0, new CategoryModel(null, getString(R.string.no_cuisine_selected)));
                ArrayAdapter<CategoryModel> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_dropdown_item, categories);
                mCuisinesSpinner.setAdapter(adapter);
                mCuisinesLoaded = true;
            } else if (status == 403) {
                mLogin.doLogin(getActivity(), mDelegate);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.unable_to_load_cuisines));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(android.R.string.yes),
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new CuisinesTask(mLogin, mDelegate).execute();
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
        }
    }

    @Override
    public void loginSuccess() {
        loadCuisines();
    }
}
