package edu.up.cs.androidcatan.catan;


import android.util.Log;

import edu.up.cs.androidcatan.catan.actions.CatanBuildCityAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildRoadAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuildSettlementAction;
import edu.up.cs.androidcatan.catan.actions.CatanBuyDevCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanEndTurnAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberDiscardAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberMoveAction;
import edu.up.cs.androidcatan.catan.actions.CatanRobberStealAction;
import edu.up.cs.androidcatan.catan.actions.CatanRollDiceAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithBankAction;
import edu.up.cs.androidcatan.catan.actions.CatanTradeWithPortAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseDevCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseKnightCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseMonopolyCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseRoadBuildingCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseVictoryPointCardAction;
import edu.up.cs.androidcatan.catan.actions.CatanUseYearOfPlentyCardAction;
import edu.up.cs.androidcatan.catan.gamestate.DevelopmentCard;
import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.LocalGame;
import edu.up.cs.androidcatan.game.actionMsg.GameAction;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * @version November 8th, 2018
 * https://github.com/alexweininger/android-catan
 **/

public class CatanLocalGame extends LocalGame {

    private final static String TAG = "CatanLocalGame";

    private CatanGameState gameState;

    CatanLocalGame () {
        super();
        gameState = new CatanGameState();
    }

    /*--------------------------------------- Action Methods -------------------------------------------*/

    /**
     * Tell whether the given player is allowed to make a move at the
     * present point in the game.
     *
     * @param playerIdx the player's player-number (ID)
     * @return true iff the player is allowed to move
     */
    @Override
    protected boolean canMove (int playerIdx) {
        Log.d(TAG, "canMove() called with: playerIdx = [" + playerIdx + "]");

        if (gameState.isRobberPhase()) return true; // todo fix this iffy logic

        if (playerIdx < 0 || playerIdx > 3) Log.e(TAG, "canMove: Invalid playerIds: " + playerIdx);

        Log.d(TAG, "canMove() returned: " + (playerIdx == gameState.getCurrentPlayerId()));
        return playerIdx == gameState.getCurrentPlayerId();
    }

