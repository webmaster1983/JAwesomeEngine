package input;

public class MouseInput extends Input {
	public static final int MOUSE_MOVED = 0;

	public static final int MOUSE_BUTTON_PRESSED = 1;
	public static final int MOUSE_BUTTON_DOWN = 2;
	public static final int MOUSE_BUTTON_RELEASED = 3;

	public MouseInput(String componentname, int eventtype) {
		super(MOUSE_EVENT, componentname, eventtype);
	}
}
