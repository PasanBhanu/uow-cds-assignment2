import config.DatabaseConfig;
import grpc.InventoryGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class InventoryServer {

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
    }
}
