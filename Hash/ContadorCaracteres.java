import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class ContadorCaracteres extends JFrame {
    private JTextField inputField;
    private JTextArea resultArea;
    private JButton contarButton;

    public ContadorCaracteres() {
        setTitle("Contador de Caracteres");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        contarButton = new JButton("Contar caracteres");
        topPanel.add(new JLabel("Ingresa una frase:"), BorderLayout.WEST);
        topPanel.add(inputField, BorderLayout.CENTER);
        topPanel.add(contarButton, BorderLayout.EAST);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        contarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contarCaracteres();
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contarCaracteres();
            }
        });
    }

    private void contarCaracteres() {
        String frase = inputField.getText();
        HashMap<Character, Integer> contador = new HashMap<>();

        for (int i = 0; i < frase.length(); i++) {
            char c = frase.charAt(i);
            if (c == ' ') {
                continue;
            }
            contador.put(c, contador.getOrDefault(c, 0) + 1);
        }

      
        java.util.List<Map.Entry<Character, Integer>> lista = new java.util.ArrayList<>(contador.entrySet());
        lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        StringBuilder resultado = new StringBuilder();
        resultado.append("Frecuencia de caracteres (ordenado):\n");
        for (Map.Entry<Character, Integer> entry : lista) {
            resultado.append("").append(entry.getKey()).append(" : ")
                .append(entry.getValue()).append("\n");
        }
        resultArea.setText(resultado.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ContadorCaracteres().setVisible(true);
        });
    }
}
