import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Clase que simula el Servidor de Tiempo (Time Server)
class TimeServer {
    // Tiempo "verdadero" del servidor (en milisegundos)
    private long serverTime;

    public TimeServer(long initialTime) {
        this.serverTime = initialTime;
    }

   public synchronized long getServerTime() {
        return serverTime;
    }

   public synchronized void tick(long ms) {
        serverTime += ms;
    }
}

// Clase que simula un Cliente (proceso distribuido)
class CristianClient {
    private String name;
    private long localTime;       // Reloj local del cliente (desincronizado)
    private Random random;

    public CristianClient(String name, long localTime) {
        this.name = name;
        this.localTime = localTime;
        this.random = new Random();
    }

    public String getName() { return name; }
    public long getLocalTime() { return localTime; }

     //Ejecuta el Algoritmo de Cristian para sincronizarse con el servidor.
    public void synchronize(TimeServer server, int networkDelayMs) {

        System.out.println("\n[" + name + "] ── Iniciando sincronización ──");
        System.out.println("[" + name + "] Tiempo local ANTES de sincronizar: " + localTime + " ms");

        // PASO 1: Registrar T0 (momento del envío)
        long T0 = localTime;
        System.out.println("[" + name + "] T0 (envío de solicitud): " + T0 + " ms");

        // PASO 2: Simular latencia de ida (red)
        int delayOneWay = networkDelayMs / 2 + random.nextInt(10); // pequeña variación

        // PASO 3: Obtener tiempo del servidor
        long T_server = server.getServerTime();
        System.out.println("[" + name + "] T_server (tiempo del servidor): " + T_server + " ms");

        // PASO 4: Simular latencia de vuelta (red) — el RTT total
        int RTT = networkDelayMs + random.nextInt(10);
        long T1 = T0 + RTT;
        System.out.println("[" + name + "] T1 (recepción de respuesta): " + T1 + " ms");
        System.out.println("[" + name + "] RTT calculado: " + RTT + " ms");

        // PASO 5: Aplicar la fórmula de Cristian
        long nuevoTiempo = T_server + (RTT / 2);
        long diferencia = nuevoTiempo - localTime;

        System.out.println("[" + name + "] Fórmula: T_nuevo = " + T_server + " + " + (RTT / 2) + " = " + nuevoTiempo + " ms");
        System.out.println("[" + name + "] Ajuste aplicado: " + (diferencia >= 0 ? "+" : "") + diferencia + " ms");

        // PASO 6: Actualizar reloj local
        localTime = nuevoTiempo;
        System.out.println("[" + name + "] Tiempo local DESPUÉS de sincronizar: " + localTime + " ms");
    }
}

// Clase principal — Pruebas del Algoritmo de Cristian
public class CristianAlgorithm {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("=======================================================");
        System.out.println("       ALGORITMO DE CRISTIAN — Sincronización");
        System.out.println("       Sistemas Distribuidos | UNSA");
        System.out.println("=======================================================");

        // ── PRUEBA 1: Sincronización básica con un cliente ──
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  PRUEBA 1: Sincronización básica          ║");
        System.out.println("╚══════════════════════════════════════════╝");

        TimeServer server = new TimeServer(10000); // El servidor está en t=10000 ms
        CristianClient cliente1 = new CristianClient("Cliente-A", 9800); // Atrasado 200ms

        System.out.println("\nServidor (tiempo verdadero): " + server.getServerTime() + " ms");
        System.out.println("Cliente-A (tiempo local):    " + cliente1.getLocalTime() + " ms");
        System.out.println("Diferencia inicial:          " + (server.getServerTime() - cliente1.getLocalTime()) + " ms");

        cliente1.synchronize(server, 100); // 100ms RTT simulado

        System.out.println("\n✓ Error residual estimado: " + Math.abs(server.getServerTime() - cliente1.getLocalTime()) + " ms");


        // ── PRUEBA 2: Múltiples clientes con distintos desfases ──
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  PRUEBA 2: Múltiples clientes             ║");
        System.out.println("╚══════════════════════════════════════════╝");

        TimeServer server2 = new TimeServer(50000);

        List<CristianClient> clientes = new ArrayList<>();
        clientes.add(new CristianClient("Proceso-P1", 49500)); // atrasado 500ms
        clientes.add(new CristianClient("Proceso-P2", 50300)); // adelantado 300ms
        clientes.add(new CristianClient("Proceso-P3", 48800)); // atrasado 1200ms
        clientes.add(new CristianClient("Proceso-P4", 50100)); // casi sincronizado
        clientes.add(new CristianClient("Proceso-P5", 47000)); // muy atrasado

        System.out.println("\nServidor (tiempo verdadero): " + server2.getServerTime() + " ms");
        System.out.println("\nEstado INICIAL de los procesos:");
        for (CristianClient c : clientes) {
            long diff = c.getLocalTime() - server2.getServerTime();
            System.out.printf("  %-12s: %6d ms  (desfase: %+d ms)%n",
                    c.getName(), c.getLocalTime(), diff);
        }

        // Sincronizar cada cliente usando hilos
        List<Thread> threads = new ArrayList<>();
        Random rand = new Random();

        for (CristianClient cliente : clientes) {
            int latencia = 50 + rand.nextInt(150); // latencia aleatoria entre 50-200ms
            Thread t = new Thread(() -> cliente.synchronize(server2, latencia));
            threads.add(t);
        }

        // Iniciar todos los hilos
        for (Thread t : threads) {
            t.start();
        }

        // Esperar a que todos terminen
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("\n─────────────────────────────────────────────");
        System.out.println("Estado FINAL (después de sincronización):");
        for (CristianClient c : clientes) {
            long errorResidual = Math.abs(c.getLocalTime() - server2.getServerTime());
            System.out.printf("  %-12s: %6d ms  (error residual: ~%d ms)%n",
                    c.getName(), c.getLocalTime(), errorResidual);
        }

        // ── PRUEBA 3: Efecto del RTT sobre la precisión ──
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║  PRUEBA 3: Impacto del RTT en precisión   ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("\nCuanto mayor es el RTT, mayor es el error de sincronización.");
        System.out.println("(La fórmula asume red simétrica: RTT/2 = tiempo real de tránsito)\n");

        TimeServer server3 = new TimeServer(100000);
        int[] latencias = {10, 50, 100, 300, 1000};

        System.out.printf("  %-20s %-15s %-15s%n", "Escenario", "RTT (ms)", "Error estimado");
        System.out.println("  " + "─".repeat(52));

        for (int rtt : latencias) {
            CristianClient c = new CristianClient("Test", 99000);
            c.synchronize(server3, rtt);
            long error = Math.abs(c.getLocalTime() - server3.getServerTime());
            System.out.printf("  %-20s %-15d ~%-15d%n",
                    "RTT=" + rtt + "ms", rtt, error);
        }

        System.out.println("\n=======================================================");
        System.out.println(" FIN DE PRUEBAS — Algoritmo de Cristian");
        System.out.println("=======================================================");
    }
}
