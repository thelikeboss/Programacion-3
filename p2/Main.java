package p2;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// ============================================================================
// 1. CLASE BLOQUE (Estructura de Datos del Ledger)
// ============================================================================
class Block {
    public int index;
    public long timestamp;
    public String data; // Guardara los datos ENCRIPTADOS para proteger la privacidad
    public String previousHash; // El enlace al bloque anterior (Garantiza el encadenamiento)
    public String hash;         // La huella digital unica de ESTE bloque (SHA-256)
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

    // EXPLICACIÓN DEL SHA-256:
    // Es una funcion criptografica unidireccional. Transforma cualquier volumen de datos 
    // en una huella digital fija de 64 caracteres. Si se altera una sola letra del historial, 
    // el Hash cambia por completo (Efecto Avalancha), rompiendo los eslabones de la Blockchain.
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

// ============================================================================
// 2. SIMULACIÓN DE SMART CONTRACT Y CIFRADO
// ============================================================================
class MedicalSmartContract {
    // Clave simetrica privada del paciente para otorgar permisos de lectura/escritura
    private static final String VALID_PATIENT_KEY = "VickySecretKey123";

    // EXPLICACIÓN SMART CONTRACT: Evalua de forma automatica y sin intermediarios 
    // si el solicitante posee la firma autorizada del paciente.
    public static boolean validateAccess(String patientPrivateKey) {
        return VALID_PATIENT_KEY.equals(patientPrivateKey);
    }

    public static String signAuthorization(String privateKey, String hospitalId, String data) {
        if (!validateAccess(privateKey)) {
            return null; // Si la llave no es valida, el Smart Contract aborta la operacion
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

    // EXPLICACIÓN CIFRADO SIMÉTRICO (XOR): Aplica una mascara logica bit a bit. 
    // Convierte la informacion medica confidencial en "ruido" o texto corrupto dentro de la red, 
    // garantizando que los datos esten seguros aun si los nodos de almacenamiento son vulnerados.
    public static String encryptData(String data, String key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            sb.append((char) (data.charAt(i) ^ key.charAt(i % key.length())));
        }
        return sb.toString();
    }

    public static String decryptData(String encryptedData, String key) {
        return encryptData(encryptedData, key); // La operacion XOR recupera el texto original al aplicarse de nuevo
    }
}

// ============================================================================
// 3. LA CADENA DE BLOQUES (Blockchain)
// ============================================================================
class Blockchain {
    public ArrayList<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
        // El bloque genesis (Bloque 0) inicializa la red con una clave del sistema
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
            return false; // El Smart Contract deniega el registro si la clave no es valida
        }

        String encryptedData = MedicalSmartContract.encryptData(data, patientPrivateKey);

        // Se amarra de forma obligatoria el nuevo bloque con el Hash del ultimo bloque actual
        Block newBlock = new Block(chain.size(), encryptedData, getLatestBlock().hash, hospitalId, signature);
        chain.add(newBlock);
        return true;
    }

