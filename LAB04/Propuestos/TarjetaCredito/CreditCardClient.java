package TarjetaCredito;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.List;

public class CreditCardClient {

    public static void main(String[] args) {
        try {
            CreditCardInterface tarjeta = (CreditCardInterface)
                Naming.lookup("rmi://localhost:1099/CreditCardService");

            System.out.println("=== SISTEMA DE TARJETA DE CREDITO RMI ===");
            System.out.printf("Limite de credito : S/. %.2f%n",
                tarjeta.obtenerLimiteCredito());
            System.out.printf("Saldo disponible  : S/. %.2f%n",
                tarjeta.consultarSaldo());

            // Realizar cargos
            boolean ok1 = tarjeta.realizarCargo("Supermercado Wong", 250.00);
            System.out.println("Cargo S/. 250.00 (Supermercado): " + (ok1 ? "APROBADO" : "DENEGADO"));

            boolean ok2 = tarjeta.realizarCargo("Laptop Dell", 3500.00);
            System.out.println("Cargo S/. 3500.00 (Laptop): " + (ok2 ? "APROBADO" : "DENEGADO"));

            boolean ok3 = tarjeta.realizarCargo("Hotel 5 estrellas", 2000.00);
            System.out.println("Cargo S/. 2000.00 (Hotel): " + (ok3 ? "APROBADO" : "DENEGADO"));

            System.out.printf("%nSaldo tras cargos : S/. %.2f%n",
                tarjeta.consultarSaldo());

            // Realizar pago
            tarjeta.realizarPago(500.00);
            System.out.println("Pago S/. 500.00 realizado.");
            System.out.printf("Saldo tras pago   : S/. %.2f%n",
                tarjeta.consultarSaldo());

            // Mostrar historial
            System.out.println("");
            System.out.println("=== HISTORIAL DE TRANSACCIONES ===");
            List<String> historial = tarjeta.obtenerHistorial();
            for (String entrada : historial) {
                System.out.println("  " + entrada);
            }

        } catch (MalformedURLException murle) {
            System.out.println("MalformedURLException: " + murle);
        } catch (RemoteException re) {
            System.out.println("RemoteException: " + re);
        } catch (NotBoundException nbe) {
            System.out.println("NotBoundException: " + nbe);
        }
    }
}