package com.example.Proyecto1Completo;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.Proyecto1Completo.cartas.Carta;
import com.example.Proyecto1Completo.cartas.CartaComodin;
import com.example.Proyecto1Completo.cartas.Color;
import com.example.Proyecto1Completo.cartas.TipoComodin;
import com.example.cartelera.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    //crear una actividad


    Juego j;


    ImageView uc;
    TextView statusP;

    LinearLayout hcont;
    LinearLayout hcont2;
    //TODO
    //botones para elegir color
    Button br;
    Button ba;
    Button bz;
    Button bv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // IMPORTANTE
        // PARA PODER LLAMAR METODOS DE EL MAIN EN EL JUEGO, SE TIENE QUE CREAR JUEGO CON ESTE ACTIVITY
        j = new Juego(this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        hcont = findViewById(R.id.hcont);
        hcont2 = findViewById(R.id.hcont2);
        statusP = findViewById(R.id.statusP);

        uc = findViewById(R.id.uc);
        br = findViewById(R.id.br);
        ba = findViewById(R.id.ba);
        bz = findViewById(R.id.bz);
        bv = findViewById(R.id.bv);


        construirHcont();

        actualizarUc();

        construirHcont2(j.getCartasPlayer(), hcont2);


    }

    private void construirHcont() {
        actualizarUc();
        hcont.removeAllViews(); //se quitan todas las imagenes

        for (Carta carta : j.getCartasMaquina()) {
            ImageView imv = new ImageView(this);

            String nombre = carta.toString();

            @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier(nombre, "drawable", getPackageName());

            imv.setImageResource(resId);

            hcont.addView(imv);
        }
    }


    @SuppressLint("SetTextI18n")
    public void construirHcont2(List<Carta> cartas, LinearLayout hcont) {

        hcont.removeAllViews();
        // borra todas las vistas para que cuando se vuelva a crear
        if (j.getCartasPlayer().isEmpty())
            statusP.setText("Gana el jugador!");

        for (Carta carta : cartas) {
            ImageView imv = new ImageView(this);
            String nombre = carta.toString();
            int resId = getResources().getIdentifier(nombre, "drawable", getPackageName());

            imv.setImageResource(resId);

            imv.setOnClickListener(view -> {
                jugarCartaUI(cartas, hcont, carta);
            });
            hcont.addView(imv);
        }

    }

    private void jugarCartaUI(List<Carta> cartas, LinearLayout hcont, Carta carta) {

        if(j.getCartasMaquina().isEmpty()){
            //si la maquina esta vacia, no hagas nada loco
            statusP.setText("Gana la maquina!");}
        else {


            if (!(j.revisarMano(j.getCartasPlayer()))) {
                //si no puedes jugar ninguna carta para jugar , caes aqui

                statusP.append("No tienes cartas que jugar!" + "\n");
                //te dan una carta, borra los mensajes por si jugaste un bloqueo y de una te mando a jugar de nuevo aca
                j.mensaje.delete(0, j.mensaje.length());

                j.darCartas(j.getCartasPlayer(),1);
                statusP.append(j.mensaje.toString() + "\n");
                //actualiza tu mano
                actualizarTablero(cartas, hcont2);
                //borra los mensajes lol

                //la maquina juega
                j.jugarTurno(j.getCartasMaquina(), j.getCartasPlayer(), "MAQUINA", "JUGADOR", j.elegirCarta(cartas));
                //mostramos lo que sucedio
                statusP.append(j.mensaje.toString() + "\n");
                //actualizamos
                actualizarTablero(cartas, hcont2);
            }
            //revisar si la carta se puede jugar
            else if (j.revisarCarta(carta)) {

                if (carta instanceof CartaComodin) {

                    CartaComodin cc = (CartaComodin) carta;
                    if (cc.getColor() == Color.n) {
                        // aqui juegan los comodines negros, que requieren que escojas un color y tienen que esperar.
                        //callback
                        j.jugarComodinColor(cc, "JUGADOR", new SeleccionarColorCallback() {
                            @Override
                            public void seleccionarColor(Color color) {
//                            cc.setColor(color);
                                manejarComodin(cartas, hcont, cc);
                            }
                        });
                    } else {
                        // aqui juegan los comodines que no son negros (mas2,mas4,bloqueo,reversa)
                        manejarComodin(cartas, hcont, cc);
                    }
                } else {
                    //juegan cartas normales
                    jugarTurnoUI(cartas, hcont2, carta);
                }

                actualizarTablero(cartas, hcont);
            } else {
                // si no se puede jugar, caes aqui
                statusP.append("No se puede jugar esa carta:" + carta.toString() + "\n");
                statusP.append("Puedes jugar la carta" + j.elegirCarta(j.getCartasPlayer()).toString() + "\n");
            }
        }
    }

    private void manejarComodin(List<Carta> cartas, LinearLayout hcont, CartaComodin cc) {
        if (cc.getTipo() == TipoComodin.BLOQUEO || cc.getTipo() == TipoComodin.REVERSA) {
            //los comodines bloqueo y reversa negros juegan una vez, y no juega la maquina
            j.jugarTurno(j.getCartasPlayer(), j.getCartasMaquina(), "JUGADOR", "MAQUINA",cc);
            statusP.append(j.mensaje.toString()+"\n");
            actualizarTablero(cartas, hcont);
        } else {
            //aqui juegan los cambio de color, mas2, mas4
            // juegas tu, y luego la maquina.
            jugarTurnoUI(cartas, hcont, cc);
        }
    }

    private void jugarTurnoUI(List<Carta> cartas, LinearLayout hcont, Carta cc) {

        j.jugarTurno(j.getCartasPlayer(), j.getCartasMaquina(), "JUGADOR", "MAQUINA",cc);
        statusP.append(j.mensaje.toString()+"\n");
        j.jugarTurno(j.getCartasMaquina(), j.getCartasPlayer(), "MAQUINA", "JUGADOR", cc);
        statusP.append(j.mensaje.toString()+"\n");
        actualizarTablero(cartas, hcont);
    }

    private void actualizarTablero(List<Carta> cartas, LinearLayout hcont) {
        construirHcont2(cartas, hcont);
        construirHcont();
        actualizarUc();
    }


    @SuppressLint("DiscouragedApi")
    public void actualizarUc() {
        uc.setImageResource(getResources().getIdentifier(j.getUltimaCarta().toString(), "drawable", getPackageName()));
    }

    //metodo que selecciona el color.
    public void retornarColor(SeleccionarColorCallback callback) {


        br.setVisibility(TextView.VISIBLE);
        ba.setVisibility(TextView.VISIBLE);
        bv.setVisibility(TextView.VISIBLE);
        bz.setVisibility(TextView.VISIBLE);

        br.setOnClickListener(view -> {
            callback.seleccionarColor(Color.r);
            ocultarBotones();
        });
        ba.setOnClickListener(view -> {
            callback.seleccionarColor(Color.a);
            ocultarBotones();
        });
        bv.setOnClickListener(view -> {
            callback.seleccionarColor(Color.v);
            ocultarBotones();
        });
        bz.setOnClickListener(view -> {
            callback.seleccionarColor(Color.z);
            ocultarBotones();
        });
    }

    private void ocultarBotones() {
        br.setVisibility(TextView.INVISIBLE);
        ba.setVisibility(TextView.INVISIBLE);
        bv.setVisibility(TextView.INVISIBLE);
        bz.setVisibility(TextView.INVISIBLE);
    }
}