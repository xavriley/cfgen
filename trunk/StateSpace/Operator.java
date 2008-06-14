package StateSpace;

public class Operator {

    public Object[] args;

    public State apply(State old) {
	return old;
    }

    public String toString() {
	return "Identity operation";
    }

}