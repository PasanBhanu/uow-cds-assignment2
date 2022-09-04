package service;


import grpc.InventoryGrpcServiceClient;

public class InventoryService {
    final InventoryGrpcServiceClient inventoryGrpcServiceClient;

    public InventoryService(InventoryGrpcServiceClient inventoryGrpcServiceClient) {
        this.inventoryGrpcServiceClient = inventoryGrpcServiceClient;
    }

    public void createItem(String itemName, String itemId, Integer count) {
        if (itemName == null || itemId == null || count == null){
            System.out.println("Invalid Data! Request Failed.");
            return;
        }
        if (count <= 0) {
            System.out.println("Item count should be greater than 0.");
            return;
        }

        try {
            inventoryGrpcServiceClient.initializeConnection();
            inventoryGrpcServiceClient.createItem(itemName, itemId, count);
            inventoryGrpcServiceClient.closeConnection();
        } catch (InterruptedException ex) {
            System.out.println("Communication with Server Failed!");
        }
    }

    public void processOrder(String orderId, String itemId, Integer count) {
        if (orderId == null || itemId == null || count == null){
            System.out.println("Invalid Data! Request Failed.");
            return;
        }
        if (count <= 0) {
            System.out.println("Item count should be greater than 0.");
            return;
        }

        try {
            inventoryGrpcServiceClient.initializeConnection();
            inventoryGrpcServiceClient.processOrder(orderId, itemId, count);
            inventoryGrpcServiceClient.closeConnection();
        } catch (InterruptedException ex) {
            System.out.println("Communication with Server Failed!");
        }
    }

    public void updateInventory(String itemId, Integer count) {
        if (itemId == null || count == null){
            System.out.println("Invalid Data! Request Failed.");
            return;
        }
        if (count <= 0) {
            System.out.println("Item count should be greater than 0.");
            return;
        }

        try {
            inventoryGrpcServiceClient.initializeConnection();
            inventoryGrpcServiceClient.updateInventory(itemId, count);
            inventoryGrpcServiceClient.closeConnection();
        } catch (InterruptedException ex) {
            System.out.println("Communication with Server Failed!");
        }
    }
}
