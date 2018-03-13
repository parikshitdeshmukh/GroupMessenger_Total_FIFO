package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */

/** References: Android Developer : https://developer.android.com/develop/index.html
 *  Priority Queue : https://docs.oracle.com/javase/7/docs/api/java/util/PriorityQueue.html
 *  Input/Output streams: https://docs.oracle.com/javase/7/docs/api/java/io/InputStream.html
 * Content Provider : https://developer.android.com/guide/topics/providers/content-providers.html
 * Content Resolver : https://developer.android.com/reference/android/content/ContentResolver.html
 * https://docs.oracle.com/javase/7/docs/api/java/lang/Exception.html
 *
 * */
public class GroupMessengerActivity extends Activity {

    static int globalCount=0;
    static final String TAG = GroupMessengerActivity.class.getSimpleName(); /// for getting simple name of the class for logger
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final int SERVER_PORT = 10000;
    final String KEY_FIELD = "key";
    final String VALUE_FIELD = "value";
    static int i=0;
    PriorityQueue<Message> q= new PriorityQueue<Message>();
    static List<String> remotePort = new ArrayList<String>();
    static Map<String, String> procId= new HashMap<String, String>();
    static String crashedAVDPort="";
    static boolean isCrashed = false;
    static int limit=5;

//
//    public static void setRemotePort1(List<String> remotePort1) {
//        GroupMessengerActivity.remotePort1 = remotePort1;
//        remotePort1.add(REMOTE_PORT0);
//        remotePort1.add(REMOTE_PORT1);
//        remotePort1.add(REMOTE_PORT2);
//        remotePort1.add(REMOTE_PORT3);
//        remotePort1.add(REMOTE_PORT4);
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        procId.put(REMOTE_PORT0,"0");
        procId.put(REMOTE_PORT1,"1");
        procId.put(REMOTE_PORT2,"2");
        procId.put(REMOTE_PORT3,"3");
        procId.put(REMOTE_PORT4,"4");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String telLineNo = tel.getLine1Number();
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);

        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        // final EditText editText = (EditText) findViewById(R.id.editText1);
//        String msg= editText.getText().toString() + "\n";
//        editText.setText("");
        TextView tv = (TextView) findViewById(R.id.sentMsgView);
//        tv.append("\t"+msg);
//
        tv.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));


        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        //Server Socket Creation code
        try{

            Log.e(TAG, "Before creating server socket");
            ServerSocket serverSocket =  new ServerSocket(SERVER_PORT);
            Log.e(TAG, "After creating server socket");
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket); //Gets it's own thread which can run indefinitely


        }catch (IOException e){
            Log.e(TAG, "Not able to create Server Socket");

        }
        final EditText editText = (EditText) findViewById(R.id.editText1);

//        editText.setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
//                        (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
//                    findViewById(R.id.button4).performClick();
//
//
//
//
//                    return true;
//
//                }
//                return false;
//
//                }
//            });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg= editText.getText().toString();
                editText.setText("");
                // TextView tv = (TextView) findViewById(R.id.textView1);
                // tv.append("\t"+msg);

                // tv.append("\n");


                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
            }
        });



        //Button send = (Button) findViewById(R.id.button4);
//        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                String msg= editText.getText().toString() + "\n";
//                editText.setText("");
//               // TextView tv = (TextView) findViewById(R.id.textView1);
//                // tv.append("\t"+msg);
//
//                // tv.append("\n");
//
//
//                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
//
//            }
//        });

