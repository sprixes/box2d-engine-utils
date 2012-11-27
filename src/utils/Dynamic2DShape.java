package utils;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Dynamic2DShape {
	public World world;
	public float pixel_per_meter;
	public BodyEditorLoader loader;
	public boolean BodyEditorLoaderExist = false;

	/*
	 * initialize World
	 */
	public Dynamic2DShape(World world, float pixel_per_meter) {
		this.world = world;
		this.pixel_per_meter = pixel_per_meter;
	}

	public Vector2 ConvertPixelsToMeter(Vector2 n) {
		return n.div(pixel_per_meter);
	}

	public float ConvertPixelsToMeter(float n) {
		return n / pixel_per_meter;
	}

	public void setBodyEditorLoader(BodyEditorLoader loader) {
		this.loader = loader;
		BodyEditorLoaderExist = true;
	}

	public Body createDynamicShape(String target, BodyType type, float x, float y, float density, float friction, float restitution) {
		if (BodyEditorLoaderExist) {
			BodyDef def = new BodyDef();
			def.position.set(0, 0);
			def.type = type;
			Body box = world.createBody(def);

			FixtureDef fd = new FixtureDef();
			fd.density = density;
			fd.friction = friction;
			fd.restitution = restitution;
			loader.attachFixture(box, target, fd, 0);// scale set to 0;

			/*
			 * the default origin of box2d center center damn that shit
			 * loader.getOrigin(target, 0).cpy();// scale set to 0; FIXME
			 */

			return box;
		}
		System.out.println("[Dynamic2DShpae] BodyEditor loader not yet set!");
		return null;
	}

	/*
	 * returns Body EdgeShape
	 */

	public Body createEdge(BodyType type, float x1, float y1, float x2, float y2, float density) {
		return createEdge(type, x1, y1, x2, y2, density, 0.2f, 0);
	}

	public Body createEdge(BodyType type, float x1, float y1, float x2, float y2, float density, float friction, float restitution) {
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);

		PolygonShape poly = new PolygonShape();
		// poly.setAsBox(x2 - x1, y2 - y1);
		poly.setAsBox(ConvertPixelsToMeter(x2 - x1) / 2, ConvertPixelsToMeter(y2 - y1) / 2);

		FixtureDef fd = new FixtureDef();
		fd.shape = poly;
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		box.createFixture(fd);
		// box.createFixture(poly, density);
		box.setTransform(ConvertPixelsToMeter(x1 + x2) / 2, ConvertPixelsToMeter(y1 + y2) / 2, 0);

		poly.dispose();
		return box;
	}

	/*
	 * returns Body CircleShape registration for position left bottom
	 */
	public Body createCircle(DynamicDisplay target, BodyType type, float x, float y, float density, float friction, float restitution) {
		return createCircle(target, type, x, y, target.getBounds().getWidth(), density, friction, restitution);
	}

	public Body createCircle(DynamicSprite target, BodyType type, float x, float y, float density, float friction, float restitution) {
		return createCircle(target, type, x, y, target.getBounds().getWidth(), density, friction, restitution);
	}

	public Body createCircle(DynamicAnimation target, BodyType type, float x, float y, float density, float friction, float restitution) {
		return createCircle(target, type, x, y, target.getBounds().getWidth(), density, friction, restitution);
	}

	public Body createCircle(BodyType type, float x, float y, float distance, float density) {

		return createCircle(null, type, x, y, distance, density, 0.2f, 0);
	}

	public Body createCircle(DynamicDisplay target, BodyType type, float x, float y, float distance, float density, float friction, float restitution) {

		Vector2 position = new Vector2(x, y);
		float radius = distance / 2;
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);

		CircleShape poly = new CircleShape();
		// poly.setRadius(radius);
		poly.setRadius(ConvertPixelsToMeter(radius));
		FixtureDef fd = new FixtureDef();
		fd.shape = poly;
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		box.createFixture(fd);
		box.setTransform(ConvertPixelsToMeter(position.add(radius, radius)), box.getAngle());

		// box.createFixture(poly, density);
		poly.dispose();
		if (target != null) {
			box.setUserData(target);
		}
		return box;
	}

	/*
	 * returns Body BoxShape registration for position left bottom
	 */
	public Body createBox(DynamicDisplay target, BodyType type, float x, float y, float density, float friction, float restitution) {
		return createBox(target, type, x, y, target.getBounds().width, target.getBounds().height, density, friction, restitution);
	}

	public Body createBox(DynamicSprite target, BodyType type, float x, float y, float density, float friction, float restitution) {
		return createBox(target, type, x, y, target.getBounds().width, target.getBounds().height, density, friction, restitution);
	}

	public Body createBox(DynamicAnimation target, BodyType type, float x, float y, float density, float friction, float restitution) {
		return createBox(target, type, x, y, target.getBounds().width, target.getBounds().height, density, friction, restitution);
	}

	public Body createBox(BodyType type, float x, float y, float width, float height, float density) {

		return createBox(null, type, x, y, width, height, density, 0.2f, 0);
	}

	public Body createBox(DynamicDisplay target, BodyType type, float x, float y, float width, float height, float density, float friction, float restitution) {
		Vector2 position = new Vector2(x, y);
		float tempwidth = width / 2;
		float tempheight = height / 2;
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);

		PolygonShape poly = new PolygonShape();
		poly.setAsBox(ConvertPixelsToMeter(tempwidth), ConvertPixelsToMeter(tempheight));
		FixtureDef fd = new FixtureDef();
		fd.shape = poly;
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		box.createFixture(fd);
		box.setTransform(ConvertPixelsToMeter(position.x + (width / 2)), ConvertPixelsToMeter(position.y + (height / 2)), box.getAngle());
		// box.setTransform(ConvertPixelsToMeter(position.x),
		// ConvertPixelsToMeter(position.y), box.getAngle());

		// box.createFixture(poly, density);
		poly.dispose();
		if (target != null) {
			box.setUserData(target);
		}
		return box;
	}

	/*
	 * dynamicPolygon "TRIAL" FIXME
	 * 
	 * registration CENTER CENTER
	 */
	public Body createPolygon(DynamicDisplay target, BodyType type, float x, float y, float[] vertices, float density, float friction, float restitution) {
		Vector2 position = new Vector2(x, y);
		BodyDef def = new BodyDef();
		def.type = type;
		Body box = world.createBody(def);

		PolygonShape poly = new PolygonShape();
		poly.set(vertices);
		FixtureDef fd = new FixtureDef();
		fd.shape = poly;
		fd.density = density;
		fd.friction = friction;
		fd.restitution = restitution;
		box.createFixture(fd);
		box.setTransform(ConvertPixelsToMeter(position.x), ConvertPixelsToMeter(position.y), box.getAngle());
		// box.setTransform(ConvertPixelsToMeter(position.x),
		// ConvertPixelsToMeter(position.y), box.getAngle());

		// box.createFixture(poly, density);
		poly.dispose();
		if (target != null) {
			box.setUserData(target);
		}
		return box;
	}
}
