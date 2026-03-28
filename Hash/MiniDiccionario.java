import java.util.LinkedList;

// Clase que representa cada par clave-valor
class Entry {
    String key;
    String value;

    Entry(String key, String value) {
        this.key = key;
        this.value = value;
    }
}

// Clase principal del diccionario usando tabla hash
public class MiniDiccionario {

    private int SIZE = 10; // Tamaño de la tabla
    private LinkedList<Entry>[] table;

    // Constructor: inicializa la tabla
    public MiniDiccionario() {
        table = new LinkedList[SIZE];
        for (int i = 0; i < SIZE; i++) {
            table[i] = new LinkedList<>();
        }
    }

    // Función hash: convierte la clave en un índice
    private int hash(String key) {
        return Math.abs(key.hashCode() % SIZE);
    }

    // Insertar o actualizar un valor
    public void put(String key, String value) {
        int idx = hash(key);

        // Verifica si la clave ya existe
        for (Entry e : table[idx]) {
            if (e.key.equals(key)) {
                e.value = value; // Actualiza el valor
                return;
            }
        }

        // Si no existe, se agrega (manejo de colisión por lista)
        table[idx].add(new Entry(key, value));
    }

    // Buscar un valor por su clave
    public String get(String key) {
        int idx = hash(key);

        for (Entry e : table[idx]) {
            if (e.key.equals(key)) {
                return e.value;
            }
        }

        return "No encontrado";
    }

    // Eliminar una clave
    public void remove(String key) {
        int idx = hash(key);

        // Elimina si encuentra coincidencia
        table[idx].removeIf(e -> e.key.equals(key));
    }

    // Método principal para probar el funcionamiento
    public static void main(String[] args) {
        MiniDiccionario dic = new MiniDiccionario();

        dic.put("Apple", "Manzana");
        dic.put("Book", "Libro");
        dic.put("Cat", "Gato");

        System.out.println("Apple significa: " + dic.get("Apple")); // Manzana

        dic.remove("Apple");

        System.out.println("Tras borrar, Apple es: " + dic.get("Apple")); // No encontrado
    }
}