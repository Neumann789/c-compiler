package practice.finitStateMachine;
public interface FMS {
	public  final int STATE_FAILURE = -1;
	
    public int yy_next(int state, byte c);
    public boolean isAcceptState(int state);
}
