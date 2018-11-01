package edu.up.cs.androidcatan.animation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * A SurfaceView which allows which an animation to be drawn on it by a
 * Animator.
 *
 * @author Steve Vegdahl
 * @author Andrew Nuxoll
 * @version July 2013
 *
 *
 */
public class AnimationSurface extends SurfaceView implements OnTouchListener {

    // instance variables
    private Animator animator; // our animator
    private AnimationThread animationThread = null; // thread to generate ticks
    private Paint backgroundPaint = new Paint(); // painter for painting background
    private int flashCount; // counts down ticks for background-flash
    private Paint flashPaint; // has color for background flash

    /**
     * Constructor for the AnimationSurface class. In order to be useful, an
     * object must be supplied that implements the Animator interface. This
     * can either be done by overriding the 'createAnimator' method (which by
     * default give null, or by invoking the setAnimator method.
     *
     * @param context
     *            - a reference to the activity this animation is run under
     */
    public AnimationSurface(Context context) {
        super(context);
        init();
    }// ctor

    /**
     * An alternate constructor for use when a subclass is directly specified
     * in the layout. It is expected that the subclass will have overridden
     * the 'createAnimator' method.
     *
     * @param context
     *            - a reference to the activity this animation is run under
     * @param attrs
     *            - set of attributes passed from system
     */
    public AnimationSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }// ctor

    /**
     * Helper-method for the constructors
     */
    private void init() {

        // Tell the OS that *yes* I will draw stuff
        setWillNotDraw(false);

        // initialize the animator instance variable animator-creation method
        animator = createAnimator();

        //Begin listening for touch events
        this.setOnTouchListener(this);

        if (animator != null) {
            startAnimation();
        }
    }// init

    /**
     * Starts the animation
     */
    private void startAnimation() {

        // create and start a thread to generate "ticks" for the animator
        // with the frequency that it desires
        this.animationThread = new AnimationThread(getHolder());
        animationThread.start();

        // Initialize the background color paint as instructed by the animator
        backgroundPaint.setColor(animator.backgroundColor());
    }

    /**
     * Creates the animator for the object. If this method returns null, then it will
     * be necessary to invoke the 'setAnimator' method before the animation can start.
     * @return the animator
     */
    public Animator createAnimator() {
        return null;
    }

    /**
     * Sets and starts the animator for the AnimationSurface if it does not already
     * have an animator.
     *
     * @param animator the animator to use.
     */
    public void setAnimator(Animator animator) {
        if (this.animator == null) {
            // set the animator
            this.animator = animator;
        }
        if (this.animator != null) {
            // start the animator
            startAnimation();
        }
    }

    /**
     * Causes the background color to flash (change color) for the specified amount of time.
     * @param color
     * 			the color to flash
     * @param millis
     * 			the number of milliseconds to flash
     */
    public void flash(int color, int millis) {
        animationThread.flash(color, millis);
    }

    /**
     * Thread subclass to control the game loop
     *
     * Code adapted from Android:How to Program by Deitel, et.al., first edition
     * copyright (C)2013.
     *
     */
    private class AnimationThread extends Thread {

        // a reference to a SurfaveView's holder. This is used to "lock" the
        // canvas when we want to write to it
        private SurfaceHolder surfaceHolder;

        // controls animation stop/go based upon instructions from the Animator
        private boolean threadIsRunning = true;

        /** ctor inits instance variables */
        public AnimationThread(SurfaceHolder holder) {
            surfaceHolder = holder;
            setName("AnimationThread");
        }

        /**
         * causes this thread to pause for a given interval.
         *
         * @param interval
         *            duration in milliseconds
         */
        private void sleep(int interval) {
            try {
                Thread.sleep(interval); // use sleep to avoid busy wait
            } catch (InterruptedException ie) {
                // don't care if we're interrupted
            }
        }// sleep

        /**
         * Causes the background to be changed ("flash") for the given period
         * of time.
         *
         * @param color
         * 			the color to flash
         * @param millis
         * 			the number of milliseconds for this the flash should occur
         */
        public void flash(int color, int millis) {
            flashCount = millis; // set the flash count
            flashPaint = new Paint(); // create painter ...
            flashPaint.setColor(color); // ... with the appropriate color
        }

        /**
         * This is the main animation loop. It calls the Animator's draw()
         * method at regular intervals to creation the animation.
         */
        @Override
        public void run() {

            Canvas canvas = null;// ref to canvas animator draws upon
            long lastTickEnded = 0; // when the last tick ended

            while (threadIsRunning) {

                // stop if the animator asks for it
                if (animator.doQuit())
                    break;

                // pause while the animator wishes it
                while (animator.doPause()) {
                    sleep(animator.interval());
                }// while

                // Pause to honor the animator's tick frequency specification
                long currTime = System.currentTimeMillis();
                long remainingWait = animator.interval()
                        - (currTime - lastTickEnded);
                if (remainingWait > 0) {
                    sleep((int) remainingWait);
                }

                // Ok! We can draw now.
                try {
                    // lock the surface for drawing
                    canvas = surfaceHolder.lockCanvas(null);

                    //paint the background
                    if (canvas != null) {
                        // draw the background
                        if (flashCount > 0) {
                            // we are flashing: draw the "flash" color
                            canvas.drawRect(0,0,getWidth(),getHeight(), flashPaint);

                            // decrement the flash count by the number of milliseconds in
                            // our interval
                            flashCount -= animator.interval();

                            // if we've finished, "release" the flash-painting object
                            if (flashCount <= 0) {
                                flashPaint = null;
                            }
                        }
                        else {
                            // not flashing: draw the normal background color
                            canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);
                        }

                        // tell the animator to draw the next frame
                        synchronized (surfaceHolder) {
                            animator.tick(canvas);
                        }// synchronized
                    }
                }// try
                finally {
                    // release the canvas
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                // Note when this tick ended
                lastTickEnded = System.currentTimeMillis();

            }// while
        }// run
    }

    /**
     * if I am touched, pass the touch event to the animator
     */
    public boolean onTouch(View v, MotionEvent event) {
        if (animator != null) {
            this.animator.onTouch(event);
        }
        return true;
    };// class AnimationThread

}// class AnimationSurface
