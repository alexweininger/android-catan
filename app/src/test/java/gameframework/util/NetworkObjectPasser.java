package gameframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

/**
 * A class that supports two-way passing of objects across the network,
 * using serialization.
 *
 * @author Steven R. Vegdahl
 * @version July 2013
 *
 */
public abstract class NetworkObjectPasser {
    /**
     * instance variables
     */

    // a queue for collecting objects that "sent" to this object
    // before the network connection is established
    private Queue<Object> objQueue = new LinkedList<Object>();

    // the handler the objects "sending thread"
    private Handler sendHandler;

    // the streams for reading and writing objects from/to the network
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

//	private static int numRunsLocal = 0;

    //	// whether our connection has been established, so that we can therefore
//	// send messages
    private boolean ready = false;

    /**
     * constructor
     *
     * @param ipAddress
     * 		the IP address of the device to which we want to connect as a client,
     * 		or null we are acting as the server
     * @param port
     * 		the port number though which to connect
     */
    public NetworkObjectPasser(String ipAddress, int port) {

        // Create thread that waits for the connection to be
        // established from the network. This will ultimately become
        // the thread that is reading objects from the network.
        CreateRunner createRunner = new CreateRunner(ipAddress, port);
        Thread thread2 = new Thread(createRunner);
        thread2.setName("Network receive-handler");
        thread2.start();

        // Loop, waiting for the status (from the just-created thread)
        // to not be "waiting". This will occur when the connection
        // is established.
        while (createRunner.getStatus() == RunnerStatus.WAITING) {
            Thread.yield();
        }

        // create/run a thread and handler for sending objects
        // via the network
        Runnable runnable = new Runnable() {
            public void run() {
                Looper.prepare();
                sendHandler = new Handler();
                Looper.loop();
            }
        };
        Thread thread = new Thread(runnable);
        thread.setName("Network send-handler");
        thread.start();

        // wait for sendHandler to be set
        while (sendHandler == null) {
            Thread.yield();
        }
    }

    public abstract void onReceiveObject(Object obj);

    private enum RunnerStatus {
        WAITING, READY, FAILED;
    }

    private class CreateRunner implements Runnable {
        private String ipAddress; // the ipAddress of server (or null if we are server)
        private int port; // the port
        private RunnerStatus status;

        // constructor
        public CreateRunner(String ipAddress, int port) {
            this.ipAddress = ipAddress;
            this.port = port;
            this.status = RunnerStatus.WAITING;
        }

        public RunnerStatus getStatus() {
            return status;
        }

        // run-method, which runs in the separate thread
        public void run() {
            Log.i("NetworkObjectPasser", "starting run method at bottom");

            // the socket connection
            Socket socket = null;

            // catch I/O exceptions
            try {
                if (ipAddress == null) {
                    // IP address is null, indicating that we are a server
                    Log.i("NetworkObjectPasser", "about to create server socket");

                    // get (possibly creating) the server socket for this port
                    ServerSocket ss = ServerSocketMap.getServerSocket(port);

                    // set our internally-visible status to be "ready"
                    status = RunnerStatus.READY;

                    // wait for a client to connect to us
                    Log.i("NetworkObjectPasser", "server attempt at port "+port);
                    socket = ss.accept();

                    // register that we are finished with the server socket
                    ServerSocketMap.release(port);
                    Log.i("NetworkObjectPasser", "server connect at port "+port);

                    // set our externally-visible status to be "ready"
                    ready = true;
                }
                else {
                    // create as client socket
                    Log.i("NetworkObjectPasser", "client attempt at port "+port);

                    // set our internal status to be "ready"
                    status = RunnerStatus.READY;

                    // wait for a connection to a server
                    socket = new Socket(ipAddress, port);

                    // set out externally-visible status to be "ready"
                    ready = true;
                    Log.i("NetworkObjectPasser", "client connected at port "+port);
                }
            } catch (IOException e) {
                // if we could not make the connection, set our status to "failed" and return
                status = RunnerStatus.FAILED;
                Log.d(e.getClass()+"", e.getMessage());
                return;
            }

            // create the input and output streams; also send already queued objects
            synchronized (this) {

                try {
                    // create the input and output streams, and flush the output stream
                    InputStream inBasic = socket.getInputStream();
                    OutputStream outBasic = socket.getOutputStream();
                    out = new ObjectOutputStream(outBasic);
                    in = new ObjectInputStream(inBasic);
                    out.flush();
                }
                catch (IOException e) {
                    // if exception, return
                    status = RunnerStatus.FAILED;
                    return;
                }

                // send out all queued-up objects
                while (!objQueue.isEmpty()) {
                    Object obj = objQueue.remove();
                    try {
                        out.writeObject(obj);
                        out.flush();
                    } catch (IOException e) {
                        Log.e("NetworkObjectPasser", "could not write object");
                    }
                }
            }

            // go into our read-object loop, passing the object to our user by
            // invoking the user's 'onReceiveObject' method on each object
            for (;;) {
                try {
                    Log.i("NetworkObjectPasser", "ready to read object");
                    Object obj = in.readObject();
                    Log.i("NetworkObjectPasser", "object read ("+obj.getClass()+")");
                    onReceiveObject(obj);
                }
                catch (Exception x) {
                    break;
                }
            }
        }
    }

