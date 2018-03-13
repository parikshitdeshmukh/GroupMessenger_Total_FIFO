package edu.buffalo.cse.cse486586.groupmessenger2;

import android.util.Log;

import java.util.PriorityQueue;

import static edu.buffalo.cse.cse486586.groupmessenger2.GroupMessengerActivity.TAG;

/**
 * Created by parik on 3/7/18.
 */

//References: https://docs.oracle.com/javase/7/docs/api/java/lang/Comparable.html

public class Message implements Comparable<Message> {

    String msg;
    String id;
    boolean ready;
    boolean newMsg;

    public Message() {
    }

    public Message(String msg, String id, boolean ready, boolean newMsg) {
        this.msg = msg;
        this.id = id;
        this.ready=ready;
        this.newMsg = newMsg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }


    public boolean isNewMsg() {
        return newMsg;
    }

    public void setNewMsg(boolean newMsg) {
        this.newMsg = newMsg;
    }

    @Override
    public int compareTo(Message another) {
//        Log.d(TAG, " Inside CompareTo: "+ this.id);
//        Log.d(TAG, "Inside Compare TO : "+ another.id);

        if (this.id.equals(another.id)){
//            Log.d(TAG, " Inside CompareTo: "+ this.id);
//            Log.d(TAG, "Inside Compare TO : "+ another.id);
            return 0;
        }else if (Double.parseDouble(this.id) > Double.parseDouble(another.id)){
            return 1;
        }else return -1;
    }

    @Override
    public String toString() {
        return msg + ","+id+","+ready+","+newMsg + "\n";
    }


    @Override
    public boolean equals(Object o) {

        if(o instanceof Message){
            Message m = (Message) o;
            return  m.getMsg().equals(msg);
        }

        return false;
    }
}