//        send.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                String msg= editText.getText().toString() + "\n";
//                editText.setText("");
//                TextView tv = (TextView) findViewById(R.id.textView1);
//               // tv.append("\t"+msg);
//
//               // tv.append("\n");
//
//
//                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
//
//
//            }
//
//        });

    }

    private class  ServerTask extends  AsyncTask<ServerSocket, String, Void>{


        @Override
        protected Void doInBackground(ServerSocket... sockets) {

            String inMsg;
            ServerSocket serverSocket=sockets[0];
            Socket appSocket=null;
            DataInputStream in;

                try {
                    while (true) {

                        Message msg = new Message();

                        appSocket = serverSocket.accept();
                        Log.e(TAG, "Entered the server side");
                        in = new DataInputStream(appSocket.getInputStream());
                        inMsg = in.readUTF();

                        Log.e(TAG, "Message received by server socket:" + "Local Port" + appSocket.getLocalPort() + " And Message is " + inMsg);
                        Log.e(TAG, inMsg);

                        // Log.e(TAG, String.valueOf(inMsg.getId()));

                        String[] objs = inMsg.split(",");
                        Log.e(TAG, "splitted data " + inMsg.toString());
                        Log.d(TAG, "NewMReceived" + inMsg);
                        if (objs[0].equals("Failed")) {
                            crashedAVDPort = objs[1].trim();
                            removeFromQ(procId.get(crashedAVDPort));
                            DataOutputStream out = new DataOutputStream(appSocket.getOutputStream());
                            out.writeUTF("Received OK");
                            out.flush();

                        }
                        else {

                            msg.setMsg(objs[0]);
                            msg.setId(objs[1]);
                            msg.setReady(Boolean.parseBoolean(objs[2].trim()));
                            msg.setNewMsg(Boolean.parseBoolean(objs[3].trim()));

                            if (msg.isNewMsg()) {
                                globalCount++;
                                msg.setId(String.valueOf(globalCount) + "." + objs[1]);
                                q.add(msg);
                                DataOutputStream out = new DataOutputStream(appSocket.getOutputStream());
                                Log.e(TAG, "Sending from server side " + String.valueOf(globalCount));
                                out.writeUTF(msg.toString());
                                out.flush();
                                // out.close();
                                Log.d(TAG, "Sending first step object to client" + msg.toString());
                                //appSocket.close();
                            }
                            if (!msg.isNewMsg()) {

                                Log.e(TAG, "Not a new msg");
                                // appSocket = serverSocket.accept();
                                q.remove(msg);
                                q.add(msg);
                                globalCount = Math.max(Integer.parseInt(msg.getId().split("\\.")[0]), globalCount);
                                globalCount++;
                                Log.d(TAG, "updated_globalcount" + globalCount);
                                DataOutputStream out = new DataOutputStream(appSocket.getOutputStream());
                                Log.e(TAG, "Sending final response to client as: Received OK");
                                out.writeUTF("Received OK");
                                out.flush();
                                // out.close();

                            }
                        }

                        appSocket.close();
                        PriorityQueue<Message> q2 = new PriorityQueue<Message>(q);

                        Log.d(TAG, " This is queue2");
                        queueData(q2);

                        while (!q.isEmpty()) {
                            if (q.peek().isReady()) {

                                Log.e(TAG, "Printing queue: " + q.peek().toString());
                                Message s = q.poll();
                                Log.d("QUEUE", s.getMsg().trim());
                                String st = s.getMsg().trim();
                                Log.d("QUEUE", st);


                                publishProgress(st);
                            } else
                                break;

                        }

                        PriorityQueue<Message> q3 = new PriorityQueue<Message>(q);

                        Log.d(TAG, " This is queue3");
                        queueData(q3);


                        //  appSocket.close();


                        // publishProgress(inMsg);
                        //  in.close();


                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("EXC", "Inside Socket Exc :" + e.getMessage());
//                    Log.e("EXC", "Error due to Server socket certain issue");
                }

                return null;

        }



        public void queueData(PriorityQueue<Message> q){
            Log.d(TAG," This is queue");

            while (!q.isEmpty()){

                Log.d(TAG, "This is queue data :"+ q.poll().toString() );

            }
        }


        protected void onProgressUpdate(String...strings) {

            Log.e(TAG, "Printing msg to be published: "+ strings[0] );

            String strReceived = strings[0].trim();
            TextView remoteTextView = (TextView) findViewById(R.id.receivedMsgView);
            remoteTextView.append(strReceived+"\n");


            //remoteTextView.append(strReceived + "\t\n");
            //remoteTextView.append("\n");

            Log.e(TAG, "Inside On Progress Update");


            // TextView mTextView;
           ContentResolver mContentResolver = getContentResolver();
            Log.e(TAG, "Before definition");
           Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger2.provider");
           ContentValues mContentValues;
            Log.e(TAG, "Before Cursor");

            //;

            Log.e(TAG, "After Cursor");
//            if (resultCursor == null) {
//                Log.e(TAG, "Result null");
//
//            }


            mContentValues = initTestValues(i, strReceived);
            mContentResolver.insert(mUri, mContentValues);
            i++;

            Log.e(TAG, "Inserted at key: " + i + " and Value is:" + mContentValues.get(VALUE_FIELD));
            Log.e(TAG, "Inserted the data :"+ mContentValues.getAsString(Integer.toString(i)) + " ");
//
//
        }


    }

    private ContentValues initTestValues(int i, String msg) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_FIELD, i);
        cv.put(VALUE_FIELD, msg);
        return cv;
    }

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }


    /***
     * ClientTask is an AsyncTask that should send a string over the network.
     * It is created by ClientTask.executeOnExecutor() call whenever OnKeyListener.onKey() detects
     * an enter key press event.
     *
     * @author stevko
     *
     */
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {

            //Socket socket=null;
            DataOutputStream out = null;
            // Message toRead = null;
            Log.e(TAG, "Socket declared Client");
            int localCount = 0;
            int testExc = 0;

            List<String> remotePort = new ArrayList<String>();
            remotePort.add(REMOTE_PORT0);
            remotePort.add(REMOTE_PORT1);
            remotePort.add(REMOTE_PORT2);
            remotePort.add(REMOTE_PORT3);
            remotePort.add(REMOTE_PORT4);


            try {

                Message msgToSend = new Message();

                for (int i = 0; i < 5; i++) {
                    if (!remotePort.get(i).equals(crashedAVDPort)) {
                        try {
                            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), // By default chosen by Android system emulator for communication and is directed to 127.0.0.1 which is actual listening ip fro host
                                    Integer.parseInt(remotePort.get(i)));

//                            Log.d("EXC", "Value of test var: " + testExc);

                            Log.e(TAG, "Sending first step object to other clients" + remotePort.get(i));

                            out = new DataOutputStream(socket.getOutputStream());
                            msgToSend.setMsg(msgs[0].trim());
                            msgToSend.setReady(false);
                            msgToSend.setNewMsg(true);
                            msgToSend.setId(procId.get(msgs[1]));
                            out.writeUTF(msgToSend.toString());
                            Log.e(TAG, "Sent message " + msgToSend.toString());
                            out.flush();
                            // out.close();

                            DataInputStream in = new DataInputStream(socket.getInputStream());
//                            Log.e(TAG, "------------Ready to listen on client side" + "  -----");

                            String toRead = in.readUTF();
                            Log.e(TAG, "Message received by Client " + remotePort.get(i) + " second time And Message is" + "  -----" + toRead);
                            //in.close();

                            String[] objs = toRead.split(",");
                            int c = Integer.parseInt(objs[1].split("\\.")[0]);
                            if (c > localCount) {
                                localCount = c;
                            }

//                            Log.e(TAG, "printing st object-----------" + msgToSend.toString());

//                     if(localCount > Double.parseDouble(msgToSend.getId().split("\\.")[0]))
//                     msgToSend.setId(String.valueOf(localCount));
                            socket.close();

                        } catch (SocketException e) {
                            isCrashed = true;
                            crashedAVDPort = remotePort.get(i);
                            Log.d("EXC", "Inside Socket Exception :" + e.getMessage());

                        } catch (IOException e) {
                            isCrashed = true;
                            crashedAVDPort = remotePort.get(i);


                            Log.e(TAG, "Error in creating socket | Failed at Client side due to | Inside IOException : " + e.getMessage());
//
//                            Log.d("EXC", "Timeout thrown: " + testExc);
//                            testExc = 999;
//                            Log.d("EXC", "Changing varible here : " + testExc);
//                            if (e instanceof SocketTimeoutException) {
//                                Log.d("EXC", "Instance of Socket timetout");
//                            }
//                            if (e instanceof InterruptedIOException) {
//
//                                Log.d("EXC", "Instance of Interrupted IO ");
//                            }

                        }
                    }
                    //out.flush();
                }

                if (isCrashed) {

                        for (int i = 0; i < limit; i++) {
                            if (!remotePort.get(i).equals(crashedAVDPort)) {
                                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), // By default chosen by Android system emulator for communication and is directed to 127.0.0.1 which is actual listening ip fro host
                                        Integer.parseInt(remotePort.get(i)));
                                Log.e(TAG, "Sending crashed AVDs port number to other AVDs| Port number is :" + remotePort.get(i).toString());

                                out = new DataOutputStream(socket.getOutputStream());                  //  out.writeObject(toRead);
                                Log.e(TAG, "Failed port " + crashedAVDPort + " sent");
                                out.writeUTF("Failed," + crashedAVDPort + "\n");
                                out.flush();
                                DataInputStream in = new DataInputStream(socket.getInputStream());
                                //                        try {
                                String x = in.readUTF();
                                Log.e(TAG, "Received OK from server " + x);

                                //                            System.out.print(x);
                                //
                                //                        } catch (Exception e) {
                                //
                                //                            System.out.println(e);
                                //                        }
                                if (x.trim().equals("Received OK")) {
                                    in.close();
                                    socket.close();
                                }

                            }
                            isCrashed = false;
                        }

                }

                // toRead.setReady(true);

                msgToSend.setMsg(msgs[0].trim());
                msgToSend.setId(localCount + "." + procId.get(msgs[1]));
                msgToSend.setNewMsg(false);
                msgToSend.setReady(true);


                for (int i = 0; i < 5; i++) {
                    if (!remotePort.get(i).equals(crashedAVDPort)) {
                        try {
                            Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), // By default chosen by Android system emulator for communication and is directed to 127.0.0.1 which is actual listening ip fro host
                                    Integer.parseInt(remotePort.get(i)));
                            Log.e(TAG, "Sending agreed msg to other AVDs :  " + remotePort.get(i).toString());

                            out = new DataOutputStream(socket.getOutputStream());                  //  out.writeObject(toRead);
                            Log.e(TAG, "Sent");
                            out.writeUTF(msgToSend.toString());
                            out.flush();
                            // out.close();
                            Log.d(TAG, "Sent final object " + msgToSend.toString());
                            DataInputStream in = new DataInputStream(socket.getInputStream());
//                        try {
                            String x = in.readUTF();
                            Log.e(TAG, "Received OK from server " + x);

//                            System.out.print(x);
//
//                        } catch (Exception e) {
//
//                            System.out.println(e);
//                        }
                            if (x.trim().equals("Received OK")) {
                                in.close();
                                socket.close();
                            }
                        } catch (SocketException se) {
                            isCrashed = true;
                            crashedAVDPort = remotePort.get(i);

                            Log.e(TAG, " Not creating socket second time : " + se.getMessage());
                        } catch (IOException io) {
                            isCrashed = true;
                            crashedAVDPort = remotePort.get(i);

                        }

                    }
                }

