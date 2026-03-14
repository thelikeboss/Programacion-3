package Parcial;
import java.awt.*;
import java.util.*;
import javax.swing.*;

class NodoExpresion {
    public NodoExpresion Iz, Dr;
    public String texto;

    public NodoExpresion(String nuevo_texto) {
        texto = nuevo_texto;
        Iz = Dr = null;
    }

    public NodoExpresion(String nuevo_texto, NodoExpresion izquierda, NodoExpresion derecha) {
        texto = nuevo_texto;
        Iz = izquierda;
        Dr = derecha;
    }

    public boolean esOperador(String nuevo_texto) {
        String[] mOperadores = {"+", "-", "*", "/", "%", "^"};
        for (String op : mOperadores) {
            if (nuevo_texto.equals(op)) return true;
        }
        return false;
    }

    // PREORDER
    public String preOrder(NodoExpresion raiz) {
        if (raiz == null) return "";
        return raiz.texto + " " + preOrder(raiz.Iz) + preOrder(raiz.Dr);
    }

    // Convertir infija a postfija (Shunting Yard)
    public String infijaAPostfija(String expresion) {
        Stack<String> operadores = new Stack<>();
        StringBuilder salida = new StringBuilder();
        StringTokenizer tokens = new StringTokenizer(expresion, "()+-*/", true);

        Map<String,Integer> precedencia = Map.of(
            "+",1,"-",1,"*",2,"/",2,"^",3
        );

        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (token.isEmpty()) continue;

            if (!esOperador(token) && !token.equals("(") && !token.equals(")")) {
                salida.append(token).append(" ");
            } else if (esOperador(token)) {
                while (!operadores.isEmpty() && !operadores.peek().equals("(") &&
                       precedencia.get(operadores.peek()) >= precedencia.get(token)) {
                    salida.append(operadores.pop()).append(" ");
                }
                operadores.push(token);
            } else if (token.equals("(")) {
                operadores.push(token);
            } else if (token.equals(")")) {
                while (!operadores.isEmpty() && !operadores.peek().equals("(")) {
                    salida.append(operadores.pop()).append(" ");
                }
                operadores.pop(); // quitar "("
            }
        }
        while (!operadores.isEmpty()) {
            salida.append(operadores.pop()).append(" ");
        }
        return salida.toString().trim();
    }

    // CREAR ÁRBOL DESDE POSTFIJA
    public NodoExpresion CrearArbolDeExpresiones(String expresion) {
        if (expresion == null || expresion.isEmpty()) return null;
        Stack<NodoExpresion> pila = new Stack<>();
        for (String caracter : expresion.split("\\s+")) {
            if (esOperador(caracter)) {
                NodoExpresion derecha = pila.pop();
                NodoExpresion izquierda = pila.pop();
                pila.push(new NodoExpresion(caracter, izquierda, derecha));
            } else {
                pila.push(new NodoExpresion(caracter));
            }
        }
        return pila.peek();
    }
}

public class ArbolDeExpresionGUI extends JFrame {
    private JTextField txtExpresion;
    private JTextArea txtResultado;

    public ArbolDeExpresionGUI() {
        setTitle("Árbol de Expresiones");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new FlowLayout());
        panelSuperior.add(new JLabel("Expresión Infija:"));
        txtExpresion = new JTextField(20);
        panelSuperior.add(txtExpresion);

        JButton btnCalcular = new JButton("Generar");
        panelSuperior.add(btnCalcular);
        add(panelSuperior, BorderLayout.NORTH);

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);

        btnCalcular.addActionListener(e -> generarArbol());
    }

    private void generarArbol() {
        String expresionInfija = txtExpresion.getText();
        NodoExpresion nodo = new NodoExpresion("");
        String postfija = nodo.infijaAPostfija(expresionInfija);
        NodoExpresion raiz = nodo.CrearArbolDeExpresiones(postfija);

        if (raiz == null) {
            txtResultado.setText("Expresión inválida");
            return;
        }

        String pre = nodo.preOrder(raiz);

        txtResultado.setText(
            "Raíz: " + raiz.texto + "\n\n" +
            "Hojas: " + obtenerHojas(raiz) + "\n\n" +
            "PreOrder:  " + pre + "\n\n" 
           
        );
    }

    private String obtenerHojas(NodoExpresion raiz) {
        if (raiz == null) return "";
        if (raiz.Iz == null && raiz.Dr == null) return raiz.texto + " ";
        return obtenerHojas(raiz.Iz) + obtenerHojas(raiz.Dr);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ArbolDeExpresionGUI().setVisible(true));
    }
}
