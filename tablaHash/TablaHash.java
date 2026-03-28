package tablaHash;

public class TablaHash {

    // arreglo donde cada casilla tiene una lista
    private EmpleadoListaEnlazada[] empleadoListaEnlazadaArray;

    // tamaño total de la tabla
    private int size;

    // constructor
    public TablaHash(int size) {
        this.size = size;
        this.empleadoListaEnlazadaArray = new EmpleadoListaEnlazada[size];

        // aqui preparo cada espacio del arreglo
        for (int i = 0; i < size; i++) {
            empleadoListaEnlazadaArray[i] = new EmpleadoListaEnlazada();
        }
    }

    // eliminar un empleado usando el id
    public void deleteById(int id) {
        int posicion = hashFun(id);
        empleadoListaEnlazadaArray[posicion].deleteEmpById(id);
    }

    // buscar empleado por id
    public void findEmpById(int id) {
        int posicion = hashFun(id);

        Empleado empleado = empleadoListaEnlazadaArray[posicion].findEmpById(id);

        if (empleado != null) {
            System.out.println("Dato encontrado: " + empleado);
        } else {
            System.out.println("No existe empleado con id " + id);
        }
    }

    // enseña toda la tabla
    public void list() {
        for (int i = 0; i < size; i++) {
            empleadoListaEnlazadaArray[i].list(i);
        }
    }

    // agrega un empleado a su posicion correspondiente
    public void add(Empleado empleado) {
        int posicion = hashFun(empleado.id);
        empleadoListaEnlazadaArray[posicion].add(empleado);
    }

    // funcion hash sencilla con modulo
    private int hashFun(int id) {
        return id % size;
    }
}
