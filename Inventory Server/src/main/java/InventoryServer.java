import config.DatabaseConfig;
import grpc.InventoryGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class InventoryServer {

    public static final String NAME_SERVICE_ADDRESS = "http://localhost:2379";

    private int serverPort;

    public InventoryServer(String host, int port){
        this.serverPort = port;
    }

    public void startServer() throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(serverPort)
                .addService(new InventoryGrpcService())
                .build();
        server.start();

        System.out.println("Inventory Server started on: " + serverPort);

        server.awaitTermination();
    }

    public static void main (String[] args) throws Exception{
        if (args.length != 1) {
            System.out.println("Usage executable-name <port>");
        }
        int serverPort = Integer.parseInt(args[0]);

        // Initialize Database
        DatabaseConfig.initializeDatabase();

        // Initialize Server
        InventoryServer server = new InventoryServer("localhost", serverPort);
        server.startServer();

        // Initialize ETCD
        LoadBalancerClient client = new LoadBalancerClient(NAME_SERVICE_ADDRESS);
        client.registerService("InventoryService", "127.0.0.1", serverPort, "tcp");

        Thread printingHook = new Thread(() -> {
            try {
                client.unregisterService("InventoryService");
                System.out.println("Service removed");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Runtime.getRuntime().addShutdownHook(printingHook);
    }
}
