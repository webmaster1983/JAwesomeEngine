package space;

import integration.IntegrationSolver;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import manifold.CollisionManifold;
import manifold.ContactManifold;
import manifold.ManifoldManager;
import narrowphase.Narrowphase;
import objects.RigidBody;
import objects.Updateable;
import positionalcorrection.PositionalCorrection;
import quaternion.Rotation;
import resolution.CollisionResolution;
import utils.Pair;
import vector.Vector;
import broadphase.Broadphase;

public abstract class Space<L extends Vector, A1 extends Vector, A2 extends Rotation, A3 extends Rotation>
		implements Updateable {
	final IntegrationSolver integrationsolver;
	final Broadphase<L> broadphase;
	final Narrowphase<L> narrowphase;
	final CollisionResolution collisionresolution;
	final PositionalCorrection positionalcorrection;
	final ManifoldManager<L> manifoldmanager;
	protected List<RigidBody<L, A1, A2, A3>> objects;
	protected Set<Pair<RigidBody<L, ?, ?, ?>, RigidBody<L, ?, ?, ?>>> overlaps;
	protected L globalForce;

	public Space(IntegrationSolver integrationsolver, Broadphase<L> broadphase,
			Narrowphase<L> narrowphase,
			CollisionResolution collisionresolution,
			PositionalCorrection positionalcorrection,
			ManifoldManager<L> manifoldmanager) {
		this.integrationsolver = integrationsolver;
		this.broadphase = broadphase;
		this.narrowphase = narrowphase;
		this.collisionresolution = collisionresolution;
		this.positionalcorrection = positionalcorrection;
		this.manifoldmanager = manifoldmanager;
		objects = new ArrayList<RigidBody<L, A1, A2, A3>>();
		overlaps = new LinkedHashSet<Pair<RigidBody<L, ?, ?, ?>, RigidBody<L, ?, ?, ?>>>();
	}

	public void addRigidBody(RigidBody<L, A1, A2, A3> body) {
		broadphase.add(body);
		objects.add(body);
	}

	private void applyGlobalForce() {
		for (RigidBody<L, ?, ?, ?> rb : objects)
			rb.applyCentralForce(globalForce);
	}

	public void applyGlobalForce(L force) {
		for (RigidBody<L, ?, ?, ?> rb : objects)
			rb.applyCentralForce(force);
	}

	protected abstract void correct();

	public Broadphase<L> getBroadphase() {
		return broadphase;
	}

	public List<CollisionManifold<L>> getCollisionManifolds() {
		return manifoldmanager.getManifolds();
	}

	// public void addObject(CollisionObject obj) {
	// objects.add(obj);
	// }

	public CollisionResolution getCollsionResolution() {
		return collisionresolution;
	}

	public IntegrationSolver getIntegrationSolver() {
		return integrationsolver;
	}

	public Narrowphase<L> getNarrowphase() {
		return narrowphase;
	}

	public List<RigidBody<L, A1, A2, A3>> getObjects() {
		return objects;
	}

	public Set<Pair<RigidBody<L, ?, ?, ?>, RigidBody<L, ?, ?, ?>>> getOverlaps() {
		return overlaps;
	}

	public PositionalCorrection getPositionalCorrection() {
		return positionalcorrection;
	}

	protected abstract void integrate(float delta);

	public void removeRigidBody(RigidBody<L, A1, A2, A3> body) {
		objects.remove(body);
		broadphase.remove(body);
	}

	protected abstract void resolve();

	public void setGlobalForce(L force) {
		globalForce = force;
	}

	@Override
	public void update(int delta) {
		updateTimestep(delta / 1000f);
	}

	public void updateTimestep(float delta) {
		for (RigidBody<?, ?, ?, ?> o : objects)
			o.updateInverseRotation();

		System.out.println("Physics start");

		broadphase.update();
		overlaps = broadphase.getOverlaps();

		manifoldmanager.start();
		for (Pair<RigidBody<L, ?, ?, ?>, RigidBody<L, ?, ?, ?>> overlap : overlaps) {
			if (narrowphase
					.isColliding(overlap.getFirst(), overlap.getSecond())) {
				ContactManifold<L> contactManifold = narrowphase
						.computeCollision(overlap.getFirst(),
								overlap.getSecond());
				manifoldmanager.add(new CollisionManifold<L>(overlap,
						contactManifold));
			}
		}
		manifoldmanager.end();
		resolve();
		applyGlobalForce();
		for (RigidBody<?, ?, ?, ?> o : objects)
			System.out.println(o.getRotation().magnitude());
		integrate(delta);
		for (RigidBody<?, ?, ?, ?> o : objects)
			System.out.println(o.getRotation().magnitude());
		correct();

		// TESTING...
		// for (RigidBody<?, ?, ?, ?> o : objects) {
		// System.out.println(o.getRotation().magnitude());
		// // o.getRotation().setIdentity();
		// // System.out.println(o.getRotation() + "; "
		// // + o.getRotation().magnitude());
		// }
	}
}