package ConversionMoneda;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.util.Scanner;

/**
 * Cliente RMI para el servicio de conversión de moneda.
 *
 * Se conecta al servidor RMI, busca el objeto remoto "ConversionService"
 * y llama sus métodos como si fueran locales.
 *
 * Ejecución (en una consola separada, con el servidor ya activo):
 *   java ConversionMoneda.ConversionClient
 *
 * También se puede ejecutar pasando el monto como argumento:
 *   java ConversionMoneda.ConversionClient 100.50
 */
public class ConversionClient {

    public static void main(String[] args) {
        try {
            // -------------------------------------------------------
            // 1. Localizar el objeto remoto en el registro RMI
            // -------------------------------------------------------
            System.out.println("[CLIENT] Conectando al servicio RMI...");
            ConversionInterface servicio =
                (ConversionInterface) Naming.lookup("rmi://localhost:1099/ConversionService");
            System.out.println("[CLIENT] Conexion establecida con 'ConversionService'.\n");

            // -------------------------------------------------------
            // 2. Mostrar tasas de cambio vigentes (llamadas remotas)
            // -------------------------------------------------------
            System.out.println("=== TASAS DE CAMBIO VIGENTES ===");
            System.out.printf("  Tasa USD: 1 USD = %.2f PEN%n", servicio.getTasaDolar());
            System.out.printf("  Tasa EUR: 1 EUR = %.2f PEN%n", servicio.getTasaEuro());
            System.out.println("================================\n");

            // -------------------------------------------------------
            // 3. Obtener el monto a convertir
            // -------------------------------------------------------
            double monto;
            if (args.length > 0) {
                // Monto pasado como argumento de línea de comandos
                monto = Double.parseDouble(args[0]);
                System.out.printf("[CLIENT] Monto recibido como argumento: S/. %.2f%n%n", monto);
            } else {
                // Monto ingresado de forma interactiva
                Scanner sc = new Scanner(System.in);
                System.out.print("Ingrese el monto en Soles (PEN) a convertir: S/. ");
                monto = sc.nextDouble();
                sc.close();
                System.out.println();
            }

            // -------------------------------------------------------
            // 4. Llamar a los métodos remotos de conversión
            //    La sintaxis RMI: result = remoteInterface.method(args)
            // -------------------------------------------------------
            double enDolares = servicio.convertirADolares(monto);
            double enEuros   = servicio.convertirAEuros(monto);

            // -------------------------------------------------------
            // 5. Mostrar resultados
            // -------------------------------------------------------
            System.out.println("=== RESULTADO DE CONVERSION ===");
            System.out.printf("  Monto original  : S/.  %.2f PEN%n", monto);
            System.out.printf("  Equivalente USD : $    %.2f USD%n", enDolares);
            System.out.printf("  Equivalente EUR : EUR  %.2f EUR%n", enEuros);
            System.out.println("================================");

        } catch (MalformedURLException murle) {
            System.err.println("[CLIENT] Error: URL del servicio RMI mal formada.");
            System.err.println(murle.getMessage());

        } catch (NotBoundException nbe) {
            System.err.println("[CLIENT] Error: El servicio 'ConversionService' no esta registrado.");
            System.err.println("Asegurese de que el servidor este en ejecucion.");
            System.err.println(nbe.getMessage());

        } catch (RemoteException re) {
            System.err.println("[CLIENT] Error remoto al invocar el metodo.");
            System.err.println(re.getMessage());

        } catch (NumberFormatException nfe) {
            System.err.println("[CLIENT] Error: El argumento proporcionado no es un numero valido.");

        } catch (Exception e) {
            System.err.println("[CLIENT] Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
