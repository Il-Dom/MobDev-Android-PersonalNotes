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

import java.util.ArrayList;

public class WebFrag extends Fragment {
    private WebAdapter adapter;
    private static final String STATE_ITEMS ="items";
    private ArrayList<WebModel> mItems;
    DBHelper mydb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.web_rv, container, false);
        mydb = new DBHelper(getContext());
        if(savedInstanceState != null){
            mItems = ((ArrayList<WebModel>) savedInstanceState.getSerializable(STATE_ITEMS));
        }
        else {
            getDataFromDB();
        }

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        adapter = new WebAdapter(getContext(), mItems, mydb);
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

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_ITEMS,mItems);
    }

    public void addItem(String title, String url,String audioPath, String imagePath){
        WebModel tmp = new WebModel();
        tmp.setTitle(title);
        tmp.setWebLink(url);
        tmp.setWebPrev(imagePath);
        tmp.setAudioPath(audioPath);

        mItems.add(mItems.size(),tmp);
        adapter.notifyItemInserted(mItems.size());
        adapter.notifyDataSetChanged();

        mydb.insertWeb(title,url,imagePath,audioPath);
    }

    public void modItem(int position, String link, String title, String photoPath, String audioPath){
        WebModel tmp = new WebModel();
        tmp.setTitle(title);
        tmp.setWebPrev(photoPath);
        tmp.setAudioPath(audioPath);
        tmp.setWebLink(link);

        mItems.set(position,tmp);
        adapter.notifyItemChanged(position);
        adapter.notifyDataSetChanged();

        mydb.modifyWebAt(title,link,photoPath,audioPath);
    }

    private void getDataFromDB(){
        WebModel tmp;
        mItems = new ArrayList<>();

        SQLiteDatabase sqLiteDatabase = mydb.getReadableDatabase();
        String query = "SELECT * FROM web ";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            tmp= new WebModel();
            tmp.setTitle(cursor.getString(0));
            tmp.setWebPrev(cursor.getString(1));
            tmp.setWebLink(cursor.getString(2));
            tmp.setAudioPath(cursor.getString(3));
            mItems.add(tmp);
            cursor.moveToNext();
        }
    }
}
