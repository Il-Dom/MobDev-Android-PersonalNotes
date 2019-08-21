package domenicobarretta.pnotes;

import java.io.Serializable;

public class ImageModel implements Serializable {
    private String note;
    private String audioPath;
    private String imagePath;

    public String getImagePath(){
        return imagePath;
    }

    public void setImagePath(String path){
        this.imagePath = path;
    }

    public String getNote(){
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAudioPath(){
        return audioPath;
    }

    public void setAudioPath(String path){
        this.audioPath = path;
    }
}