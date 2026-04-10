import java.util.*;
import java.time.Instant;

// ============================================================
//  MODELO DE DATOS
// ============================================================

enum PlayerStatus { OFFLINE, ONLINE, IN_QUEUE, IN_GAME }
enum MatchStatus  { WAITING, ACTIVE, FINISHED }
enum PlayerRank   { IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND }

class Player {
    String id;
    String username;
    int    elo;
    PlayerRank rank;
    String region;        // "NA", "LATAM", "EU"
    PlayerStatus status;
    long   joinedQueueAt; // timestamp para fairness

    Player(String id, String username, int elo, String region) {
        this.id       = id;
        this.username = username;
        this.elo      = elo;
        this.region   = region;
        this.rank     = calcRank(elo);
        this.status   = PlayerStatus.OFFLINE;
    }

    static PlayerRank calcRank(int elo) {
        if (elo < 800)  return PlayerRank.IRON;
        if (elo < 1000) return PlayerRank.BRONZE;
        if (elo < 1200) return PlayerRank.SILVER;
        if (elo < 1500) return PlayerRank.GOLD;
        if (elo < 1800) return PlayerRank.PLATINUM;
        return PlayerRank.DIAMOND;
    }

    // Diferencia de ELO con otro jugador
    int eloDiff(Player other) {
        return Math.abs(this.elo - other.elo);
    }

    @Override public String toString() {
        return String.format("[%s | ELO:%d | %s | %s | %s]",
            username, elo, rank, region, status);
    }
}

class Match {
    String  matchId;
    Player  player1;
    Player  player2;
    int     avgElo;
    long    startTime;
    long    endTime;
    MatchStatus status;
    String  winner;

    Match(String matchId, Player p1, Player p2) {
        this.matchId   = matchId;
        this.player1   = p1;
        this.player2   = p2;
        this.avgElo    = (p1.elo + p2.elo) / 2;
        this.startTime = Instant.now().getEpochSecond();
        this.status    = MatchStatus.ACTIVE;
    }

    void finish(String winnerId) {
        this.winner  = winnerId;
        this.endTime = Instant.now().getEpochSecond();
        this.status  = MatchStatus.FINISHED;
    }

    @Override public String toString() {
        return String.format("Match[%s] %s vs %s | ELO promedio:%d | %s",
            matchId, player1.username, player2.username, avgElo, status);
    }
}

// ============================================================
//  TABLA HASH GENÉRICA — máximo 2 colisiones por slot
//  Estrategia: encadenamiento con LinkedList de capacidad 2
// ============================================================

class HashTable<K, V> {

    // Nodo de la cadena
    static class Entry<K, V> {
        K key;
        V value;
        Entry(K k, V v) { this.key = k; this.value = v; }
    }

    private final int MAX_CHAIN = 2;          // máximo 2 colisiones
    private final int capacity;
    private final List<LinkedList<Entry<K,V>>> table;
    private int size;
    private int collisions;

    HashTable(int capacity) {
        this.capacity   = capacity;
        this.collisions = 0;
        this.table      = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) this.table.add(new LinkedList<>());
    }

    // Función hash: suma de chars del toString de la clave % capacidad
    private int hash(K key) {
        int sum = 0;
        for (char c : key.toString().toCharArray()) sum += c;
        return sum % capacity;
    }

    // Inserción — lanza excepción si se superan 2 colisiones
    void put(K key, V value) {
        int idx = hash(key);
        LinkedList<Entry<K,V>> chain = table.get(idx);

        // Actualizar si ya existe la clave
        for (Entry<K,V> e : chain) {
            if (e.key.equals(key)) { e.value = value; return; }
        }

        // Verificar límite de colisiones
        if (chain.size() >= MAX_CHAIN) {
            throw new RuntimeException(
                "ERROR: Slot " + idx + " ya tiene " + MAX_CHAIN +
                " entradas. Máximo de colisiones alcanzado para clave: " + key
            );
        }

        if (!chain.isEmpty()) collisions++;   // hay colisión real
        chain.add(new Entry<>(key, value));
        size++;
    }

    // Búsqueda O(1) amortizado, O(2) peor caso
    V get(K key) {
        int idx = hash(key);
        for (Entry<K,V> e : table.get(idx))
            if (e.key.equals(key)) return e.value;
        return null;
    }

    // Eliminación
    boolean remove(K key) {
        int idx = hash(key);
        LinkedList<Entry<K,V>> chain = table.get(idx);
        Iterator<Entry<K,V>> it = chain.iterator();
        while (it.hasNext()) {
            if (it.next().key.equals(key)) { it.remove(); size--; return true; }
        }
        return false;
    }

    boolean containsKey(K key) { return get(key) != null; }
    int  size()       { return size; }
    int  collisions() { return collisions; }

    // Recorre todos los valores
    List<V> values() {
        List<V> out = new ArrayList<>();
        for (LinkedList<Entry<K,V>> chain : table)
            for (Entry<K,V> e : chain) out.add(e.value);
        return out;
    }

    void printState(String label) {
        System.out.println("\n--- " + label + " ---");
        for (int i = 0; i < capacity; i++) {
            LinkedList<Entry<K,V>> chain = table.get(i);
            if (!chain.isEmpty()) {
                System.out.print("  Slot[" + i + "]: ");
                for (Entry<K,V> e : chain) System.out.print("[" + e.key + "] ");
                if (chain.size() > 1) System.out.print("<-- COLISIÓN");
                System.out.println();
            }
        }
        System.out.println("  Tamaño: " + size + " | Colisiones: " + collisions);
    }
}

