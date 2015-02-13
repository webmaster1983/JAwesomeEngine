package collisionshape2d;

import math.ComplexMath;
import objects.CollisionShape;
import objects.CollisionShape2;
import objects.SupportCalculator;
import quaternion.Complexf;
import shapedata2d.EllipseStructure;
import vector.Vector2f;

public class EllipseShape extends CollisionShape2 implements EllipseStructure {
	float radius, height;

	public EllipseShape(float x, float y, float radius, float height) {
		super();
		translate(x, y);
		this.radius = radius;
		this.height = height;
		init();
	}

	public EllipseShape(Vector2f pos, float radius, float height) {
		super();
		translate(pos);
		this.radius = radius;
		this.height = height;
		init();
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getRadius() {
		return radius;
	}

	private void init() {
		float longest = radius > height ? radius : height;
		setAABB(new Vector2f(-longest, -longest),
				new Vector2f(longest, longest));
		supportcalculator = createSupportCalculator(this);
	}

	@Override
	public SupportCalculator<Vector2f> createSupportCalculator(
			CollisionShape<Vector2f, Complexf> cs) {
		return new EllipseSupport(cs);
	}

	protected class EllipseSupport implements SupportCalculator<Vector2f> {
		private CollisionShape<Vector2f, Complexf> collisionshape;

		public EllipseSupport(CollisionShape<Vector2f, Complexf> cs) {
			collisionshape = cs;
		}

		@Override
		public Vector2f supportPointLocal(Vector2f direction) {
			if (direction.length() == 0)
				direction = new Vector2f(0, 1);
			direction.normalize();
			Vector2f v = ComplexMath.transform(
					collisionshape.getInverseRotation(), direction);
			return new Vector2f(v.x * radius, v.y * height);
		}
	}
}