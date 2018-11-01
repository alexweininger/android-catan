package edu.up.cs.androidcatan;

import android.view.View;

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
 * @version October 31, 2018
 * https://github.com/alexweininger/android-catan
 **/
public class MainActivity extends GameMainActivity {

    // the port number that this game will use when playing over the network
    private static final int PORT_NUMBER = 2278;

    @Override
    public GameConfig createDefaultConfig() {
        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // Pig has two player types:  human and computer
        playerTypes.add(new GamePlayerType("Local Human Player") {
            public GamePlayer createPlayer(String name) {
                return new Player(0);
            }});

        // Create a game configuration class for Pig:
        GameConfig defaultConfig = new GameConfig(playerTypes, 4, 4, "Settlers of Catan", PORT_NUMBER);
        defaultConfig.addPlayer("Human", 0); // player 1: a human player
        defaultConfig.addPlayer("Computer", 1); // player 2: a computer player
        defaultConfig.addPlayer("Smart Computer", 2); // Player 3 a smart computer player
        defaultConfig.setRemoteData("Remote Human Player", "", 0);

        return defaultConfig;
    }

    @Override
    public LocalGame createLocalGame() {
        return new CatanLocalGame();
    }

    /*protected void onCreate(Bundle savedInstanceState) {

        /*
         * External Citation
         * Date: 20 September 2018
         * Problem: Needed more screen space and wanted to get rid of the title bar and the
         * notification bar
         * Resource:
         * https://stackoverflow.com/questions/2591036/how-to-hide-the-title-bar-for-an-activity-in-xml-with-existing-custom-theme
         * Solution: I used the code from the stack overflow post.
         *//*

        // remove title bar and notification bar
        //TODO: Speak to Dr. Tribelhorn about moving to GameMainActivity
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        boardSurfaceView board = findViewById(R.id.board); // boardSurfaceView board is the custom SurfaceView

        Canvas canvas = new Canvas(); // create Canvas object

        board.createHexagons();        // draw the board of hexagons and ports on the canvas

        board.draw(canvas); // draw

        // button listeners TODO move to separate class?
        Button scoreButton = findViewById(R.id.sidebar_button_score);
        final Group scoreBoardGroup = findViewById(R.id.group_scoreboard);
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

        Button developmentButton = findViewById(R.id.sidebar_button_devcards);
        final Group developmentGroup = findViewById(R.id.group_development_card_menu);
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
        final Group buildMenuGroup = findViewById(R.id.group_build_menu);

        Button roadButton = findViewById(R.id.sidebar_button_road);

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

        // the spinner that holds the development cards the player has
        Spinner developmentSpinner = findViewById(R.id.development_Card_Spinner);

        // spinner logic and handlers
        ArrayAdapter<CharSequence> developmentChoices = ArrayAdapter.createFromResource(this, R.array.resource_Card, android.R.layout.simple_spinner_item);
        developmentChoices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        developmentSpinner.setAdapter(developmentChoices);
    }*/

    // TODO cite this https://stackoverflow.com/questions/46065897/android-studio-how-to-remove-navigation-bar-in-android-app-to-get-a-full-screen
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
