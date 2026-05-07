package ConversionMoneda;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementación del servicio remoto de conversión de moneda.
 * Extiende UnicastRemoteObject para ser exportado como objeto remoto.
 *
 * Tasas fijas utilizadas:
 *   - 1 USD = 3.75 PEN (Soles)
 *   - 1 EUR = 4.10 PEN (Soles)
 */
public class ConversionImpl extends UnicastRemoteObject implements ConversionInterface {

    // Tasas de cambio fijas (PEN -> moneda extranjera)
    private static final double TASA_DOLAR = 3.75; // 1 USD = 3.75 Soles
    private static final double TASA_EURO  = 4.10; // 1 EUR = 4.10 Soles

    /**
     * Constructor explícito requerido para declarar RemoteException.
     * Llama a super() de UnicastRemoteObject para exportar el objeto.
     */
    public ConversionImpl() throws RemoteException {
        super();
    }

    /**
     * Convierte soles a dólares usando tasa fija.
     * Fórmula: dólares = monto / TASA_DOLAR
     */
    @Override
    public double convertirADolares(double monto) throws RemoteException {
        if (monto < 0) {
            throw new RemoteException("El monto no puede ser negativo.");
        }
        double resultado = monto / TASA_DOLAR;
        // Redondear a 2 decimales
        return Math.round(resultado * 100.0) / 100.0;
    }

    /**
     * Convierte soles a euros usando tasa fija.
     * Fórmula: euros = monto / TASA_EURO
     */
    @Override
    public double convertirAEuros(double monto) throws RemoteException {
        if (monto < 0) {
            throw new RemoteException("El monto no puede ser negativo.");
        }
        double resultado = monto / TASA_EURO;
        // Redondear a 2 decimales
        return Math.round(resultado * 100.0) / 100.0;
    }

    @Override
    public double getTasaDolar() throws RemoteException {
        return TASA_DOLAR;
    }

    @Override
    public double getTasaEuro() throws RemoteException {
        return TASA_EURO;
    }
}
