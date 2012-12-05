package utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class DynamicToggleButton extends DynamicButton {

    public DynamicToggleButton(TextureRegion upstate, TextureRegion hoverstate, TextureRegion downstate, Vector2 position) {
	super(upstate, hoverstate, downstate, position);
    }

    @Override
    public void inputDown(float x, float y) {
	if (enabled && visible && getBounds().contains(x, y)) {
	    if (callback != null)
		callback.onButtonEvent(this, DynamicButtonCallback.DOWN);
	    if (state == State.DOWN)
		state = DynamicButton.State.UP;
	    else if (state == State.UP)
		state = DynamicButton.State.DOWN;
	}
    }

    @Override
    public void inputUp(float x, float y) {
	if (enabled && visible && getBounds().contains(x, y))
	    if (callback != null)
		callback.onButtonEvent(this, DynamicButtonCallback.UP);
    }

}
