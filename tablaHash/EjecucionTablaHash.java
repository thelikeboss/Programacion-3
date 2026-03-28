package tablaHash;

import java.util.Scanner;

public class EjecucionTablaHash {

    public static void main(String[] args) {

        // contador para asignar ids automaticos
        int id = 1;

        // creo la tabla con 7 espacios
        TablaHash tablaHash = new TablaHash(7);

        String key = "";
        Scanner input = new Scanner(System.in);

        while (true) {

            // menu principal
            System.out.println("add: agregar | del: eliminar | list: mostrar | find: buscar | exit: salir");
            key = input.next();

            switch (key) {

                case "add":
                    System.out.print("Ingrese nombre: ");
                    String name = input.next();

                    // armo el nuevo empleado
                    Empleado empleado = new Empleado(id++, name);

                    // se guarda en la tabla hash
                    tablaHash.add(empleado);
                    break;

                case "del":
                    System.out.println("Introducir clave:");
                    tablaHash.deleteById(input.nextInt());
                    break;

                case "list":
                    tablaHash.list();
                    break;

                case "find":
                    System.out.println("Introducir clave:");
                    tablaHash.findEmpById(input.nextInt());
                    break;

                case "exit":
                    input.close();
                    System.exit(0);
                    break;

                default:
                    // si mete otro comando mejor cierro
                    input.close();
                    System.exit(0);
                    break;
            }
        }
    }
}
