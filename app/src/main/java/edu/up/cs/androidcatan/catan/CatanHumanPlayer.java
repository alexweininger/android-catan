package edu.up.cs.androidcatan.catan;


import android.graphics.Canvas;
import android.graphics.Color;
import android.media.Image;
import android.support.constraint.Group;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuyDevCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseDevCardAction;
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
    private int[] resourceCards = {2, 1, 2, 0, 1}; // array for number of each resource card a player has

    // array for relating resource card names to resource card ids in the resourceCards array above
    private static final String[] resourceCardIds = {"Brick", "Grain", "Lumber", "Ore", "Wool"};

    // ArrayList of the development cards the player owns
    private ArrayList<DevelopmentCard> developmentCards = new ArrayList<>();

    // number of buildings the player has to build {roads, settlements, cities}
    private int[] buildingInventory = {15, 5, 4};

    //current chosen building to for confirm button; 0 = Road, 1 = Settlement, 2 = City, 3 = Dev Card
    private int buildingId = 0;

    // determined by how many knight dev cards the player has played, used for determining who currently has the largest army trophy
    private int armySize = 0;

    // playerId
    private int playerId;

    // These variables will reference widgets that will be modified during play
    private Button buildCityButton = null;
    private Button buildRoadButton = null;
    private Button buildSettlementButton = null;
    private Button buyDevCardButton = null;
    private Button endTurnButton = null;
    private Button robberDiscard = null;
    private Button robberMove = null;
    private Button robberSteal = null;
    private Button rollButton = null;
    private Button tradeButton = null;
    private Button tradeCustomPort = null;
    private Button tradePort = null;
    private Button useDevCard = null;
    private Button buyDevCard = null;
    private Button okButton = null;
    private Button cancelButton = null;

    //Buttons that only affect the GUI
    private Button showScore = null;

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

    // scoreboard player name TextViews
    private TextView player0Name = (TextView) null;
    private TextView player1Name = (TextView) null;
    private TextView player2Name = (TextView) null;
    private TextView player3Name = (TextView) null;
    //Groups
    private Group scoreMenu = null;
    private Group devMenu = null;
    private Group tradeMenu = null;
    private Group buildMenu = null;

    // misc sidebar TextViews
    private TextView myScore = (TextView) null;

    // game state
    private CatanGameState state = null;

    private GameMainActivity myActivity;  // the android activity that we are running

    //Trading instance variable
    private int[] tradeResWant = new int[]{0, 0, 0, 0, 0};
    private int[] tradeResGet = new int[]{0, 0, 0, 0, 0};

    CatanGameState state = null; // game state

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

            this.state = (CatanGameState) info;

            updateTextViews();


        } else if (info instanceof NotYourTurnInfo) {
            Log.i(TAG, "receiveInfo: Player tried to make action but it is not thier turn.");
        } else if (info instanceof IllegalMoveInfo) {
            Log.i(TAG, "receiveInfo: Illegal move info received.");
        } else {
            Log.e(TAG, "receiveInfo: Received instanceof not anything we know. Returning void.");
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
        if (state == null) {
            Log.e(TAG, "onClick: state is null.");
        }
        if (state.isSetupPhase()) {
            Log.i(TAG, "onClick: setup phase ");
            // if it is the setup phase, player can only make these actions

            if (button.getId() == R.id.sidebar_button_road) {
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(0, 1); // give 1 brick
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(2, 1); // give 1 lumber
                CatanBuildRoadAction action = new CatanBuildRoadAction(this, 0, 1, this.playerId);
                Log.d(TAG, "onClick: Road");
                game.sendAction(action);
                return;
            } else if (button.getId() == R.id.sidebar_button_settlement) {
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(0, 1); // give 1 brick
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(1, 1); // give 1 grain
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(2, 1); // give 1 lumber
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(4, 1); // give 1 wool

                Log.i(TAG, "onClick: clicked build settlement button"); // here
                myActivity.findViewById(R.id.group_singleIntersectionInput).setVisibility(View.VISIBLE); // todo

                final CatanGameState copyState = new CatanGameState(state);
                Button confirmIntersectionButton = (Button) myActivity.findViewById(R.id.confirm);
                confirmIntersectionButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText intersectionText = myActivity.findViewById(R.id.start_road_id_entered);
                        int intersectionIdInput = Integer.parseInt(intersectionText.getText().toString());
                        Log.i(TAG, "onClick: inputted intersectionId: " + intersectionIdInput);

                        if (copyState.getBoard().validBuildingLocation(copyState.getCurrentPlayerId(), true, intersectionIdInput)) {
                            Log.i(TAG, "onClick: building location is valid. Sending a BuildSettlementAction to the game.");
                            game.sendAction(new CatanBuildSettlementAction(copyState.getPlayerList().get(copyState.getCurrentPlayerId()), copyState.getCurrentPlayerId(), intersectionIdInput));
                            myActivity.findViewById(R.id.start_road_id_entered).setBackgroundColor(Color.WHITE);
                            myActivity.findViewById(R.id.group_singleIntersectionInput).setVisibility(View.GONE);
                            return;
                        } else {
                            Log.i(TAG, "onClick: invalid intersection input");
                            myActivity.findViewById(R.id.start_road_id_entered).setBackgroundColor(Color.RED);
                        }
                    }
                });
            } else {
                Log.i(TAG, "onClick: It is the setup phase and received a unchecked for button click.");
            }
        } else {
            /* ----- if it is not the setup phase ----- */
            if (button.getId() == R.id.sidebar_button_city) {
                CatanBuildCityAction action = new CatanBuildCityAction(this, this.playerId, 0);
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
                Log.i(TAG, "onClick: clicked build settlement button"); // here
                myActivity.findViewById(R.id.group_singleIntersectionInput).setVisibility(View.VISIBLE); // todo

                final CatanGameState copyState = new CatanGameState(state);
                Button confirmIntersectionButton = (Button) myActivity.findViewById(R.id.confirm);
                confirmIntersectionButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText intersectionText = myActivity.findViewById(R.id.start_road_id_entered);
                        int intersectionIdInput = Integer.parseInt(intersectionText.getText().toString());
                        Log.i(TAG, "onClick: inputted intersectionId: " + intersectionIdInput);

                        if (copyState.getBoard().validBuildingLocation(copyState.getCurrentPlayerId(), true, intersectionIdInput)) {
                            Log.i(TAG, "onClick: building location is valid. Sending a BuildSettlementAction to the game.");
                            game.sendAction(new CatanBuildSettlementAction(copyState.getPlayerList().get(copyState.getCurrentPlayerId()), copyState.getCurrentPlayerId(), intersectionIdInput));
                            return;
                        }
                    }
                });

                CatanBuildSettlementAction action = new CatanBuildSettlementAction(this, this.playerId, 1);
                Log.d(TAG, "onClick: Settlement");
                game.sendAction(action);
                return;
            }
            if (button.getId() == R.id.sidebar_button_devcards) {
                CatanBuyDevCardAction action = new CatanBuyDevCardAction(this);
                Log.d(TAG, "onClick: Buy Dev Card");
                game.sendAction(action);
                return;
            }
            if (button.getId() == R.id.sidebar_button_endturn) {
                CatanEndTurnAction action = new CatanEndTurnAction(this);
                Log.d(TAG, "onClick: End Turn");

                game.sendAction(action);
                return;
            }
        }
            //TODO Placeholder for Robber actions until they're implemented