    /**
     * send an object to this NetworkObjectPasser object. This will have the
     * effect of getting the object sent across the network
     *
     * @param obj
     * 		the object to send
     */
    public void sendObject(Object obj) {
        // schedule the "send" in the object's "sending" thread
        Runnable run = new MsgRunnable(obj);
        sendHandler.post(run);
    }

    /**
     * helper class for sending objects using a dedicated thread
     * @author Steven R. Vegdahl
     * @version July 2013
     *
     */
    private class MsgRunnable implements Runnable {

        // the object we're going to send
        private Object obj;

        // constructor
        public MsgRunnable(Object obj) {
            this.obj = obj;
        }

        // run method, which writes out the object or, if unsuccessful,
        // queues the object up for sending later
        public void run() {
            synchronized(this) {
                boolean success = false;
                if (out != null) {
                    try {
                        // write object
                        out.writeObject(obj);
                        success = true;
                    } catch (IOException e) {
                        Log.e("NetworkObjectPasser", "could not write object");
                    }
                }
                if (!success) {
                    // could not write object, so queue it up
                    objQueue.add(obj);
                }
            }
        }
    }

    /**
     * Asks whether our object is ready
     *
     * @return
     * 		whether the object is ready
     */
    public boolean isReady() {
        // check if we're ready; if not, poll a few times before
        // giving up
        for (int i = 0; i < 10; i++) {
            if (ready) return true;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

        // could never establish that we're ready
        return false;
    }

    /**
     * Helper-class to coordinate multiple servers that might want to connect
     * through the same port number.
     *
     * @author Steven R. Vegdahl
     * @version July 2013
     *
     */
    private static class ServerSocketMap {

        // A hashtable that keeps track of all port numbers for which server sockets
        // have already been created. It maps the port number to the pair
        //   - server socket
        //   - number of current users of the server socket
        private static Hashtable<Integer,Pair<ServerSocket,Integer>> map =
                new Hashtable<Integer,Pair<ServerSocket,Integer>>();

        /**
         * Gets a server socket for the given port number. It returns an existing one
         * if it exists, otherwise, it creates a new one.
         *
         * @param portNum
         * 		the port number
         * @return
         * 		the server socket for that port number
         */
        public static ServerSocket getServerSocket(int portNum) {
            // the server socket, if any, and the number of current user
            // of that socket
            Pair<ServerSocket,Integer> pair;

            synchronized(map) {

                // get the entry for this port number
                pair = map.get(portNum);

                if (pair == null) {
                    // no entry exists; create the server socket and register it
                    // in the hash table, with a count of 1
                    ServerSocket ss;
                    try {
                        ss = new ServerSocket(portNum);
                    } catch (IOException e) {
                        return null;
                    }
                    pair = new Pair<ServerSocket,Integer>(ss,1);
                    map.put(portNum, pair);
                }
                else {
                    // entry exists; increment its user-count
                    Pair<ServerSocket,Integer> newPair = new Pair<ServerSocket,Integer>(pair.first, pair.second+1);
                    map.put(portNum, newPair);
                }
            }

            // return the socket that was found/created
            return pair.first;
        }

        /**
         * Releases a server socket (with respect to the calling user)
         *
         * @param portNum
         * 		// the port number whose server socket is to be released
         */
        public static void release(int portNum) {
            synchronized(map) {
                // get the socket/count pair
                Pair<ServerSocket,Integer> pair = map.get(portNum);

                // double-check that the entry was there (it should always be,
                // unless our caller is buggy
                if (pair == null) return; // should never happen if used properly

                // decrement the count and, if it's gone down to zero, close the
                // server socket and remove the entry from our hash table and
                ServerSocket ss = pair.first;
                int newVal = pair.second-1;
                Pair<ServerSocket,Integer> newPair =
                        new Pair<ServerSocket,Integer>(ss, newVal);
                map.put(portNum, newPair);
                if (newVal <= 0) {
                    map.remove(portNum);
                    try {
                        ss.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
}

