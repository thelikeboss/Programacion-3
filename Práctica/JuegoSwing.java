package Práctica;
import java.awt.*;
import java.util.*;
import javax.swing.*;

public class JuegoSwing extends JFrame {
    private JTextArea areaTexto;
    private Stack<String> pilaAcciones;
    private Queue<String> colaAcciones;

    public JuegoSwing() {
        setTitle("Practica con Pila y Cola");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializar estructuras
        pilaAcciones = new Stack<>();
        colaAcciones = new LinkedList<>();


        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaTexto);


        JMenuBar barraMenu = new JMenuBar();

        // Menú de Pila
        JMenu menuPila = new JMenu("Juego con Pila");
        JMenuItem moverAdelantePila = new JMenuItem("Mover adelante");
        JMenuItem moverAtrasPila = new JMenuItem("Mover atrás");
        JMenuItem atacarPila = new JMenuItem("Atacar");
        JMenuItem deshacerPila = new JMenuItem("Deshacer última acción");

        moverAdelantePila.addActionListener(e -> {
            pilaAcciones.push("Avanzar");
            areaTexto.append("El jugador avanza (Pila).\n");
        });
        moverAtrasPila.addActionListener(e -> {
            pilaAcciones.push("Retroceder");
            areaTexto.append("El jugador retrocede (Pila).\n");
        });
        atacarPila.addActionListener(e -> {
            pilaAcciones.push("Atacar");
            areaTexto.append("El jugador ataca (Pila).\n");
        });
        deshacerPila.addActionListener(e -> {
            if (!pilaAcciones.isEmpty()) {
                String ultima = pilaAcciones.pop();
                areaTexto.append("Se deshizo la acción: " + ultima + " (Pila).\n");
            } else {
                areaTexto.append("No hay acciones para deshacer (Pila).\n");
            }
        });

        menuPila.add(moverAdelantePila);
        menuPila.add(moverAtrasPila);
        menuPila.add(atacarPila);
        menuPila.add(deshacerPila);

        // Menú de Cola
        JMenu menuCola = new JMenu("Juego con Cola");
        JMenuItem programarAdelanteCola = new JMenuItem("Programar adelante");
        JMenuItem programarAtrasCola = new JMenuItem("Programar atrás");
        JMenuItem programarAtaqueCola = new JMenuItem("Programar ataque");
        JMenuItem ejecutarCola = new JMenuItem("Ejecutar siguiente acción");

        programarAdelanteCola.addActionListener(e -> {
            colaAcciones.add("Avanzar");
            areaTexto.append("Acción programada: Avanzar (Cola).\n");
        });
        programarAtrasCola.addActionListener(e -> {
            colaAcciones.add("Retroceder");
            areaTexto.append("Acción programada: Retroceder (Cola).\n");
        });
        programarAtaqueCola.addActionListener(e -> {
            colaAcciones.add("Atacar");
            areaTexto.append("Acción programada: Atacar (Cola).\n");
        });
        ejecutarCola.addActionListener(e -> {
            if (!colaAcciones.isEmpty()) {
                String siguiente = colaAcciones.poll();
                areaTexto.append("Ejecutando acción: " + siguiente + " (Cola).\n");
            } else {
                areaTexto.append("No hay acciones pendientes (Cola).\n");
            }
        });

        menuCola.add(programarAdelanteCola);
        menuCola.add(programarAtrasCola);
        menuCola.add(programarAtaqueCola);
        menuCola.add(ejecutarCola);


        barraMenu.add(menuPila);
        barraMenu.add(menuCola);

        setJMenuBar(barraMenu);
        add(scroll, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new JuegoSwing().setVisible(true);
        });
    }
}
