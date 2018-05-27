package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>> {
    //Api-key need to fetch data by using The Guardian API
    private static final String API_KEY = "";
    //URL to fetch data from The Guardian
    private static final String NEWS_URL =
            "http://content.guardianapis.com/search?q=android&show-tags=contributor&api-key=" + API_KEY;
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
    public Loader<List<News>> onCreateLoader(int id, Bundle bundle) {
        Loader<List<News>> loader = null;
        if (id == LOADER_ID) {

            loader = new NewsLoader(this, NEWS_URL);
        }
        return loader;
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
}
