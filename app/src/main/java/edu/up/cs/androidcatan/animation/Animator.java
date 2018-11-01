package edu.up.cs.androidcatan.animation;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * An interface that defines an object that is capable of drawing an animation.
 * It is intended to be used with the AnimationSurface class.
 *
 * @author Steve Vegdahl
 * @author Andrew Nuxoll
 * @version September 2012
 */

public interface Animator {

    /**
     * The time interval (in milliseconds) between animation frames. Thus, for
     * example, to draw a frame 20 times per second, you would return 50. This
     * method is called once at the beginning of the animation, so changing the
     * value during the animation will have no effect.
     *
     * @return the time interval (in milliseconds) between calls to this class'
     *         "tick" method.
     */
    public int interval();

    /**
     * The background color with which to paint the canvas before the animation
     * frame is drawn. This method is called at each tick, so the background
     * color can change dynamically by having this method return different
     * values.
     *
     * @return the desired background color
     */
    public int backgroundColor();

    /**
     * Tells whether the animation should be paused.
     *
     * @return a true/false value that says whether the animation should be
     *         paused.
     */
    public boolean doPause();

    /**
     * Tells whether the animation should be stopped.
     *
     * @return true/false value that tells whether to terminate the animation.
     */
    public boolean doQuit();

    /**
     * Called once every clock tick (frequency specified by the "interval"
     * method) to draw the next animation-frame. Typically this is used to
     * update the animation's data to reflect the passage of time (e.g., to
     * modify an instance variable that gives the position of an object) before
     * the frame is drawn.
     *
     * @param canvas
     *            the Canvas object on which to draw the animation-frame.
     */
    public void tick(Canvas canvas);

    /**
     * Called whenever the user touches the AnimationSurface so that the
     * animation can respond to the event.
     *
     * @param event a MotionEvent describing the touch
     */
    public void onTouch(MotionEvent event);
}
