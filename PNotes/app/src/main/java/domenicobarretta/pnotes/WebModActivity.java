package domenicobarretta.pnotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WebModActivity extends AppCompatActivity{
    
    private EditText editText;
    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static ImageButton playButton, recordButton;
    private String [] permissions = {"android.permission.RECORD_AUDIO",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static String audioName = null;
    private String note;
    private String webPrev;
    private String link;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        setUpToolBar();

        Intent intent = getIntent();
        note = intent.getStringExtra("note");
        webPrev = intent.getStringExtra("webPrev");
        webPrev = webPrev.replace("file://", "");
        audioName = intent.getStringExtra("audioPath");
        link = intent.getStringExtra("url");
        position = intent.getIntExtra("position",0);

        File imgFile = new File(webPrev);
        if (imgFile.exists()) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
        setUpView(note);

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        recordButton = (ImageButton) findViewById(R.id.recording_button);
        playButton = (ImageButton) findViewById(R.id.play_button);

        if(audioName == null) {
            playButton.setEnabled(false);
        }

        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startRecording();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        break;
                }
                return false;
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(audioName);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                Toast.makeText(getApplicationContext(), "In riproduzione", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpView(String note){
        editText = (EditText) findViewById(R.id.editText);
        editText.setText(note);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("position",position);
                intent.putExtra("note", editText.getText().toString());
                intent.putExtra("url",link);
                intent.putExtra("photo",webPrev);
                intent.putExtra("audio", audioName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void startRecording(){
        playButton.setEnabled(false);
        mediaRecorder = new MediaRecorder();

        if(audioName==null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            audioName = getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
            audioName += "/AUD_"+timeStamp+".3gp";
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Sto registrando...", Toast.LENGTH_LONG).show();
    }

    private void stopRecording(){
        if(null != mediaRecorder){
            try{
                mediaRecorder.stop();
            }catch(RuntimeException e){
                e.printStackTrace();
            }
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            playButton.setEnabled(true);
        }
        Toast.makeText(getApplicationContext(), "Registrazione terminata", Toast.LENGTH_LONG).show();
    }
}
