package display;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.GLFW_ACCUM_ALPHA_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_ACCUM_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_ACCUM_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_ACCUM_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_ALPHA_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_AUX_BUFFERS;
import static org.lwjgl.glfw.GLFW.GLFW_BLUE_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_GREEN_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RED_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_SRGB_CAPABLE;
import static org.lwjgl.glfw.GLFW.GLFW_STENCIL_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_STEREO;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class GLDisplay extends Display {
	private long windowid;
	private GLFWErrorCallback errorCallback;
	private GLFWFramebufferSizeCallback sizeCallback;
	private boolean mousebound = false;

	@Override
	public void bindMouse() {
		glfwSetInputMode(windowid, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		mousebound = true;

		// HACK (drop 2 inputs to avoid wrong mouse values)
		this.swap();
		this.pollInputs();
		resetMouse();
		this.swap();
		this.pollInputs();
		resetMouse();
	}

	@Override
	public void clear() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT
				| GL_STENCIL_BUFFER_BIT);
	}

	@Override
	public void close() {
		glfwDestroyWindow(windowid);
		glfwTerminate();
		if (sizeCallback != null)
			sizeCallback.release();
		errorCallback.release();
	}

	public long getWindowID() {
		return windowid;
	}

	@Override
	public boolean isCloseRequested() {
		return glfwWindowShouldClose(windowid) == 1;
	}

	@Override
	public boolean isMouseBound() {
		return mousebound;
	}

	@Override
	public void open(DisplayMode displaymode, PixelFormat pixelformat) {
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));

		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		// See: http://www.glfw.org/docs/latest/window.html#window_hints_values
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, displaymode.isResizeable() ? GL_TRUE
				: GL_FALSE);
		glfwWindowHint(GLFW_RED_BITS, pixelformat.getBitsPerPixel());
		glfwWindowHint(GLFW_GREEN_BITS, pixelformat.getBitsPerPixel());
		glfwWindowHint(GLFW_BLUE_BITS, pixelformat.getBitsPerPixel());
		glfwWindowHint(GLFW_ALPHA_BITS, pixelformat.getAlpha());
		glfwWindowHint(GLFW_DEPTH_BITS, pixelformat.getDepth());
		glfwWindowHint(GLFW_STENCIL_BITS, pixelformat.getStencil());
		glfwWindowHint(GLFW_ACCUM_RED_BITS,
				pixelformat.getAccumulationBitsPerPixel());
		glfwWindowHint(GLFW_ACCUM_GREEN_BITS,
				pixelformat.getAccumulationBitsPerPixel());
		glfwWindowHint(GLFW_ACCUM_BLUE_BITS,
				pixelformat.getAccumulationBitsPerPixel());
		glfwWindowHint(GLFW_ACCUM_ALPHA_BITS,
				pixelformat.getAccumulationAlpha());
		glfwWindowHint(GLFW_AUX_BUFFERS, pixelformat.getAuxBuffers());
		glfwWindowHint(GLFW_SAMPLES, pixelformat.getSamples());
		// glfwWindowHint(GLFW_REFRESH_RATE, ???);
		glfwWindowHint(GLFW_STEREO, pixelformat.isStereo() ? GL_TRUE : GL_FALSE);
		glfwWindowHint(GLFW_SRGB_CAPABLE, pixelformat.isSRGB() ? GL_TRUE
				: GL_FALSE);

		width = displaymode.getWidth();
		height = displaymode.getHeight();

		windowid = glfwCreateWindow(width, height, displaymode.getTitle(),
				NULL, NULL);
		if (windowid == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(windowid, (GLFWvidmode.width(vidmode) - width) / 2,
				(GLFWvidmode.height(vidmode) - height) / 2);

		glfwMakeContextCurrent(windowid);
		glfwSwapInterval(displaymode.isVSync() ? 1 : 0);

		glfwShowWindow(windowid);

		GLContext.createFromCurrent();

		if (displaymode.isResizeable()) {
			glfwSetFramebufferSizeCallback(windowid,
					sizeCallback = new GLFWFramebufferSizeCallback() {
						@Override
						public void invoke(long arg0, int w, int h) {
							width = w;
							height = h;
							glViewport(0, 0, width, height);
						}
					});
		}
	}

	@Override
	public void pollInputs() {
		glfwPollEvents();
	}

	@Override
	public void resetMouse() {
		glfwSetCursorPos(windowid, 0, 0);
	}

	@Override
	public void swap() {
		glfwSwapBuffers(windowid);
	}

	@Override
	public void unbindMouse() {
		glfwSetInputMode(windowid, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		mousebound = false;
	}
}