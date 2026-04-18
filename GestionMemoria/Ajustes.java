package GestionMemoria;

import java.util.List;

public class Ajustes {

    public static int ultimoBloqueAsignado = 0;

    public static int primerAjuste(List<int[]> listaControl, int tamanyo) {

        for (int i = 0; i < listaControl.size(); i++) {
            int[] bloque = listaControl.get(i);

            if (bloque[0] == 0 && bloque[2] >= tamanyo) {
                return i;
            }
        }

        return -1;
    }

    public static int siguienteAjuste(List<int[]> listaControl, int tamanyo) {

        int res = ultimoBloqueAsignado;
        boolean vuelta = false;

        while (true) {

            int[] bloque = listaControl.get(res);

            if (bloque[0] == 0 && bloque[2] >= tamanyo) {
                ultimoBloqueAsignado = res;
                return res;
            }

            res++;

            if (res >= listaControl.size()) {
                if (vuelta) return -1;
                res = 0;
                vuelta = true;
            }
        }
    }

    public static int mejorAjuste(List<int[]> listaControl, int tamanyo) {

        int mejor = -1;
        int tamMejor = Integer.MAX_VALUE;

        for (int i = 0; i < listaControl.size(); i++) {
            int[] bloque = listaControl.get(i);

            if (bloque[0] == 0 && bloque[2] >= tamanyo && bloque[2] < tamMejor) {
                mejor = i;
                tamMejor = bloque[2];
            }
        }

        return mejor;
    }

    public static int peorAjuste(List<int[]> listaControl, int tamanyo) {

        int peor = -1;
        int tamPeor = -1;

        for (int i = 0; i < listaControl.size(); i++) {
            int[] bloque = listaControl.get(i);

            if (bloque[0] == 0 && bloque[2] >= tamanyo && bloque[2] > tamPeor) {
                peor = i;
                tamPeor = bloque[2];
            }
        }

        return peor;
    }
}
