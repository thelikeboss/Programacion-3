
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
// Clases del mismo paquete, no requieren import explícito

/**
 * SIMULADOR DE SERVIDOR DE MATCHMAKING Propósito: Gestionar perfiles y sesiones
 * de juego usando Tablas Hash (HashMap).
 */
public class MatchmakingApp extends JFrame {

    // --- SECCIÓN: ALMACENAMIENTO (TABLAS HASH) ---
    // Tabla A: Perfiles de jugadores (Clave: Email, Valor: Objeto Jugador)
    private HashMap<String, Jugador> tablaPerfiles = new HashMap<>();
    // Tabla B: Sesiones activas (Clave: ID Partida, Valor: Objeto Partida)
    private HashMap<String, Partida> tablaSesiones = new HashMap<>();

    private final String ARCHIVO_DATOS = "ranking.txt";
    private String miEmailActual = "";

    // Colores Estilo Valorant
    private final Color COLOR_FONDO = Color.decode("#0f1923"), COLOR_PRIMARIO = Color.decode("#ff4655");
    private final Color COLOR_SECUNDARIO = Color.decode("#1f2933");
    private final Color COLOR_CONECTADO = Color.decode("#00ff85"), COLOR_ESPERA = Color.decode("#ffdb22");

    private DefaultListModel<Jugador> modeloListaSocial;
    private JPanel panelEquipo;
    private CardLayout cl = new CardLayout();
    private JPanel cards = new JPanel(cl);

    // ...el enum Estado ahora está en Estado.java...
    public MatchmakingApp() {
        setTitle("VALORANT PROTOCOL - SERVIDOR ACTIVO");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cargar datos previos de ranking.txt si existe
        cargarDatosDesdeArchivo();

        // 2. Configurar pantallas
        cards.add(crearPantallaLogin(), "LOGIN");
        cards.add(crearPantallaPrincipal(), "MAIN");
        add(cards);

        // --- SECCIÓN: EL LATIDO DEL SERVIDOR (TIMER) ---
        // Simula la actividad constante del servidor: actualiza pings y refresca la UI
        new javax.swing.Timer(1000, e -> {
            tablaPerfiles.values().forEach(j -> j.ping = 2 + (int) (Math.random() * 68));
            actualizarListaSocial();
            if (panelEquipo != null) {
                panelEquipo.repaint();
            }
        }).start();

        // Guardar automáticamente al cerrar el programa
        Runtime.getRuntime().addShutdownHook(new Thread(this::guardarDatosEnArchivo));
    }

    // Cargar los datos de los jugadores desde el archivo ranking.txt
    private void cargarDatosDesdeArchivo() {
        File archivo = new File(ARCHIVO_DATOS);
        if (!archivo.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 3) {
                    String email = partes[0];
                    String nombre = partes[1];
                    int elo;
                    try {
                        elo = Integer.parseInt(partes[2]);
                    } catch (NumberFormatException e) {
                        elo = 1200;
                    }
                    Jugador j = new Jugador(nombre, email, elo);
                    j.estado = Estado.DESCONECTADO;
                    tablaPerfiles.put(email, j);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer " + ARCHIVO_DATOS + ": " + e.getMessage());
        }
    }

    // --- SECCIÓN: LÓGICA DE RANGOS (Cálculo de ELO) ---
    public static String obtenerNombreRango(int elo) {
        if (elo < 300) {
            return "HIERRO";
        }
        if (elo < 600) {
            return "BRONCE";
        }
        if (elo < 900) {
            return "PLATA";
        }
        if (elo < 1200) {
            return "ORO";
        }
        if (elo < 1500) {
            return "PLATINO";
        }
        if (elo < 1800) {
            return "DIAMANTE";
        }
        if (elo < 2100) {
            return "ASCENDENTE";
        }
        if (elo < 2400) {
            return "INMORTAL";
        }
        return "RADIANTE";
    }

    public static Color obtenerColorRango(int elo) {
        if (elo < 300) {
            return Color.GRAY;
        }
        if (elo < 1200) {
            return Color.YELLOW;
        }
        if (elo < 1800) {
            return Color.CYAN;
        }
        if (elo < 2400) {
            return Color.MAGENTA;
        }
        return Color.ORANGE;
    }

