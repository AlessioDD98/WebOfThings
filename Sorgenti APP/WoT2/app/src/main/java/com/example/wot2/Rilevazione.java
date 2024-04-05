package com.example.wot2;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.wot2.CreaCanale.CHANNEL_ID;
import static com.example.wot2.MainActivity.db;


public class Rilevazione extends IntentService {

    ArrayList<Valore> f;
    final String sharedPrefs = "sharedPrefs";
    SharedPreferences sharedPreferences;
    int valSogliaA;
    int valSogliaAS;
    int valSogliaG;
    int valSogliaGS;
    int valIntervallo;
    boolean isA;
    boolean isAS;
    boolean isG;
    boolean isGS;
    String nome;
    String prop;
    ArrayList<Servizio> tutti;
    private PowerManager.WakeLock wakeLock;
    boolean firstTime=true;



    private class getStoricoAsyncTask extends AsyncTask<String,Void, List<Sensore>> {
        @Override
        protected List<Sensore> doInBackground(String... strings) {
            return db.daoSensore().filtra(strings[0],strings[1]);
        }
    }

    private class InsSensoreAsyncTask extends AsyncTask<Sensore, Void,Void> {
        @Override
        protected Void doInBackground(Sensore... sensores) {
            MainActivity.db.daoSensore().InserisciValore(sensores[0]);
            return null;
        }
    }

