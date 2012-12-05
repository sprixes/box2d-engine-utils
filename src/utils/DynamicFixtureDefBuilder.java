package utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;

public class DynamicFixtureDefBuilder {

	FixtureDef fixtureDef;
	float pixel_per_meter;

	public DynamicFixtureDefBuilder(float pixel_per_meter) {
		this.pixel_per_meter = pixel_per_meter;
		reset();
	}

	/* changes */

	public float ConvertPixelsToMeter(float n) {
		return n / pixel_per_meter;
	}

	public DynamicFixtureDefBuilder sensor() {
		fixtureDef.isSensor = true;
		return this;
	}

	public DynamicFixtureDefBuilder boxShape(DynamicDisplay target) {

		return boxShape(target.getBounds().getWidth() / 2, target.getBounds().getHeight() / 2);
	}

	public DynamicFixtureDefBuilder boxShape(float hx, float hy) {
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(ConvertPixelsToMeter(hx), ConvertPixelsToMeter(hy));
		fixtureDef.shape = shape;
		return this;
	}

	public DynamicFixtureDefBuilder circleShape(DynamicDisplay target) {
		return circleShape(target.getBounds().width / 2);
	}

	public DynamicFixtureDefBuilder circleShape(float radius) {
		Shape shape = new CircleShape();
		shape.setRadius(ConvertPixelsToMeter(radius));
		fixtureDef.shape = shape;
		return this;
	}

	public DynamicFixtureDefBuilder polygonShape(Vector2[] vertices) {
		/* test */
		for (int index = 0; index < vertices.length; index++) {
			vertices[index].div(pixel_per_meter);
		}

		PolygonShape shape = new PolygonShape();
		shape.set(vertices);
		fixtureDef.shape = shape;
		return this;
	}

	public DynamicFixtureDefBuilder density(float density) {
		fixtureDef.density = density;
		return this;
	}

	public DynamicFixtureDefBuilder friction(float friction) {
		fixtureDef.friction = friction;
		return this;
	}

	public DynamicFixtureDefBuilder restitution(float restitution) {
		fixtureDef.restitution = restitution;
		return this;
	}

	public DynamicFixtureDefBuilder categoryBits(short categoryBits) {
		fixtureDef.filter.categoryBits = categoryBits;
		return this;
	}

	public DynamicFixtureDefBuilder maskBits(short maskBits) {
		fixtureDef.filter.maskBits = maskBits;
		return this;
	}

	private void reset() {
		fixtureDef = new FixtureDef();

	}

	public FixtureDef build() {
		FixtureDef fixtureDef = this.fixtureDef;
		reset();
		return fixtureDef;
	}

}