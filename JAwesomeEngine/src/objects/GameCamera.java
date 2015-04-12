package objects;

import input.Input;
import input.InputEvent;
import input.InputManager;
import input.KeyInput;
import math.VecMath;
import utils.DefaultValues;
import vector.Vector3d;
import vector.Vector3f;

public class GameCamera extends Camera implements Updateable {
	boolean flycam, invertX, invertY;
	float minVAngle, maxVAngle, speed;

	InputManager inputs;
	InputEvent forwards, backwards, left, right;

	public GameCamera(InputManager inputs) {
		super();
		init(inputs, DefaultValues.DEFAULT_GAMECAMERA_MIN_V_ANGLE,
				DefaultValues.DEFAULT_GAMECAMERA_MAX_V_ANGLE,
				DefaultValues.DEFAULT_GAMECAMERA_SPEED);
	}

	public GameCamera(InputManager inputs, float speed) {
		super();
		init(inputs, DefaultValues.DEFAULT_GAMECAMERA_MIN_V_ANGLE,
				DefaultValues.DEFAULT_GAMECAMERA_MAX_V_ANGLE, speed);
	}

	public GameCamera(InputManager inputs, float minVAngle, float maxVAngle) {
		super();
		init(inputs, minVAngle, maxVAngle,
				DefaultValues.DEFAULT_GAMECAMERA_SPEED);
	}

	public GameCamera(InputManager inputs, float minVAngle, float maxVAngle,
			float speed) {
		super();
		init(inputs, minVAngle, maxVAngle, speed);
	}

	private void init(InputManager inputs, float minVAngle, float maxVAngle,
			float speed) {
		this.minVAngle = minVAngle;
		this.maxVAngle = maxVAngle;
		this.speed = speed;
		flycam = false;
		invertX = false;
		invertY = false;

		this.inputs = inputs;
		setupControls(inputs);
	}

	public float getFlySpeed() {
		return speed;
	}

	public boolean isFlyCam() {
		return flycam;
	}

	public boolean isInvertedX() {
		return invertX;
	}

	public boolean isInvertedY() {
		return invertY;
	}

	public void setFlyCam(boolean fly) {
		flycam = fly;
	}

	public void setFlySpeed(float speed) {
		this.speed = speed;
	}

	public void setInverted(boolean inverted) {
		invertX = inverted;
		invertY = inverted;
	}

	public void setInvertedX(boolean inverted) {
		invertX = inverted;
	}

	public void setInvertedY(boolean inverted) {
		invertY = inverted;
	}

	public void setMinVAngle(float angle) {
		minVAngle = angle;
	}

	public void setMaxVAngle(float angle) {
		maxVAngle = angle;
	}

	public float getMinVAngle() {
		return minVAngle;
	}

	public float getMaxVAngle() {
		return maxVAngle;
	}

	public void rotation(float deltah, float deltav) {
		hrot += deltah;
		if (deltav > 0) {
			if (vrot + deltav < maxVAngle) {
				vrot += deltav;
			} else {
				vrot = maxVAngle;
			}
		} else {
			if (vrot + deltav > minVAngle) {
				vrot += deltav;
			} else {
				vrot = minVAngle;
			}
		}

		if (hrot > 360 || hrot < -360) {
			hrot %= 360;
		}
		rotateTo(vrot, hrot, 0);
	}

	private void setupControls(InputManager inputs) {
		forwards = new InputEvent("camera_forwards", new Input(
				Input.KEYBOARD_EVENT, "Up", KeyInput.KEY_DOWN), new Input(
				Input.KEYBOARD_EVENT, "W", KeyInput.KEY_DOWN));
		backwards = new InputEvent("camera_backwards", new Input(
				Input.KEYBOARD_EVENT, "Down", KeyInput.KEY_DOWN), new Input(
				Input.KEYBOARD_EVENT, "S", KeyInput.KEY_DOWN));
		left = new InputEvent("camera_left", new Input(Input.KEYBOARD_EVENT,
				"Left", KeyInput.KEY_DOWN), new Input(Input.KEYBOARD_EVENT,
				"A", KeyInput.KEY_DOWN));
		right = new InputEvent("camera_right", new Input(Input.KEYBOARD_EVENT,
				"Right", KeyInput.KEY_DOWN), new Input(Input.KEYBOARD_EVENT,
				"D", KeyInput.KEY_DOWN));

		inputs.addEvent(forwards);
		inputs.addEvent(backwards);
		inputs.addEvent(left);
		inputs.addEvent(right);
	}

	public void update(int delta) {
		if (flycam) {
			float mousedx = inputs.getMouseDX();
			float mousedy = inputs.getMouseDY();

			if ((mousedx != 0 || mousedy != 0)) {
				if (!invertX)
					mousedx = -mousedx;
				if (!invertY)
					mousedy = -mousedy;

				rotate(mousedx / 10f, mousedy / 10f);
			}

			Vector3d move = new Vector3d(0, 0, 0);
			if (forwards.isActive()) {
				move = VecMath.addition(move, direction);
			}
			if (backwards.isActive()) {
				move = VecMath.subtraction(move, direction);
			}
			if (left.isActive()) {
				move = VecMath.subtraction(move,
						VecMath.crossproduct(direction, new Vector3f(0, 1, 0)));
			}
			if (right.isActive()) {
				move = VecMath.addition(move,
						VecMath.crossproduct(direction, new Vector3f(0, 1, 0)));
			}
			if (move.length() != 0) {
				move = VecMath.normalize(move);
				move = VecMath.scale(move, delta * speed);
				translateTo(VecMath.addition(getTranslation(), move));
			}
		}
	}
}