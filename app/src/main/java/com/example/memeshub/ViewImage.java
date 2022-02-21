package com.example.memeshub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

//Get A Full View Of Downloaded Meme:
public class ViewImage extends AppCompatActivity {

    private ImageView viewImage;
    private String file_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        initialTask();
        setUpWidgets();
    }

    /* Initial Required Task: */
    private void initialTask() {

        Intent intent = getIntent();
        file_url = intent.getStringExtra("img");

    }

    /* Setting Up Widgets Which Were Defined In ViewImage XML File: */
    private void setUpWidgets() {

        viewImage = (ImageView)findViewById(R.id.view_image);

        Glide.with(getApplicationContext()).load(file_url).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                Toast.makeText(ViewImage.this, "Failed To Load :(", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                return false;
            }
        }).into(viewImage);

        //ToolBar Setup:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(arrow -> onBackPressed());

    }

    /* Option - Menu: */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.view_image_option_menu,menu);
        return (true);
    }

    /* Working Of Option - Menu: */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.share_this_meme){

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            Uri screenshotUri = Uri.parse(file_url);
            try {
                InputStream stream = getContentResolver().openInputStream(screenshotUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            sharingIntent.setType("image/jpeg");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            startActivity(Intent.createChooser(sharingIntent, "Share This Meme Using:"));

        }

        if(item.getItemId() == R.id.delete){

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);

            builder.setMessage("Do You Want To Delete This Meme?")

                    .setCancelable(false)

                    //CODE FOR POSITIVE(YES) BUTTON: -
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //ACTION FOR "YES" BUTTON: -
                            File file = new File(file_url);
                            if (file.exists()) {

                                file.delete();

                                Toast.makeText(getApplicationContext(), "Successfully Deleted!", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), DownloadLibrary.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }

                            else {
                                Toast.makeText(getApplicationContext(),"Deletion Failed!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })

                    //CODE FOR NEGATIVE(NO) BUTTON: -
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //ACTION FOR "NO" BUTTON: -
                            dialog.cancel();

                        }
                    });

            //CREATING A DIALOG-BOX: -
            AlertDialog alertDialog = builder.create();
            //SET TITLE MAUALLY: -
            alertDialog.setTitle("Delete");
            alertDialog.show();

        }
        return (true);

    }
}
