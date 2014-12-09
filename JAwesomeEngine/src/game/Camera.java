package game;

import static org.lwjgl.opengl.GL11.glMultMatrix;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import input.Input;
import input.InputEvent;
import input.InputManager;
import input.KeyEvent;
import math.QuatMath;
import math.VecMath;
import matrix.Matrix4f;
import objects.RenderedObject;
import vector.Vector3d;
import vector.Vector3f;

public class Camera extends RenderedObject {
	Vector3f position;
	Vector3f direction;
	float hrot, vrot;
	boolean flycam, invertX, invertY;
	float speed = 0.012f;

	InputManager inputs;
	InputEvent forwards, backwards, left, right;

	public Camera(InputManager inputs) {
		super();
		flycam = false;
		invertX = false;
		invertY = false;
		hrot = 0;
		vrot = 0;
		rotateTo(180, 0);

		this.inputs = inputs;
		setupControls(inputs);
	}

	private void setupControls(InputManager inputs) {
		forwards = new InputEvent("camera_forwards", new Input(
				Input.KEYBOARD_EVENT, "Up", KeyEvent.KEY_DOWN), new Input(
				Input.KEYBOARD_EVENT, "W", KeyEvent.KEY_DOWN));
		backwards = new InputEvent("camera_backwards", new Input(
				Input.KEYBOARD_EVENT, "Down", KeyEvent.KEY_DOWN), new Input(
				Input.KEYBOARD_EVENT, "S", KeyEvent.KEY_DOWN));
		left = new InputEvent("camera_left", new Input(Input.KEYBOARD_EVENT,
				"Left", KeyEvent.KEY_DOWN), new Input(Input.KEYBOARD_EVENT,
				"A", KeyEvent.KEY_DOWN));
		right = new InputEvent("camera_right", new Input(Input.KEYBOARD_EVENT,
				"Right", KeyEvent.KEY_DOWN), new Input(Input.KEYBOARD_EVENT,
				"D", KeyEvent.KEY_DOWN));

		inputs.addEvent(forwards);
		inputs.addEvent(backwards);
		inputs.addEvent(left);
		inputs.addEvent(right);
	}

	public void begin() {
		glPushMatrix();
		glTranslatef(rotcenter.x, rotcenter.y, rotcenter.z);
		glMultMatrix(buf);
		glTranslatef(-rotcenter.x, -rotcenter.y, -rotcenter.z);
	}

	public void end() {
		glPopMatrix();
	}

	public Vector3f getDirection() {
		return direction;
	}

	public float getFlySpeed() {
		return speed;
	}

	public Vector3f getPosition() {
		return position;
	}

	public boolean isInvertedX() {
		return invertX;
	}

	public boolean isInvertedY() {
		return invertY;
	}

	public boolean isFlyCam() {
		return flycam;
	}

	@Override
	public void render() {

	}

	public void rotate(float rotH, float rotV) {
		rotation(rotH, rotV);
	}

	public void rotateTo(float rotH, float rotV) {
		rotation(rotH - hrot, rotV - vrot);
	}

	public void rotation(float deltah, float deltav) {
		hrot += deltah;
		float dV = deltav;
		if ((deltav > 0 && vrot + dV < 85) || (deltav < 0 && vrot + dV > -85)) {
			vrot += dV;
		}

		if (hrot > 360 || hrot < -360) {
			hrot %= 360;
		}
		rotateTo(vrot, hrot, 0);
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

	public void setFlyCam(boolean fly) {
		flycam = fly;
	}

	public void setFlySpeed(float speed) {
		this.speed = speed;
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
		} else {
			if (attachedTo != null) {
				// rotation(0, 0);
			}
		}
	}

	@Override
	public void updateBuffer() {
		// Matrix4f transformmatrix = new Matrix4f(matrix);
		// // if (isAttached()) {
		// // transformmatrix = VecMath.transformMatrix(transformmatrix,
		// // attachedTo.getMatrix());
		// // }
		// position = getTranslation();
		// direction = VecMath.transformVector(transformmatrix.getSubMatrix(),
		// new Vector3f(0, 0, -1));
		// transformmatrix.invert();
		// transformmatrix.store(buf);
		// buf.rewind();
		position = getTranslation();
		direction = QuatMath.transform(rotation, new Vector3f(0, 0, -1));
		// Quaternionf tmp = new Quaternionf(rotation);
		// tmp.invert();
		Matrix4f mat = new Matrix4f();
		mat.setSubMatrix(rotation.toMatrixf());
		mat.translate(position);
		mat.invert();
		mat.store(buf);
		buf.rewind();

		// float[][] mat = tmp.toMatrixf().getArrayf();
		// buf.put(mat[0][0]);
		// buf.put(mat[0][1]);
		// buf.put(mat[0][2]);
		// // buf.put(0);
		// buf.put(mat[1][0]);
		// buf.put(mat[1][1]);
		// buf.put(mat[1][2]);
		// // buf.put(0);
		// buf.put(mat[2][0]);
		// buf.put(mat[2][1]);
		// buf.put(mat[2][2]);
		// // buf.put(0);
		// // buf.put(translation.getXf());
		// // buf.put(translation.getYf());
		// // buf.put(translation.getZf());
		// // buf.put(1);
		// buf.rewind();
	}
}