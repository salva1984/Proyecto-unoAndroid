package com.example.Proyecto1Completo.jugadores;

import com.example.Proyecto1Completo.cartas.Carta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class Jugador {
    private List<Carta> cartas;
    // Este constructor toma el mazo y le quita 7 cartas y se las añade al del jugador
    public Jugador(List<Carta> mazo){ 
        cartas = new ArrayList<>();
        while(cartas.size()<7){ //cuantas cartas tiene el jugador.
            Random random = new Random();
            int randint = random.nextInt(mazo.size());
            cartas.add(mazo.get(randint));
            mazo.remove(randint);
        }
    }
    public List<Carta> getCartas() {
        return cartas;
    }
    public void setCartas(List<Carta> cartas) {
        this.cartas = cartas;
    }
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Carta carta : cartas) {
            sb.append(carta.toString()+" - ");
        }
        return sb.toString();   
    }
}
