package moderwarfareapp.futurewarfare;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by andrea on 14/05/16.
 */
public class BluetoothThread extends Thread{
    private Handler handler;                    //handler required to exchange information with the main activity
    private BluetoothSocket btSocket;           //socket required for connection and exchange of messages
    private OutputStream outStream;             //required for sending of messages
    private InputStream inStream;               //required for receiving of messages
    private boolean firstIteration = true;      //required to send Arduino the kindOfGame
    private boolean run = true;

    //this thread needs only handler, furthermore, the bluetooth socket (to exchange bluetooth message) is taken from GlobalValue
    public BluetoothThread (Handler handler){
        this.handler = handler;
        btSocket = GlobalValue.getInstance().getSocket();
        if(btSocket!=null) {
            try {
                inStream = btSocket.getInputStream();
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run (){
        while(run){
            if(firstIteration) {        //if the game is just started, notify the kindOfGame
                firstIteration = false;
                if (GlobalValue.getInstance().getKindOfGame() == "Death Match"){
                    sendMessageBluetooth("4");
                }
                else {
                    sendMessageBluetooth("5");
                }
            }

            sendMessageBluetooth("1");          //with 1 i ask remaining number of shots
            String colpi = receiveMessageBluetooth();
            notifyMessage("colpi",colpi);

            sendMessageBluetooth("2");          //with 2 i ask if i'm dead: it response with 1 if i'm dead, 0 otherwise
            String morto = receiveMessageBluetooth();
            notifyMessage("morto",morto);


        }
    }

    //this method exchange information with MapsActivity thanks to handler
    private void notifyMessage(String notify, String str) {
        //thread sends this message to MapsActivity
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("colpi", ""+ str);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    //when the game ends thread must be stopped
    public void stopThread (){
        run = false;
    }

    //this method allow us to send a message via bluetooth
    private void sendMessageBluetooth(String message) {
        if (outStream == null) {
            return;
        }
        byte[] msgBuffer = message.getBytes();
        try{
            outStream.write(msgBuffer);
        }
        catch (IOException e){
            e.getStackTrace();
        }
    }

    //this method allow us to receive a message via bluetooth
    private String receiveMessageBluetooth() {
        try {
            Thread.sleep(2000);             //We wait a few seconds before request the response
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        byte[] buffer = new byte[512];
        int bytes;
        StringBuilder readMessage = new StringBuilder();
        String recv = "";
        boolean fine = true;
        while (fine) {
            try {
                Thread.sleep(1000);
                bytes = inStream.read(buffer);
                String readed = new String(buffer, 0, bytes);
                readMessage.append(readed);

                if (readed.contains("\n") || readed.contains("\0")) {
                    fine = false;
                    recv = readMessage.toString();
                    readMessage.setLength(0);
                }

            } catch (IOException e) {
                e.getStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return recv;
    }
}
