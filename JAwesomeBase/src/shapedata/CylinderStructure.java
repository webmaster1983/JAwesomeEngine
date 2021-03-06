package shapedata;

import objects.ShapeStructure;
import vector.Vector3f;

/**
 * Class structure for cylinders.
 * 
 * @author Oliver Schall
 * 
 */

public interface CylinderStructure extends ShapeStructure<Vector3f> {
	/**
	 * Gets the half height of the cylinder.
	 * 
	 * @return half height of the cylinder
	 */
	public float getHalfHeight();

	/**
	 * Gets the height of the cylinder.
	 * 
	 * @return height of the cylinder
	 */
	public float getHeight();

	/**
	 * Gets the radius of the cylinder.
	 * 
	 * @return radius of the cylinder
	 */
	public float getRadius();
}
