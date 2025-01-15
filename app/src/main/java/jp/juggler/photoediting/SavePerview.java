package jp.juggler.photoediting;

import static jp.juggler.photoediting.EditImageActivity.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

import jp.juggler.coman;
import jp.juggler.screenshotbutton.ActMain;
import jp.juggler.screenshotbutton.ActViewer;
import jp.juggler.screenshotbutton.R;
import jp.juggler.v2mixup.ui.MainActivity;

public class SavePerview extends AppCompatActivity {
     ImageView img1;
     Button btnclose , btnshare , btnhome , btndelete;
     String image;
     public static Bitmap bitmap;
    @SuppressLint({"MissingInflatedId", "VisibleForTests"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveperview);
        img1 = findViewById(R.id.img1);
        btnclose = findViewById(R.id.btnclose);
        btnshare = findViewById(R.id.btnshare);
        btnhome = findViewById(R.id.btnhome);
        btndelete = findViewById(R.id.btndelete);
        Toast.makeText(this, "sss" + coman.page , Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "aa" + coman.bm, Toast.LENGTH_SHORT).show();



        if(coman.bm == ActViewer.Companion.getMBitmap()) {
            img1.setImageBitmap(ActViewer.Companion.getMBitmap());
        }
        else if (coman.bm == MainActivity.Companion.getMBitmap()){
            img1.setImageBitmap(MainActivity.Companion.getMBitmap());
        } else if (coman.bm == EditImageActivity.Companion.getMBitmap()) {
            img1.setImageBitmap(Companion.getMBitmap());

        }
        else if (coman.bm == Historydemoadapter.mbm) {
            img1.setImageBitmap(Historydemoadapter.mbm);
            Log.d("TAG13","fff" + img1);

        }
        img1.buildDrawingCache();
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent i = new Intent(getApplicationContext(),ActMain.class);
                Bitmap cropped = img1.getDrawingCache();
                Toast.makeText(getApplicationContext(), "aa" +cropped, Toast.LENGTH_SHORT).show();
                bitmap = cropped;
                startActivity(i);
            }
        });
        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent ( getApplicationContext() , ActMain.class);
                startActivity(i);
            }
        });
        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) img1.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageandText(bitmap);
            }
        });
    }
    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getmageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    private Uri getmageToShare(Bitmap bitmap) {
        File imagefolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, "com.anni.shareimage.fileprovider", file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }
    public void uri(Bitmap bitmap){
        Uri uri = getimageuri(bitmap);


    }

    public Uri getimageuri(Bitmap bitmap) {
        File imagefolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this, "com.anni.shareimage.fileprovider", file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();


}}}