    // EXPLICACIÓN AUDITORÍA DE INMUTABILIDAD: Recorre el libro contable de inicio a fin.
    // Compara matematicamente los hashes grabados contra los calculados en tiempo real. 
    // Si un solo bit cambio en el pasado, la funcion devuelve 'false'.
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

// ============================================================================
// 4. INTERFAZ GRÁFICA PRINCIPAL (Main.java)
// ============================================================================
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
        updateLedgerView(""); // Inicia mostrando los bloques cifrados en el Ledger general
    }

    private void initUI() {
        setTitle("Blockchain - Historial Clinico Compartido (Victoria)");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Estado de Integridad
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(new Color(240, 244, 248));
        lblStatus = new JLabel("ESTADO DE LA RED: VALIDANDO...", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblStatus.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelTop.add(lblStatus, BorderLayout.CENTER);
        add(panelTop, BorderLayout.NORTH);

        // Panel Central: Consola del Ledger Publico
        txtLedger = new JTextArea();
        txtLedger.setEditable(false);
        txtLedger.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLedger.setBackground(new Color(30, 30, 30));
        txtLedger.setForeground(new Color(220, 220, 220));
        JScrollPane scrollPane = new JScrollPane(txtLedger);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Bloques de la Cadena (Datos Cifrados si no hay autorizacion)"));
        add(scrollPane, BorderLayout.CENTER);

        // Panel Inferior: Controles de la Aplicacion
        JPanel panelBottom = new JPanel(new GridLayout(1, 2, 10, 10));
        panelBottom.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel panelForm = new JPanel(new GridLayout(4, 2, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Operaciones del Historial Clinico"));
        
        panelForm.add(new JLabel("ID Hospital / Clinica Solicitante:"));
        txtHospital = new JTextField("Hospital General");
        panelForm.add(txtHospital);

        panelForm.add(new JLabel("Datos Medicos (Escribir):"));
        txtData = new JTextField("Paciente presenta optimo estado fisico.");
        panelForm.add(txtData);

        panelForm.add(new JLabel("Llave Privada Paciente (Autorizar Escritura):"));
        txtPrivateKey = new JPasswordField("VickySecretKey123");
        panelForm.add(txtPrivateKey);

        JButton btnAdd = new JButton("Autorizar Escritura (Anadir Bloque)");
        panelForm.add(btnAdd);
        panelBottom.add(panelForm);

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

        // --- Gestion de Eventos (Controladores de Botones) ---
        
        // ACCIÓN: AÑADIR BLOQUE
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
                updateLedgerView(""); // Se mantiene cifrado en el panel general tras agregar
                JOptionPane.showMessageDialog(this, "Bloque añadido exitosamente. Los datos se guardaron encriptados en la Blockchain.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "[Smart Contract] Transaccion Rechazada:\nLlave de autorizacion incorrecta. El bloque no se creo.", 
                    "Error de Permisos", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // ACCIÓN: LEER HISTORIAL (SOLICITUD DE ACCESO AL SMART CONTRACT)
        btnRead.addActionListener(e -> {
            String inputKey = JOptionPane.showInputDialog(this, 
                    "El historial clinico requiere consentimiento.\nIngrese la Llave Privada del Paciente:", 
                    "Solicitud de Autorización de Lectura", 
                    JOptionPane.QUESTION_MESSAGE);
            
            if (inputKey == null) {
                return; 
            }
            
            if (MedicalSmartContract.validateAccess(inputKey)) {
                StringBuilder reporte = new StringBuilder();
                reporte.append("==================================================\n");
                reporte.append("       REPORTE CLÍNICO COMPARTIDO EN BLOCKCHAIN   \n");
                reporte.append("==================================================\n");
                reporte.append(" PACIENTE IDENTIFICADO: Victoria\n");
                reporte.append(" ACCESO: Autorizado por Smart Contract\n");
                reporte.append("==================================================\n\n");
                
                boolean tieneRegistros = false;
                for (Block b : medicalRecords.chain) {
                    if (b.index > 0) { 
                        String datosDescifrados = MedicalSmartContract.decryptData(b.data, inputKey);
                        reporte.append(" -> REGISTRO #").append(b.index).append("\n");
                        reporte.append("    Hospital Emisor: ").append(b.hospitalId).append("\n");
                        reporte.append("    Diagnostico:     ").append(datosDescifrados).append("\n");
                        reporte.append("    Firma Digital:   ").append(b.patientSignature).append("\n");
                        reporte.append("--------------------------------------------------\n");
                        tieneRegistros = true;
                    }
                }
                
                if (!tieneRegistros) {
                    reporte.append(" El paciente no cuenta con registros medicos en la cadena aun.\n");
                }
                
                reporte.append("\n==================================================");
                
                JTextArea msgArea = new JTextArea(reporte.toString());
                msgArea.setEditable(false);
                msgArea.setFont(new Font("Consolas", Font.PLAIN, 12));
                JScrollPane scroll = new JScrollPane(msgArea);
                scroll.setPreferredSize(new Dimension(500, 350));
                
                JOptionPane.showMessageDialog(this, scroll, "Historial Clínico Autorizado - Victoria", JOptionPane.INFORMATION_MESSAGE);
                updateLedgerView(inputKey); 
                
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Acceso Denegado: La llave provista no corresponde al dueño del historial.\nLos datos permanecen cifrados de forma inmutable.", 
                        "Fallo Crítico de Permisos", 
                        JOptionPane.ERROR_MESSAGE);
                updateLedgerView("CLAVE_ERRONEA"); 
            }
        });

        // ============================================================================
        // EXPLICACIÓN DE LA DESACTIVACIÓN DEL BOTÓN DE ATAQUE:
        // Se ha desvinculado por completo cualquier interaccion de este boton con el motor 
        // de la Blockchain o el sistema de alertas del UI. No ejecuta modificaciones de bits,
        // no lanza mensajes en pantalla ni genera advertencias. Al presionarlo no pasa nada.
        // ============================================================================
        btnHack.addActionListener(e -> {
            // El boton se deja completamente vacio intencionalmente para no realizar ninguna accion
        });

        // ACCIÓN: REINICIAR
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