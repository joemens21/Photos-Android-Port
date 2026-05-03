package com.photos.storage;

import android.content.Context;

import com.photos.model.Album;
import com.photos.model.Photo;
import com.photos.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading the single app user via Java serialization.
 * The file is stored in the app's private internal storage.
 */
public class UserStorage {

    private static final String FILE_NAME = "user_data.ser";

    /** Save the single user to internal storage. */
    public static void saveUser(Context context, User user) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the single user from internal storage.
     * Returns a fresh default user if no save exists.
     */
    public static User loadUser(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) {
            return createDefaultUser();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (User) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return createDefaultUser();
        }
    }

    /** Creates a brand-new user with no albums. */
    private static User createDefaultUser() {
        return new User("owner");
    }
}
