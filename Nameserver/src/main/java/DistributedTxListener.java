public interface DistributedTxListener {
   void onGlobalCommit();
   void onGlobalAbort();
}