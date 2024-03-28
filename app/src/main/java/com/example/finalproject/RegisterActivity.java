package com.example.finalproject;

import static com.example.finalproject.ReferencesFB.*;
import static com.example.finalproject.LoginActivity.userFB;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author		inbar menahem
 * @version	    1
 * @since		21/12/2023
 * the register activity for the user to open an account.
 */
public class RegisterActivity extends AppCompatActivity {
    EditText fullNameET, userNameET, distanceET, ageET, addressET, cityET, yearsOfPlayET;
    Switch genderSW;
    ImageButton pfpIB;
    RadioButton begRB, amRB, advRB, tourRB;
    public static UsersClass user;
    String Uid, fullName, userName, address, city, gender;
    int distance, age, yearsOfPlay, level, ratingLevel;
    Intent si,gi;
    AlertDialog.Builder adb;
    public static StorageReference imageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fullNameET = (EditText) findViewById(R.id.fullNameET);
        userNameET = (EditText) findViewById(R.id.userNameET);
        distanceET = (EditText) findViewById(R.id.distanceET);
        ageET = (EditText) findViewById(R.id.ageET);
        cityET = (EditText) findViewById(R.id.cityET);
        addressET = (EditText) findViewById(R.id.addressET);
        yearsOfPlayET = (EditText) findViewById(R.id.yearsOfPlayET);
        genderSW = (Switch) findViewById(R.id.genderSW);
        pfpIB = (ImageButton) findViewById(R.id.pfpIB);
        begRB = (RadioButton) findViewById(R.id.begRB);
        amRB = (RadioButton) findViewById(R.id.amRB);
        advRB = (RadioButton) findViewById(R.id.advRB);
        tourRB = (RadioButton) findViewById(R.id.tourRB);

        gi = getIntent();
        Uid = userFB.getUid();
        imageRef = imagesRef.child(Uid);

        adb = new AlertDialog.Builder(this);

