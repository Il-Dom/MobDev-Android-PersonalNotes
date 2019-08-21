package domenicobarretta.pnotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FloatingActionButton fab;
    private int iconIntArray[] = {R.drawable.ic_open_in_browser_white_48dp,
            R.drawable.ic_add_a_photo_white_48dp};

    String mCurrentPhotoPath;
    File photoFile;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_ADD_PHOTO = 2;
    static final int REQUEST_SAVE_BOOKMARK = 5;
    static final int REQUEST_MOD_PHOTO = 10;
    static final int REQUEST_MOD_BOOKMARK = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolBar();
        setUpTabLayout();
        setUpFab();
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpTabLayout(){
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                animateFab(position);
                if (position == 0){
                    fab.setImageResource(iconIntArray[0]);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openBrowserIntent();
                        }
                    });
                }
                else if(position==1) {
                    fab.setImageResource(iconIntArray[1]);
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            takePictureIntent();
                        }
                    });
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setUpFab(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(iconIntArray[0]);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBrowserIntent();
            }
        });
    }


    public FloatingActionButton getFab() {
        return fab;
    }

    /*crea un'animazione per il floating action button quando si passa da un tab all'altro*/
    protected void animateFab(final int position) {
        fab.clearAnimation();
        
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150); 
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setImageResource(iconIntArray[position]);

                ScaleAnimation expand =  new ScaleAnimation(0.2f, 1f, 0.2f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }

    private void openBrowserIntent(){
        Intent intent = new Intent(getBaseContext(), WebActivity.class);
        intent.putExtra("link","http://www.google.com");
        startActivityForResult(intent,REQUEST_SAVE_BOOKMARK);
    }

    private void takePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mCurrentPhotoPath = null;
            photoFile = null;
            
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex){
            }

            if (photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(this,
                        "domenicobarretta.pnotes.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /*crea un file per salvare la foto e le assegna un nome unico*/
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Intent saveIntent = new Intent(getBaseContext(), ImageActivity.class);
                saveIntent.putExtra("filepath", photoFile.getAbsolutePath());
                startActivityForResult(saveIntent, REQUEST_ADD_PHOTO);
            }
        }
        else if(requestCode==REQUEST_ADD_PHOTO){
            if (resultCode==RESULT_OK) {
                mSectionsPagerAdapter.getImageFrag().addItem(data.getStringExtra("note"),
                        photoFile,data.getStringExtra("audio"));
            }
        }
        else if(requestCode==REQUEST_MOD_PHOTO){
            if (resultCode==RESULT_OK){
                mSectionsPagerAdapter.getImageFrag().modItem(data.getIntExtra("position",0),
                        data.getStringExtra("note"),
                        data.getStringExtra("photo"),
                        data.getStringExtra("audio"));
            }
        }
        else if(requestCode == REQUEST_SAVE_BOOKMARK){
            if(resultCode==RESULT_OK){
                mSectionsPagerAdapter.getWebFrag().addItem(data.getStringExtra("note"),
                        data.getStringExtra("url"),
                        data.getStringExtra("audio"),
                        data.getStringExtra("webPrev"));
            }
        }
        else if (requestCode == REQUEST_MOD_BOOKMARK){
            if(resultCode == RESULT_OK){
                mSectionsPagerAdapter.getWebFrag().modItem(data.getIntExtra("position",0),
                        data.getStringExtra("url"),
                        data.getStringExtra("note"),
                        data.getStringExtra("photo"),
                        data.getStringExtra("audio"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /*gestisce la richiesta di permessi dai frammenti "web" e "immagini"*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    tab1 = new WebFrag();
                    return tab1;
                case 1:
                    tab2 = new ImageFrag();
                    return tab2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "web";
                case 1:
                    return "immagini";
            }
            return null;
        }

        private WebFrag tab1;
        private ImageFrag tab2;
        public WebFrag getWebFrag(){
            return tab1;
        }
        public ImageFrag getImageFrag(){
            return tab2;
        }
    }
}