    public Rilevazione() {
        super("Rilevazione");
        setIntentRedelivery(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager powerManager=(PowerManager)getSystemService(POWER_SERVICE);
        wakeLock=powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MYAPP:WAKELOCK");
        wakeLock.acquire(60000);
        Log.v("MYAPP","Wakelock OK");
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            Log.v("MYAPP","CIAONEE");
            Notification notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_android)
                    .setContentTitle("WoT APP")
                    .setContentText("In uso in background")
                    .build();
            startForeground(1,notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("MYAPP","ONDESTROY");
        wakeLock.release();
        stopSelf();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while(true){
            sharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE);
            Log.v("MYAPP","HandleIntent");
            valIntervallo=sharedPreferences.getInt("valIntervallo",20);
            Log.v("INTERVALLO","INT: "+sharedPreferences.getInt("valIntervallo",20));
            tutti=new ArrayList<>();
            RequestFuture<JSONObject> requestFuture= RequestFuture.newFuture();
            RequestQueue queue= Volley.newRequestQueue(this);
            String url="http://137.204.57.93:8443/serviceregistry/mgmt/";
            JsonObjectRequest o= new JsonObjectRequest(Request.Method.GET, url, null,requestFuture,requestFuture);
            queue.add(o);
            try {
                JSONObject response = requestFuture.get();
                //  stopSelf();
                Log.v("MYAPP","a: "+response);
                try {
                    JSONArray jsonArray=response.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject dato=jsonArray.getJSONObject(i);
                        int id=dato.getInt("id");
                        JSONObject serviceDefinition=dato.getJSONObject("serviceDefinition");
                        String sd=serviceDefinition.getString("serviceDefinition");
                        JSONObject provider=dato.getJSONObject("provider");
                        String address=provider.getString("address");
                        int port=provider.getInt("port");
                        try{
                            JSONObject metadata=dato.getJSONObject("metadata");
                            prop=metadata.getString("additionalProp1");
                            if(!prop.equals("")){
                                try{
                                    Log.v("PROP","PROP: "+address+", "+port+", "+sd+", "+prop);
                                    JSONObject td=new JSONObject(prop);
                                    tutti.add(new Servizio(address,port,sd,prop));
                                }catch (JSONException e) {

                                }
                            }
                            Log.v("a","ID: "+id+"servizio: "+address+":"+port+", "+sd+", "+prop);
                        }catch (JSONException e) {
                            Log.e("Errore","errore json: ",e);
                            prop="";
                            //tutti.add(new Servizio(address,port,sd,prop));
                            Log.v("a","ID: "+id+"servizio: "+address+":"+port+", "+sd+", "+prop);
                        }
                    }
                    if(tutti.size()==0){
                        Log.v("MYAPP","Nessun servizio");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                Log.e("error","errore: "+e);
                e.printStackTrace();
            } catch (ExecutionException e) {
                Log.e("error","errore: "+e);
            }
            Log.v("MYAPP","size: "+tutti.size());
            ArrayList<String> indirizzi=new ArrayList<>();
            ArrayList<Integer> porte=new ArrayList<>();
            for (int j = 0; j < tutti.size(); j++) {
                Log.v("MYAPP","Nome tutti: "+tutti.get(j).getNome());
            }
            for (int i = 0; i < tutti.size(); i++) {
                if(tutti.get(i).getNome().equalsIgnoreCase("TESTSERIAL-1-measure")||tutti.get(i).getNome().equalsIgnoreCase("TESTSERIAL-2-measure")||tutti.get(i).getNome().equalsIgnoreCase("TESTSERIAL-3-measure")){
                    indirizzi.add(tutti.get(i).getIndirizzo());
                    porte.add(tutti.get(i).getPorta());
                }
            }
            isA=sharedPreferences.getBoolean("isA",false);
            isAS=sharedPreferences.getBoolean("isAS",false);
            isG=sharedPreferences.getBoolean("isG",false);
            isGS=sharedPreferences.getBoolean("isGS",false);
            valSogliaA = sharedPreferences.getInt("valSogliaA", 100);
            valSogliaAS = sharedPreferences.getInt("valSogliaAS", 100);
            valSogliaG = sharedPreferences.getInt("valSogliaG", 100);
            valSogliaGS = sharedPreferences.getInt("valSogliaGS", 100);
            if(isA==true){
                for (int k = 0; k < indirizzi.size(); k++) {
                    rilevazione("Acceleration",indirizzi.get(k),porte.get(k));
                }
            }
            if(isAS==true){
                for (int k = 0; k < indirizzi.size(); k++) {
                    rilevazione("AccelerationSamples",indirizzi.get(k),porte.get(k));
                }
            }
            if(isG==true){
                for (int k = 0; k < indirizzi.size(); k++) {
                    rilevazione("Gyroscope",indirizzi.get(k),porte.get(k));
                }
            }
            if(isGS==true){
                for (int k = 0; k < indirizzi.size(); k++) {
                    rilevazione("GyroscopeSamples",indirizzi.get(k),porte.get(k));
                }
            }
            valIntervallo=sharedPreferences.getInt("valIntervallo",20);
            // stopSelf();
            SystemClock.sleep(valIntervallo*1000);
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

    public void rilevazione(final String comando, String ip, int porta){
        RequestQueue queue= Volley.newRequestQueue(this);
        String url="http://"+ip+":"+porta+"/?property="+comando;
        Log.v("MYAPP","URL: "+url);
        StringRequest o= new StringRequest(Request.Method.GET,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("{}")||response.equals("[]")){
                    Log.v("MYAPP","Valori assenti");
                    return;
                }
                else{
                    int c=0;
                    String m="";
                    Log.v("APP","RESPONSE "+response);
                    f=new ArrayList<>();
                    try {
                        JSONObject ris= new JSONObject(response);
                        nome=ris.getString("nome");
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
                            JSONObject n= aris.getJSONObject(0);
                            nome=n.getString("nome");
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
                            Log.v("MYAPP","Try ARRAY fallito");
                            return;
                        }
                    }
                    if(c==1){
                        Log.v("MYAPP","c'è solo nome");
                        return;
                    }
                    if(comando.equals("Acceleration")||comando.equals("AccelerationSamples")||comando.equals("Gyroscope")||comando.equals("GyroscopeSamples")){
                        ValComposto valComposto=new ValComposto(f.get(0).getVal(),f.get(1).getVal(),f.get(2).getVal(),f.get(3).getVal());
                        Log.v("APP","F:"+valComposto.getX()+" "+valComposto.getY()+" "+valComposto.getZ());
                        for(int i=0;i<f.size();i++){
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
                        if(comando.equals("Acceleration")){
                            float vettore=(float) Math.sqrt((Float.parseFloat(sensore.getVc().getX())*Float.parseFloat(sensore.getVc().getX()))+(Float.parseFloat(sensore.getVc().getY())*Float.parseFloat(sensore.getVc().getY()))+(Float.parseFloat(sensore.getVc().getZ())*Float.parseFloat(sensore.getVc().getZ())));
                            Log.v("MYAPP","Soglia A: "+valSogliaA);
                            if(vettore>valSogliaA){
                                Log.v("MYAPP","Soglia A ALTA: "+valSogliaA);
                                Intent intent= new Intent(getApplicationContext(),RilevazioneReceiver.class);
                                intent.putExtra("Rilevazione","Acceleration");
                                sendBroadcast(intent);
                            }
                        }
                        if(comando.equals("AccelerationSamples")){
                            float vettore=(float) Math.sqrt((Float.parseFloat(sensore.getVc().getX())*Float.parseFloat(sensore.getVc().getX()))+(Float.parseFloat(sensore.getVc().getY())*Float.parseFloat(sensore.getVc().getY()))+(Float.parseFloat(sensore.getVc().getZ())*Float.parseFloat(sensore.getVc().getZ())));
                            if(vettore>valSogliaAS){
                                Intent intent= new Intent(getApplicationContext(),RilevazioneReceiver.class);
                                intent.putExtra("Rilevazione","AccelerationSamples");
                                sendBroadcast(intent);
                            }
                        }
                        if(comando.equals("Gyroscope")){
                            float vettore=(float) Math.sqrt((Float.parseFloat(sensore.getVc().getX())*Float.parseFloat(sensore.getVc().getX()))+(Float.parseFloat(sensore.getVc().getY())*Float.parseFloat(sensore.getVc().getY()))+(Float.parseFloat(sensore.getVc().getZ())*Float.parseFloat(sensore.getVc().getZ())));
                            if(vettore>valSogliaG){
                                Intent intent= new Intent(getApplicationContext(),RilevazioneReceiver.class);
                                intent.putExtra("Rilevazione","Gyroscope");
                                sendBroadcast(intent);
                            }
                        }
                        if(comando.equals("GyroscopeSamples")){
                            float vettore=(float) Math.sqrt((Float.parseFloat(sensore.getVc().getX())*Float.parseFloat(sensore.getVc().getX()))+(Float.parseFloat(sensore.getVc().getY())*Float.parseFloat(sensore.getVc().getY()))+(Float.parseFloat(sensore.getVc().getZ())*Float.parseFloat(sensore.getVc().getZ())));
                            if(vettore>valSogliaGS){
                                Intent intent= new Intent(getApplicationContext(),RilevazioneReceiver.class);
                                intent.putExtra("Rilevazione","GyroscopeSamples");
                                sendBroadcast(intent);
                            }
                        }
                        Log.v("MYAPP","Inserimento OK");
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("MYAPP","ONERROR");
            }
        });
        queue.add(o);

    }
}
