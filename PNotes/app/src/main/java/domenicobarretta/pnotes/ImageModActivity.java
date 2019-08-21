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

public class ImageModActivity extends AppCompatActivity {
    private EditText note;

    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;
    private static ImageButton playButton, recordButton;
    private String [] permissions = {"android.permission.RECORD_AUDIO",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static String mFileName = null;
    private String title;
    private String filepath;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        setUpToolBar();

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        filepath = intent.getStringExtra("filepath");
        mFileName = intent.getStringExtra("audiopath");
        position = intent.getIntExtra("position",0);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        filepath = filepath.replace("file://", "");
        File imgFile = new File(filepath);
        if (imgFile.exists()) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            imageView.setImageBitmap(bitmap);
        }
        setUpView(title);

        //permission requests
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        recordButton = (ImageButton) findViewById(R.id.recording_button);
        playButton = (ImageButton) findViewById(R.id.play_button);

        if(mFileName == null) playButton.setEnabled(false);

        installListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void installListeners(){
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
                    mediaPlayer.setDataSource(mFileName);
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

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpView(String title){
        note = (EditText) findViewById(R.id.editText);
        note.setText(title);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("note", note.getText().toString());
                intent.putExtra("audio", mFileName);
                intent.putExtra("photo",filepath);
                intent.putExtra("position",position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
    private void startRecording(){
        playButton.setEnabled(false);
        mediaRecorder = new MediaRecorder();

        if(mFileName==null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            mFileName = getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
            mFileName += "/AUD_"+timeStamp+".3gp";
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(mFileName);
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
