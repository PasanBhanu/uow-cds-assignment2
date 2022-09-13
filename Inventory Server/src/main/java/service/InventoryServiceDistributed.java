package service;

import iit.uow.communication.grpc.generated.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import model.Item;
import model.Order;
import zookeeper.ZookeeperService;

import java.util.HashMap;
import java.util.Map;

public class InventoryServiceDistributed {
    private ManagedChannel channel = null;
    InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceBlockingStub = null;

    public Map<String, String> processOrder(Order order) {
        if (ZookeeperService.isLeader.get()) {
            System.out.println("Request Execution as Leader - Process Order: " + order.getOrderNumber());

            InventoryService service = new InventoryService();
            Map<String, String> serviceResponse = service.processOrder(order);
            return serviceResponse;
        } else {
            String[] leaderData = ZookeeperService.getCurrentLeaderData();
            System.out.println("Request sent to Leader - Process Order: " + order.getOrderNumber());

            initializeConnection(leaderData[0], Integer.parseInt(leaderData[1]));

            ProcessOrderRequest request = ProcessOrderRequest
                    .newBuilder()
                    .setOrderNumber(order.getOrderNumber())
                    .setItemId(order.getItemId())
                    .setCount(order.getCount())
                    .build();

            ProcessOrderResponse response = inventoryServiceBlockingStub.processOrder(request);

            closeConnection();

            System.out.println("Response received to Process Order: [" + response.getStatus() + "] " + response.getMessage());
            System.out.println("");

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("status", response.getStatus());
            responseMap.put("message", response.getMessage());

            return responseMap;
        }
    }

    public Map<String, String> updateInventory(Item item) {
        if (ZookeeperService.isLeader.get()) {
            System.out.println("Request Execution as Leader - Update Inventory: " + item.getItemId());

            InventoryService service = new InventoryService();
            Map<String, String> serviceResponse = service.updateInventory(item);
            return serviceResponse;
        } else {
            String[] leaderData = ZookeeperService.getCurrentLeaderData();
            System.out.println("Request sent to Leader - Update Inventory: " + item.getItemId());

            initializeConnection(leaderData[0], Integer.parseInt(leaderData[1]));

            UpdateInventoryRequest request = UpdateInventoryRequest
                    .newBuilder()
                    .setItemId(item.getItemId())
                    .setCount(item.getCount())
                    .build();

            UpdateInventoryResponse response = inventoryServiceBlockingStub.updateInventory(request);

            closeConnection();

            System.out.println("Response received to Update Inventory: [" + response.getStatus() + "] " + response.getMessage());
            System.out.println("");

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("status", response.getStatus());
            responseMap.put("message", response.getMessage());

            return responseMap;
        }

    }

    private void initializeConnection(String host, Integer port) {
        System.out.println("Server Connection to " + host + ":" + port);
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        inventoryServiceBlockingStub = InventoryServiceGrpc.newBlockingStub(channel);
    }

    private void closeConnection() {
        channel.shutdown();
    }
}
