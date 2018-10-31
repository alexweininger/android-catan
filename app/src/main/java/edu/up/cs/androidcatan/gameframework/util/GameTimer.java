package edu.up.cs.androidcatan.gameframework.util;

/**
 * A GameTimer is an object that sends a GameTimerAction to a Game or a
 * Player at regular intervals.  It can be used, for example, in games
 * where players have deadlines for performing certain actions (e.g., chess
 * clock, 24-second clock in a basketball simultation).<P>
 *
 * Each LocalGame, HumanPlayer and ComputerPlayer has a GameTimer created
 * for it when the object is created. If one of these entities needs to have
 * a "tick" at regular intervals, the will typically execute code such as:
 * <PRE>
 *   // set timer to go off every 100 milliseconds
 *   getTimer().setInterval(100);
 *   getTimer().start();
 * </PRE>
 * then its 'timerTicked()' method will be invoked every 100 milliseconds;
 * it can then perform whatever time-related operations it wishes to perform.
 * <P>
 * If additional timers are required, they must be explicitly created, and
 * then handled as a TimerAction (in the checkAndHandleAction method) or
 * TimerInfo (in the receiveInfo method).
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 */
public class GameTimer {
    // the game to send actions to
    private Tickable target;

    // the number of ticks since timer was created (or last reset)
    private int ticks;

    // the interval, in milliseconds, between timer events
    private int interval;

    // the thread that is running, which causes the "ticks" to occur.  If
    // this variable is null, it means that the timer is stopped
    private Thread thread;

    /**
     * Constructor for objects of class GameTimer
     *
     * @param target the object to "tick" when the timer goes off
     *  GameTimerAction sends
     */
    public GameTimer(Tickable target)
    {
        this.target = target; // initialize game
        ticks = 0; // start #ticks at zero
        thread = null; // indicates thread not running
    }

    public void setInterval(int interval) {
        this.interval = Math.max(0, interval);
    }

    /**
     * Starts the timer.  Has no effect if the timer is already running.
     *
     */
    public void start() {
        // synchronize to ensure null test and thread-start are "atomic"
        synchronized(this) {
            if (thread == null) {
                // if thread is not null create new timer object/thread
                // and start it
                MyTimer timer = new MyTimer();
                thread = new Thread(timer);
                thread.start();
            }
        }
    }

    /**
     * Stops the timer.
     *
     */
    public void stop() {
        // set the 'thread' instance-variable to null.  The next time the
        // thread tests this variable, it will terminate.
        thread = null;
    }

    /**
     * The number of GameTimerActions sent to the game since the timer
     * was started, or since the last reset was performed on this GameTimer.
     *
     * @return if a reset had been performed, the number of ticks since the
     *  most recent reset; otherwise since the object was created.
     */
    public int getTicks() {
        // return # ticks
        return ticks;
    }

    /**
     * Resets the timer's tick-pig to zero.
     */
    public void reset() {
        // reset tick-pig to zero
        ticks = 0;
    }

    /**
     * MyTimer: A private inner-class that runs the timer-thread.
     */
    private class MyTimer implements Runnable {

        /**
         * The code that runs in the separate thread.
         */
        public void run() {

            // as long as we've not been marked as "stopped", apply the
            // timer action to the games at the appropriate time-intervals
            //
            while (thread != null)
                try {
                    Thread.sleep(interval); // wait for appropriate interval
                    ticks++; // increment # ticks
                    target.tick(GameTimer.this); // apply action to target
                }
                catch (InterruptedException ix) {
                    // this should never happen, but if it does, loop back
                }
        }
    }

}

