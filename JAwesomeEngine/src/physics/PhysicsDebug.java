package physics;

import gui.Font;
import input.InputManager;

import java.awt.Color;
import java.util.List;

import manifold.CollisionManifold;
import math.VecMath;
import net.java.games.input.Component.Identifier.Key;
import objects.RigidBody;
import objects.ShapedObject;

import org.lwjgl.opengl.GL11;

import quaternion.Quaternionf;
import space.Space3;
import vector.Vector3f;

public class PhysicsDebug {
	InputManager inputs;
	Font font;
	Space3 physics;
	boolean showAABBs = false;
	boolean showCollisionNormals = false;
	boolean showVelocities = false;

	public PhysicsDebug(InputManager i, Font f, Space3 physics) {
		inputs = i;
		font = f;
		this.physics = physics;
	}

	public boolean isAABBsShown() {
		return showAABBs;
	}

	public boolean isCollisionNormalsShown() {
		return showCollisionNormals;
	}

	public boolean isVelocitiesShown() {
		return showVelocities;
	}

	public void render2d() {

	}

	public void render3d() {
		if (showAABBs) {

		}
		if (showCollisionNormals) {
			List<CollisionManifold<Vector3f>> manifolds = physics
					.getCollisionManifolds();
			for (CollisionManifold<Vector3f> cm : manifolds) {
				Color c = Color.RED;
				ShapedObject normal1 = new ShapedObject();
				ShapedObject normal2 = new ShapedObject();
				normal1.setRenderMode(GL11.GL_LINES);
				normal2.setRenderMode(GL11.GL_LINES);
				normal1.addVertex(cm.getContactPointA(), c);
				normal1.addVertex(
						VecMath.addition(cm.getContactPointA(),
								VecMath.negate(cm.getCollisionNormal())), c);
				normal2.addVertex(cm.getContactPointB(), c);
				normal2.addVertex(
						VecMath.addition(cm.getContactPointB(),
								cm.getCollisionNormal()), c);
				normal1.addIndices(0, 1);
				normal2.addIndices(0, 1);
				normal1.prerender();
				normal2.prerender();
				normal1.render();
				normal2.render();
				normal1.delete();
				normal2.delete();
			}
		}
		if (showVelocities) {
			List<RigidBody<Vector3f, Vector3f, Quaternionf, Quaternionf>> objs = physics
					.getObjects();
			for (RigidBody<Vector3f, Vector3f, Quaternionf, Quaternionf> o : objs) {
				Color c = Color.BLUE;
				ShapedObject velocity = new ShapedObject();
				velocity.setRenderMode(GL11.GL_LINES);
				velocity.addVertex(o.getTranslation(), c);
				velocity.addVertex(
						VecMath.addition(o.getTranslation(),
								o.getLinearVelocity()), c);
				velocity.addIndices(0, 1);
				velocity.prerender();
				velocity.render();
				velocity.delete();
			}
		}
	}

	public void setShowAABBs(boolean s) {
		showAABBs = s;
	}

	public void setShowCollisionNormals(boolean s) {
		showCollisionNormals = s;
	}

	public void setShowVelocities(boolean s) {
		showVelocities = s;
	}

	public void toggleShowAABBs() {
		setShowAABBs(!showAABBs);
	}

	public void toggleShowCollisionNormals() {
		setShowCollisionNormals(!showCollisionNormals);
	}

	public void toggleShowVelocities() {
		setShowVelocities(!showVelocities);
	}

	public void update() {
		// TODO: put that somewhere else
		if (inputs.isKeyPressed(Key.F5))
			toggleShowAABBs();
		if (inputs.isKeyPressed(Key.F6))
			toggleShowCollisionNormals();
		if (inputs.isKeyPressed(Key.F7))
			toggleShowVelocities();
	}
}