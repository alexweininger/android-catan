package edu.up.cs.androidcatan.catan;


import android.graphics.Canvas;
import android.graphics.Color;
import android.support.constraint.Group;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuyDevCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithPortAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseKnightCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseMonopolyCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseRoadBuildingCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseVictoryPointCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseYearOfPlentyCardAction;
import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.Port;
import edu.up.cs.androidcatan.catan.gamestate.buildings.Road;
import edu.up.cs.androidcatan.catan.graphics.BoardSurfaceView;
import edu.up.cs.androidcatan.catan.graphics.HexagonDrawable;
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
    private final String TAG = "CatanHumanPlayer";

    // instance variables for logic
    private ArrayList<Integer> buildingsBuiltOnThisTurn = new ArrayList<>();
    private int currentBuildingSelectionId = 1;
    private float lastTouchDownXY[] = new float[2];
    boolean debugMode = false;

    private TextView messageTextView = (TextView) null;

    private int selectedHexagonId = -1;

    private ArrayList<Integer> selectedIntersections = new ArrayList<>();

    private boolean isMenuOpen;

    /* ------------------------------ SCOREBOARD button init ------------------------------------ */

    /* ------------- Building Buttons -------------------- */
    private Button buildCityButton = null;
    private Button buildRoadButton = null;
    private Button buildSettlementButton = null;

    /* ------------- Action Buttons -------------------- */
    private Button sidebarOpenDevCardMenuButton = null;
    private Button robberDiscard = null;
    private Button robberMove = null;
    private Button robberSteal = null;
    private Button tradeButton = null;
    private Button tradeCustomPort = null;
    private Button tradePort = null;
    private Button useDevCard = null;
    private Button buildDevCard = null;
    private Spinner devCardList = null;

    /* ------ Turn Buttons ------- */
    private Button rollButton = null;
    private ImageView diceImageLeft = null;
    private ImageView diceImageRight = null;
    private Button endTurnButton = null;

    /* ------------- Misc Buttons -------------------- */

    private Button sidebarMenuButton = (Button) null;
    private ImageView buildingCosts = null;
    private Button sidebarScoreboardButton = (Button) null;

    /* ------------- resource count text views -------------------- */

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
    private TextView playerNameSidebar = (TextView) null;

    // intersection menu
    private Group roadIntersectionSelectionMenuGroup = (Group) null;
    private TextView singleIntersectionLabelTextView = (TextView) null;
    private EditText singleIntersectionInputEditText = (EditText) null;
    private Button singleIntersectionCancelButton = (Button) null;

    // road intersection selection menu
    private EditText roadIntersectionAEditText = (EditText) null;
    private EditText roadIntersectionBEditText = (EditText) null;

    private TextView roadIntersectionPromptLabel = (EditText) null;
    private Button roadIntersectionOkButton = (Button) null;
    private Button roadIntersectionCancelButton = (Button) null;

    private Group singleIntersectionInputMenuGroup = (Group) null;
    private Button singleIntersectionOkButton = (Button) null;

    //Robber Buttons
    private ImageView robberBrickPlus = (ImageView) null;
    private ImageView robberBrickMinus = (ImageView) null;
    private ImageView robberLumberPlus = (ImageView) null;
    private ImageView robberLumberMinus = (ImageView) null;
    private ImageView robberGrainPlus = (ImageView) null;
    private ImageView robberGrainMinus = (ImageView) null;
    private ImageView robberOrePlus = (ImageView) null;
    private ImageView robberOreMinus = (ImageView) null;
    private ImageView robberWoolPlus = (ImageView) null;
    private ImageView robberWoolMinus = (ImageView) null;

    //Trade Buttons - Receive
    private ImageView brickSelectionBoxReceive = (ImageView) null;
    private ImageView grainSelectionBoxReceive = (ImageView) null;
    private ImageView lumberSelectionBoxReceive = (ImageView) null;
    private ImageView oreSelectionBoxReceive = (ImageView) null;
    private ImageView woolSelectionBoxReceive = (ImageView) null;

    //Trade Buttons - Give
    private ImageView brickSelectionBoxGive = (ImageView) null;
    private ImageView grainSelectionBoxGive = (ImageView) null;
    private ImageView lumberSelectionBoxGive = (ImageView) null;
    private ImageView oreSelectionBoxGive = (ImageView) null;
    private ImageView woolSelectionBoxGive = (ImageView) null;

    //Trade Menu - Receive
    private ImageView image_trade_menu_rec_brick = (ImageView) null;
    private ImageView image_trade_menu_rec_grain = (ImageView) null;
    private ImageView image_trade_menu_rec_ore = (ImageView) null;
    private ImageView image_trade_menu_rec_lumber = (ImageView) null;
    private ImageView image_trade_menu_rec_wool = (ImageView) null;

    //Trade Menu - Give
    private ImageView image_trade_menu_give_brick = (ImageView) null;
    private ImageView image_trade_menu_give_grain = (ImageView) null;
    private ImageView image_trade_menu_give_lumber = (ImageView) null;
    private ImageView image_trade_menu_give_ore = (ImageView) null;
    private ImageView image_trade_menu_give_wool = (ImageView) null;

    //Trade Menu - Confirm and Cancel
    private Button button_trade_menu_confirm = (Button) null;
    private Button button_trade_menu_cancel = (Button) null;
    private int tradeGiveSelection = -1;
    private int tradeReceiveSelection = -1;

    //Other Groups
    private Group scoreBoardGroup = (Group) null;
    private Group developmentGroup = (Group) null;
    private Group tradeGroup = (Group) null;
    private Group robberDiscardGroup = (Group) null;

    private GameMainActivity myActivity;  // the android activity that we are running

    public CatanGameState state = null; // game state

    private BoardSurfaceView boardSurfaceView;

    private int roadCount = 0; // counter variables
    private int settlementCount = 0;

    /*--------------------- Constructors ------------------------*/

    public CatanHumanPlayer (String name) {
        super(name);
    }

    /*---------------------------------- onClick Method -----------------------------------------*/

    /**
     * this method gets called when the user clicks ANY button that has the listener set to 'this'
     *
     * @param button the button that was clicked
     */
    public void onClick (View button) {

        Log.d(TAG, "onClick() called with: button = [" + button + "]");

        if (state == null) {
            Log.e(TAG, "onClick: state is null.");
        } // check if state is null

        /* ---------- Turn Actions ---------- */

        if (button.getId() == R.id.sidebar_button_roll) {
            Log.d(TAG, "onClick: Roll");
            game.sendAction(new CatanRollDiceAction(this));

            if (state.getCurrentDiceSum() == 7) {
                //TODO Make robber menu appear
                Log.i(TAG, "onClick: Robber has been activated");
                state.setRobberPhase(true);
            }
            return;
        }

        if (button.getId() == R.id.sidebar_button_endturn) {
            if (state.isSetupPhase()) {
                // todo @Niraj Mali? - AW
            }
            Log.d(TAG, "onClick: End Turn");

            game.sendAction(new CatanEndTurnAction(this));
            this.buildingsBuiltOnThisTurn = new ArrayList<>();
            return;
        }

        /* ---------- Misc. Buttons ---------- */

        if (button.getId() == R.id.sidebar_button_menu) {
            this.boardSurfaceView.getGrid().toggleDebugMode();
            this.boardSurfaceView.invalidate();
            this.debugMode = !this.debugMode;
            if (this.buildingCosts.getVisibility() == View.VISIBLE) {
                this.buildingCosts.setVisibility(View.GONE);
            } else {
                this.buildingCosts.setVisibility(View.VISIBLE);
            }
            Log.e(TAG, "onClick: toggled debug mode");
            Log.d(TAG, state.toString());
            return;
        }

        if (button.getId() == R.id.sidebar_button_score) {
            toggleGroupVisibility(scoreBoardGroup); // toggle menu vis.
            return;
        }

        /*-------------------- Robber ------------------------*/

        if (button.getId() == R.id.robber_discard_brickAddImg) {

        }
        if (button.getId() == R.id.robber_discard_brickMinusImg) {

        }
        if (button.getId() == R.id.robber_discard_lumberAddImg) {

        }
        if (button.getId() == R.id.robber_discard_lumberMinusImg) {

        }
        if (button.getId() == R.id.robber_discard_grainAddImg) {

        }
        if (button.getId() == R.id.robber_discard_grainMinusImg) {

        }
        if (button.getId() == R.id.robber_discard_oreAddImg) {

        }
        if (button.getId() == R.id.robber_discard_oreMinusImg) {

        }
        if (button.getId() == R.id.robber_discard_woolAddImg) {

        }
        if (button.getId() == R.id.robber_discard_woolMinusImg) {

        }

        /* ---------- Trade action buttons ---------- */

        //TODO Need functionality for both Port, Custom Port and Bank
        if (button.getId() == R.id.sidebar_button_trade) {

            if (selectedIntersections.size() == 0) {
                if (tryTradeWithBank()) {

                }
            } else if (selectedIntersections.size() == 1) {

            }

            // toggle menu vis.
            toggleGroupVisibility(tradeGroup);
            return;
        }

        /* ---------------------------- Building Sidebar Button OnClick() Handlers --------------------- */

        // Road button on the sidebar.
        if (button.getId() == R.id.sidebar_button_road) {
            currentBuildingSelectionId = 0;
            if (selectedIntersections.size() != 2) {
                messageTextView.setText("Select two intersections to build a road.");
            } else {
                if (tryBuildRoad(selectedIntersections.get(0), selectedIntersections.get(1))) {
                    messageTextView.setText("Built a road.");
                } else {
                    messageTextView.setText("Invalid road placement.");
                }
            }
            return;
        }

        // Settlement button on the sidebar.
        if (button.getId() == R.id.sidebar_button_settlement) {
            Log.d(TAG, "onClick: sidebar_button_settlement listener");
            if (selectedIntersections.size() != 1) {
                messageTextView.setText("Select one intersection to build a settlement.");
            } else {
                if (tryBuildSettlement(selectedIntersections.get(0))) {
                    messageTextView.setText("Built a settlement.");
                } else {
                    messageTextView.setText("Invalid settlement location.");
                }
            }
            return;
        }

        // City button on the sidebar.
        if (button.getId() == R.id.sidebar_button_city) {
            if (selectedIntersections.size() != 1) {
                messageTextView.setText("Select one intersection to build a city.");
            } else {
                if (tryBuildCity(selectedIntersections.get(0))) {
                    messageTextView.setText("Built a city.");
                } else {
                    messageTextView.setText("Invalid city location.");
                }
            }
            return;
        }

        /* -------------------- Development Card Button OnClick() Handlers ---------------------- */

        // Development button located on the sidebar. Should only show/hide dev card menu.
        if (button.getId() == R.id.sidebar_button_devcards) {
            toggleGroupVisibility(developmentGroup); // toggle menu vis.
            return;
        }

        // Buy development card button on the dev card menu. This sends a BuyDevCard action to the game state.
        if (button.getId() == R.id.build_devCard) {

            // try to remove the resources required to buy a dev card from the players inventory
            if (state.getPlayerList().get(state.getCurrentPlayerId()).removeResourceBundle(DevelopmentCard.resourceCost)) {
                // the CatanBuyDevCardAction holds the player, and the currently selected dev card id from the spinner.
                game.sendAction(new CatanBuyDevCardAction(this, devCardList.getSelectedItemPosition()));
                return;
            } else {
                // else: meaning the player does not have enough resources to buy dev card
                Log.i(TAG, "onClick: Player " + this.playerNum + " tried to buy a dev card. But does not have enough resources. (removeResourceBundle returned false.)");

                // tell the user with the message text view
                messageTextView.setText("You do not have enough resources to buy a development.");

                // shake the message text view TODO make this a helper method bc we use it a lot for this one repeated thing
                Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
                roadIntersectionBEditText.startAnimation(shake);
                messageTextView.startAnimation(shake);
                return;
            }
        }

        // Use development card button on the dev card menu.
        if (button.getId() == R.id.use_Card) {
            // todo, validate the player can use the card. e.g. they have it etc. and then send the action

            // this long af expression does this: gets the spinner position and checks if the player has that specific dev card in their dev card list
            if (state.getPlayerList().get(state.getCurrentPlayerId()).getDevelopmentCards().contains(devCardList.getSelectedItemPosition())) {
                int developmentCardId = devCardList.getSelectedItemPosition();

                // remove the card the player is using from their dev card list. At this point all validation should be over.
                state.getPlayerList().get(state.getCurrentPlayerId()).removeDevCard(developmentCardId);

                // if chain to send specific action class to CatanLocalGame
                if (developmentCardId == 0) {
                    Log.d(TAG, "onClick: sending CatanUseKnightCard action to the game. From player " + this.playerNum);
                    game.sendAction(new CatanUseKnightCardAction(this));
                    return;
                }
                if (developmentCardId == 1) {
                    Log.d(TAG, "onClick: sending CatanUseVictoryPointCard action to the game. From player " + this.playerNum);
                    game.sendAction(new CatanUseVictoryPointCardAction(this));
                    return;
                }
                if (developmentCardId == 2) {
                    Log.d(TAG, "onClick: sending CatanUseYearOfPlentyCard action to the game. From player " + this.playerNum);
                    game.sendAction(new CatanUseYearOfPlentyCardAction(this));
                    return;
                }
                if (developmentCardId == 3) {
                    Log.d(TAG, "onClick: sending CatanUseMonopolyCardAction action to the game. From player " + this.playerNum);
                    game.sendAction(new CatanUseMonopolyCardAction(this));
                    return;
                }
                if (developmentCardId == 4) {
                    Log.d(TAG, "onClick: sending CatanUseRoadBuildingCard action to the game. From player " + this.playerNum);
                    game.sendAction(new CatanUseRoadBuildingCardAction(this));
                    return;
                }
            }
        }

        /* ------------------------------------ Trade Menu -------------------------------------- */

        // arrays of the selection box image views
        ImageView selectionBoxGive[] = {brickSelectionBoxGive, grainSelectionBoxGive, lumberSelectionBoxGive, oreSelectionBoxGive, woolSelectionBoxGive};
        ImageView selectionBoxReceive[] = {brickSelectionBoxReceive, grainSelectionBoxReceive, lumberSelectionBoxReceive, oreSelectionBoxReceive, woolSelectionBoxReceive};

        // set all give selection boxes to transparent
        for (ImageView imageView : selectionBoxGive) {
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }

        // set all receive selection boxes to transparent
        for (ImageView imageView : selectionBoxReceive) {
            imageView.setBackgroundColor(Color.TRANSPARENT);
        }

        // arrays of the buttons
        int giveButtonIds[] = {R.id.image_trade_menu_give_brick, R.id.image_trade_menu_give_grain, R.id.image_trade_menu_give_lumber, R.id.image_trade_menu_give_ore, R.id.image_trade_menu_give_wool};
        int recButtonIds[] = {R.id.image_trade_menu_rec_brick, R.id.image_trade_menu_rec_grain, R.id.image_trade_menu_rec_lumber, R.id.image_trade_menu_rec_ore, R.id.image_trade_menu_rec_wool};

        for (int i = 0; i < 5; i++) {
            if (button.getId() == giveButtonIds[i]) {
                tradeGiveSelection = i;
                break;
            }
            if (button.getId() == recButtonIds[i]) {
                tradeReceiveSelection = i;
                break;
            }
        }

        if (tradeReceiveSelection != -1) {
            selectionBoxReceive[tradeReceiveSelection].setBackgroundColor(Color.argb(255, 255, 255, 187));
        }
        if (tradeGiveSelection != -1) {
            selectionBoxGive[tradeGiveSelection].setBackgroundColor(Color.argb(255, 255, 255, 187));
        }

        if (button.getId() == R.id.button_trade_menu_confirm) {
            if (selectedIntersections.size() > 0) {
                if (tryTradeWithPort(tradeGiveSelection, tradeReceiveSelection)) {
                    Log.d(TAG, "onClick: traded with port");
                } else {
                    Log.d(TAG, "onClick: invalid location");
                }
            }
        }

        if (button.getId() == R.id.button_trade_menu_cancel) {
            toggleGroupVisibility(tradeGroup);
        }

    } // onClick END

    /* ----------------------- BoardSurfaceView Touch Listeners --------------------------------- */

    // the purpose of the touch listener is just to store the touch X,Y coordinates
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch (View v, MotionEvent event) {

            // save the X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.getX();
                lastTouchDownXY[1] = event.getY();
            }

            // let the touch event pass on to whoever needs it
            return false;
        }
    }; // touchListener END

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick (View v) {
            // retrieve the stored coordinates
            float x = lastTouchDownXY[0];
            float y = lastTouchDownXY[1];

            boolean touchedIntersection = false;
            boolean touchedHexagon = false;

            HexagonGrid grid = boardSurfaceView.getGrid();

            // use the coordinates for whatever
            Log.i("TAG", "onLongClick: x = " + x + ", y = " + y);

            for (int i = 0; i < grid.getIntersections().length; i++) {
                int xPos = grid.getIntersections()[i].getxPos();
                int yPos = grid.getIntersections()[i].getyPos();

                // if y is greater than y - 25 and less than y + 25
                if (y > yPos - 100 && y < yPos + 100 && x > xPos - 100 && x < xPos + 100) {
                    // if x is greater than point 3 and less than point 0
                    Log.w(TAG, "onClick: Touched intersection id: " + grid.getIntersections()[i].getIntersectionId());
                    touchedIntersection = true;

                    if (grid.getHighlightedIntersections().contains(i)) {
                        boardSurfaceView.getGrid().getHighlightedIntersections().remove((Integer) i);
                        selectedIntersections.remove((Integer) i);
                    } else {
                        boardSurfaceView.getGrid().addHighlightedIntersection(i);
                        if (selectedIntersections.size() > 1) {
                            selectedIntersections.remove(0);
                        }
                        selectedIntersections.add(i);
                    }

                    boardSurfaceView.getGrid().setHighlightedHexagon(-1);
                    selectedHexagonId = -1;
                    boardSurfaceView.invalidate();
                }
            }
            if (!touchedIntersection) {
                ArrayList<HexagonDrawable> dHexes = grid.getDrawingHexagons();

                int index = 0;
                for (HexagonDrawable hex : dHexes) {
                    int[][] points = hex.getHexagonPoints();

                    // if y is greater than point 0 and less than point 1
                    if (y > points[0][1] && y < points[1][1]) {
                        // if x is greater than point 3 and less than point 0
                        if (x > points[3][0] && x < points[0][0]) {
                            Hexagon dataHexagon = state.getBoard().getHexagonListForDrawing().get(index);
                            Log.w(TAG, "onClick: Touched hexagon id: " + dataHexagon.getHexagonId());
                            touchedHexagon = true;

                            if (dataHexagon.getHexagonId() == boardSurfaceView.getGrid().getHighlightedHexagon()) {
                                // if the hexagon touched is already selected, un-select it
                                boardSurfaceView.getGrid().setHighlightedHexagon(-1);
                                selectedHexagonId = -1;
                            } else {
                                // if touched hexagon is not already selected, select/highlight it
                                boardSurfaceView.getGrid().setHighlightedHexagon(dataHexagon.getHexagonId());
                                selectedHexagonId = dataHexagon.getHexagonId();
                            }

                            boardSurfaceView.getGrid().getHighlightedIntersections().clear();
                            selectedIntersections.clear();
                            boardSurfaceView.invalidate();

                        }
                    }
                    index++;
                }
            }

            // check if no hexagon or intersection was touched (aka. outside the island)
            if (!touchedHexagon && !touchedIntersection) {
                boardSurfaceView.getGrid().setHighlightedHexagon(-1);
                boardSurfaceView.getGrid().getHighlightedIntersections().clear();
                selectedIntersections.clear();
                boardSurfaceView.invalidate();
            }
        }
    }; // clickListener END

    /*---------------------------------------Validation Methods-------------------------------------------*/

    /**
     * @param intersectionA First intersection of the road.
     * @param intersectionB Second intersection of the road. (order does not matter)
     * @return If success.
     */
    private boolean tryBuildRoad (int intersectionA, int intersectionB) {
        Log.d(TAG, "tryBuildRoad() called with: intersectionA = [" + intersectionA + "], intersectionB = [" + intersectionB + "]");

        if (!state.isSetupPhase()) {
            if (state.getPlayerList().get(state.getCurrentPlayerId()).removeResourceBundle(Road.resourceCost)) {
                messageTextView.setText("Not enough resources to build a road.");
            }
        }

        // check if user given intersections are valid
        if (state.getBoard().validRoadPlacement(state.getCurrentPlayerId(), state.isSetupPhase(), intersectionA, intersectionB)) {
            Log.i(TAG, "tryBuildRoad: Valid road placement received.");

            if (state.isSetupPhase()) { // todo remove
                // add just enough resources so player can build a road
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(0, 1); // give 1 brick
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(2, 1); // give 1 lumber
            }
            // send build settlement action to the game
            Log.e(TAG, "tryBuildRoad: Sending a CatanBuildRoadAction to the game.");
            game.sendAction(new CatanBuildRoadAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersectionA, intersectionB));

            boardSurfaceView.getGrid().clearHighLightedIntersections();
            selectedIntersections.clear(); // clear the selected intersections

            this.buildingsBuiltOnThisTurn.add(0);
            // return true
            Log.d(TAG, "tryBuildRoad() returned: " + true);
            return true;
        } else {
            messageTextView.setText("Invalid road location.");
            Log.e(TAG, "tryBuildSettlement: Returning false.");
            Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
            roadIntersectionBEditText.startAnimation(shake);
            messageTextView.startAnimation(shake);
            return false;
        }
    }

    /**
     * @param intersection1 IntersectionDrawable at which the player is trying to build a settlement upon.
     * @return If the building location chosen is valid, and if the action was carried out.
     */
    private boolean tryBuildSettlement (int intersection1) {

        Log.d(TAG, "tryBuildSettlement() called with: intersection1 = [" + intersection1 + "]");

        if (state.getBoard().validBuildingLocation(state.getCurrentPlayerId(), state.isSetupPhase(), intersection1)) {
            Log.i(TAG, "onClick: building location is valid. Sending a BuildSettlementAction to the game.");

            if (state.isSetupPhase()) {
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(0, 1); // give 1 brick
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(1, 1); // give 1 lumber
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(2, 1); // give 1 brick
                state.getPlayerList().get(state.getCurrentPlayerId()).addResourceCard(4, 1); // give 1 brick
            }

            // send build settlement action to the game
            Log.e(TAG, "tryBuildSettlement: Sending a CatanBuildSettlementAction to the game.");
            game.sendAction(new CatanBuildSettlementAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersection1));

            this.buildingsBuiltOnThisTurn.add(1);

            Log.d(TAG, "tryBuildSettlement() returned: " + true);

            return true;
        } else {
            messageTextView.setText("Invalid settlement location.");
            Log.e(TAG, "tryBuildSettlement: Returning false.");
            Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
            singleIntersectionInputEditText.startAnimation(shake);
            messageTextView.startAnimation(shake);
            return false;
        }
    }

    /**
     * @param intersection Intersection player is attempting to build a city at.
     * @return If a city was built at the intersection.
     */
    private boolean tryBuildCity (int intersection) {
        Log.d(TAG, "tryBuildCity() called with: intersection = [" + intersection + "]");

        if (state.isSetupPhase()) {
            Log.i(TAG, "tryBuildCity: Cannot built city during setup phase. Returning false.");
            return false;
        }

        if (state.getBoard().validCityLocation(state.getCurrentPlayerId(), intersection)) {
            Log.i(TAG, "onClick: building location is valid. Sending a BuildCityAction to the game.");
            this.buildingsBuiltOnThisTurn.add(2);

            game.sendAction(new CatanBuildCityAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersection));
        }
        return true;
    }

    private boolean tryTradeWithPort (int resourceGiving, int resourceReceiving) {

        ArrayList<Port> ports = state.getBoard().getPortList();
        Port tradingWith = null;

        for (Port port : ports) {
            if (port.getIntersectionB() == selectedIntersections.get(0) || port.getIntersectionA() == selectedIntersections.get(0)) {
                tradingWith = port;
            }
        }

        if (tradeButton == null) {
            this.messageTextView.setText("Selected intersection does not have port access.");
            Log.d(TAG, "tryTradeWithPort() returned: " + false);
            return false;
        }

        if (tradingWith.getResourceId() != -1) {
            if (state.getPlayerList().get(state.getCurrentPlayerId()).removeResourceCard(tradingWith.getResourceId(), tradingWith.getTradeRatio())) {

            }
        } else {
            if (state.getPlayerList().get(state.getCurrentPlayerId()).removeResourceCard(resourceGiving, tradingWith.getTradeRatio())) {
                game.sendAction(new CatanTradeWithPortAction(this));
            } else {

            }
        }
        return true;
    }

    private boolean tryTradeWithBank () {

        return true;
    }

    /* ---------------------------------------- GUI Methods --------------------------------------*/

    /**
     *
     */
    private void updateTextViews () {

        // Check if the Game State is null. If it is return void.
        if (this.state == null) {
            Log.e(TAG, "updateTextViews: state is null. Returning void.");
            return;
        }

        if (state.getDice().getDiceValues()[0] == 1)
            diceImageLeft.setBackgroundResource(R.drawable.dice_1);
        else if (state.getDice().getDiceValues()[0] == 2)
            diceImageLeft.setBackgroundResource(R.drawable.dice_2);
        else if (state.getDice().getDiceValues()[0] == 3)
            diceImageLeft.setBackgroundResource(R.drawable.dice_3);
        else if (state.getDice().getDiceValues()[0] == 4)
            diceImageLeft.setBackgroundResource(R.drawable.dice_4);
        else if (state.getDice().getDiceValues()[0] == 5)
            diceImageLeft.setBackgroundResource(R.drawable.dice_5);
        else diceImageLeft.setBackgroundResource(R.drawable.dice_6);

        if (state.getDice().getDiceValues()[1] == 1)
            diceImageRight.setBackgroundResource(R.drawable.dice_1);
        else if (state.getDice().getDiceValues()[1] == 2)
            diceImageRight.setBackgroundResource(R.drawable.dice_2);
        else if (state.getDice().getDiceValues()[1] == 3)
            diceImageRight.setBackgroundResource(R.drawable.dice_3);
        else if (state.getDice().getDiceValues()[1] == 4)
            diceImageRight.setBackgroundResource(R.drawable.dice_4);
        else if (state.getDice().getDiceValues()[1] == 5)
            diceImageRight.setBackgroundResource(R.drawable.dice_5);
        else diceImageRight.setBackgroundResource(R.drawable.dice_6);

        if (this.state.getRobberPhase()) {
            this.messageTextView.setText("Robber phase.");

            // if it is the setup phase, grey out some buttons and make them un clickable
            this.buildRoadButton.setAlpha(0.5f);
            this.buildRoadButton.setClickable(false);
            this.buildSettlementButton.setAlpha(0.5f);
            this.buildSettlementButton.setClickable(false);
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
            this.sidebarOpenDevCardMenuButton.setAlpha(0.5f);
            this.sidebarOpenDevCardMenuButton.setClickable(false);
            this.tradeButton.setAlpha(0.5f);
            this.tradeButton.setClickable(false);
            this.endTurnButton.setAlpha(0.5f);
            this.endTurnButton.setClickable(false);
        }
        if (this.state.isSetupPhase()) { // IF SETUP PHASE

            this.messageTextView.setText("Setup phase."); // set info message

            // get settlement and road count for the current turn
            int settlements = Collections.frequency(this.buildingsBuiltOnThisTurn, 1);
            int roads = Collections.frequency(this.buildingsBuiltOnThisTurn, 0);

            if (settlements == 2 && roads == 2) {
                this.endTurnButton.setAlpha(1f);
                this.endTurnButton.setClickable(true);
                this.messageTextView.setText("Setup turn complete. Please end your turn.");
            } else {
                this.endTurnButton.setAlpha(0.5f);
                this.endTurnButton.setClickable(false);
                this.buildRoadButton.setAlpha(1f);
                this.buildRoadButton.setClickable(true);
                this.buildSettlementButton.setAlpha(1f);
                this.buildSettlementButton.setClickable(true);
            }

            if ((settlements == 2 && roads == 1) || (settlements == 1 && roads == 0)) {
                this.buildRoadButton.setAlpha(1f);
                this.buildRoadButton.setClickable(true);
            } else {
                this.buildRoadButton.setAlpha(0.5f);
                this.buildRoadButton.setClickable(false);
            }

            if ((settlements == 1 && roads == 0) || (settlements == 2 && roads == 1)) {
                this.buildSettlementButton.setAlpha(0.5f);
                this.buildSettlementButton.setClickable(false);
            }

            // if it is the setup phase, grey out some buttons and make them un clickable
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
            this.sidebarOpenDevCardMenuButton.setAlpha(0.5f);
            this.sidebarOpenDevCardMenuButton.setClickable(false);
            this.tradeButton.setAlpha(0.5f);
            this.tradeButton.setClickable(false);

            this.singleIntersectionCancelButton.setAlpha(0.5f);
            this.singleIntersectionCancelButton.setClickable(false);
            this.roadIntersectionCancelButton.setAlpha(0.5f);
            this.roadIntersectionCancelButton.setClickable(false);
            this.roadIntersectionAEditText.setAlpha(0.5f);
            this.roadIntersectionAEditText.setEnabled(false);

        } else if (!state.isActionPhase()) { // IF NOT THE ACTION PHASE AND NOT THE SETUP PHASE

            this.messageTextView.setText("Roll the dice.");

            // set the roll button only as available
            this.rollButton.setAlpha(1f);
            this.rollButton.setClickable(true);

            this.buildRoadButton.setAlpha(0.5f);
            this.buildRoadButton.setClickable(false);
            this.buildSettlementButton.setAlpha(0.5f);
            this.buildSettlementButton.setClickable(false);
            this.buildCityButton.setAlpha(0.5f);
            this.buildCityButton.setClickable(false);
            this.sidebarOpenDevCardMenuButton.setAlpha(0.5f);
            this.sidebarOpenDevCardMenuButton.setClickable(false);
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

        } else { // ACTION PHASE AND NOT SETUP PHASE

            this.messageTextView.setText("Action phase.");
            setAllButtonsToVisible();
        }

        if (this.debugMode) {
            setAllButtonsToVisible();
        }

        //                setAllButtonsToVisible(); // TODO REMOVE THIS IS ONLY FOR DEBUGGING

        /* ----- update resource value TextViews ----- */

        int[] resourceCards = this.state.getPlayerList().get(this.playerNum).getResourceCards();
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
                player0Name.setBackgroundColor(HexagonGrid.playerColors[0]);
                break;
            case 1:
                player1Name.setBackgroundColor(HexagonGrid.playerColors[1]);
                break;
            case 2:
                player2Name.setBackgroundColor(HexagonGrid.playerColors[2]);
                break;
            case 3:
                player3Name.setBackgroundColor(HexagonGrid.playerColors[3]);
                break;
        }

        /* ----- update misc. sidebar TextViews ----- */

        // human player score (sidebar menu)
        this.myScore.setText(String.valueOf(this.state.getPlayerVictoryPoints()[this.state.getCurrentPlayerId()]));

        // current turn indicator (sidebar menu)
        this.currentTurnIdTextView.setText(String.valueOf(state.getCurrentPlayerId()));

        /* -------- animations ----------- */

        if (this.state.getCurrentPlayerId() == 0) {
            this.playerNameSidebar = (TextView) blinkAnimation(this.playerNameSidebar, 250, 250);
        }
        //        this.player0Name.clearAnimation();
        // Animate an image view
        //        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //        imageView = (ImageView)Utils.blinkAnimation(imageView,250,20);

    } // updateTextViews END

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

    /**
     * callback method--our game has been chosen/re-chosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    public void setAsGui (GameMainActivity activity) {
        Log.d(TAG, "setAsGui() called with: activity = [" + activity + "]");

        myActivity = activity; // remember the activity
        activity.setContentView(R.layout.activity_main); // Load the layout resource for our GUI

        scoreBoardGroup = activity.findViewById(R.id.group_scoreboard); // todo move this somewhere meaningful

        messageTextView = activity.findViewById(R.id.textview_game_message);

        /* ---------- Surface View for drawing the graphics ----------- */

        this.boardSurfaceView = activity.findViewById(R.id.board); // boardSurfaceView board is the custom SurfaceView

        this.boardSurfaceView.setOnClickListener(clickListener);
        this.boardSurfaceView.setOnTouchListener(touchListener);

        /* ----------------------------------- SIDEBAR ------------------------------------------ */

        //dice roll images
        diceImageLeft = activity.findViewById(R.id.diceImageLeft);
        diceImageRight = activity.findViewById(R.id.diceImageRight);
        // building buttons
        buildRoadButton = activity.findViewById(R.id.sidebar_button_road);
        buildRoadButton.setOnClickListener(this);

        buildSettlementButton = activity.findViewById(R.id.sidebar_button_settlement);
        buildSettlementButton.setOnClickListener(this);

        buildCityButton = activity.findViewById(R.id.sidebar_button_city);
        buildCityButton.setOnClickListener(this);

        // action buttons
        sidebarOpenDevCardMenuButton = activity.findViewById(R.id.sidebar_button_devcards); // buy dev card
        sidebarOpenDevCardMenuButton.setOnClickListener(this);

        tradeButton = activity.findViewById(R.id.sidebar_button_trade); // trade
        tradeButton.setOnClickListener(this);

        /* ----------------------------------- Turn Buttons --------------------------------------*/
        /*--------------------Robber Buttons and Groups------------------------*/
        robberBrickPlus = activity.findViewById(R.id.robber_discard_brickAddImg);
        robberBrickMinus = activity.findViewById(R.id.robber_discard_brickMinusImg);
        robberLumberPlus = activity.findViewById(R.id.robber_discard_lumberAddImg);
        robberLumberMinus = activity.findViewById(R.id.robber_discard_lumberMinusImg);
        robberGrainPlus = activity.findViewById(R.id.robber_discard_grainAddImg);
        robberGrainMinus = activity.findViewById(R.id.robber_discard_grainMinusImg);
        robberOrePlus = activity.findViewById(R.id.robber_discard_oreAddImg);
        robberOreMinus = activity.findViewById(R.id.robber_discard_oreMinusImg);
        robberWoolPlus = activity.findViewById(R.id.robber_discard_woolAddImg);
        robberWoolMinus = activity.findViewById(R.id.robber_discard_woolMinusImg);

        robberDiscardGroup = activity.findViewById(R.id.robber_discard_group);

        robberBrickPlus.setOnClickListener(this);
        robberBrickMinus.setOnClickListener(this);
        robberLumberPlus.setOnClickListener(this);
        robberLumberMinus.setOnClickListener(this);
        robberGrainPlus.setOnClickListener(this);
        robberGrainMinus.setOnClickListener(this);
        robberOrePlus.setOnClickListener(this);
        robberOreMinus.setOnClickListener(this);
        robberWoolPlus.setOnClickListener(this);
        robberWoolMinus.setOnClickListener(this);

        //Trade Menu Background - Receive
        brickSelectionBoxReceive = activity.findViewById(R.id.brickSelectionBoxReceive);
        grainSelectionBoxReceive = activity.findViewById(R.id.grainSelectionBoxReceive);
        lumberSelectionBoxReceive = activity.findViewById(R.id.lumberSelectionBoxReceive);
        oreSelectionBoxReceive = activity.findViewById(R.id.oreSelectionBoxReceive);
        woolSelectionBoxReceive = activity.findViewById(R.id.woolSelectionBoxReceive);

        //Trade Menu Background - Give
        brickSelectionBoxGive = activity.findViewById(R.id.brickSelectionBoxGive);
        grainSelectionBoxGive = activity.findViewById(R.id.grainSelectiomBoxGive);
        lumberSelectionBoxGive = activity.findViewById(R.id.lumberSelectionBoxGive);
        oreSelectionBoxGive = activity.findViewById(R.id.oreSelectionBoxGive);
        woolSelectionBoxGive = activity.findViewById(R.id.woolSelectionBoxGive);

        //Trade Menu - Receive
        image_trade_menu_give_brick = activity.findViewById(R.id.image_trade_menu_give_brick);
        image_trade_menu_give_brick.setOnClickListener(this);

        image_trade_menu_give_grain = activity.findViewById(R.id.image_trade_menu_give_grain);
        image_trade_menu_give_grain.setOnClickListener(this);

        image_trade_menu_give_lumber = activity.findViewById(R.id.image_trade_menu_give_lumber);
        image_trade_menu_give_lumber.setOnClickListener(this);

        image_trade_menu_give_ore = activity.findViewById(R.id.image_trade_menu_give_ore);
        image_trade_menu_give_ore.setOnClickListener(this);

        image_trade_menu_give_wool = activity.findViewById(R.id.image_trade_menu_give_wool);
        image_trade_menu_give_wool.setOnClickListener(this);

        //Trade Menu - Give
        image_trade_menu_rec_brick = activity.findViewById(R.id.image_trade_menu_rec_brick);
        image_trade_menu_rec_brick.setOnClickListener(this);

        image_trade_menu_rec_grain = activity.findViewById(R.id.image_trade_menu_rec_grain);
        image_trade_menu_rec_grain.setOnClickListener(this);

        image_trade_menu_rec_lumber = activity.findViewById(R.id.image_trade_menu_rec_lumber);
        image_trade_menu_rec_lumber.setOnClickListener(this);

        image_trade_menu_rec_ore = activity.findViewById(R.id.image_trade_menu_rec_ore);
        image_trade_menu_rec_ore.setOnClickListener(this);

        image_trade_menu_rec_wool = activity.findViewById(R.id.image_trade_menu_rec_wool);
        image_trade_menu_rec_wool.setOnClickListener(this);

        //Confirm trade action
        button_trade_menu_confirm = activity.findViewById(R.id.button_trade_menu_confirm);
        button_trade_menu_confirm.setOnClickListener(this);

        button_trade_menu_cancel = activity.findViewById(R.id.button_trade_menu_cancel);
        button_trade_menu_cancel.setOnClickListener(this);

        // turn buttons
        rollButton = activity.findViewById(R.id.sidebar_button_roll);
        rollButton.setOnClickListener(this);

        endTurnButton = activity.findViewById(R.id.sidebar_button_endturn);
        endTurnButton.setOnClickListener(this);

        /* ---------- Sidebar resource values ---------- */

        this.oreValue = activity.findViewById(R.id.sidebar_value_ore);
        this.grainValue = activity.findViewById(R.id.sidebar_value_grain);
        this.lumberValue = activity.findViewById(R.id.sidebar_value_lumber);
        this.woolValue = activity.findViewById(R.id.sidebar_value_wool);
        this.brickValue = activity.findViewById(R.id.sidebar_value_brick);

        /* ---------- misc sidebar buttons and text views ---------- */

        this.sidebarMenuButton = activity.findViewById(R.id.sidebar_button_menu);
        this.sidebarMenuButton.setOnClickListener(this);
        this.buildingCosts = activity.findViewById(R.id.building_costs);

        this.sidebarScoreboardButton = activity.findViewById(R.id.sidebar_button_score);
        this.sidebarScoreboardButton.setOnClickListener(this);

        this.myScore = activity.findViewById(R.id.sidebar_heading_vp);
        this.currentTurnIdTextView = activity.findViewById(R.id.sidebar_heading_current_turn);
        this.playerNameSidebar = activity.findViewById(R.id.sidebar_heading_playername);

        /* ------------ DEV CARD SPINNER ----------------- */

        devCardList = activity.findViewById(R.id.development_Card_Spinner); // DEV CARD SPINNER

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity.getApplicationContext(), R.array.dev_Card, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        devCardList.setAdapter(adapter);
        devCardList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //TODO Implement the Listener

            }

            @Override
            public void onNothingSelected (AdapterView<?> parentView) {
                // your code here todo
            }
        });

        /* ---------- Scoreboard scores ---------- */

        this.player0Score = activity.findViewById(R.id.Player1_Score);
        this.player1Score = activity.findViewById(R.id.Player2_Score);
        this.player2Score = activity.findViewById(R.id.Player3_Score);
        this.player3Score = activity.findViewById(R.id.Player4_Score);

        /* ---------- Scoreboard names ---------- */

        this.player0Name = activity.findViewById(R.id.Player1_Name);
        this.player1Name = activity.findViewById(R.id.Player2_Name);
        this.player2Name = activity.findViewById(R.id.Player3_Name);
        this.player3Name = activity.findViewById(R.id.Player4_Name);

        /* -------------------------------------- MENUS ---------------------------------------- */

        /* ------------ Development Card Menu ------------- */

        developmentGroup = activity.findViewById(R.id.group_development_card_menu); // dev card menu GROUP

        useDevCard = activity.findViewById(R.id.use_Card); // use dev card
        useDevCard.setOnClickListener(this);

        buildDevCard = activity.findViewById(R.id.build_devCard); // build dev card
        buildDevCard.setOnClickListener(this);

        /* ---------------- Trade Menu -------------------- */

        tradeGroup = activity.findViewById(R.id.group_trade_menu); // trade menu GROUP

        // if we have state update the GUI based on the state
        if (this.state != null) {
            receiveInfo(state);
        }

        /*--------------------Robber Buttons and Groups------------------------*/

        robberBrickPlus = activity.findViewById(R.id.robber_discard_brickAddImg);
        robberBrickMinus = activity.findViewById(R.id.robber_discard_brickMinusImg);
        robberLumberPlus = activity.findViewById(R.id.robber_discard_lumberAddImg);
        robberLumberMinus = activity.findViewById(R.id.robber_discard_lumberMinusImg);
        robberGrainPlus = activity.findViewById(R.id.robber_discard_grainAddImg);
        robberGrainMinus = activity.findViewById(R.id.robber_discard_grainMinusImg);
        robberOrePlus = activity.findViewById(R.id.robber_discard_oreAddImg);
        robberOreMinus = activity.findViewById(R.id.robber_discard_oreMinusImg);
        robberWoolPlus = activity.findViewById(R.id.robber_discard_woolAddImg);
        robberWoolMinus = activity.findViewById(R.id.robber_discard_woolMinusImg);

        robberDiscardGroup = activity.findViewById(R.id.robber_discard_group);

        robberBrickPlus.setOnClickListener(this);
        robberBrickMinus.setOnClickListener(this);
        robberLumberPlus.setOnClickListener(this);
        robberLumberMinus.setOnClickListener(this);
        robberGrainPlus.setOnClickListener(this);
        robberGrainMinus.setOnClickListener(this);
        robberOrePlus.setOnClickListener(this);
        robberOreMinus.setOnClickListener(this);
        robberWoolPlus.setOnClickListener(this);
        robberWoolMinus.setOnClickListener(this);
    }// setAsGui() END

    /**
     *
     */
    private void drawGraphics () {
        Log.d(TAG, "drawGraphics() called");

        Canvas canvas = new Canvas();
        boardSurfaceView.createHexagons(this.state.getBoard());
        boardSurfaceView.createHexagons(this.state.getBoard()); // draw the board of hexagons and ports on the canvas

        int height = boardSurfaceView.getHeight();
        int width = boardSurfaceView.getWidth();

        Log.i(TAG, "drawGraphics: boardSurfaceView height: " + height + " width: " + width);

        this.boardSurfaceView.setGrid(new HexagonGrid(myActivity.getApplicationContext(), state.getBoard(), 80, 185, 175, 20, this.debugMode));
        this.boardSurfaceView.draw(canvas);

        boardSurfaceView.invalidate();
    } // drawGraphics END

    /**
     * @param message Game over message.
     */
    protected void gameIsOver (String message) {
        for (int i = 0; i < this.state.getPlayerVictoryPoints().length; i++) {
            if (this.state.getPlayerVictoryPoints()[i] > 9) {
                super.gameIsOver("Player " + i + " wins!");
            }
        }
    } // gameIsOver END

    /**
     *
     */
    protected void initAfterReady () {
        Log.e(TAG, "initAfterReady() called");
    }

    /**
     * Returns the GUI's top view object
     *
     * @return the top object in the GUI's view hierarchy
     */
    public View getTopView () {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /**
     * Toggles the visibility of a group.
     *
     * @param group Group to toggle visibility.
     */
    private void toggleGroupVisibility (Group group) {
        if (group.getVisibility() == View.GONE) group.setVisibility(View.VISIBLE);
        else group.setVisibility(View.GONE);
    }

    private void toggleViewVisibility (View view) {
        if (view.getVisibility() == View.GONE) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    private void setAllButtonsToVisible () {
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
        this.sidebarOpenDevCardMenuButton.setAlpha(1f);
        this.sidebarOpenDevCardMenuButton.setClickable(true);
        this.tradeButton.setAlpha(1f);
        this.tradeButton.setClickable(true);
        this.buildSettlementButton.setAlpha(1f);
        this.buildSettlementButton.setClickable(true);
        this.buildRoadButton.setAlpha(1f);
        this.buildRoadButton.setClickable(true);
        this.endTurnButton.setAlpha(1f);
        this.endTurnButton.setClickable(true);

        this.rollButton.setAlpha(1f);
        this.rollButton.setClickable(true);

        this.singleIntersectionCancelButton.setAlpha(1f);
        this.singleIntersectionCancelButton.setClickable(true);
        this.roadIntersectionCancelButton.setAlpha(1f);
        this.roadIntersectionCancelButton.setClickable(true);
        this.roadIntersectionAEditText.setAlpha(1f);
        this.roadIntersectionAEditText.setEnabled(true);
    }

    /**
     * Make a View Blink for a desired duration
     *
     * @param view View to be animated.
     * @param duration Duration of the animation.
     * @param offset Start offset.
     * @return returns The View with animation properties on it.
     */
    private static View blinkAnimation (View view, int duration, int offset) {

        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(duration);
        anim.setStartOffset(offset);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(3);
        view.startAnimation(anim);
        return view;
    }// blinkAnimation END

    /**
     * @param v View to make shake.
     */
    private void shake (View v) {
        Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
        v.startAnimation(shake);
        v.startAnimation(shake);
    }

}// class CatanHumanPlayer END

