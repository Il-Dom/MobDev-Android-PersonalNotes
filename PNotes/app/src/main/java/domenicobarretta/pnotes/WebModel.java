package domenicobarretta.pnotes;

import java.io.Serializable;

public class WebModel implements Serializable{
    private String title;
    private String webPrev;
    private String webLink;
    private String audioPath;


    public String getImagePath(){
        return webPrev;
    }

    public void setWebPrev(String path){
        this.webPrev = path;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebLink(){
        return webLink;
    }

    public void setWebLink(String path){
        this.webLink = path;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }
}
