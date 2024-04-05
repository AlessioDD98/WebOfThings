package com.example.wot2;

public class ValComposto {
    String ora;
    String x;
    String y;
    String z;

    public ValComposto(String ora,String x, String y, String z) {
        this.ora=ora;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(String z) {
        this.z = z;
    }
}
