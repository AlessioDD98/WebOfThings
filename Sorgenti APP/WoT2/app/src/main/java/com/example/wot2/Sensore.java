package com.example.wot2;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity
public class Sensore {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @TypeConverters(DateConverter.class)
    @NonNull
    private Date data;
    private String nome;
    private String proprietà;
    @Embedded(prefix = "valcomp")
    private ValComposto vc;
    private String valore;

    public Sensore(){}

    public Sensore(int id, Date data,String nome, String proprietà, ValComposto vc, String valore) {
        this.id = id;
        this.data=data;
        this.nome = nome;
        this.proprietà = proprietà;
        this.vc=vc;
        this.valore = valore;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public ValComposto getVc() {
        return vc;
    }

    public void setVc(ValComposto vc) {
        this.vc = vc;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getProprietà() {
        return proprietà;
    }

    public void setProprietà(String proprietà) {
        this.proprietà = proprietà;
    }

    public String getValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
