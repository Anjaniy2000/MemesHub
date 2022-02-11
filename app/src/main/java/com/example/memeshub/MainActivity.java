package com.example.memeshub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.memeshub.SingleTon.MySingleTon;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private Button shareButton,nextButton;
    private ImageView memeImage;
    private ImageButton downloadButton;
    private ProgressBar progressBar;
    private String get_meme_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpWidgets();
        VerifyPermission_INTERNET_ACCESS();
        VerifyPermission_EXTERNAL_STORAGE();
        loadMeme();

        /* Buttons: */

        //Share Button:
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareButton_Task();
            }
        });

        //Next Button:
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextButton_Task();
            }
        });

        //Download Button:
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadButton_Task();
            }
        });

    }

    /* Setting Up Widgets Which Were Defined In Activity_Main XML File: */
    private void setUpWidgets() {
        shareButton = (Button)findViewById(R.id.share);
        nextButton = (Button)findViewById(R.id.next);
        downloadButton = (ImageButton)findViewById(R.id.download);
        memeImage = (ImageView)findViewById(R.id.meme_image_view);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_meme);

        //CUSTOM - TOOLBAR: -
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    /* Verifying Permissions: */

    /* Permission For Internet Access: */
    private Boolean VerifyPermission_INTERNET_ACCESS() {

        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), "No Internet Connection!!!!!", Toast.LENGTH_LONG).show();
            noInternet_ExceptionHandler();
            return false;
        }
        return true;
    }

    /* Permission For External Storage: */
    private Boolean VerifyPermission_EXTERNAL_STORAGE() {

        // This will return the current Status
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {

            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            // If permission not granted then ask for permission real time.
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 1);
            return false;
        }

        return true;

    }

    /* Load a Meme in an ImageView: */
    private void loadMeme(){

        progressBar.setVisibility(View.VISIBLE);

//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        final String url ="https://meme-api.herokuapp.com/gimme";


        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,

                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            get_meme_url = response.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Glide.with(getApplicationContext()).load(get_meme_url).addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                View view = findViewById(R.id.next);
                                String msg = "Failed To Load :(";
                                int duration = Snackbar.LENGTH_SHORT;
                                showSnackBar(view,msg,duration);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        }).into(memeImage);


                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        // Add the request to the RequestQueue.
        MySingleTon.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    /* Working Of Share Button: */
    private void shareButton_Task() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        Intent chooser = intent.createChooser(intent,"Share This Meme Using:");
        startActivity(chooser);
    }

    /* Working Of Next Button: */
    private void nextButton_Task() {
        loadMeme();
    }

    /* Working Of Download Button: */
    private void downloadButton_Task() {
        downloadMeme(get_meme_url);
    }

    /* Download a Meme: */
    private void downloadMeme(String meme_url) {

        if (!VerifyPermission_EXTERNAL_STORAGE()) {
            return;
        }

        final File Dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/MemesHub_DOWNLOADS");

        final String fileName = meme_url.substring(meme_url.lastIndexOf('/') + 1);

        Glide.with(this)
                .load(meme_url)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {

                        Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();

                        saveImage(bitmap, Dir, fileName);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);

                        View view = findViewById(R.id.download);
                        String msg = "Failed To Download Image! Please Try Again Later :(";
                        int duration = Snackbar.LENGTH_SHORT;
                        showSnackBar(view,msg,duration);

                    }
                });
    }

    /* Save Memes on External Storage: */
    private void saveImage(Bitmap image, File storageDir, String imageFileName){

        boolean successDirCreated = true;

        if (!storageDir.exists()) {
            successDirCreated = storageDir.mkdir();
        }
        if (successDirCreated) {
            File imageFile = new File(storageDir, imageFileName);
            String savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(imageFile));
                sendBroadcast(intent);

                View view = findViewById(R.id.download);
                String msg = "Download Completed!";
                int duration = Snackbar.LENGTH_SHORT;
                showSnackBar(view,msg,duration);

            } catch (Exception e) {
                View view = findViewById(R.id.download);
                String msg = "Error While Downloading An Image!";
                int duration = Snackbar.LENGTH_SHORT;
                showSnackBar(view,msg,duration);

                e.printStackTrace();
            }

        }else{
            View view = findViewById(R.id.download);
            String msg = "Failed To Make Folder/Directory!";
            int duration = Snackbar.LENGTH_SHORT;
            showSnackBar(view,msg,duration);

        }
    }

    /* SnackBar Setup: */
    public void showSnackBar(View view, String msg, int duration) {

        Snackbar snackbar = Snackbar.make(view,msg,duration);

//        snackbar.setAction("Click Again", new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Toast.makeText(MainActivity.this, "You Click Again!", Toast.LENGTH_LONG).show();
//
//            }
//        });

//        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
        View view_2 = snackbar.getView();
        view_2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        TextView textView = (TextView) view_2.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);

        snackbar.show();
    }

    /* onBackPressed() - Method: */
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        builder.setMessage("Do You Want To Close This App?")

                .setCancelable(false)

                //CODE FOR POSITIVE(YES) BUTTON: -
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ACTION FOR "YES" BUTTON: -
                        finish();
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
        alertDialog.setTitle("Exit");
        alertDialog.show();

    }

    /* Option - Menu: */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_option_menu,menu);
        return (true);
    }

    /* Working Of Option - Menu: */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.downloads){
            Intent intent = new Intent(getApplicationContext(),DownloadLibrary.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.about_app){
            Intent intent = new Intent(getApplicationContext(),AboutApp.class);
            startActivity(intent);
        }

        return (true);
    }

    /* Exception Handler For No Internet Connection: */
    private void noInternet_ExceptionHandler() {
        shareButton.setEnabled(false);
        nextButton.setEnabled(false);
        downloadButton.setEnabled(false);
    }

}
