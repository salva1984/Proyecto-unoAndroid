package com.example.Proyecto1Completo;

import com.example.Proyecto1Completo.cartas.Carta;
import com.example.Proyecto1Completo.cartas.CartaComodin;
import com.example.Proyecto1Completo.cartas.CartaNormal;
import com.example.Proyecto1Completo.cartas.Color;
import com.example.Proyecto1Completo.cartas.TipoComodin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Mazo {
    
    private Mazo(){}
    
    public static List<Carta> llenarMazo(){
        ArrayList<Carta> cartas = new ArrayList<>();
        for(int i = 1; i < 10; i++){
            cartas.add(new CartaNormal(Color.a, i));
            cartas.add(new CartaNormal(Color.r, i));
            cartas.add(new CartaNormal(Color.v, i));
            cartas.add(new CartaNormal(Color.z, i));
        }
        for (Color c : Color.values()) {
            if(c != Color.n){
                cartas.add(new CartaComodin(c, TipoComodin.REVERSA));
                cartas.add(new CartaComodin(c,TipoComodin.REVERSA));
                cartas.add(new CartaComodin(c,TipoComodin.MAS2));
                cartas.add(new CartaComodin(c,TipoComodin.MAS2));
                cartas.add(new CartaComodin(c,TipoComodin.MAS4));
                cartas.add(new CartaComodin(c,TipoComodin.MAS4));
                cartas.add(new CartaComodin(c,TipoComodin.BLOQUEO));
                cartas.add(new CartaComodin(c,TipoComodin.BLOQUEO));

            }
            else{
            cartas.add(new CartaComodin(c,TipoComodin.MAS2));
            cartas.add(new CartaComodin(c,TipoComodin.MAS2));
            cartas.add(new CartaComodin(c,TipoComodin.MAS4));
            cartas.add(new CartaComodin(c,TipoComodin.MAS4));
            cartas.add(new CartaComodin(c,TipoComodin.CAMBIOCOLOR));
            cartas.add(new CartaComodin(c,TipoComodin.CAMBIOCOLOR));
            }
        }
        Collections.shuffle(cartas);
        return cartas;
    }

    
}
