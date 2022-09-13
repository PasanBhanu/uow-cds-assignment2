package grpc;

import iit.uow.communication.grpc.generated.InventoryServiceGrpc;
import model.Item;
import model.Order;
import service.InventoryService;
import service.InventoryServiceDistributed;

import java.util.Map;

public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase{

    @Override
    public void createItem(iit.uow.communication.grpc.generated.CreateItemRequest request,
                           io.grpc.stub.StreamObserver<iit.uow.communication.grpc.generated.CreateItemResponse> responseObserver) {

        System.out.println("Request received to Create Item: " + request.getItemId());
        Item item = new Item();
        item.setItemId(request.getItemId());
        item.setItemName(request.getItemName());
        item.setCount(request.getCount());

        InventoryService service = new InventoryService();
        Map<String, String> serviceResponse = service.createItem(item);

        iit.uow.communication.grpc.generated.CreateItemResponse response = iit.uow.communication.grpc.generated.CreateItemResponse
                .newBuilder()
                .setStatus(serviceResponse.get("status"))
                .setMessage(serviceResponse.get("message"))
                .build();

        System.out.println("Responding to Create Item: " + request.getItemId());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void processOrder(iit.uow.communication.grpc.generated.ProcessOrderRequest request,
                             io.grpc.stub.StreamObserver<iit.uow.communication.grpc.generated.ProcessOrderResponse> responseObserver) {
        System.out.println("Request received to Process Order: " + request.getItemId());
        Order order = new Order();
        order.setOrderNumber(request.getOrderNumber());
        order.setItemId(request.getItemId());
        order.setCount(request.getCount());

        InventoryServiceDistributed service = new InventoryServiceDistributed();
        Map<String, String> serviceResponse = service.processOrder(order);

        iit.uow.communication.grpc.generated.ProcessOrderResponse response = iit.uow.communication.grpc.generated.ProcessOrderResponse
                .newBuilder()
                .setStatus(serviceResponse.get("status"))
                .setMessage(serviceResponse.get("message"))
                .build();

        System.out.println("Responding to Process Order: " + request.getOrderNumber());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateInventory(iit.uow.communication.grpc.generated.UpdateInventoryRequest request,
                                io.grpc.stub.StreamObserver<iit.uow.communication.grpc.generated.UpdateInventoryResponse> responseObserver) {
        System.out.println("Request received to Update Inventory of Item: " + request.getItemId());
        Item item = new Item();
        item.setItemId(request.getItemId());
        item.setCount(request.getCount());

        InventoryServiceDistributed service = new InventoryServiceDistributed();
        Map<String, String> serviceResponse = service.updateInventory(item);

        iit.uow.communication.grpc.generated.UpdateInventoryResponse response = iit.uow.communication.grpc.generated.UpdateInventoryResponse
                .newBuilder()
                .setStatus(serviceResponse.get("status"))
                .setMessage(serviceResponse.get("message"))
                .build();

        System.out.println("Responding to Update Inventory of Item: " + request.getItemId());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
