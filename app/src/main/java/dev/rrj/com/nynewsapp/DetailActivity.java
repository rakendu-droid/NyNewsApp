package dev.rrj.com.nynewsapp;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class DetailActivity extends Activity {

    WebView wView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();

        wView = (WebView)findViewById(R.id.webView);
        wView.setWebViewClient(new WebViewClient());
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        wView.setWebChromeClient(new WebChromeClient(){

            public void onProgressChanged(WebView view,int progress){
                progressBar.setProgress(progress);
                if(progress==100)
                    progressBar.setVisibility(View.GONE);
                else
                    progressBar.setVisibility(View.VISIBLE);
            }
        });
        if (extras != null) {
            String url = extras.getString("URL");
            wView.loadUrl(url);
        }
    }

}
