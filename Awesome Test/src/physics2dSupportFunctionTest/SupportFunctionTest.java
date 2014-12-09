package physics2dSupportFunctionTest;

import game.Debugger;
import game.StandardGame;
import input.Input;
import integration.EulerIntegration;
import loader.FontLoader;
import loader.InputLoader;
import manifold.SimpleManifoldManager;
import narrowphase.EPA2;
import narrowphase.GJK2;
import objects.RigidBody2;
import physics.PhysicsShapeCreator;
import physics.PhysicsSpace2;
import positionalcorrection.NullCorrection;
import resolution.NullResolution;
import shape2d.Circle;
import shape2d.Quad;
import vector.Vector2f;
import broadphase.SAP2;

public class SupportFunctionTest extends StandardGame {
	PhysicsSpace2 space;
	Quad q1, q2;
	Circle c1;
	SupportObject so1, so2, so3;
	SupportDifferenceObject support1, support2;
	DirectionRenderer dirrenderer;
	RigidBody2 rb1, rb2, rb3;
	Debugger debugmanager;

	@Override
	public void init() {
		initDisplay(false, 800, 600, true);
		cam.setFlyCam(true);
		cam.translateTo(0.5f, 0f, 5);
		cam.rotateTo(0, 0);

		inputs = InputLoader.load("res/inputs.txt");
		debugmanager = new Debugger(inputs,
				FontLoader.loadFont("res/fonts/DejaVuSans.ttf"), cam);
		this.setRendering2d(true);

		space = new PhysicsSpace2(new EulerIntegration(), new SAP2(), new GJK2(
				new EPA2()), new NullResolution(), new NullCorrection(),
				new SimpleManifoldManager<Vector2f>());

		q1 = new Quad(400, 200, 25, 25);
		rb1 = PhysicsShapeCreator.create(q1);
		space.addRigidBody(q1, rb1);
		// addObject(b1);

		q2 = new Quad(400, 200, 25, 25);
		rb2 = PhysicsShapeCreator.create(q2);
		space.addRigidBody(q2, rb2);
		// addObject(b2);

		c1 = new Circle(80, 80, 25, 50);
		rb3 = PhysicsShapeCreator.create(c1);
		space.addRigidBody(c1, rb3);
		// addObject(s1);

		dirrenderer = new DirectionRenderer();
		addObject(dirrenderer);

		so1 = new SupportObject(q1, rb1);
		so2 = new SupportObject(q2, rb2);
		so3 = new SupportObject(c1, rb3);

		add2dObject(so1);
		add2dObject(so2);
		add2dObject(so3);

		support1 = new SupportDifferenceObject(q1, rb1, q2, rb2);
		support2 = new SupportDifferenceObject(q1, rb1, c1, rb3);

		add2dObject(support1);
		add2dObject(support2);

		// addObject(new ResultTetrahedron(new ArrayList<Vector3f>() {{add(new
		// Vector3f(2,5,2)); add(new Vector3f(8,5,2)); add(new Vector3f(2,5,8));
		// add(new Vector3f(4,8,4)); }}));

		inputs.createInputEvent("toggle Mouse grab").addEventTrigger(
				new Input(Input.KEYBOARD_EVENT, Keyboard.KEY_R,
						KeyEvent.Key_Pressed));
	}

	@Override
	public void render() {
		debugmanager.render3d();
		renderScene();
	}

	@Override
	public void render2d() {
		debugmanager.render2d(fps, objects.size());
		render2dScene();
	}

	@Override
	public void update(int delta) {
		boolean moved = false;
		if (inputs.isInputEventActive("Translate1")) {
			q1.translate(0, -delta / 4f);
			moved = true;
		}
		if (inputs.isInputEventActive("Translate2")) {
			q1.translate(0, delta / 4f);
			moved = true;
		}
		if (inputs.isInputEventActive("Translate3")) {
			q1.translate(-delta / 4f, 0);
			moved = true;
		}
		if (inputs.isInputEventActive("Translate4")) {
			q1.translate(delta / 4f, 0);
			moved = true;
		}

		if (inputs.isInputEventActive("Rotate2")) {
			q1.rotate(delta / 10f);
			moved = true;
		}
		if (inputs.isInputEventActive("Rotate3")) {
			q1.rotate(-delta / 10f);
			moved = true;
		}

		if (inputs.isInputEventActive("toggle Mouse grab")) {
			mouse.setGrabbed(!mouse.isGrabbed());
		}

		if (moved) {
			so1.updateShape();
			so2.updateShape();
			so3.updateShape();

			dirrenderer.setDirections(support1.updateShape());
			dirrenderer.setDirections(support2.updateShape());
		}

		// if(space.testlist.size() > 0) {
		// for(List<Vector2f> v : space.testlist) {
		// addObject(new ResultTetrahedron(v));
		// }
		// }

		debugmanager.update();
		cam.update(delta);
		space.update(delta);
	}
}
