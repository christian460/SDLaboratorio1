package TarjetaCredito;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class CreditCardServer {

    public CreditCardServer() {
        try {
            LocateRegistry.createRegistry(1099);
            CreditCardInterface tarjeta = new CreditCardImpl();
            Naming.rebind("rmi://localhost:1099/CreditCardService", tarjeta);
            System.out.println("[SERVER] Servicio de tarjeta de credito activo.");
            System.out.println("[SERVER] Escuchando en rmi://localhost:1099/CreditCardService");
        } catch (Exception e) {
            System.out.println("[SERVER] Error: " + e);
        }
    }

    public static void main(String[] args) {
        new CreditCardServer();
    }
}
