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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */

//Reference: Android Developer : https://developer.android.com/develop/index.html
public class GroupMessengerActivity extends Activity {


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

            ServerSocket serverSocket =  new ServerSocket(SERVER_PORT);
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

                String msg= editText.getText().toString() + "\n";
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
            BufferedReader in;


            while (true){

                try {
                    appSocket = serverSocket.accept();
                    in = new BufferedReader( new InputStreamReader(appSocket.getInputStream()));
                    inMsg = in.readLine();
                    Log.e(TAG, "Message received by server And Message is" + "  -----" + inMsg);
                    publishProgress(inMsg);
                    in.close();
                    // appSocket.close();



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }finally {
                    try {
                        appSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }

        }


        protected void onProgressUpdate(String...strings) {

            String strReceived = strings[0].trim();
            TextView remoteTextView = (TextView) findViewById(R.id.receivedMsgView);
            remoteTextView.append(strReceived+"\n");


            //remoteTextView.append(strReceived + "\t\n");
            //remoteTextView.append("\n");

            Log.e(TAG, "Inside On Progress Update");


            // TextView mTextView;
            ContentResolver mContentResolver = getContentResolver();
            Log.e(TAG, "Before definition");
            Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger1.provider");
            ContentValues mContentValues;
            Log.e(TAG, "Before Cursor");

            //;

            Log.e(TAG, "After Cursor");
//            if (resultCursor == null) {
//                Log.e(TAG, "Result null");
//
//            }

            // int i = resultCursor.getCount();


            mContentValues = initTestValues(i, strReceived);
            mContentResolver.insert(mUri, mContentValues);
            i++;

            Log.e(TAG, "Inserted at key: " + i + " and Value is:" + mContentValues.get(VALUE_FIELD));
            Log.e(TAG, "Inserted the data :"+ mContentValues.getAsString(Integer.toString(i)) + " ");
//
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

            // Socket socket=null;
            PrintWriter out = null;
            Log.e(TAG, "Socket declared Client");

            try {

                // String remotePort2=REMOTE_PORT1;

                List<String> remotePort = new ArrayList<String>();


                remotePort.add(0, REMOTE_PORT1);
                remotePort.add(1, REMOTE_PORT2);
                remotePort.add(2, REMOTE_PORT3);
                remotePort.add(3, REMOTE_PORT4);
                remotePort.add(4, REMOTE_PORT0);

//                String remotePort = REMOTE_PORT0;
//                if (msgs[1].equals(REMOTE_PORT0))
//                    remotePort = REMOTE_PORT1;
                String msgToSend = msgs[0];

                for (int i = 0; i < 5; i++) {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), // By default chosen by Android system emulator for communication and is directed to 127.0.0.1 which is actual listening ip fro host
                            Integer.parseInt(remotePort.get(i)));
                    Log.e(TAG, "now sending to " + remotePort.get(i).toString());

                    out = new PrintWriter(socket.getOutputStream(), true);

                    out.println(msgToSend);
                    Log.e(TAG, "Sent");
                    out.flush();
                    //socket.close();

//                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), // By default chosen by Android system emulator for communication and is directed to 127.0.0.1 which is actual listening ip fro host
//                        Integer.parseInt(remotePort[1]));
//                out =new PrintWriter(socket.getOutputStream(), true);
//                out.println(msgToSend);
//
//                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), // By default chosen by Android system emulator for communication and is directed to 127.0.0.1 which is actual listening ip fro host
//                        Integer.parseInt(remotePort[2]));
//                out =new PrintWriter(socket.getOutputStream(), true);
//                out.println(msgToSend);
//
//                socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}), // By default chosen by Android system emulator for communication and is directed to 127.0.0.1 which is actual listening ip fro host
//                        Integer.parseInt(remotePort[3]));
//                out =new PrintWriter(socket.getOutputStream(), true);
//                out.println(msgToSend);


                    //out.flush();
                    //socket[i].close();
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

            } catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
}
