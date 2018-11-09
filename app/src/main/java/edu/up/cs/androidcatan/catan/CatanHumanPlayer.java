package edu.up.cs.androidcatan.catan;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.constraint.Group;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import edu.up.cs.androidcatan.catan.actions.CatanUseDevCardAction;
import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.catan.graphics.BoardSurfaceView;
import edu.up.cs.androidcatan.catan.graphics.HexagonGrid;
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

/*---------------------------------------Instance Variables-------------------------------------------*/
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

    private ArrayList<Integer> buildingsBuiltOnThisTurn;

    private int currentBuildingSelectionId = 1;

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
    private Button buildDevCard = null;

    // other buttons on the sidebar

    private Button menuButton = (Button) null;

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
    Group roadIntersectionSelectionMenuGroup = (Group) null;
    TextView singleIntersectionTextView = (TextView) null;
    EditText singleIntersectionInputEditText = (EditText) null;
    Button singleIntersectionCancelButton = (Button) null;

    // road intersection selection menu
    EditText roadIntersectionAEditText = (EditText) null;
    EditText roadIntersectionBEditText = (EditText) null;

    TextView roadIntersectionPromptLabel = (EditText) null;
    Button roadIntersectionOkButton = (Button) null;
    Button roadIntersectionCancelButton = (Button) null;

    Group singleIntersectionInputMenuGroup = (Group) null;
    Button singleIntersectionOkButton = (Button) null;

    //Other Groups
    Group scoreBoardGroup = (Group) null;
    Group developmentGroup = (Group) null;
    Group tradeGroup = (Group) null;

    private GameMainActivity myActivity;  // the android activity that we are running

    public CatanGameState state = null; // game state

    private BoardSurfaceView boardSurfaceView;

    Canvas canvas = (Canvas) null;

    //Counter Variables
    int roadCount = 0;
    int settlementCount = 0;

    /*---------------------------------------Constructor Methods-------------------------------------------*/

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
        Log.d(TAG, "receiveInfo() called with: info: \n" + info.toString() + "----------------------------");
        if (info == null) {
            Log.e(TAG, "receiveInfo: info is null");
            return;
        }
        if (this.boardSurfaceView == null) {
            Log.e(TAG, "receiveInfo: boardSurfaceView is null.");
            return;
        }

        if (info instanceof CatanGameState) {
            // set resource count TextViews to the players resource inventory amounts
            Log.i(TAG, "receiveInfo: player list: " + ((CatanGameState) info).getPlayerList());

            this.state = (CatanGameState) info;

            updateTextViews();
            drawGraphics();

        } else if (info instanceof NotYourTurnInfo) {
            Log.i(TAG, "receiveInfo: Player tried to make action but it is not thier turn.");
        } else if (info instanceof IllegalMoveInfo) {
            Log.i(TAG, "receiveInfo: Illegal move info received.");
        } else {
            Log.e(TAG, "receiveInfo: Received instanceof not anything we know. Returning void.");
        }
    }//receiveInfo

