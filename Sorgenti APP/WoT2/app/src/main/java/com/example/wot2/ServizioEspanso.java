package com.example.wot2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class ServizioEspanso extends AppCompatActivity {


    private TextView r;
  //  private Button esegui=findViewById(R.id.eseguiAct);
   // private Button ottieni=findViewById(R.id.ottieniProp);
    private mSpinner sp;
    private mSpinner sp1;
    private String indirizzo;
    private int porta;
    private String comando;
    JSONObject act;
    JSONObject pro;
    ArrayList<String> props;
    ArrayList<String> acts;
    ArrayList<Valore> f;
    String nome;
    Button VS;

    private class InsSensoreAsyncTask extends AsyncTask<Sensore, Void,Void> {
        @Override
        protected Void doInBackground(Sensore... sensores) {
            MainActivity.db.daoSensore().InserisciValore(sensores[0]);
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servizio_espanso);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("WoT App");
        VS=findViewById(R.id.tVS);
        r=findViewById(R.id.risultato);
        sp=findViewById(R.id.proprieta);
        sp1=findViewById(R.id.azione);
        Intent i=getIntent();
        indirizzo=i.getStringExtra("indirizzo");
        porta=i.getIntExtra("porta",-1);
        nome=i.getStringExtra("nome");
        String td=i.getStringExtra("metadata");
        Log.v("TD",td);
        TextView t=findViewById(R.id.Titolo);
        t.setText("Servizio "+nome);
        try {
            JSONObject servizio = new JSONObject(td);
            props=new ArrayList<>();
            acts=new ArrayList<>();
            try{
                pro= servizio.getJSONObject("properties");
                props.add("Seleziona proprietà");
                Iterator<String> iter = pro.keys();
                while(iter.hasNext()){
                    String key = iter.next();
                    props.add(key);
                }
            }catch (JSONException e) {
                props.add("Non è presente nessuna proprietà");
            }
            try{
                act= servizio.getJSONObject("actions");
                acts.add("Seleziona azione");
                Iterator<String> iter1 = act.keys();
                while(iter1.hasNext()){
                    String key = iter1.next();
                    acts.add(key);
                }
            }catch (JSONException e) {
                acts.add("Non è presente nessuna azione");
            }
            ArrayAdapter<String> arraySP=new ArrayAdapter<>(this,R.layout.spinner_item,props);
            arraySP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(arraySP);
            ArrayAdapter<String> arraySP1=new ArrayAdapter<>(this,R.layout.spinner_item,acts);
            arraySP1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(arraySP1);
            for(int k=0;k<props.size();k++){
                Log.v("props",props.get(k));
            }
            sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    comando=adapterView.getItemAtPosition(i).toString();
                    if(!comando.equals("Seleziona proprietà")&&!comando.equals("Non è presente nessuna proprietà")){
                        Interagisci(indirizzo,porta,comando,0);
                        VS.setVisibility(View.VISIBLE);
                        Log.v("APP","Premuto");
                    }
                    else{
                        VS.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    comando=adapterView.getItemAtPosition(i).toString();
                    if(!comando.equals("Seleziona azione")&&!comando.equals("Non è presente nessuna azione")){
                        Interagisci(indirizzo,porta,comando,1);
                        VS.setVisibility(View.VISIBLE);
                    }
                    else{
                        VS.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            VS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("APP","comando:"+comando);
                    Intent i= new Intent(getApplicationContext(),Storico.class);
                    i.putExtra("comando",comando);
                    i.putExtra("nome",nome);
                    startActivity(i);
                }
            });
        } catch (Throwable tx) {
            Log.e("My App", "Errore: \"" + tx + "\"");
            Toast.makeText(this,"Si è verificato un errore",Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    public ArrayList<Valore> containssObject(JSONObject o){
        Iterator<String> iter = o.keys();
        while(iter.hasNext()) {
            String key = iter.next();
            try {
                containssObject(o.getJSONObject(key));
            } catch (JSONException e) {
                while (iter.hasNext()) {
                    Log.v("APP","verificando "+key);
                    try {
                        f.add(new Valore(key, o.getString(key)));
                        Log.v("APP","aggiunto "+key);
                         key=iter.next();
                        if ( ! iter.hasNext()) {
                            Log.v("APP","verificando "+key);
                            f.add(new Valore(key, o.getString(key)));
                            Log.v("APP","aggiunto "+key);
                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        return f;
    }

    //0=property
    //1=action
    public void Interagisci(String indirizzo, int porta, final String comando, int tipo){
        RequestQueue queue= Volley.newRequestQueue(this);
        String url;
        if(tipo==0){
            url="http://"+indirizzo+":"+porta+"/?property="+comando;
        }
        else{
            url="http://"+indirizzo+":"+Integer.toString(porta)+"/?action="+comando;
        }

        StringRequest o= new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("{}")||response.equals("[]")){
                    r.setText("Risultato proprieta' "+comando+": Impossibile ottenere valore");
                }
                else{
                    int c=0;
                    String m="";
                    Log.v("APP","RESPONSE "+response);
                    f=new ArrayList<>();
                    try {
                        JSONObject ris= new JSONObject(response);
                        Iterator<String> iter = ris.keys();
                        while(iter.hasNext()){
                            String key = iter.next();
                            c++;
                            try{
                                f=containssObject(ris.getJSONObject(key));
                            }catch (JSONException e){
                                Valore v= new Valore(key,ris.getString(key));
                                f.add(v);
                            }
                        }
                    } catch (JSONException e) {
                        try{
                            JSONArray aris= new JSONArray(response);
                            Log.v("APP","ARIS "+aris);
                            for(int i=0;i<aris.length();i++){
                                try{
                                    JSONObject ris2= aris.getJSONObject(i);
                                    Iterator<String> iterator = ris2.keys();
                                    while(iterator.hasNext()){
                                        String key = iterator.next();
                                        c++;
                                        try{
                                            f=containssObject(ris2.getJSONObject(key));
                                        }catch (JSONException ex){
                                            Valore v= new Valore(key,ris2.getString(key));
                                            f.add(v);
                                        }
                                    }
                                }catch (JSONException exx){
                                    Valore v= new Valore(Integer.toString(i),aris.getString(i));
                                    f.add(v);
                                }
                            }
                        } catch (JSONException ex) {
                            r.setText("");
                            Toast.makeText(ServizioEspanso.this,"Si è verificato un errore, impossibile eseguire l'operazione",Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(c==1){
                        Log.v("MYAPP","c'è solo nome");
                        r.setText("Risultato proprieta' "+comando+": Impossibile ottenere valore");
                        return;
                    }
                    if(comando.equals("Acceleration")||comando.equals("AccelerationSamples")||comando.equals("Gyroscope")||comando.equals("GyroscopeSamples")){
                        ValComposto valComposto=new ValComposto(f.get(0).getVal(),f.get(1).getVal(),f.get(2).getVal(),f.get(3).getVal());
                        Log.v("APP","F:"+valComposto.getX()+" "+valComposto.getY()+" "+valComposto.getZ());
                        for(int i=0;i<f.size();i++){
                            if(f.get(i).getNome().equals("nome")){
                                i++;
                                if(i>=f.size()){
                                    break;
                                }
                            }
                            m+=f.get(i).getNome()+": "+f.get(i).getVal()+"\n\n";
                        }
                        Sensore sensore=new Sensore();
                        Date d=new Date();
                        sensore.setData(d);
                        sensore.setNome(nome);
                        sensore.setProprietà(comando);
                        sensore.setVc(valComposto);
                        Log.v("APP","nome: "+sensore.getNome()+" proprietà: "+sensore.getProprietà()+" valore: "+sensore.getVc().getX()+" "+sensore.getVc().getY()+" "+sensore.getVc().getZ()+" ");
                        new InsSensoreAsyncTask().execute(sensore);
                    }
                    else{
                        for(int i=0;i<f.size();i++){
                            Log.v("APP","F:"+f.get(i).getNome()+" "+f.get(i).getVal());
                            Log.v("MYAPP","NOME: "+f.get(i).getNome());
                            if(f.get(i).getNome().equals("nome")){
                                i++;
                                if(i>=f.size()){
                                    break;
                                }
                            }
                            m+=f.get(i).getNome()+": "+f.get(i).getVal()+"\n\n";
                            Sensore sensore= new Sensore();
                            Date d=new Date();
                            sensore.setData(d);
                            sensore.setNome(nome);
                            sensore.setProprietà(comando);
                            sensore.setValore(f.get(i).getVal());
                            Log.v("APP","nome: "+sensore.getNome()+" proprietà: "+sensore.getProprietà()+" valore: "+sensore.getValore());
                            new InsSensoreAsyncTask().execute(sensore);
                        }
                    }

                    r.setText(m);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               r.setText("");
               Toast.makeText(ServizioEspanso.this,"Si è verificato un errore, impossibile eseguire l'operazione",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(o);
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