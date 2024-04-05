package com.example.wot2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.wot2.MainActivity.db;

public class Storico extends AppCompatActivity {

    LineChart lineChart;
    TextView titolo;
    List<Sensore> tutti;
    TextView storico;

    private class getStoricoAsyncTask extends AsyncTask<String,Void, List<Sensore>> {
        @Override
        protected List<Sensore> doInBackground(String... strings) {
            return db.daoSensore().filtra(strings[0],strings[1]);
        }
    }

    private class getTuttoAsyncTask extends AsyncTask<Void,Void, List<Sensore>> {
        @Override
        protected List<Sensore> doInBackground(Void... voids) {
            return db.daoSensore().filtraTutto();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storico);
        lineChart=findViewById(R.id.graficoLinee);
        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("WoT App");
        Intent i=getIntent();
        String t=i.getStringExtra("nome");
        String c=i.getStringExtra("comando");
        titolo=findViewById(R.id.TitoloStorico);
        titolo.setText("Storico "+t+" proprietà "+c);
        storico=findViewById(R.id.storico);
        try {
            tutti=new getStoricoAsyncTask().execute(c,t).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String r="Rilevazioni: ";
        if(tutti.size()==0){
            r+="ASSENTE";
        }
        else{
            for(int k=0;k<tutti.size();k++){
                if(c.equals("Acceleration")||c.equals("AccelerationSamples")||c.equals("Gyroscope")||c.equals("GyroscopeSamples")){
                    if(k==tutti.size()-1){
                        r+="x: "+tutti.get(k).getVc().getX()+"\ny: "+tutti.get(k).getVc().getY()+"\nz: "+tutti.get(k).getVc().getZ();
                    }
                    else{
                        r+="x: "+tutti.get(k).getVc().getX()+"\ny: "+tutti.get(k).getVc().getY()+"\nz: "+tutti.get(k).getVc().getZ()+"\n\n ";
                    }
                }
                else{
                    if(k==tutti.size()-1){
                        r+=tutti.get(k).getValore();
                    }
                    else{
                        r+=tutti.get(k).getValore()+", ";
                    }
                }
            }
        }
        storico.setText(r);
        ArrayList<Entry> pos= new ArrayList<>();
        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        ArrayList<String> labelsName= new ArrayList<>();
        for(int k=0;k<tutti.size();k++){
            Date d=tutti.get(k).getData();
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = dateFormat.format(d);
            labelsName.add(strDate);
            pos.add(new Entry(k, (float) Math.sqrt(
                    (Float.parseFloat(tutti.get(k).getVc().getX())*Float.parseFloat(tutti.get(k).getVc().getX()))+
                    (Float.parseFloat(tutti.get(k).getVc().getY())*Float.parseFloat(tutti.get(k).getVc().getY()))+
                    (Float.parseFloat(tutti.get(k).getVc().getZ())*Float.parseFloat(tutti.get(k).getVc().getZ())))
            ));
        }
        LineDataSet lineDataSet= new LineDataSet(pos,"Variazione "+c);
        dataSets.add(lineDataSet);
        LineData data=new LineData(dataSets);
        lineChart.setData(data);
        Description description= new Description();
        description.setText("Data");
        lineDataSet.setLineWidth(3f);
        lineDataSet.setValueTextSize(10f);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setDescription(description);
        lineChart.setDrawBorders(true);
        lineChart.setNoDataText("Grafico assente per mancanza di rilevazioni");
        XAxis xAxis=lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsName));
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setLabelCount(labelsName.size());
        xAxis.setLabelRotationAngle(270);
        lineChart.animateY(2000);
        lineChart.invalidate();


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