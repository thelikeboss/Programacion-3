package tablaHash;

public class EmpleadoListaEnlazada {

    // primer nodo de la lista
    private Empleado head;

    // borra un empleado segun el id
    public void deleteEmpById(int id) {

        // si no hay datos, no hago nada
        if (head == null) {
            return;
        }

        Empleado temp = head;

        while (true) {

            // por si el que toca borrar es el primero
            if (temp.id == id) {
                head = temp.next;
                break;
            }

            // si ya no hay siguiente ya no se encontro
            if (temp.next == null) {
                break;
            }

            // aqui reviso el siguiente para saltarmelo
            if (temp.next.id == id) {
                temp.next = temp.next.next;
                break;
            }

            // sigo caminando en la lista
            temp = temp.next;
        }
    }

    // busca un empleado y lo devuelve
    public Empleado findEmpById(int id) {

        // lista vacia
        if (head == null) {
            return null;
        }

        Empleado temp = head;

        while (true) {

            // si coincide el id ya estuvo
            if (temp.id == id) {
                break;
            }

            // llegue al final y nada
            if (temp.next == null) {
                temp = null;
                break;
            }

            temp = temp.next;
        }

        return temp;
    }

    // muestra lo que hay dentro de una seccion
    public void list(int no) {

        if (head == null) {
            System.out.println("Sección " + (no + 1) + " vacía");
            return;
        }

        System.out.print("Sección " + (no + 1) + ": ");

        Empleado temp = head;

        while (true) {
            System.out.print(temp + " -> ");

            if (temp.next == null) {
                break;
            }

            temp = temp.next;
        }

        System.out.println();
    }

    // mete un empleado al final
    public void add(Empleado emp) {

        // si aun no hay cabeza, este entra de primero
        if (head == null) {
            head = emp;
            return;
        }

        Empleado temp = head;

        // busco el ultimo
        while (temp.next != null) {
            temp = temp.next;
        }

        // lo enlazo al final
        temp.next = emp;
    }
}