/*---------------------------------------onClick Methods-------------------------------------------*/
    /**
     * this method gets called when the user clicks the die or hold button. It
     * creates a new CatanRollAction or CatanHoldAction and sends it to the game.
     * creates a new CatanRollAction or CatanHoldAction and sends it to the game.
     *
     * @param button the button that was clicked
     */
    public void onClick (View button) {

        Log.d(TAG, "onClick() called with: button = [" + button + "]");

        if (state == null) {
            Log.e(TAG, "onClick: state is null.");
        } // check if state is null


        /* ---------- actions other than building ---------- */
        if(button.getId() == R.id.menu_settings){
            Log.d(TAG, state.toString());
            return;
        }

        if(button.getId() == R.id.sidebar_button_score){
            toggleGroupVisibility(scoreBoardGroup); // toggle menu vis.
            return;
        }
        if (button.getId() == R.id.sidebar_button_roll) {
            CatanRollDiceAction a = new CatanRollDiceAction(this);
            Log.d(TAG, "onClick: Roll");
            game.sendAction(a);
            return;
        }
        if (button.getId() == R.id.sidebar_button_endturn) {
            if (state.isSetupPhase()) {

            }
            Log.d(TAG, "onClick: End Turn");

            game.sendAction(new CatanEndTurnAction(this));
            this.buildingsBuiltOnThisTurn = new ArrayList<>();
            return;
        }

        /* ---------- Trade action buttons ---------- */
        //TODO Need functionality for both Port, Custom Port and Bank
        if (button.getId() == R.id.sidebar_button_trade) {
            // toggle menu vis.
            toggleGroupVisibility(tradeGroup);
            return;
        }

        /* ---------- Building sidebar buttons ---------- */

        if(button.getId() == R.id.sidebar_button_road){
            if (roadIntersectionSelectionMenuGroup.getVisibility() == View.GONE) {
                developmentGroup.setVisibility(View.GONE);
                tradeGroup.setVisibility(View.GONE);
                singleIntersectionInputMenuGroup.setVisibility(View.GONE);
                roadIntersectionSelectionMenuGroup.setVisibility(View.VISIBLE);
                currentBuildingSelectionId = 0;
            }
            return;
        }

        if(button.getId() == R.id.sidebar_button_settlement){
            if (singleIntersectionInputMenuGroup.getVisibility() == View.GONE) {
                developmentGroup.setVisibility(View.GONE);
                tradeGroup.setVisibility(View.GONE);
                roadIntersectionSelectionMenuGroup.setVisibility(View.GONE);
                singleIntersectionInputMenuGroup.setVisibility(View.VISIBLE);
                currentBuildingSelectionId = 1;
            }
            return;
        }

        if (button.getId() == R.id.sidebar_button_city) {
            if (singleIntersectionInputMenuGroup.getVisibility() == View.GONE) {
                developmentGroup.setVisibility(View.GONE);
                tradeGroup.setVisibility(View.GONE);
                roadIntersectionSelectionMenuGroup.setVisibility(View.GONE);
                singleIntersectionInputMenuGroup.setVisibility(View.VISIBLE);
                currentBuildingSelectionId = 2;
            }
            return;
        }

        /* ---------- Building confirmation buttons ---------- */
        if(button.getId() == R.id.button_roadOk){
            int intersectionA;
            int intersectionB;
            try {
                intersectionA = Integer.parseInt(roadIntersectionAEditText.getText().toString());
                intersectionB = Integer.parseInt(roadIntersectionBEditText.getText().toString());
            }catch(NumberFormatException nfe){
                Log.e(TAG, "onClick: Error, not integer");
                Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
                roadIntersectionAEditText.startAnimation(shake);
                return;
            }

            Log.e(TAG, "onClick: Single intersection id input: " + intersectionA + " and: " + intersectionB + ". Selected building id: " + currentBuildingSelectionId);

            if (tryBuildRoad(intersectionA, intersectionB)) {
                CatanBuildRoadAction action = new CatanBuildRoadAction(this, state.isSetupPhase(), intersectionA, intersectionB, this.playerId);
                game.sendAction(action);

                Log.d(TAG, "onClick: valid location");
                // toggle menu vis.
                toggleGroupVisibility(roadIntersectionSelectionMenuGroup);
                currentBuildingSelectionId = -1;
                if(state.isSetupPhase()){
                    currentBuildingSelectionId = 1;
                    roadCount++;
                    if(roadCount == 2 && settlementCount == 2){
                        Log.d(TAG, "onClick: End setup phase for player");
                        game.sendAction(new CatanEndTurnAction(this));
                        this.buildingsBuiltOnThisTurn = new ArrayList<>();
                        state.updateSetupPhase();

                        return;
                    }
                    toggleGroupVisibility(singleIntersectionInputMenuGroup);
                }
            } else {
                Log.d(TAG, "onClick: invalid location");
            }
            return;
        }

        if(button.getId() == R.id.button_roadCancel){
            toggleGroupVisibility(roadIntersectionSelectionMenuGroup);
            currentBuildingSelectionId = -1;
        }

        if(button.getId() == R.id.button_singleIntersectionMenuOk){
            int singleIntersectionIdInput;
            if(singleIntersectionInputEditText.getText().equals("")){
                Log.d(TAG, "onClick: Intersection is null (" + singleIntersectionInputEditText.getText() + ")");
                return;
            }
            try {
                singleIntersectionIdInput = Integer.parseInt(singleIntersectionInputEditText.getText().toString());
            }catch(NumberFormatException nfe){
                Log.e(TAG, "onClick: Error, not integer");
                Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
                singleIntersectionInputEditText.startAnimation(shake);
                return;
            }
            Log.e(TAG, "onClick: Single intersection id input: " + singleIntersectionIdInput + " selected building id: " + currentBuildingSelectionId);

            if (tryBuildSettlement(singleIntersectionIdInput)) {
                if(currentBuildingSelectionId == 1) {
                    CatanBuildSettlementAction action = new CatanBuildSettlementAction(this, state.isSetupPhase(), singleIntersectionIdInput, this.playerId);
                    game.sendAction(action);
                }
                else{
                    CatanBuildCityAction action = new CatanBuildCityAction(this, state.isSetupPhase(), singleIntersectionIdInput, this.playerId);
                    game.sendAction(action);
                }
                Log.d(TAG, "onClick: valid location");
                // toggle menu vis.
                toggleGroupVisibility(singleIntersectionInputMenuGroup);
                currentBuildingSelectionId = -1;
                if(state.isSetupPhase()){
                    toggleGroupVisibility(roadIntersectionSelectionMenuGroup);
                    roadIntersectionAEditText.setText("" + singleIntersectionIdInput + "");
                    currentBuildingSelectionId = 0;
                    settlementCount++;
                }
            } else {
                Log.d(TAG, "onClick: invalid location at " + singleIntersectionIdInput);
            }
            return;
        }

        if(button.getId() == R.id.button_singleIntersectionMenuCancel){
            toggleGroupVisibility(singleIntersectionInputMenuGroup);
            currentBuildingSelectionId = -1;
            return;
        }
        /* ---------- Development card buttons ---------- */
        if(button.getId() == R.id.sidebar_button_devcards){
            // toggle menu vis.
            toggleGroupVisibility(developmentGroup);
            return;
        }

        if(button.getId() == R.id.use_Card){
            CatanUseDevCardAction action = new CatanUseDevCardAction(this);
            game.sendAction(action);
            return;
        }

        if(button.getId() == R.id.build_devCard){
            CatanBuyDevCardAction action = new CatanBuyDevCardAction(this);
            game.sendAction(action);
            return;
        }



    }// onClick


