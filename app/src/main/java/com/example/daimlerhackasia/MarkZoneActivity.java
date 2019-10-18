package com.example.daimlerhackasia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MarkZoneActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    ImageView i1, i2;
    Button bt1;
    EditText et1, et2;
    Spinner sp;

    private Uri uri;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int REQUEST_LOCATION = 1;
    private static final String TAG = "CapturePicture";
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private ImageView image;
    private String pictureFilePath;
    private FirebaseStorage firebaseStorage;
    private String deviceIdentifier;
    private FirebaseAuth auth;
    private ProgressDialog mProgress;
    Bitmap imageBitmap;
    byte[] dataBAOS;
    int rresultCode;
    DatabaseReference databaseReference, databaseReference1;
    public String zoneImageURI = null;
    String ZoneTitle;
    String uid;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_zone);


        databaseReference = FirebaseDatabase.getInstance().getReference("Zones");
        //databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Zones");
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();

        i1 = findViewById(R.id.imageView1_markzone);
        i2 = findViewById(R.id.imageView2_markzone);
        bt1 = findViewById(R.id.button_markzone);
        et1 = findViewById(R.id.editText_description);
        et2 = findViewById(R.id.editText_solution);
        sp = findViewById(R.id.spinner_markzone);


        mProgress = new ProgressDialog(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.departments, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        bt1.setOnClickListener(this);
        i1.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view==bt1){
            final String ZoneData = et1.getText().toString().trim();
            final String ZoneSolution = et2.getText().toString().trim();
            final String ZoneImage = zoneImageURI;


            if (TextUtils.isEmpty(ZoneData)) {
                Toast.makeText(MarkZoneActivity.this, "Please Enter The Zone Description", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(ZoneSolution)) {
                Toast.makeText(MarkZoneActivity.this, "Please Enter The Zone Solution", Toast.LENGTH_SHORT).show();
                return;
            }

            if(dataBAOS.equals("")){
                Toast.makeText(MarkZoneActivity.this, "Please Capture The Image First", Toast.LENGTH_SHORT).show();
                return;
            }


            Log.d("bataBAOS0", String.valueOf(dataBAOS[0]));
            mProgress.setMessage("Uploading your Status...");
            mProgress.show();
            StorageReference mStorage = FirebaseStorage.getInstance().getReference()
                    .child(auth.getUid())
                    .child("" + new Date().getTime());
            UploadTask uploadTask = mStorage.putBytes(dataBAOS);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Sending failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgress.dismiss();
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png' in uri
                            zoneImageURI = uri.toString();
                            System.out.println(uri.toString());
                            Log.d("ZONE",zoneImageURI);
                            String url = zoneImageURI;
                            System.out.println("The url passed in addnames is   :" + url);

                            final String ZoneLat = "Lat";
                            final String ZoneLong = "Logg";
                            String ZoneKey = databaseReference.push().getKey();

                            Zone zn = new Zone(auth.getUid()+ new Random().nextInt(1000), ZoneTitle, ZoneData, ZoneSolution,
                                    ZoneLat, ZoneLong,0,0 ,0, zoneImageURI);
                            databaseReference.child(ZoneKey).setValue(zn);
                            addData();

                            //;setValue(hm);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("imageUri",exception.toString());
                            // Handle any errors
                        }
                    });
                    //Log.d("ZONE",zoneImageURI);
                    // Log.d("imageUri", zoneImageURI);

                }


            });
        }
        if(view==i1){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void addData() {
        databaseReference1 = FirebaseDatabase.getInstance().getReference("User_Zones");
        String ZoneKey1 = databaseReference1.push().getKey();
        User_Zones un = new User_Zones(uid,ZoneKey1,zoneImageURI);
        databaseReference1.child(ZoneKey1).setValue(un);

        //HashMap<String,String,String> hm = new HashMap<String, String>();
        //Log.d("HM",hm.toString());
        //hm.put(ZoneKey1,uid,zoneImageURI);
        //databaseReference1.child(ZoneKey1).child(hm).setValue();

        Toast.makeText(MarkZoneActivity.this, " All The Details Added Successfully", Toast.LENGTH_SHORT);
        startActivity(new Intent(MarkZoneActivity.this,MainActivity.class));
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        /*super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        i2.setImageBitmap(bitmap);*/
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            dataBAOS = baos.toByteArray();
            rresultCode = resultCode;
            i2.setImageBitmap(imageBitmap);

            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        ZoneTitle = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}