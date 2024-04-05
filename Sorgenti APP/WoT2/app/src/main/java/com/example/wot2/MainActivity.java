package com.example.wot2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static Database db;
    private androidx.appcompat.widget.Toolbar tb;
    final String sharedPrefs = "sharedPrefs";
    SharedPreferences sharedPreferences;

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("MYAPP","Destroy main");
        stopService(new Intent(MainActivity.this, Rilevazione.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb=findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        db= Room.databaseBuilder(getApplicationContext(), com.example.wot2.Database.class,"appdb").build();
        Intent is=new Intent(this, Rilevazione.class);
        ContextCompat.startForegroundService(this,is);
        sharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        boolean isFirstTime=sharedPreferences.getBoolean("isFirstTime",true);
        if(isFirstTime){
            startActivity(new Intent(this,SettingsActivity.class));
        }
        //deleteDatabase("appdb");
        final Button visualizza=findViewById(R.id.Visualizza);
        visualizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(), VisualizzaServizi.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this,SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}