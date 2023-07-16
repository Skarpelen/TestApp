package com.example.testapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class WebviewActivity extends Activity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 3;

    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Получение ссылки на сайт из переданных данных
        Intent intent = getIntent();
        if (intent != null) {
            //String url = intent.getStringExtra("url");
            String url = "https://www.google.com/";

            // Инициализация WebView
            WebView webView = findViewById(R.id.webView);
            progressBar = findViewById(R.id.progressBar);

            // Настройка WebView
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);

            // Включение поддержки куки
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);

            // Настройка WebChromeClient для отображения ProgressBar
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress < 100) {
                        progressBar.setProgress(newProgress);
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                    } else {
                        progressBar.setVisibility(ProgressBar.GONE);
                    }
                }
            });

            // Настройка WebViewClient для перехвата URL и закрытия баннеров
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    // Получение содержимого страницы
                    webView.evaluateJavascript("document.documentElement.outerHTML", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String html) {
                            // Обработка содержимого страницы
                            Log.d("WebView", "Page content: " + html);
                        }
                    });
                }
            });

            // Загрузка сайта в WebView
            webView.loadUrl(url);
        }
    }

    // Метод для обработки результата выбора изображения из галереи или камеры
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    // Обработка изображения, сделанного с помощью камеры
                    // ...
                    break;
                case REQUEST_IMAGE_PICK:
                    // Обработка выбранного изображения из галереи
                    // ...
                    break;
            }
        }
    }

    // Метод для запроса разрешений на доступ к камере
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    // Метод для обработки результатов запроса разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для открытия камеры для съемки фото
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "Camera app not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Метод для открытия галереи для выбора фото
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_PICK);
    }
}

