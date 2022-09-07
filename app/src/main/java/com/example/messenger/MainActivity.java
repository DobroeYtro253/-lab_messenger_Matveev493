package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.text.format.Formatter;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    DatagramSocket socket;
    TextView his;
    TextView message;
    TextView ip;
    TextView receivingPort;
    TextView port;
    DB mydb;
    String BDip;
    String BDport;
    String BDrecport;
    ArrayList<address> lst = new ArrayList<>();
    ArrayAdapter<address> adp;

    void update_list()
    {
        lst.clear();

        adp.notifyDataSetChanged();
    }
    byte[] receive_buffer = new byte[100];
    byte[] send_buffer = new byte[500];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        g.address = new DB(this, "address.db", null, 1);


        ip = findViewById(R.id.txt_address);
        port = findViewById(R.id.txt_port);
        receivingPort = findViewById(R.id.txt_ResPort);

        try {
            BDip = g.address.selectAddress("ip", "0");
            BDport = g.address.selectAddress("port", "0");
            BDrecport = g.address.selectAddress("recport", "0");
            ip.setText(BDip);
            port.setText(BDport);
            receivingPort.setText(BDrecport);
        }
        catch (Exception e){}




        his = findViewById(R.id.txtHistory);


        try {
            Integer ResPort = Integer.parseInt(receivingPort.getText().toString());
            InetAddress local_network = InetAddress.getByName("0.0.0.0");
            SocketAddress local_address = new InetSocketAddress(local_network, ResPort);
            socket = new DatagramSocket(null);

            socket.bind(local_address);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }


        receivingPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {

                    Integer ResPort = Integer.parseInt(receivingPort.getText().toString());
                    InetAddress local_network = InetAddress.getByName("0.0.0.0");
                    SocketAddress local_address = new InetSocketAddress(local_network, ResPort);
                    socket = new DatagramSocket(null);

                    socket.bind(local_address);
                } catch (UnknownHostException | SocketException e) {
                    e.printStackTrace();
                }
            }
        });
        Runnable receiver = new Runnable() {
            @Override
            public void run() {
                Log.e("TEST", "Received thread is running");
                DatagramPacket received_packet = new DatagramPacket(receive_buffer, receive_buffer.length);
                while (true)
                {
                    try {
                        socket.receive(received_packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String s = new String(received_packet.getData(), 0, received_packet.getLength());
                    Log.e("TEST", "Received: = " + s);

                    runOnUiThread(() -> {
                        String timeText = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        his.append(  "\n" +"(" + timeText + ")" +s);});

                }
            }
        };
        Thread receiving_thread = new Thread(receiver);
        receiving_thread.start();
    }
    protected void onDestroy()
    {
        super.onDestroy();
        ContentValues cv = new ContentValues();

        BDip = ip.getText().toString();
        BDport = port.getText().toString();
        BDrecport = receivingPort.getText().toString();
        try {
            g.address.deleteAddress("0");

        }
        catch (Exception e)
        {

        }
        g.address.addAddress("0", BDip, BDport, BDrecport);


    }

    DatagramPacket send_packet;

    public void on_save(View v)
    {

    }
    public void on_load(View v)
    {


    }
    public void on_click(View v)
    {
        EditText ta = findViewById(R.id.txt_address);
        String ip = ta.getText().toString();
        EditText tp = findViewById(R.id.txt_port);
        int port = Integer.parseInt(tp.getText().toString());

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String address = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

       message = findViewById(R.id.txtMessage);
       String mes = address + ":" + port + " - " + message.getText().toString();

        send_buffer = mes.getBytes();
        try {

           InetAddress remote_address = InetAddress.getByName(ip);
           send_packet = new DatagramPacket(send_buffer, send_buffer.length, remote_address, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }



        send_packet.setLength(mes.length());

        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.e("TEST", "Sending thread is running");
                try {
                    socket.send(send_packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread sending_thread = new Thread(r);
        sending_thread.start();


    }
}