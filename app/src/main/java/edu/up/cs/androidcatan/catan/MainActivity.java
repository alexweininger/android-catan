package edu.up.cs.androidcatan.catan;

import java.util.ArrayList;

import edu.up.cs.androidcatan.game.GameMainActivity;
import edu.up.cs.androidcatan.game.GamePlayer;
import edu.up.cs.androidcatan.game.LocalGame;
import edu.up.cs.androidcatan.game.config.GameConfig;
import edu.up.cs.androidcatan.game.config.GamePlayerType;

/**
 * @author Alex Weininger
 * @author Andrew Lang
 * @author Daniel Borg
 * @author Niraj Mali
 * https://github.com/alexweininger/android-catan
 **/

public class MainActivity extends GameMainActivity {

    // the port number that this game will use when playing over the network
    private static final int PORT_NUMBER = 2278;

    // default game configuration
    @Override
    public GameConfig createDefaultConfig() {
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // Catan has three player types:  human, smart cpu, dumb cpu
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new CatanHumanPlayer(name);
            }
        });
        playerTypes.add(new GamePlayerType("Dumb Computer") {
            public GamePlayer createPlayer(String name) {
                return new CatanDumbComputerPlayer(name);
            }
        });
        playerTypes.add(new GamePlayerType("Smart Computer") {
            public GamePlayer createPlayer(String name) {
                return new CatanSmartComputerPlayer(name);
            }
        });

        GameConfig defaultConfig = new GameConfig(playerTypes, 4, 4, "Settlers of Catan", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0); // player 1: a human player
        defaultConfig.addPlayer("Computer 1", 1); // player 2: a computer player
        defaultConfig.addPlayer("Computer 2", 1); // player 2: a computer player
        defaultConfig.addPlayer("Computer 3", 1); // player 2: a computer player
        defaultConfig.setRemoteData("Remote Human Player", "", 0);

        return defaultConfig;
    }

    @Override
    public LocalGame createLocalGame() {
        return new CatanLocalGame();
    }
}
