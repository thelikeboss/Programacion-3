package GestionMemoria;
import java.util.LinkedList;
import java.util.List;

public class GestionMemoria {

    public static final int LONGITUD_MEMORIA = 32;

    public static List<int[]> listaControl;
    public static int[] memoria;
    public static int ultimoPid;

    private static void inicializaMemoria() {

        listaControl = new LinkedList<>();
        memoria = new int[LONGITUD_MEMORIA];
        ultimoPid = 0;

        // Primer hueco (memoria libre completa)
        int[] hueco = {0, 0, LONGITUD_MEMORIA, 0};
        listaControl.add(hueco);
    }

    private static void escribeMemoria(int direccion, int tamanyo, int dato) {
        for (int i = 0; i < tamanyo; i++) {
            memoria[direccion + i] = dato;
        }
    }

    public static boolean creaProceso(int pid, int tamanyo) {

        int hueco = Ajustes.siguienteAjuste(listaControl, tamanyo);

        if (hueco == -1) return false;

        int direcc = listaControl.get(hueco)[1];

        int[] proceso = {1, direcc, tamanyo, pid};

        int espacioRestante = listaControl.get(hueco)[2] - tamanyo;

        listaControl.set(hueco, proceso);

        if (espacioRestante > 0) {
            int[] bloqueRestante = {0, direcc + tamanyo, espacioRestante, 0};
            listaControl.add(hueco + 1, bloqueRestante);
        }

        escribeMemoria(direcc, tamanyo, 1);

        return true;
    }

    public static boolean destruyeProceso(int pid) {

        int indice = -1;

        for (int i = 0; i < listaControl.size(); i++) {
            if (listaControl.get(i)[3] == pid) {
                indice = i;
                break;
            }
        }

        if (indice == -1) return false;

        int[] bloque = listaControl.get(indice);

        bloque[0] = 0;
        bloque[3] = 0;

        escribeMemoria(bloque[1], bloque[2], 0);

        fusiona(indice);
        fusiona(indice - 1);

        return true;
    }

    private static boolean fusiona(int indice) {

        if (indice < 0 || indice + 1 >= listaControl.size()) return false;

        if (listaControl.get(indice)[0] == 0 &&
            listaControl.get(indice + 1)[0] == 0) {

            int tam = listaControl.remove(indice + 1)[2];
            listaControl.get(indice)[2] += tam;

            return true;
        }

        return false;
    }

    public static void imprimeMemoria() {

        String s = "Lista de control: {[EST-DIR-TAM-PROC]:";

        for (int[] bloque : listaControl) {
            s += "[" + bloque[0] + "-" + bloque[1] + "-" + bloque[2] + "-" + bloque[3] + "]";
        }

        s += "}\nMemoria: ";

        for (int i : memoria) {
            s += i;
        }

        System.out.println(s + "\n");
    }

    public static void main(String[] args) {

        inicializaMemoria();

        creaProceso(1, 6);
        imprimeMemoria();

        creaProceso(2, 6);
        imprimeMemoria();

        creaProceso(3, 8);
        imprimeMemoria();

        creaProceso(4, 7);
        imprimeMemoria();

        destruyeProceso(2);
        destruyeProceso(4);

        creaProceso(5, 7);
        imprimeMemoria();

        destruyeProceso(1);

        creaProceso(6, 5);
        imprimeMemoria();

        destruyeProceso(5);
        imprimeMemoria();

        destruyeProceso(3);
        destruyeProceso(6);
        imprimeMemoria();
    }
}
