package Lamport;

import java.util.concurrent.atomic.AtomicInteger;

class Process extends Thread {
    private String processName;
    private AtomicInteger clock;

    public Process(String name) {
        this.processName = name;
        this.clock = new AtomicInteger(0);
    }

    public int getClock() {
        return clock.get();
    }

    // Regla 1: Evento local
    public void localEvent() {
        int newVal = clock.incrementAndGet();
        System.out.println("[Evento Local] " + processName + " incrementa reloj a: " + newVal);
    }

    // Regla 2: Enviar mensaje
    public int sendEvent() {
        int timeSent = clock.incrementAndGet();
        System.out.println("[Enviar] " + processName + " envía mensaje con T=" + timeSent);
        return timeSent;
    }

    // Regla 3: Recibir mensaje
    public void receiveEvent(String senderName, int receivedTime) {
        int currentTime = clock.get();
        // Lógica: máx(local, recibido) + 1
        int nextTime = Math.max(currentTime, receivedTime) + 1;
        clock.set(nextTime);
        System.out.println("[Recibir] " + processName + " recibió de " + senderName + 
                           " (T=" + receivedTime + "). Reloj local era " + currentTime + 
                           " -> Ajustado a: " + nextTime);
    }

    @Override
    public void run() {
        System.out.println("Proceso " + processName + " iniciado con reloj: " + clock.get());
    }
}

public class LamportAlgorithm {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== SIMULACIÓN DE RELOJES LÓGICOS DE LAMPORT ===");

        Process p1 = new Process("P1");
        Process p2 = new Process("P2");
        Process p3 = new Process("P3");

        p1.start(); p2.start(); p3.start();
        Thread.sleep(100); // Esperar inicio

        // 1. P1 tiene un evento interno
        p1.localEvent();

        // 2. P1 envía un mensaje a P2
        int msgP1toP2 = p1.sendEvent();
        p2.receiveEvent("P1", msgP1toP2);

        // 3. P3 tiene un evento local (está desfasado, empieza de 0)
        p3.localEvent();

        // 4. P2 envía mensaje a P3
        int msgP2toP3 = p2.sendEvent();
        p3.receiveEvent("P2", msgP2toP3);

        // 5. P3 envía mensaje a P1 (salto grande en el reloj de P1)
        int msgP3toP1 = p3.sendEvent();
        p1.receiveEvent("P3", msgP3toP1);

        System.out.println("\nEstado Final:");
        System.out.println("P1: " + p1.getClock());
        System.out.println("P2: " + p2.getClock());
        System.out.println("P3: " + p3.getClock());
    }
}
