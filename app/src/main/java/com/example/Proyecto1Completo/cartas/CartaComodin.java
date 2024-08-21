package com.example.Proyecto1Completo.cartas;

public class CartaComodin extends Carta {
    private TipoComodin tipo;
    
    public CartaComodin(Color color,TipoComodin tipo){
        super(color);
        this.tipo = tipo;
    }
    public TipoComodin getTipo() {
        return tipo;
    }
    public void setTipo(TipoComodin tipo) {
        this.tipo = tipo;
    }

    // Se necesita un if separado para cada comodin pq no pude hacer que el enum me acepte caracteres raros
    @Override
    public String toString() {
        if(tipo == TipoComodin.BLOQUEO)
            return "a_"+"b"+super.toString();
        if(tipo == TipoComodin.CAMBIOCOLOR)
            return "a_"+"cc"+super.toString();
        if(tipo == TipoComodin.REVERSA)
            return "a_"+"r"+super.toString();
        if(tipo == TipoComodin.MAS2)
            return "a_"+"mas2"+super.toString();
        if(tipo == TipoComodin.MAS4)
            return "a_"+"mas4"+super.toString();
        return ""; // nunca deberia retornar null (cruza los dedos)   
    }
    
}
