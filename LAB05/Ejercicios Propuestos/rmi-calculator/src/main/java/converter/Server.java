package converter;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            CalculatorImpl obj = new CalculatorImpl();

            Registry registry = LocateRegistry.createRegistry(1099);

            registry.rebind("CalculatorService", obj);

            System.out.println("Servidor RMI activo en puerto 1099");

            // Mantener el hilo principal vivo para que Maven no cierre el servidor
            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}