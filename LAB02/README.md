# ⏱️ Sincronización en Sistemas Distribuidos

## 📌 Descripción
Este proyecto aborda el estudio e implementación de algoritmos de sincronización en sistemas distribuidos, con énfasis en relojes lógicos y físicos. Se analiza el problema de la coordinación entre procesos que operan en distintas máquinas sin un reloj global compartido.

---

## 🧠 Marco Conceptual

La sincronización en sistemas distribuidos es compleja debido a:

- La información está distribuida en múltiples máquinas  
- Cada proceso toma decisiones basadas en información local  
- Se debe evitar un punto único de falla  
- No existe un reloj global compartido  

### ⏱️ Importancia del tiempo

El tiempo es clave porque:
- Permite medir eventos de forma precisa  
- Muchos algoritmos dependen de la sincronización temporal  

Problemas:
- No existe un tiempo absoluto  
- Los relojes de diferentes máquinas no están sincronizados  

Además, la comunicación por red introduce retardos variables (latencia, congestión, distancia, etc.).

---

## 🔄 Tipos de Relojes

### 🧩 Relojes Lógicos (Lamport)
- No buscan sincronización exacta, sino orden de eventos  
- Se basan en la relación “ocurre antes de” (→)  
- Importa el orden, no el tiempo real  

### 🌍 Relojes Físicos
- Basados en estándares como UTC o TAI  
- Se sincronizan mediante radio o satélites  

---

## ⚙️ Algoritmos de Sincronización

### 📌 Sincronización de Relojes
- Algoritmo de Lamport  
- Algoritmo de Cristian  
- Algoritmo de Berkeley  
- Algoritmo de Promedio  

### 🔐 Exclusión Mutua
- Centralizado  
- Distribuido  
- Token Ring  
- Elección  
- Algoritmo del Grandulón (García Molina)  
- Anillo  

---

## 🧮 Algoritmo de Lamport

Define la relación:

> Si a → b, entonces C(a) < C(b)

Reglas:
1. Eventos en el mismo proceso mantienen orden  
2. Envío y recepción de mensajes definen orden  
3. Eventos concurrentes no tienen relación  

---

## 💻 Implementación en Java

```java
import java.util.ArrayList;
import java.util.List;

public class LamportClock {
    private int clock;

    public LamportClock() {
        this.clock = 0;
    }

    public synchronized int tick() {
        this.clock++;
        return this.clock;
    }

    public synchronized void update(int receivedTime) {
        this.clock = Math.max(this.clock, receivedTime) + 1;
    }

    public int getTime() {
        return this.clock;
    }

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        LamportClock clock = new LamportClock();

        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                int time = clock.tick();
                System.out.println("Thread " + Thread.currentThread().getId() +
                        " created event with Lamport time " + time);

                try {
                    Thread.sleep((long) (Math.random() * 1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int receivedTime = clock.tick();
                System.out.println("Thread " + Thread.currentThread().getId() +
                        " received event with Lamport time " + receivedTime);

                clock.update(receivedTime);
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Final Lamport time: " + clock.getTime());
    }
}