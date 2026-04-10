package src;

public class Partida {
    public String id;
    public Perfil jugador1;
    public Perfil jugador2;
    public boolean activa;

    public Partida(String id, Perfil jugador1, Perfil jugador2) {
        this.id = id;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.activa = true;
    }

    @Override
    public String toString() {
        return "Partida " + id + ": " + jugador1.username + " vs " + jugador2.username + (activa ? " (Activa)" : " (Finalizada)");
    }
}
