package LAB02.Berkeley;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class BerkeleyClock {
    private int time;

    public BerkeleyClock(int time) {
        this.time = time;
    }

    public synchronized int getTime() {
        return time;
    }

    public synchronized void adjustTime(int offset) {
        this.time += offset;
    }
}

public class BerkeleyAlgorithm {

    public static void main(String[] args) {

        List<Thread> threads = new ArrayList<>();
        List<BerkeleyClock> clocks = new ArrayList<>();
        Random rand = new Random();

        // Crear "procesos" con tiempos distintos
        for (int i = 0; i < 5; i++) {
            clocks.add(new BerkeleyClock(90 + rand.nextInt(20))); // tiempos entre 90 y 110
        }

        System.out.println("Tiempos iniciales:");
        for (int i = 0; i < clocks.size(); i++) {
            System.out.println("P" + i + ": " + clocks.get(i).getTime());
        }

        // Maestro (P0)
        BerkeleyClock master = clocks.get(0);

        // Calcular diferencias
        List<Integer> offsets = new ArrayList<>();
        int sum = 0;

        for (BerkeleyClock c : clocks) {
            int diff = c.getTime() - master.getTime();
            offsets.add(diff);
            sum += diff;
        }

        int average = sum / clocks.size();

        // Crear threads para ajustar tiempos
        for (int i = 0; i < clocks.size(); i++) {
            final int index = i;

            Thread thread = new Thread(() -> {
                BerkeleyClock c = clocks.get(index);

                int adjustment = average - offsets.get(index);

                System.out.println("P" + index + " ajuste: " + adjustment);

                try {
                    Thread.sleep(rand.nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                c.adjustTime(adjustment);

                System.out.println("P" + index + " nuevo tiempo: " + c.getTime());
            });

            threads.add(thread);
            thread.start();
        }

        // Esperar a que todos terminen
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nTiempos sincronizados:");
        for (int i = 0; i < clocks.size(); i++) {
            System.out.println("P" + i + ": " + clocks.get(i).getTime());
        }
    }
}