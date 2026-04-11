
// Clase que representa a un jugador del sistema de matchmaking

public class Jugador {
    // Nombre del jugador
    String nombre;
    // Email único del jugador (clave en la tabla de perfiles)
    String email;
    // Puntuación ELO (ranking)
    int elo;
    // Ping simulado (latencia de red)
    int ping = 20;
    // Estado actual del jugador (conectado, en partida, desconectado)
    Estado estado = Estado.DESCONECTADO;

    // Constructor: inicializa un jugador con nombre, email y elo
    public Jugador(String n, String e, int elo) {
        this.nombre = n;
        this.email = e;
        this.elo = elo;
    }
}
