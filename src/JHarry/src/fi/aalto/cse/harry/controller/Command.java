package fi.aalto.cse.harry.controller;

/**
 * Command list
 * @author Long
 *
 */
public enum Command {
    FLY(1),
    DROP(0),    
    STOP(2),
    TURNRIGHT(12),
    TURNLEFT(11),
    FORWARD(21),
    EMERGENCY(99);

    private final int value;

    private Command(int value) {
	this.value = value;
    }

    public int getValue() {
	return value;
    }
}
