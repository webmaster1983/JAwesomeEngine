package collisionshape;

import math.QuatMath;
import math.VecMath;
import objects.RigidBody3;
import shapedata.BoxStructure;
import vector.Vector3f;

public class BoxShape extends RigidBody3 implements BoxStructure {
	Vector3f halfsize;

	public BoxShape(float x, float y, float z, float halfsizex,
			float halfsizey, float halfsizez) {
		super();
		translate(x, y, z);
		halfsize = new Vector3f(halfsizex, halfsizey, halfsizez);
		init();
	}

	public BoxShape(float x, float y, float z, Vector3f halfsize) {
		super();
		translate(x, y, z);
		this.halfsize = halfsize;
		init();
	}

	public BoxShape(Vector3f pos, float halfsizex, float halfsizey,
			float halfsizez) {
		super();
		translate(pos);
		halfsize = new Vector3f(halfsizex, halfsizey, halfsizez);
		init();
	}

	public BoxShape(Vector3f pos, Vector3f halfsize) {
		super();
		translate(pos);
		this.halfsize = halfsize;
		init();
	}

	@Override
	public Vector3f getHalfSize() {
		return halfsize;
	}

	@Override
	public Vector3f getSize() {
		return VecMath.scale(halfsize, 2);
	}

	private void init() {
		float diag = (float) Math.sqrt(halfsize.x * halfsize.x + halfsize.y
				* halfsize.y + halfsize.z * halfsize.z);
		setAABB(new Vector3f(-diag, -diag, -diag), new Vector3f(diag, diag,
				diag));
	}

	@Override
	public Vector3f supportPointLocal(Vector3f direction) {
		Vector3f v = QuatMath.transform(this.getInverseRotation(), direction);
		return VecMath.multiplication(new Vector3f(v.x < 0 ? -1 : 1,
				v.y < 0 ? -1 : 1, v.z < 0 ? -1 : 1), halfsize);
	}
}