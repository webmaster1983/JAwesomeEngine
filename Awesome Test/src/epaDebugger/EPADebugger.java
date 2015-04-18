package epaDebugger;

import game.StandardGame;
import input.Input;
import input.InputEvent;
import input.KeyInput;

import java.util.ArrayList;
import java.util.List;

import loader.FontLoader;
import math.VecMath;
import narrowphase.EmptyManifoldGenerator;
import narrowphase.GJK;
import objects.RigidBody3;
import objects.SupportMap;
import physics.PhysicsShapeCreator;
import physics.PhysicsSpace;
import physicsSupportFunction.SupportDifferenceObject;
import shape.Box;
import shape.Sphere;
import utils.Debugger;
import vector.Vector3f;
import display.DisplayMode;
import display.GLDisplay;
import display.PixelFormat;
import display.VideoSettings;

public class EPADebugger extends StandardGame {
	PhysicsSpace space;
	Debugger debugger;
	RigidBody3 rb1, rb2;
	Simplex simplex;
	SupportDifferenceObject support1;

	InputEvent toggleMouseBind;

	@Override
	public void init() {
		initDisplay(new GLDisplay(), new DisplayMode(), new PixelFormat(),
				new VideoSettings());

		display.bindMouse();
		debugger = new Debugger(inputs,
				FontLoader.loadFont("res/fonts/DejaVuSans.ttf"), cam);
		cam.setFlyCam(true);
		cam.translateTo(0, 2, 20);

		toggleMouseBind = new InputEvent("toggleMouseBind", new Input(
				Input.KEYBOARD_EVENT, "T", KeyInput.KEY_PRESSED));
		inputs.addEvent(toggleMouseBind);

		Box b1 = new Box(-8.339999f, 9.969998f, 0.0f, 1f, 1f, 1f);
		rb1 = new RigidBody3(PhysicsShapeCreator.create(b1));

		Sphere s1 = new Sphere(-10, 10, 0, 1, 36, 36);
		rb2 = new RigidBody3(PhysicsShapeCreator.create(s1));

		// Fix transformation (usually done in PhysicsSpace-class of Engine
		rb1.setRotation(b1.getRotation());
		rb1.setTranslation(b1.getTranslation());

		rb2.setRotation(s1.getRotation());
		rb2.setTranslation(s1.getTranslation());

		// Visualize the support functions
		support1 = new SupportDifferenceObject(b1, rb1, s1, rb2);

		// Compute simplex as starting point for EPA
		GJK gjk = new GJK(new EmptyManifoldGenerator());
		gjk.isColliding(rb1, rb2);

		// init EPA
		epaInit(gjk.getSimplex());

		// Input to step EPA
		InputEvent stepEPA = new InputEvent("Step EPA", new Input(
				Input.KEYBOARD_EVENT, "E", KeyInput.KEY_PRESSED));
		inputs.addEvent(stepEPA);
	}

	@Override
	public void render() {
		debugger.render3d();
		debugger.begin();
		renderScene();
		simplex.render();
		support1.render();
	}

	@Override
	public void render2d() {
		render2dScene();
		debugger.end();
		debugger.render2d(fps, objects.size(), objects2d.size());
	}

	@Override
	public void update(int delta) {
		if (inputs.isEventActive("Step EPA")) {
			epaStep();
			simplex.delete();
			simplex = new Simplex(faces);
		}
		debugger.update();

		if (display.isMouseBound())
			cam.update(delta);
		if (toggleMouseBind.isActive()) {
			if (!display.isMouseBound())
				display.bindMouse();
			else
				display.unbindMouse();
		}
	}

	public void updateSimplex() {

	}

	// ------------------- EPA ---------------------
	List<Triangle> faces;
	Vector3f normal = new Vector3f();
	float depth = 0;
	SupportMap<Vector3f> Sa;
	SupportMap<Vector3f> Sb;

	private final float TOLERANCE = 0.001f;
	private final int MAX_ITERATIONS = 50;

	public void epaInit(List<Vector3f> gjksimplex) {
		Sa = rb1;
		Sb = rb2;

		faces = new ArrayList<Triangle>();

		Vector3f A = gjksimplex.get(3);
		Vector3f B = gjksimplex.get(2);
		Vector3f C = gjksimplex.get(1);
		Vector3f D = gjksimplex.get(0);
		faces.add(new Triangle(A, B, C));
		faces.add(new Triangle(A, D, B));
		faces.add(new Triangle(A, C, D));
		faces.add(new Triangle(D, C, B));

		simplex = new Simplex(faces);
	}

	public void epaStep() {
		Triangle t = findClosestTriangle(faces);
		// System.out.println(faces.size() + "; " + t.normal + "; "
		// + VecMath.dotproduct(t.normal, VecMath.negate(t.a)));
		// System.out.println(t.normal);

		if (isOriginInsideTriangleArea(t)) {
			Vector3f p = support(Sa, Sb, t.normal);
			// System.out.println(t.normal);
			double d = VecMath.dotproduct(p, t.normal);
			System.out.println(d - t.distance + "; " + p);
			if (d - t.distance < TOLERANCE) {
				normal = t.normal;
				depth = (float) d;
				// System.out.println("res: " + normal + "; " + depth + "; "
				// + t.a + "; " + t.b + "; " + t.c);
				return; // break replaced with return
			} else {
				faces.add(new Triangle(t.a, t.b, p));
				faces.add(new Triangle(t.b, t.c, p));
				faces.add(new Triangle(t.c, t.a, p));
			}
		}
		faces.remove(t);
		if (faces.size() == 0) {
			System.out.println("ERROR");
			return; // break replaced with return
		}
	}

	private Triangle findClosestTriangle(List<Triangle> faces) {
		Triangle closest = null;
		float distance = Float.MAX_VALUE;
		for (Triangle f : faces) {
			float dist = VecMath.dotproduct(f.normal, f.a);
			if (dist < distance) {
				closest = f;
				distance = dist;
				f.distance = distance;
			}
		}
		return closest;
	}

	private boolean isOriginInsideTriangleArea(Triangle t) {
		if (VecMath.dotproduct(
				VecMath.crossproduct(VecMath.subtraction(t.b, t.a), t.normal),
				VecMath.negate(t.a)) <= 0) {
			if (VecMath.dotproduct(VecMath.crossproduct(
					VecMath.subtraction(t.c, t.b), t.normal), VecMath
					.negate(t.b)) <= 0) {
				if (VecMath.dotproduct(VecMath.crossproduct(
						VecMath.subtraction(t.a, t.c), t.normal), VecMath
						.negate(t.c)) <= 0) {
					return true;
				}
			}
		}
		return false;
	}

	private Vector3f support(SupportMap<Vector3f> Sa, SupportMap<Vector3f> Sb,
			Vector3f dir) {
		return VecMath.subtraction(Sa.supportPoint(dir),
				Sb.supportPoint(VecMath.negate(dir)));
	}

	public class Triangle {
		Vector3f a, b, c, normal;
		float distance;

		public Triangle(Vector3f a, Vector3f b, Vector3f c) {
			this.a = a;
			this.b = b;
			this.c = c;
			normal = VecMath.normalize(VecMath.computeNormal(a, b, c));
		}
	}
}