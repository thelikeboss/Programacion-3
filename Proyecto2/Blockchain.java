package Proyecto2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Blockchain {
    private List<Block> chain;
    private Set<String> votedIds; // Smart contract: no doble voto

    public Blockchain() {
        chain = new ArrayList<>();
        votedIds = new HashSet<>();
        chain.add(new Block(0, System.currentTimeMillis(), "GENESIS", "", "0"));
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public boolean addVote(String voterId, String candidate) {
        // Smart contract: un voto por persona
        if (votedIds.contains(voterId)) {
            return false;
        }
        Block prev = getLatestBlock();
        Block newBlock = new Block(prev.index + 1, System.currentTimeMillis(), voterId, candidate, prev.hash);
        chain.add(newBlock);
        votedIds.add(voterId);
        return true;
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block curr = chain.get(i);
            Block prev = chain.get(i - 1);
            if (!curr.hash.equals(curr.calculateHash())) {
                return false;
            }
            if (!curr.previousHash.equals(prev.hash)) {
                return false;
            }
        }
        return true;
    }

    public int countVotes(String candidate) {
        int count = 0;
        for (Block b : chain) {
            if (b.candidate.equals(candidate)) {
                count++;
            }
        }
        return count;
    }

    public List<Block> getChain() {
        return chain;
    }
}
