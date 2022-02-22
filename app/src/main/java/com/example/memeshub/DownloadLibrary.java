package com.example.memeshub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class DownloadLibrary extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<File> files_list;
    private TextView noDownloads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_library);
        initialTask();
        setUpWidgets();
        finalTask();
    }

    /* Initial Required Task: */
    private void initialTask() {

        files_list = imageReader(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/MemesHub_DOWNLOADS"));

    }

    /* Setting Up Widgets Which Were Defined In Download_Library XML File: */
    private void setUpWidgets() {

        gridView = (GridView) findViewById(R.id.PhoneImageGrid);
        gridView.setAdapter(new GridAdapter());

        //CUSTOM - TOOLBAR: -
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_1);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

        noDownloads = (TextView)findViewById(R.id.noDownloads);
        if(!files_list.isEmpty()){
            noDownloads.setVisibility(View.GONE);
        }
    }

    /* Final Task: */
    private void finalTask() {

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(getApplicationContext(),ViewImage.class).putExtra("img",files_list.get(position).getAbsolutePath().toString()));


            }
        });

    }

    //Fetching The Image Files and File Paths From External Storage:
    private ArrayList<File> imageReader(File root) {

        ArrayList<File> temp = new ArrayList<>();

        File[] files = root.listFiles();

        for(int i = 0 ; i < files.length ; i++){

            if(files[i].isDirectory()){
                temp.addAll(imageReader(files[i]));
            }

            else {
                if (files[i].getName().endsWith(".jpg") || files[i].getName().endsWith(".png") || files[i].getName().endsWith(".jpeg") || files[i].getName().endsWith(".gif")){
                    temp.add(files[i]);
                }
            }

        }

        return temp;

    }

    //Adapter Class For GridView:
    class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return files_list.size();
        }

        @Override
        public Object getItem(int position) {
            return files_list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = getLayoutInflater().inflate(R.layout.gelleryitem, parent, false);

            ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item);

            imageView.setImageURI(Uri.parse(getItem(position).toString()));

            return convertView;

        }
    }
}
