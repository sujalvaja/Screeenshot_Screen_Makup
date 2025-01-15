package jp.juggler.photoediting;

import android.graphics.Bitmap;

public class ItemData {

    private String title;
    private int imageUrl;


    private Bitmap bitmap;

    public ItemData(String title,int imageUrl , Bitmap bitmap){

        this.title = title;
        this.imageUrl = imageUrl;
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}