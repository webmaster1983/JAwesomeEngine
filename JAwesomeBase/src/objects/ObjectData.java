package objects;

/**
 * Stores constants to determine which type of shape is used.
 * 
 * @author Oliver Schall
 * 
 */
// TODO: remove?
public interface ObjectData {
	public static final int SHAPE = 0;
	public static final int SHAPE_QUAD = 1;
	public static final int SHAPE_CIRCLE = 2;
	public static final int SHAPE_ELLIPSE = 3;
	public static final int SHAPE_BOX = 1;
	public static final int SHAPE_SPHERE = 2;
	public static final int SHAPE_CAPSULE = 3;
	public static final int SHAPE_CYLINDER = 4;
	public static final int SHAPE_TERRAIN = 5;

	/**
	 * Gets the type of the shape in an integer. Use SHAPE constants for
	 * comparison.
	 * 
	 * @return shape type
	 */
	public int getShapeType();
}