package tutorialp2;

import input.Input;
import input.InputEvent;
import input.KeyInput;
import shape.Box;
import game.StandardGame;
import gui.DisplayMode;
import gui.GLDisplay;
import gui.PixelFormat;
import gui.VideoSettings;

public class Tutorial extends StandardGame {
	Box player;
	InputEvent forward, backward, left, right;

	@Override
	public void init() {
		initDisplay(new GLDisplay(), new DisplayMode(), new PixelFormat(),
				new VideoSettings());
		display.bindMouse();
		cam.translateTo(0f, 0f, 5);
		cam.rotateTo(0, 0);
		
		player = new Box(0,0,0,1,1.7f,1);
		this.addObject(player);
		
		forward = new InputEvent("Forward", new Input(
				Input.KEYBOARD_EVENT, "W", KeyInput.KEY_DOWN), new Input(
						Input.KEYBOARD_EVENT, "Up", KeyInput.KEY_DOWN));
		backward = new InputEvent("Backward", new Input(
				Input.KEYBOARD_EVENT, "S", KeyInput.KEY_DOWN), new Input(
						Input.KEYBOARD_EVENT, "Down", KeyInput.KEY_DOWN));
		left = new InputEvent("Left", new Input(
				Input.KEYBOARD_EVENT, "A", KeyInput.KEY_DOWN), new Input(
						Input.KEYBOARD_EVENT, "Left", KeyInput.KEY_DOWN));
		right = new InputEvent("Right", new Input(
				Input.KEYBOARD_EVENT, "D", KeyInput.KEY_DOWN), new Input(
						Input.KEYBOARD_EVENT, "Right", KeyInput.KEY_DOWN));
		inputs.addEvent(forward);
		inputs.addEvent(backward);
		inputs.addEvent(left);
		inputs.addEvent(right);
	}
	
	@Override
	public void update(int delta) {
		if(inputs.isMouseMoved()) {
			System.out.println("mouse moved (" + inputs.getMouseDX() + "; " + inputs.getMouseDY() + ")");
		}
		if(forward.isActive()) {
			System.out.println("forward");
		}
		if(backward.isActive()) {
			System.out.println("backward");
		}
		if(left.isActive()) {
			System.out.println("left");
		}
		if(right.isActive()) {
			System.out.println("right");
		}
		
		cam.update(delta);
	}

	@Override
	public void render() {
		renderScene();
	}

	@Override
	public void render2d() {
		
	}

}