//
//                if (isCrashed){
//                    removeFromQ(q,procId.get(crashedAVDPort));
//                    //isCrashed=false;
//
//                }


                if (isCrashed) {

                        for (int i = 0; i < limit; i++) {
                            if (!remotePort.get(i).equals(crashedAVDPort)) {
                                Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), // By default chosen by Android system emulator for communication and is directed to 127.0.0.1 which is actual listening ip fro host
                                        Integer.parseInt(remotePort.get(i)));
                                Log.e(TAG, "Sending crashed AVDs port number to other AVDs| Port number is : " + remotePort.get(i).toString());

                                out = new DataOutputStream(socket.getOutputStream());                  //  out.writeObject(toRead);
                                Log.e(TAG, "Sent");
                                out.writeUTF("Failed," + crashedAVDPort + "\n");
                                out.flush();

                                DataInputStream in = new DataInputStream(socket.getInputStream());
                                //                        try {
                                String x = in.readUTF();
                                Log.e(TAG, "Received OK from server " + x);

                                //                            System.out.print(x);
                                //
                                //                        } catch (Exception e) {
                                //
                                //                            System.out.println(e);
                                //                        }
                                if (x.trim().equals("Received OK")) {
                                    //                                try {
                                    in.close();
                                    //                                } catch (IOException e) {
                                    //                                    e.printStackTrace();
                                    //                                }
                                    //                                try {
                                    socket.close();
                                    //                                } catch (IOException e) {
                                    //                                    e.printStackTrace();
                                    //                                }
                                }
                            }
                            isCrashed = false;
                        }


                }
                /*
                 * TODO: Fill in your client code that sends out a message- Client code written below:
                 * Reference: https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
                 * I tried to use DataOutputStream for writing to the client AVD however it wasn't working. SO I used PrintWritter class for output and took reference from the above mentioned oracle citing.
                 */
//                out =new PrintWriter(socket[0].getOutputStream(), true);
//                out.println(msgToSend);
//
//                    out =new PrintWriter(socket[1].getOutputStream(), true);
//                    out.println(msgToSend);
//                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//                out.writeChars(msgToSend);


                //  out.close();
                //  socket.close(); //Commented for some random issue; But, Socket shud be always closed in general

            } catch (Exception e) {
                Log.e(TAG, "ClientTask UnknownHostException :" + e.getMessage());
            }
//             finally {
//                if (socket!=null) {
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }

            return null;
        }
    }

    private void removeFromQ(String procId) {

        Iterator<Message> itr= q.iterator();
        while (itr.hasNext()){
            Message m=itr.next();
            if (m.getId().split("\\.")[1].equals(procId)){
                Log.d("EXC", "The object to be removed :" + m.toString());
                q.remove(m);
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
