package com.example.android.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving news using The Guardian API.
 */
public final class QueryUtils {

    private static final String LOG_TAG = "QueryUtils.java";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static ArrayList<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        ArrayList<News> newsArrayList = extractNewsFromJson(jsonResponse);
        return newsArrayList;
    }
    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<News> extractNewsFromJson(String jsonResponse) {
            ArrayList<News> newsArray = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject jsonRootObject = new JSONObject(jsonResponse);
            JSONObject responseObject = jsonRootObject.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentNews = resultsArray.getJSONObject(i);
                String currentNewsTitle = currentNews.getString("webTitle");
                String currentNewsUrl = currentNews.getString("webUrl");
                // there is possibility that news has not date so we use optString instead getString
                String currentNewsDate = currentNews.optString("webPublicationDate");
                String currentNewsSection = currentNews.getString("sectionName");
                //get info about author (if it is)
                JSONArray currentNewsTags = currentNews.getJSONArray("tags");
                JSONObject currentNewsTagsObject = currentNewsTags.optJSONObject(0);
                //if we have not null Tag object we extract author name
                if (currentNewsTagsObject != null) {
                    String authorName = currentNewsTagsObject.optString("webTitle");
                    newsArray.add(new News(currentNewsTitle, currentNewsUrl, currentNewsDate, currentNewsSection, authorName));
                } else {
                    // if Tag object is empty, we create News object without author name
                    newsArray.add(new News(currentNewsTitle, currentNewsUrl,
                            currentNewsDate, currentNewsSection));
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        // Return the list of earthquakes
        return newsArray;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exeption) {
            Log.e(LOG_TAG, "Error with creating URL: ", exeption);
            return null;
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        //если урл пустой (нет его) возвращаем пустой джейсон
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) { // если всё ок, сервер отвечает
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else { // что-то пошло не так, сервер выдал ошибку
                Log.e(LOG_TAG, "Server error. Response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results", e);
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }
    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return  output.toString();
    }

}
