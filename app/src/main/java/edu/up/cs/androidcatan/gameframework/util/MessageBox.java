package edu.up.cs.androidcatan.gameframework.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;

/**
 * Helper-class for showing dialog boxes
 * @author Andrew M. Nuxoll
 * @author Steven R. Vegdahl
 * @version July 2013
 *
 */
public class MessageBox {
    /**
     * popUpMessage, a handy method for putting a message box on the screen.
     *
     * @param msg
     *          the message to post
     * @param activity
     * 			the current activity. (Nothing will be shown if the activity is null.)
     */
    public static void popUpMessage(String msg, Activity activity) {
        if (activity == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", null);
        AlertDialog alert = builder.create();
        alert.show();
    }// popUpMessage

    /**
     * choiceBox, a handy method for putting a message box on the screen in which
     * the user is given two options
     *
     * <p>
     * CAVEAT: this dialog does not wait for user input, so the indication of how
     * to respond to the selection is done via attaching listeners (passed as parameters
     * below) to the method.
     *
     * @param msg
     * 			the message to post
     * @param posButtonText
     * 			the "positive" button text
     * @param negButtonText
     * 			the "negative" button text
     * @param posListener
     * 			the listener, if any, to activate when the positive
     * 			button is pressed
     * @param negListener
     * 			the listener, if any, to activate when the negative
     * 			button is pressed
     * @param activity
     * 			the current activity
     */
    public static void popUpChoice(String msg, String posButtonText,
                                   String negButtonText, OnClickListener posListener,
                                   OnClickListener negListener, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg);
        builder.setPositiveButton(posButtonText, posListener);
        builder.setNegativeButton(negButtonText, negListener);
        AlertDialog alert = builder.create();
        alert.show();
    }// popUpChoice
}
