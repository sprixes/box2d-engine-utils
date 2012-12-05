package utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;

public class DynamicBodyBuilder {

	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private Object userData = null;
	private Vector2 position = new Vector2();
	private final World world;
	private float angle;

	DynamicFixtureDefBuilder fixtureDefBuilder;

	private MassData massData = new MassData();
	private boolean massSet;
	public float pixel_per_meter;

	/* changes */

	public DynamicBodyBuilder(World world, float pixel_per_meter) {
		this.world = world;
		this.pixel_per_meter = pixel_per_meter;
		this.fixtureDefBuilder = new DynamicFixtureDefBuilder(pixel_per_meter);
		this.fixtureDef = new FixtureDef();
		reset(true);
	}

	public DynamicFixtureDefBuilder getfixtureDefBuilder() {
		return fixtureDefBuilder;
	}

	private void reset(boolean disposeShapes) {
		if (fixtureDef.shape != null && disposeShapes) {
			fixtureDef.shape.dispose();
		}
		bodyDef = new BodyDef();
		angle = 0f;
		userData = null;
		position.set(0f, 0f);
		massSet = false;
	}

	public DynamicBodyBuilder type(BodyType type) {
		bodyDef.type = type;
		return this;
	}

	public DynamicBodyBuilder bullet() {
		bodyDef.bullet = true;
		return this;
	}

	public DynamicBodyBuilder fixedRotation() {
		bodyDef.fixedRotation = true;
		return this;
	}

	public DynamicBodyBuilder awake(boolean awake) {
		bodyDef.awake = awake;
		return this;
	}

	public DynamicBodyBuilder active(boolean active) {
		bodyDef.active = active;
		return this;
	}

	public DynamicBodyBuilder linearDamping(float linearDamping) {
		bodyDef.linearDamping = linearDamping;
		return this;
	}

	public DynamicBodyBuilder angularDamping(float angularDamping) {
		bodyDef.angularDamping = angularDamping;
		return this;
	}

	public DynamicBodyBuilder fixture(FixtureDef fixtureDef) {
		this.fixtureDef = fixtureDef;
		return this;
	}

	public DynamicBodyBuilder mass(float mass) {
		this.massData.mass = mass;
		this.massSet = true;
		return this;
	}

	public DynamicBodyBuilder inertia(float intertia) {
		this.massData.I = intertia;
		this.massSet = true;
		return this;
	}

	public DynamicBodyBuilder userData(Object userData) {
		this.userData = userData;
		return this;
	}

	public DynamicBodyBuilder position(float x, float y) {
		this.position.set(x, y);
		return this;
	}

	public DynamicBodyBuilder angle(float angle) {
		this.angle = angle;
		return this;
	}

	public Body build() {
		return build(true);
	}

	public Body build(boolean disposeShapes) {
		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		if (massSet) {
			MassData bodyMassData = body.getMassData();
			massData.center.set(bodyMassData.center);
			body.setMassData(massData);
		}
		if (userData != null) {
			body.setUserData(userData);
		}

		body.setTransform(ConvertPixelsToMeter(position), angle);

		reset(disposeShapes);
		return body;
	}

	public Vector2 ConvertPixelsToMeter(Vector2 n) {
		return n.div(pixel_per_meter);
	}
}