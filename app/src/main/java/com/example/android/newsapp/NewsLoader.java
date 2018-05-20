package com.example.android.newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = "EarthquakeLoader.java";
    private String downloadUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        if (url != null) {
            downloadUrl = url;
        }
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        //create URL object
        if (downloadUrl == null) {
            return null;
        }
        //Perform HTTP request to the URL and receive a JSON response back
        List<News> newsList = QueryUtils.fetchNewsData(downloadUrl);
        return newsList;
    }
}
