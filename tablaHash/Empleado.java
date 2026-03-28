package tablaHash;

public class Empleado {

    // este numero lo estoy usando como llave principal
    public int id;

    // nombre del empleado
    public String name;

    // referencia al siguiente nodo de la lista
    public Empleado next;

    // constructor normal
    public Empleado(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        // esto solo es para que cuando se imprima se vea mas claro
        return "Empleado{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
