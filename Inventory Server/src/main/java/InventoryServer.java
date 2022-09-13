import config.DatabaseConfig;
import grpc.InventoryGrpcService;
import iit.uow.nameserver.DistributedLock;
import iit.uow.nameserver.LoadBalancerClient;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.zookeeper.KeeperException;
import zookeeper.ZookeeperService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryServer {

    public static final String NAME_SERVICE_ADDRESS = "http://localhost:2379";
    public static final String ZOOKEEPER_ADDRESS = "localhost:2181";

    private DistributedLock leaderLock;
    private int serverPort;

    public InventoryServer(String host, int port) throws InterruptedException, IOException, KeeperException {
        this.serverPort = port;
        leaderLock = new DistributedLock("InventoryServerCluster", buildServerData(host, port));
    }

    public void startServer() throws IOException, InterruptedException, KeeperException {
        Server server = ServerBuilder
                .forPort(serverPort)
                .addService(new InventoryGrpcService())
                .build();
        server.start();

        System.out.println("Inventory Server started on: " + serverPort);

        tryToBeLeader();
        server.awaitTermination();
    }

    public static String buildServerData(String ip, int port) {
        StringBuilder builder = new StringBuilder();
        builder.append(ip).append(":").append(port);
        return builder.toString();
    }

    private void tryToBeLeader() throws KeeperException, InterruptedException {
        Thread leaderCampaignThread = new Thread(new LeaderCampaignThread());
        leaderCampaignThread.start();
    }

    class LeaderCampaignThread implements Runnable {
        private byte[] currentLeaderData = null;

        @Override
        public void run() {
            System.out.println("Starting the Leader Campaign");

            try {
                boolean leader = leaderLock.tryAcquireLock();

                while (!leader) {
                    byte[] leaderData = leaderLock.getLockHolderData();
                    if (currentLeaderData != leaderData) {
                        currentLeaderData = leaderData;
                        setCurrentLeaderData(currentLeaderData);
                    }
                    Thread.sleep(10000);
                    leader = leaderLock.tryAcquireLock();
                }
                currentLeaderData = null;
                beTheLeader();
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage executable-name <port>");
        }
        int serverPort = Integer.parseInt(args[0]);

        // Initialize Database
        DatabaseConfig.initializeDatabase();

        // Initialize ETCD
        LoadBalancerClient client = new LoadBalancerClient(NAME_SERVICE_ADDRESS);
        client.registerService("service.InventoryService", "127.0.0.1", serverPort, "tcp");

        Thread printingHook = new Thread(() -> {
            try {
                client.unregisterService("service.InventoryService");
                System.out.println("Service removed");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Runtime.getRuntime().addShutdownHook(printingHook);

        // Initialize Server
        DistributedLock.setZooKeeperURL(ZOOKEEPER_ADDRESS);
        InventoryServer server = new InventoryServer("localhost", serverPort);
        server.startServer();
    }

    private synchronized void setCurrentLeaderData(byte[] leaderData) {
        ZookeeperService.leaderData = leaderData;
    }

    public List<String[]> getOthersData() throws KeeperException, InterruptedException {
        List<String[]> result = new ArrayList<>();
        List<byte[]> othersData = leaderLock.getOthersData();

        for (byte[] data : othersData) {
            String[] dataStrings = new String(data).split(":");
            result.add(dataStrings);
        }
        return result;
    }

    private void beTheLeader() {
        System.out.println("Leader Lock Acquired. Acting as Primary!");
        ZookeeperService.isLeader = new AtomicBoolean(true);
    }
}
