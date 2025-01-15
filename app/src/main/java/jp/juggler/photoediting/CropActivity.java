package jp.juggler.photoediting;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.juggler.screenshotbutton.R;

public class CropActivity extends AppCompatActivity {
    public static Bitmap mybitmap;

    Button BSelectImage , captureimage , btnback;

    Button nextpage;
    ImageView IVPreviewImage;


    int SELECT_PICTURE = 200;
    private static final int CAMERA_REQUEST = 1888;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        BSelectImage = findViewById(R.id.BSelectImage);
        IVPreviewImage = findViewById(R.id.IVPreviewImage);
        nextpage = findViewById(R.id.nextpage);
        captureimage = findViewById(R.id.captureimage);
        btnback = findViewById(R.id.btnback);

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              onBackPressed();
            }
        }); BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });

        captureimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               captureimage();
            }
        });

        nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(getApplicationContext() , cropandroted.class);
                startActivity(i);
            }
        });
    }
    void imageChooser() {


        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    void captureimage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }
    public void onActivityResult(int requestcode ,int resultcode,Intent data ) {

        super.onActivityResult(requestcode, resultcode, data);
        if (requestcode == 200 && resultcode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                IVPreviewImage.setImageBitmap(bitmap);
                Toast.makeText(this, "bitmap " + bitmap, Toast.LENGTH_SHORT).show();
                mybitmap = bitmap;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestcode == 1) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            Toast.makeText(this, "" + photo, Toast.LENGTH_SHORT).show();
            IVPreviewImage.setImageBitmap(photo);
            mybitmap=photo;
        }
    }
    }