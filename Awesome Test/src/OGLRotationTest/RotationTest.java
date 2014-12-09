package OGLRotationTest;

import game.Debugger;
import game.StandardGame;
import loader.FontLoader;

public class RotationTest extends StandardGame {
	RotationObject b;
	Debugger debugmanager;

	@Override
	public void init() {
		initDisplay(false, 800, 600, true);
		debugmanager = new Debugger(inputs,
				FontLoader.loadFont("res/fonts/DejaVuSans.ttf"), cam);
		this.setRendering2d(true);
		cam.setFlyCam(true);
		cam.translateTo(0.5f, 0f, 5);
		cam.rotateTo(0, 0);

		b = new RotationObject(0, 0, 0, 1, 1, 1);
		addObject(b);
	}

	@Override
	public void render() {
		debugmanager.render3d();
		renderScene();
	}

	@Override
	public void render2d() {
		debugmanager.render2d(fps, objects.size());
		render2dScene();
	}

	@Override
	public void update(int delta) {
		b.rotateFurther(delta / 10f);
		debugmanager.update();
		cam.update(delta);
	}

}