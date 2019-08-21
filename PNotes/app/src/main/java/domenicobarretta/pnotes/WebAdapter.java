package domenicobarretta.pnotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WebAdapter extends RecyclerView.Adapter<WebAdapter.MyViewHolder> {

    private ArrayList<WebModel> webList;
    private LayoutInflater inflater;
    private Context context;
    private DBHelper mydb;

    public WebAdapter(Context context, ArrayList<WebModel> webList, DBHelper mydb){
        this.context = context;
        this.mydb = mydb;

        inflater = LayoutInflater.from(this.context);
        this.webList = webList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.web_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        WebModel current = webList.get(position);
        holder.setData(current,position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return webList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView webThumb, webDelete, webEdit;
        private ImageButton playButton;
        private int position;
        private WebModel currentObject;


        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.web_title);
            webThumb = (ImageView) itemView.findViewById(R.id.web_thumb);
            webDelete = (ImageView) itemView.findViewById(R.id.web_delete);
            webEdit = (ImageView) itemView.findViewById(R.id.web_modify);
            playButton = (ImageButton) itemView.findViewById(R.id.play_button);
        }

        public void setData(final WebModel currentObject, int position) {
            this.title.setText(currentObject.getTitle());

            Glide.with(context).load(currentObject.getImagePath())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(this.webThumb);

            this.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(currentObject.getAudioPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                }
            });

            if (currentObject.getAudioPath() == null) this.playButton.setVisibility(View.INVISIBLE);
            else this.playButton.setVisibility(View.VISIBLE);

            this.position = position;
            this.currentObject = currentObject;
        }

        public void setListeners(){
            webDelete.setOnClickListener(MyViewHolder.this);
            webThumb.setOnClickListener(MyViewHolder.this);
            webEdit.setOnClickListener(MyViewHolder.this);
        }

        static final int REQUEST_SAVE_BOOKMARK = 5;

        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.web_delete:
                    removeItem(position);
                    break;
                case R.id.web_modify:
                    modItem(position,currentObject);
                    break;
                case R.id.web_thumb:
                    Intent openBrowIntent = new Intent(context, WebActivity.class);
                    openBrowIntent.putExtra("link",currentObject.getWebLink());
                    ((Activity) context).startActivityForResult(openBrowIntent,REQUEST_SAVE_BOOKMARK);
                    break;
            }
        }
        private File imgToDelete,audToDelete;
        public void removeItem(int position){
            if(currentObject.getImagePath() != null){
                imgToDelete = new File(currentObject.getImagePath());
                imgToDelete.delete();
            }
            if(currentObject.getAudioPath() != null) {
                audToDelete = new File(currentObject.getAudioPath());
                audToDelete.delete();
            }

            mydb.deleteWebAt(position);
            webList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            notifyDataSetChanged();
        }


        public void modItem(int position, WebModel currentObject){
            Intent intent = new Intent(context, WebModActivity.class);
            intent.putExtra("note",currentObject.getTitle());
            intent.putExtra("webPrev", currentObject.getImagePath());
            intent.putExtra("audioPath",currentObject.getAudioPath());
            intent.putExtra("url",currentObject.getWebLink());
            intent.putExtra("position",position);
            ((Activity) context).startActivityForResult(intent,11);
        }
    }
}
