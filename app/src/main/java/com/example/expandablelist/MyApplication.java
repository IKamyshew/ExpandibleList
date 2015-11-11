package com.example.expandablelist;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;

public class MyApplication extends Application {
    private static MyApplication singleton;

    private ImageLoader imageLoader;

    public MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
        imageLoader = ImageLoader.getInstance();
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