// ============================================================
//  GESTOR DE PERFILES — login / logout / CRUD
// ============================================================

class ProfileManager {

    private final HashTable<String, Player> profiles;

    ProfileManager(int capacity) {
        this.profiles = new HashTable<>(capacity);
    }

    // Registro de jugador
    boolean register(Player p) {
        if (profiles.containsKey(p.id)) {
            System.out.println("  [REGISTRO] " + p.username + " ya existe.");
            return false;
        }
        profiles.put(p.id, p);
        System.out.println("  [REGISTRO] " + p.username + " registrado. ELO:" + p.elo);
        return true;
    }

    // Login
    Player login(String playerId) {
        Player p = profiles.get(playerId);
        if (p == null) { System.out.println("  [LOGIN] ID no encontrado: " + playerId); return null; }
        p.status = PlayerStatus.ONLINE;
        System.out.println("  [LOGIN]  " + p.username + " conectado. ELO:" + p.elo + " | Rango:" + p.rank);
        return p;
    }

    // Logout
    void logout(String playerId) {
        Player p = profiles.get(playerId);
        if (p == null) return;
        p.status = PlayerStatus.OFFLINE;
        System.out.println("  [LOGOUT] " + p.username + " desconectado.");
    }

    // Actualizar ELO tras partida
    void updateElo(String playerId, int delta) {
        Player p = profiles.get(playerId);
        if (p == null) return;
        int oldElo = p.elo;
        p.elo = Math.max(0, p.elo + delta);
        p.rank = Player.calcRank(p.elo);
        System.out.printf("  [ELO] %s: %d → %d (%s%d) | Rango: %s%n",
            p.username, oldElo, p.elo, delta >= 0 ? "+" : "", delta, p.rank);
    }

    Player getPlayer(String id) { return profiles.get(id); }

    void printStats() { profiles.printState("TABLA DE PERFILES"); }
}

// ============================================================
//  MOTOR DE MATCHMAKING — búsqueda por rango de ELO
// ============================================================

class MatchmakingEngine {

    private static final int ELO_RANGE = 100;   // ±100 ELO para emparejar
    private final Queue<Player> waitingQueue;    // FIFO, respeta tiempo de espera
    private final ProfileManager profileManager;

    MatchmakingEngine(ProfileManager pm) {
        this.profileManager = pm;
        this.waitingQueue   = new LinkedList<>();
    }

    // Unirse a la cola
    void enqueue(Player p) {
        if (p.status == PlayerStatus.IN_GAME) {
            System.out.println("  [COLA] " + p.username + " ya está en partida."); return;
        }
        p.status      = PlayerStatus.IN_QUEUE;
        p.joinedQueueAt = Instant.now().getEpochSecond();
        waitingQueue.add(p);
        System.out.println("  [COLA] " + p.username + " (ELO:" + p.elo + ") entró a la cola. En espera: " + waitingQueue.size());
    }

    // Salir de la cola
    void dequeue(Player p) {
        waitingQueue.remove(p);
        p.status = PlayerStatus.ONLINE;
        System.out.println("  [COLA] " + p.username + " salió de la cola.");
    }

