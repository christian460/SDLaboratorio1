package converter;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class CalculatorImpl extends UnicastRemoteObject implements Calculator {

    public CalculatorImpl() throws RemoteException {
        super();
    }

    public double multiply(double a, double b) {
        System.out.println("[INFO] Operación solicitada: MULTIPLICACIÓN | Valores: " + a + " * " + b);
        return a * b;
    }

    public double divide(double a, double b) {
        System.out.println("[INFO] Operación solicitada: DIVISIÓN | Valores: " + a + " / " + b);
        if (b == 0) {
            System.err.println("[ERROR] Intento de división entre 0.");
            throw new ArithmeticException("No se puede dividir entre 0");
        }
        return a / b;
    }

    public double power(double a, double b) {
        System.out.println("[INFO] Operación solicitada: POTENCIA | Valores: " + a + " ^ " + b);
        return Math.pow(a, b);
    }

    public double add(double a, double b) {
        System.out.println("[INFO] Operación solicitada: SUMA | Valores: " + a + " + " + b);
        return a + b;
    }

    public double subtract(double a, double b) {
        System.out.println("[INFO] Operación solicitada: RESTA | Valores: " + a + " - " + b);
        return a - b;
    }

    public double sqrt(double a) {
        System.out.println("[INFO] Operación solicitada: RAÍZ CUADRADA | Valor: √" + a);
        if (a < 0) {
            System.err.println("[ERROR] Intento de raíz cuadrada de un número negativo: " + a);
            throw new ArithmeticException("No se puede calcular la raiz de un numero negativo");
        }
        return Math.sqrt(a);
    }

    public double modulo(double a, double b) {
        System.out.println("[INFO] Operación solicitada: MÓDULO | Valores: " + a + " % " + b);
        return a % b;
    }
}