    // --- SECCIÓN: MATCHMAKING (PROCESAMIENTO) ---
    private void realizarMatchmaking() {
        // Filtrar jugadores disponibles de la Tabla A
        List<Jugador> disponibles = tablaPerfiles.values().stream()
                .filter(j -> j.estado == Estado.CONECTADO)
                .sorted((j1, j2) -> Integer.compare(j2.elo, j1.elo))
                .limit(5).collect(Collectors.toList());

        if (disponibles.size() < 5) {
            JOptionPane.showMessageDialog(this, "Buscando agentes... Faltan " + (5 - disponibles.size()));
            return;
        }

        // Crear nueva sesión en la Tabla B
        String matchId = "M-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        tablaSesiones.put(matchId, new Partida(matchId, disponibles));

        // Cambiar estados para que no aparezcan en otras búsquedas
        disponibles.forEach(j -> j.estado = Estado.EN_PARTIDA);
        actualizarPanelEquipo(disponibles);
    }

    private void finalizarPartida() {
        if (tablaSesiones.isEmpty()) {
            return;
        }

        // Extraer la partida de la Tabla B (Simula que el juego terminó)
        String key = tablaSesiones.keySet().iterator().next();
        Partida p = tablaSesiones.remove(key);

        // Algoritmo de recompensa: MVP obtiene más ELO
        Jugador mvp = p.jugadores.get((int) (Math.random() * p.jugadores.size()));
        p.jugadores.forEach(j -> {
            j.estado = Estado.CONECTADO;
            j.elo += (j.equals(mvp)) ? 30 : 15;
        });

        JOptionPane.showMessageDialog(this, "PARTIDA FINALIZADA\nMVP: " + mvp.nombre);
        actualizarPanelEquipo(new ArrayList<>());
    }

