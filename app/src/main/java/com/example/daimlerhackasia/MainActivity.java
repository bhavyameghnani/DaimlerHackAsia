package com.example.daimlerhackasia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public ImageView btnLogOut;
    ImageView i1,i2,i3,i4,i5,i6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        i1 = findViewById(R.id.markZone);
        i2 = findViewById(R.id.queueMgt);
        i3 = findViewById(R.id.autogen);
        i4 = findViewById(R.id.rewards);
        i5 = findViewById(R.id.Map);
        i6 = findViewById(R.id.SOS);
        btnLogOut = findViewById(R.id.btnLogOut);

        i1.setOnClickListener(this);
        i2.setOnClickListener(this);
        i3.setOnClickListener(this);
        i4.setOnClickListener(this);
        i5.setOnClickListener(this);
        i6.setOnClickListener(this);


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent I = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(I);
            }
        });

    }

    @Override
    public void onClick(View view) {

        if(view == i1){
            startActivity(new Intent(this, MapsActivity.class));
        }
        else if(view == i2){
            startActivity(new Intent(this, MarkZoneActivity.class));
        }
        else if(view == i3){
            startActivity(new Intent(this, MapsActivity.class));
        }
        else if(view == i4){
            startActivity(new Intent(this, MapsActivity.class));
        }
        else if(view == i5){
            startActivity(new Intent(this, MapsActivity.class));
        }
        else if(view == i6){
            startActivity(new Intent(this, SOS.class));
        }

    }
}
