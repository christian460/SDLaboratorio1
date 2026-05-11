package TarjetaCredito;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaccion implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Tipo { CARGO, PAGO, CONSULTA }

    private final Tipo        tipo;
    private final double      monto;
    private final String      descripcion;
    private final LocalDateTime fecha;

    public Transaccion(Tipo tipo, double monto, String descripcion) {
        this.tipo        = tipo;
        this.monto       = monto;
        this.descripcion = descripcion;
        this.fecha       = LocalDateTime.now();
    }

    public Tipo        getTipo()        { return tipo; }
    public double      getMonto()       { return monto; }
    public String      getDescripcion() { return descripcion; }
    public LocalDateTime getFecha()     { return fecha; }

    @Override
    public String toString() {
        DateTimeFormatter fmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("[%s] %s: %s - S/. %.2f",
            fecha.format(fmt), tipo, descripcion, monto);
    }
}
