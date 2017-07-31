package com.codepath.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticleArrayAdapter;
import com.codepath.nytimessearch.customlistener.EndlessScrollListener;
import com.codepath.nytimessearch.fragments.SettingsFragment;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.SearchFilter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity
                        implements Serializable, SettingsFragment.SettingsDialogListener {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;
    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;
    SearchFilter searchFilterToUse;
    private final int REQUEST_CODE = 10;
    Integer currentPage = 0;
    String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

    }

    public void setupViews(){
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);
        //listener for grid item click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //create intent to open article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

                //get data item at current position
                Article article = articles.get(position);
                //pass data to the new activity
                i.putExtra("article", article);
                //start activity
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page + 1);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        searchFilterToUse = new SearchFilter("","",new ArrayList<String>());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                prepareAPIRequest(0); //TO RELOAD WITH FILTERED RESULTS
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view){
        prepareAPIRequest(currentPage);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
        prepareAPIRequest(offset);
    }

    public void prepareAPIRequest(int pageToUse){
        try{
            if (!isOnline() || !isNetworkAvailable()) {
                throw new Exception("Internet access unavailable. Please check your connection.");
            }
            else {
                //final String query = etQuery.getText().toString();
                final String query = searchView.getQuery().toString();
                //Toast.makeText(this, "Searching for " + query  , Toast.LENGTH_SHORT).show();

                sendAPIRequest(query, pageToUse);
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage() , Toast.LENGTH_LONG).show();
        }
    }
    public void sendAPIRequest(String query, int pageToUse){
        currentPage = pageToUse;
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("api-key", getString(R.string.NYTS_API_KEY).toString());
        params.put("page",currentPage);
        params.put("q", query);

        if (searchFilterToUse.getBeginDate().trim().length() > 0)
            params.put("begin_date", searchFilterToUse.getBeginDate().trim());

        if (searchFilterToUse.getSortOrder().trim().length() > 0)
            params.put("sort", searchFilterToUse.getSortOrder().trim());

        //Log.d("DEBUG",searchFilterToUse.getNewsDeskValuesSingleString().trim());
        if (searchFilterToUse.getNewsDeskValuesSingleString().trim().length() > 0)
            params.put("fq", searchFilterToUse.getNewsDeskValuesSingleString().trim());

        client.get(url,params,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    if (currentPage == 0)
                        adapter.clear();
                    if (articleJsonResults.length() > 0){
                        adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    } else {
                        if (currentPage == 0)
                            Toast.makeText(getApplicationContext(), "No results were found", Toast.LENGTH_LONG).show();
                    }

                    Log.d("DEBUG", articles.toString());
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public void OnFilterClick(MenuItem item) {
        //USE ACTIVITY AND INTENT
        //
        //Intent i = new Intent(SearchActivity.this, SettingsActivity.class);
        //i.putExtra("search_filter", searchFilterToUse);
        //startActivityForResult(i,REQUEST_CODE);

        //USE FRAGMENT
        //
        showSettingsDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            searchFilterToUse = (SearchFilter) data.getSerializableExtra("search_filter");
            prepareAPIRequest(0); //TO RELOAD WITH SEARCH RESULTS
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public void onFinishSettingsDialog(SearchFilter searchFilterDefined) {
        this.searchFilterToUse = searchFilterDefined;
        prepareAPIRequest(0); //TO RELOAD WITH SEARCH RESULTS
    }

    private void showSettingsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsFragment settingsFragment = SettingsFragment.newInstance(searchFilterToUse);
        settingsFragment.show(fm, "fragment_settings");
    }
}
