package utils;

import java.util.Iterator;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

public class DynamicScreen extends InputMultiplexer implements Screen, InputProcessor, ContactListener {
	public static class DynamicCamera extends Camera implements TweenAccessor<DynamicCamera> {

		protected static final int TWEEN_XY = 1;
		protected static final int TWEEN_ZOOM = 2;
		protected static final int TWEEN_ROTATION = 3;

		public Tween tween;

		public TweenCallback tweenCallback;
		public TweenManager tweenManager = new TweenManager();
		public long tweenDeltaTime = System.currentTimeMillis();

		public float zoom = 1f;
		public float rotation = 0f;

		public Matrix4 box2Dprojection = new Matrix4();

		/** Only invoke once. If not called, tween will not work. */
		public static void register() {
			Tween.registerAccessor(DynamicCamera.class, new DynamicCamera());
		}

		@Override
		public int getValues(DynamicCamera target, int type, float[] returnValues) {
			switch (type) {
				case TWEEN_XY:
					returnValues[0] = target.position.x;
					returnValues[1] = target.position.y;
					return 2;
				case TWEEN_ZOOM:
					returnValues[0] = target.zoom;
					return 1;
				case TWEEN_ROTATION:
					returnValues[0] = target.rotation;
					return 1;
				default:
					assert false;
					return -1;
			}
		}

		public Tween interpolateZoom(float targetScaleX, float targetScaleY, TweenEquation equation, int duration, boolean autoStart) {
			tween = Tween.to(this, TWEEN_ZOOM, duration).target(targetScaleX, targetScaleY).ease(equation);
			tweenDeltaTime = System.currentTimeMillis();
			if (autoStart)
				tween.start(tweenManager);
			return tween;

		}

		public Tween interpolateRotation(int targetRotation, TweenEquation equation, int duration, boolean autoStart) {
			tween = Tween.to(this, TWEEN_ROTATION, duration).target(targetRotation).ease(equation);
			tweenDeltaTime = System.currentTimeMillis();
			if (autoStart)
				tween.start(tweenManager);
			return tween;
		}

		public Tween interpolateXY(Vector2 target, TweenEquation equation, int duration, boolean autoStart) {
			tween = Tween.to(this, TWEEN_XY, duration).target(target.x, target.y).ease(equation);
			tweenDeltaTime = System.currentTimeMillis();
			if (autoStart)
				tween.start(tweenManager);
			return tween;
		}

		@Override
		public void setValues(DynamicCamera target, int type, float[] newValues) {
			switch (type) {
				case TWEEN_XY:
					target.position.set(newValues[0], newValues[1], 0);
					break;
				case TWEEN_ZOOM:
					target.zoom = newValues[0];
					break;
				case TWEEN_ROTATION:
					target.rotation = newValues[0];
					break;
			}
		}

		public void shake() {
			tweenManager.update(1000);
			interpolateXY(new Vector2(position.x - 15, position.y), Linear.INOUT, 40, true);
			interpolateXY(new Vector2(position.x + 15, position.y), Linear.INOUT, 40, true).delay(15);
			interpolateXY(new Vector2(position.x - 15, position.y), Linear.INOUT, 40, true).delay(30);
			interpolateXY(new Vector2(position.x + 15, position.y), Linear.INOUT, 40, true).delay(45);
			interpolateXY(new Vector2(position.x - 15, position.y), Linear.INOUT, 40, true).delay(60);
			interpolateXY(new Vector2(position.x + 15, position.y), Linear.INOUT, 40, true).delay(75);
			interpolateXY(new Vector2(position.x - 15, position.y), Linear.INOUT, 40, true).delay(90);
			interpolateXY(new Vector2(position.x + 15, position.y), Linear.INOUT, 40, true).delay(105);
			interpolateXY(new Vector2(position.x, position.y), Linear.INOUT, 40, true).delay(120);
		}

		@Override
		public void update() {
			tweenManager.update((int) (System.currentTimeMillis() - tweenDeltaTime));
			tweenDeltaTime = System.currentTimeMillis();
			super.projection.setToOrtho2D(position.x, position.y, viewportWidth * zoom, viewportHeight * zoom, near, far);
			box2Dprojection.setToOrtho2D(position.x / PIXEL_PER_METER, position.y / PIXEL_PER_METER, (viewportWidth / PIXEL_PER_METER) * zoom, (viewportHeight / PIXEL_PER_METER) * zoom, near, far);
		}

		@Override
		public void update(boolean updateFrustum) {
			// TODO Auto-generated method stub

		}

	}

	protected AssetManager assetManager;

	protected Game game;

	protected SpriteBatch spriteBatch;

	protected DynamicCamera camera;

	protected World world;

	protected Box2DDebugRenderer box2dDebugRenderer;

	protected Vector2 gravity = new Vector2(0, -20);

