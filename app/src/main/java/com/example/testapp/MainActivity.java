package com.example.testapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        // Проверка условий и переход на соответствующий экран
        if (checkConditions()) {
            openWebviewScreen();
        } else {
            openContentScreen();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean checkConditions() {
        boolean isRU = isRussianLocale();
        boolean isVPNOff = isVPNOff();

        return isRU && isVPNOff;
    }

    // Проверка на русскую локаль
    private boolean isRussianLocale() {
        String locale = getResources().getConfiguration().locale.getLanguage();
        return locale.equals("ru");
    }

    // Проверка состояния VPN
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isVPNOff() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network[] networks = connectivityManager.getAllNetworks();
            for (Network network : networks) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    return false; // VPN включен
                }
            }
        }
        return true; // VPN выключен
    }

    // Открытие экрана webview
    private void openWebviewScreen() {
        Intent intent = new Intent(this, WebviewActivity.class);
        startActivity(intent);
    }

    // Открытие экрана контента приложения
    private void openContentScreen() {
        Intent intent = new Intent(this, ContentActivity.class);
        startActivity(intent);
    }
}