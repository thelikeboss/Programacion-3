package src;

import java.awt.*;

import javax.swing.*;

public class MatchmakingGUI extends JFrame {
    private final MatchmakingController controller;
    private final JPanel mainPanel;
    // Campos de la GUI
    private JTextArea usuariosArea;
    private JTextArea partidasArea;
    private JTextField loginUserField;
    private JPasswordField loginPassField;
    // Para registro
    private JTextField userField;
    private JPasswordField passField;
    // Para matchmaking y logout
    private JTextField colaUserField;
    private JTextField buscarUserField;
    private JTextField logoutUserField;

    public MatchmakingGUI() {
        setTitle("");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        controller = new MatchmakingController(new MatchmakingSystem(13, 13));

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(crearPantallaLogin(), BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel crearPantallaLogin() {
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(Color.WHITE);

        // Panel izquierdo (formulario)
        JPanel panelIzq = new JPanel();
        panelIzq.setPreferredSize(new Dimension(380, 600));
        panelIzq.setBackground(Color.WHITE);
        panelIzq.setLayout(new BoxLayout(panelIzq, BoxLayout.Y_AXIS));
        panelIzq.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Tabs de login (solo uno visible)
        JPanel tabsPanel = new JPanel();
        tabsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabsPanel.setOpaque(false);
        JButton signInTab = new JButton("Sign-in");
        signInTab.setEnabled(false);
        signInTab.setBackground(new Color(230,230,230));
        signInTab.setFont(new Font("Arial", Font.BOLD, 15));
        signInTab.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        tabsPanel.add(signInTab);
        panelIzq.add(tabsPanel);
        panelIzq.add(Box.createRigidArea(new Dimension(0, 30)));

        // Usuario
        loginUserField = new JTextField();
        loginUserField.setMaximumSize(new Dimension(300, 40));
        loginUserField.setFont(new Font("Arial", Font.PLAIN, 18));
        loginUserField.setBackground(Color.WHITE);
        loginUserField.setForeground(Color.BLACK);
        loginUserField.setCaretColor(Color.BLACK);
        loginUserField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180,180,180)), "USERNAME", 0, 0, new Font("Arial", Font.PLAIN, 14), Color.GRAY));
        panelIzq.add(loginUserField);
        panelIzq.add(Box.createRigidArea(new Dimension(0, 15)));

        // Clave
        loginPassField = new JPasswordField();
        loginPassField.setMaximumSize(new Dimension(300, 40));
        loginPassField.setFont(new Font("Arial", Font.PLAIN, 18));
        loginPassField.setBackground(Color.WHITE);
        loginPassField.setForeground(Color.BLACK);
        loginPassField.setCaretColor(Color.BLACK);
        loginPassField.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(180,180,180)), "PASSWORD", 0, 0, new Font("Arial", Font.PLAIN, 14), Color.GRAY));
        panelIzq.add(loginPassField);
        panelIzq.add(Box.createRigidArea(new Dimension(0, 20)));


        // Botón de iniciar sesión
        JButton loginBtn = new JButton("Iniciar sesión");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 18));
        loginBtn.setBackground(new Color(220, 20, 60));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setMaximumSize(new Dimension(300, 40));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(loginBtn);
        panelIzq.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botón de registro
        JButton registerBtn = new JButton("¿No tienes usuario? Regístrate");
        registerBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setForeground(new Color(60,60,60));
        registerBtn.setBorderPainted(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panelIzq.add(registerBtn);
        panelIzq.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel status = new JLabel("");
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        status.setForeground(Color.GRAY);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelIzq.add(status);


        loginBtn.addActionListener(e -> {
            boolean ok = controller.login(loginUserField.getText(), new String(((JPasswordField)loginPassField).getPassword()));
            if (ok) {
                mostrarMenuPrincipal();
            } else {
                status.setText("No existe, clave incorrecta o ya está online");
            }
        });

        registerBtn.addActionListener(e -> {
            mostrarRegistroSolo();
        });

        fondo.add(panelIzq, BorderLayout.WEST);
        // Panel derecho vacío (blanco)
        JPanel panelDer = new JPanel();
        panelDer.setBackground(Color.WHITE);
        fondo.add(panelDer, BorderLayout.CENTER);

        return fondo;
    }

    // Mostrar solo el panel de registro
    private void mostrarRegistroSolo() {
        mainPanel.removeAll();
        JPanel panelRegistro = crearPanelCrearUsuario();
        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBorder(BorderFactory.createEmptyBorder(80, 200, 80, 200));
        JLabel titulo = new JLabel("Registro de usuario");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.add(titulo);
        wrapper.add(Box.createRigidArea(new Dimension(0, 30)));
        wrapper.add(panelRegistro);
        JButton volverBtn = new JButton("Volver al inicio de sesión");
        volverBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        volverBtn.setBackground(Color.WHITE);
        volverBtn.setForeground(new Color(60,60,60));
        volverBtn.setBorderPainted(false);
        volverBtn.setFocusPainted(false);
        volverBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        volverBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        wrapper.add(Box.createRigidArea(new Dimension(0, 20)));
        wrapper.add(volverBtn);
        volverBtn.addActionListener(e -> {
            mainPanel.removeAll();
            mainPanel.add(crearPantallaLogin(), BorderLayout.CENTER);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
        mainPanel.add(wrapper, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void mostrarMenuPrincipal() {
        mainPanel.removeAll();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Crear Usuario", crearPanelCrearUsuario());
        tabs.addTab("Logout", crearPanelLogout());
        tabs.addTab("Cola/Matchmaking", crearPanelColaMatchmaking());
        tabs.addTab("Estado", crearPanelEstado());
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel crearPanelCrearUsuario() {
        JPanel panel = new JPanel(new GridLayout(3,2));
        userField = new JTextField();
        passField = new JPasswordField();
        JButton crearBtn = new JButton("Crear Usuario");
        JLabel status = new JLabel("");

        panel.add(new JLabel("Usuario:")); panel.add(userField);
        panel.add(new JLabel("Clave:")); panel.add(passField);
        panel.add(crearBtn); panel.add(status);

        crearBtn.addActionListener(e -> {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                status.setText("Usuario y clave requeridos");
                return;
            }
            boolean ok = controller.crearUsuario(u, p);
            status.setText(ok ? "Usuario creado" : "Error: bucket lleno o usuario existe");
        });
        return panel;
    }



    private JPanel crearPanelLogout() {
        JPanel panel = new JPanel(new GridLayout(2,2));
        logoutUserField = new JTextField();
        JButton logoutBtn = new JButton("Cerrar Sesión");
        JLabel status = new JLabel("");
        panel.add(new JLabel("Usuario:")); panel.add(logoutUserField);
        panel.add(logoutBtn); panel.add(status);
        logoutBtn.addActionListener(e -> {
            boolean ok = controller.logout(logoutUserField.getText());
            status.setText(ok ? "Sesión cerrada" : "No existe o ya está offline");
        });
        return panel;
    }

    private JPanel crearPanelColaMatchmaking() {
        JPanel panel = new JPanel(new GridLayout(3,2));
        colaUserField = new JTextField();
        JButton colaBtn = new JButton("Entrar en Cola");
        buscarUserField = new JTextField();
        JButton buscarBtn = new JButton("Buscar Partida");
        JLabel status = new JLabel("");

        panel.add(new JLabel("Usuario para cola:")); panel.add(colaUserField);
        panel.add(colaBtn); panel.add(new JLabel(""));
        panel.add(new JLabel("Usuario para buscar partida:")); panel.add(buscarUserField);
        panel.add(buscarBtn); panel.add(status);

        colaBtn.addActionListener(e -> {
            boolean ok = controller.ponerEnCola(colaUserField.getText());
            status.setText(ok ? "En cola" : "No existe, offline o ya en cola");
        });
        buscarBtn.addActionListener(e -> {
            var partida = controller.buscarPartida(buscarUserField.getText());
            status.setText(partida != null ? "Emparejado: " + partida : "No hay rival disponible");
        });
        return panel;
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new BorderLayout());
        usuariosArea = new JTextArea(10, 30);
        partidasArea = new JTextArea(10, 30);
        usuariosArea.setEditable(false);
        partidasArea.setEditable(false);
        JButton refrescarBtn = new JButton("Refrescar");
        refrescarBtn.addActionListener(e -> refrescarEstado());
        JPanel top = new JPanel();
        top.add(refrescarBtn);
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(usuariosArea), BorderLayout.WEST);
        panel.add(new JScrollPane(partidasArea), BorderLayout.EAST);
        refrescarEstado();
        return panel;
    }

    private void refrescarEstado() {
        StringBuilder sb = new StringBuilder("Usuarios Online:\n");
        for (Perfil p : controller.usuariosOnline()) {
            sb.append(p).append("\n");
        }
        usuariosArea.setText(sb.toString());

        StringBuilder sp = new StringBuilder("Partidas Activas:\n");
        for (Partida partida : controller.partidasActivas()) {
            sp.append(partida).append("\n");
        }
        partidasArea.setText(sp.toString());
    }
}
