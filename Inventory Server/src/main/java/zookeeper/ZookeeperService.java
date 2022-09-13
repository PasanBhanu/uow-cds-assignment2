package zookeeper;

import java.util.concurrent.atomic.AtomicBoolean;

public class ZookeeperService {
    public static AtomicBoolean isLeader = new AtomicBoolean(false);

    public static byte[] leaderData;

    public static synchronized String[] getCurrentLeaderData() {
        return new String(leaderData).split(":");
    }
}
