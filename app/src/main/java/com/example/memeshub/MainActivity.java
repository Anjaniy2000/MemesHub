package com.example.memeshub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button shareButton,nextButton;
    private ImageView memeImage;
    private ImageButton downloadButton;
    private ProgressBar progressBar;
    private String get_meme_url;
    private final String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private final int RC = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpWidgets();
        VerifyPermission_INTERNET_ACCESS();

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
                try {
                    downloadButton_Task();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /* Setting Up Widgets Which Were Defined In Activity_Main XML File: */
    private void setUpWidgets() {
        shareButton = findViewById(R.id.share);
        nextButton = findViewById(R.id.next);
        downloadButton = findViewById(R.id.download);
        memeImage = findViewById(R.id.meme_image_view);
        progressBar = findViewById(R.id.progress_bar_meme);

        //CUSTOM - TOOLBAR: -
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    /* Verifying Permissions: */

    /* Permission For Internet Access: */
    private void VerifyPermission_INTERNET_ACCESS() {

        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), "No Internet Connection!!!!!", Toast.LENGTH_LONG).show();
            noInternet_ExceptionHandler();
        }
    }

    /* Permission For External Storage: */
    private void askPermissions(){
        ActivityCompat.requestPermissions(MainActivity.this, permissions, RC);
    }

    private boolean isPermissionsGranted(){
        for (String permission : permissions){
            if(ActivityCompat.checkSelfPermission(MainActivity.this,permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
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
                            Log.e("URL", get_meme_url);
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
        Intent chooser = Intent.createChooser(intent,"Share This Meme Using:");
        startActivity(chooser);
    }

    /* Working Of Next Button: */
    private void nextButton_Task() {
        loadMeme();
    }

    /* Working Of Download Button: */
    private void downloadButton_Task() throws IOException {
        String temp = get_meme_url.substring(get_meme_url.lastIndexOf('.') + 1);
//        Log.e("TEMP", temp);

        //If Image Is In .Gif Format:
        if(temp.equalsIgnoreCase("gif")){
//            Toast.makeText(MainActivity.this, "Unable To Download", Toast.LENGTH_SHORT).show();
            downloadMemeWithGif(get_meme_url);
        }
        //If Image Is In Other Formats(.jpg, .jpeg, .png):
        else{
            downloadMemeWithoutGif(get_meme_url);
        }

    }

    /* Download a Meme Without Gif: */
    private void downloadMemeWithoutGif(String meme_url) {

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

    /* Download a Meme With Gif: */
    private void downloadMemeWithGif(String meme_url) throws IOException {
        final File Dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/MemesHub_DOWNLOADS");
        final String fileName = meme_url.substring(meme_url.lastIndexOf('/') + 1);

        Glide.with(MainActivity.this)
                .download(meme_url)
                .listener(new RequestListener<File>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                        View view = findViewById(R.id.download);
                        String msg = "Failed To Download Image! Please Try Again Later :(";
                        int duration = Snackbar.LENGTH_SHORT;
                        showSnackBar(view,msg,duration);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                        try {
                            saveGifImage(MainActivity.this,getBytesFromFile(resource),Dir, fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }).submit();
    }

    /* Save Memes on External Storage(Without Gif): */
    private void saveImage(Bitmap image, File storageDir, String imageFileName){

        boolean successDirCreated = true;

        if (!isPermissionsGranted()) {
            askPermissions();
        }

        if (!storageDir.exists()) {
            successDirCreated = storageDir.mkdir();
        }
        if (successDirCreated) {
            File imageFile = new File(storageDir, imageFileName);
//            String savedImagePath = imageFile.getAbsolutePath();
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

    /* Save Memes on External Storage(With Gif): */
    private void saveGifImage(Context context,byte[] bytesFromFile,File storageDir, String imageFileName) throws IOException {
        boolean successDirCreated = true;
        FileOutputStream fos = null;

        if (!isPermissionsGranted()) {
            askPermissions();
        }

        if (!storageDir.exists()) {
            successDirCreated = storageDir.mkdir();
        }

        if(successDirCreated){
            File file = new File(storageDir, imageFileName);

            try {
                fos = new FileOutputStream(file);
                fos.write(bytesFromFile);
                fos.flush();
                fos.close();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
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

    //Gif Format Processing(Get Gif Image):
    public byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large!");
        }
        byte[] bytes = new byte[(int) length];
        int offset = 0;
        int numRead = 0;
        try (InputStream is = new FileInputStream(file)) {
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        return bytes;
    }

    /* SnackBar Setup: */
    public void showSnackBar(View view, String msg, int duration) {

        Snackbar snackbar = Snackbar.make(view,msg,duration);
        View view_2 = snackbar.getView();
        view_2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        TextView textView = view_2.findViewById(R.id.snackbar_text);
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
            if (!isPermissionsGranted()) {
                askPermissions();
            }
            else{
                Intent intent = new Intent(getApplicationContext(),DownloadLibrary.class);
                startActivity(intent);
            }

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