    // --- SECCIÓN: INTERFAZ GRÁFICA (LOGIN CON LOGO) ---
    private JPanel crearPantallaLogin() {
        JPanel login = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // DIBUJAR EL CRÁNEO DE LA IMAGEN
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.BLACK);
                int cx = getWidth() / 2 - 50;
                int cy = 50;
                // Cabeza
                g2.fillOval(cx, cy, 100, 100);
                // Mandíbula/Dientes
                g2.fillRect(cx + 20, cy + 80, 60, 40);
                g2.setColor(Color.WHITE);
                // Ojos (Huecos)
                g2.fillOval(cx + 15, cy + 40, 30, 20);
                g2.fillOval(cx + 55, cy + 40, 30, 20);
            }
        };
        login.setBackground(Color.WHITE);

        JTextField txt = new JTextField(15);
        txt.setBorder(BorderFactory.createTitledBorder("AGENT_EMAIL"));
        JButton btn = new JButton("LOGIN");

        btn.addActionListener(e -> {
            if (!txt.getText().isEmpty()) {
                // Recargar datos más recientes antes de login
                cargarDatosDesdeArchivo();
                miEmailActual = txt.getText();
                // Si no existe, se añade a la Tabla Hash de Perfiles
                tablaPerfiles.computeIfAbsent(miEmailActual, k -> new Jugador("Tú", k, 1200)).estado = Estado.CONECTADO;
                guardarDatosEnArchivo(); // Guardar el nuevo estado inmediatamente
                cl.show(cards, "MAIN");
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        login.add(Box.createVerticalStrut(200), gbc); // Espacio para el dibujo
        gbc.gridy = 2;
        login.add(txt, gbc);
        gbc.gridy = 3;
        login.add(btn, gbc);
        return login;
    }

    private JPanel crearPantallaPrincipal() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(COLOR_FONDO);

        // Botones de acción
        JButton btnReg = crearBoton("REGISTRAR AGENTE", COLOR_PRIMARIO);
        JButton btnMatch = crearBoton("BUSCAR PARTIDA", COLOR_PRIMARIO);
        JButton btnEstado = crearBoton("MI ESTADO", Color.DARK_GRAY);
        JButton btnFinalizar = crearBoton("FIN JUEGO", Color.BLACK);
        JButton btnCerrarSesion = crearBoton("CERRAR SESIÓN", Color.RED);

        JPanel header = new JPanel(new FlowLayout(0, 15, 20));
        header.setBackground(COLOR_FONDO);
        header.add(btnReg);
        header.add(btnMatch);
        header.add(btnEstado);
        header.add(btnFinalizar);
        header.add(btnCerrarSesion);
        main.add(header, BorderLayout.NORTH);

        panelEquipo = new JPanel(new GridLayout(1, 5, 15, 0));
        panelEquipo.setBackground(COLOR_FONDO);
        panelEquipo.setBorder(new EmptyBorder(50, 40, 50, 40));
        main.add(panelEquipo, BorderLayout.CENTER);

        // --- SECCIÓN: LISTA SOCIAL (VISTA DE LA TABLA HASH A) ---
        modeloListaSocial = new DefaultListModel<>();
        JList<Jugador> lista = new JList<>(modeloListaSocial);
        lista.setBackground(COLOR_SECUNDARIO);
        lista.setCellRenderer(new JugadorRenderer());

        JPanel side = new JPanel(new BorderLayout());
        side.setPreferredSize(new Dimension(280, 0));
        side.add(new JScrollPane(lista), BorderLayout.CENTER);
        main.add(side, BorderLayout.EAST);

        btnReg.addActionListener(e -> registrarJugador());
        btnMatch.addActionListener(e -> realizarMatchmaking());
        btnEstado.addActionListener(e -> {
            Jugador yo = tablaPerfiles.get(miEmailActual);
            if (yo != null) {
                yo.estado = (yo.estado == Estado.CONECTADO) ? Estado.EN_PARTIDA : Estado.CONECTADO;
            }
        });
        btnFinalizar.addActionListener(e -> finalizarPartida());
        btnCerrarSesion.addActionListener(e -> {
            // Cambia el estado del usuario actual a DESCONECTADO
            Jugador yo = tablaPerfiles.get(miEmailActual);
            if (yo != null) {
                yo.estado = Estado.DESCONECTADO;
            }
            miEmailActual = "";
            cl.show(cards, "LOGIN");
        });

        return main;
    }

    private void guardarDatosEnArchivo() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_DATOS))) {
            for (Jugador j : tablaPerfiles.values()) {
                writer.println(j.email + "," + j.nombre + "," + j.elo);
            }
        } catch (Exception e) {
        }
    }

    // ...clases Jugador y Partida ahora están en archivos separados...
    private void registrarJugador() {
        String n = JOptionPane.showInputDialog("Nombre:");
        String m = JOptionPane.showInputDialog("Email:");
        if (n != null && m != null) {
            Jugador nuevo = new Jugador(n, m, (int) (Math.random() * 2500));
            nuevo.estado = Estado.CONECTADO;
            tablaPerfiles.put(nuevo.email, nuevo); // Insertar en Tabla Hash A
        }
    }

    private void actualizarListaSocial() {
        modeloListaSocial.clear();
        tablaPerfiles.values().forEach(modeloListaSocial::addElement);
    }

    private void actualizarPanelEquipo(List<Jugador> equipo) {
        panelEquipo.removeAll();
        for (int i = 0; i < 5; i++) {
            Jugador j = (i < equipo.size()) ? equipo.get(i) : null;
            TarjetaAgente card = new TarjetaAgente(j);
            if (j != null) {
                card.setLayout(new GridLayout(3, 1));
                card.add(new JLabel(j.nombre, 0));
                card.add(new JLabel(obtenerNombreRango(j.elo), 0));
                card.add(new JLabel(j.ping + " ms", 0));
            }
            panelEquipo.add(card);
        }
        panelEquipo.revalidate();
    }

    private JButton crearBoton(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 10));
        return b;
    }

    class TarjetaAgente extends JPanel {

        Jugador j;

        public TarjetaAgente(Jugador j) {
            this.j = j;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(j == null ? new Color(255, 255, 255, 10) : COLOR_SECUNDARIO);
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (j != null) {
                g2.setColor(obtenerColorRango(j.elo));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        }
    }

    private class JugadorRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object val, int idx, boolean sel, boolean foc) {
            JLabel l = (JLabel) super.getListCellRendererComponent(list, val, idx, sel, foc);
            Jugador j = (Jugador) val;
            l.setText(j.nombre + " // " + j.ping + "ms // " + obtenerNombreRango(j.elo));
            l.setForeground(j.estado == Estado.CONECTADO ? COLOR_CONECTADO : (j.estado == Estado.EN_PARTIDA ? COLOR_ESPERA : Color.GRAY));
            return l;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatchmakingApp().setVisible(true));
    }
}
