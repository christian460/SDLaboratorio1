package ConversionMoneda;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Servidor RMI para el servicio de conversión de moneda.
 *
 * Pasos que realiza:
 *   1. Crea una instancia de ConversionImpl (el objeto remoto)
 *   2. Inicia el RMIRegistry en el puerto 1099 (puerto estándar RMI)
 *   3. Registra el objeto remoto bajo el nombre "ConversionService"
 *
 * Ejecución:
 *   javac -d ../out ../ConversionMoneda/*.java
 *   cd ../out
 *   java ConversionMoneda.ConversionServer
 */
public class ConversionServer {

    public static void main(String[] args) {
        try {
            // Crear la implementación del servicio remoto
            ConversionImpl servicio = new ConversionImpl();

            // Iniciar el RMI Registry en el puerto estándar 1099
            // (Equivalente a ejecutar "rmiregistry" en consola)
            LocateRegistry.createRegistry(1099);
            System.out.println("[SERVER] RMI Registry iniciado en puerto 1099.");

            // Registrar el objeto remoto con un nombre lógico
            Naming.rebind("rmi://localhost:1099/ConversionService", servicio);

            System.out.println("[SERVER] Servicio 'ConversionService' registrado exitosamente.");
            System.out.println("[SERVER] Tasas de cambio activas:");
            System.out.println("[SERVER]   1 USD = " + servicio.getTasaDolar() + " PEN");
            System.out.println("[SERVER]   1 EUR = " + servicio.getTasaEuro()  + " PEN");
            System.out.println("[SERVER] Servidor listo. Esperando conexiones de clientes...");

        } catch (Exception e) {
            System.err.println("[SERVER] Error al iniciar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