/*-----------------------------------------GUI Methods-------------------------------------------*/
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

        /* ---------- action buttons ---------- */

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
        buildDevCard = activity.findViewById(R.id.build_devCard);

        /* ---------- action button listeners ---------- */
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
        buildDevCard.setOnClickListener(this);

        /* ---------- resource value text ---------- */

        this.oreValue = activity.findViewById(R.id.sidebar_value_ore);
        this.grainValue = activity.findViewById(R.id.sidebar_value_grain);
        this.lumberValue = activity.findViewById(R.id.sidebar_value_lumber);
        this.woolValue = activity.findViewById(R.id.sidebar_value_wool);
        this.brickValue = activity.findViewById(R.id.sidebar_value_brick);

        /* ---------- scoreboard scores TextViews ---------- */

        this.player0Score = activity.findViewById(R.id.Player1_Score);
        this.player1Score = activity.findViewById(R.id.Player2_Score);
        this.player2Score = activity.findViewById(R.id.Player3_Score);
        this.player3Score = activity.findViewById(R.id.Player4_Score);

        /* ---------- scoreboard names ---------- */

        this.player0Name = activity.findViewById(R.id.Player1_Name);
        this.player1Name = activity.findViewById(R.id.Player2_Name);
        this.player2Name = activity.findViewById(R.id.Player3_Name);
        this.player3Name = activity.findViewById(R.id.Player4_Name);

        this.myScore = activity.findViewById(R.id.sidebar_heading_vp);

        this.currentTurnIdTextView = activity.findViewById(R.id.sidebar_heading_current_turn);

        /* ---------- menu button listener ---------- */

        this.menuButton = activity.findViewById(R.id.sidebar_button_menu);

        this.menuButton.setOnClickListener(this);

        /* ---------- single intersection menu (buildings) ---------- */

        singleIntersectionInputMenuGroup = myActivity.findViewById(R.id.group_singleIntersectionInput);

        singleIntersectionOkButton = myActivity.findViewById(R.id.button_singleIntersectionMenuOk);
        singleIntersectionTextView = myActivity.findViewById(R.id.selectIntersectionText);
        singleIntersectionInputEditText = myActivity.findViewById(R.id.editText_singleIntersectionInput);
        singleIntersectionCancelButton = myActivity.findViewById(R.id.button_singleIntersectionMenuCancel);

        // OK button for single intersection input
        singleIntersectionOkButton.setOnClickListener(this);

        singleIntersectionCancelButton.setOnClickListener(this);

        /* ---------- road intersection menu ---------- */

        roadIntersectionSelectionMenuGroup = activity.findViewById(R.id.group_road_intersection_selection_menu);

        roadIntersectionAEditText = activity.findViewById(R.id.start_road_id_entered);
        roadIntersectionBEditText = activity.findViewById(R.id.end_road_id_entered);

        roadIntersectionPromptLabel = activity.findViewById(R.id.selectRoadIntersectionText);
        roadIntersectionOkButton = activity.findViewById(R.id.button_roadOk);
        roadIntersectionCancelButton = activity.findViewById(R.id.button_roadCancel);

        roadIntersectionOkButton.setOnClickListener(this);

        this.boardSurfaceView = activity.findViewById(R.id.board); // boardSurfaceView board is the custom SurfaceView
        // button listeners
        Button scoreButton = activity.findViewById(R.id.sidebar_button_score);
        scoreBoardGroup = activity.findViewById(R.id.group_scoreboard);
        scoreButton.setOnClickListener(this);

        Button developmentButton = activity.findViewById(R.id.sidebar_button_devcards);
        developmentGroup = activity.findViewById(R.id.group_development_card_menu);
        developmentButton.setOnClickListener(this);

        //Trade group
        tradeGroup = activity.findViewById(R.id.group_trade_menu);
        tradeGroup.setOnClickListener(this);

        // if we have state update the GUI based on the state
        if (this.state != null) {
            receiveInfo(state);
        }
        
    }//setAsGui

