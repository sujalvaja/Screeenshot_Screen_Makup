package jp.juggler.photoediting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import jp.juggler.coman;
import jp.juggler.screenshotbutton.R;


public class Historydemoadapter extends RecyclerView.Adapter<Historydemoadapter.Viewholder> {


    ArrayList<String> array_string_mywork = new ArrayList<>();
    Context context;
    int screenWidth;
    Uri str1;
    int str;
    public static Bitmap mbm;
    File appFolder;

    String imgName;
    public Historydemoadapter(Context context , ArrayList<String> array_string_mywork ) {
        this.context = context;
        this.array_string_mywork = array_string_mywork;
        screenWidth = context.getResources().getDisplayMetrics().widthPixels / 3;    }

    @NonNull
    @Override
    public Historydemoadapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View cell = inflater.inflate(R.layout.item_image_holder, parent, false);

        return new Viewholder(cell);
    }

    @Override
    public void onBindViewHolder(@NonNull Historydemoadapter.Viewholder holder, int position) {
        final String str = array_string_mywork.get(position);
        File imgFile = new File(array_string_mywork.get(position));
        if(imgFile.exists())
        {
            Glide.with(context)
                    .load(imgFile)
                    .into(holder.imageView);
                holder.title.setText(imgFile.getName());

        }



        holder.item_relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SavePerview.class);
                Bitmap bitmap = BitmapFactory.decodeFile(String.valueOf(imgFile));
                mbm = bitmap;
                coman.page =" History";
                coman.bm = mbm;
                Log.d("TAG13","Bitmap  : "+ bitmap);
                Toast.makeText(context, "position :" + imgFile, Toast.LENGTH_SHORT).show();
                context.startActivity(i);
                }
        });
    }

    @Override
    public int getItemCount() {
        return array_string_mywork.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
          ImageView imageView;
          TextView title;
          CardView item_relative;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            title = itemView.findViewById(R.id.title);
            item_relative = itemView.findViewById(R.id.item_relative);
        }
    }
}
