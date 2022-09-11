package iit.uow.nameserver;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedLock implements Watcher {
    private String childPath;
    private ZooKeeperClient client;
    private String lockPath;
    private boolean isAcquired = false;
    private String watchedNode;
    CountDownLatch startFlag = new CountDownLatch(1);
    CountDownLatch eventReceivedFlag;
    public static String zooKeeperUrl;
    private static String lockProcessPath = "/lp_";
    private byte[] myDataBytes;

    public static void setZooKeeperURL(String url){
        zooKeeperUrl = url;
    }

    public DistributedLock (String lockName, String data) throws IOException, KeeperException, InterruptedException {
        myDataBytes = data.getBytes(StandardCharsets.UTF_8);
        this.lockPath = "/" + lockName;
        client = new ZooKeeperClient(zooKeeperUrl, 5000, this);
        startFlag.await();
        if (client.checkExists(lockPath) == false) {
            createRootNode();
        }
        createChildNode();
    }

    private void createRootNode() throws InterruptedException, UnsupportedEncodingException, KeeperException {
        lockPath = client.createNode(lockPath, false, CreateMode.PERSISTENT, "".getBytes(StandardCharsets.UTF_8));
        System.out.println("Root Node Created at " + lockPath);
    }

    private void createChildNode() throws InterruptedException, UnsupportedEncodingException, KeeperException {
        childPath = client.createNode(lockPath + lockProcessPath, false, CreateMode.EPHEMERAL_SEQUENTIAL, myDataBytes);
        System.out.println("Child Node Created at " + childPath);
    }

    public void acquireLock() throws KeeperException, InterruptedException, UnsupportedEncodingException {
        String smallestNode = findSmallestNodePath();
        if (smallestNode.equals(childPath)) {
            isAcquired = true;
        } else {
            do {
                System.out.println("Lock is Currently Acquired by Node " + smallestNode + ". Waiting...");
                eventReceivedFlag = new CountDownLatch(1);
                watchedNode = smallestNode;
                client.addWatch(smallestNode);
                eventReceivedFlag.await();
                smallestNode = findSmallestNodePath();
            } while (!smallestNode.equals(childPath));
            isAcquired = true;
        }
    }

    public void releaseLock() throws KeeperException, InterruptedException {
        if (!isAcquired) {
            throw new IllegalStateException("Lock Needs to be Acquired First to Release");
        }
        client.delete(childPath);
        isAcquired = false;
    }

    public boolean tryAcquireLock() throws KeeperException, InterruptedException, UnsupportedEncodingException {
        String smallestNode = findSmallestNodePath();
        if (smallestNode.equals(childPath)) {
            isAcquired = true;
        }
        return isAcquired;
    }

    public byte[] getLockHolderData() throws KeeperException, InterruptedException {
        String smallestNode = findSmallestNodePath();
        return  client.getData(smallestNode, true);
    }

    public List<byte[]> getOthersData() throws KeeperException, InterruptedException {
        List<byte[]> result = new ArrayList<>();
        List<String> childrenNodePaths = client.getChildrenNodePaths(lockPath);
        for (String path : childrenNodePaths) {
            path = lockPath + "/" + path;
            if (!path.equals(childPath)) {
                System.out.println("Path : " + path + ", Child Path : " + childPath);
                System.out.println("Fetching Data of Node : " + path);
                byte[] data = client.getData(path, false);
                result.add(data);
            }
        }
        return  result;
    }

    private String findSmallestNodePath() throws KeeperException, InterruptedException {
        List<String> childrenNodePaths = null;
        childrenNodePaths = client.getChildrenNodePaths(lockPath);
        Collections.sort(childrenNodePaths);
        String smallestPath = childrenNodePaths.get(0);
        smallestPath = lockPath + "/" + smallestPath;
        return smallestPath;
    }

    @Override
    public void process(WatchedEvent event) {
        Watcher.Event.KeeperState state = event.getState();
        Watcher.Event.EventType type = event.getType();

        if (Watcher.Event.KeeperState.SyncConnected == state) {
            if (Watcher.Event.EventType.None == type) {
                System.out.println("Successful Connected to the Server");
                startFlag.countDown();
            }
        }

        if (Watcher.Event.EventType.NodeDeleted.equals(type)){
            if (watchedNode != null && eventReceivedFlag != null && event.getPath().equals(watchedNode)){
                System.out.println("Node Delete Event Received. Trying to Get the Lock...");
                eventReceivedFlag.countDown();
            }
        }
    }
}
