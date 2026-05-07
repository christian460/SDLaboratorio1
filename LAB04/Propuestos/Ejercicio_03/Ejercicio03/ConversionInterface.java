package ConversionMoneda;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota para el servicio de conversión de moneda.
 * Define los métodos que pueden ser invocados remotamente por el cliente.
 */
public interface ConversionInterface extends Remote {

    /**
     * Convierte un monto en soles peruanos (PEN) a dólares estadounidenses (USD).
     * Tasa fija: 1 USD = 3.75 PEN
     * @param monto El monto en soles a convertir
     * @return El equivalente en dólares
     * @throws RemoteException Si ocurre un error en la invocación remota
     */
    public double convertirADolares(double monto) throws RemoteException;

    /**
     * Convierte un monto en soles peruanos (PEN) a euros (EUR).
     * Tasa fija: 1 EUR = 4.10 PEN
     * @param monto El monto en soles a convertir
     * @return El equivalente en euros
     * @throws RemoteException Si ocurre un error en la invocación remota
     */
    public double convertirAEuros(double monto) throws RemoteException;

    /**
     * Obtiene la tasa de cambio actual de Sol a Dólar.
     * @return La tasa de cambio PEN/USD
     * @throws RemoteException Si ocurre un error en la invocación remota
     */
    public double getTasaDolar() throws RemoteException;

    /**
     * Obtiene la tasa de cambio actual de Sol a Euro.
     * @return La tasa de cambio PEN/EUR
     * @throws RemoteException Si ocurre un error en la invocación remota
     */
    public double getTasaEuro() throws RemoteException;
}
