package com.example.finalproject;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferencesFB {
    public static String Uid;
    public static void getUser(FirebaseUser fbuser) {
        Uid = fbuser.getUid();
    }
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refUsers = FBDB.getReference("Users");
    public static DatabaseReference refInvites = FBDB.getReference("Invites");
    public static DatabaseReference refPlayed = FBDB.getReference("played");
    public static DatabaseReference refNotPlayed = FBDB.getReference("not played");
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static StorageReference storageRef = storage.getReference();
    public static StorageReference imagesRef = storageRef.child("images/ ");
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;
    public static final int PICK_IMAGE_REQUEST = 2;
    public static final int REQUEST_CAMERA_PERMISSION = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 2;
    public static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 3;
    public static final int REQUEST_PICK_IMAGE = 4;
    public static final int REQUEST_CODE_REMINDER = 1;
    public static final int REQUEST_CODE_INVITE = 2;

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
