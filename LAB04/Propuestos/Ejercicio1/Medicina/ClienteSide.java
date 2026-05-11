package LAB04.Propuestos.Ejercicio1.Medicina;

import java.rmi.Naming;
import java.util.HashMap;
import java.util.Scanner;

public class ClienteSide {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        StockInterface pharm =
                (StockInterface) Naming.lookup("rmi://localhost/PHARMACY");

        System.out.println("Ingrese la opcion");
        System.out.println("1: Listar productos");
        System.out.println("2: Comprar producto");

        int selection = sc.nextInt();

        if (selection == 1) {

            HashMap<String, MedicineInterface> aux =
                    pharm.getStockProducts();

            for (String key : aux.keySet()) {

                MedicineInterface e = aux.get(key);

                System.out.println(e.print());
                System.out.println("*-----------------*");
            }

        } else if (selection == 2) {

            System.out.println("Ingrese nombre de la medicina:");
            String medicine = sc.next();

            System.out.println("Ingrese cantidad a comprar:");
            int amount = sc.nextInt();

            MedicineInterface aux =
                    pharm.buyMedicine(medicine, amount);

            System.out.println("Usted acaba de comprar:");
            System.out.println(aux.print());

        } else {

            System.out.println("Seleccione una opcion valida");
        }

        sc.close();
    }
}