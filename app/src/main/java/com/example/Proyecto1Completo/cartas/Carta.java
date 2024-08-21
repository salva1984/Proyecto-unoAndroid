package com.example.Proyecto1Completo.cartas;

public abstract class Carta{
    private Color color;

    protected Carta(Color color){
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return color.toString();
    }
    
    
}
