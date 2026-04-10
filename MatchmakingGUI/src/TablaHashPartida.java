package src;

public class TablaHashPartida {
    private Partida[][] tabla;
    private int size;
    private static final int MAX_COLISIONES = 2;

    public TablaHashPartida(int size) {
        this.size = size;
        this.tabla = new Partida[size][MAX_COLISIONES];
    }

    private int hash(String id) {
        return Math.abs(id.hashCode()) % size;
    }

    public boolean agregar(Partida partida) {
        int idx = hash(partida.id);
        for (int i = 0; i < MAX_COLISIONES; i++) {
            if (tabla[idx][i] == null || !tabla[idx][i].activa) {
                tabla[idx][i] = partida;
                return true;
            }
        }
        return false; // bucket lleno
    }

    public Partida buscar(String id) {
        int idx = hash(id);
        for (int i = 0; i < MAX_COLISIONES; i++) {
            if (tabla[idx][i] != null && tabla[idx][i].id.equals(id)) {
                return tabla[idx][i];
            }
        }
        return null;
    }

    public boolean eliminar(String id) {
        int idx = hash(id);
        for (int i = 0; i < MAX_COLISIONES; i++) {
            if (tabla[idx][i] != null && tabla[idx][i].id.equals(id)) {
                tabla[idx][i] = null;
                return true;
            }
        }
        return false;
    }

    public Partida[] obtenerTodas() {
        java.util.List<Partida> lista = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < MAX_COLISIONES; j++) {
                if (tabla[i][j] != null) lista.add(tabla[i][j]);
            }
        }
        return lista.toArray(new Partida[0]);
    }
}
