package jp.juggler.screenshotbutton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImageView;

public class ScreenshootCrop extends AppCompatActivity {
    CropImageView img1;
    Button btnnextpage , btnroted , btnfliped , btnflipedvertical ,btnback;
    public static Uri uri;
    public static Bitmap mBitmap;
    int angle = 0;
    @SuppressLint("VisibleForTests")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshoot_crop);

        img1 = findViewById(R.id.cropimg);
        img1.setImageBitmap(ActViewer.Companion.getMBitmap());

        btnnextpage = findViewById(R.id.cropimgnextpage);
        btnroted = findViewById(R.id.croproted);
        btnfliped = findViewById(R.id.fliped);
        btnflipedvertical = findViewById(R.id.flipedvertical);
        btnback = findViewById(R.id.btnback);


        btnback.setOnClickListener(v ->
                onBackPressed());

        btnroted.setOnClickListener(v -> {
            angle = angle + 90;
            img1.rotateImage(angle);
        });

        btnfliped.setOnClickListener(v ->
                img1.flipImageHorizontally());

        btnflipedvertical.setOnClickListener(v ->
                img1.flipImageVertically());

        btnnextpage.setOnClickListener(v -> {
            Intent i = new Intent( getApplicationContext() , ActViewer.class);
            Bitmap bitmap = img1.getCroppedImage();
            Toast.makeText(getApplicationContext(), "aa" +bitmap, Toast.LENGTH_SHORT).show();
            mBitmap = bitmap;
            startActivity(i);
        });



    }
}