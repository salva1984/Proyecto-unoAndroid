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
    TextView statusM;
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
        hcont.removeAllViews();
        for (Carta carta : j.getCartasMaquina()) {
            ImageView imv = new ImageView(this);
            String nombre = carta.toString();
            @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier(nombre, "drawable", getPackageName());
            if (resId != 0) {
                imv.setImageResource(resId);
            } else {
                Log.e("MainActivity", "Recurso no encontrado para: " + nombre);
            }

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
            if (resId != 0) {
                imv.setImageResource(resId);
            } else {
                Log.e("MainActivity", "Recurso no encontrado para: " + nombre);
            }
            imv.setOnClickListener(view -> {

                jugarCartaUI(cartas, hcont, carta);
            });
            hcont.addView(imv);
        }

    }

    private void jugarCartaUI(List<Carta> cartas, LinearLayout hcont, Carta carta) {

        //si no puedes jugar ninguna carta, caes aqui
        if (!(j.revisarMano(j.getCartasPlayer()))) {

            statusP.append("No tienes cartas que jugar!" + "\n");
            //te dan una carta
            j.getCartasPlayer().add(0,j.robarCarta());
            //actualiza tu mano
            actualizarTablero(cartas,hcont2);
            //la maquina juega
            j.jugarTurno(j.getCartasMaquina(),j.getCartasPlayer(),"MAQUINA","JUGADOR", j.elegirCarta(cartas));
            //mostramos lo que sucedio
            statusP.append(j.mensaje.toString()+"\n");
            //actualizamos
            actualizarTablero(cartas,hcont2);
        }
        //revisar si la carta se puede jugar
        else if (j.revisarCarta(carta)) {

            if (carta instanceof CartaComodin) {

                CartaComodin cc = (CartaComodin) carta;
                if (cc.getColor()==Color.n) {
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
                jugarTurnoUI(cartas,hcont2,carta);
            }

            actualizarTablero(cartas, hcont);
        } else {
            // si no se puede jugar, caes aqui
            statusP.append("No se puede jugar esa carta:"+carta.toString()+"\n");
            statusP.append("Puedes jugar la carta"+ j.elegirCarta(j.getCartasPlayer()).toString()+"\n");
        }
    }

    private void manejarComodin(List<Carta> cartas, LinearLayout hcont, CartaComodin cc) {
        if (cc.getTipo() == TipoComodin.BLOQUEO || cc.getTipo() == TipoComodin.REVERSA) {
            //los comodines bloqueo y reversa negros juegan una vez, y no juega la maquina
            j.jugarCarta(cc,j.getCartasPlayer(), j.getCartasMaquina(), "JUGADOR", "MAQUINA");
            statusP.append(j.mensaje.toString()+"\n");
            actualizarTablero(cartas, hcont);
        } else {
            //aqui juegan los cambio de color, mas2, mas4
            // juegas tu, y luego la maquina.
            jugarTurnoUI(cartas, hcont, cc);
        }
    }

    private void jugarTurnoUI(List<Carta> cartas, LinearLayout hcont, Carta cc) {
        //TODO edge case que ocurre cuando se juega un +4 negro y no tienes cartas que tambien se puedan jugar
        j.jugarCarta(cc,j.getCartasPlayer(), j.getCartasMaquina(), "JUGADOR", "MAQUINA");
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
        uc.setImageResource(getResources().getIdentifier(j.getUc().toString(), "drawable", getPackageName()));
    }

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