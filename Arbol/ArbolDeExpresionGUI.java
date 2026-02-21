import java.util.EmptyStackException;
import java.util.Stack;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
            if (nuevo_texto.equals(op)) {
                return true;
            }
        }
        return false;
    }

    // PREORDER
    public String preOrder(NodoExpresion raiz) {
        if (raiz == null) return "";
        return raiz.texto +
               preOrder(raiz.Iz) +
               preOrder(raiz.Dr);
    }

    // INORDER
    public String inOrder(NodoExpresion raiz) {
        if (raiz == null) return "";

        String resultado = "";

        if (esOperador(raiz.texto)) resultado += "(";

        resultado += inOrder(raiz.Iz);
        resultado += raiz.texto;
        resultado += inOrder(raiz.Dr);

        if (esOperador(raiz.texto)) resultado += ")";

        return resultado;
    }

    // POSTORDER
    public String postOrder(NodoExpresion raiz) {
        if (raiz == null) return "";

        return postOrder(raiz.Iz) +
               postOrder(raiz.Dr) +
               raiz.texto;
    }

    // CREAR ARBOL DESDE POSTFIJA
    public NodoExpresion CrearArbolDeExpresiones(String expresion) {

        if (expresion == null || expresion.isEmpty()) return null;

        Stack<NodoExpresion> pila = new Stack<>();

        for (char c : expresion.toCharArray()) {

            String caracter = String.valueOf(c);

            if (esOperador(caracter)) {

                NodoExpresion derecha = pila.pop();
                NodoExpresion izquierda = pila.pop();

                NodoExpresion nodo = new NodoExpresion(caracter, izquierda, derecha);
                pila.push(nodo);
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

        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new FlowLayout());

        panelSuperior.add(new JLabel("Expresión Postfija:"));
        txtExpresion = new JTextField(15);
        panelSuperior.add(txtExpresion);

        JButton btnCalcular = new JButton("Generar");
        panelSuperior.add(btnCalcular);

        add(panelSuperior, BorderLayout.NORTH);

        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);

        btnCalcular.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generarArbol();
            }
        });
    }

    private void generarArbol() {
        String expresion = txtExpresion.getText();

        NodoExpresion nodo = new NodoExpresion("");
        NodoExpresion raiz = nodo.CrearArbolDeExpresiones(expresion);

        if (raiz == null) {
            txtResultado.setText("Expresión inválida");
            return;
        }

        String pre = nodo.preOrder(raiz);
        String in = nodo.inOrder(raiz);
        String post = nodo.postOrder(raiz);

        txtResultado.setText(
                "PreOrder:  " + pre + "\n\n" +
                "InOrder:   " + in + "\n\n" +
                "PostOrder: " + post
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ArbolDeExpresionGUI().setVisible(true);
            }
        });
    }
}