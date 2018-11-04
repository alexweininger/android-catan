package edu.up.cs.androidcatan.catan;


import android.graphics.Canvas;
import android.graphics.Color;
import android.support.constraint.Group;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuyDevCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.catan.graphics.BoardSurfaceView;
import edu.up.cs.androidcatan.catan.graphics.HexagonGrid;
import edu.up.cs.androidcatan.game.GameHumanPlayer;
import edu.up.cs.androidcatan.game.GameMainActivity;
import edu.up.cs.androidcatan.game.GamePlayer;
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

    // determined by how many knight dev cards the player has played, used for determining who currently has the largest army trophy
    private int armySize = 0;

    // playerId
    private int playerId;

    private String currentBuildingSelection = null;

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

    // misc sidebar TextViews
    private TextView myScore = (TextView) null;
    private TextView currentTurnIdTextView = (TextView) null;

    // intersection menu
    EditText intersectionEditText = (EditText) null;

    private GameMainActivity myActivity;  // the android activity that we are running

    public CatanGameState state = null; // game state

    private BoardSurfaceView boardSurfaceView;

    Canvas canvas = (Canvas) null;

    /**
     * constructor does nothing extra
     */
    public CatanHumanPlayer (String name) {
        super(name);
    }

    /**
     * Returns the GUI's top view object
     *
     * @return the top object in the GUI's view heirarchy
     */
    public View getTopView () {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * callback method when we get a message (e.g., from the game)
     *
     * @param info the message
     */
    @Override
    public void receiveInfo (GameInfo info) {
        if (info == null) {
            Log.e(TAG, "receiveInfo: info is null");
        }
        if (boardSurfaceView == null) {
            return;
        }
        Log.d(TAG, "receiveInfo() called with: info: \n" + info.toString() + "\n----------------------------");
        //TODO You will implement this method to receive state objects from the game
        if (info instanceof CatanGameState) {
            // set resource count TextViews to the players resource inventory amounts
            Log.i(TAG, "receiveInfo: player list: " + ((CatanGameState) info).getPlayerList());

            this.state = (CatanGameState) info;

            updateTextViews();
            drawGraphics();

            if (this.boardSurfaceView == null) {
                Log.e(TAG, "receiveInfo: boardSurfaceView is null.");
            }


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
    public void onClick (View button) {
        Log.d(TAG, "onClick() called with: button = [" + button + "]");
        Group intersectionMenu = myActivity.findViewById(R.id.group_singleIntersectionInput);
        if (button.getId() == R.id.sidebar_button_settlement) {
            intersectionMenu.setVisibility(View.VISIBLE);
            this.currentBuildingSelection = "Settlement";
        }

        if (button.getId() == R.id.sidebar_button_city) {
            intersectionMenu.setVisibility(View.VISIBLE);
            this.currentBuildingSelection = "City";
        }

        if (state == null) {
            Log.e(TAG, "onClick: state is null.");
        } // check if state is null

        // check if it is the setup phase of the game
        if (state.isSetupPhase()) {
            Log.i(TAG, "onClick: It is the setup phase.");

            // if it is the setup phase, player can only make these actions

            if (button.getId() == R.id.sidebar_button_road) { // setup phase build road button listener

                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(0, 1); // give 1 brick
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(2, 1); // give 1 lumber
                CatanBuildRoadAction action = new CatanBuildRoadAction(this, true, 0, 1, this.playerId);
                Log.d(TAG, "onClick: Road");
                game.sendAction(action);
                return;

            } else if (button.getId() == R.id.sidebar_button_settlement) { // setup phase build settlement button listener

                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(0, 1); // give 1 brick
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(1, 1); // give 1 grain
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(2, 1); // give 1 lumber
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(4, 1); // give 1 wool

                Log.i(TAG, "onClick: clicked build settlement button"); // here

                Button confirmIntersectionButton = myActivity.findViewById(R.id.confirm);
                final GamePlayer p = this;
                confirmIntersectionButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        if (!intersectionEditText.getText().toString().equals("")) {
                            int intersectionIdInput = Integer.parseInt(intersectionEditText.getText().toString());
                            Log.i(TAG, "onClick: inputted intersectionId: " + intersectionIdInput);

                            if (state.getBoard().validBuildingLocation(state.getCurrentPlayerId(), true, intersectionIdInput)) {
                                Log.i(TAG, "onClick: building location is valid. Sending a BuildSettlementAction to the game.");
                                game.sendAction(new CatanBuildSettlementAction(p, true, state.getCurrentPlayerId(), intersectionIdInput));
                                myActivity.findViewById(R.id.intersection_id_entered).setBackgroundColor(Color.WHITE);
                                myActivity.findViewById(R.id.group_singleIntersectionInput).setVisibility(View.GONE);

                                // todo shotty
                                game.sendAction(new CatanEndTurnAction(p));

                            } else {
                                Log.i(TAG, "onClick: invalid intersection input. ");
                                myActivity.findViewById(R.id.intersection_id_entered).setBackgroundColor(Color.RED);
                            }
                        } else {
                            Log.i(TAG, "onClick: invalid intersection input. Input is empty.");
                            myActivity.findViewById(R.id.intersection_id_entered).setBackgroundColor(Color.RED);
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
                CatanBuildRoadAction action = new CatanBuildRoadAction(this, false, 0, 1, this.playerId);
                Log.d(TAG, "onClick: Road");
                game.sendAction(action);
                return;
            }
            if (button.getId() == R.id.sidebar_button_settlement) {
                Log.i(TAG, "onClick: clicked build settlement button"); // here
                myActivity.findViewById(R.id.group_singleIntersectionInput).setVisibility(View.VISIBLE); // todo

                final CatanGameState copyState = new CatanGameState(state);
                Button confirmIntersectionButton = myActivity.findViewById(R.id.confirm);
                confirmIntersectionButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        EditText intersectionText = myActivity.findViewById(R.id.start_road_id_entered);
                        int intersectionIdInput = Integer.parseInt(intersectionText.getText().toString());
                        Log.i(TAG, "onClick: inputted intersectionId: " + intersectionIdInput);

                        if (copyState.getBoard().validBuildingLocation(copyState.getCurrentPlayerId(), true, intersectionIdInput)) {
                            Log.i(TAG, "onClick: building location is valid. Sending a BuildSettlementAction to the game.");
                            game.sendAction(new CatanBuildSettlementAction(copyState.getPlayerList().get(copyState.getCurrentPlayerId()), false, copyState.getCurrentPlayerId(), intersectionIdInput));
                            return;
                        }
                    }
                });

                CatanBuildSettlementAction action = new CatanBuildSettlementAction(this, false, this.playerId, 1);
                Log.d(TAG, "onClick: Settlement");
                game.sendAction(action);
                game.sendAction(new CatanEndTurnAction(this));
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

            //TODO Need functionality for both Port, Custom Port and Bank
            if (button.getId() == R.id.sidebar_button_trade) {
                CatanRollDiceAction a = new CatanRollDiceAction(this);
                Log.d(TAG, "onClick: Roll");
                game.sendAction(a);
                return;
            }
        }
    }// onClick

    /**
     * callback method--our game has been chosen/rechosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    public void setAsGui (GameMainActivity activity) {
        Log.d(TAG, "setAsGui() called with: activity = [" + activity + "]");
        // remember the activity
        myActivity = activity;

        // Load the layout resource for our GUI
        activity.setContentView(R.layout.activity_main);

        buildCityButton = activity.findViewById(R.id.sidebar_button_city);
        buildRoadButton = activity.findViewById(R.id.sidebar_button_road);
        buildSettlementButton = activity.findViewById(R.id.sidebar_button_settlement);
        buyDevCardButton = activity.findViewById(R.id.sidebar_button_devcards);
        endTurnButton = activity.findViewById(R.id.sidebar_button_endturn);
        //        robberDiscard = (Button)activity.findViewById(R.id.)
        //        robberMove = (Button)activity.findViewById(R.id.)
        //        robberSteal = (Button)activity.findViewById(R.id.)
        rollButton = activity.findViewById(R.id.sidebar_button_roll);
        tradeButton = activity.findViewById(R.id.sidebar_button_trade);
        tradeCustomPort = activity.findViewById(R.id.sidebar_button_trade);
        tradePort = activity.findViewById(R.id.sidebar_button_trade);
        useDevCard = activity.findViewById(R.id.use_Card);

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

        // resource value text
        this.oreValue = activity.findViewById(R.id.sidebar_value_ore);
        this.grainValue = activity.findViewById(R.id.sidebar_value_grain);
        this.lumberValue = activity.findViewById(R.id.sidebar_value_lumber);
        this.woolValue = activity.findViewById(R.id.sidebar_value_wool);
        this.brickValue = activity.findViewById(R.id.sidebar_value_brick);

        // scoreboard TextViews

        this.player0Score = activity.findViewById(R.id.Player1_Score);
        this.player1Score = activity.findViewById(R.id.Player2_Score);
        this.player2Score = activity.findViewById(R.id.Player3_Score);
        this.player3Score = activity.findViewById(R.id.Player4_Score);

        this.currentTurnIdTextView = activity.findViewById(R.id.sidebar_heading_current_turn);

        this.boardSurfaceView = activity.findViewById(R.id.board); // boardSurfaceView board is the custom SurfaceView
        this.intersectionEditText = myActivity.findViewById(R.id.intersection_id_entered);
        // button listeners TODO move to separate class?
        Button scoreButton = activity.findViewById(R.id.sidebar_button_score);
        final Group scoreBoardGroup = activity.findViewById(R.id.group_scoreboard);
        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
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
            public void onClick (View view) {
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
            public void onClick (View view) {
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
    protected void initAfterReady () {
        Log.e(TAG, "initAfterReady() called");
    }

    /*
     *
     */
    private void updateTextViews () {
        if (state == null) {
            Log.e(TAG, "updateTextViews: state is null.");
        }
        if (this.state.isSetupPhase()) {
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
            this.endTurnButton.setAlpha(0.5f);
            this.endTurnButton.setClickable(false);
            this.buyDevCardButton.setAlpha(0.5f);
            this.buyDevCardButton.setClickable(false);
            this.tradeButton.setAlpha(0.5f);
            this.tradeButton.setClickable(false);
        } else {
            this.buildCityButton.setAlpha(0f);
            this.buildCityButton.setClickable(true);
            this.rollButton.setAlpha(0f);
            this.rollButton.setClickable(true);
            this.endTurnButton.setAlpha(0f);
            this.endTurnButton.setClickable(true);
            this.buyDevCardButton.setAlpha(0f);
            this.buyDevCardButton.setClickable(true);
            this.tradeButton.setAlpha(0f);
            this.tradeButton.setClickable(true);
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

        /* ----- update misc. TextViews ----- */
        //this.myScore.setText(this.state.getPlayerVictoryPoints()[this.playerId]);

        Log.i(TAG, "updateTextViews: current player id: " + state.getCurrentPlayerId());
        this.currentTurnIdTextView.setText(String.valueOf(state.getCurrentPlayerId()));


    }

    public void drawGraphics () {
        Log.d(TAG, "drawGraphics() called");

        this.canvas = new Canvas(); // create Canvas object
        boardSurfaceView.createHexagons(this.state.getBoard());
        boardSurfaceView.createHexagons(this.state.getBoard()); // draw the board of hexagons and ports on the canvas

        int height = boardSurfaceView.getHeight();
        int width = boardSurfaceView.getWidth();

        Log.i(TAG, "drawGraphics: boardSurfaceView height: " + height + " width: " + width);

        this.boardSurfaceView.setGrid(new HexagonGrid(myActivity.getApplicationContext(), state.getBoard(), 80, 185, 175, 20));

        this.boardSurfaceView.draw(canvas);
    }

    /**
     * @param message
     */
    protected void gameIsOver (String message) {
        for (int i = 0; i < this.state.getPlayerVictoryPoints().length; i++) {
            if (this.state.getPlayerVictoryPoints()[i] > 9) {
                super.gameIsOver("Player " + i + " wins!");
            }
        }
    }

}// class CatanHumanPlayer

