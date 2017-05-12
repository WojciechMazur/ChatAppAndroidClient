package com.wickedmonkstudio.chatapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;
import com.wickedmonkstudio.chatapp.other.Message;
import com.wickedmonkstudio.chatapp.other.Utils;
import com.wickedmonkstudio.chatapp.other.WsConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button sendButton;
    private EditText inputMessage;

    private WebSocketClient client;

    private MessageListAdapter adapter;
    private List<Message> messageList;
    private ListView listViewMessages;

    private Utils utils;

    private String clientName=null;

    public static String URL_WEBSOCKET=null;
    private static final String TAG_SELF = "self",
                                TAG_NEW = "new",
                                TAG_MESSAGE="message",
                                TAG_EXIT="exit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton=(Button)findViewById(R.id.btnSend);
        inputMessage=(EditText)findViewById(R.id.inputMsg);
        listViewMessages=(ListView)findViewById(R.id.list_view_messages);

        utils = new Utils(getApplicationContext());

        Intent intent=getIntent();
        clientName=intent.getStringExtra("name");
        String serverAddress = intent.getStringExtra("address");
        String serverPort =intent.getStringExtra("port");

        URL_WEBSOCKET="ws://"+ serverAddress+":"+serverPort+"/ChatAppServer/chat?name=";

        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendMessageToServer(utils.getSendMessageJSON(inputMessage.getText().toString()));
                inputMessage.setText("");
            }
        });

        messageList = new ArrayList<>();
        adapter=new MessageListAdapter(this, messageList);
        listViewMessages.setAdapter(adapter);

        client=new WebSocketClient(URI.create(URL_WEBSOCKET + URLEncoder.encode(clientName)), new WebSocketClient.Listener(){

            @Override
            public void onConnect() {

            }

            @Override
            public void onMessage(String message) {
                Log.d(TAG, String.format("Got string message : %s", message));
                parseMessage(message);
            }

            @Override
            public void onMessage(byte[] data) {
                Log.d(TAG, String.format("Got binary message : %s", bytesToHex(data)));
            }

            @Override
            public void onDisconnect(int code, String reason) {
                String message = String.format(Locale.US, "Disconnected! Code: %d Reason: %s", code, reason);
                showToast(message);
                utils.storeSessionId(null);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error! : "+e);
                showToast("Error! : "+e);
            }
        },null);

        client.connect();
    }

    final protected static char[] hexArray="0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length*2];
        for(int i=0; i<bytes.length; i++){
            int v = bytes[i] & 0xFF;
            hexChars[i*2]=hexArray[v>>>4];
            hexChars[i*2+1]=hexArray[v&0x0F];
        }
        return new String(hexChars);
    }

    private void parseMessage(final String msg) {
        try{
            JSONObject object = new JSONObject(msg);
            String flag = object.getString("flag");
            if(flag.equalsIgnoreCase(TAG_SELF)){
                String sessionId = object.getString("sessionId");
                utils.storeSessionId(sessionId);
                Log.e(TAG, "Your session id: "+utils.getSessionId());
            }else if(flag.equalsIgnoreCase(TAG_NEW)){
                String name = object.getString("name");
                String message = object.getString("message");
                String onlineCount = object.getString("onlineCount");
                showToast(name + message + " Currently "+ onlineCount + " people online.");
            }else if(flag.equalsIgnoreCase(TAG_MESSAGE)){
                String fromName = clientName;
                String message = object.getString("message");
                String sessionId = object.getString("sessionId");
                boolean isSelf = true;

                if(!sessionId.equals(utils.getSessionId())){
                    fromName=object.getString("name");
                    isSelf=false;
                }

                Message m = new Message(fromName, message, isSelf);
                appendMessage(m);
            }else if(flag.equalsIgnoreCase(TAG_EXIT)){
                String name = object.getString("name");
                String message = object.getString("message");

                showToast(name+message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void appendMessage(final Message msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(msg);
                adapter.notifyDataSetChanged();
                playBeep();
            }
        });
    }

    private void playBeep() {
        try{
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone rington = RingtoneManager.getRingtone(getApplicationContext(), notification);
            rington.play();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(client !=null && client.isConnected()){
            client.disconnect();
        }
    }

    private void sendMessageToServer(String message) {
        if(client!=null &&client.isConnected()){
            client.send(message);
        }
    }


}
