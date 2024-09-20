package com.chari6268.game_2048;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    android.webkit.WebView web;

    private static final String mobile_mode = "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        web = findViewById(R.id.i_webView);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true); // Enable local storage
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        web.getSettings().setUseWideViewPort(true);
        web.getSettings().setUserAgentString(mobile_mode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // Allow mixed content
        }

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                new AlertDialog.Builder(WebViewActivity.this)
                        .setTitle("Error")
                        .setMessage("Failed to load the game. Please check your connection.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                showGameLostDialog(result);
                return true; // Indicate that we handled the alert
            }
        });

        // Load the initial URL
        String url = "https://game.srinivasachari.tech"; // Change to local file if needed
        web.loadUrl(url);
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
}
