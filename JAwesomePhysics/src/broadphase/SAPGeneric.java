package broadphase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import objects.CollisionShape;
import utils.Pair;
import vector.Vector3f;

public class SAPGeneric<ObjectType extends CollisionShape<Vector3f, ?, ?>> extends SweepAndPrune<Vector3f, ObjectType> {
	final List<SweepPoint> axisX, axisY, axisZ;

	public SAPGeneric() {
		axisX = new ArrayList<SweepPoint>();
		axisY = new ArrayList<SweepPoint>();
		axisZ = new ArrayList<SweepPoint>();
	}

	@Override
	public void add(ObjectType object) {
		objects.add(object);
		axisX.add(new SweepPoint(object, true, 0));
		axisX.add(new SweepPoint(object, false, 0));
		axisY.add(new SweepPoint(object, true, 1));
		axisY.add(new SweepPoint(object, false, 1));
		axisZ.add(new SweepPoint(object, true, 2));
		axisZ.add(new SweepPoint(object, false, 2));
	}

	@Override
	public Set<ObjectType> raycast() {
		System.err.println("No raycasts possible in SAP.");
		return null;
	}

	@Override
	public void remove(ObjectType object) {
		for (int i = 0; i < axisX.size();)
			if (axisX.get(i).object.equals(object))
				axisX.remove(i);
			else
				i++;
		for (int i = 0; i < axisY.size();)
			if (axisY.get(i).object.equals(object))
				axisY.remove(i);
			else
				i++;
		for (int i = 0; i < axisZ.size();)
			if (axisZ.get(i).object.equals(object))
				axisZ.remove(i);
			else
				i++;
		Iterator<Entry<Pair<ObjectType, ObjectType>, SweepAndPrune<Vector3f, ObjectType>.Counter>> it = counters
				.entrySet().iterator();
		while (it.hasNext()) {
			if (it.next().getKey().contains(object)) {
				it.remove();
			}
		}
		for (int i = 0; i < overlaps.size();)
			if (overlaps.get(i).contains(object))
				overlaps.remove(i);
			else
				i++;
		objects.remove(object);
	}

	@Override
	public void update() {
		sortAxis(axisX);
		sortAxis(axisY);
		sortAxis(axisZ);

		Iterator<Entry<Pair<ObjectType, ObjectType>, Counter>> iter = counters.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Pair<ObjectType, ObjectType>, Counter> entry = iter.next();
			Counter c = entry.getValue();
			Pair<ObjectType, ObjectType> pair = entry.getKey();

			if (c.wasOverlapping) {
				// report separation
				if (c.overlaps < 3) {
					overlaps.remove(pair);
					c.wasOverlapping = false;

					for (BroadphaseListener<Vector3f, ObjectType> listener : listeners) {
						listener.overlapEnded(pair.getFirst(), pair.getSecond());
					}
				}
			} else {
				// report overlap
				if (c.overlaps > 2) {
					overlaps.add(pair);
					c.wasOverlapping = true;

					for (BroadphaseListener<Vector3f, ObjectType> listener : listeners) {
						listener.overlapStarted(pair.getFirst(), pair.getSecond());
					}
				}
			}

			if (c.overlaps < 1) {
				iter.remove();
			}
		}
	}
}