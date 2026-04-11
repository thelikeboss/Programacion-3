
// Clase que representa una partida o sesión de juego
import java.util.List;

public class Partida {
    // Identificador único de la partida
    String id;
    // Lista de jugadores que participan en la partida
    List<Jugador> jugadores;

    // Constructor: inicializa la partida con un id y los jugadores
    public Partida(String id, List<Jugador> j) {
        this.id = id;
        this.jugadores = j;
    }
}
