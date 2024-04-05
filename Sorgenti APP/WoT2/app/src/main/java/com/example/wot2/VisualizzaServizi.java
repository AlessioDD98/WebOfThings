package com.example.wot2;

import android.content.Intent;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class VisualizzaServizi extends AppCompatActivity implements AdapterServizi.OnItemClickListener{

    ArrayList<Servizio> tutti;
    RecyclerView rv;
    AdapterServizi rvAdapter;
    RecyclerView.LayoutManager rvLayoutManager;
    String prop;
    TextView tvs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_servizi);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("WoT App");
        RequestQueue queue= Volley.newRequestQueue(this);
        rv=findViewById(R.id.listaServizi);
        rv.setHasFixedSize(true);
        rvLayoutManager= new LinearLayoutManager(this);
        rv.setLayoutManager(rvLayoutManager);
        tutti=new ArrayList<>();
        tvs=findViewById(R.id.tVS);
        String url="http://137.204.57.93:8443/serviceregistry/mgmt/";
        JsonObjectRequest o= new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray=response.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject dato=jsonArray.getJSONObject(i);
                        int id=dato.getInt("id");
                        JSONObject serviceDefinition=dato.getJSONObject("serviceDefinition");
                        int idSD=serviceDefinition.getInt("id");
                        String sd=serviceDefinition.getString("serviceDefinition");
                        JSONObject provider=dato.getJSONObject("provider");
                        String address=provider.getString("address");
                        int port=provider.getInt("port");
                        try{
                            JSONObject metadata=dato.getJSONObject("metadata");
                            prop=metadata.getString("additionalProp1");
                            if(!prop.equals("")){
                                try{
                                    JSONObject td=new JSONObject(prop);
                                    tutti.add(new Servizio(address,port,sd,prop));
                                }catch (JSONException e) {}
                            }
                            Log.v("a","ID: "+id+"servizio: "+address+":"+port+", "+sd+", "+prop);
                        }catch (JSONException e) {
                            Log.e("Errore","errore json: ",e);
                            prop="";
                            Log.v("a","ID: "+id+"servizio: "+address+":"+port+", "+sd+", "+prop);
                        }
                    }
                    if(tutti.size()==0){
                        tvs.setText("Non è presente nessun servizio");
                    }
                    rvAdapter=new AdapterServizi(VisualizzaServizi.this,tutti);
                    rv.setAdapter(rvAdapter);
                    rvAdapter.setOnItemClickListener(VisualizzaServizi.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(o);
    }

    @Override
    public void onItemClick(int position) {
        Intent i=new Intent(this,ServizioEspanso.class);
        Servizio s= tutti.get(position);
        if(s.getTD()==null ||s.getTD().equals("")){
            Toast.makeText(this, "Non è possibile interagire con questa thing", Toast.LENGTH_SHORT).show();
        }
        else {
            i.putExtra("indirizzo",s.getIndirizzo());
            i.putExtra("porta",s.getPorta());
            i.putExtra("nome",s.getNome());
            i.putExtra("metadata",s.getTD());
            startActivity(i);
        }
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