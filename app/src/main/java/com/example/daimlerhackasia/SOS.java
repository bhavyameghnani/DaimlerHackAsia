package com.example.daimlerhackasia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SOS extends AppCompatActivity {

    private ToggleButton togglebutton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("SOS_Status");
    DatabaseReference myRef1 = database.getReference("SOS_Location_lat");
    DatabaseReference myRef2 = database.getReference("SOS_Location_long");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);


        togglebutton = (ToggleButton) findViewById(R.id.togglebutton);
    }

    public void toggleclick(View v){
        if(togglebutton.isChecked()){
            Toast.makeText(SOS.this, "ON", Toast.LENGTH_SHORT).show();
            myRef.setValue("on");
            myRef1.setValue("1.2833965");
            myRef2.setValue("103.8585639");
        }

        else{
            Toast.makeText(SOS.this, "OFF", Toast.LENGTH_SHORT).show();
            myRef.setValue("off");
            myRef1.setValue("103.8585639");
            myRef2.setValue("103.8585639");
        }

    }
}