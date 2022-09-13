import grpc.InventoryGrpcServiceClient;
import iit.uow.nameserver.LoadBalancerClient;
import service.InventoryService;

import java.io.IOException;
import java.util.Scanner;

public class InventoryClient {

    public static final String NAME_SERVICE_ADDRESS = "http://localhost:2379";

    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println("Usage executable-name <port>");
        }
        int serverPort = Integer.parseInt(args[0]);

        System.out.println("-----------------------------------------------");
        System.out.println("---- Welcome to Inventory Management System ---");
        System.out.println("-----------------------------------------------");

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
                    createItem(sc);
                    break;
                case 2:
                    processOrder(sc);
                    break;
                case 3:
                    updateInventory(sc);
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

    private static void createItem(Scanner sc) {
        InventoryService inventoryService = fetchServer();
        if (inventoryService == null) {
            System.out.println("Unable to connect with Server! Exiting application...");
            return;
        }

        System.out.println("Enter Item Name");
        String itemName = sc.next();
        System.out.println("Enter Item ID");
        String itemId = sc.next();
        System.out.println("Enter Count");
        Integer count = sc.nextInt();

        inventoryService.createItem(itemName, itemId, count);
    }

    private static void processOrder(Scanner sc) {
        InventoryService inventoryService = fetchServer();
        if (inventoryService == null) {
            System.out.println("Unable to connect with Server! Exiting application...");
            return;
        }

        System.out.println("Enter Order ID");
        String orderId = sc.next();
        System.out.println("Enter Item ID");
        String itemId = sc.next();
        System.out.println("Enter Count");
        Integer count = sc.nextInt();

        inventoryService.processOrder(orderId, itemId, count);
    }

    private static void updateInventory(Scanner sc) {
        InventoryService inventoryService = fetchServer();
        if (inventoryService == null) {
            System.out.println("Unable to connect with Server! Exiting application...");
            return;
        }

        System.out.println("Enter Item ID");
        String itemId = sc.next();
        System.out.println("Enter Incoming Count");
        Integer count = sc.nextInt();

        inventoryService.updateInventory(itemId, count);
    }

    private static InventoryService fetchServer() {
        System.out.println("Acquiring server through ETCD");
        try {
            LoadBalancerClient client = new LoadBalancerClient(NAME_SERVICE_ADDRESS);
            LoadBalancerClient.ServiceDetails serviceDetails = client.findService("service.InventoryService");
            String host = serviceDetails.getIpAddress();
            Integer port = serviceDetails.getPort();

            System.out.println("Server acquired! Host: " + host + " Port: " + port);

            return new InventoryService(new InventoryGrpcServiceClient(host, port));
        } catch (IOException e) {
            System.out.println("Server acquire failed!");
        } catch (InterruptedException e) {
            System.out.println("Server acquire failed!");
        }

        return null;
    }
}
