package TarjetaCredito;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CreditCardInterface extends Remote {

    // Consulta el saldo disponible de la tarjeta
    public double consultarSaldo() throws RemoteException;

    // Realiza un cargo a la tarjeta (compra)
    public boolean realizarCargo(String descripcion, double monto)
        throws RemoteException;

    // Realiza un pago a la tarjeta (abono)
    public boolean realizarPago(double monto) throws RemoteException;

    // Retorna el historial de transacciones
    public List<String> obtenerHistorial() throws RemoteException;

    // Retorna el limite de credito de la tarjeta
    public double obtenerLimiteCredito() throws RemoteException;

}
