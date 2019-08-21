package domenicobarretta.pnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FullScreenActivity extends AppCompatActivity {

    /*Questa attività viene richiamata quando si tappa su una foto del tab "immagini".
     La mostra a tutto schermo
    */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_image);

        setUpToolBar();

        ImageView fullScreenIV = (ImageView) findViewById(R.id.fullScreenIV);
        Intent intent = getIntent();
        if(intent != null){
            String imagePath = intent.getStringExtra("path");
            if(imagePath != null && fullScreenIV != null){
                Glide.with(this)
                        .load(imagePath)
                        .into(fullScreenIV);
            }
        }
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
