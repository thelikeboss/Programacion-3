package Tarea1;

import java.util.*;

public class Ejemplo {

    public static void main(String[] args) {

        List<String> lista = new LinkedList<String>();

        lista.add("María");

        lista.add("Pedro");

        lista.add("Jose");

        lista.add("Marcos");

        Iterator<String> it = lista.iterator();

        while (it.hasNext()) {
            System.out.println(it.next());
        }

        lista.remove("Pedro"); // it debe descartarse

        System.out.println("Pedro borrado");

        ListIterator<String> it2 = lista.listIterator(lista.size());

        while (it2.hasPrevious()) { //esta vez vamos hacia atrás

            System.out.println(it2.previous());
        }

    }

}
