package com.example.wot2;

public class Servizio {
    private String indirizzo;
    private int porta;
    private String nome;
    private String TD;

    public Servizio(String indirizzo,int porta, String nome, String TD) {
        this.indirizzo = indirizzo;
        this.porta=porta;
        this.nome = nome;
        this.TD = TD;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTD() {
        return TD;
    }

    public void setTD(String TD) {
        this.TD = TD;
    }
}
