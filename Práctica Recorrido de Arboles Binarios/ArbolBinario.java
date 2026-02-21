import java.util.Stack;

class Nodo {
    private int n;
    private Nodo leftChild;
    private Nodo rightChild;

    public Nodo(int n) {
        this(n, null, null);
    }

    public Nodo(int n, Nodo leftChild, Nodo rightChild) {
        this.n = n;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

    public int getN() {
        return n;
    }

    public Nodo getLeftChild() {
        return leftChild;
    }

    public Nodo getRightChild() {
        return rightChild;
    }
}

public class ArbolBinario {

    private Nodo root;

    public void setRoot(Nodo root) {
        this.root = root;
    }

    public Nodo getRoot() {
        return root;
    }

    // Inicializa el árbol
    public Nodo initial() {
        Nodo D = new Nodo(2);
        Nodo E = new Nodo(4);
        Nodo F = new Nodo(6);
        Nodo G = new Nodo(8);
        Nodo B = new Nodo(3, D, E);
        Nodo C = new Nodo(7, F, G);
        Nodo A = new Nodo(5, B, C);
        return A;
    }

    // PREORDER (Raíz - Izquierda - Derecha)
    public void preOrder(Nodo nodo) {
        if (nodo == null) {
            return;
        }
        System.out.print(nodo.getN() + " ");
        preOrder(nodo.getLeftChild());
        preOrder(nodo.getRightChild());
    }

    // INORDER (Izquierda - Raíz - Derecha)
    public void inOrder(Nodo nodo) {
        if (nodo == null) {
            return;
        }
        inOrder(nodo.getLeftChild());
        System.out.print(nodo.getN() + " ");
        inOrder(nodo.getRightChild());
    }

    // POSTORDER (Izquierda - Derecha - Raíz)
    public void postOrder(Nodo nodo) {
        if (nodo == null) {
            return;
        }a
        postOrder(nodo.getRightChild());
        System.out.print(nodo.getN() + " ");
    }

    // POSTORDER Iterativo (método doble pila)
    public void iterativePostOrder(Nodo nodo) {
        if (nodo == null) return;

        Stack<Nodo> stack = new Stack<>();
        Stack<Nodo> temp = new Stack<>();

        while (nodo != null || !stack.isEmpty()) {

            while (nodo != null) {
                temp.push(nodo);
                stack.push(nodo);
                nodo = nodo.getRightChild();
            }

            if (!stack.isEmpty()) {
                nodo = stack.pop();
                nodo = nodo.getLeftChild();
            }
        }

        while (!temp.isEmpty()) {
            nodo = temp.pop();
            System.out.print(nodo.getN() + " ");
        }
    }

    // Profundidad del árbol
    public int getTreeDepth(Nodo nodo) {
        if (nodo == null) {
            return 0;
        }

        int left = getTreeDepth(nodo.getLeftChild());
        int right = getTreeDepth(nodo.getRightChild());

        return (left < right) ? (right + 1) : (left + 1);
    }

    public static void main(String[] args) {

        ArbolBinario bt = new ArbolBinario();
        bt.setRoot(bt.initial());

        System.out.println("PreOrder Recorrido de Arbol Binario:");
        bt.preOrder(bt.getRoot());
        System.out.println();

        System.out.println("InOrder Recorrido de Arbol Binario:");
        bt.inOrder(bt.getRoot());
        System.out.println();

        System.out.println("PostOrder Recorrido de Arbol Binario:");
        bt.postOrder(bt.getRoot());
        System.out.println();

        System.out.println("PostOrder Iterativo:");
        bt.iterativePostOrder(bt.getRoot());
        System.out.println();

        System.out.print("Profundidad del Arbol Binario: ");
        System.out.println(bt.getTreeDepth(bt.getRoot()));
    }
}
