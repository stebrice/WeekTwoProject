package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;

import java.io.IOException;

public class ArticleActivity extends AppCompatActivity {
    WebView wvArticle;
    Article article;
    private ShareActionProvider miShareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            setupViews();
        } catch (Exception e){
            Toast.makeText(this, e.getMessage() , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_share, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider

        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        //shareIntent.setType("text/plain");
        shareIntent.setType("message/rfc822");

        // pass in the URL currently being used by the WebView
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getHeadline());
        shareIntent.putExtra(Intent.EXTRA_TEXT, wvArticle.getUrl());

        miShareAction.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);
    }


    public void setupViews() throws Exception {
        wvArticle = (WebView) findViewById(R.id.wvArticle);
        article = (Article) getIntent().getSerializableExtra("article");

        if (!isOnline()) {
            throw new Exception("Internet access unavailable. Please check your connection.");
        }
        else {
            wvArticle.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            wvArticle.loadUrl(article.getWebUrl());
        }
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

}