	public static final float PIXEL_PER_METER = 10f;

	public Dynamic2DShape dynamic2dShape;

	public DynamicBodyBuilder dynamicBodyBuilder;

	public Body hitBody = null;

	public Vector3 testPoint = new Vector3();

	public boolean Box2DDebug = true;

	public DynamicScreen(Game game) {
		super();

		this.game = game;
		Gdx.input.setInputProcessor(this);
		camera = new DynamicCamera();
		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportHeight = Gdx.graphics.getHeight();
		camera.near = 1;
		camera.far = -1;
		spriteBatch = new SpriteBatch(100);
		world = new World(gravity, true);
		box2dDebugRenderer = new Box2DDebugRenderer();
		dynamic2dShape = new Dynamic2DShape(world, PIXEL_PER_METER);
		dynamicBodyBuilder = new DynamicBodyBuilder(world, PIXEL_PER_METER);
		Gdx.input.setInputProcessor(this);

	}

	public DynamicScreen(Game game, float width, float height) {
		super();

		this.game = game;
		Gdx.input.setInputProcessor(this);
		camera = new DynamicCamera();

		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.near = 1;
		camera.far = -1;

		spriteBatch = new SpriteBatch(100);

		world = new World(gravity, true);
		box2dDebugRenderer = new Box2DDebugRenderer();
		dynamic2dShape = new Dynamic2DShape(world, PIXEL_PER_METER);
		dynamicBodyBuilder = new DynamicBodyBuilder(world, PIXEL_PER_METER);
		Gdx.input.setInputProcessor(this);
		world.setContactListener(this);
	}

	public QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture(Fixture fixture) {
			// if the hit point is inside the fixture of the body
			// we report it
			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				// hitBody = fixture.getBody();
				// hitBody.destroyFixture(fixture);
				// System.out.println(hitBody);

				return false;
			} else {

				return true;
			}
		}
	};

	public float getPixelPermeter() {
		return PIXEL_PER_METER;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {

		camera.update();
		if (delta > 1) {
			System.out.println(delta);
		}
		// world.step(delta, 8, 5);
		world.step(1f / 60f, 8, 5);
		if (Box2DDebug) {
			box2dDebugRenderer.render(world, camera.box2Dprojection);
		}

		spriteBatch.setProjectionMatrix(camera.projection);
		/* Render DynamicDisplay */
		Iterator<Body> bi = world.getBodies();
		while (bi.hasNext()) {
			Body body = bi.next();
			DynamicDisplay target = (DynamicDisplay) body.getUserData();
			if (target != null && body != null) {
				if (target.visible) {
					spriteBatch.begin();
					target.render(spriteBatch);
					spriteBatch.end();
					target.update(delta);
					if (body.isActive()) {
						target.setPosition(body.getPosition().x * PIXEL_PER_METER, body.getPosition().y * PIXEL_PER_METER);
						target.setRotation(MathUtils.radiansToDegrees * body.getAngle());

					}

				}

			}
		}
	}

	public DynamicCamera getCamera() {
		return camera;
	}

	@Override
	public void resize(int width, int height) {

		System.out.print(width + " ");
		System.out.println(height);

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean keyDown(int keycode) {

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// hitBody = null;
		Vector2 position = new Vector2(screenX, screenY);

		position.x *= (float) 1280 / Gdx.graphics.getWidth();
		position.y = (Gdx.graphics.getHeight() - position.y) * 800 / Gdx.graphics.getHeight();
		position.add(camera.position.x, camera.position.y);
		position.div(PIXEL_PER_METER);
		testPoint.set(position.x, position.y, 0);
		System.out.println(position);
		world.QueryAABB(callback, testPoint.x - 0.0001f, testPoint.y - 0.0001f, testPoint.x + 0.0001f, testPoint.y + 0.0001f);

		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		Vector2 position = new Vector2(screenX, screenY);
		position.x *= (float) 1280 / Gdx.graphics.getWidth();
		position.y = (Gdx.graphics.getHeight() - position.y) * 800 / Gdx.graphics.getHeight();
		position.add(camera.position.x, camera.position.y);
		// System.out.println(position);
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	/* contact listener */
	/*
	 * BeginContact - fired when two fixtures start contacting (aka touching)
	 * each other
	 * 
	 * /*EndContact - fired when two fixtures cease contact
	 */
	/*
	 * /* PreSolve -fired before contact is resolved. you have the opportunity
	 * to override the contact here.
	 */
	/*
	 * PostSolve - fired once the contact is resolved. the event also includes
	 * the impulse from the contact.
	 */
	@Override
	public void beginContact(Contact contact) {

	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

	public Vector2 ConvertPixelsToMeter(Vector2 n) {
		return n.div(getPixelPermeter());
	}

	public float ConvertPixelsToMeter(float n) {
		return n / getPixelPermeter();
	}
}
