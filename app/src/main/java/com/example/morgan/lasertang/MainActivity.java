package com.example.morgan.lasertang;

import android.content.Intent;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, View.OnDragListener {

    Button moveBtn;
    Button shootBtn;
    SeekBar servo;

    BluetoothSocket clientSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moveBtn = (Button) findViewById(R.id.move_btn);
        shootBtn = (Button) findViewById(R.id.shoot_btn);

        servo = (SeekBar) findViewById(R.id.seekBar);

        moveBtn.setOnClickListener(this);
        shootBtn.setOnClickListener(this);

        moveBtn.setOnTouchListener(this);
        // moveBtn.setOnDragListener(this);

        String enableBT = BluetoothAdapter.ACTION_REQUEST_ENABLE;
        startActivityForResult(new Intent(enableBT), 0);

        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        try {
            BluetoothDevice device = bluetooth.getRemoteDevice("00:13:02:01:00:09");
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            clientSocket = (BluetoothSocket) m.invoke(device, 1);
            clientSocket.connect();
        } catch (IOException |
                SecurityException |
                IllegalArgumentException |
                NoSuchMethodException |
                InvocationTargetException |
                IllegalAccessException e) {
            Log.d("BLUETOOTH", e.getMessage());
        }

        Toast.makeText(getApplicationContext(), "CONNECTED", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        try {
            OutputStream outStream = clientSocket.getOutputStream();
            int value = 0;
            if (v == shootBtn) {
                value = 1;
            }
            outStream.write(value);
        } catch (IOException e) {
            Log.d("BLUETOOTH", e.getMessage());
        }
    }

    private int mActivePointerId = -1;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int pointerIndex = 0;
        MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();
        event.getPointerCoords(pointerIndex, coords);
        TextView tX = (TextView) findViewById(R.id.textView);
        tX.setText(coords.x+"");
        TextView tY = (TextView) findViewById(R.id.textView2);
        tY.setText(coords.y+"");
        float cX = v.getX() - v.getWidth() / 2 + coords.x;
        float cY = v.getY() - v.getHeight() / 2 + coords.y;
        v.setX(cX);
        v.setY(cY);
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        float x = event.getX();
        float y = event.getY();
        Toast.makeText(getApplicationContext(), x+":"+y, Toast.LENGTH_LONG).show();
        return false;
    }
}
