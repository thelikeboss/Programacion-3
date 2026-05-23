package p2;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// ==========================================
// 1. CLASE BLOQUE (Estructura de Datos)
// ==========================================
class Block {
    public int index;
    public long timestamp;
    public String data; // Guardara los datos ENCRIPTADOS para proteger la privacidad
    public String previousHash;
    public String hash;
    public String hospitalId;
    public String patientSignature;

    public Block(int index, String data, String previousHash, String hospitalId, String patientSignature) {
        this.index = index;
        this.timestamp = new Date().getTime();
        this.data = data;
        this.previousHash = previousHash;
        this.hospitalId = hospitalId;
        this.patientSignature = patientSignature;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String input = index + Long.toString(timestamp) + previousHash + data + hospitalId + patientSignature;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

// ==========================================
// 2. SIMULACIÓN DE SMART CONTRACT Y CIFRADO
// ==========================================
class MedicalSmartContract {
    private static final String VALID_PATIENT_KEY = "VickySecretKey123";

    public static boolean validateAccess(String patientPrivateKey) {
        return VALID_PATIENT_KEY.equals(patientPrivateKey);
    }

    public static String signAuthorization(String privateKey, String hospitalId, String data) {
        if (!validateAccess(privateKey)) {
            return null; 
        }
        
        String inputToSign = privateKey + hospitalId + data;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(inputToSign.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return "DIGITAL_SIG_" + hexString.toString().substring(0, 16).toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Encripta el registro usando una operacion XOR basada en la clave.
     */
    public static String encryptData(String data, String key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            sb.append((char) (data.charAt(i) ^ key.charAt(i % key.length())));
        }
        return sb.toString();
    }

    /**
     * Descifra el registro medico.
     */
    public static String decryptData(String encryptedData, String key) {
        return encryptData(encryptedData, key); // La operacion XOR es simetrica
    }
}

// ==========================================
// 3. LA CADENA DE BLOQUES (Blockchain)
// ==========================================
class Blockchain {
    public ArrayList<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
        String genesisText = "Bloque Genesis - Historial Clinico de Victoria";
        String encryptedGenesis = MedicalSmartContract.encryptData(genesisText, "SYSTEM_INIT");
        chain.add(new Block(0, encryptedGenesis, "0", "Sistema Central", "GENESIS_SIG_VALID"));
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public boolean addBlock(String data, String hospitalId, String patientPrivateKey) {
        String signature = MedicalSmartContract.signAuthorization(patientPrivateKey, hospitalId, data);

        if (signature == null) {
            return false; 
        }

        String encryptedData = MedicalSmartContract.encryptData(data, patientPrivateKey);

        Block newBlock = new Block(chain.size(), encryptedData, getLatestBlock().hash, hospitalId, signature);
        chain.add(newBlock);
        return true;
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                return false;
            }

            if (!currentBlock.previousHash.equals(previousBlock.hash)) {
                return false;
            }
        }
        return true;
    }
}

// ==========================================
// 4. INTERFAZ GRÁFICA PRINCIPAL (Main.java)
// ==========================================
public class Main extends JFrame {

    private Blockchain medicalRecords;
    private JTextArea txtLedger;
    private JLabel lblStatus;
    private JTextField txtData;
    private JTextField txtHospital;
    private JPasswordField txtPrivateKey;

    public Main() {
        medicalRecords = new Blockchain();
        initUI();
        updateLedgerView(""); 
    }

