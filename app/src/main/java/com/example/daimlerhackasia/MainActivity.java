package com.example.daimlerhackasia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Trace;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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



        i3 = findViewById(R.id.autogen);


        i3.setOnClickListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();
                Intent I = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(I);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {


        if(view == i3){
            startActivity(new Intent(this, MapsActivity.class));
        }

    }
}
