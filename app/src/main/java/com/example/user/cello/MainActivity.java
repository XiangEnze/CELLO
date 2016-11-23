package com.example.user.cello;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.net.http.SslError;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.app.PendingIntent;
import android.app.NotificationManager;
import android.app.Notification;
import android.content.Intent;
import com.kii.cloud.storage.*;
import android.app.Application;
import android.widget.Toast;
import com.kii.cloud.storage.callback.*;
import com.kii.cloud.storage.utils.Log;


public class MainActivity extends AppCompatActivity {
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    private NotificationManager manager;
    private Notification notification;
    private Intent intent;
    private PendingIntent contentIntent;




    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Kii.initialize(getApplicationContext(), "fcf60a2a", "db276f49cd7a9eed4675e8f1c0edf943", Kii.Site.US, true);
        final WebView view  = (WebView) this.findViewById(R.id.webView);
        view.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });


        view.getSettings().setJavaScriptEnabled(true);

        view.getSettings().setSaveFormData(true);
        view.getSettings().setDefaultTextEncodingName("UTF-8");
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setLoadWithOverviewMode(true);
        view.getSettings().setUserAgentString("Mozilla/5.0(fr-fr)");
        view.setVisibility(View.VISIBLE);

        view.loadUrl("https://myistra.cello.fr/login/#/");
        showNotification();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String errorMessage = intent.getStringExtra("ErrorMessage");
                Log.e("GCMTest", "Registration completed:" + errorMessage);
                if (errorMessage != null) {
                    Toast.makeText(MainActivity.this, "Error push registration:" + errorMessage, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Succeeded push registration", Toast.LENGTH_LONG).show();
                }
            }
        };
        String username = "user1";
        String password = "123ABC";
        KiiUser.logIn(new KiiUserCallBack() {
            @Override
            public void onLoginCompleted(int token, KiiUser user, Exception exception) {
                if (exception != null) {
                    Toast.makeText(MainActivity.this, "Error login user:" + exception.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "Succeeded to login", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
                startService(intent);
            }
        }, username, password);







        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && view.canGoBack()) {
                        view.goBack();   //
                        return true;    //
                    }
                }
                return false;
            }
        });
        view.loadUrl("https://myistra.cello.fr/login/");
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter("com.example.pushtest.COMPLETED"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void showNotification(){
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentText("you got a message");
        builder.setContentTitle("Myistra");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("News");
        builder.setAutoCancel(true);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(10, notification);

    }
}

