package src;

public class TablaHashPerfil {
    private Perfil[][] tabla;
    private int size;
    private static final int MAX_COLISIONES = 2;

    public TablaHashPerfil(int size) {
        this.size = size;
        this.tabla = new Perfil[size][MAX_COLISIONES];
    }

    private int hash(String username) {
        return Math.abs(username.hashCode()) % size;
    }

    public boolean agregar(Perfil perfil) {
        int idx = hash(perfil.username);
        for (int i = 0; i < MAX_COLISIONES; i++) {
            if (tabla[idx][i] == null || !tabla[idx][i].online) {
                tabla[idx][i] = perfil;
                return true;
            }
        }
        return false; // bucket lleno
    }

    public Perfil buscar(String username) {
        int idx = hash(username);
        for (int i = 0; i < MAX_COLISIONES; i++) {
            if (tabla[idx][i] != null && tabla[idx][i].username.equals(username)) {
                return tabla[idx][i];
            }
        }
        return null;
    }

    public boolean eliminar(String username) {
        int idx = hash(username);
        for (int i = 0; i < MAX_COLISIONES; i++) {
            if (tabla[idx][i] != null && tabla[idx][i].username.equals(username)) {
                tabla[idx][i] = null;
                return true;
            }
        }
        return false;
    }

    public Perfil[] obtenerTodos() {
        java.util.List<Perfil> lista = new java.util.ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < MAX_COLISIONES; j++) {
                if (tabla[i][j] != null) lista.add(tabla[i][j]);
            }
        }
        return lista.toArray(new Perfil[0]);
    }
}
