package Tarea1; // Declara que esta clase pertenece al paquete Tarea1

import java.util.*; // Importa las utilidades de colecciones: List, LinkedList, Iterator, ListIterator

public class Ejemplo { // Define la clase pública llamada Ejemplo

    public static void main(String[] args) { // Método principal; punto de entrada de la aplicación

        List<String> lista = new LinkedList<String>(); // Crea una LinkedList que almacena Strings

        lista.add("María"); // Añade el elemento "María" a la lista

        lista.add("Pedro"); // Añade el elemento "Pedro" a la lista

        lista.add("Jose"); // Añade el elemento "Jose" a la lista

        lista.add("Marcos"); // Añade el elemento "Marcos" a la lista

        Iterator<String> it = lista.iterator(); // Obtiene un iterador para recorrer la lista hacia adelante

        while (it.hasNext()) { // Mientras el iterador tenga un siguiente elemento
            System.out.println(it.next()); // Imprime el siguiente elemento y avanza el iterador
        }

        lista.remove("Pedro"); // Elimina el elemento "Pedro" de la lista

        System.out.println("Pedro borrado"); // Imprime un mensaje indicando que "Pedro" fue borrado

        ListIterator<String> it2 = lista.listIterator(lista.size()); // Crea un ListIterator posicionado al final de la lista

        while (it2.hasPrevious()) { // Mientras el ListIterator tenga un elemento anterior (recorrido inverso)

            System.out.println(it2.previous()); // Imprime el elemento anterior y mueve el iterador hacia atrás
        }

    } // Fin del método main

} // Fin de la clase Ejemplo
