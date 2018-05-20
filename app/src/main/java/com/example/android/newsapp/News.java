package com.example.android.newsapp;

/** This News class handle informations about news (news's topic, url, date, section) */
public class News {

    private String title;
    private String newsDate;
    private String url;
    private String sectionName;
    private String authorName = null;
    private boolean hasAuthorName = false;
    private boolean hasNewsDate = false;


    /**
     * constructor for News (without information about author and data)
     * @param newsTitle title of news
     * @param newsUrl news url on The Guardian site
     * @param section the section of news
     */
    public News(String newsTitle, String newsUrl, String section) {
        title = newsTitle;
        url = newsUrl;
        sectionName = section;
    }

    /**
     * constructor for News (with data and without information about author)
     * @param newsTitle title of news
     * @param newsUrl news url on The Guardian site
     * @param newsDateTime date and time of news publishing
     * @param section the section of news
     */
    public News(String newsTitle, String newsUrl, String newsDateTime, String section) {
        title = newsTitle;
        url = newsUrl;
        if (!newsDateTime.isEmpty()) {
            newsDate = newsDateTime;
            hasNewsDate = true;
        }
        sectionName = section;
        hasNewsDate = true;
    }

    /**
     * constructor for News (with information about author and date)
     * @param newsTitle title of news
     * @param newsUrl news url on The Guardian site
     * @param newsDateTime date and time of news publishing
     * @param section the section of news
     * @param author the name of news author
     */
    public News(String newsTitle, String newsUrl, String newsDateTime, String section, String author) {
        title = newsTitle;
        url = newsUrl;
        if (!newsDateTime.isEmpty()) {
            newsDate = newsDateTime;
            hasNewsDate = true;
        }
        sectionName = section;
        authorName = author;
        hasAuthorName = true;
    }



    /**
     * @return date of news
     */
    public String getNewsDate() {
        return newsDate;
    }

    /**
     * @return title of news
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return news Url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return section name of news
     */
    public String getSectionName() {
        return sectionName;
    }

    /**
     * @return author name
     */
    public String getAuthorName() {
        return authorName;
    }
    //
    public boolean hasAuthorName() {
        return hasAuthorName;
    }

    public boolean hasNewsDate() {
        return hasNewsDate;
    }

    public String toString() {
        return "Title: " + title + " Url: " + url + " Date: " + newsDate;
    }

}
