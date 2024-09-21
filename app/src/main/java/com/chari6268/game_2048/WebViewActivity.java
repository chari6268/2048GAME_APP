package com.chari6268.game_2048;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class WebViewActivity extends AppCompatActivity {

    private WebView web;
    private LinearLayout noInternetLayout;
    private boolean isInternetAvailable;

    private static final String mobile_mode = "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkInternetConnection();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        web = findViewById(R.id.i_webView);
        noInternetLayout = findViewById(R.id.no_internet_message); // Ensure this layout is defined

        setupWebView();

        checkInternetConnection();

        // Register the BroadcastReceiver
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void setupWebView() {
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setUserAgentString(mobile_mode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                showErrorDialog();
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                showGameLostDialog(result);
                return true;
            }
        });

        // Load the initial URL
        loadWebPage();
    }

    private void loadWebPage() {
        String url = "https://game.srinivasachari.tech";
        web.loadUrl(url);
    }

    private void checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isInternetAvailable = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isInternetAvailable) {
            noInternetLayout.setVisibility(View.GONE);
            web.setVisibility(View.VISIBLE);
            loadWebPage(); // Reload the web page if internet is restored
        } else {
            noInternetLayout.setVisibility(View.VISIBLE);
            web.setVisibility(View.GONE);
        }
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(WebViewActivity.this)
                .setTitle("Error")
                .setMessage("Failed to load the game. Please check your connection.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showGameLostDialog(JsResult result) {
        new AlertDialog.Builder(this)
                .setTitle("Game Over")
                .setMessage("Do you want to start a new game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    result.confirm();
                    reloadGame();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    result.cancel();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void reloadGame() {
        web.reload();
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver); // Unregister the receiver
    }
}