    private void initUI() {
        setTitle("Blockchain - Historial Clinico Compartido (Victoria)");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Panel Superior: Estado de Integridad ---
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(new Color(240, 244, 248));
        lblStatus = new JLabel("ESTADO DE LA RED: VALIDANDO...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatus.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelTop.add(lblStatus, BorderLayout.CENTER);
        add(panelTop, BorderLayout.NORTH);

        // --- Panel Central: Consola de Visualización del Ledger ---
        txtLedger = new JTextArea();
        txtLedger.setEditable(false);
        txtLedger.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLedger.setBackground(new Color(30, 30, 30));
        txtLedger.setForeground(new Color(220, 220, 220));
        JScrollPane scrollPane = new JScrollPane(txtLedger);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bloques de la Cadena (Datos Cifrados si no hay autorizacion)"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel Inferior: Controles e Interacción ---
        JPanel panelBottom = new JPanel(new GridLayout(1, 2, 10, 10));
        panelBottom.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Formulario de entrada
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Operaciones del Historial Clinico"));
        
        panelForm.add(new JLabel("ID Hospital / Clinica Solicitante:"));
        txtHospital = new JTextField("Hospital General");
        panelForm.add(txtHospital);

        panelForm.add(new JLabel("Datos Medicos (Escribir):"));
        txtData = new JTextField("Paciente presenta optimo estado fisico.");
        panelForm.add(txtData);

        panelForm.add(new JLabel("Llave Privada Paciente (Autorizar Acceso):"));
        txtPrivateKey = new JPasswordField("VickySecretKey123");
        panelForm.add(txtPrivateKey);

        JButton btnAdd = new JButton("Autorizar Escritura (Anadir Bloque)");
        panelForm.add(btnAdd);
        panelBottom.add(panelForm);

        // Acciones de Red (Validar, Leer, Hackear)
        JPanel panelActions = new JPanel(new GridLayout(3, 1, 5, 5));
        panelActions.setBorder(BorderFactory.createTitledBorder("Controles de Acceso y Red"));
        
        JButton btnRead = new JButton("Desencriptar y Leer Historial Clinico");
        JButton btnHack = new JButton("Simular Ataque Malicioso (Alterar Bloque #1)");
        btnHack.setBackground(new Color(255, 204, 204));
        JButton btnReset = new JButton("Reiniciar Blockchain");

        panelActions.add(btnRead);
        panelActions.add(btnHack);
        panelActions.add(btnReset);
        panelBottom.add(panelActions);

        add(panelBottom, BorderLayout.SOUTH);

        // --- Gestión de Eventos (Controladores) ---
        
        // BOTÓN: AÑADIR BLOQUE (ESCRITURA)
        btnAdd.addActionListener(e -> {
            String hospital = txtHospital.getText();
            String data = txtData.getText();
            String key = new String(txtPrivateKey.getPassword());

            if (hospital.isEmpty() || data.isEmpty() || key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = medicalRecords.addBlock(data, hospital, key);
            if (success) {
                txtData.setText(""); 
                updateLedgerView(key); 
            } else {
                JOptionPane.showMessageDialog(this, 
                    "[Smart Contract] Transaccion Rechazada:\nEl paciente denego el acceso. Llave de autorizacion incorrecta.", 
                    "Error de Permisos", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // BOTÓN: LEER HISTORIAL (LECTURA AUTORIZADA)
        btnRead.addActionListener(e -> {
            String key = new String(txtPrivateKey.getPassword());
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar la llave privada del paciente para solicitar acceso de lectura.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (MedicalSmartContract.validateAccess(key)) {
                updateLedgerView(key);
                JOptionPane.showMessageDialog(this, "Acceso Autorizado por el Smart Contract. Desencriptando datos...", "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
            } else {
                updateLedgerView("CLAVE_ERRONEA"); 
                JOptionPane.showMessageDialog(this, "[Smart Contract] Acceso Denegado: Llave incorrecta. Los datos permanecen protegidos.", "Fallo de Lectura", JOptionPane.ERROR_MESSAGE);
            }
        });

        // BOTÓN: SIMULAR MODIFICACIÓN DIRECTA
        btnHack.addActionListener(e -> {
            if (medicalRecords.chain.size() < 2) {
                JOptionPane.showMessageDialog(this, "Debe anadir al menos un bloque medico legitimo para simular el hackeo.", "Informacion", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            medicalRecords.chain.get(1).data = "Datos alterados maliciosamente por un intruso externo.";
            String key = new String(txtPrivateKey.getPassword());
            updateLedgerView(key);
            JOptionPane.showMessageDialog(this, "Ataque simulado. Se alteraron directamente los bits guardados en el Bloque 1.", "Aviso del Sistema", JOptionPane.WARNING_MESSAGE);
        });

        // BOTÓN: REINICIAR
        btnReset.addActionListener(e -> {
            medicalRecords = new Blockchain();
            updateLedgerView("");
        });
    }

    private void updateLedgerView(String decryptionKey) {
        StringBuilder sb = new StringBuilder();
        
        for (Block b : medicalRecords.chain) {
            String displayedData;
            
            if (b.index == 0) {
                displayedData = MedicalSmartContract.decryptData(b.data, "SYSTEM_INIT");
            } else {
                if (MedicalSmartContract.validateAccess(decryptionKey)) {
                    displayedData = MedicalSmartContract.decryptData(b.data, decryptionKey);
                } else {
                    displayedData = "[CIFRADO - REQUIERE AUTORIZACION] -> " + b.data;
                }
            }

            sb.append("================================================================================\n");
            sb.append(" INDICE:        ").append(b.index).append("\n");
            sb.append(" TIMESTAMP:     ").append(b.timestamp).append("\n");
            sb.append(" HOSPITAL:      ").append(b.hospitalId).append("\n");
            sb.append(" FIRMA PACIENTE:").append(b.patientSignature).append("\n");
            sb.append(" REGISTRO DATA: ").append(displayedData).append("\n");
            sb.append(" PREVIOUS HASH: ").append(b.previousHash).append("\n");
            sb.append(" HASH BLOQUE:   ").append(b.hash).append("\n");
        }
        sb.append("================================================================================\n");
        txtLedger.setText(sb.toString());

        if (medicalRecords.isChainValid()) {
            lblStatus.setText("ESTADO DE LA RED: Blockchain valida e intacta (Inmutabilidad verificada)");
            lblStatus.setForeground(new Color(34, 139, 34));
        } else {
            lblStatus.setText("ESTADO DE LA RED: CORRUPTA. Se detecto manipulacion ilegal de datos");
            lblStatus.setForeground(Color.RED);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}