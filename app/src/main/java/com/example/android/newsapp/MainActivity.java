package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    //Api-key need to fetch data by using The Guardian API
    private static final String API_KEY = "acc0381e-10da-4543-b322-f1adea635e65";
    //URL to fetch data from The Guardian
    private static final String NEWS_URL =
            "http://content.guardianapis.com/search";
    //        "http://content.guardianapis.com/search?q=android&show-tags=contributor&api-key=" + API_KEY;

    //TAG for log file
    private static final String LOG_TAG = "MainActivity.java";
    //ID for LoaderManager to download data from The Guardian Site
    private static final int LOADER_ID = 1;
    private NewsAdapter adapter;
    //progressbar that show it is downloading news from internet
    private ProgressBar progressBar;
    //if our ListView is empty we show this TextView
    TextView emptyListTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.download_progressbar);
        emptyListTextView = (TextView) findViewById(R.id.empty_listview);

        adapter = new NewsAdapter(this, new ArrayList<News>());

        ListView newsListView = (ListView) findViewById(R.id.news_list);
        newsListView.setAdapter(adapter);

        //Request status of internet connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        //if there is internet connection, get data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            //if there isnot connection, make invisible download progressbar and
            //show message that there is no internet connection
            progressBar.setVisibility(View.GONE);
            emptyListTextView.setText(R.string.no_internet_connection);
        }

        //In case we don't have any news we show this TextView
        newsListView.setEmptyView(emptyListTextView);

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News clickedNews = adapter.getItem(position);
                Uri newsUrl = Uri.parse(clickedNews.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, newsUrl);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // getString retrieves a String value from the preferences. The second parameter is the default value for this preference.
        String items = sharedPrefs.getString(
                getString(R.string.settings_items_key),
                getString(R.string.settings_items_default)
        );

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_orderby_key),
                getString(R.string.settings_orderby_default)
        );

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(NEWS_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `q=android`
        uriBuilder.appendQueryParameter("q", "android");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", items);
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", API_KEY);

        // Return the completed uri `http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&limit=10&minmag=minMagnitude&orderby=time

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        //make invisible progressbar
        progressBar.setVisibility(View.GONE);
        //set text replaces empty listView (if it is blank)
        emptyListTextView.setText(R.string.empty_news_list);
        //clear the adapter of previous news data
        adapter.clear();
        //if there is a valid list of news date we add them to the adapter.
        //This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        adapter.clear();
    }

    //helper method for testing
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
