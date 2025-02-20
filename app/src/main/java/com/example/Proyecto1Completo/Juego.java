package com.example.Proyecto1Completo;

import com.example.Proyecto1Completo.cartas.Carta;
import com.example.Proyecto1Completo.cartas.CartaComodin;
import com.example.Proyecto1Completo.cartas.CartaNormal;
import com.example.Proyecto1Completo.cartas.Color;
import com.example.Proyecto1Completo.cartas.TipoComodin;
import com.example.Proyecto1Completo.jugadores.Jugador;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Juego {


    public StringBuilder mensaje;
    private CartaComodin cc;
    MainActivity mainActivity;

    private Scanner sc;
    private Random rd;
    // mazo
    List<Carta> mazo;
    // jugadores
    Jugador jh;
    Jugador jm;
    // cartas
    List<Carta> cartasPlayer;
    List<Carta> cartasMaquina;

    // carta inicial
    Carta ultimaCarta;

    public static final String SEPARADOR = "-------------------------------";
    public static final String JUEGA = " juega la carta: ";

    public Juego(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        mensaje = new StringBuilder();


        sc = new Scanner(System.in);
        rd = new Random();
        // mazo
        mazo = Mazo.llenarMazo();
        // jugadores
        jh = new Jugador(mazo);
        jm = new Jugador(mazo);
        // cartas
        cartasPlayer = jh.getCartas();
        cartasMaquina = jm.getCartas();

        // carta inicial
        ultimaCarta = this.robarCarta();
        while (ultimaCarta.getColor() == Color.n){
            ultimaCarta = this.robarCarta();
        }
    }



    public Carta robarCarta() {
        Collections.shuffle(mazo);
        Carta c = mazo.get(0);
        mazo.remove(0);
        return c;
    }

    // revisarCarta recibe la ultima carta jugada y la que quieres jugar, y revisa
    // si es posible jugar la carta que le pasaste
    public boolean revisarCarta(Carta cartaJugar) {

        // Tenemos que revisar si la carta que queremos jugar es normal o comodin.
        if (cartaJugar instanceof CartaNormal) {

            // Hacemos downcasting para acceder a getNumero de CartaNormal
            CartaNormal cn = (CartaNormal) cartaJugar;

            // Tenemos que revisar si la ultima carta es Normal o Comodin
            if (ultimaCarta instanceof CartaNormal) {

                // Hacemos downcasting por las mismas razones
                CartaNormal ucn = (CartaNormal) ultimaCarta;

                // Comparamos si ambas comparten numero o color
                if (cn.getNumero() == ucn.getNumero() || cn.getColor() == ucn.getColor())
                    return true;
            }
            if (ultimaCarta instanceof CartaComodin && cn.getColor() == ultimaCarta.getColor())
                return true;

        }

        if (cartaJugar instanceof CartaComodin) {
            if (cartaJugar.getColor() == Color.n)
                return true; // Siempre es posible jugar una carta negra
            // la ultima carta puede ser comodin o normal, pero al jugar una carta comodin
            // encima de esta solo nos importa el color porque los comodines no tiene
            // numeros
            return ultimaCarta.getColor() == cartaJugar.getColor();

        }
        return false;

    }

    // revisa si puedes jugar una carta de tu mano
    public boolean revisarMano(List<Carta> cartasJugador) {
        for (Carta carta : cartasJugador) {
            if (revisarCarta(carta))
                return true;
        }
        return false;
    }

    // metodo solo pensado para jugador humano
    // pide un indice de carta y retorna la carta si se puede jugar
    public Carta pedirCarta(List<Carta> cartasJugador) {
        System.out.println("Ultima carta: " + ultimaCarta);
        System.out.println("Ingrese el indice de la carta a jugar: ");
        int idx = sc.nextInt();
        sc.nextLine();
        if (idx >= 0 && idx < cartasJugador.size()) {
            if (revisarCarta(cartasJugador.get(idx))) {

                return cartasJugador.get(idx);
            } else {
                System.out.println("Ingrese una carta valida");
                pedirCarta(cartasJugador);
            }
        } else {
            System.out.println("Ingrese un indice valido");
            pedirCarta(cartasJugador);
        }

        return null;
    }


    // Elige la primera carta que se puede jugar, null si no se puede jugar nada
    public Carta elegirCarta(List<Carta> cartasJugador) {
        for (Carta carta : cartasJugador) {
            if (revisarCarta(carta)) {
                return carta;
            }
        }
        return null;
    }


    public void jugarTurno(List<Carta> cartasJugador, List<Carta> cartasRival, String nombreJugador,
                           String nombreRival, Carta cartaJugar) {
//        System.out.println(Juego.SEPARADOR);// -------
//        System.out.println("Ultima carta: " + ultimaCarta);
//        System.out.println("Turno de:" + nombreJugador);
//        System.out.println("cartas del jugador: ");
//        System.out.println(cartasPlayer.toString());
//        System.out.println("cartas de la maquina");
//        System.out.println(cartasMaquina);

        mensaje = new StringBuilder();

        //AQUI JUEGA LA MAQUINA
        if (nombreJugador.equals("MAQUINA")) {
            //borrate mensaje!!!!
            mensaje.delete(0, mensaje.length());
            //si la maquina no puede jugar ninguna....
            if (elegirCarta(cartasJugador) == null) {
                //le damoos una maquina a la maquina
                mensaje.append(nombreJugador).append(" no tiene cartas disponibles para jugar, saltando turno."+"\n");
                darCartas(cartasJugador,1);

//                System.out.println(nombreJugador + " no tiene cartas disponibles para jugar, saltando turno.");
            } else {
                //si la maquina si puede jugar alguna carta:...
                jugarCarta(elegirCarta(cartasJugador), cartasJugador, cartasRival, nombreJugador, nombreRival);
            }
        } else { // Aqui juega el jugador

            if (revisarMano(cartasJugador)) {
                jugarCarta(cartaJugar, cartasJugador, cartasRival, nombreJugador, nombreRival);
            } else {
                mensaje.delete(0, mensaje.length());
                mensaje.append(
                        "Jugador no tiene cartas disponibles para jugar, tomando carta y pasando turno "+"\n");
                darCartas(cartasJugador,1);
                //sc.nextLine();

            }
        }
    }

    //TODO
    public void jugarComodinColor(CartaComodin cc, String nombreJugador, SeleccionarColorCallback callback) {
        if (cc.getColor() == Color.n) {

            if (nombreJugador.equals("JUGADOR")) {
//                System.out.println("Ingresa que color quieres que tome la carta cambie de color: R(rojo),A(amarillo),z(azul),v(verde)");
                mensaje.append("Ingresa que color quieres que tome la carta cambie de color: R(rojo),A(amarillo),z(azul),v(verde)"+"\n");
                mainActivity.retornarColor(new SeleccionarColorCallback() {

                    @Override
                    public void seleccionarColor(Color color) {
                        cc.setColor(color);
                        callback.seleccionarColor(cc.getColor());
                    }
                });

            }
        } else {
            callback.seleccionarColor(cc.getColor());
        }
    }

    // si juega la maquina y la carta es negra, da un color aleatorio a la carta.

    private void elegirColorAleatorio(CartaComodin cc,String nombreJugador) {

        if (cc.getColor() == Color.n && nombreJugador.equals("MAQUINA")) {
            Color cl = Color.values()[rd.nextInt(Color.values().length)];
            while (cl == Color.n) {
                cl = Color.values()[rd.nextInt(Color.values().length)];
            }
            cc.setColor(cl);
        }

    }



    public void jugarCarta(Carta cartaJugar, List<Carta> cartasJugador,
                           List<Carta> cartasRival, String nombreJugador, String nombreRival) {



        if (cartaJugar instanceof CartaNormal) {
            // Metemos al mazo la ultima carta
            mazo.add(ultimaCarta);
            // Cambiamos la ultima carta
            ultimaCarta = cartaJugar;
            // quitmos la carta jugada al jugador
            cartasJugador.remove(ultimaCarta);
            mensaje.append(nombreJugador).append(Juego.JUEGA).append(ultimaCarta.toString()+"\n");
//            System.out.println(Juego.SEPARADOR);

        }

        if (cartaJugar instanceof CartaComodin) {

            CartaComodin cc = (CartaComodin) cartaJugar;

            //bloqueo y reversa (funcionan igual)
            if (cc.getTipo() == TipoComodin.BLOQUEO || cc.getTipo() == TipoComodin.REVERSA) {

                // Metemos al mazo la ultima carta
                mazo.add(ultimaCarta);
                // Cambiamos la ultima carta
                ultimaCarta = cc;
                // quitmos la carta jugada al jugador
                cartasJugador.remove(cc);

//                System.out.println(nombreJugador + Juego.JUEGA + ultimaCarta.toString());
                mensaje.append(nombreJugador).append(Juego.JUEGA).append(ultimaCarta.toString()).append("\n");
//                System.out
//                        .println(nombreJugador + " bloquea a: " + nombreRival + ", " + nombreJugador + " juega de nuevo."+"\n");
                mensaje.append(nombreJugador).append(" bloquea a: ").append(nombreRival).append(", ").append(nombreJugador).append(" juega de nuevo.");
//                System.out.println(Juego.SEPARADOR);

                ////////////////////////////////////////////////////////////////////
                //
                //IMPORTANTE
                //
                //check si la maquina esta jugando, puede volver a jugar
                if (nombreJugador.equals("MAQUINA")) {
                    jugarTurno(cartasJugador, cartasRival, nombreJugador, nombreRival, elegirCarta(cartasJugador));
                }
            }

            if (cc.getTipo() == TipoComodin.CAMBIOCOLOR) {

                elegirColorAleatorio(cc,nombreJugador);

                mazo.add(ultimaCarta);
                ultimaCarta = cc;
                cartasJugador.remove(cc);

//                System.out.println(nombreJugador + Juego.JUEGA + ultimaCarta.toString());
                mensaje.append(nombreJugador).append(Juego.JUEGA).append(ultimaCarta.toString()+"\n");
//                System.out.println(Juego.SEPARADOR);

            }

            if (cc.getTipo() == TipoComodin.MAS2) {

                elegirColorAleatorio(cc,nombreJugador);

                mazo.add(ultimaCarta);
                ultimaCarta = cc;
                cartasJugador.remove(cc);
                System.out.println(nombreJugador + Juego.JUEGA + ultimaCarta.toString());
                System.out.println(nombreRival + " toma dos cartas!");
                mensaje.append(nombreJugador +" "+ Juego.JUEGA + ultimaCarta.toString()+"\n");
                mensaje.append(nombreRival + " toma dos cartas!"+"\n");


                System.out.println(Juego.SEPARADOR);
                darCartas(cartasRival,2);


            }
            if (cc.getTipo() == TipoComodin.MAS4) {
                elegirColorAleatorio(cc,nombreJugador);
                mazo.add(ultimaCarta);
                ultimaCarta = cc;
                cartasJugador.remove(cc);

                System.out.println(nombreRival + "toma CUATRO cartas!");
                System.out.println(nombreJugador + "juega la carta" + ultimaCarta.toString());

                mensaje.append(nombreJugador + "juega la carta" + ultimaCarta.toString()+"\n");
                mensaje.append(nombreRival + "toma CUATRO cartas!"+"\n");

                System.out.println(Juego.SEPARADOR);
                darCartas(cartasRival,4);
            }
            System.out.println(ultimaCarta);
        }
        if(cartasJugador.size()==1)
            mensaje.append(nombreJugador+"Tiene una carta!"+"UNO!!!"+"\n");
    }

    public void darCartas(List<Carta> cartas,int numero){

        for (int i = 0; i < numero; i++) {
            Carta c = robarCarta();
            mensaje.append("Robando carta ").append(c.toString()).append("\n");
            cartas.add(0,c);
        }
    }

    public List<Carta> getCartasPlayer() {
        return cartasPlayer;
    }

    public List<Carta> getCartasMaquina() {
        return cartasMaquina;
    }

    public Carta getUltimaCarta() {
        return ultimaCarta;
    }





}
