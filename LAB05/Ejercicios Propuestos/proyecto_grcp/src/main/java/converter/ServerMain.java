package converter;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ServerMain {

    public static void main(String[] args) throws Exception {

        Server server = ServerBuilder
                .forPort(50051)
                .addService(new ConverterServiceImpl())
                .build();

        server.start();

        System.out.println("Servidor iniciado en puerto 50051");

        server.awaitTermination();
    }
}