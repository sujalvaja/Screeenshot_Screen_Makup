package jp.juggler.photoediting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;

import jp.juggler.screenshotbutton.R;

public class cropandroted extends AppCompatActivity {
    public static Bitmap cropped;
    public static Uri uri;
    public static Bitmap bitmap;
    CropImageView img;
    Button btnnextpage, btnroted, btnfliped, btnflipedvertical, btnback;
    Parcelable person_object;
    int angle = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropandroted);
        img = findViewById(R.id.cropimg);

        img.setImageBitmap(CropActivity.mybitmap);// Bitmap c;

        btnnextpage = findViewById(R.id.cropimgnextpage);
        btnroted = findViewById(R.id.croproted);
        btnfliped = findViewById(R.id.fliped);
        btnflipedvertical = findViewById(R.id.flipedvertical);
        btnback = findViewById(R.id.btnback);


        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });
        btnroted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                angle = angle + 90;

                img.rotateImage(angle);

            }
        });
        btnfliped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img.flipImageHorizontally();
            }
        });
        btnflipedvertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                img.flipImageVertically();
            }
        });
        btnnextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EditImageActivity.class);
                Bitmap cropped = img.getCroppedImage();
                Toast.makeText(getApplicationContext(), "aa" + cropped, Toast.LENGTH_SHORT).show();
                bitmap = cropped;
                startActivity(i);
            }
        });

    }

    public void onSaveIm(Bitmap bitmap) {
        FileOutputStream outStream = null;
        String fileName = null;
        try {

            File file1 = getExternalFilesDir("MyScreenshot");
            String imgFolder = file1.getAbsolutePath() + File.separator + "screenshot";
            File file = new File(imgFolder);
            if (!file.exists()) {
                file.mkdirs();
            }
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            fileName = "Image_" + n + ".jpg";
            File outFile = new File(file, fileName);

            outStream = new FileOutputStream(outFile);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }



  }