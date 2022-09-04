import grpc.InventoryGrpcServiceClient;
import service.InventoryService;

import java.util.Scanner;

public class InventoryClient {
    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println("Usage executable-name <port>");
        }
        int serverPort = Integer.parseInt(args[0]);

        System.out.println("-----------------------------------------------");
        System.out.println("---- Welcome to Inventory Management System ---");
        System.out.println("-----------------------------------------------");

        InventoryService inventoryService = new InventoryService(new InventoryGrpcServiceClient("localhost", serverPort));
        Scanner sc = new Scanner(System.in);
        int choice;
        mainLoop:
        while (true) {

            System.out.println("Please Choose Option");
            System.out.println("-----------------------------------------------");
            System.out.println("Enter '1'  - Create Item");
            System.out.println("Enter '2'  - Process Order");
            System.out.println("Enter '3'  - Update Inventory");
            System.out.println("Enter '-1' - Exit Application");
            System.out.println("-----------------------------------------------");

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    createItem(inventoryService, sc);
                    break;
                case 2:
                    processOrder(inventoryService, sc);
                    break;
                case 3:
                    updateInventory(inventoryService, sc);
                    break;
                case -1:
                    System.out.println("-----------------------------------------------");
                    System.out.println("----- Thank You for using the Application -----");
                    System.out.println("-----------------------------------------------");
                    break mainLoop;
                default:
                    System.out.println("Invalid Choice! Choose Between 1, 2, 3, or -1");
            }
        }
    }

    private static void createItem(InventoryService inventoryService, Scanner sc) {
        System.out.println("Enter Item Name");
        String itemName = sc.next();
        System.out.println("Enter Item ID");
        String itemId = sc.next();
        System.out.println("Enter Count");
        Integer count = sc.nextInt();

        inventoryService.createItem(itemName, itemId, count);
    }

    private static void processOrder(InventoryService inventoryService, Scanner sc) {
        System.out.println("Enter Order ID");
        String orderId = sc.next();
        System.out.println("Enter Item ID");
        String itemId = sc.next();
        System.out.println("Enter Count");
        Integer count = sc.nextInt();

        inventoryService.processOrder(orderId, itemId, count);
    }

    private static void updateInventory(InventoryService inventoryService, Scanner sc) {
        System.out.println("Enter Item ID");
        String itemId = sc.next();
        System.out.println("Enter Incoming Count");
        Integer count = sc.nextInt();

        inventoryService.updateInventory(itemId, count);
    }
}
