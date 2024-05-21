package com.example.finalproject.Activities;

import static com.example.finalproject.ReferencesFB.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.finalproject.Objs.UsersClass;
import com.example.finalproject.R;
import com.example.finalproject.ReferencesFB;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * The type Register activity.
 *
 * @author inbar menahem
 * @version 1
 * @since 21 /12/2023 the register activity for the user to open an account.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText fullNameET, userNameET, distanceET, ageET, addressET, yearsOfPlayET;
    private Switch genderSW;
    private ImageButton pfpIB;
    private Button nextBTN;
    private RadioButton begRB, amRB, advRB, tourRB;

    int distance, age, yearsOfPlay, level;
    double longitude, latitude;
    String fullName, userName, address, city, gender, addressToConvert;

    UsersClass user;
    Intent si, gi;
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
        addressET = (EditText) findViewById(R.id.addressET);
        yearsOfPlayET = (EditText) findViewById(R.id.yearsOfPlayET);
        genderSW = (Switch) findViewById(R.id.genderSW);
        pfpIB = (ImageButton) findViewById(R.id.pfpIB);
        nextBTN = (Button) findViewById(R.id.nextBtn);
        begRB = (RadioButton) findViewById(R.id.begRB);
        amRB = (RadioButton) findViewById(R.id.amRB);
        advRB = (RadioButton) findViewById(R.id.advRB);
        tourRB = (RadioButton) findViewById(R.id.tourRB);

        nextBTN.setBackgroundColor(Color.TRANSPARENT);

        gi = getIntent();
        imageRef = imagesRef.child(Uid);

        if (genderSW.isChecked())
            gender = "Man";
        else
            gender = "Female";

        // initialize the places api
        Places.initialize(getApplicationContext(), "AIzaSyA2LZ1UsMgr1ODFaAcHv08S-f1FM6-9Jzo");

        // sets listener for address EditText
        addressET.setFocusable(false);
        addressET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opens Google Maps addresses search intent
                List<Place.Field> listField = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, listField).setCountry("IL").build(RegisterActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        // check Camera permissions
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        // check Gallery permissions
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if user came from Profile Activity to change details
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
                            addressET.setText("" + user.getAddressName());
                            yearsOfPlayET.setText("" + user.getYearsOfPlay());
                            if (user.getGender() == "Female") {
                                genderSW.setChecked(false);
                            } else {
                                genderSW.setChecked(true);
                            }
                            if (user.getLevel() == 1) {
                                begRB.setChecked(true);
                            } else if (user.getLevel() == 2) {
                                amRB.setChecked(true);
                            } else if (user.getLevel() == 3) {
                                advRB.setChecked(true);
                            } else if (user.getLevel() == 4) {
                                tourRB.setChecked(true);
                            }

                            try {
                                showPhoto();
                            } catch (IOException e) {
                                Toast.makeText(RegisterActivity.this, "Image failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }

    /**
     * On Click method next button.
     * <p>
     * this method checks if all fields are filled and valid
     * if so, it saves the information into database
     * after it's done, the user goes to the main activity.
     *
     * @param view the view
     * @throws IOException the io exception
     */
    public void next(View view) throws IOException {
        if ((fullNameET.getText().toString().equals("")) || (userNameET.getText().toString().equals("")) || (ageET.getText().toString().equals("")) || (addressET.getText().toString().equals("")) || (yearsOfPlayET.getText().toString().equals("")) || (distanceET.getText().toString().equals(""))){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
        }
        else{
            fullName = fullNameET.getText().toString();
            userName = userNameET.getText().toString();
            distance = Integer.parseInt(distanceET.getText().toString());
            age = Integer.parseInt(ageET.getText().toString());
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
            ReferencesFB.getUser(mAuth.getCurrentUser());
            LatLng latlng = getLocationFromAddress(RegisterActivity.this, addressToConvert);
            user = new UsersClass(Uid, fullName, userName, age, gender, address, latlng.latitude, latlng.longitude, city, level, yearsOfPlay, distance);
            refUsers.child(Uid).setValue(user);
            si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
    }

    private LatLng getLocationFromAddress(Context context, String address) throws IOException {
        //converting address to latitute and longitude object
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocationName(address+", "+city, 1);
        if (addresses != null && !addresses.isEmpty()) {
            double latitude = addresses.get(0).getLatitude();
            double longitude = addresses.get(0).getLongitude();
            return new LatLng(latitude, longitude);
        }
        return null;
    }

    /**
     * On Click method profile photo
     * <p>
     *this method opens a dialog and checks if the user uploads a photo from Camera or Gallery.
     * if the user chose Camera, it opens the Camera and upload the photo to Firebase Storage.
     * if the user chose Gallery, it opens the Gallery and upload the photo to Firebase Storage.
     *
     * @param view the view
     */
    public void pfp(View view) {
        adb = new AlertDialog.Builder(this);
        adb.setTitle("choose");
        adb.setMessage("choose where do you take the profile picture from");
        adb.setPositiveButton("camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // checks if Camera permission is granted
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
                else{
                    //opens Camera
                    Intent takePicIntent = new Intent();
                    takePicIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
            }
        });
        adb.setNegativeButton("gallery", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // checks if Gallery permission is granted
                if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
                }
                else {
                    //opens Gallery
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_PICK);
                    galleryIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
                }
            }
        });
        AlertDialog ad = adb.create();
        ad.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // upload the taken photo to Firebase Storage
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
                                Toast.makeText(RegisterActivity.this, "didn't download", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(RegisterActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                        }
                    });
            // upload the selected photo to Firebase Storage
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
                                        Toast.makeText(RegisterActivity.this, "didn't download", Toast.LENGTH_LONG).show();
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
                }
            }
        }

        // if chosen address from intent is valid, it saves into variables
        if (requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            addressToConvert = place.getAddress();
            getAddressWithoutCountry(place);
            latitude = place.getLatLng().latitude;
            longitude = place.getLatLng().longitude;
        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getAddressWithoutCountry(Place place) {
        // saving address name and city into 2 different variables
        String[] addressParts = place.getAddress().split(", ");
        city = addressParts[addressParts.length - 2];
        addressET.setText(addressParts[0]);
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
     * this method downloads the uploaded photo from Firebase Storage and shows it to the user.
     *
     * @throws IOException the io exception
     */
    public void showPhoto() throws IOException {
        final ProgressDialog pd = ProgressDialog.show(RegisterActivity.this, "Image download", "downloading...", true);
        pd.setCancelable(false);

        final File localFile = File.createTempFile(Uid,"jpeg");
        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                String filePath = localFile.getPath();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                pfpIB.setImageBitmap(bitmap);
                pd.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RegisterActivity.this, "Image download failed", Toast.LENGTH_LONG).show();
                pd.dismiss();
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