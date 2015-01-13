package renderToTexture;

import game.Debugger;
import game.StandardGame;
import gui.DisplayMode;
import gui.GLDisplay;
import gui.PixelFormat;
import gui.VideoSettings;
import loader.FontLoader;
import loader.ModelLoader;
import loader.ShaderLoader;
import shape.Box;
import utils.RenderToTexture;
import utils.Shader;

public class RenderTest extends StandardGame {
	RenderToTexture rtt;
	Shader screenshader;
	Debugger debugmanager;

	@Override
	public void init() {
		initDisplay(new GLDisplay(), new DisplayMode(), new PixelFormat(),
				new VideoSettings());
		display.bindMouse();
		cam.setFlyCam(true);
		cam.translateTo(0, 2, 20);
		cam.rotateTo(0, 0);
		debugmanager = new Debugger(inputs,
				FontLoader.loadFont("res/fonts/DejaVuSans.ttf"), cam);
		this.setRendering2d(true);
		addObject(ModelLoader.load("res/models/bunny.mobj"));

		rtt = new RenderToTexture(this);
		rtt.updateTexture();

		screenshader = new Shader(ShaderLoader.loadShaderPair(
				"res/shaders/textureshader.vert",
				"res/shaders/textureshader.frag"));
		screenshader.addArgumentName("colorMap");
		screenshader.addArgument(rtt.getTextureID());

		Box screen = new Box(2, 3, 12, 2, 1, 0.1f);
		addObject(screen);
	}

	@Override
	public void render() {
		debugmanager.render3d();
		renderScene();
	}

	@Override
	public void render2d() {
		debugmanager.render2d(fps, objects.size(), objects2d.size());
	}

	@Override
	public void update(int delta) {
		rtt.updateTexture();
		debugmanager.update();
		cam.update(delta);
	}

}
