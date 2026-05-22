package Parcial2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Clase Nodo AVL
class NodoAVL {

    int valor;
    int altura;
    NodoAVL izquierdo, derecho;

    public NodoAVL(int valor, int altura) {
        this.valor = valor;
        this.altura = altura;
    }
}

public class AVLVisualizer extends JFrame {

    private JTextField alturaIzqField;
    private JTextField alturaDerField;
    private JTextArea resultadoArea;

    public AVLVisualizer() {
        setTitle("Factor de Equilibrio AVL");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        add(new JLabel("Altura hijo izquierdo:"));
        alturaIzqField = new JTextField(5);
        add(alturaIzqField);

        add(new JLabel("Altura hijo derecho:"));
        alturaDerField = new JTextField(5);
        add(alturaDerField);

        JButton calcularBtn = new JButton("Calcular");
        add(calcularBtn);

        resultadoArea = new JTextArea(10, 30);
        resultadoArea.setEditable(false);
        add(new JScrollPane(resultadoArea));

        calcularBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                evaluarNodo();
            }
        });
    }

    // Método para calcular factor de equilibrio
    private int factorEquilibrio(NodoAVL nodo) {
        int alturaIzq = (nodo.izquierdo != null) ? nodo.izquierdo.altura : 0;
        int alturaDer = (nodo.derecho != null) ? nodo.derecho.altura : 0;
        return alturaIzq - alturaDer;
    }

    // Método que evalúa el nodo y determina la rotación
    private void evaluarNodo() {
        try {
            int hIzq = Integer.parseInt(alturaIzqField.getText());
            int hDer = Integer.parseInt(alturaDerField.getText());

            NodoAVL raiz = new NodoAVL(0, Math.max(hIzq, hDer) + 1);
            raiz.izquierdo = new NodoAVL(0, hIzq);
            raiz.derecho = new NodoAVL(0, hDer);

            int fe = factorEquilibrio(raiz);
            String resultado = "Factor de equilibrio: " + fe + "\n";

            if (fe > 1) {
                int feHijo = factorEquilibrio(raiz.izquierdo);
                if (feHijo >= 0) {
                    resultado += "Rotación: Simple a la Derecha\n";
                } else {
                    resultado += "Rotación: Doble Izquierda-Derecha\n";
                }
            } else if (fe < -1) {
                int feHijo = factorEquilibrio(raiz.derecho);
                if (feHijo <= 0) {
                    resultado += "Rotación: Simple a la Izquierda\n";
                } else {
                    resultado += "Rotación: Doble Derecha-Izquierda\n";
                }
            } else {
                resultado += "El nodo está balanceado\n";
            }

            resultadoArea.setText(resultado);

        } catch (NumberFormatException ex) {
            resultadoArea.setText("Ingrese valores válidos.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AVLVisualizer().setVisible(true);
        });
    }
}
