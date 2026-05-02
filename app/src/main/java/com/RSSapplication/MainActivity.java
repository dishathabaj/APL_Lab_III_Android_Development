// MainActivity.java
package com.RSSapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private List<RSSItem> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsList, this::onNewsClick);
        recyclerView.setAdapter(newsAdapter);

        new FetchRSSFeed().execute("https://feeds.bbci.co.uk/news/rss.xml");
    }

    private void onNewsClick(RSSItem item) {
        Intent intent = new Intent(this, NewsDetailActivity.class);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("description", item.getDescription());
        intent.putExtra("link", item.getLink());
        intent.putExtra("pubDate", item.getPubDate());
        intent.putExtra("imageUrl", item.getImageUrl());
        startActivity(intent);
    }

    private class FetchRSSFeed extends AsyncTask<String, Void, List<RSSItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<RSSItem> doInBackground(String... urls) {
            RSSParser parser = new RSSParser();
            return parser.parseRSS(urls[0]);
        }

        @Override
        protected void onPostExecute(List<RSSItem> items) {
            progressBar.setVisibility(View.GONE);
            if (items != null && !items.isEmpty()) {
                newsList.clear();
                newsList.addAll(items);
                newsAdapter.notifyDataSetChanged();
            } else {
                Snackbar.make(recyclerView, "Failed to load news", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}