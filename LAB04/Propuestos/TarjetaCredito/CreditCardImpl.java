package TarjetaCredito;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreditCardImpl extends UnicastRemoteObject
                           implements CreditCardInterface {

    private double limiteCredito;
    private double saldoDisponible;
    private List<String> historial;
    private static final DateTimeFormatter fmt =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public CreditCardImpl() throws RemoteException {
        super();
        this.limiteCredito    = 5000.00;
        this.saldoDisponible  = 5000.00;
        this.historial        = new ArrayList<>();
        historial.add("[SISTEMA] Tarjeta activada. Limite: S/. 5000.00");
    }

    @Override
    public double consultarSaldo() throws RemoteException {
        return saldoDisponible;
    }

    @Override
    public boolean realizarCargo(String descripcion, double monto)
            throws RemoteException {
        if (monto <= 0) throw new RemoteException("Monto invalido");
        if (monto > saldoDisponible) {
            historial.add(String.format("[%s] DENEGADO: %s - S/. %.2f",
                LocalDateTime.now().format(fmt), descripcion, monto));
            return false;
        }
        saldoDisponible -= monto;
        historial.add(String.format("[%s] CARGO: %s - S/. %.2f",
            LocalDateTime.now().format(fmt), descripcion, monto));
        return true;
    }

    @Override
    public boolean realizarPago(double monto) throws RemoteException {
        if (monto <= 0) throw new RemoteException("Monto invalido");
        double nuevoPago = Math.min(monto, limiteCredito - saldoDisponible);
        saldoDisponible += nuevoPago;
        historial.add(String.format("[%s] PAGO recibido: S/. %.2f",
            LocalDateTime.now().format(fmt), nuevoPago));
        return true;
    }

    @Override
    public List<String> obtenerHistorial() throws RemoteException {
        return new ArrayList<>(historial);
    }

    @Override
    public double obtenerLimiteCredito() throws RemoteException {
        return limiteCredito;
    }
}