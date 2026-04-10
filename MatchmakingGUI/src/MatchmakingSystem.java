package src;

import java.util.*;

public class MatchmakingSystem {
    private TablaHashPerfil tablaUsuarios;
    private TablaHashPartida tablaPartidas;
    private List<Perfil> enCola;
    private int partidaCounter = 1;

    public MatchmakingSystem(int tamUsuarios, int tamPartidas) {
        tablaUsuarios = new TablaHashPerfil(tamUsuarios);
        tablaPartidas = new TablaHashPartida(tamPartidas);
        enCola = new ArrayList<>();
    }

    // Crear usuario
    public boolean crearUsuario(Perfil perfil) {
        return tablaUsuarios.agregar(perfil);
    }

    // Iniciar sesión
    public boolean login(String username, String password) {
        Perfil p = tablaUsuarios.buscar(username);
        if (p != null && !p.online && p.password.equals(password)) {
            p.online = true;
            return true;
        }
        return false;
    }

    // Cerrar sesión
    public boolean logout(String username) {
        Perfil p = tablaUsuarios.buscar(username);
        if (p != null && p.online) {
            p.online = false;
            enCola.remove(p);
            return true;
        }
        return false;
    }

    // Poner usuario en cola para matchmaking
    public boolean ponerEnCola(String username) {
        Perfil p = tablaUsuarios.buscar(username);
        if (p != null && p.online && !enCola.contains(p)) {
            enCola.add(p);
            return true;
        }
        return false;
    }

    // Emparejar jugadores por ELO y MMR oculto
    public Partida buscarPartida(String username) {
        Perfil p = tablaUsuarios.buscar(username);
        if (p == null || !p.online || !enCola.contains(p)) return null;
        Perfil mejor = null;
        double mejorDiff = Double.MAX_VALUE;
        for (Perfil otro : enCola) {
            if (!otro.username.equals(username) && otro.online) {
                int diffElo = Math.abs(p.elo - otro.elo);
                if (diffElo <= 100) { // rango de ELO
                    double diffMMR = Math.abs(p.calcularMMR() - otro.calcularMMR());
                    if (diffMMR < mejorDiff) {
                        mejorDiff = diffMMR;
                        mejor = otro;
                    }
                }
            }
        }
        if (mejor != null) {
            enCola.remove(p);
            enCola.remove(mejor);
            String idPartida = "P" + (partidaCounter++);
            Partida partida = new Partida(idPartida, p, mejor);
            tablaPartidas.agregar(partida);
            return partida;
        }
        return null;
    }

    public Perfil[] usuariosOnline() {
        return Arrays.stream(tablaUsuarios.obtenerTodos()).filter(p -> p.online).toArray(Perfil[]::new);
    }

    public Partida[] partidasActivas() {
        return tablaPartidas.obtenerTodas();
    }
}
