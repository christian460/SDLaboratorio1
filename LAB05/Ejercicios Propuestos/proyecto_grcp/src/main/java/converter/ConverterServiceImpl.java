package converter;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

public class ConverterServiceImpl
        extends ConverterGrpc.ConverterImplBase {

    private static final Logger logger =
            Logger.getLogger(ConverterServiceImpl.class.getName());

    // ----------------------------------------------------
    //  1. Celsius -> Fahrenheit
    // ----------------------------------------------------
    @Override
    public void celsiusToFahrenheit(
            TemperatureRequest request,
            StreamObserver<ConvertResponse> responseObserver) {

        double celsius = request.getCelsius();
        logger.info("[CelsiusToFahrenheit] Peticion recibida: " + celsius + " C");

        // Validacion: cero absoluto
        if (celsius < -273.15) {
            String err = "Temperatura invalida: " + celsius
                    + " C es menor al cero absoluto (-273.15 C)";
            logger.warning("[CelsiusToFahrenheit] ERROR - " + err);
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(err).asRuntimeException());
            return;
        }

        double result = (celsius * 1.8) + 32;
        logger.info("[CelsiusToFahrenheit] Resultado: " + result + " F");

        responseObserver.onNext(ConvertResponse.newBuilder()
                .setResult(result)
                .setMessage("Conversion exitosa: " + celsius + " C = " + result + " F")
                .build());
        responseObserver.onCompleted();
    }

    // ----------------------------------------------------
    //  2. Soles -> Dolares
    // ----------------------------------------------------
    @Override
    public void solesToDollars(
            MoneyRequest request,
            StreamObserver<ConvertResponse> responseObserver) {

        double soles = request.getSoles();
        logger.info("[SolesToDollars] Peticion recibida: S/. " + soles);

        // Validacion: monto negativo
        if (soles < 0) {
            String err = "Monto invalido: no se permiten valores negativos (" + soles + ")";
            logger.warning("[SolesToDollars] ERROR - " + err);
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(err).asRuntimeException());
            return;
        }

        double result = Math.round(soles * 0.27 * 100.0) / 100.0;
        logger.info("[SolesToDollars] Resultado: $ " + result);

        responseObserver.onNext(ConvertResponse.newBuilder()
                .setResult(result)
                .setMessage("Conversion exitosa: S/. " + soles + " = $ " + result)
                .build());
        responseObserver.onCompleted();
    }

    // ----------------------------------------------------
    //  3. Kilometros -> Millas
    // ----------------------------------------------------
    @Override
    public void kilometersToMiles(
            DistanceRequest request,
            StreamObserver<ConvertResponse> responseObserver) {

        double km = request.getKilometers();
        logger.info("[KilometersToMiles] Peticion recibida: " + km + " km");

        // Validacion: distancia negativa
        if (km < 0) {
            String err = "Distancia invalida: no se permiten valores negativos (" + km + ")";
            logger.warning("[KilometersToMiles] ERROR - " + err);
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(err).asRuntimeException());
            return;
        }

        double result = Math.round(km * 0.621371 * 1000.0) / 1000.0;
        logger.info("[KilometersToMiles] Resultado: " + result + " mi");

        responseObserver.onNext(ConvertResponse.newBuilder()
                .setResult(result)
                .setMessage("Conversion exitosa: " + km + " km = " + result + " mi")
                .build());
        responseObserver.onCompleted();
    }

    // ----------------------------------------------------
    //  4. Kilogramos -> Libras  (NUEVA)
    // ----------------------------------------------------
    @Override
    public void kilogramsToPounds(
            WeightRequest request,
            StreamObserver<ConvertResponse> responseObserver) {

        double kg = request.getKilograms();
        logger.info("[KilogramsToPounds] Peticion recibida: " + kg + " kg");

        // Validacion: peso negativo
        if (kg < 0) {
            String err = "Peso invalido: no se permiten valores negativos (" + kg + ")";
            logger.warning("[KilogramsToPounds] ERROR - " + err);
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(err).asRuntimeException());
            return;
        }

        double result = Math.round(kg * 2.20462 * 1000.0) / 1000.0;
        logger.info("[KilogramsToPounds] Resultado: " + result + " lb");

        responseObserver.onNext(ConvertResponse.newBuilder()
                .setResult(result)
                .setMessage("Conversion exitosa: " + kg + " kg = " + result + " lb")
                .build());
        responseObserver.onCompleted();
    }

    // ----------------------------------------------------
    //  5. Litros -> Galones  (NUEVA)
    // ----------------------------------------------------
    @Override
    public void litersToGallons(
            VolumeRequest request,
            StreamObserver<ConvertResponse> responseObserver) {

        double liters = request.getLiters();
        logger.info("[LitersToGallons] Peticion recibida: " + liters + " L");

        // Validacion: volumen negativo
        if (liters < 0) {
            String err = "Volumen invalido: no se permiten valores negativos (" + liters + ")";
            logger.warning("[LitersToGallons] ERROR - " + err);
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(err).asRuntimeException());
            return;
        }

        double result = Math.round(liters * 0.264172 * 1000.0) / 1000.0;
        logger.info("[LitersToGallons] Resultado: " + result + " gal");

        responseObserver.onNext(ConvertResponse.newBuilder()
                .setResult(result)
                .setMessage("Conversion exitosa: " + liters + " L = " + result + " gal")
                .build());
        responseObserver.onCompleted();
    }

    // ----------------------------------------------------
    //  6. Metros -> Pies  (NUEVA)
    // ----------------------------------------------------
    @Override
    public void metersToFeet(
            LengthRequest request,
            StreamObserver<ConvertResponse> responseObserver) {

        double meters = request.getMeters();
        logger.info("[MetersToFeet] Peticion recibida: " + meters + " m");

        // Validacion: longitud negativa
        if (meters < 0) {
            String err = "Longitud invalida: no se permiten valores negativos (" + meters + ")";
            logger.warning("[MetersToFeet] ERROR - " + err);
            responseObserver.onError(
                    Status.INVALID_ARGUMENT.withDescription(err).asRuntimeException());
            return;
        }

        double result = Math.round(meters * 3.28084 * 1000.0) / 1000.0;
        logger.info("[MetersToFeet] Resultado: " + result + " ft");

        responseObserver.onNext(ConvertResponse.newBuilder()
                .setResult(result)
                .setMessage("Conversion exitosa: " + meters + " m = " + result + " ft")
                .build());
        responseObserver.onCompleted();
    }
}