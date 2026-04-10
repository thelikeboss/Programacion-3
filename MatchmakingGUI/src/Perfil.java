package src;

public class Perfil {
    public String username;
    public String password;
    public int elo;
    public double headshotPercent;
    public double precisionPercent;
    public int kills;
    public double kd;
    public boolean online;

    public Perfil(String username, String password, int elo, double headshotPercent, double precisionPercent, int kills, double kd) {
        this.username = username;
        this.password = password;
        this.elo = elo;
        this.headshotPercent = headshotPercent;
        this.precisionPercent = precisionPercent;
        this.kills = kills;
        this.kd = kd;
        this.online = false;
    }

    public double calcularMMR() {
        return 0.4 * elo + 0.2 * headshotPercent + 0.2 * precisionPercent + 0.1 * kills + 0.1 * kd;
    }

    @Override
    public String toString() {
        return username + " | ELO: " + elo + " | MMR: " + calcularMMR();
    }
}
