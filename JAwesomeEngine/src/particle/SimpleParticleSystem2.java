package particle;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

import objects.ShapedObject;
import objects.ShapedObject2;
import vector.Vector2f;
import vector.Vector3f;

public class SimpleParticleSystem2 extends ParticleSystem2 {
	ShapedObject2 particles;
	HashMap<Integer, Particle> particleList;
	LinkedList<Integer> freeindices;
	int maxParticles;

	public SimpleParticleSystem2(Vector2f center) {
		particles = new ShapedObject2(center);
		particles.setRenderMode(GL11.GL_TRIANGLES);
		particleList = new HashMap<Integer, Particle>();
		freeindices = new LinkedList<Integer>();
		maxParticles = 0;
	}

	private final Vector2f topleft = new Vector2f(0, 0), bottomleft = new Vector2f(0, 1),
			bottomright = new Vector2f(1, 1), topright = new Vector2f(1, 0);

	@Override
	public int addParticle(Vector2f position, Vector2f velocity, Vector2f size, int lifetime) {
		return addParticle(new Particle(position, velocity, lifetime, size));
	}

	public int addParticle(Particle particle) {
		Integer pos = freeindices.poll();
		int insertpos;
		Vector3f color = new Vector3f(1, 1, 1);
		if (pos != null) {
			insertpos = pos;
			pos *= 4;
			particles.setVertex(pos,
					new Vector2f(particle.position.x - particle.size.x, particle.position.y - particle.size.y), color,
					topleft);
			particles.setVertex(pos + 1,
					new Vector2f(particle.position.x - particle.size.x, particle.position.y + particle.size.y), color,
					topright);
			particles.setVertex(pos + 2,
					new Vector2f(particle.position.x + particle.size.x, particle.position.y + particle.size.y), color,
					bottomright);
			particles.setVertex(pos + 3,
					new Vector2f(particle.position.x + particle.size.x, particle.position.y - particle.size.y), color,
					bottomleft);
			int indexpos = insertpos * 6;
			particles.setIndex(indexpos, pos);
			particles.setIndex(indexpos + 1, pos + 1);
			particles.setIndex(indexpos + 2, pos + 2);
			particles.setIndex(indexpos + 3, pos);
			particles.setIndex(indexpos + 4, pos + 2);
			particles.setIndex(indexpos + 5, pos + 3);
		} else {
			particles.addVertex(
					new Vector2f(particle.position.x - particle.size.x, particle.position.y - particle.size.y), color,
					topleft);
			particles.addVertex(
					new Vector2f(particle.position.x - particle.size.x, particle.position.y + particle.size.y), color,
					topright);
			particles.addVertex(
					new Vector2f(particle.position.x + particle.size.x, particle.position.y + particle.size.y), color,
					bottomright);
			particles.addVertex(
					new Vector2f(particle.position.x + particle.size.x, particle.position.y - particle.size.y), color,
					bottomleft);
			insertpos = maxParticles;
			pos = maxParticles * 4;
			particles.addIndices(pos, pos + 1, pos + 2, pos, pos + 2, pos + 3);
			maxParticles++;
		}
		particleList.put(insertpos, particle);
		return insertpos;
	}

	public Particle getParticle(int particleID) {
		return particleList.get(particleID);
	}

	private final Vector2f nullvec = new Vector2f(0, 0);

	@Override
	public void removeParticle(int particleID) {
		particleList.remove(particleID);
		int i4 = particleID * 4;
		particles.setVertex(i4 + 3, nullvec);
		particles.setVertex(i4 + 2, nullvec);
		particles.setVertex(i4 + 1, nullvec);
		particles.setVertex(i4, nullvec);
		int i6 = particleID * 6;
		particles.setIndex(i6 + 5, 0);
		particles.setIndex(i6 + 4, 0);
		particles.setIndex(i6 + 3, 0);
		particles.setIndex(i6 + 2, 0);
		particles.setIndex(i6 + 1, 0);
		particles.setIndex(i6, 0);
		freeindices.add(particleID);
	}

	@Override
	public void updateParticles(int delta, float maxLifeTime) {
		// TODO: parallel
		for (int i = 0; i < maxParticles; i++) {
			Particle p = particleList.get(i);
			if (p != null) {
				if (p.lifetime > delta) {
					p.lifetime -= delta;
					float stepX = p.velocity.x * delta;
					float stepY = p.velocity.y * delta;
					p.position.x += stepX;
					p.position.y += stepY;
					int i4 = i * 4;
					particles.getVertex(i4).x += stepX;
					particles.getVertex(i4).y += stepY;
					particles.getVertex(i4 + 1).x += stepX;
					particles.getVertex(i4 + 1).y += stepY;
					particles.getVertex(i4 + 2).x += stepX;
					particles.getVertex(i4 + 2).y += stepY;
					particles.getVertex(i4 + 3).x += stepX;
					particles.getVertex(i4 + 3).y += stepY;
					float particleAlpha = p.lifetime / (float) maxLifeTime;
					particles.getColor(i4).x = particleAlpha;
					// particles.getColor(i4 + 1).x = particleAlpha;
					// particles.getColor(i4 + 2).x = particleAlpha;
					// particles.getColor(i4 + 3).x = particleAlpha;
				} else {
					removeParticle(i);
				}
			}
		}
		particles.prerender();
	}

	protected class Particle {
		Vector2f position, velocity, size;
		int lifetime;

		protected Particle(Vector2f position, Vector2f velocity, int lifetime, Vector2f size) {
			this.position = position;
			this.velocity = velocity;
			this.lifetime = lifetime;
			this.size = size;
		}
	}

	@Override
	public void render() {
		particles.render();
	}

	@Override
	public void delete() {
		particles.delete();
	}

	@Override
	public FloatBuffer getMatrixBuffer() {
		return particles.getMatrixBuffer();
	}

	@Override
	public ShapedObject<Vector2f, ?> getParticleObject() {
		return particles;
	}
}