package jp.juggler.photoediting;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.File;
import java.util.ArrayList;

import jp.juggler.screenshotbutton.R;

public class Historydemo extends AppCompatActivity {
    public static Historydemo myWorkActivity;
    public static boolean item_click;
    static RecyclerView rv_mywork;

    static Historydemoadapter myWorkAdapter;
    static ArrayList<String> array_string_mywork = new ArrayList<String>();

    static File[] listFile;
    File main_directory;
    String path1; 
    String strFolder;
    RecyclerView recyclerView;
    AppCompatButton imgClose;
    private File[] folderNameList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historydemo);


        recyclerView = findViewById(R.id.rvAnimals);
        imgClose = findViewById(R.id.imgClose);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        File file1 = getExternalFilesDir("MyScreenshot");
        String imgFolder = file1.getAbsolutePath() + File.separator + "screenshot";
        File file = new File(imgFolder);
        main_directory = new File(imgFolder);

        if (!main_directory.exists()) {
            main_directory.mkdirs();
        }
        if (main_directory.exists()) {
            folderNameList = main_directory.listFiles();
        }
        myWorkActivity = this;

        array_string_mywork.clear();
        getFromSdcard(file);

        if (array_string_mywork.size() > 0) {
            myWorkAdapter = new Historydemoadapter(this, array_string_mywork);
            recyclerView.setAdapter(myWorkAdapter);
        } else {
        }


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void getFromSdcard(File file) {


        listFile = file.listFiles();


        for (int i = 0; i < listFile.length; i++) {
            if (listFile[i].isDirectory()) {
                getFromSdcard(listFile[i]);
            } else {
                if (listFile[i].getName().endsWith(".jpg")) {
                            Log.e("TagFileList", "name --- " + listFile[i].getPath());
                    array_string_mywork.add(listFile[i].getAbsolutePath());
                }
            }
        }
    }
}