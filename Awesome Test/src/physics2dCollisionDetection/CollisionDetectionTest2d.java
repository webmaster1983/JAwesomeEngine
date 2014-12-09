package physics2dCollisionDetection;

import game.StandardGame;
import integration.EulerIntegration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import loader.InputLoader;
import loader.ShaderLoader;
import manifold.CollisionManifold;
import manifold.SimpleManifoldManager;
import narrowphase.EPA2;
import narrowphase.GJK2;
import objects.RigidBody;
import objects.RigidBody2;
import physics.PhysicsShapeCreator;
import physics.PhysicsSpace2;
import positionalcorrection.NullCorrection;
import resolution.NullResolution;
import shape2d.Circle;
import shape2d.Ellipse;
import shape2d.Quad;
import utils.Pair;
import utils.Shader;
import vector.Vector2f;
import vector.Vector4f;
import broadphase.SAP2;

public class CollisionDetectionTest2d extends StandardGame {
	PhysicsSpace2 space;
	Quad q1, q2, q3;
	Circle c1;
	Ellipse e1;
	Shader s1, s2, s3, s4, s5;
	RigidBody2 rb1, rb2, rb3, rb4, rb5;
	List<ManifoldVisualization> manifolds;

	@Override
	public void init() {
		initDisplay(false, 800, 600, true);
		cam.setFlyCam(true);
		cam.translateTo(0f, 0f, 5);
		cam.rotateTo(0, 0);
		mouse.setGrabbed(false);

		int shaderprogram = ShaderLoader.loadShaderPair(
				"res/shaders/colorshader.vert", "res/shaders/colorshader.frag");
		s1 = new Shader(shaderprogram);
		s2 = new Shader(shaderprogram);
		s3 = new Shader(shaderprogram);
		s4 = new Shader(shaderprogram);
		s5 = new Shader(shaderprogram);

		s1.addArgument("color", new Vector4f(1f, 1f, 1f, 1f));
		s2.addArgument("color", new Vector4f(1f, 1f, 1f, 1f));
		s3.addArgument("color", new Vector4f(1f, 1f, 1f, 1f));
		s4.addArgument("color", new Vector4f(1f, 1f, 1f, 1f));
		s5.addArgument("color", new Vector4f(1f, 1f, 1f, 1f));

		manifolds = new ArrayList<ManifoldVisualization>();

		space = new PhysicsSpace2(new EulerIntegration(), new SAP2(), new GJK2(
				new EPA2()), new NullResolution(), new NullCorrection(),
				new SimpleManifoldManager<Vector2f>());

		q1 = new Quad(400, 200, 25, 25);
		q1.setShader(s1);
		rb1 = PhysicsShapeCreator.create(q1);
		space.addRigidBody(q1, rb1);
		add2dObject(q1);

		q2 = new Quad(500, 200, 35, 35);
		q2.setShader(s2);
		rb2 = PhysicsShapeCreator.create(q2);
		space.addRigidBody(q2, rb2);
		add2dObject(q2);

		q3 = new Quad(100, 500, 25, 25);
		q3.setShader(s3);
		rb3 = PhysicsShapeCreator.create(q3);
		space.addRigidBody(q3, rb3);
		add2dObject(q3);

		c1 = new Circle(80, 80, 25, 40);
		c1.setShader(s4);
		rb4 = PhysicsShapeCreator.create(c1);
		space.addRigidBody(c1, rb4);
		add2dObject(c1);

		e1 = new Ellipse(500, 50, 50, 25, 40);
		e1.setShader(s5);
		rb5 = PhysicsShapeCreator.create(e1);
		space.addRigidBody(e1, rb5);
		add2dObject(e1);

		inputs = InputLoader.load("res/inputs.txt");
	}

	@Override
	public void render() {
		renderScene();
	}

	@Override
	public void render2d() {
		render2dScene();
		for (ManifoldVisualization mv : manifolds) {
			mv.render();
			mv.delete();
		}
		manifolds.clear();
	}

	@Override
	public void update(int delta) {
		q1.rotate(delta / 10f);

		if (inputs.isInputEventActive("Translate1")) {
			q1.translate(0, -delta / 4f);
		}
		if (inputs.isInputEventActive("Translate2")) {
			q1.translate(0, delta / 4f);
		}
		if (inputs.isInputEventActive("Translate3")) {
			q1.translate(-delta / 4f, 0);
		}
		if (inputs.isInputEventActive("Translate4")) {
			q1.translate(delta / 4f, 0);
		}

		if (inputs.isInputEventActive("Rotate2")) {
			q1.rotate(delta / 10f);
		}
		if (inputs.isInputEventActive("Rotate3")) {
			q1.rotate(-delta / 10f);
		}

		space.update(delta);

		s1.setArgument(0, new Vector4f(1f, 1f, 1f, 1f));
		s2.setArgument(0, new Vector4f(1f, 1f, 1f, 1f));
		s3.setArgument(0, new Vector4f(1f, 1f, 1f, 1f));
		s4.setArgument(0, new Vector4f(1f, 1f, 1f, 1f));
		s5.setArgument(0, new Vector4f(1f, 1f, 1f, 1f));

		Set<Pair<RigidBody<Vector2f, ?, ?, ?>, RigidBody<Vector2f, ?, ?, ?>>> overlaps = space
				.getBroadphase().getOverlaps();
		for (Pair<RigidBody<Vector2f, ?, ?, ?>, RigidBody<Vector2f, ?, ?, ?>> o : overlaps) {
			if (o.contains(rb1))
				s1.setArgument(0, new Vector4f(1f, 1f, 0f, 1f));
			if (o.contains(rb2))
				s2.setArgument(0, new Vector4f(1f, 1f, 0f, 1f));
			if (o.contains(rb3))
				s3.setArgument(0, new Vector4f(1f, 1f, 0f, 1f));
			if (o.contains(rb4))
				s4.setArgument(0, new Vector4f(1f, 1f, 0f, 1f));
			if (o.contains(rb5))
				s5.setArgument(0, new Vector4f(1f, 1f, 0f, 1f));
		}

		for (CollisionManifold<Vector2f> cm : space.getCollisionManifolds()) {
			manifolds.add(new ManifoldVisualization(cm));
			Pair<RigidBody<Vector2f, ?, ?, ?>, RigidBody<Vector2f, ?, ?, ?>> o = cm
					.getObjects();
			if (o.contains(rb1))
				s1.setArgument(0, new Vector4f(1f, 0f, 0f, 1f));
			if (o.contains(rb2))
				s2.setArgument(0, new Vector4f(1f, 0f, 0f, 1f));
			if (o.contains(rb3))
				s3.setArgument(0, new Vector4f(1f, 0f, 0f, 1f));
			if (o.contains(rb4))
				s4.setArgument(0, new Vector4f(1f, 0f, 0f, 1f));
			if (o.contains(rb5))
				s5.setArgument(0, new Vector4f(1f, 0f, 0f, 1f));
		}

		cam.update(delta);
	}
}