package ml.truecoder.tcrypter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView=new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/splashView.html");
        webView.addJavascriptInterface(new Object(){

            @JavascriptInterface
            public void nextScreen(){
                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        }, "jsInterface");

        setContentView(webView);
    }
}