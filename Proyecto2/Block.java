package Proyecto2;

import java.security.MessageDigest;

public class Block {
    public int index;
    public long timestamp;
    public String data;
    public String previousHash;
    public String hash;

        public String voterId;
        public String candidate;
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = previousHash;
            this.voterId = voterId;
            this.candidate = candidate;
    }

    public String calculateHash() {
        try {
            String input = index + Long.toString(timestamp) + data + previousHash;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String input = index + Long.toString(timestamp) + voterId + candidate + previousHash;
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
