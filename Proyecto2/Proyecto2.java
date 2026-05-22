package Proyecto2;

public class Proyecto2 {
	public static void main(String[] args) {
		Blockchain blockchain = new Blockchain();

		// Simulación de votos: voterId, candidato
		System.out.println("Agregando votos...");
		System.out.println("Voto 1: " + blockchain.addVote("v001", "Alice"));
		System.out.println("Voto 2: " + blockchain.addVote("v002", "Bob"));
		System.out.println("Voto 3: " + blockchain.addVote("v003", "Alice"));
		System.out.println("Voto 4 (repetido): " + blockchain.addVote("v001", "Bob")); // No debe permitir

		System.out.println("\nResultados en tiempo real:");
		System.out.println("Alice: " + blockchain.countVotes("Alice"));
		System.out.println("Bob: " + blockchain.countVotes("Bob"));

		System.out.println("\nValidando cadena: " + blockchain.isChainValid());

		// Mostrar todos los votos (bloques)
		System.out.println("\nVotos registrados:");
		for (Block b : blockchain.getChain()) {
			System.out.println("Index: " + b.index + ", Votante: " + b.voterId + ", Candidato: " + b.candidate + ", Hash: " + b.hash);
		}

		// Demostración de inmutabilidad
		System.out.println("\n--- Prueba de inmutabilidad ---");
		System.out.println("Cadena válida antes de modificar: " + blockchain.isChainValid());
		// Modificar manualmente un bloque
		if (blockchain.getChain().size() > 1) {
			blockchain.getChain().get(1).candidate = "Bob";
		}
		System.out.println("Cadena válida después de modificar: " + blockchain.isChainValid());
	}
}
