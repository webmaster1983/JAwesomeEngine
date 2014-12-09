package shape2d;

import java.awt.Color;

import math.VecMath;
import objects.ShapedObject2;
import shapedata2d.QuadStructure;
import vector.Vector2f;

public class Quad extends ShapedObject2 implements QuadStructure {
	Vector2f halfsize;

	public Quad(float x, float y, float halfsizex, float halfsizey) {
		super();
		translateTo(x, y);
		init(halfsizex, halfsizey);
	}

	public Quad(Vector2f pos, float halfsizex, float halfsizey) {
		super();
		translateTo(pos);
		init(halfsizex, halfsizey);
	}

	public Quad(Vector2f pos, Vector2f halfsize) {
		super();
		translateTo(pos);
		init(halfsize.x, halfsize.y);
	}

	@Override
	public Vector2f getHalfSize() {
		return halfsize;
	}

	@Override
	public Vector2f getSize() {
		return VecMath.scale(halfsize, 0.5f);
	}

	// public boolean contains(float px, float py) {
	// if (px >= getTranslation().x && py >= getTranslation().y) {
	// if (px <= getTranslation().x + halfsize.x
	// && py <= getTranslation().y + halfsize.y) {
	// return true;
	// }
	// }
	// return false;
	// }

	// public boolean contains(Vector2f point) {
	// return contains(point.x, point.y);
	// }

	private void init(float hsx, float hsy) {
		shapetype = SHAPE_QUAD;
		halfsize = new Vector2f(hsx, hsy);
		Color color = Color.GRAY;

		addVertex(new Vector2f(-hsx, -hsy), color, new Vector2f(0, 0));
		addVertex(new Vector2f(-hsx, hsy), color, new Vector2f(0, 1));
		addVertex(new Vector2f(hsx, hsy), color, new Vector2f(1, 1));
		addVertex(new Vector2f(hsx, -hsy), color, new Vector2f(1, 0));

		addQuad(0, 1, 2, 3);

		this.prerender();
	}
}