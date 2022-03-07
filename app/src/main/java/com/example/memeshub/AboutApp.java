package com.example.memeshub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutApp extends AppCompatActivity {

    private Toolbar About_App_ToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        activitySetup();
    }

    /* Setting Up An Activity: */
    private void activitySetup() {

        Element versionElement = new Element();
        versionElement.setTitle("Version 1.0");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.logo)
                .setDescription(getString(R.string.about_app))
                .addItem(versionElement)
                .addEmail("anjaniy01salekar@gmail.com")
                .addGitHub("Anjaniy2000/MemesHub")
                .create();

        setContentView(aboutPage);
    }
}
