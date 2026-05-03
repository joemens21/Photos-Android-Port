package com.photos;

import android.app.Application;
import android.content.Context;

import com.photos.model.User;
import com.photos.storage.UserStorage;

/**
 * Application-level singleton that holds the current User in memory
 * and coordinates saves/loads with UserStorage.
 */
public class PhotosApp extends Application {

    private static PhotosApp instance;
    private User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        instance    = this;
        currentUser = UserStorage.loadUser(this);
    }

    public static PhotosApp getInstance() { return instance; }

    public User getCurrentUser() { return currentUser; }

    /** Persist the current user state immediately. */
    public void save() {
        UserStorage.saveUser(this, currentUser);
    }
}
