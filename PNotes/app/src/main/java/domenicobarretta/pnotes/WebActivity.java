package domenicobarretta.pnotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class WebActivity extends AppCompatActivity{
    private WebView web;
    private int REQUEST_SAVE_BOOKMARK = 1;
    private String imgPath;


    @Override public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);

        setUpToolBar();

        Intent mainIntent = getIntent();
        web = (WebView) findViewById(R.id.webview);
        web.setWebViewClient(new MyWebViewClient());
        web.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        web.loadUrl(mainIntent.getStringExtra("link"));

        web.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_UP
                        && web.canGoBack()) {
                    web.goBack();
                    return true;
                }
                return false;
            }

        });

        setUpView();
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*Per mostrare la pagina web nel frammento sfrutto WebViewClient*/
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.toString());
            return true;
        }
    }

    private void setUpView(){
        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SaveBookmarkActivity.class);
                intent.putExtra("url", web.getUrl());
                imgPath = takeScreenshot();
                intent.putExtra("webPrev",imgPath);
                startActivityForResult(intent, REQUEST_SAVE_BOOKMARK);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SAVE_BOOKMARK) {
            if (resultCode == RESULT_OK) {
                String note = data.getStringExtra("note");
                String audioPath = data.getStringExtra("audio");
                Intent intent = new Intent();
                intent.putExtra("url",web.getUrl());
                intent.putExtra("note",note);
                intent.putExtra("audio",audioPath);
                intent.putExtra("webPrev",imgPath);
                setResult(RESULT_OK,intent);
                finish();
            }
        }
    }

    /*Per dare un'idea all'utente della nota web che ha salvato, viene effettuato uno screenshot
    della pagina e mostrato nel "web fragment" come se fosse un'anteprima*/
    private String takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        File imageFile = null;
        try {
            String mPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            mPath += "/SCR_"+now+".jpg";

            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());


            Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 275,
                    bitmap.getWidth(), bitmap.getHeight()/2);

            v1.setDrawingCacheEnabled(false);
            imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return imageFile.getAbsolutePath();
    }
}