    /**
     * Initiates action based on what kind of GameAction object received.
     *
     * @param action The move that the player has sent to the game
     * @return Tells whether the move was a legal one.
     */
    @Override
    protected boolean makeMove (GameAction action) {
        Log.d(TAG, "makeMove() called with: action = [" + action + "]");

        /* --------------------------- Turn Actions --------------------------------------- */

        if (action instanceof CatanRollDiceAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.rollDice();
        }

        if (action instanceof CatanEndTurnAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.endTurn();
        }

        /* --------------------------- Build Actions --------------------------------------- */

        if (action instanceof CatanBuildRoadAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.buildRoad(gameState.getCurrentPlayerId(), ((CatanBuildRoadAction) action).getIntersectionAId(), ((CatanBuildRoadAction) action).getIntersectionBid());
        }

        if (action instanceof CatanBuildSettlementAction) {
            Log.i(TAG, "makeMove: received an CatanBuildSettlementAction. Returning a CatanGameState.buildSettlement action.");
            return gameState.buildSettlement(gameState.getCurrentPlayerId(), ((CatanBuildSettlementAction) action).getIntersectionId());
        }

        if (action instanceof CatanBuildCityAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.buildCity(gameState.getCurrentPlayerId(), ((CatanBuildCityAction) action).getIntersectionId());
        }

        /*------------------------------- Development Card Actions -------------------------------*/

        if (action instanceof CatanBuyDevCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            Player player = gameState.getCurrentPlayer();

            // remove resources from players inventory (also does checks)
            if (!player.removeResourceBundle(DevelopmentCard.resourceCost)) return false;

            // add random dev card to players inventory
            player.getDevelopmentCards().add(gameState.getRandomCard());
            return true;
        }

        if (action instanceof CatanUseDevCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            //return gameState.useDevCard();
            return true;
        }

        if (action instanceof CatanUseKnightCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.useDevCard(gameState.getCurrentPlayerId(), 0);
        }

        if (action instanceof CatanUseVictoryPointCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");

            gameState.getPlayerList().get(gameState.getCurrentPlayerId()).addVictoryPointsDevCard();
            return true;
        }

        if (action instanceof CatanUseRoadBuildingCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            gameState.getCurrentPlayer().addResourceCard(0, 2);
            gameState.getCurrentPlayer().addResourceCard(2, 2);
            return gameState.useDevCard(gameState.getCurrentPlayerId(), 4);
        }

        if (action instanceof CatanUseMonopolyCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            int totalResources = 0;

            int resourceId = ((CatanUseMonopolyCardAction) action).getChosenResource();

            for (Player player : gameState.getPlayerList()) {
                int resCount = player.getResourceCards()[resourceId];
                player.removeResourceCard(resourceId, resCount);
                totalResources += resCount;
            }

            gameState.getCurrentPlayer().addResourceCard(resourceId, totalResources);

            return true;
        }

        if (action instanceof CatanUseYearOfPlentyCardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            gameState.getCurrentPlayer().addResourceCard(((CatanUseYearOfPlentyCardAction) action).getChosenResource(), 2);
            return gameState.useDevCard(gameState.getCurrentPlayerId(), 2);
        }

        /*---------------------------------- Robber Actions --------------------------------------*/

        if (action instanceof CatanRobberDiscardAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.discardResources(((CatanRobberDiscardAction) action).getPlayerId(), ((CatanRobberDiscardAction) action).getRobberDiscardedResources());
        }
        if (action instanceof CatanRobberMoveAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.tryToMoveRobber(((CatanRobberMoveAction) action).getHexagonId(), ((CatanRobberMoveAction) action).getPlayerId());
        }
        if (action instanceof CatanRobberStealAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.robberSteal(((CatanRobberStealAction) action).getPlayerId(), ((CatanRobberStealAction) action).getStealId());
        }

        /*---------------------------------- Trade Actions ---------------------------------------*/

        if (action instanceof CatanTradeAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            //return gameState.trade();
            return true;
        }

        if (action instanceof CatanTradeWithBankAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            return gameState.tradeWithBank(gameState.getCurrentPlayerId(), ((CatanTradeWithBankAction) action).getResourceIdGiving(), ((CatanTradeWithBankAction) action).getResourceIdRec());
        }

        if (action instanceof CatanTradeWithPortAction) {
            Log.d(TAG, "makeMove() called with: action = [" + action + "]");
            //            return gameState.tradeWithPort(gameState.getCurrentPlayerId());
        }

        // if we reach here, the GameAction object we received is not one that we recognize
        Log.e(TAG, "makeMove: FATAL ERROR: GameAction action was not and instance of an action class that we recognize.");
        return false;
    }

    /*---------------------- Methods for checking the Game State and updating it ------------------------------------*/

    /**
     * Notify the given player that its state has changed. This should involve sending
     * a GameInfo object to the player. If the game is not a perfect-information game
     * this method should remove any information from the game that the player is not
     * allowed to know.
     *
     * @param p the player to notify
     */
    @Override
    protected void sendUpdatedStateTo (GamePlayer p) {
        Log.d(TAG, "sendUpdatedStateTo() called with: p = [" + p + "]");
        p.sendInfo(new CatanGameState(this.gameState));
    }

    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return a message that tells who has won the game, or null if the
     * game is not over
     */
    @Override
    protected String checkIfGameOver () {
        Log.d(TAG, "checkIfGameOver() called");
        for (int i = 0; i < this.gameState.getPlayerVictoryPoints().length; i++) {
            if (this.gameState.getPlayerVictoryPoints()[i] > 9) {
                return playerNames[i] + " wins!";
            }
        }
        return null; // return null if no winner, but the game is not over
    }
}