        if (genderSW.isChecked())
            gender = "Man";
        else
            gender = "Female";
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gi.getIntExtra("from profile", -1) != -1) {
            Query query = refUsers
                    .orderByChild("uid")
                    .equalTo(Uid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    if (dS.exists()) {
                        for (DataSnapshot data : dS.getChildren()) {
                            user = data.getValue(UsersClass.class);
                            fullNameET.setText(user.getFullName());
                            userNameET.setText(user.getUserName());
                            distanceET.setText("" + user.getDistance());
                            ageET.setText("" + user.getAge());
                            cityET.setText(user.getCity());
                            addressET.setText("" + user.getAddress());
                            yearsOfPlayET.setText("" + user.getYearsOfPlay());
                            if (user.getGender() == "Female") {
                                genderSW.setChecked(false);
                            } else {
                                genderSW.setChecked(true);
                            }
                            if (user.level == 1) {
                                begRB.setChecked(true);
                            } else if (user.level == 2) {
                                amRB.setChecked(true);
                            } else if (user.level == 3) {
                                advRB.setChecked(true);
                            } else if (user.level == 4) {
                                tourRB.setChecked(true);
                            }

                            try {
                                showPhoto();
                            } catch (IOException e) {
                                Toast.makeText(RegisterActivity.this, "Image failed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(RegisterActivity.this, "on cancelled", Toast.LENGTH_LONG).show();
                }
            });
            addressET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addressET.setFilters(new InputFilter[]{new CapitalizeFirstLetterInputFilter()});
                }
            });
        }
        else addressET.setFilters(new InputFilter[]{new CapitalizeFirstLetterInputFilter()});

    }

    /**
     * method of next button.
     *  <p>
     *
     *  @param	view
     *  @return	checks if all fields are filled. if so then it saves everything in firebase, if not then make a toast that asks the user to fill everything.
     */
    public void next(View view) {
        if ((fullNameET.getText().toString().equals("")) || (userNameET.getText().toString().equals("")) || (ageET.getText().toString().equals("")) || (addressET.getText().toString().equals("")) || (cityET.getText().toString().equals("")) || (yearsOfPlayET.getText().toString().equals("")) || (distanceET.getText().toString().equals(""))){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        }
        else{
            fullName = fullNameET.getText().toString();
            userName = userNameET.getText().toString();
            distance = Integer.parseInt(distanceET.getText().toString());
            age = Integer.parseInt(ageET.getText().toString());
            city = cityET.getText().toString();
            address = addressET.getText().toString();
            yearsOfPlay = Integer.parseInt(yearsOfPlayET.getText().toString());
            if (begRB.isChecked()){
                level = 1;
            } else if (amRB.isChecked()) {
                level = 2;
            } else if (advRB.isChecked()) {
                level = 3;
            } else if (tourRB.isChecked()) {
                level = 4;
            }
            if (genderSW.isChecked()) {
                gender = "Female";
            }
            else{
                gender = "Male";
            }
            Uid = userFB.getUid();
            user = new UsersClass(Uid, fullName, userName, age, gender, address, city, level, ratingLevel, yearsOfPlay, distance);
            refUsers.child(Uid).setValue(user);
            si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
    }

    /**
     * profile photo method
     * <p>
     *
     * @param	view
     * @return	opens an alarm dialog and checks if he uploads the photo from camera or gallery.
     *          if the user chose camera then it opens the camera and upload the photo to firebase.
     *          if the user chose gallery then it opens the gallery and upload the photo to firebase.
     */
    public void pfp(View view) {
        adb.setTitle("choose");
        adb.setMessage("choose where do you take the profile picture from");
        adb.setPositiveButton("camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
                Intent takePicIntent = new Intent();
                takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePicIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        adb.setNegativeButton("gallery", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
                }
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
            }
        });
        AlertDialog ad = adb.create();
        ad.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imgData = baos.toByteArray();
            imageRef.putBytes(imgData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(RegisterActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                            try {
                                showPhoto();
                            } catch (IOException e) {
                                Toast.makeText(RegisterActivity.this, "didnt download", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(RegisterActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                        }
                    });
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                pfpIB.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_IMAGE) {
                Uri file = data.getData();
                if (file != null) {
                    final ProgressDialog pd=ProgressDialog.show(this,"Upload image","Uploading...",true);
                    imageRef.putFile(file)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    pd.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Image Uploaded", Toast.LENGTH_LONG).show();
                                    try {
                                        showPhoto();
                                    } catch (IOException e) {
                                        Toast.makeText(RegisterActivity.this, "didnt download", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    pd.dismiss();
                                    Toast.makeText(RegisterActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "No Image was selected", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Write external storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * show photo method
     * <p>
     *
     * @param	-
     * @return	download the uploaded photo from firebase.
     */
    public void showPhoto() throws IOException {
        final ProgressDialog pd = ProgressDialog.show(this, "Image download", "downloading...", true);

        final File localFile = File.createTempFile(Uid,"jpeg");
        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, "Image download success", Toast.LENGTH_LONG).show();
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                pfpIB.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, "Image download failed", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String str = item.getTitle().toString();
        if (str.equals("register")){
        }
        else if (str.equals("main")){
            si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        else if (str.equals("profile")){
            Intent si = new Intent(this, ProfileActivity.class);
            startActivity(si);
        }
        else if (str.equals("coach profile")){
            Intent si = new Intent(this, CoachActivity.class);
            startActivity(si);
        }
        else if (str.equals("join as coach")){
            Intent si = new Intent(this, JoinAsCoachActivity.class);
            startActivity(si);
        }
        else if (str.equals("login")){
            Intent si = new Intent(this, LoginActivity.class);
            startActivity(si);
        }
        else if (str.equals("coachAct")) {
            Intent si = new Intent(this, CoachActivity.class);
            startActivity(si);
        }
        return super.onOptionsItemSelected(item);
    }
}