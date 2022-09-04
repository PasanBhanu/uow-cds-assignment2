package grpc;

import iit.uow.communication.grpc.generated.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class InventoryGrpcServiceClient {
    private ManagedChannel channel = null;
    InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceBlockingStub = null;
    String host = null;
    int port = -1;

    public InventoryGrpcServiceClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void initializeConnection () {
        System.out.println("Client Connection to " + host + ":" + port);
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        inventoryServiceBlockingStub = InventoryServiceGrpc.newBlockingStub(channel);
    }

    public void closeConnection() {
        channel.shutdown();
    }

    public void createItem(String itemName, String itemId, Integer count) throws InterruptedException {
        System.out.println("Request sent to Create Item: " + itemId);

        CreateItemRequest request = CreateItemRequest
                .newBuilder()
                .setItemId(itemId)
                .setItemName(itemName)
                .setCount(count)
                .build();

        CreateItemResponse response = inventoryServiceBlockingStub.createItem(request);

        System.out.println("Response received to Create Item: [" + response.getStatus() + "] " + response.getMessage());
        System.out.println("");
        Thread.sleep(1000);
    }

    public void processOrder(String orderId, String itemId, Integer count) throws InterruptedException {
        System.out.println("Request sent to Process Order: " + orderId);

        ProcessOrderRequest request = ProcessOrderRequest
                .newBuilder()
                .setOrderNumber(orderId)
                .setItemId(itemId)
                .setCount(count)
                .build();

        ProcessOrderResponse response = inventoryServiceBlockingStub.processOrder(request);

        System.out.println("Response received to Process Order: [" + response.getStatus() + "] " + response.getMessage());
        System.out.println("");
        Thread.sleep(1000);
    }

    public void updateInventory(String itemId, Integer count) throws InterruptedException {
        System.out.println("Request sent to Update Inventory: " + itemId);

        UpdateInventoryRequest request = UpdateInventoryRequest
                .newBuilder()
                .setItemId(itemId)
                .setCount(count)
                .build();

        UpdateInventoryResponse response = inventoryServiceBlockingStub.updateInventory(request);

        System.out.println("Response received to Update Inventory: [" + response.getStatus() + "] " + response.getMessage());
        System.out.println("");
        Thread.sleep(1000);
    }
}
