import iit.uow.nameserver.DistributedTxListener;

public class InventoryServiceDistributed implements DistributedTxListener {

    private InventoryServer server;

    @Override
    public void onGlobalCommit() {

    }

    @Override
    public void onGlobalAbort() {

    }
}
