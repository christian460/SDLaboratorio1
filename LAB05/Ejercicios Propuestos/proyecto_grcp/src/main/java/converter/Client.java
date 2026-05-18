package converter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class Client {

    public static void main(String[] args) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        ConverterGrpc.ConverterBlockingStub stub =
                ConverterGrpc.newBlockingStub(channel);

        System.out.println("==========================================");
        System.out.println("  Cliente gRPC - Servicio de Conversion   ");
        System.out.println("==========================================\n");

        // -- 1. Celsius -> Fahrenheit ----------------
        llamarConversion("Temperatura: 25 C -> F", () -> {
            ConvertResponse r = stub.celsiusToFahrenheit(
                    TemperatureRequest.newBuilder().setCelsius(25).build());
            System.out.println("  [OK] " + r.getMessage());
        });

        // -- 2. Celsius invalido (bajo cero absoluto) -
        llamarConversion("Temperatura invalida: -300 C", () -> {
            stub.celsiusToFahrenheit(
                    TemperatureRequest.newBuilder().setCelsius(-300).build());
        });

        // -- 3. Soles -> Dolares ---------------------
        llamarConversion("Moneda: S/. 100 -> USD", () -> {
            ConvertResponse r = stub.solesToDollars(
                    MoneyRequest.newBuilder().setSoles(100).build());
            System.out.println("  [OK] " + r.getMessage());
        });

        // -- 4. Soles negativos (invalido) -----------
        llamarConversion("Moneda invalida: S/. -50", () -> {
            stub.solesToDollars(
                    MoneyRequest.newBuilder().setSoles(-50).build());
        });

        // -- 5. Kilometros -> Millas -----------------
        llamarConversion("Distancia: 10 km -> mi", () -> {
            ConvertResponse r = stub.kilometersToMiles(
                    DistanceRequest.newBuilder().setKilometers(10).build());
            System.out.println("  [OK] " + r.getMessage());
        });

        // -- 6. Kilogramos -> Libras -----------------
        llamarConversion("Peso: 70 kg -> lb", () -> {
            ConvertResponse r = stub.kilogramsToPounds(
                    WeightRequest.newBuilder().setKilograms(70).build());
            System.out.println("  [OK] " + r.getMessage());
        });

        // -- 7. Litros -> Galones --------------------
        llamarConversion("Volumen: 5 L -> gal", () -> {
            ConvertResponse r = stub.litersToGallons(
                    VolumeRequest.newBuilder().setLiters(5).build());
            System.out.println("  [OK] " + r.getMessage());
        });

        // -- 8. Metros -> Pies -----------------------
        llamarConversion("Longitud: 3 m -> ft", () -> {
            ConvertResponse r = stub.metersToFeet(
                    LengthRequest.newBuilder().setMeters(3).build());
            System.out.println("  [OK] " + r.getMessage());
        });

        // -- 9. Metros negativos (invalido) ----------
        llamarConversion("Longitud invalida: -1 m", () -> {
            stub.metersToFeet(
                    LengthRequest.newBuilder().setMeters(-1).build());
        });

        System.out.println("\n==========================================");
        System.out.println("    Todas las peticiones completadas.     ");
        System.out.println("==========================================");

        channel.shutdown();
    }

    /**
     * Ejecuta una peticion gRPC y captura errores de validacion
     * mostrando el mensaje del servidor de forma clara.
     */
    private static void llamarConversion(String titulo, Runnable peticion) {
        System.out.println("--- " + titulo);
        try {
            peticion.run();
        } catch (StatusRuntimeException e) {
            System.out.println("  [ERROR] " + e.getStatus().getDescription());
        }
        System.out.println();
    }
}