    /*  Algoritmo de emparejamiento
     *  ──────────────────────────
     *  1. Tomar el primer jugador de la cola (más tiempo esperando)
     *  2. Buscar en el resto de la cola un jugador con ELO en rango ±100
     *     y misma región (preferencia) o cualquier región (fallback)
     *  3. Si se encuentra → crear Match
     *  4. Si no → devolver null (se reintentará en el próximo ciclo)
     *
     *  Atributos comparados:
     *    - ELO          (obligatorio, rango ±100)
     *    - Región       (preferencia, no obligatorio)
     *    - Estado       (debe ser IN_QUEUE)
     */
    Match tryMatch() {
        if (waitingQueue.size() < 2) return null;

        // Candidato principal: primero en la cola
        Player seeker = waitingQueue.poll();

        System.out.printf("  [MATCH] Buscando rival para %s (ELO:%d ±%d, región:%s)%n",
            seeker.username, seeker.elo, ELO_RANGE, seeker.region);

        Player bestMatch   = null;
        boolean sameRegion = false;

        // Iterar el resto de la cola
        for (Player candidate : waitingQueue) {
            int diff = seeker.eloDiff(candidate);
            if (diff > ELO_RANGE) {
                System.out.printf("    ✗ %s ELO:%d dif:%d (fuera de rango)%n",
                    candidate.username, candidate.elo, diff);
                continue;
            }

            boolean samReg = seeker.region.equals(candidate.region);

            // Preferir misma región, luego menor diferencia de ELO
            if (bestMatch == null ||
                (!sameRegion && samReg) ||
                (sameRegion == samReg && diff < seeker.eloDiff(bestMatch))) {
                bestMatch  = candidate;
                sameRegion = samReg;
                System.out.printf("    ✓ %s ELO:%d dif:%d región:%s%n",
                    candidate.username, candidate.elo, diff, samReg ? "igual" : "distinta");
            }
        }

        if (bestMatch == null) {
            // No se encontró rival → devolver a la cola (al frente para prioridad)
            System.out.println("  [MATCH] Sin rival disponible. " + seeker.username + " vuelve a la cola.");
            ((LinkedList<Player>) waitingQueue).addFirst(seeker);
            return null;
        }

        // Crear partida
        waitingQueue.remove(bestMatch);
        seeker.status    = PlayerStatus.IN_GAME;
        bestMatch.status = PlayerStatus.IN_GAME;

        String matchId = "MATCH-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Match match = new Match(matchId, seeker, bestMatch);

        System.out.printf("  [MATCH] ¡Partida creada! %s vs %s | ELO promedio:%d | Δ:%d%n",
            seeker.username, bestMatch.username, match.avgElo, seeker.eloDiff(bestMatch));

        return match;
    }

    int queueSize() { return waitingQueue.size(); }

    void printQueue() {
        System.out.println("\n--- COLA DE ESPERA (" + waitingQueue.size() + " jugadores) ---");
        int i = 0;
        for (Player p : waitingQueue)
            System.out.printf("  [%d] %s ELO:%d región:%s%n", i++, p.username, p.elo, p.region);
    }
}

// ============================================================
//  GESTOR DE SESIONES ACTIVAS
// ============================================================

class SessionManager {

    private final HashTable<String, Match> activeSessions;
    private int totalMatchesPlayed;

    SessionManager(int capacity) {
        this.activeSessions   = new HashTable<>(capacity);
        this.totalMatchesPlayed = 0;
    }

    void addSession(Match match) {
        activeSessions.put(match.matchId, match);
        System.out.println("  [SESIÓN] Registrada: " + match);
    }

    // Simular fin de partida — actualiza ELO y libera recursos
    void endSession(String matchId, String winnerId, ProfileManager pm) {
        Match match = activeSessions.get(matchId);
        if (match == null) {
            System.out.println("  [SESIÓN] No encontrada: " + matchId); return;
        }

        match.finish(winnerId);
        activeSessions.remove(matchId);
        totalMatchesPlayed++;

        // Determinar ganador y perdedor
        boolean p1Won  = match.player1.id.equals(winnerId);
        Player winner  = p1Won ? match.player1 : match.player2;
        Player loser   = p1Won ? match.player2 : match.player1;
        int    eloDelta = calcEloDelta(winner.elo, loser.elo);

        System.out.printf("  [FIN]   %s ganó. +%d ELO / -%d ELO%n",
            winner.username, eloDelta, eloDelta);

        pm.updateElo(winner.id, +eloDelta);
        pm.updateElo(loser.id,  -eloDelta);

        winner.status = PlayerStatus.ONLINE;
        loser.status  = PlayerStatus.ONLINE;
    }

    /*  Delta de ELO simplificado (variante del sistema Elo)
     *  Si el ganador tiene mucho más ELO → gana pocos puntos
     *  Si el ganador tiene menos ELO   → gana muchos puntos */
    private int calcEloDelta(int winnerElo, int loserElo) {
        int diff = winnerElo - loserElo;
        if (diff >  200) return 10;
        if (diff > 0)    return 20;
        if (diff > -200) return 30;
        return 40;  // victoria upset
    }

