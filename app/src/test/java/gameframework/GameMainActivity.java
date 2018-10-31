package gameframework;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import gameframework.config.GameConfig;
import gameframework.config.GamePlayerType;
import gameframework.util.IPCoder;
import gameframework.util.MessageBox;

/**
 * class GameMainActivity
 *
 * is the main activity for the game framework. To create a new game, create a
 * sub-class of this class that implements its abstract methods below.
 *
 * @author Andrew M. Nuxoll
 * @author Steven R. Vegdahl
 * @date Version 2013
 */
public abstract class GameMainActivity extends Activity implements
        View.OnClickListener {

    /*
     * ====================================================================
     * Instance Variables
     * --------------------------------------------------------------------
     */

    // A reference to the object representing the game itself. This is the
    // object that knows the rules of the game. This variable is initialized in
    // launchGame.
    private Game game = null;

    // an array containing references to all the players that are playing the game
    private GamePlayer[] players = null;

    // tells which player, if any, is running in the GUI
    private GamePlayer guiPlayer = null;

    // whether the game is over
    private boolean gameIsOver = false;

    // whether it is so early in the game that the configuration screen may
    // not have been fully linked to the GUI
    private boolean justStarted = true;

    // whether the game is in the "configuration" stage, before the actual game
    // has started
    private boolean doingConfiguration = true;

    /**
     * contains the game configuration this activity will be used to initialize
     */
    GameConfig config = null;

    // Each of these is initialized to point to various GUI controls
    TableLayout playerTable = null;
    ArrayList<TableRow> tableRows = new ArrayList<TableRow>();

    /*
     * ====================================================================
     * Abstract Methods
     *
     * To create a game using the game framework you must create a subclass of
     * GameMainActivity that implements the following methods.
     * --------------------------------------------------------------------
     */
    /**
     * Creates a default, game-specific configuration for the current game.
     *
     * IMPORTANT: The default configuration must be a legal configuration!
     *
     * @return an instance of the GameConfig class that defines a default
     *         configuration for this game. (The default may be subsequently
     *         modified by the user if this is allowed.)
     */
    public abstract GameConfig createDefaultConfig();

    /**
     * createLocalGame
     *
     * Creates a new game that runs on the server tablet. For example, if
     * you were creating tic-tac-toe, you would implement this method to return
     * an instance of your TTTLocalGame class which, in turn, would be a
     * subclass of {@link LocalGame}.
     *
     * @return a new, game-specific instance of a sub-class of the LocalGame
     *         class.
     */
    public abstract LocalGame createLocalGame();

    /**
     * Creates a "proxy" game that acts as an intermediary between a local
     * player and a game that is somewhere else on the net.
     *
     * @param hostName
     *            the name of the machine where the game resides. (e.g.,
     *            "upibmg.egr.up.edu")
     * @return the ProxyGame object that was created
     */
    private ProxyGame createRemoteGame(String hostName) {
        int portNum = getPortNumber();
        return ProxyGame.create(portNum, hostName);
    }

    /*
     * ====================================================================
     * Public Methods
     * --------------------------------------------------------------------
     */
    /**
     * onCreate
     *
     * "main" for the game framework
     */
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the layout
        setContentView(R.layout.game_config_main);

        // create the default configuration for this game
        this.config = createDefaultConfig();

        // if there is a saved configuration, modify the default configuration accordingly
        if (!this.config.restoreSavedConfig(saveFileName(), this)) {
            MessageBox.popUpMessage("Error in attempting to read game configuration file.",
                    this);
        }

        if (this.config.isUserModifiable()) { // normal run: user has chance to modify configuration

            // initialize and show the GUI that allows the user to specify the game's
            // configuration
            initStarterGui();

            // hide the soft keyboard, so the that user does not need to dismiss it (which
            // he would often want to do)
            hideSoftKeyboard();

            // allow buttons to interact
            justStarted = false;
        }
        else { // special run (during debugging?): use the given configuration, unmodified
            String msg = launchGame(this.config);
            if (msg != null) {
                // we have an error message
                MessageBox.popUpMessage(msg, this);
            }
        }

    }// onCreate

    /**
     * Returns the name of the configuration save-file.
     *
     * @return
     * 		the name of the configuration file for this application to use
     */
    private String saveFileName() {
        return "savedConfig"+getPortNumber()+".dat";
    }//saveFileName

    /**
     * hides the soft keyboard so that the use does not need to dismiss it
     */
    private void hideSoftKeyboard() {
        // create a runnable object that waits for things to settle down, and then
        // hides the window
        Runnable runner = new Runnable() {
            public void run() {
                try {
                    // wait for one second
                    Thread.sleep(1000);

                    // hide the keyboard
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                catch (Exception x) {
                    // catch and ignore any exceptions we might encounter
                }
            }
        };

        // run the thread
        Thread t = new Thread(runner);
        t.start();
//		try {
//			// join the thread to that we don't get ahead of it
//			t.join();
//		} catch (InterruptedException e) {
//		}

    }//hideSoftKeyboard

    /**
     * Callback-method, called when the configuration changes--typically when the tablet
     * is rotated.
     */
    public void onConfigurationChanged(Configuration newConfig) {

        // Perform superclass configuration changes
        super.onConfigurationChanged(newConfig);

        // if still on the configuration screen, continue showing it;
        // otherwise, set the new GUI (which may have changed) for the
        // human player
        if (!doingConfiguration) {
            if (guiPlayer != null) {
                // if there is a GUI player, link it to the activity
                guiPlayer.gameSetAsGui(this);
            }
            else {
                // if there is no GUI player, set the layout to be one
                // with a "no GUI" message
                setContentView(R.layout.game_no_gui);
            }
        }
    }//onConfigurationChanged

    /**
     * Creates the game and players, and starts the game.
     *
     * @param config
     *            is the configuration for this game
     * @return
     * 			null if the launch was successful; otherwise a message telling
     * 			why game could not be launched
     */
    private final String launchGame(GameConfig config) {

        // Set the title text with the game's name
        this.setTitle(config.getGameName());

        // create the game if it's local (we defer remote game creation
        // until further down so that we do not attempt to make the
        // network connection until other errors are checked)
        if (config.isLocal()) { // local game
            game = createLocalGame();
            // verify we have a game
            if (game == null) {
                return "Game creation failed.";
            }
        }

        //////////////////////////////////////
        // create the players
        //////////////////////////////////////
        int requiresGuiCount = 0; // the number of players that require a GUI
        guiPlayer = null; // the player that will be our GUI player
        players = new GamePlayer[config.getNumPlayers()]; // the array to contains our players

        // loop through each player
        for (int i = 0; i < players.length; i++) {
            String name = config.getSelName(i); // the player's name
            GamePlayerType gpt = config.getSelType(i); // the player's type
            GamePlayerType[] availTypes = config.getAvailTypes(); // the available player types
            players[i] = gpt.createPlayer(name); // create the player

            // check that the player name is legal
            if (name.length() <= 0 && gpt != availTypes[availTypes.length-1]) {
                // disallow an empty player name, unless it's a dummy (proxy) player
                return "Local player name cannot be empty.";
            }

            // if the player requires a GUI, count and mark it; otherwise, if a player
            // supports a GUI and the "requires" count is zero, mark it
            if (players[i].requiresGui()) {
                requiresGuiCount++;
                guiPlayer = players[i];
            }
            else if (guiPlayer == null && players[i].supportsGui()) {
                guiPlayer = players[i];
            }
        }

        // create the game if it's remote
        if (!config.isLocal()) { // remote game
            game = createRemoteGame(config.getIpCode());
            // verify we have a game
            if (game == null) {
                return "Could not find game server on network.";
            }
        }

        // if there is more than one player that requires a GUI, abort
        if (requiresGuiCount >= 2) {
            return "Cannot have more than one GUI player on a single device.";
        }

        // if there is a player that supports a GUI, link it to the activity,
        // otherwise set the GUI to be a "dummy" one with a "no GUI" message
        if (guiPlayer != null) {
            guiPlayer.gameSetAsGui(this);
        }
        else {
            // set the layout to be one with a "no GUI" message
            setContentView(R.layout.game_no_gui);
        }

        // mark the configuration as being completed
        doingConfiguration = false;

        // start the game; then return null to indicate that the launch was
        // successful
        game.start(players);
        return null;

    }// launchGame

    /**
     * initializes the pages in the tabbed dialog
     */
    protected void initTabs() {
        // Setup the tabbed dialog on the layout and add the content of each tab
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabSpec localTabSpec = tabHost.newTabSpec(localTabString());
        localTabSpec.setContent(R.id.localGameTab);
        localTabSpec.setIndicator(localTabString());
        TabSpec remoteTabSpec = tabHost.newTabSpec(remoteTabString());
        remoteTabSpec.setContent(R.id.remoteGameTab);
        remoteTabSpec.setIndicator(remoteTabString());
        tabHost.addTab(localTabSpec);
        tabHost.addTab(remoteTabSpec);

        // make sure the current tab is the right one
        tabHost.setCurrentTab(config.isLocal() ? 0 : 1);

    }// initTabs

    /**
     * initialize the rows in the player table
     */
    protected void initTableRows() {

        // save away the information about whether we're on the local tab;
        // set things temporarily ab being true so that the rows end up in
        // the first tab
        boolean savedIsLocal = config.isLocal();
        config.setLocal(true);

        // put a row in the table for each player in the config
        this.playerTable = (TableLayout) findViewById(R.id.configTableLayout);
        int numPlayers = config.getNumPlayers();
        for (int i = 0; i < numPlayers; ++i) {

            // add the row
            TableRow row = addPlayer();

            // Set the player name
            TextView playerName = (TextView) row
                    .findViewById(R.id.playerNameEditText);
            playerName.setText(config.getSelName(i));

            // Set the initial selection for the spinner
            GamePlayerType[] selTypes = config.getSelTypes(); // the player types in the config
            GamePlayerType[] availTypes = config.getAvailTypes(); // the available player types
            Spinner typeSpinner = (Spinner) row
                    .findViewById(R.id.playerTypeSpinner); // the spinner for the current player
            // search through to find the one whose label matches; set it as the selection
            for (int j = 0; j < availTypes.length; ++j) {
                if (selTypes[i].getTypeName().equals(availTypes[j].getTypeName())) {
                    typeSpinner.setSelection(j);
                    break;
                }
            }

            // set up our spinner so that when its last element ("Network Player") is selected,
            // the corresponding EditText (the player name) is disabled.
            typeSpinner.setOnItemSelectedListener(new SpinnerListListener(playerName, availTypes.length-1));

        }// for

        // restore the 'isLocal' property of the configuration object
        config.setLocal(savedIsLocal);

    }// initTableRows

    protected void initRemoteWidgets() {
        //Set the remote name
        EditText remoteNameEditText = (EditText)findViewById(R.id.remoteNameEditText);
        remoteNameEditText.setText(config.getRemoteName());

        // index of remote player type
        GamePlayerType remotePlayerType = config.getRemoteSelType();
        GamePlayerType[] availTypes = config.getAvailTypes();
        Spinner remoteTypeSpinner = (Spinner)findViewById(R.id.remote_player_spinner);
        for (int j = 0; j < availTypes.length; ++j) {
            if (remotePlayerType.getTypeName().equals(availTypes[j].getTypeName())) {
                remoteTypeSpinner.setSelection(j);
                break;
            }
        }

        //Set the IP code
        EditText ipCodeEditText = (EditText)findViewById(R.id.remoteIPCodeEditText);
        ipCodeEditText.setText(config.getIpCode());
    }

    /**
     * places the data from this.config into the GUI.
     *
     */
    protected void initStarterGui() {
        // do nothing without a game config
        if (this.config == null)
            return;

        // Set the title text using the game's name
        this.setTitle(config.getGameName() + " Configuration");

        // place the pages in the tabbed dialog
        initTabs();

        // Insert a row for each player in the current config
        initTableRows();

        // Set the remote widget data
        initRemoteWidgets();

        // Set myself as the listener for the buttons
        View v = findViewById(R.id.addPlayerButton);
        v.setOnClickListener(this);
        v = findViewById(R.id.saveConfigButton);
        v.setOnClickListener(this);
        v = findViewById(R.id.playGameButton);
        v.setOnClickListener(this);


        String ipCode = IPCoder.encodeLocalIP();
        String ipAddress = IPCoder.getLocalIpAddress();
        TextView ipText = (TextView)findViewById(R.id.ipCodeLabel);
        ipText.setText(ipText.getText()+ipCode+" ("+ipAddress+") ");

    }// initStarterGui

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_main, menu);
        return true;
    }//onCreateOptionsMenu

    /**
     * this method is called whenever the user clicks on a button.
     *
     * <p>
     * NOTE: With the current layout it could either be a Button or ImageButton.
     */
    public void onClick(View button) {

        Log.i("onClick", "just clicked");

        // if the GUI many not have been fully initialized, ignore
        if (justStarted) {
            return;
        }

        // Add Player Button
        if (button.getId() == R.id.addPlayerButton) {
            addPlayer();
            this.playerTable.invalidate(); // show the user the change
        }

        // Delete Player Button
        else if (button.getId() == R.id.delPlayerButton) {
            // Search the existing players to find out which delete button got
            // clicked
            for (int i = 0; i < this.tableRows.size(); i++) {
                TableRow row = tableRows.get(i);

                View v = row.findViewById(R.id.delPlayerButton);
                if (v == button) {
                    // found it! remove from the layout and the list
                    removePlayer(row);
                }
            }

        }// else if (delete button)

        //Save Config Button
        else if (button.getId() == R.id.saveConfigButton) {
            GameConfig configTemp = scrapeData();
            if (configTemp.saveConfig(saveFileName(), this)) {
                MessageBox.popUpMessage("Game configuration saved.", this);
            }
            else {
                MessageBox.popUpMessage("Unable to save game configuration.", this);
            }
        }

        //Start Game Button
        else if (button.getId() == R.id.playGameButton) {
            String msg = startGame();
            if (msg != null) {
                // we have an error message
                MessageBox.popUpMessage(msg, this);
            }

        }

    }// onClick

    private String startGame() {
        GameConfig finalConfig = scrapeData();
        return launchGame(finalConfig);
    }

    /**
     * removePlayer
     *
     * removes the player in the table associated with a given TableRow object
     *
     * <p>
     * NOTE: this method will refuse to delete a row if the total would drop
     * below the minimum allowed by the game configuration.
     */
    private void removePlayer(TableRow row) {
        // first, make sure that we won't exceed the min number of players
        if (this.tableRows.size() <= config.getMinPlayers()) {
            MessageBox.popUpMessage("Sorry, removing a player would drop below the minimum allowed.",
                    this);
            return;
        }

        this.playerTable.removeView(row);
        this.tableRows.remove(row);

    }// removePlayer

    /**
     * addPlayer
     *
     * adds a new, blank row to the player table and initializes instance
     * variables and listeners appropriately
     *
     * @return a reference to the TableRow object that was created or null on
     *         failure
     */
    private TableRow addPlayer() {
        // first, make sure that we won't exceed the max number of players
        if (this.tableRows.size() >= config.getMaxPlayers()) {
            MessageBox.popUpMessage("Sorry, adding another player would exceed the maximum allowed.",
                    this);
            return null;
        }

        // add the row
        TableRow row = (TableRow) getLayoutInflater().inflate(
                R.layout.game_player_list_row, playerTable, false);

        // Initialize the values in the Spinner control
        //		GamePlayerType[] selTypes = config.getSelTypes();
        GamePlayerType[] availTypes = config.getAvailTypes();
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (GamePlayerType gpt : availTypes) {
            adapter.add(gpt.getTypeName());
        }
        Spinner typeSpinner = (Spinner) row
                .findViewById(R.id.playerTypeSpinner);
        typeSpinner.setAdapter(adapter);
        // link player name field and spinner
        TextView playerName = (TextView) row
                .findViewById(R.id.playerNameEditText);
        typeSpinner.setOnItemSelectedListener(new SpinnerListListener(playerName, availTypes.length-1));
        typeSpinner.setSelection(0);

        ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (int j = 0; j < availTypes.length-1; j++) {
            // leaves out the last item (network player)
            adapter2.add(availTypes[j].getTypeName());
        }
        Spinner remoteTypeSpinner = (Spinner)findViewById(R.id.remote_player_spinner);
        remoteTypeSpinner.setAdapter(adapter2);

        // set myself up as the button listener for the button
        ImageButton delButton = (ImageButton) row
                .findViewById(R.id.delPlayerButton);
        delButton.setOnClickListener(this);

        // add the row to the right lists and layout
        this.tableRows.add(row);
        playerTable.addView(row);

        return row;
    }// addPlayer

    /**
     * scrapeData
     *
     * retrieves all the data from the GUI and creates a new GameConfig object
     * with it
     */
    public GameConfig scrapeData() {

        // First make a copy of the original config without the players
        GameConfig result = config.copyWithoutPlayers();

        // Set remote/local
        TabHost tabHost = (TabHost)findViewById(R.id.tabHost);
        result.setLocal(tabHost.getCurrentTab() == 0);

        // Retrieve the info for each player and add to the config
        for (TableRow row : this.tableRows) {
            //player name
            EditText nameEditor = (EditText) row
                    .findViewById(R.id.playerNameEditText);
            String name = nameEditor.getText().toString();

            //index of player type
            Spinner typeSpinner = (Spinner) row
                    .findViewById(R.id.playerTypeSpinner);
            int selIndex = typeSpinner.getSelectedItemPosition();

            //add to the config
            result.addPlayer(name, selIndex);
        }//for

        //Set the remote name
        EditText remoteNameEditText = (EditText)findViewById(R.id.remoteNameEditText);
        String remoteName = remoteNameEditText.getText().toString();
        result.setRemoteName(remoteName);

        //index of remote player type
        Spinner remoteTypeSpinner = (Spinner)findViewById(R.id.remote_player_spinner);
        int selIndex = remoteTypeSpinner.getSelectedItemPosition();
        result.setRemoteSelType(selIndex);

        //Set the IP code
        EditText ipCodeEditText = (EditText)findViewById(R.id.remoteIPCodeEditText);
        String ipCode = ipCodeEditText.getText().toString();
        result.setIpCode(ipCode);

        return result;
    }// scrapeData

    /**
     * Call-back method when a soft key-event happens. Intercepts the "back" button
     * so that the activity is not killed with out user confirmation (unless the
     * game is already over).
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !gameIsOver) {
            // We have seen the back-key pressed, and the game is not over;
            // confirm with user that whether they want to quit
            String quitQuestion =
                    getResources().getString(R.string.dialog_quit_question);
            String posLabel =
                    getResources().getString(R.string.dialog_quit_label);
            String negLabel =
                    getResources().getString(R.string.dialog_continue_label);
            MessageBox.popUpChoice(quitQuestion, posLabel, negLabel,
                    new OnClickListener(){
                        public void onClick(DialogInterface di, int val) {
                            // if the user says that he wants to quit, exit the
                            // application
                            System.exit(0);
                        }},
                    null,
                    this);
            // return 'true' because we have handled this event
            return true;
        }
        else {
            // otherwise (not BACK key, or game is over), allow superclass method
            // to handle it
            return super.onKeyDown(keyCode, event);
        }
    }// onKeyDown

    /**
     * Gets the port number for this configuration
     *
     * @return the configuration's port number
     */
    private int getPortNumber() {
        return config.getPortNum();
    }

    /**
     * marks the game as being over
     *
     * @param b
     * 			tells whether the game is over
     */
    public void setGameOver(boolean b) {
        gameIsOver = b;
    }// setGameOver

    /**
     *  the label for the local tab header
     *
     * @return
     * 		the label for the local tab header
     */
    private String localTabString() {
        return this.getResources().getString(R.string.local_tab);
    }// localTabString

    /**
     *  the label for the remote tab header
     *
     * @return
     * 		the label for the remote tab header
     */
    private String remoteTabString() {
        return this.getResources().getString(R.string.remote_tab);
    }// remoteTabString


    /**
     * Helper-class so that we disable the name fields in the configuration
     * if the user has selected "Network player".
     */
    private static class SpinnerListListener implements OnItemSelectedListener {

        // the textView to disable
        private TextView correspondingTextField;

        // the position in the spinner of the "Network Player" selection
        private int disableIndex;

        /**
         * constructor
         *
         * @param txt
         * 			the TextView object
         * @param idxNum
         * 			the index of the "Network Player" item in the spinner
         */
        public SpinnerListListener(TextView txt, int idxNum) {
            correspondingTextField = txt;
            disableIndex = idxNum;
        }//constructor

        /**
         * callback method when an item is selected
         *
         * @param parent
         *		the AdapterView where the selection happened
         * @param view
         *		the view within the AdapterView that was clicked
         * @param position
         *		the position in the spinner of the new selection
         * @param id
         *		the row id of the item that is selected
         */
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                   long id) {
            // enable the corresponding TextView depending on whether the "disabling"
            // position was selected
            correspondingTextField.setEnabled(position != disableIndex);
        }// onItemSelected

        /**
         * callback method when nothing is selected
         *
         * @param parent
         *		the AdapterView where the selection happened
         */
        public void onNothingSelected(AdapterView<?> parent) {
            // do nothing
        }// onNothingSelected

    }// class SpinnerListListener

    /**
     * finishes the activity
     *
     * @param v
     * 		the object that cause the callback
     */
    public void doFinish(View v) {
        finish();
    }
}

