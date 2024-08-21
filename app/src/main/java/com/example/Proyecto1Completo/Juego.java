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


    StringBuilder mensaje;
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
    boolean turnoJugador;
    // carta inicial
    Carta uc;

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
        turnoJugador = true;
        // carta inicial
        uc = this.robarCarta();
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
            if (uc instanceof CartaNormal) {

                // Hacemos downcasting por las mismas razones
                CartaNormal ucn = (CartaNormal) uc;

                // Comparamos si ambas comparten numero o color
                if (cn.getNumero() == ucn.getNumero() || cn.getColor() == ucn.getColor())
                    return true;
            }
            if (uc instanceof CartaComodin && cn.getColor() == uc.getColor())
                return true;

        }

        if (cartaJugar instanceof CartaComodin) {
            if (cartaJugar.getColor() == Color.n)
                return true; // Siempre es posible jugar una carta negra
            // la ultima carta puede ser comodin o normal, pero al jugar una carta comodin
            // encima de esta solo nos importa el color porque los comodines no tiene
            // numeros
            return uc.getColor() == cartaJugar.getColor();

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
        System.out.println("Ultima carta: " + uc);
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

    // metodo solo de maquina
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
        System.out.println(Juego.SEPARADOR);// -------
        System.out.println("Ultima carta: " + uc);
        System.out.println("Turno de:" + nombreJugador);
        System.out.println("cartas del jugador: ");
        System.out.println(cartasPlayer.toString());
        System.out.println("cartas de la maquina");
        System.out.println(cartasMaquina);

        if (nombreJugador.equals("MAQUINA")) {
            if (elegirCarta(cartasJugador) == null) {
                cartasJugador.add(0,robarCarta());
                mensaje.append(nombreJugador).append(" no tiene cartas disponibles para jugar, saltando turno.");
                System.out.println(nombreJugador + " no tiene cartas disponibles para jugar, saltando turno.");
            } else {
                jugarCarta(elegirCarta(cartasJugador), cartasJugador, cartasRival, nombreJugador, nombreRival);
            }
        } else { // Aqui juega el jugador

            if (revisarMano(cartasJugador)) {
                jugarCarta(cartaJugar, cartasJugador, cartasRival, nombreJugador, nombreRival);
            } else {
                mensaje.delete(0, mensaje.length());
                mensaje.append(
                        "Jugador no tiene cartas disponibles para jugar, tomando carta y pasando turno "+"\n");
                cartasJugador.add(0,robarCarta());
                //sc.nextLine();

            }
        }
    }

    //TODO
    public void jugarComodinColor(CartaComodin cc, String nombreJugador, SeleccionarColorCallback callback) {
        if (cc.getColor() == Color.n) {

            if (nombreJugador.equals("JUGADOR")) {
                System.out.println(
                        "Ingresa que color quieres que tome la carta cambie de color: R(rojo),A(amarillo),z(azul),v(verde)");
                mensaje.append(
                        "Ingresa que color quieres que tome la carta cambie de color: R(rojo),A(amarillo),z(azul),v(verde)"+"\n");
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


        mensaje = new StringBuilder();
        if (cartaJugar instanceof CartaNormal) {
            // Metemos al mazo la ultima carta
            mazo.add(uc);
            // Cambiamos la ultima carta
            uc = cartaJugar;
            // quitmos la carta jugada al jugador
            cartasJugador.remove(uc);
            mensaje.append(nombreJugador).append(Juego.JUEGA).append(uc.toString());
            System.out.println(Juego.SEPARADOR);

        }

        if (cartaJugar instanceof CartaComodin) {
            CartaComodin cc = (CartaComodin) cartaJugar;

            //bloqueo y reversa (funcionan igual)
            if (cc.getTipo() == TipoComodin.BLOQUEO || cc.getTipo() == TipoComodin.REVERSA) {



                // Metemos al mazo la ultima carta
                mazo.add(uc);
                // Cambiamos la ultima carta
                uc = cc;
                // quitmos la carta jugada al jugador
                cartasJugador.remove(cc);
                System.out.println(nombreJugador + Juego.JUEGA + uc.toString());
                mensaje.append(nombreJugador).append(Juego.JUEGA).append(uc.toString());
                System.out
                        .println(nombreJugador + " bloquea a: " + nombreRival + ", " + nombreJugador + " juega de nuevo.");
                mensaje.append(nombreJugador).append(" bloquea a: ").append(nombreRival).append(", ").append(nombreJugador).append(" juega de nuevo.");
                System.out.println(Juego.SEPARADOR);

                //TODO
                //check si la maquina esta jugando, puede volver a jugar
                if (nombreJugador.equals("MAQUINA")) {
                    jugarTurno(cartasJugador, cartasRival, nombreJugador, nombreRival, elegirCarta(cartasJugador));
                }
            }

            if (cc.getTipo() == TipoComodin.CAMBIOCOLOR) {
                elegirColorAleatorio(cc,nombreJugador);
                mazo.add(uc);
                uc = cc;
                cartasJugador.remove(cc);
                System.out.println(nombreJugador + Juego.JUEGA + uc.toString());
                mensaje.append(nombreJugador).append(Juego.JUEGA).append(uc.toString());
                System.out.println(Juego.SEPARADOR);


            }

            if (cc.getTipo() == TipoComodin.MAS2) {
                elegirColorAleatorio(cc,nombreJugador);
                mazo.add(uc);
                uc = cc;
                cartasJugador.remove(cc);
                System.out.println(nombreJugador + Juego.JUEGA + uc.toString());
                System.out.println(nombreRival + " toma dos cartas!");
                mensaje.append(nombreJugador + Juego.JUEGA + uc.toString());
                mensaje.append(nombreRival + " toma dos cartas!");
                System.out.println(Juego.SEPARADOR);
                cartasRival.add(0,robarCarta());
                cartasRival.add(0,robarCarta());


            }
            if (cc.getTipo() == TipoComodin.MAS4) {
                elegirColorAleatorio(cc,nombreJugador);
                mazo.add(uc);
                uc = cc;
                cartasJugador.remove(cc);
                System.out.println(nombreRival + "toma CUATRO cartas!");
                System.out.println(nombreJugador + "juega la carta" + uc.toString());
                mensaje.append(nombreRival + "toma CUATRO cartas!");
                mensaje.append(nombreJugador + "juega la carta" + uc.toString());
                System.out.println(Juego.SEPARADOR);
                cartasRival.add(0,robarCarta());
                cartasRival.add(0,robarCarta());
                cartasRival.add(0,robarCarta());
                cartasRival.add(0,robarCarta());

            }
            System.out.println(uc);
        }
    }

    public Scanner getSc() {
        return sc;
    }

    public Random getRd() {
        return rd;
    }

    public List<Carta> getMazo() {
        return mazo;
    }

    public Jugador getJh() {
        return jh;
    }

    public Jugador getJm() {
        return jm;
    }

    public List<Carta> getCartasPlayer() {
        return cartasPlayer;
    }

    public List<Carta> getCartasMaquina() {
        return cartasMaquina;
    }

    public Carta getUc() {
        return uc;
    }

    public static String getSeparador() {
        return SEPARADOR;
    }

    public static String getJuega() {
        return JUEGA;
    }

}
