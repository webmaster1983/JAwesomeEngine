package anim;

import objects.BaseObject;
import objects.BaseObject2;
import quaternion.Complexf;
import vector.Vector2f;
import curves.BezierCurve2;
import curves.SimpleAngularCurvePath;
import curves.SimpleCurvePath;
import curves.SquadCurve2;

public class Skeleton2 extends Skeleton<Vector2f, Complexf> {
	boolean mirroredX = false;
	boolean mirroredY = false;
	boolean invertRotation = false;

	public Skeleton2(Animation<Vector2f, Complexf> animation) {
		super(animation, new BaseObject2[0]);
		nullvec = new Vector2f();
		nullrot = new Complexf();
	}

	public Skeleton2(Animation<Vector2f, Complexf> animation,
			BaseObject<Vector2f, Complexf>[] bodypart) {
		super(animation, bodypart);
		nullvec = new Vector2f();
		nullrot = new Complexf();
	}

	@Override
	protected void updateAnimation(float animationTimer) {
		Animation<Vector2f, Complexf> currentAnimation = (dynamicAnimationTransition != null) ? dynamicAnimationTransition
				: animation;
		for (int i = 0; i < bodyparts.size(); i++) {
			BaseObject<Vector2f, Complexf> part = bodyparts.get(i);
			Vector2f trans = currentAnimation.getAnimationTranslationPath(i)
					.getPoint(animationTimer);
			if (mirroredX)
				trans.x = -trans.x;
			if (mirroredY)
				trans.y = -trans.y;
			part.translate(trans);
			Complexf rot = currentAnimation.getAnimationRotationPath(i)
					.getRotation(animationTimer);
			if (invertRotation)
				rot.invert();
			part.rotate(rot);
		}
	}

	// TODO: Optimize!
	public void setDynamicAnimation(
			Animation<Vector2f, Complexf> animationparam,
			float dynamicAnimationSpeed) {
		dynamicAnimationTransition = new DynamicAnimationTransition<Vector2f, Complexf>(
				animationparam, dynamicAnimationSpeed);
		for (int i = 0; i < bodyparts.size(); i++) {
			Vector2f trans = animation.getAnimationTranslationPath(i).getPoint(
					animationTimer);
			SimpleCurvePath<Vector2f> translationpath = new SimpleCurvePath<Vector2f>(
					dynamicAnimationTransition.getAnimationTranslationPath(i));
			SimpleAngularCurvePath<Complexf> rotationpath = new SimpleAngularCurvePath<Complexf>(
					dynamicAnimationTransition.getAnimationRotationPath(i));
			BezierCurve2 oldcurve = (BezierCurve2) translationpath.getCurves()
					.get(0);
			SquadCurve2 oldrotcurve = (SquadCurve2) rotationpath.getCurves()
					.get(0);
			translationpath.getCurves().set(
					0,
					new BezierCurve2(trans, oldcurve.getP1(), oldcurve.getP2(),
							oldcurve.getP3()));
			// TODO: fix, when squadcurves are properly fixed.
			Complexf newSquadrotation = animation.getAnimationRotationPath(i)
					.getRotation(animationTimer);
			rotationpath.getCurves().set(
					0,
					new SquadCurve2(newSquadrotation, newSquadrotation,
							oldrotcurve.getR2(), oldrotcurve.getR3()));
			dynamicAnimationTransition.getAnimationTranslationPaths().set(i,
					translationpath);
			dynamicAnimationTransition.getAnimationRotationPaths().set(i,
					rotationpath);
		}
		animation = animationparam;
		animationTimer = 0;
	}

	public void mirrorX() {
		mirroredX = !mirroredX;
	}

	public boolean isMirroredX() {
		return mirroredX;
	}

	public void setMirroredX(boolean mirroredX) {
		this.mirroredX = mirroredX;
	}

	public void mirrorY() {
		mirroredY = !mirroredY;
	}

	public boolean isMirroredY() {
		return mirroredY;
	}

	public void setMirroredY(boolean mirroredY) {
		this.mirroredY = mirroredY;
	}

	public void invertRotation() {
		invertRotation = !invertRotation;
	}

	public boolean isRotationInverted() {
		return invertRotation;
	}

	public void setRotationInverted(boolean invertRotation) {
		this.invertRotation = invertRotation;
	}
}