package backend;

public interface IExecutorBrocaster {
    public void brocastBeforeExecution(ICodeNode node);
    public void brocastAfterExecution(ICodeNode node);
    public void registerReceiverForBeforeExe(IExecutorReceiver receiver);
    public void registerReceiverForAfterExe(IExecutorReceiver receiver);
    public void removeReceiver(IExecutorReceiver receiver);
}
