package iit.uow.nameserver;

public interface DistributedTxListener {
   void onGlobalCommit();
   void onGlobalAbort();
}