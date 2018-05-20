package com.example.android.newsapp;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<News> {

    //Constants of sections names
    private static final String TECHNOLOGY_SECTION="Technology";
    private static final String GAMES_SECTION="Games";
    private static final String HELP_SECTION="Help";
    private static final String MUSIC_SECTION="Music";
    //Date and Time divider
    private static final String DATE_DIVIDER="T";

    public NewsAdapter(Activity context, ArrayList<News> newsList) {
        super(context, 0, newsList);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news_list, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Get news information at given position
        News currentNews = getItem(position);
        //show title, section
        String newsTitle = currentNews.getTitle();
        holder.newsTitle.setText(newsTitle);
        String newsSectionName = currentNews.getSectionName();
        holder.sectionName.setText(newsSectionName);

        String newsDate;
        String newsTime;
        //check is there date or not and show if it is, or show "-" if there is not
        if (currentNews.hasNewsDate()) {
            String newsFullDate = currentNews.getNewsDate();
            //Make human-readable date and time format
            newsDate = dateParser(newsFullDate);
            newsTime = timeParser(newsFullDate);
        } else {
            newsDate = "-";
            newsTime = "-";
        }
        //Set title, date and time of news to TextView
        holder.newsDate.setText(newsDate);
        holder.newsTime.setText(newsTime);
        //set background color correspond to section name
        GradientDrawable sectionColor = (GradientDrawable) holder.sectionName.getBackground();
        int sectionBackgroundColor = getSectionColor(newsSectionName);
        sectionColor.setColor(sectionBackgroundColor);

        //if news has author show his name
        if (currentNews.hasAuthorName()) {
            String newsAuthorName = currentNews.getAuthorName();
            holder.authorName.setVisibility(View.VISIBLE);
            holder.authorName.setText(newsAuthorName);
        } else {
            //make Author name TextView invisible
            holder.authorName.setVisibility(View.GONE);
        }
        return convertView;
    }

    //get date from string in format 2015-10-27T06:00:03Z and return only 2015-10-27
    private String dateParser(String date) {
        //There art "T" symbol that divide string as date and time
        String[] dateTime = date.split(DATE_DIVIDER);
        return dateTime[0];
    }
    //get time from string in format 2015-10-27T06:00:03Z and return only 06:00:03
    private String timeParser(String date) {
        //There art "T" symbol that divide string as date and time
        String[] dateTime = date.split(DATE_DIVIDER);
        //Get all time string except last symbol "Z"
        String time = dateTime[1].substring(0, dateTime[0].length() - 2);
        return time;
    }


    /**
     * Class ViewHolder content link to ListView elements
     * to make
     */
    static class ViewHolder {
        public final TextView newsTitle;
        public final TextView newsDate;
        public final TextView newsTime;
        public final TextView sectionName;
        public final TextView authorName;
        public ViewHolder(View view) {
            newsTitle =   view.findViewById(R.id.news_title);
            newsDate =    view.findViewById(R.id.news_date);
            newsTime =    view.findViewById(R.id.news_time);
            sectionName = view.findViewById(R.id.section_name);
            authorName =  view.findViewById(R.id.author_name);
        }
    }

    private int getSectionColor(String section) {
        if (section.equals(TECHNOLOGY_SECTION)) {
            return ContextCompat.getColor(getContext(), R.color.section_technology);
        }
        if (section.equals(GAMES_SECTION)) {
            return ContextCompat.getColor(getContext(), R.color.section_games);
        }
        if (section.equals(HELP_SECTION)) {
            return ContextCompat.getColor(getContext(), R.color.section_help);
        }
        if (section.equals(MUSIC_SECTION)) {
            return ContextCompat.getColor(getContext(), R.color.section_music);
        }
        return ContextCompat.getColor(getContext(), R.color.section_other);
    }

}
