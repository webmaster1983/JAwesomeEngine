package textureCoordinateTest;

import game.StandardGame;
import loader.ShaderLoader;
import loader.TextureLoader;
import shader.Shader;
import shape.Box;
import shape.Capsule;
import shape.Cylinder;
import shape.Sphere;
import texture.Texture;
import display.DisplayMode;
import display.GLDisplay;
import display.PixelFormat;
import display.VideoSettings;

public class TextureCoordinateTest extends StandardGame {

	@Override
	public void init() {
		initDisplay(new GLDisplay(), new DisplayMode(), new PixelFormat(),
				new VideoSettings());
		display.bindMouse();
		cam.setFlyCam(true);
		cam.translateTo(0, 0, 5);
		cam.rotateTo(0, 0);

		Texture texture = new Texture(
				TextureLoader
						.loadTexture("res/textures/textureCoordinateTest.png"));

		Shader textureshader = new Shader(ShaderLoader.loadShaderFromFile(
				"res/shaders/textureshader.vert",
				"res/shaders/textureshader.frag"));
		textureshader.addArgumentName("texture");
		textureshader.addArgument(texture);

		Box b = new Box(-1, 0, 0, 1, 1, 1);
		b.setRenderHints(false, true, false);
		b.setShader(textureshader);
		addObject(b);

		Sphere s = new Sphere(2, 0, 0, 1, 6, 6);
		s.setRenderHints(false, true, false);
		s.setShader(textureshader);
		addObject(s);

		Capsule c = new Capsule(5, 0, 0, 1, 2, 36, 36);
		c.setRenderHints(false, true, false);
		c.setShader(textureshader);
		addObject(c);

		Cylinder cy = new Cylinder(8, 0, 0, 1, 2, 36);
		cy.setRenderHints(false, true, false);
		cy.setShader(textureshader);
		addObject(cy);
	}

	@Override
	public void render() {
		renderScene();
	}

	@Override
	public void render2d() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(int delta) {
		cam.update(delta);
	}
}