//        if(button.getId() == R.id.) {
//            CatanRollDiceAction a = new CatanRollDiceAction(this);
//            Log.d(TAG, "onClick: Roll");
//            game.sendAction(a);
//            return;
//        }
            if (button.getId() == R.id.sidebar_button_roll) {
                CatanRollDiceAction a = new CatanRollDiceAction(this);
                Log.d(TAG, "onClick: Roll");
                game.sendAction(a);
                return;
            }

        //GUI Buttons (Opens another menu)

        if(button.getId() == R.id.sidebar_button_score){
            Log.d(TAG, "onClick() called with: button = [" + button + "]");
            if(scoreMenu.getVisibility() == View.VISIBLE){
                Log.d(TAG, "onClick: Now to gone");
                scoreMenu.setVisibility(View.GONE);
            }
            else if(scoreMenu.getVisibility() == View.GONE){
                scoreMenu.setVisibility(View.VISIBLE);
                devMenu.setVisibility(View.GONE);
                tradeMenu.setVisibility(View.GONE);
                Log.d(TAG, "onClick: Now to visible" + scoreMenu.getVisibility() + "");
            }
            else{
                Log.d(TAG, "onClick: NULL");
            }
            return;
        }

        //Dev Card buttons
        if(button.getId() == R.id.sidebar_button_devcards){
            Log.d(TAG, "onClick() called with: button = [" + button + "]");
            if(devMenu.getVisibility() == View.VISIBLE){
                Log.d(TAG, "onClick: Now to gone");
                devMenu.setVisibility(View.GONE);
            }
            else if(devMenu.getVisibility() == View.GONE){
                devMenu.setVisibility(View.VISIBLE);
                scoreMenu.setVisibility(View.GONE);
                tradeMenu.setVisibility(View.GONE);
                Log.d(TAG, "onClick: Now to visible" + devMenu.getVisibility() + "");
            }
            else{
                Log.d(TAG, "onClick: NULL");
            }
            return;
        }

        if(button.getId() == R.id.use_Card){
            CatanUseDevCardAction action = new CatanUseDevCardAction(this);
            Log.d(TAG, "onClick: Use Dev Card");
            game.sendAction(action);
        }

        if(button.getId() == R.id.devBuildbutton){
            Log.d(TAG, "onClick: Dev Card");
            buildMenu.setVisibility(View.VISIBLE);
            buildingId = 3;
            return;
        }

        //ok and cancel
        if(button.getId() == R.id.okButton) {
            switch (buildingId) {
                case 0:
                    CatanBuildRoadAction action0 = new CatanBuildRoadAction(this, this.playerId , 1, 1);
                    Log.d(TAG, "(ok)onClick: City");
                    game.sendAction(action0);
                    break;
                case 1:
                    CatanBuildSettlementAction action1 = new CatanBuildSettlementAction(this, this.playerId , 1);
                    Log.d(TAG, "(ok)onClick: City");
                    game.sendAction(action1);
                    break;
                case 2:
                    CatanBuildCityAction action2 = new CatanBuildCityAction(this, this.playerId , 1);
                    Log.d(TAG, "(ok)onClick: City");
                    game.sendAction(action2);
                    break;
                case 3:
                    CatanBuyDevCardAction action3 = new CatanBuyDevCardAction(this);
                    Log.d(TAG, "(ok)onClick: Dev Card");
                    game.sendAction(action3);
                    break;
                default:
                    Log.d(TAG, "(ok)onClick: Error, out of bounds");
            }
            buildMenu.setVisibility(View.GONE);
        }

        if(button.getId() == R.id.cancelButton){
            buildMenu.setVisibility(View.GONE);
        }

        //TODO Need functionality for both Port, Custom Port and Bank
        if(button.getId() == R.id.sidebar_button_trade){
            Log.d(TAG, "onClick() called with: button = [" + button + "]");
            if(tradeMenu.getVisibility() == View.VISIBLE){
                Log.d(TAG, "onClick: Now to gone");
                tradeMenu.setVisibility(View.GONE);
            }
            else if(tradeMenu.getVisibility() == View.GONE){
                tradeMenu.setVisibility(View.VISIBLE);
                scoreMenu.setVisibility(View.GONE);
                devMenu.setVisibility(View.GONE);
                Log.d(TAG, "onClick: Now to visible" + tradeMenu.getVisibility() + "");
            }
            else{
                Log.d(TAG, "onClick: NULL");
            }
            return;
        }

    }// onClick

    /**
     * callback method--our game has been chosen/rechosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    public void setAsGui(GameMainActivity activity){
        Log.d(TAG, "setAsGui() called with: activity = [" + activity + "]");
        // remember the activity
        myActivity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.activity_main);

        buildCityButton = (Button) activity.findViewById(R.id.sidebar_button_city);
        buildRoadButton = (Button) activity.findViewById(R.id.sidebar_button_road);
        buildSettlementButton = (Button) activity.findViewById(R.id.sidebar_button_settlement);
        buyDevCardButton = (Button) activity.findViewById(R.id.sidebar_button_devcards);
        endTurnButton = (Button) activity.findViewById(R.id.sidebar_button_endturn);
//        robberDiscard = (Button)activity.findViewById(R.id.)
//        robberMove = (Button)activity.findViewById(R.id.)
//        robberSteal = (Button)activity.findViewById(R.id.)
        rollButton = (Button) activity.findViewById(R.id.sidebar_button_roll);
        tradeButton = (Button) activity.findViewById(R.id.sidebar_button_trade);
        tradeCustomPort = (Button) activity.findViewById(R.id.sidebar_button_trade);
        tradePort = (Button) activity.findViewById(R.id.sidebar_button_trade);
        useDevCard = (Button) activity.findViewById(R.id.use_Card);
        buyDevCard = (Button) activity.findViewById(R.id.devBuildbutton);
        okButton = (Button) activity.findViewById(R.id.okButton);
        cancelButton = (Button) activity.findViewById(R.id.cancelButton);

        //GUI Buttons
        showScore = (Button) activity.findViewById(R.id.sidebar_button_score);

        //GUI: Trading Menu Buttons
//        ImageButton brickGive = (ImageButton) activity.findViewById(R.id.brick_give_icon);
//        ImageButton brickGet = (ImageButton) activity.findViewById(R.id.brick_want_icon);
//        ImageButton grainGive = (ImageButton) activity.findViewById(R.id.grain_give_icon);
//        ImageButton grainGet = (ImageButton) activity.findViewById(R.id.grain_want_icon);
//        ImageButton woodGive = (ImageButton) activity.findViewById(R.id.wood_give_icon);
//        ImageButton woodGet = (ImageButton) activity.findViewById(R.id.wood_want_icon);
//        ImageButton oreGive = (ImageButton) activity.findViewById(R.id.ore_give_icon);
//        ImageButton oreGet = (ImageButton) activity.findViewById(R.id.ore_want_icon);
//        ImageButton sheepGive = (ImageButton) activity.findViewById(R.id.sheep_give_icon);
//        ImageButton sheepGet = (ImageButton) activity.findViewById(R.id.sheep_want_icon);

        buildCityButton.setOnClickListener(this);
        buildRoadButton.setOnClickListener(this);
        buildSettlementButton.setOnClickListener(this);
        buyDevCardButton.setOnClickListener(this);
        endTurnButton.setOnClickListener(this);
//        robberDiscard.setOnClickListener(this);
//        robberMove.setOnClickListener(this);
//        robberSteal.setOnClickListener(this);
        rollButton.setOnClickListener(this);
        tradeButton.setOnClickListener(this);
        tradeCustomPort.setOnClickListener(this);
        tradePort.setOnClickListener(this);
        useDevCard.setOnClickListener(this);

        //Ok and cancel listeners
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        //GUI change listeners
        showScore.setOnClickListener(this);

        //Group GUI Items
        scoreMenu = activity.findViewById(R.id.group_scoreboard);
        devMenu = activity.findViewById(R.id.group_development_card_menu);
        tradeMenu = activity.findViewById(R.id.group_trade_menu);
        buildMenu = activity.findViewById(R.id.group_build_menu);

        // resource value text
        this.oreValue = (TextView) activity.findViewById(R.id.sidebar_value_ore);
        this.grainValue = (TextView) activity.findViewById(R.id.sidebar_value_grain);
        this.lumberValue = (TextView) activity.findViewById(R.id.sidebar_value_lumber);
        this.woolValue = (TextView) activity.findViewById(R.id.sidebar_value_wool);
        this.brickValue = (TextView) activity.findViewById(R.id.sidebar_value_brick);

        // scoreboard TextViews

        this.player0Score = activity.findViewById(R.id.Player1_Score);
        this.player1Score = activity.findViewById(R.id.Player2_Score);
        this.player2Score = activity.findViewById(R.id.Player3_Score);
        this.player3Score = activity.findViewById(R.id.Player4_Score);


        // Listeners for ok and cancel button
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        //Board stuff
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
        Log.e(TAG, "initAfterReady() called");
    }

    /*
     *
     */
    private void updateTextViews() {

        if (this.state.isSetupPhase()) {
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.rollButton.setAlpha(0.5f);
            this.endTurnButton.setAlpha(0.5f);
            this.buyDevCardButton.setAlpha(0.5f);
            this.tradeButton.setAlpha(0.5f);

        } else {
            this.buildCityButton.setAlpha(1f);
            this.rollButton.setAlpha(1f);
            this.endTurnButton.setAlpha(1f);
            this.buyDevCardButton.setAlpha(1f);
            this.tradeButton.setAlpha(1f);
        }

        /* ----- update resource value TextViews ----- */
        int[] resourceCards = this.state.getPlayerList().get(this.playerId).getResourceCards();
        this.brickValue.setText(String.valueOf(resourceCards[0]));
        this.grainValue.setText(String.valueOf(resourceCards[1]));
        this.lumberValue.setText(String.valueOf(resourceCards[2]));
        this.oreValue.setText(String.valueOf(resourceCards[3]));
        this.woolValue.setText(String.valueOf(resourceCards[4]));

        /* ----- update scoreboard ----- */
        this.player0Score.setText(String.valueOf(state.getPlayerVictoryPoints()[0]));
        this.player1Score.setText(String.valueOf(state.getPlayerVictoryPoints()[1]));
        this.player2Score.setText(String.valueOf(state.getPlayerVictoryPoints()[2]));
        this.player3Score.setText(String.valueOf(state.getPlayerVictoryPoints()[3]));

        /* ----- update human player score ----- */

    }

    /**
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

