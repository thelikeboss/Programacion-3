public class ArbolAVLUsuarios {

    // Clase que representa cada nodo del árbol
    static class NodoUsuario {
        int id;
        String nombre;
        int altura;
        NodoUsuario izquierdo, derecho;

        public NodoUsuario(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
            this.altura = 1; // Cuando se crea, el nodo es hoja
        }
    }

    private NodoUsuario raiz;

    // Método para obtener la altura de un nodo
    private int altura(NodoUsuario n) {
        if (n == null)
            return 0;
        return n.altura;
    }

    // Obtener el factor de equilibrio
    // balance = altura(izquierdo) - altura(derecho)
    private int obtenerBalance(NodoUsuario n) {
        if (n == null)
            return 0;
        return altura(n.izquierdo) - altura(n.derecho);
    }

    // Rotación simple a la derecha
    private NodoUsuario rotarDerecha(NodoUsuario y) {
        NodoUsuario x = y.izquierdo;
        NodoUsuario T2 = x.derecho;

        // Realizar rotación
        x.derecho = y;
        y.izquierdo = T2;

        // Actualizar alturas
        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;
        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;

        // x pasa a ser la nueva raíz del subárbol
        return x;
    }

    // Rotación simple a la izquierda
    private NodoUsuario rotarIzquierda(NodoUsuario x) {
        NodoUsuario y = x.derecho;
        NodoUsuario T2 = y.izquierdo;

        // Realizar rotación
        y.izquierdo = x;
        x.derecho = T2;

        // Actualizar alturas
        x.altura = Math.max(altura(x.izquierdo), altura(x.derecho)) + 1;
        y.altura = Math.max(altura(y.izquierdo), altura(y.derecho)) + 1;

        // y pasa a ser la nueva raíz del subárbol
        return y;
    }

    // Método público para insertar
    public void insertar(int id, String nombre) {
        raiz = insertar(raiz, id, nombre);
    }

    // Inserción recursiva con balanceo automático
    private NodoUsuario insertar(NodoUsuario nodo, int id, String nombre) {

        // 1. Inserción normal como en un árbol binario de búsqueda
        if (nodo == null)
            return new NodoUsuario(id, nombre);

        if (id < nodo.id)
            nodo.izquierdo = insertar(nodo.izquierdo, id, nombre);
        else if (id > nodo.id)
            nodo.derecho = insertar(nodo.derecho, id, nombre);
        else
            return nodo; // No se permiten IDs duplicados

        // 2. Actualizar altura del nodo actual
        nodo.altura = 1 + Math.max(altura(nodo.izquierdo), altura(nodo.derecho));

        // 3. Obtener factor de equilibrio
        int balance = obtenerBalance(nodo);

        // 4. Casos de desbalance

        // Caso Izquierda-Izquierda
        if (balance > 1 && id < nodo.izquierdo.id)
            return rotarDerecha(nodo);

        // Caso Derecha-Derecha
        if (balance < -1 && id > nodo.derecho.id)
            return rotarIzquierda(nodo);

        // Caso Izquierda-Derecha
        if (balance > 1 && id > nodo.izquierdo.id) {
            nodo.izquierdo = rotarIzquierda(nodo.izquierdo);
            return rotarDerecha(nodo);
        }

        // Caso Derecha-Izquierda
        if (balance < -1 && id < nodo.derecho.id) {
            nodo.derecho = rotarDerecha(nodo.derecho);
            return rotarIzquierda(nodo);
        }

        // Si no hubo desbalance, se devuelve el nodo sin cambios
        return nodo;
    }

    // Recorrido inorden (muestra ordenado por ID)
    public void inOrden() {
        inOrden(raiz);
    }

    private void inOrden(NodoUsuario nodo) {
        if (nodo != null) {
            inOrden(nodo.izquierdo);
            System.out.println("ID: " + nodo.id + " | Nombre: " + nodo.nombre);
            inOrden(nodo.derecho);
        }
    }

    // Método principal para probar el árbol
    public static void main(String[] args) {
        ArbolAVLUsuarios arbol = new ArbolAVLUsuarios();

        arbol.insertar(30, "Carlos");
        arbol.insertar(20, "Ana");
        arbol.insertar(40, "Luis");
        arbol.insertar(10, "Sofia");
        arbol.insertar(25, "Pedro");

        System.out.println("Recorrido Inorden del árbol AVL:");
        arbol.inOrden();
    }
}