    void printActiveSessions() {
        System.out.println("\n--- SESIONES ACTIVAS (" + activeSessions.size() + ") ---");
        for (Match m : activeSessions.values()) System.out.println("  " + m);
    }

    void printStats() {
        activeSessions.printState("TABLA DE SESIONES");
        System.out.println("  Total partidas jugadas (historial): " + totalMatchesPlayed);
    }
}

// ============================================================
//  SERVIDOR PRINCIPAL — orquesta todo
// ============================================================

class GameServer {

    private final ProfileManager   profileManager;
    private final MatchmakingEngine matchmakingEngine;
    private final SessionManager   sessionManager;

    GameServer() {
        // Tamaño elegido para demostrar colisiones controladas
        profileManager    = new ProfileManager(11);
        matchmakingEngine = new MatchmakingEngine(profileManager);
        sessionManager    = new SessionManager(13);
    }

    // Flujo completo: registro → login → cola → match → sesión → fin
    void run() {

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   SERVIDOR DE MATCHMAKING — SIMULACIÓN       ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        // ── 1. REGISTRO DE JUGADORES ────────────────────────────────
        System.out.println("\n════ 1. REGISTRO ════");
        Player[] players = {
            new Player("P001", "IronBlade",   750,  "LATAM"),
            new Player("P002", "SilverFox",   1100, "LATAM"),
            new Player("P003", "DarkWolf",    1480, "NA"),
            new Player("P004", "Player123",   1500, "LATAM"),
            new Player("P005", "Sniper99",    1820, "EU"),
            new Player("P006", "GhostX",      1780, "EU"),
            new Player("P007", "CrimsonAce",  1200, "LATAM"),
            new Player("P008", "StormRider",  1150, "NA"),
            new Player("P009", "ZeroGravity", 1520, "NA"),
            new Player("P010", "NightOwl",    500,  "LATAM"),
        };

        for (Player p : players) profileManager.register(p);
        profileManager.printStats();

        // ── 2. LOGIN ─────────────────────────────────────────────────
        System.out.println("\n════ 2. LOGIN ════");
        String[] toLogin = {"P001","P002","P003","P004","P005","P006","P007","P008","P009"};
        List<Player> online = new ArrayList<>();
        for (String id : toLogin) {
            Player p = profileManager.login(id);
            if (p != null) online.add(p);
        }

        // ── 3. COLA DE ESPERA ─────────────────────────────────────────
        System.out.println("\n════ 3. COLA DE ESPERA ════");
        for (Player p : online) matchmakingEngine.enqueue(p);
        matchmakingEngine.printQueue();

        // ── 4. EMPAREJAMIENTO ────────────────────────────────────────
        System.out.println("\n════ 4. EMPAREJAMIENTO POR ELO ════");
        List<Match> matches = new ArrayList<>();
        Match m;
        int attempts = 0, maxAttempts = 10;
        while ((m = matchmakingEngine.tryMatch()) != null && attempts++ < maxAttempts) {
            matches.add(m);
            sessionManager.addSession(m);
        }
        System.out.println("  Jugadores sin rival: " + matchmakingEngine.queueSize());
        sessionManager.printActiveSessions();
        sessionManager.printStats();

        // ── 5. FINALIZACIÓN DE PARTIDAS ──────────────────────────────
        System.out.println("\n════ 5. FIN DE PARTIDAS ════");
        if (!matches.isEmpty()) {
            Match first = matches.get(0);
            System.out.println("  Terminando: " + first.matchId);
            sessionManager.endSession(first.matchId, first.player1.id, profileManager);
        }
        if (matches.size() > 1) {
            Match second = matches.get(1);
            System.out.println("  Terminando: " + second.matchId);
            sessionManager.endSession(second.matchId, second.player2.id, profileManager);
        }

        sessionManager.printActiveSessions();

        // ── 6. LOGOUT ─────────────────────────────────────────────────
        System.out.println("\n════ 6. LOGOUT ════");
        profileManager.logout("P001");
        profileManager.logout("P002");

        // ── 7. RESUMEN FINAL ─────────────────────────────────────────
        System.out.println("\n════ 7. RESUMEN FINAL ════");
        profileManager.printStats();
        sessionManager.printStats();
    }
}

// ============================================================
//  PUNTO DE ENTRADA
// ============================================================

public class MatchmakingSystem {
    public static void main(String[] args) {
        new GameServer().run();
    }
}
