package edu.up.cs.androidcatan.catan;


import android.graphics.Canvas;
import android.support.constraint.Group;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.catan.graphics.boardSurfaceView;
import edu.up.cs.androidcatan.game.GameHumanPlayer;
import edu.up.cs.androidcatan.game.GameMainActivity;
import edu.up.cs.androidcatan.game.infoMsg.GameInfo;
import edu.up.cs.androidcatan.game.infoMsg.IllegalMoveInfo;
import edu.up.cs.androidcatan.game.infoMsg.NotYourTurnInfo;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version October 31th, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class CatanHumanPlayer extends GameHumanPlayer implements OnClickListener {

    private final String TAG = "CatanHumanPlayer";

    /* instance variables */

    // resourceCard index values: 0 = Brick, 1 = Grain, 2 = Lumber, 3 = Ore, 4 = Wool
    private int[] resourceCards = {4, 2, 4, 0, 2}; // array for number of each resource card a player has

    // array for relating resource card names to resource card ids in the resourceCards array above
    private static final String[] resourceCardIds = {"Brick", "Grain", "Lumber", "Ore", "Wool"};

    // ArrayList of the development cards the player owns
    private ArrayList<DevelopmentCard> developmentCards = new ArrayList<>();

    // number of buildings the player has to build {roads, settlements, cities}
    private int[] buildingInventory = {15, 5, 4};

    // determined by how many knight dev cards the player has played, used for determining who currently has the largest army trophy
    private int armySize = 0;

    // playerId
    private int playerId;

    // These variables will reference widgets that will be modified during play
    private Button buildCity = null;
    private Button buildRoad = null;
    private Button buildSettlement = null;
    private Button buyDevCard = null;
    private Button endTurn = null;
    private Button robberDiscard = null;
    private Button robberMove = null;
    private Button robberSteal = null;
    private Button roll = null;
    private Button tradeBank = null;
    private Button tradeCustomPort = null;
    private Button tradePort = null;
    private Button useDevCard = null;

    // resource count text views
    private TextView oreValue = (TextView) null;
    private TextView grainValue = (TextView) null;
    private TextView lumberValue = (TextView) null;
    private TextView woolValue = (TextView) null;
    private TextView brickValue = (TextView) null;

    // scoreboard text views
    private TextView player0Score = (TextView) null;
    private TextView player1Score = (TextView) null;
    private TextView player2Score = (TextView) null;
    private TextView player3Score = (TextView) null;


    // the android activity that we are running
    private GameMainActivity myActivity;

    // game state
    CatanGameState state = null;

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
        Log.d(TAG, "receiveInfo() called with: info = [" + info.toString() + "]");
        //TODO You will implement this method to receive state objects from the game
        if (info instanceof CatanGameState) {
            // set resource count TextViews to the players resource inventory amounts
            Log.i(TAG, "receiveInfo: player list: " + ((CatanGameState) info).getPlayerList());


        } else if (info instanceof NotYourTurnInfo) {
            Log.i(TAG, "receiveInfo: Player tried to make action but it is not thier turn.");
        } else if (info instanceof IllegalMoveInfo) {
            Log.i(TAG, "receiveInfo: Illegal move info received.");
        } else if (!(info instanceof CatanGameState)) {
            Log.e(TAG, "receiveInfo: Received instanceof not anything we know. Returning void.");
            return;
        } else {
            state = (CatanGameState) info;
            updateTextViews();
        }
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
        Log.d(TAG, "onClick() called with: button = [" + button + "]");
        if (button.getId() == R.id.sidebar_button_city) {
            CatanBuildCityAction action = new CatanBuildCityAction(this);
            Log.d(TAG, "onClick: City");
            game.sendAction(action);
            return;
        }
        if (button.getId() == R.id.sidebar_button_road) {
            CatanBuildRoadAction action = new CatanBuildRoadAction(this, 0, 1, this.playerId);
            Log.d(TAG, "onClick: Road");
            game.sendAction(action);
            return;
        }
        if (button.getId() == R.id.sidebar_button_settlement) {
            CatanBuildSettlementAction action = new CatanBuildSettlementAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(action);
            return;
        }
        if (button.getId() == R.id.sidebar_button_roll) {
            CatanRollDiceAction action = new CatanRollDiceAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(action);
            return;
        }
        if (button.getId() == R.id.sidebar_button_endturn) {
            CatanEndTurnAction action = new CatanEndTurnAction(this);
            Log.d(TAG, "onClick: End Turn");

            game.sendAction(action);
            return;
        }
        if (button.getId() == R.id.sidebar_button_roll) {
            CatanRollDiceAction a = new CatanRollDiceAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(a);
            return;
        }
        if (button.getId() == R.id.sidebar_button_roll) {
            CatanRollDiceAction a = new CatanRollDiceAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(a);
            return;
        }
        if (button.getId() == R.id.sidebar_button_roll) {
            CatanRollDiceAction a = new CatanRollDiceAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(a);
            return;
        }
        if (button.getId() == R.id.sidebar_button_roll) {
            CatanRollDiceAction a = new CatanRollDiceAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(a);
            return;
        }
        if (button.getId() == R.id.sidebar_button_roll) {
            CatanRollDiceAction a = new CatanRollDiceAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(a);
            return;
        }

    }// onClick

    /**
     * callback method--our game has been chosen/rechosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    public void setAsGui(GameMainActivity activity) {
        Log.d(TAG, "setAsGui() called with: activity = [" + activity + "]");
        // remember the activity
        myActivity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.activity_main);

        buildCity = (Button) activity.findViewById(R.id.sidebar_button_city);
        buildRoad = (Button) activity.findViewById(R.id.sidebar_button_road);
        buildSettlement = (Button) activity.findViewById(R.id.sidebar_button_settlement);
        buyDevCard = (Button) activity.findViewById(R.id.sidebar_button_devcards);
        endTurn = (Button) activity.findViewById(R.id.sidebar_button_endturn);
//        robberDiscard = (Button)activity.findViewById(R.id.)
//        robberMove = (Button)activity.findViewById(R.id.)
//        robberSteal = (Button)activity.findViewById(R.id.)
        roll = (Button) activity.findViewById(R.id.sidebar_button_roll);
        tradeBank = (Button) activity.findViewById(R.id.sidebar_button_trade);
        tradeCustomPort = (Button) activity.findViewById(R.id.sidebar_button_trade);
        tradePort = (Button) activity.findViewById(R.id.sidebar_button_trade);
        useDevCard = (Button) activity.findViewById(R.id.use_Card);


        buildCity.setOnClickListener(this);
        buildRoad.setOnClickListener(this);
        buildSettlement.setOnClickListener(this);
        buyDevCard.setOnClickListener(this);
        endTurn.setOnClickListener(this);
//        robberDiscard.setOnClickListener(this);
//        robberMove.setOnClickListener(this);
//        robberSteal.setOnClickListener(this);
        roll.setOnClickListener(this);
        tradeBank.setOnClickListener(this);
        tradeCustomPort.setOnClickListener(this);
        tradePort.setOnClickListener(this);
        useDevCard.setOnClickListener(this);

        // resource value text
        this.oreValue = (TextView) activity.findViewById(R.id.oreAmount);
        this.grainValue = (TextView) activity.findViewById(R.id.grainAmount);
        this.lumberValue = (TextView) activity.findViewById(R.id.lumberAmount);
        this.woolValue = (TextView) activity.findViewById(R.id.woolAmount);
        this.brickValue = (TextView) activity.findViewById(R.id.brickAmount);

        boardSurfaceView board = activity.findViewById(R.id.board); // boardSurfaceView board is the custom SurfaceView

        Canvas canvas = new Canvas(); // create Canvas object

        board.createHexagons();        // draw the board of hexagons and ports on the canvas

        board.draw(canvas); // draw

        // button listeners TODO move to separate class?
        Button scoreButton = activity.findViewById(R.id.sidebar_button_score);
        final Group scoreBoardGroup = activity.findViewById(R.id.group_scoreboard);
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scoreBoardGroup.getVisibility() == View.GONE) {
                    scoreBoardGroup.setVisibility(View.VISIBLE);
                } else {
                    scoreBoardGroup.setVisibility(View.GONE);
                }
            }
        });

        Button developmentButton = activity.findViewById(R.id.sidebar_button_devcards);
        final Group developmentGroup = activity.findViewById(R.id.group_development_card_menu);
        developmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (developmentGroup.getVisibility() == View.GONE) {
                    developmentGroup.setVisibility(View.VISIBLE);
                } else {
                    developmentGroup.setVisibility(View.GONE);
                }
            }
        });

        // build menu layout group
        final Group buildMenuGroup = activity.findViewById(R.id.group_build_menu);

        Button roadButton = activity.findViewById(R.id.sidebar_button_road);

        roadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buildMenuGroup.getVisibility() == View.GONE) {
                    buildMenuGroup.setVisibility(View.VISIBLE);
                } else {
                    buildMenuGroup.setVisibility(View.GONE);
                }
            }
        });


        // if we have state update the GUI based on the state

        if (this.state != null) {
            receiveInfo(state);
        }


    }//setAsGui

    /**
     *
     */
    protected void initAfterReady() {
        Log.d(TAG, "initAfterReady() called");
    }

    /*
     *
     */
    private void updateTextViews() {

        this.brickValue.setText(this.resourceCards[0]);
        this.grainValue.setText(this.resourceCards[1]);
        this.lumberValue.setText(this.resourceCards[2]);
        this.oreValue.setText(this.resourceCards[3]);
        this.woolValue.setText(this.resourceCards[4]);
    }

    /**
     *
     * @param message
     */
    protected void gameIsOver(String message) {
        for (int i = 0; i < this.state.getPlayerVictoryPoints().length; i++) {
            if (this.state.getPlayerVictoryPoints()[i] > 9) {
                super.gameIsOver("Player " + i + " wins!");
            }
        }
    }

}// class CatanHumanPlayer

