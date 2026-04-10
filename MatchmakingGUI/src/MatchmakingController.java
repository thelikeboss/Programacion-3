package src;

public class MatchmakingController {
    private MatchmakingSystem sistema;

    public MatchmakingController(MatchmakingSystem sistema) {
        this.sistema = sistema;
    }

    public boolean crearUsuario(String username, String password) {
        // Generar datos aleatorios
        int elo = 800 + (int)(Math.random() * 1200); // 800-2000
        double headshot = Math.round((30 + Math.random() * 70) * 10.0) / 10.0; // 30-100%
        double precision = Math.round((20 + Math.random() * 80) * 10.0) / 10.0; // 20-100%
        int kills = 10 + (int)(Math.random() * 90); // 10-100
        double kd = Math.round((0.5 + Math.random() * 2.5) * 100.0) / 100.0; // 0.5-3.0
        Perfil p = new Perfil(username, password, elo, headshot, precision, kills, kd);
        return sistema.crearUsuario(p);
    }

    public boolean login(String username, String password) {
        return sistema.login(username, password);
    }

    public boolean logout(String username) {
        return sistema.logout(username);
    }

    public boolean ponerEnCola(String username) {
        return sistema.ponerEnCola(username);
    }

    public Partida buscarPartida(String username) {
        return sistema.buscarPartida(username);
    }

    public Perfil[] usuariosOnline() {
        return sistema.usuariosOnline();
    }

    public Partida[] partidasActivas() {
        return sistema.partidasActivas();
    }
}
