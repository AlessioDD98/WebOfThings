package com.example.wot2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    final String sharedPrefs = "sharedPrefs";
    SharedPreferences sharedPreferences;

    EditText testoA;
    EditText testoAS;
    EditText testoG;
    EditText testoGS;
    EditText intervallo;
    Switch switchA;
    Button MA;
    TextView sogliaA;
    Switch switchAS;
    Button MAS;
    TextView sogliaAS;
    Switch switchG;
    Button MG;
    TextView sogliaG;
    Switch switchGS;
    Button MGS;
    TextView sogliaGS;

    int valSogliaA;
    int valSogliaAS;
    int valSogliaG;
    int valSogliaGS;
    int valIntervallo;
    boolean isA;
    boolean isAS;
    boolean isG;
    boolean isGS;
    AlertDialog.Builder builder;

    public void modificaGrafica(Switch s, Button b, TextView t, int val) {
        if (!s.isChecked()) {
            b.setVisibility(View.INVISIBLE);
            t.setVisibility(View.INVISIBLE);
        } else {
            b.setVisibility(View.VISIBLE);
            t.setText("Soglia: " + val);
            t.setVisibility(View.VISIBLE);
        }
    }

    public AlertDialog creaAlert(String titolo, final String messaggio, final String campo,
                                 final String risultato, EditText et, final int defVal, final TextView r) {
        builder = new AlertDialog.Builder(this);
        builder.setTitle(titolo);
        et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(et);
        final EditText finalEt = et;
        builder.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int p = Integer.parseInt(finalEt.getText().toString());
                sharedPreferences.edit().putInt(campo, p).apply();
                r.setText(messaggio + sharedPreferences.getInt(campo, defVal));
                Toast.makeText(getApplicationContext(), risultato, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("WoT App");
        sharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("isFirstTime",false).apply();
        Button MIntervallo=findViewById(R.id.MIntervallo);
        isA = sharedPreferences.getBoolean("isA", false);
        isAS = sharedPreferences.getBoolean("isAS", false);
        isG = sharedPreferences.getBoolean("isG", false);
        isGS = sharedPreferences.getBoolean("isGS", false);
        MA = findViewById(R.id.BAccelerometro);
        sogliaA = findViewById(R.id.Accelerometro);
        MAS = findViewById(R.id.BAS);
        sogliaAS = findViewById(R.id.AccelerometroS);
        MG = findViewById(R.id.BG);
        sogliaG = findViewById(R.id.Giroscopio);
        MGS = findViewById(R.id.BGS);
        sogliaGS = findViewById(R.id.GiroscopioS);
        switchA = findViewById(R.id.switchA);
        switchAS = findViewById(R.id.switchAS);
        switchG = findViewById(R.id.switchG);
        switchGS = findViewById(R.id.switchGS);
        valIntervallo=sharedPreferences.getInt("valIntervallo",20);
        valSogliaA = sharedPreferences.getInt("valSogliaA", 100);
        valSogliaAS = sharedPreferences.getInt("valSogliaAS", 100);
        valSogliaG = sharedPreferences.getInt("valSogliaG", 100);
        valSogliaGS = sharedPreferences.getInt("valSogliaGS", 100);
        TextView textIntervallo=findViewById(R.id.TextIntervallo);
        textIntervallo.setText("Seleziona intervallo tra una rilevazione e la successiva (in secondi): "+valIntervallo);
        switchA.setChecked(isA);
        switchAS.setChecked(isAS);
        switchG.setChecked(isG);
        switchGS.setChecked(isGS);
        modificaGrafica(switchA, MA, sogliaA, valSogliaA);
        modificaGrafica(switchAS, MAS, sogliaAS, valSogliaAS);
        modificaGrafica(switchG, MG, sogliaG, valSogliaG);
        modificaGrafica(switchGS, MGS, sogliaGS, valSogliaGS);
        //Alert per la soglia Accelerometro
        final AlertDialog aa=creaAlert("Inserisci soglia Accelerometro","Soglia: ","valSogliaA","Soglia modificata",testoA,100,sogliaA);
        switchA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("isA",isChecked).apply();
                modificaGrafica(switchA,MA,sogliaA,sharedPreferences.getInt("valSogliaA",100));
            }
        });
        MA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aa.show();
            }
        });
        //Alert per la soglia Accelerometro Samples
        final AlertDialog aas=creaAlert("Inserisci soglia Accelerometro Samples","Soglia: ","valSogliaAS","Soglia modificata",testoAS,100,sogliaAS);
        switchAS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("isAS",isChecked).apply();
                modificaGrafica(switchAS,MAS,sogliaAS,sharedPreferences.getInt("valSogliaAS",100));
            }
        });
        MAS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aas.show();
            }
        });
        //Alert per la soglia Giroscopio
        final AlertDialog ag=creaAlert("Inserisci soglia Giroscopio","Soglia: ","valSogliaG","Soglia modificata",testoG,100,sogliaG);
        switchG.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("isG",isChecked).apply();
                modificaGrafica(switchG,MG,sogliaG,sharedPreferences.getInt("valSogliaG",100));
            }
        });
        MG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ag.show();
            }
        });
        //Alert per la soglia Giroscopio Samples
        final AlertDialog ags=creaAlert("Inserisci soglia Giroscopio Samples","Soglia: ","valSogliaGS","Soglia modificata",testoGS,110,sogliaGS);
        switchGS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("isGS",isChecked).apply();
                modificaGrafica(switchGS,MGS,sogliaGS,sharedPreferences.getInt("valSogliaGS",100));
            }
        });
        MGS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ags.show();
            }
        });

        final AlertDialog ai=creaAlert("Inserisci intervallo di rilevamento (in secondi)","Seleziona intervallo tra una rilevazione e la successiva (in secondi): ","valIntervallo","Intervallo modificato",intervallo,20,textIntervallo);
        MIntervallo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ai.show();
            }
        });
    }
}