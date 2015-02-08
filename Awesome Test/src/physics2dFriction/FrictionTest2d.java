package physics2dFriction;

import game.StandardGame;
import gui.DisplayMode;
import gui.GLDisplay;
import gui.PixelFormat;
import gui.VideoSettings;
import integration.EulerIntegration;
import manifold.SimpleManifoldManager;
import narrowphase.EPA2;
import narrowphase.GJK2;
import objects.RigidBody2;
import physics.PhysicsShapeCreator;
import physics.PhysicsSpace2;
import positionalcorrection.ProjectionCorrection;
import resolution.LinearImpulseResolution;
import shape2d.Quad;
import vector.Vector2f;
import broadphase.SAP2;

public class FrictionTest2d extends StandardGame {
	PhysicsSpace2 space;
	int tempdelta = 0;

	@Override
	public void init() {
		initDisplay(new GLDisplay(), new DisplayMode(), new PixelFormat(),
				new VideoSettings());
		cam.setFlyCam(true);
		cam.translateTo(0f, 0f, 5);
		cam.rotateTo(0, 0);

		space = new PhysicsSpace2(new EulerIntegration(), new SAP2(), new GJK2(
				new EPA2()), new LinearImpulseResolution(),
				new ProjectionCorrection(),
				new SimpleManifoldManager<Vector2f>());
		space.setGlobalForce(new Vector2f(0, 100));

		Quad ground = new Quad(400, 550, 300, 20);
		RigidBody2 rb = new RigidBody2(PhysicsShapeCreator.create(ground));
		space.addRigidBody(ground, rb);
		add2dObject(ground);

		Quad q = new Quad(100, 450, 10, 10);
		RigidBody2 rb1 = new RigidBody2(PhysicsShapeCreator.create(q));
		rb1.setMass(1f);
		rb1.applyCentralImpulse(new Vector2f(100f, 0f));
		space.addRigidBody(q, rb1);
		add2dObject(q);
	}

	@Override
	public void render() {

	}

	@Override
	public void render2d() {
		render2dScene();
	}

	@Override
	public void update(int delta) {
		// System.out.println(rb1.getLinearVelocity());
		if (tempdelta > 200) {
			if (inputs.isMouseButtonDown("0")) {
				Quad q = new Quad(100, 450, 10, 10);
				RigidBody2 rb = new RigidBody2(PhysicsShapeCreator.create(q));
				rb.setMass(1f);
				rb.applyCentralImpulse(new Vector2f(100f, 0f));
				space.addRigidBody(q, rb);
				add2dObject(q);
				tempdelta = 0;
			}
			if (inputs.isMouseButtonDown("1")) {
				Quad q = new Quad(700, 450, 10, 10);
				RigidBody2 rb = new RigidBody2(PhysicsShapeCreator.create(q));
				rb.setMass(1f);
				rb.applyCentralImpulse(new Vector2f(-100f, 0f));
				space.addRigidBody(q, rb);
				add2dObject(q);
				tempdelta = 0;
			}
		} else {
			tempdelta += delta;
		}

		space.update(delta);
		cam.update(delta);
	}
}
