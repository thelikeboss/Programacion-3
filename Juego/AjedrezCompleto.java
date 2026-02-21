package Juego;
import java.awt.*;
import java.util.Stack;
import javax.swing.*;

public class AjedrezCompleto extends JFrame {

    private JButton[][] tablero = new JButton[8][8];
    private String[][] estado = new String[8][8];

    private boolean turnoBlancas = true;
    private JLabel lblTurno;

    private Stack<Movimiento> pila = new Stack<>();

    private int filaSel = -1, colSel = -1;

    public AjedrezCompleto() {

        setTitle("Ajedrez con Reglas y Jaque");
        setSize(700, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(8, 8));

        for (int f = 0; f < 8; f++) {
            for (int c = 0; c < 8; c++) {

                tablero[f][c] = new JButton();
tablero[f][c].setFont(new Font("Segoe UI Symbol", Font.PLAIN, 40));
                if ((f + c) % 2 == 0)
                    tablero[f][c].setBackground(new Color(240, 217, 181));
                else
                    tablero[f][c].setBackground(new Color(181, 136, 99));

                final int fila = f;
                final int col = c;

                tablero[f][c].addActionListener(e -> click(fila, col));

                panel.add(tablero[f][c]);
            }
        }

        inicializar();

        lblTurno = new JLabel("Turno: Blancas");
        lblTurno.setHorizontalAlignment(SwingConstants.CENTER);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 20));

        JButton btnDeshacer = new JButton("Deshacer");
        btnDeshacer.addActionListener(e -> deshacer());

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(lblTurno, BorderLayout.NORTH);
        abajo.add(btnDeshacer, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);

        actualizar();
        setVisible(true);
    }

    private void inicializar() {

        String[] negras = {"♜","♞","♝","♛","♚","♝","♞","♜"};
        String[] blancas = {"♖","♘","♗","♕","♔","♗","♘","♖"};

        for (int i = 0; i < 8; i++) {
            estado[0][i] = negras[i];
            estado[1][i] = "♟";
            estado[6][i] = "♙";
            estado[7][i] = blancas[i];
        }
    }

    private void click(int f, int c) {

        if (filaSel == -1) {

            if (estado[f][c] != null && esTurnoCorrecto(estado[f][c])) {
                filaSel = f;
                colSel = c;
            }

        } else {

            if (movimientoValido(filaSel, colSel, f, c)) {

                String capturada = estado[f][c];

                estado[f][c] = estado[filaSel][colSel];
                estado[filaSel][colSel] = null;

                if (!reyEnJaque(turnoBlancas)) {

                    pila.push(new Movimiento(filaSel, colSel, f, c, capturada));

                    turnoBlancas = !turnoBlancas;
                    lblTurno.setText("Turno: " + (turnoBlancas ? "Blancas" : "Negras"));

                    if (reyEnJaque(turnoBlancas)) {
                        JOptionPane.showMessageDialog(this, "¡JAQUE!");
                    }

                } else {
                    estado[filaSel][colSel] = estado[f][c];
                    estado[f][c] = capturada;
                }
            }

            filaSel = -1;
            colSel = -1;
        }

        actualizar();
    }

    private boolean esTurnoCorrecto(String pieza) {
        boolean blanca = pieza.matches("[♖♘♗♕♔♙]");
        return turnoBlancas == blanca;
    }

    private boolean movimientoValido(int f1, int c1, int f2, int c2) {

        if (estado[f2][c2] != null &&
                esTurnoCorrecto(estado[f2][c2]))
            return false;

        String pieza = estado[f1][c1];

        int df = f2 - f1;
        int dc = c2 - c1;

        switch (pieza) {

            case "♙": return dc == 0 && df == -1 && estado[f2][c2] == null;
            case "♟": return dc == 0 && df == 1 && estado[f2][c2] == null;

            case "♖": case "♜":
                return f1 == f2 || c1 == c2;

            case "♗": case "♝":
                return Math.abs(df) == Math.abs(dc);

            case "♕": case "♛":
                return f1 == f2 || c1 == c2 || Math.abs(df) == Math.abs(dc);

            case "♘": case "♞":
                return (Math.abs(df) == 2 && Math.abs(dc) == 1)
                        || (Math.abs(df) == 1 && Math.abs(dc) == 2);

            case "♔": case "♚":
                return Math.abs(df) <= 1 && Math.abs(dc) <= 1;
        }

        return false;
    }

    private boolean reyEnJaque(boolean blancas) {

        int reyF = -1, reyC = -1;
        String rey = blancas ? "♔" : "♚";

        for (int f = 0; f < 8; f++)
            for (int c = 0; c < 8; c++)
                if (rey.equals(estado[f][c])) {
                    reyF = f;
                    reyC = c;
                }

        for (int f = 0; f < 8; f++)
            for (int c = 0; c < 8; c++)
                if (estado[f][c] != null &&
                        esTurnoCorrecto(estado[f][c]) != blancas)
                    if (movimientoValido(f, c, reyF, reyC))
                        return true;

        return false;
    }

    private void deshacer() {

        if (!pila.isEmpty()) {

            Movimiento m = pila.pop();

            estado[m.f1][m.c1] = estado[m.f2][m.c2];
            estado[m.f2][m.c2] = m.capturada;

            turnoBlancas = !turnoBlancas;
            lblTurno.setText("Turno: " + (turnoBlancas ? "Blancas" : "Negras"));

            actualizar();
        }
    }

    private void actualizar() {

        for (int f = 0; f < 8; f++)
            for (int c = 0; c < 8; c++)
                tablero[f][c].setText(estado[f][c] == null ? "" : estado[f][c]);
    }

    public static void main(String[] args) {
        new AjedrezCompleto();
    }

    class Movimiento {
        int f1, c1, f2, c2;
        String capturada;

        public Movimiento(int f1, int c1, int f2, int c2, String capturada) {
            this.f1 = f1;
            this.c1 = c1;
            this.f2 = f2;
            this.c2 = c2;
            this.capturada = capturada;
        }
    }
}