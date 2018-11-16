package edu.up.cs.androidcatan.catan;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.up.cs.androidcatan.R;
import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuyDevCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberDiscardAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberMoveAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberStealAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithBankAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithCustomPortAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithPortAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseKnightCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseMonopolyCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseRoadBuildingCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseVictoryPointCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseYearOfPlentyCardAction;
import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.catan.gamestate.Hexagon;
import edu.up.cs.androidcatan.catan.gamestate.Port;
import edu.up.cs.androidcatan.catan.gamestate.buildings.City;
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
    private boolean debugMode = false;
    private boolean isMenuOpen = false;

    private int selectedHexagonId = -1;
    private ArrayList<Integer> selectedIntersections = new ArrayList<>();

    // resourceCard index values: 0 = Brick, 1 = Lumber, 2 = Grain, 3 = Ore, 4 = Wool
    private int[] robberDiscardedResources = new int[]{0, 0, 0, 0, 0};  //How many resources the player would like to discard
    private ArrayList<Integer> resourceIdsToDiscard = new ArrayList<>();
    private TextView messageTextView = (TextView) null;

    private ArrayList<String> devCards = new ArrayList<>();
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
    private TextView robberDiscardMessage = (TextView) null;
    private Button robberConfirmDiscard = (Button) null;

    private TextView robberBrickAmount = (TextView) null;
    private TextView robberLumberAmount = (TextView) null;
    private TextView robberGrainAmount = (TextView) null;
    private TextView robberOreAmount = (TextView) null;
    private TextView robberWoolAmount = (TextView) null;

    private Button robberConfirmHex = (Button) null;
    private TextView robberHexMessage = (TextView) null;

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

    //Monopoly Menu - Resource Icons
    private ImageView monopolyBrickIcon = (ImageView) null;
    private ImageView monopolyGrainIcon = (ImageView) null;
    private ImageView monopolyLumberIcon = (ImageView) null;
    private ImageView monopolyOreIcon = (ImageView) null;
    private ImageView monopolyWoolIcon = (ImageView) null;

    //Monopoly Menu - SelectionBoxes
    private ImageView monopolyBrickSelectionBox = (ImageView) null;
    private ImageView monopolyGrainSelcionBox = (ImageView) null;
    private ImageView monopolyLumberSelectionBox = (ImageView) null;
    private ImageView monopolyOreSelectionBox = (ImageView) null;
    private ImageView monopolyWoolSelectionBox = (ImageView) null;

    //Monopoly Menu - Confrim
    private TextView monopolyConfirm = (TextView) null;
    private int monopolyResourceChoice = -1;

    //Dev Card Menu
    private TextView devcard_text_name = (TextView) null;
    private TextView devcard_text_info = (TextView) null;
    private int devCardId = 0;

    //Other Groups
    private Group scoreBoardGroup = (Group) null;
    private Group developmentGroup = (Group) null;
    private Group tradeGroup = (Group) null;
    private Group robberDiscardGroup = (Group) null;
    private Group robberChooseHexGroup = (Group) null;
    private Group monopolyPickGroup = (Group) null;

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

        /* ---------------------------- Building Sidebar Button OnClick() Handlers --------------------- */
        messageTextView.setTextColor(Color.WHITE);
        // Road button on the sidebar.
        if (button.getId() == R.id.sidebar_button_road) {
            if (selectedIntersections.size() != 2) {
                messageTextView.setText(R.string.need_2_ints_for_road);
                return;
            } else {
                if (tryBuildRoad(selectedIntersections.get(0), selectedIntersections.get(1))) {
                    messageTextView.setText(R.string.build_a_road);
                    return;
                } else {
                    return;
                }
            }
        }

        // Settlement button on the sidebar.
        if (button.getId() == R.id.sidebar_button_settlement) {
            Log.d(TAG, "onClick: sidebar_button_settlement listener");
            if (selectedIntersections.size() != 1) {
                messageTextView.setText(R.string.one_int_for_set);
                shake(messageTextView);
            } else {
                if (tryBuildSettlement(selectedIntersections.get(0))) {
                    messageTextView.setText(R.string.built_settlement);
                } else {
                    // tell user location is invalid
                    messageTextView.setText(R.string.invalid_set_loc);
                }
            }
            return;
        }

        // City button on the sidebar.
        if (button.getId() == R.id.sidebar_button_city) {
            if (selectedIntersections.size() != 1) {
                messageTextView.setText(R.string.select_one_int_for_city);
            } else {
                Log.e(TAG, "onClick: build city selected intersection: " + selectedIntersections.get(0));
                if (tryBuildCity(selectedIntersections.get(0))) {
                    messageTextView.setText(R.string.built_city);
                } else {
                    messageTextView.setText(R.string.invalid_city_loc);
                }
            }
            return;
        }

        /* ----------------------------------- Turn Actions ------------------------------------- */

        // Roll button on the sidebar.
        if (button.getId() == R.id.sidebar_button_roll) {
            Log.d(TAG, "onClick: Roll button clicked.");
            game.sendAction(new CatanRollDiceAction(this));

            if (state.getCurrentDiceSum() == 7) {
                Log.d(TAG, "onClick: Robber has been activated");
                Log.d(TAG, "onClick: Making Robber Visible");
                state.setRobberPhase(true);
            }
            return;
        }

        // End turn button on the sidebar.
        if (button.getId() == R.id.sidebar_button_endturn) {
            Log.d(TAG, "onClick: End Turn button pressed.");

            game.sendAction(new CatanEndTurnAction(this));
            this.buildingsBuiltOnThisTurn = new ArrayList<>(); // reset array list
            return;
        }

        /* -------------------------- Scoreboard and Menu Buttons Handlers ---------------------- */

        // Menu button on the sidebar.
        if (button.getId() == R.id.sidebar_button_menu) {
            //            this.boardSurfaceView.getGrid().toggleDebugMode();
            this.boardSurfaceView.invalidate();

            //            this.debugMode = !this.debugMode; // toggle debug mode

            toggleViewVisibility(this.buildingCosts); // toggle help image

            //            setAllButtonsToVisible();
            //            Log.e(TAG, "onClick: toggled debug mode");
            Log.d(TAG, state.toString());
            return;
        }
        // Score button on the sidebar.
        if (button.getId() == R.id.sidebar_button_score) {
            toggleGroupVisibilityAllowTapping(scoreBoardGroup);
        }


        /*--------------------------------- Robber onClick --------------------------------*/

        if (button.getId() == R.id.robber_choosehex_confirm) {
            Log.i(TAG, "onClick: Checking if good Hex to place Robber on");
            if (state.getHasMovedRobber()) {
                if (selectedIntersections.size() != 1) {
                    robberHexMessage.setText("Please select only one intersection.");
                    return;
                }

                if (!state.getBoard().hasBuilding(selectedIntersections.get(0))) {
                    robberHexMessage.setText(R.string.select_int_w_bldg_robber);
                    messageTextView.setText(R.string.select_int_w_bldg_robber);
                    return;
                }

                if (state.getBoard().getBuildingAtIntersection(selectedIntersections.get(0)).getOwnerId() == playerNum) {
                    robberHexMessage.setText("Please select an intersection not owned by you.");
                    messageTextView.setText(R.string.select_int_not_owned_by_you);
                    return;
                }

                int stealId = state.getBoard().getBuildingAtIntersection(selectedIntersections.get(0)).getOwnerId();
                robberChooseHexGroup.setVisibility(View.GONE);
                game.sendAction(new CatanRobberStealAction(this, playerNum, stealId));
                return;
            }

            if (!tryMoveRobber(selectedHexagonId)) {
                Log.e(TAG, "onClick: Error, Not valid Hexagon chosen");
                robberHexMessage.setText(R.string.invalid_tile);
                shake(robberHexMessage);
                messageTextView.setText(R.string.invalid_tile);
                shake(messageTextView);
                return;
            }

            Log.i(TAG, "onClick: Successful Hex chosen for Robber, now making group visible");
            robberChooseHexGroup.setVisibility(View.VISIBLE);
            robberHexMessage.setText("Please selected an intersection with a building adjacent to the robber");
            game.sendAction(new CatanRobberMoveAction(this, playerNum, selectedHexagonId));
            return;
        }

        if (button.getId() == R.id.robber_discard_confirm) {
            if (state.validDiscard(this.playerNum, this.robberDiscardedResources)) {
                robberDiscardMessage.setText("Discarding..");
                if (state.getCurrentPlayerId() == playerNum) {
                    robberChooseHexGroup.setVisibility(View.VISIBLE);
                }
                robberDiscardGroup.setVisibility(View.GONE);

                // todo

                robberBrickAmount.setText(R.string.zero);
                robberLumberAmount.setText(R.string.zero);
                robberGrainAmount.setText(R.string.zero);
                robberOreAmount.setText(R.string.zero);
                robberWoolAmount.setText(R.string.zero);

                // putting the array into the arraylist todo fix lol this is not good
                for (int i = 0; i < robberDiscardedResources.length; i++) {
                    for (int j = 0; j < robberDiscardedResources[i]; j++) {
                        resourceIdsToDiscard.add(i);
                    }
                }

                this.robberDiscardedResources = state.getRobberDiscardedResource();
                game.sendAction(new CatanRobberDiscardAction(this, playerNum, resourceIdsToDiscard));
                return;
            }

            String message = "" + state.getPlayerList().get(this.playerNum).getTotalResourceCardCount() / 2 + " resources are needed.";
            robberDiscardMessage.setText(message);
            messageTextView.setText(message);
            shake(messageTextView);
            return;
        }

        int robberDiscardAddButtonIds[] = {R.id.robber_discard_brickAddImg, R.id.robber_discard_grainAddImg, R.id.robber_discard_lumberAddImg, R.id.robber_discard_oreAddImg, R.id.robber_discard_woolAddImg};
        int robberDiscardMinusButtonIds[] = {R.id.robber_discard_brickMinusImg, R.id.robber_discard_grainMinusImg, R.id.robber_discard_lumberMinusImg, R.id.robber_discard_oreMinusImg, R.id.robber_discard_woolMinusImg};
        TextView robberAmounts[] = {robberBrickAmount, robberGrainAmount, robberLumberAmount, robberOreAmount, robberWoolAmount};

        for (int i = 0; i < robberDiscardAddButtonIds.length; i++) {
            if (button.getId() == robberDiscardAddButtonIds[i]) {
                robberDiscardedResources[i]++;
            } else if (button.getId() == robberDiscardMinusButtonIds[i]) {
                robberDiscardedResources[i]--;
            }
        }

        for (int i = 0; i < robberAmounts.length; i++) {
            robberAmounts[i].setText("" + robberDiscardedResources[i]);
        }

        // todo i think the code i added above does the same as this please verify @todo - alex and niraj
        //        if (button.getId() == R.id.robber_discard_brickAddImg) {
        //            robberDiscardedResources[0] += 1;
        //            robberBrickAmount.setText("" + robberDiscardedResources[0]);
        //        }
        //        if (button.getId() == R.id.robber_discard_brickMinusImg) {
        //            robberDiscardedResources[0] -= 1;
        //            robberBrickAmount.setText("" + robberDiscardedResources[0]);
        //        }
        //        if (button.getId() == R.id.robber_discard_grainAddImg) {
        //            robberDiscardedResources[1] += 1;
        //            robberGrainAmount.setText("" + robberDiscardedResources[1]);
        //        }
        //        if (button.getId() == R.id.robber_discard_grainMinusImg) {
        //            robberDiscardedResources[1] -= 1;
        //            robberGrainAmount.setText("" + robberDiscardedResources[1]);
        //        }
        //        if (button.getId() == R.id.robber_discard_lumberAddImg) {
        //            robberDiscardedResources[2] += 1;
        //            robberLumberAmount.setText("" + robberDiscardedResources[2]);
        //        }
        //        if (button.getId() == R.id.robber_discard_lumberMinusImg) {
        //            robberDiscardedResources[2] -= 1;
        //            robberLumberAmount.setText("" + robberDiscardedResources[2]);
        //        }
        //        if (button.getId() == R.id.robber_discard_oreAddImg) {
        //            robberDiscardedResources[3] += 1;
        //            robberOreAmount.setText("" + robberDiscardedResources[3]);
        //        }
        //        if (button.getId() == R.id.robber_discard_oreMinusImg) {
        //            robberDiscardedResources[3] -= 1;
        //            robberOreAmount.setText("" + robberDiscardedResources[3]);
        //        }
        //        if (button.getId() == R.id.robber_discard_woolAddImg) {
        //            robberDiscardedResources[4] += 1;
        //            robberWoolAmount.setText("" + robberDiscardedResources[4]);
        //        }
        //        if (button.getId() == R.id.robber_discard_woolMinusImg) {
        //            robberDiscardedResources[4] -= 1;
        //            robberWoolAmount.setText("" + robberDiscardedResources[4]);
        //        }

        /*-------------------------End of Robber----------------------------------------*/

        /* ---------- Trade action buttons ---------- */

        //TODO Need functionality for both Port, Custom Port and Bank
        if (button.getId() == R.id.sidebar_button_trade) {

            if (selectedIntersections.size() == 0) {
                //                if (tryTradeWithBank()) {
                //
                //                }
            } else if (selectedIntersections.size() == 1) {

            }

            // toggle menu vis.
            toggleGroupVisibility(tradeGroup);
            return;
        }



        /* -------------------- Development Card Button OnClick() Handlers ---------------------- */

        // Development button located on the sidebar. Should only show/hide dev card menu.
        if (button.getId() == R.id.sidebar_button_devcards) {

            //            state.getCurrentPlayer().addResourceCard(1, 2);
            //            state.getCurrentPlayer().addResourceCard(3, 2);
            //            state.getCurrentPlayer().addResourceCard(4, 2);

            toggleGroupVisibility(developmentGroup); // toggle menu vis.
            return;
        }

        // Use development card button on the dev card menu.
        if (button.getId() == R.id.use_Card) {
            Log.d(TAG, "onClick: Player tapped the use card button.");

            String devCardNames[] = {"Knight Development", "Victory Points Development", "Year of Plenty", "Monopoly", "Road Development"};

            int developmentCardId = -1;
            for (int i = 0; i < devCardNames.length; i++) {
                if (devCardList.getSelectedItem().equals(devCardNames[i])) {
                    developmentCardId = i;
                }
            }

            Log.i(TAG, "onClick: Player is using dev card id: " + developmentCardId + " (" + devCardNames[developmentCardId] + ")");

            if (!state.getCurrentPlayer().getDevelopmentCards().contains(developmentCardId)) {
                Log.e(TAG, "onClick: player does not have development card. Cannot use.");
                messageTextView.setText("Don't have card");
                return;
            } else {

                state.getCurrentPlayer().removeDevCard(developmentCardId);
                Log.d(TAG, "onClick: Development Card was removed from hand");

                if (developmentCardId == 0) {
                    game.sendAction(new CatanUseKnightCardAction(this));
                    return;
                }

                if (developmentCardId == 1) {
                    game.sendAction(new CatanUseVictoryPointCardAction(this));
                    return;
                }

                ImageView monopolySelectionBox[] = {monopolyBrickSelectionBox, monopolyGrainSelcionBox, monopolyLumberSelectionBox, monopolyOreSelectionBox, monopolyWoolSelectionBox};

                //year of plenty
                if (developmentCardId == 2) {

                    game.sendAction(new CatanUseYearOfPlentyCardAction(this, 1)); // todo
                    return;
                }

                //
                if (developmentCardId == 3) {
                    toggleGroupVisibility(monopolyPickGroup);
                    for (ImageView imageView : monopolySelectionBox) {
                        imageView.setBackgroundColor(Color.TRANSPARENT);
                    }

                    int monopolyResourceIds[] = {R.id.pickResMenu_brickIcon, R.id.pickResMenu_grainIcon, R.id.pickResMenu_lumberIcon, R.id.pickResMenu_oreIcon, R.id.pickResMenu_woolIcon};

                    for (int i = 0; i < 5; i++) {
                        if (button.getId() == monopolyResourceIds[i]) {
                            monopolyResourceChoice = i;
                            break;
                        }
                    }

                    if (monopolyResourceChoice != -1) {
                        monopolySelectionBox[monopolyResourceChoice].setBackgroundColor(Color.argb(255, 255, 255, 187));
                    }

                    if (button.getId() == R.id.pickResMenu_ConfirmButton) {
                        Log.d(TAG, "onClick: Player tried to confirm a monopoly card");
                        game.sendAction(new CatanUseMonopolyCardAction(this, 1)); // todo
                        toggleGroupVisibilityAllowTapping(monopolyPickGroup);
                        return;
                    }
                }

                if (developmentCardId == 4) {
                    game.sendAction(new CatanUseRoadBuildingCardAction(this));
                    this.rollButton.setAlpha(0.5f);
                    this.rollButton.setClickable(false);
                    this.buildRoadButton.setAlpha(1f);
                    this.buildRoadButton.setClickable(true);
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
                    this.sidebarScoreboardButton.setAlpha(0.5f);
                    this.sidebarScoreboardButton.setClickable(false);
                    this.sidebarMenuButton.setAlpha(0.5f);
                    this.sidebarMenuButton.setClickable(false);

                    return;
                }
            }
        }

        // Build development card button in the development card menu.
        if (button.getId() == R.id.build_devCard) {
            // check if player has resources
            if (state.getCurrentPlayer().hasResourceBundle(DevelopmentCard.resourceCost)) {
                // send action to the game
                game.sendAction(new CatanBuyDevCardAction(this));
                messageTextView.setText(R.string.you_built_a_dev);
            } else {
                messageTextView.setText(R.string.not_enough_for_dev_card);
                shake(messageTextView);
            }
            return;
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

        // if the user selects resource to receive -> highlight the selection
        if (tradeReceiveSelection != -1) {
            selectionBoxReceive[tradeReceiveSelection].setBackgroundColor(Color.argb(255, 255, 255, 187));
        }
        // if the user selects resource to give -> highlight the selection
        if (tradeGiveSelection != -1) {
            selectionBoxGive[tradeGiveSelection].setBackgroundColor(Color.argb(255, 255, 255, 187));
        }

        // confirm trade logic
        if (button.getId() == R.id.button_trade_menu_confirm) {
            Log.d(TAG, "onClick: Player tried to confirm trade");
            Log.e(TAG, "onClick: selected intersections: " + this.selectedIntersections);
            //checks to see if the user has any intersections selected.
            if (selectedIntersections.size() == 1) {
                if (tryTradeWithPort(tradeGiveSelection, tradeReceiveSelection)) {
                    Log.d(TAG, "onClick: traded with port");
                } else {
                    Log.e(TAG, "onClick: trade with port failed");
                }
            } else if (selectedIntersections.size() == 0) {
                if (tryTradeWithBank(tradeGiveSelection, tradeReceiveSelection)) {
                    Log.d(TAG, "onClick: traded with bank");
                    selectedIntersections.clear();
                    toggleGroupVisibility(tradeGroup);
                } else {
                    Log.e(TAG, "onClick: trade with bank failed");
                }
            } else if (selectedIntersections.size() > 1) {
                Log.e(TAG, "onClick: user has selected too many intersections");
                messageTextView.setText("Please select less than 2 intersections.");
            } else {
                Log.e(TAG, "onClick: logic error, because selectedIntersections.size() is negative or null");
            }
        }

        if (button.getId() == R.id.button_trade_menu_cancel) {
            toggleGroupVisibility(tradeGroup);
        }



        /*----------------Monopoly-----------------------------*/
        //ImageView monopolySelectionBox[] = {monopolyBrickSelectionBox, monopolyGrainSelcionBox, monopolyLumberSelectionBox, monopolyOreSelectionBox, monopolyWoolSelectionBox};

        //        for(ImageView imageView : monopolySelectionBox)
        //        {
        //            imageView.setBackgroundColor(Color.TRANSPARENT);
        //        }
        //
        //        int monopolyResourceIds[] = {R.id.pickResMenu_brickIcon, R.id.pickResMenu_grainIcon,R.id.pickResMenu_lumberIcon, R.id.pickResMenu_oreIcon, R.id.pickResMenu_woolIcon};
        //
        //        for(int i = 0; i < 5; i++)
        //        {
        //            if(button.getId() == monopolyResourceIds[i])
        //            {
        //                monopolyResourceChoice = i;
        //                break;
        //            }
        //        }
        //
        //        if(monopolyResourceChoice != -1)
        //        {
        //            monopolySelectionBox[monopolyResourceChoice].setBackgroundColor(Color.argb(255, 255, 255, 187));
        //        }
        //
        //        if(button.getId() == R.id.pickResMenu_ConfirmButton)
        //        {
        //            Log.d(TAG, "onClick: Player tried to confirm a monopoly card");
        //
        //        }

    } // onClick END

    /* ----------------------- BoardSurfaceView Touch Listeners --------------------------------- */

    // the purpose of the touch listener is just to store the touch X,Y coordinates
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch (View v, MotionEvent event) {
            if (isMenuOpen) {
                return false;
            }
            // save the X,Y coordinates
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                lastTouchDownXY[0] = event.getX();
                lastTouchDownXY[1] = event.getY();
            }

            // let the touch event pass on to whoever needs it
            return false;
        }
    }; // touchListener END

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick (View v) {
            if (isMenuOpen) {
                return;
            }
            // retrieve the stored coordinates
            float x = lastTouchDownXY[0];
            float y = lastTouchDownXY[1];

            boolean touchedIntersection = false;
            boolean touchedHexagon = false;

            HexagonGrid grid = boardSurfaceView.getGrid();

            // use the coordinates for whatever
            Log.i("TAG", "onLongClick: x = " + x + ", y = " + y);

            for (int i = 0; i < grid.getIntersections().length; i++) {
                int xPos = grid.getIntersections()[i].getXPos();
                int yPos = grid.getIntersections()[i].getYPos();

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

        // check if user given intersections are valid
        if (state.getBoard().validRoadPlacement(state.getCurrentPlayerId(), state.isSetupPhase(), intersectionA, intersectionB)) {
            Log.i(TAG, "tryBuildRoad: Valid road placement received.");
        } else {
            messageTextView.setText(R.string.invalid_road_placement);
            Log.d(TAG, "tryBuildRoad() returned: " + false);
            return false;
        }

        if (state.isSetupPhase()) {
            boardSurfaceView.getGrid().clearHighLightedIntersections();
            selectedIntersections.clear(); // clear the selected intersections
            this.buildingsBuiltOnThisTurn.add(0);
            game.sendAction(new CatanBuildRoadAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersectionA, intersectionB));
            messageTextView.setText(R.string.road_built);
            return true;
        }

        // if it is not the setup phase, then check if it is the action phase
        if (!state.isActionPhase()) {
            Log.i(TAG, "tryBuildRoad: Player cannot build road. Not action phase.");
            messageTextView.setText(R.string.roll_the_dice);
            shake(messageTextView);
            return false;
        }

        // if it is not the setup phase check if the player has enough resources to build a road
        if (state.getPlayerList().get(state.getCurrentPlayerId()).hasResourceBundle(Road.resourceCost)) {
            // send build settlement action to the game
            Log.d(TAG, "tryBuildRoad: Sending a CatanBuildRoadAction to the game.");
            game.sendAction(new CatanBuildRoadAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersectionA, intersectionB));
            // clear selected intersections
            boardSurfaceView.getGrid().clearHighLightedIntersections();
            selectedIntersections.clear(); // clear the selected intersections
            this.buildingsBuiltOnThisTurn.add(0);
            Log.d(TAG, "tryBuildRoad() returned: " + true);
            return true;
        }

        Log.i(TAG, "tryBuildRoad: player does not have enough resources to build a road.");
        messageTextView.setText(R.string.not_enough_for_road);
        Log.d(TAG, "tryBuildRoad() returned: " + false);
        return false;
    }

    /**
     * @param intersection1 IntersectionDrawable at which the player is trying to build a settlement upon.
     * @return If the building location chosen is valid, and if the action was carried out.
     */
    private boolean tryBuildSettlement (int intersection1) {

        Log.d(TAG, "tryBuildSettlement() called with: intersection1 = [" + intersection1 + "]");

        if (state.getBoard().validBuildingLocation(state.getCurrentPlayerId(), state.isSetupPhase(), intersection1)) {
            Log.i(TAG, "onClick: building location is valid. Sending a BuildSettlementAction to the game.");

            // send build settlement action to the game
            Log.e(TAG, "tryBuildSettlement: Sending a CatanBuildSettlementAction to the game.");
            game.sendAction(new CatanBuildSettlementAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersection1));

            this.buildingsBuiltOnThisTurn.add(1);

            Log.d(TAG, "tryBuildSettlement() returned: " + true);

            return true;
        } else {
            messageTextView.setText(R.string.invalid_set_loc);
            Log.e(TAG, "tryBuildSettlement: Returning false.");
            shake(messageTextView);
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

        if (!state.getCurrentPlayer().hasResourceBundle(City.resourceCost)) {
            messageTextView.setText(R.string.not_enough_for_city);
            shake(messageTextView);
            Log.d(TAG, "tryBuildCity() returned: " + false);
            return false;
        }

        if (state.getBoard().validCityLocation(state.getCurrentPlayerId(), intersection)) {
            Log.d(TAG, "onClick: building location is valid. Sending a BuildCityAction to the game.");
            this.buildingsBuiltOnThisTurn.add(2);

            game.sendAction(new CatanBuildCityAction(this, state.isSetupPhase(), state.getCurrentPlayerId(), intersection));
        }
        return true;
    }

    /**
     * @param hexId Hexagon to try to move the robber to.
     * @return Success.
     */
    private boolean tryMoveRobber (int hexId) {
        // make sure they have a hexagon selected
        if (hexId == -1) {
            messageTextView.setText(R.string.hex_for_robber);
            shake(messageTextView);
            return false;
        }
        // make sure they move the robber to a new hexagon
        if (hexId == state.getBoard().getRobber().getHexagonId()) {
            messageTextView.setText(R.string.new_hex);
            shake(messageTextView);
            return false;
        }
        // get a list of adjacent intersections to the hexagon
        ArrayList<Integer> intersections = state.getBoard().getHexToIntIdMap().get(hexId);
        for (Integer intersection : intersections) {
            if (state.getBoard().getBuildings()[intersection] != null) {
                if (state.getBoard().getBuildings()[intersection].getOwnerId() != playerNum) {
                    messageTextView.setText(R.string.robber_moved);
                    return true;
                }
            }
        }
        messageTextView.setText(R.string.opp_bldg);
        shake(messageTextView);
        return false;
    }

    /**
     * @param resourceGiving Resource the player wants to give in the trade.
     * @param resourceReceiving Resource the player wants to receive in the trade.
     * @return Trade success.
     */
    private boolean tryTradeWithPort (int resourceGiving, int resourceReceiving) {
        if (resourceGiving < 0) {
            messageTextView.setText(R.string.give_res_not_sel);
            shake(messageTextView);
            return false;
        }
        if (resourceReceiving < 0) {
            messageTextView.setText(R.string.rec_res_not_sel);
            shake(messageTextView);
            return false;
        }
        ArrayList<Port> ports = state.getBoard().getPortList();
        Port tradingWith = null;
        for (Port port : ports) {
            if (port.getIntersectionB() == selectedIntersections.get(0) || port.getIntersectionA() == selectedIntersections.get(0))
                tradingWith = port;
        }
        // make sure selected intersection has port access
        if (tradingWith == null) {
            this.messageTextView.setText(R.string.no_port_access);
            Log.d(TAG, "tryTradeWithPort() returned: " + false);
            return false;
        }
        // make sure a building is selected
        if (!state.getBoard().hasBuilding(selectedIntersections.get(0))) {
            return false;
        }
        // check if player owns selected building
        if (state.getBoard().getBuildings()[selectedIntersections.get(0)].getOwnerId() != state.getCurrentPlayerId()) {
            return false;
        }
        // if trading with a normal port
        if (tradingWith.getResourceId() != -1) {
            // check if player has enough resources
            if (state.getPlayerList().get(state.getCurrentPlayerId()).checkResourceCard(tradingWith.getResourceId(), tradingWith.getTradeRatio())) {
                // send action to the game
                game.sendAction(new CatanTradeWithPortAction(this, tradingWith, resourceReceiving));
                messageTextView.setText(R.string.traded_with_port);
                toggleGroupVisibilityAllowTapping(tradeGroup);
                return true;
            } else {
                messageTextView.setText(R.string.not_enough_for_trade);
                shake(messageTextView);
                return false;
            }
        } else { // if the player is trading with a mystery port
            // check if the player has enough resources
            if (state.getPlayerList().get(state.getCurrentPlayerId()).checkResourceCard(resourceGiving, tradingWith.getTradeRatio())) {
                // send action to the game
                game.sendAction(new CatanTradeWithCustomPortAction(this, resourceGiving, resourceReceiving));
                messageTextView.setText(R.string.traded_with_port);
                toggleGroupVisibilityAllowTapping(tradeGroup);
                return true;
            } else {
                messageTextView.setText(R.string.not_enough_for_trade);
                shake(messageTextView);
                return false;
            }
        }
    }

    /**
     * @param resourceGiving Resource to give in the trade with the bank.
     * @param resourceReceiving Resource to receive in the trade with the bank.
     * @return Trade success.
     */
    private boolean tryTradeWithBank (int resourceGiving, int resourceReceiving) {
        Log.d(TAG, "tryTradeWithBank() called with: resourceGiving = [" + resourceGiving + "], resourceReceiving = [" + resourceReceiving + "]");
        if (resourceGiving < 0) {
            messageTextView.setText(R.string.give_res_not_sel);
            shake(messageTextView);
            return false;
        }
        if (resourceReceiving < 0) {
            messageTextView.setText(R.string.rec_res_not_sel);
            shake(messageTextView);
            return false;
        }
        // Check if player has 4 or more of the resource they have selected to give to the bank.
        if (state.getPlayerList().get(state.getCurrentPlayerId()).getResourceCards()[resourceGiving] - 4 >= 0) {
            Log.d(TAG, "tryTradeWithBank: sending CatanTradeWithBankAction to the game.");
            game.sendAction(new CatanTradeWithBankAction(this, resourceGiving, resourceReceiving));
            return true;
        }
        Log.d(TAG, "tryTradeWithBank: player " + state.getPlayerList().get(state.getCurrentPlayerId()) + " would have have enough " + resourceGiving + " to complete trade");
        return false;
    }

    /* ---------------------------------------- GUI Methods --------------------------------------*/

    private void updateTextViews () {

        // Check if the Game State is null. If it is return void.
        if (this.state == null) {
            Log.e(TAG, "updateTextViews: state is null. Returning void.");
            return;
        }

        String devCardNames[] = {"Knight Development", "Victory Points Development", "Year of Plenty", "Monopoly", "Road Development"};

        if (!devCards.isEmpty()) {
            devCards.clear();
        }
        for (int i = 0; i < state.getPlayerList().get(this.playerNum).getDevelopmentCards().size(); i++) {
            devCards.add(devCardNames[state.getPlayerList().get(this.playerNum).getDevelopmentCards().get(i)]);
        }

        List<String> spinnerList = new ArrayList<>(devCards);
        if (spinnerList.size() == 0) {
            this.useDevCard.setAlpha(0.5f);
            this.useDevCard.setClickable(false);
        } else {
            this.useDevCard.setAlpha(1f);
            this.useDevCard.setClickable(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(myActivity, R.layout.support_simple_spinner_dropdown_item, spinnerList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        devCardList.setAdapter(adapter);

        // Apply the adapter to the spinner
        // array of dice image ids
        int diceImageIds[] = {R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3, R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6};

        // set the dice ImageViews to the corresponding dice image of the current dice values
        diceImageLeft.setBackgroundResource(diceImageIds[state.getDice().getDiceValues()[0] - 1]);
        diceImageRight.setBackgroundResource(diceImageIds[state.getDice().getDiceValues()[1] - 1]);

        if (this.state.getRobberPhase() && this.state.getCurrentPlayerId() == playerNum) {

            this.messageTextView.setText(R.string.robber_phase);

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

            if (state.needsToDiscardHalf(this.playerNum) && !state.isHasDiscarded()) {
                Log.d(TAG, "updateTextViews: Has not discarded cards");
                robberDiscardGroup.setVisibility(View.VISIBLE);
            } else if (state.getCurrentPlayerId() == playerNum && state.isHasDiscarded()) {
                Log.d(TAG, "updateTextViews: Now needs to move Robber");
                robberChooseHexGroup.setVisibility(View.VISIBLE);
            } else {
                // todo
            }
        } else if (this.state.isSetupPhase()) { // IF SETUP PHASE

            this.messageTextView.setText(R.string.setup_phase); // set info message

            // get settlement and road count for the current turn
            int settlements = Collections.frequency(this.buildingsBuiltOnThisTurn, 1);
            int roads = Collections.frequency(this.buildingsBuiltOnThisTurn, 0);

            if (settlements == 2 && roads == 2) {
                this.endTurnButton.setAlpha(1f);
                this.endTurnButton.setClickable(true);
                this.messageTextView.setText(R.string.setup_phase_complete);
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

        } else if (!state.isActionPhase()) { // IF NOT THE ACTION PHASE AND NOT THE SETUP PHASE

            this.messageTextView.setText(R.string.roll_the_dice);

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

        } else { // ACTION PHASE AND NOT SETUP PHASE
            this.messageTextView.setText(R.string.action_phase);
            setAllButtonsToVisible();
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
        }

        //Not
        if (this.playerNum != state.getCurrentPlayerId()) {
            this.rollButton.setAlpha(0.5f);
            this.rollButton.setClickable(false);
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

            this.sidebarScoreboardButton.setAlpha(1f);
            this.sidebarScoreboardButton.setClickable(true);
            this.sidebarMenuButton.setAlpha(1f);
            this.sidebarMenuButton.setClickable(true);
        }

        if (this.debugMode) {
            setAllButtonsToVisible();
        }

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
        this.player0Name.setText(getAllPlayerNames()[0]);
        this.player1Name.setText(getAllPlayerNames()[1]);
        this.player2Name.setText(getAllPlayerNames()[2]);
        this.player3Name.setText(getAllPlayerNames()[3]);

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
        this.playerNameSidebar.setText(getAllPlayerNames()[0]);

        // human player score (sidebar menu)
        this.myScore.setText(String.valueOf(this.state.getPlayerVictoryPoints()[this.state.getCurrentPlayerId()]));

        // current turn indicator (sidebar menu)
        this.currentTurnIdTextView.setText(String.valueOf(getAllPlayerNames()[state.getCurrentPlayerId()]));

        /* -------- animations ----------- */
        this.playerNameSidebar.setTextColor(HexagonGrid.playerColors[this.playerNum]);

        if (this.state.getCurrentPlayerId() == this.playerNum && !this.state.isActionPhase()) {
            this.playerNameSidebar = (TextView) blinkAnimation(this.playerNameSidebar);
        }

    } // updateTextViews END

    /**
     * callback method when we get a message (e.g., from the game)
     *
     * @param info the message
     */
    @Override
    public void receiveInfo (GameInfo info) {
        if (debugMode) {
            Log.d(TAG, "receiveInfo() called with: info: \n" + info.toString() + "----------------------------");
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

            if (state.isRobberPhase() && state.getCurrentPlayerId() != playerNum) {
                messageTextView.setText(R.string.robber_phase);
                if (state.needsToDiscardHalf(playerNum)) {
                    robberDiscardGroup.setVisibility(View.VISIBLE);
                } else {
                    game.sendAction(new CatanRobberDiscardAction(this, playerNum, new ArrayList<Integer>()));
                }
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
     * callback method--our game has been chosen/re-chosen to be the GUI,
     * called from the GUI thread
     *
     * @param activity the activity under which we are running
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setAsGui (GameMainActivity activity) {
        Log.d(TAG, "setAsGui() called with: activity = [" + activity + "]");

        myActivity = activity; // remember the activity
        activity.setContentView(R.layout.activity_main); // Load the layout resource for our GUI

        messageTextView = activity.findViewById(R.id.textview_game_message);

        /* ---------- Surface View for drawing the graphics ----------- */

        this.boardSurfaceView = activity.findViewById(R.id.board); // boardSurfaceView board is the custom SurfaceView
        this.boardSurfaceView.setOnClickListener(clickListener);
        this.boardSurfaceView.setOnTouchListener(touchListener);

        /* ----------------------------------- SIDEBAR ------------------------------------------ */

        //dice roll images
        diceImageLeft = activity.findViewById(R.id.diceImageLeft);
        diceImageRight = activity.findViewById(R.id.diceImageRight);

        /* ------------------------ Building Buttons -----------------------------------------*/
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
        robberDiscardMessage = activity.findViewById(R.id.robber_discard_selectMoreResources);
        robberDiscardGroup = activity.findViewById(R.id.robber_discard_group);
        robberConfirmDiscard = activity.findViewById(R.id.robber_discard_confirm);

        robberBrickAmount = activity.findViewById(R.id.robber_discard_brickAmount);
        robberLumberAmount = activity.findViewById(R.id.robber_discard_lumberAmount);
        robberGrainAmount = activity.findViewById(R.id.robber_discard_grainAmount);
        robberOreAmount = activity.findViewById(R.id.robber_discard_oreAmount);
        robberWoolAmount = activity.findViewById(R.id.robber_discard_woolAmount);

        robberConfirmHex = activity.findViewById(R.id.robber_choosehex_confirm);
        robberHexMessage = activity.findViewById(R.id.robber_choosehex_message);
        robberHexMessage.setText(R.string.choose_robber_tile);
        robberChooseHexGroup = activity.findViewById(R.id.robber_choosehex_menu);

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

        robberConfirmDiscard.setOnClickListener(this);
        robberConfirmHex.setOnClickListener(this);

        rollButton = activity.findViewById(R.id.sidebar_button_roll); // Roll button
        rollButton.setOnClickListener(this);
        endTurnButton = activity.findViewById(R.id.sidebar_button_endturn); // End Turn button
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

        devcard_text_info = activity.findViewById(R.id.development_Card_Info);
        devCardList = activity.findViewById(R.id.development_Card_Spinner); // DEV CARD SPINNER

        devCardList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected (AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String devCardNames[] = {"Knight Development", "Victory Points Development", "Year of Plenty", "Monopoly", "Road Development"};

                int devCardId = -1;
                for (int i = 0; i < devCardNames.length; i++) {
                    if (devCardNames[i].equals(devCards.get(position))) devCardId = i;
                }
                switch (devCardId) {
                    case 0:
                        devcard_text_info.setText(R.string.knight_info);
                        break;
                    case 1:
                        devcard_text_info.setText(R.string.victory_point_info);
                        break;
                    case 2:
                        devcard_text_info.setText(R.string.year_of_plenty_name);
                        break;
                    case 3:
                        devcard_text_info.setText(R.string.monopoly_info);
                        break;
                    case 4:
                        devcard_text_info.setText(R.string.road_building_info);
                        break;
                }
            }

            @Override
            public void onNothingSelected (AdapterView<?> parentView) {
                devcard_text_name.setText(R.string.knight_name);
                devcard_text_info.setText(R.string.knight_info);
            }
        });

        scoreBoardGroup = activity.findViewById(R.id.group_scoreboard);

        this.player0Score = activity.findViewById(R.id.Player1_Score); // scores
        this.player1Score = activity.findViewById(R.id.Player2_Score);
        this.player2Score = activity.findViewById(R.id.Player3_Score);
        this.player3Score = activity.findViewById(R.id.Player4_Score);
        this.player0Name = activity.findViewById(R.id.Player1_Name); // names
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
        // confirm and cancel trade buttons
        button_trade_menu_confirm = activity.findViewById(R.id.button_trade_menu_confirm);
        button_trade_menu_confirm.setOnClickListener(this);
        button_trade_menu_cancel = activity.findViewById(R.id.button_trade_menu_cancel);
        button_trade_menu_cancel.setOnClickListener(this);
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

        /*--------------------Robber Buttons and Groups------------------------*/

        robberDiscardGroup = activity.findViewById(R.id.robber_discard_group);

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

        List<String> spinnerList = new ArrayList<>(devCards);
        devCardList.setAdapter(new ArrayAdapter<>(activity, R.layout.support_simple_spinner_dropdown_item, spinnerList));
        messageTextView.setTextColor(Color.WHITE);

        /*--------------------------Monopoly---------------------------------*/

        monopolyPickGroup = activity.findViewById(R.id.group_pickResourceMenu);
        monopolyPickGroup.setOnClickListener(this);

        monopolyBrickIcon = activity.findViewById(R.id.pickResMenu_brickIcon);
        monopolyBrickIcon.setOnClickListener(this);
        monopolyGrainIcon = activity.findViewById(R.id.pickResMenu_grainIcon);
        monopolyBrickIcon.setOnClickListener(this);
        monopolyLumberIcon = activity.findViewById(R.id.pickResMenu_lumberIcon);
        monopolyLumberIcon.setOnClickListener(this);
        monopolyOreIcon = activity.findViewById(R.id.pickResMenu_oreIcon);
        monopolyOreIcon.setOnClickListener(this);
        monopolyWoolIcon = activity.findViewById(R.id.pickResMenu_woolIcon);
        monopolyWoolIcon.setOnClickListener(this);
        monopolyBrickSelectionBox = activity.findViewById(R.id.pickResMenu_brickSelectionBox);
        monopolyGrainSelcionBox = activity.findViewById(R.id.pickResMenu_grainSelectionBox);
        monopolyLumberSelectionBox = activity.findViewById(R.id.pickResMenu_lumberSelectionBox);
        monopolyOreSelectionBox = activity.findViewById(R.id.pickResMenu_oreSelectionBox);
        monopolyWoolSelectionBox = activity.findViewById(R.id.pickResMenu_woolSelectionBox);

        monopolyConfirm = activity.findViewById(R.id.pickResMenu_ConfirmButton);
        monopolyConfirm.setOnClickListener(this);

        // if we have state update the GUI based on the state
        if (this.state != null) receiveInfo(state);
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
                super.gameIsOver(getAllPlayerNames()[i] + " wins!");
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
    private void toggleGroupVisibilityAllowTapping (Group group) {
        if (group.getVisibility() == View.GONE) group.setVisibility(View.VISIBLE);
        else group.setVisibility(View.GONE);
    }

    /**
     * Toggles the visibility of a group.
     *
     * @param group Group to toggle visibility.
     */
    private void toggleGroupVisibility (Group group) {
        if (group.getVisibility() == View.GONE) {
            this.isMenuOpen = true;
            group.setVisibility(View.VISIBLE);
        } else {
            this.isMenuOpen = false;
            group.setVisibility(View.GONE);
        }
    }

    /**
     * @param view View to toggle.
     */
    private void toggleViewVisibility (View view) {
        if (view.getVisibility() == View.GONE) view.setVisibility(View.VISIBLE);
        else view.setVisibility(View.GONE);
    }

    /**
     * Sets all buttons to visible and clickable.
     */
    private void setAllButtonsToVisible () {
        this.buildRoadButton.setAlpha(1f);
        this.buildRoadButton.setClickable(true);
        this.buildSettlementButton.setAlpha(1f);
        this.buildSettlementButton.setClickable(true);
        this.buildCityButton.setAlpha(1f);
        this.buildCityButton.setClickable(true);
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
    }

    /**
     * Make a View Blink for a desired duration
     *
     * @param view View to be animated.
     * @return returns The View with animation properties on it.
     */
    private static View blinkAnimation (View view) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(300);
        anim.setStartOffset(100);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(3);
        view.startAnimation(anim);
        return view;
    }

    /**
     * @param v View to make shake.
     */
    private void shake (TextView v) {
        Animation shake = AnimationUtils.loadAnimation(myActivity.getApplicationContext(), R.anim.shake_anim);
        v.setTextColor(Color.RED);
        v.startAnimation(shake);
        v.startAnimation(shake);
    }

    /**
     * @return names of all the players in the game
     */
    private String[] getAllPlayerNames () {
        return super.allPlayerNames;
    }

}// class CatanHumanPlayer END

