package edu.up.cs.androidcatan.catan;


import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.game.GameHumanPlayer;
import edu.up.cs.androidcatan.game.GameMainActivity;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanHumanPlayer extends GameHumanPlayer implements OnClickListener {

    /* instance variables */
    private final String TAG = "CatanHumanPlayer";

    // These variables will reference widgets that will be modified during play
    
    private Button roll = null;


    // the android activity that we are running
    private GameMainActivity myActivity;

    /**
     * constructor does nothing extra
     */
    public CatanHumanPlayer(String name) {
        super(name);
    }

    /**
     * Returns the GUI's top view object
     *
     * @return the top object in the GUI's view heirarchy
     */
    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * callback method when we get a message (e.g., from the game)
     *
     * @param info the message
     */
    @Override
    public void receiveInfo(GameInfo info) {
        //TODO You will implement this method to receive state objects from the game

    }//receiveInfo

    /**
     * this method gets called when the user clicks the die or hold button. It
     * creates a new CatanRollAction or CatanHoldAction and sends it to the game.
     * creates a new CatanRollAction or CatanHoldAction and sends it to the game.
     *
     * @param button the button that was clicked
     */
    public void onClick(View button) {
        //TODO  You will implement this method to send appropriate action objects to the game
        Log.d(TAG, "onClick: ");
        if(button.getId() == R.id.sidebar_button_roll) {
            CatanRollDiceAction a = new CatanRollDiceAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(a);

        }

    }// onClick

    /**
     * callback method--our game has been chosen/rechosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    public void setAsGui(GameMainActivity activity) {
        // TODO this is where we draw things...
        // remember the activity
        myActivity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.activity_main);

        this.roll = (Button) activity.findViewById(R.id.sidebar_button_roll);
        Log.d(TAG, "setAsGui: ");

        roll.setOnClickListener(this);
        //Initialize the widget reference member variables
//        this.playerScoreTextView = (TextView)activity.findViewById(R.id.yourScoreValue);
//        this.oppScoreTextView    = (TextView)activity.findViewById(R.id.oppScoreValue);
//        this.turnTotalTextView   = (TextView)activity.findViewById(R.id.turnTotalValue);
//        this.messageTextView     = (TextView)activity.findViewById(R.id.messageTextView);
//        this.dieImageButton      = (ImageButton)activity.findViewById(R.id.dieButton);
//        this.holdButton          = (Button)activity.findViewById(R.id.holdButton);

        //Listen for button presses
//        dieImageButton.setOnClickListener(this);
//        holdButton.setOnClickListener(this);

    }//setAsGui

}// class CatanHumanPlayer

