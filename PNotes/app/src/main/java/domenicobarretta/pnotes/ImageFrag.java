package domenicobarretta.pnotes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

public class ImageFrag extends Fragment{
    private ImageAdapter adapter;
    private static final String STATE_ITEMS = "items";
    private ArrayList<ImageModel> mItems;
    DBHelper mydb;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_rv, container, false);
        mydb = new DBHelper(getContext());

        /*Per evitare problemi con la rotazione dello schermo (ovvero la sparizione delle viste
        della recycler view), le viste vengono salvate e ricaricate nel caso della rotazione */
        if(savedInstanceState != null){
            mItems = ((ArrayList<ImageModel>) savedInstanceState.getSerializable(STATE_ITEMS));
        }
        else {
            getDataFromDB();
        }

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        adapter = new ImageAdapter(getContext(), mItems, mydb);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                FloatingActionButton fab = ((MainActivity) getActivity()).getFab();
                if(dy > 0){
                    fab.hide();
                }
                else if(dy < 0){
                    fab.show();
                }
            }
        });
        return rootView;
    }

    @Override public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_ITEMS,mItems);
    }

    public void addItem(String note, File bitmap, String audioPath){
        ImageModel tmp = new ImageModel();
        tmp.setNote(note);
        tmp.setImagePath(bitmap.getAbsolutePath());
        tmp.setAudioPath(audioPath);

        mItems.add(mItems.size(),tmp);
        adapter.notifyItemInserted(mItems.size());
        adapter.notifyDataSetChanged();

        mydb.insertImage(note,bitmap.getAbsolutePath(),audioPath);
    }

    public void modItem(int position, String text, String photoPath, String audioPath){
        ImageModel tmp = new ImageModel();
        tmp.setNote(text);
        tmp.setImagePath(photoPath);
        tmp.setAudioPath(audioPath);

        mItems.set(position,tmp);
        adapter.notifyItemChanged(position);
        adapter.notifyDataSetChanged();

        mydb.modifyItemAt(text, photoPath, audioPath);
    }

    /*Quando viene avviata l'app, dal database vengono presi tutte le note create in precedenza
    e vengono mostrate all'utente*/
    private void getDataFromDB(){
        ImageModel tmp;
        mItems = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = mydb.getReadableDatabase();
        String query = "SELECT * FROM images ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tmp= new ImageModel();
            tmp.setNote(cursor.getString(0));
            tmp.setImagePath(cursor.getString(1));
            tmp.setAudioPath(cursor.getString(2));

            mItems.add(tmp);
            cursor.moveToNext();
        }
    }
}
