package converter;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            Calculator stub = (Calculator) registry.lookup("CalculatorService");

            System.out.println("Multiplicacion: " + stub.multiply(5, 3));
            System.out.println("Division: " + stub.divide(10, 2));
            System.out.println("Potencia: " + stub.power(2, 4));
            System.out.println("Suma: " + stub.add(15, 7));
            System.out.println("Resta: " + stub.subtract(20, 8));
            System.out.println("Raiz cuadrada (144): " + stub.sqrt(144));
            System.out.println("Modulo (10 % 3): " + stub.modulo(10, 3));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