/*---------------------------------------Validation Methods-------------------------------------------*/
    private boolean tryBuildRoad (int intersectionA, int intersectionB) {
        Log.d(TAG, "tryBuildRoad() called with: intersectionA = [" + intersectionA + "], intersectionB = [" + intersectionB + "]");

        // check if current building selection id matches that of the method call
        if (this.currentBuildingSelectionId != 0) {
            Log.e(TAG, "tryBuildRoad: currentBuildingSelectionId does not equal 0 (road id). Returning false.");
            return false;
        }

        // check if user given intersections are valid
        if (state.getBoard().validRoadPlacement(state.getCurrentPlayerId(), state.isSetupPhase(), intersectionA, intersectionB)) {
            Log.i(TAG, "tryBuildRoad: Valid road placement received.");

            // add just enough resources so player can build a road
            state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(0, 1); // give 1 brick
            state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(2, 1); // give 1 lumber

            // send build settlement action to the game
            Log.e(TAG, "tryBuildRoad: Sending a CatanBuildRoadAction to the game.");
            game.sendAction(new CatanBuildRoadAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersectionA, intersectionB));

            // return true
            Log.d(TAG, "tryBuildRoad() returned: " + true);
            return true;
        } else {
            Log.e(TAG, "tryBuildSettlement: Returning false.");
            Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
            roadIntersectionAEditText.startAnimation(shake);
            return false;
        }
    }

    private boolean tryBuildSettlement (int intersection1) {

        Log.d(TAG, "tryBuildSettlement() called with: intersection1 = [" + intersection1 + "]");

        if (this.currentBuildingSelectionId != 1) {
            Log.e(TAG, "tryBuildSettlement: Error the currently selected building id is not a settlement.");
            return false;
        }
        if (state.getBoard().validBuildingLocation(state.getCurrentPlayerId(), true, intersection1)) {
            Log.i(TAG, "onClick: building location is valid. Sending a BuildSettlementAction to the game.");

            // add just enough resources so player can build settlement
            state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(0, 1); // give 1 brick
            state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(1, 1); // give 1 grain
            state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(2, 1); // give 1 lumber
            state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(4, 1); // give 1 wool

            // send build settlement action to the game
            Log.e(TAG, "tryBuildSettlement: Sending a CatanBuildSettlementAction to the game.");
            game.sendAction(new CatanBuildSettlementAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersection1));
            Log.d(TAG, "tryBuildSettlement() returned: " + true);

            return true;
        } else {
            Log.e(TAG, "tryBuildSettlement: Returning false.");
            Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
            singleIntersectionInputEditText.startAnimation(shake);
            return false;
        }
    }

    /**
     *
     */
    protected void initAfterReady () {
        Log.e(TAG, "initAfterReady() called");
    }

    /**
     *
     */
    private void updateTextViews () {
        if (state == null) {
            Log.e(TAG, "updateTextViews: state is null. Returning void.");
            return;
        }

        if (this.state.isSetupPhase()) {
            // if it is the setup phase, grey out some buttons and make them un clickable
            this.buildRoadButton.setAlpha(0.5f);
            this.buildRoadButton.setClickable(false);
            this.buildSettlementButton.setAlpha(0.5f);
            this.buildSettlementButton.setClickable(false);
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
            this.buyDevCardButton.setAlpha(0.5f);
            this.buyDevCardButton.setClickable(false);
            this.tradeButton.setAlpha(0.5f);
            this.tradeButton.setClickable(false);
            this.endTurnButton.setAlpha(0.5f);
            this.endTurnButton.setClickable(false);
            
            this.singleIntersectionCancelButton.setAlpha(0.5f);
            this.singleIntersectionCancelButton.setClickable(false);
            this.roadIntersectionCancelButton.setAlpha(0.5f);
            this.roadIntersectionCancelButton.setClickable(false);
            this.roadIntersectionAEditText.setAlpha(0.5f);
            this.roadIntersectionAEditText.setEnabled(false);
        } else {
            // if it is NOT the setup phase, no greyed out buttons and all are clickable
            setAllButtonsToVisible();
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

        /* ----- update scoreboard names ----- */
        player0Name.setBackgroundColor(Color.TRANSPARENT);
        player1Name.setBackgroundColor(Color.TRANSPARENT);
        player2Name.setBackgroundColor(Color.TRANSPARENT);
        player3Name.setBackgroundColor(Color.TRANSPARENT);

        switch (state.getCurrentPlayerId()) {
            case 0:
                player0Name.setBackgroundColor(Color.WHITE);
                break;
            case 1:
                player1Name.setBackgroundColor(Color.WHITE);
                break;
            case 2:
                player2Name.setBackgroundColor(Color.WHITE);
                break;
            case 3:
                player3Name.setBackgroundColor(Color.WHITE);
                break;
        }

        /* ----- update misc. TextViews ----- */

        // human player score (sidebar menu)
        this.myScore.setText(String.valueOf(this.state.getPlayerVictoryPoints()[this.playerId]));

        // current turn indicator (sidebar menu)
        this.currentTurnIdTextView.setText(String.valueOf(state.getCurrentPlayerId()));

    } // end updateTextViews

    private void drawGraphics () {
        Log.d(TAG, "drawGraphics() called");

        this.canvas = new Canvas(); // create Canvas object
        boardSurfaceView.createHexagons(this.state.getBoard());
        boardSurfaceView.createHexagons(this.state.getBoard()); // draw the board of hexagons and ports on the canvas

        int height = boardSurfaceView.getHeight();
        int width = boardSurfaceView.getWidth();

        Log.i(TAG, "drawGraphics: boardSurfaceView height: " + height + " width: " + width);

        this.boardSurfaceView.setGrid(new HexagonGrid(myActivity.getApplicationContext(), state.getBoard(), 80, 185, 175, 20));
        this.boardSurfaceView.draw(canvas);

        boardSurfaceView.invalidate();
    } // end drawGraphics

    /**
     * @param message Game over message.
     */
    protected void gameIsOver (String message) {
        for (int i = 0; i < this.state.getPlayerVictoryPoints().length; i++) {
            if (this.state.getPlayerVictoryPoints()[i] > 9) {
                super.gameIsOver("Player " + i + " wins!");
            }
        }
    } // end gameIsOver

    /**
     * Toggles the visibility of a group.
     *
     * @param group Group to toggle visibility.
     */
    private void toggleGroupVisibility (Group group) {
        if (group.getVisibility() == View.GONE) group.setVisibility(View.VISIBLE);
        else group.setVisibility(View.GONE);
    }

    public void setAllButtonsToVisible () {
        this.buildRoadButton.setAlpha(1f);
        this.buildRoadButton.setClickable(true);
        this.buildSettlementButton.setAlpha(1f);
        this.buildSettlementButton.setClickable(true);
        this.buildCityButton.setAlpha(1f);
        this.buildCityButton.setClickable(true);
        this.rollButton.setAlpha(1f);
        this.rollButton.setClickable(true);
        this.endTurnButton.setAlpha(1f);
        this.endTurnButton.setClickable(true);
        this.buyDevCardButton.setAlpha(1f);
        this.buyDevCardButton.setClickable(true);
        this.tradeButton.setAlpha(1f);
        this.tradeButton.setClickable(true);
        this.buildSettlementButton.setAlpha(1f);
        this.buildSettlementButton.setClickable(true);
        this.buildRoadButton.setAlpha(1f);
        this.buildRoadButton.setClickable(true);
        this.endTurnButton.setAlpha(1f);
        this.endTurnButton.setClickable(true);

        this.singleIntersectionCancelButton.setAlpha(1f);
        this.singleIntersectionCancelButton.setClickable(true);
        this.roadIntersectionCancelButton.setAlpha(1f);
        this.roadIntersectionCancelButton.setClickable(true);
        this.roadIntersectionAEditText.setAlpha(1f);
        this.roadIntersectionAEditText.setEnabled(true);
    }
    
}// class CatanHumanPlayer

