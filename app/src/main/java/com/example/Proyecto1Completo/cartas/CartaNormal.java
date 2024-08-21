package com.example.Proyecto1Completo.cartas;

public class CartaNormal extends Carta {
    private int numero;
    
    public CartaNormal(Color color, int numero){
        super(color);
        this.numero = numero;
    }
    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) {
        this.numero = numero;
    }
    @Override
    public String toString() {
        return "a_"+numero+super.toString();
    }
    
}
