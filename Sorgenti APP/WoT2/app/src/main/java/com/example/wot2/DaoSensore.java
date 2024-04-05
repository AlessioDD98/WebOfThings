package com.example.wot2;

import androidx.room.Insert;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@androidx.room.Dao

public interface DaoSensore {
    @Insert
    public void InserisciValore(Sensore valore);
    @Query("SELECT * FROM Sensore WHERE upper(nome)=upper(:nome) AND proprietà=:comando")
    public List<Sensore> filtra(String comando,String nome);
    @Query("SELECT * FROM Sensore")
    public List<Sensore> filtraTutto();

}
