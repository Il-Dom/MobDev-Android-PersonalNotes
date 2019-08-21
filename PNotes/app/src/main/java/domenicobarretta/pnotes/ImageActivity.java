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

public class ImageActivity extends AppCompatActivity {
    private EditText note;

    private static MediaRecorder mediaRecorder;
    private static MediaPlayer mediaPlayer;

    private static ImageButton playButton, recordButton;
    private String [] permissions = {"android.permission.RECORD_AUDIO",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private static String mFileName = null;


    /* Questa attività viene chiamata quando si vuole scattare una foto e memorizzarla con una nota
    (vocale o testuale). Dalla main activity viene preso il percorso in cui è stata salvata la foto
    e viene caricata in formato bitmap con dimensioni ridotte (per evitare problemi con il
    caricamento in memoria)
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        setUpToolBar();

        Intent mainIntent = getIntent();
        String filepath = mainIntent.getStringExtra("filepath");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        filepath = filepath.replace("file://", "");
        File imgFile = new File(filepath);
        if (imgFile.exists()) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            imageView.setImageBitmap(bitmap);
        }

        setUpView();

        /*richiesta permessi*/
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        mFileName = null;
        recordButton = (ImageButton) findViewById(R.id.recording_button);
        playButton = (ImageButton) findViewById(R.id.play_button);

        installListeners();
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpView(){
        note = (EditText) findViewById(R.id.editText);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent saveIntent = new Intent();
                saveIntent.putExtra("note", note.getText().toString());
                saveIntent.putExtra("audio", mFileName);
                setResult(RESULT_OK, saveIntent);
                finish();
            }
        });
    }
    private void installListeners(){
        playButton.setEnabled(false);
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

    /*Questa funzione si occupa della registrazione. Crea un file audio unico, con un nome simile
    a quello dell'immagine e sempre unico
    */
    private void startRecording(){
        playButton.setEnabled(false);
        mediaRecorder = new MediaRecorder();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        // Record to the external cache directory for visibility
        mFileName = getExternalFilesDir(Environment.DIRECTORY_PODCASTS).getAbsolutePath();
        mFileName += "/AUD_"+timeStamp+".3gp";

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(mFileName);
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
        if(mediaRecorder != null){
            /*L'aggiunta di questo try/catch sullo stop non fa bloccare l'applicazione se, provando
            a registrare un messaggio audio, si clicca e basta sul pulsante, senza tenerlo premuto
            */
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
