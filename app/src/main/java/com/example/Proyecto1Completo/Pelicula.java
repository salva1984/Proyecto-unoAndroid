package com.example.Proyecto1Completo;

import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Pelicula {
    private String nombre;
    private String sinopsis;

    public Pelicula(String nombre, String sinopsis) {
        this.nombre = nombre;
        this.sinopsis = sinopsis;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public static ArrayList<Pelicula> readPelis(AssetManager am)throws IOException
    {
        ArrayList<Pelicula> lista = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new InputStreamReader(am.open("peliculas")));
        String line;
        while((line = bf.readLine())!=null)
        {
            String[] arr = line.split(",");
            Pelicula p =  new Pelicula(arr[0],arr[1]);
            lista.add(p);
        }
        bf.close();
        return lista;
    }
}
