package com.example.smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.util.Log;

import android.view.View;

import android.widget.ImageView;



import com.chaos.view.PinView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL_ID = "Smart Home";
    private DatabaseReference mDatabase;
    public static final String GREEN = "LED_GREEN";
    public static final String BLUE = "LED_BLUE";
    public static final String YELLOW = "LED_YELLOW";
    public static final String DOOR = "DOOR";
    public static final String PIN_CODE = "PIN_CODE";
    public static final String SECURITY = "SECURITY";
    public static final String SENSOR = "SENSOR";
    public static final String FIRE_SENSOR = "FIRE_SENSOR";
    private boolean isYellowChecked = false;
    private boolean isBlueChecked = false;
    private boolean isGreenChecked = false;
    private boolean isDoorChecked = false;
    private boolean isSecurityChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        doorButtonListener();
        blueButtonListener();
        greenButtonListener();
        yellowButtonListener();
        pinCodeListener();
        securityListener();
        sensorListener();
        fireSensorListener();
        createNotificationChannel();
    }

    private void fireSensorListener() {
        ImageView fireImage = findViewById(R.id.fireIcon);
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int state = dataSnapshot.getValue(Integer.class);
                if (state == 1) {
                    fireImage.setVisibility(View.VISIBLE);
                    runOnUiThread(() -> createNotification("Խելացի տուն","Պաժաաաաաաաառ"));
                }else {
                    fireImage.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(FIRE_SENSOR).addValueEventListener(postListener);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID, "Valet notification channel",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(notificationChannel);
        }
    }
    private void sensorListener() {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int state = dataSnapshot.getValue(Integer.class);
                if (state == 1) {
                    runOnUiThread(() -> createNotification("Խելացի Տուն","Հայտնաբերվել է շարժ"));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(SENSOR).addValueEventListener(postListener);
    }

    private void createNotification(String title,String message) {
        Log.e("TAG", "createNotification: Builder" );
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_small_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.mipmap.ic_launcher))
                .setAutoCancel(true);

//                .setContentIntent(pendingIntent);

      NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());

    }

    private void securityListener() {
        ImageView securityButton = findViewById(R.id.securityBtn);
        securityButton.setOnClickListener(v -> {
            mDatabase.child(SECURITY).setValue(isSecurityChecked ? 0 : 1);
            securityButton.setEnabled(false);

        });
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int state = dataSnapshot.getValue(Integer.class);
                isSecurityChecked = state == 1;
                securityButton.setEnabled(true);
                if (isSecurityChecked) {
                    securityButton.setImageResource(R.drawable.ic_security_on);
                } else {
                    securityButton.setImageResource(R.drawable.ic_security_off);
                }
                securityButton.setEnabled(true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(SECURITY).addValueEventListener(postListener);
    }

    private void pinCodeListener() {
        PinView pinView = findViewById(R.id.securityPinView);
        pinView.setOnEditorActionListener((v, actionId, event) -> {
            String pinCode = pinView.getText().toString();
            Log.e("TAG", "pinCodeListener: ");
            if (pinCode.length() == 4) {
                mDatabase.child(PIN_CODE).setValue(pinView.getText().toString());
                pinView.setEnabled(false);
            }
            return true;
        });

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                pinView.setText(dataSnapshot.getValue(String.class));
                pinView.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child(PIN_CODE).addValueEventListener(postListener);
    }

    private void yellowButtonListener() {
        ImageView yellowBtn = findViewById(R.id.yellowBtn);
        yellowBtn.setOnClickListener(v -> {
            mDatabase.child(YELLOW).setValue(isYellowChecked ? 0 : 1);
            yellowBtn.setEnabled(false);
        });
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int state = dataSnapshot.getValue(Integer.class);
                isYellowChecked = state == 1;
                yellowBtn.setEnabled(true);
                if (isYellowChecked) {
                    yellowBtn.setImageResource(R.drawable.yellow_led);
                } else {
                    yellowBtn.setImageResource(R.drawable.grey_led);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Yellow error", "onCancelled: " + databaseError.getMessage());
            }
        };
        mDatabase.child(YELLOW).addValueEventListener(postListener);
    }

    private void greenButtonListener() {
        ImageView greenBtn = findViewById(R.id.greenBtn);
        greenBtn.setOnClickListener(v -> {
            mDatabase.child(GREEN).setValue(isGreenChecked ? 0 : 1);
            greenBtn.setEnabled(false);
        });
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int state = dataSnapshot.getValue(Integer.class);
                isGreenChecked = state == 1;
                greenBtn.setEnabled(true);
                if (isGreenChecked) {
                    greenBtn.setImageResource(R.drawable.green_led);
                } else {
                    greenBtn.setImageResource(R.drawable.grey_led);
                }
                greenBtn.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Green error", "onCancelled: " + databaseError.getMessage());
            }
        };
        mDatabase.child(GREEN).addValueEventListener(postListener);
    }

    private void blueButtonListener() {
        ImageView blueBtn = findViewById(R.id.blueBtn);
        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(BLUE).setValue(isBlueChecked ? 0 : 1);
                blueBtn.setEnabled(false);
            }
        });
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int state = dataSnapshot.getValue(Integer.class);
                isBlueChecked = state == 1;
                blueBtn.setEnabled(true);
                if (isBlueChecked) {
                    blueBtn.setImageResource(R.drawable.blue_led);
                } else {
                    blueBtn.setImageResource(R.drawable.grey_led);
                }
                blueBtn.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Blue error", "onCancelled: " + databaseError.getMessage());
            }
        };
        mDatabase.child(BLUE).addValueEventListener(postListener);
    }

    private void doorButtonListener() {
        ImageView doorBtn = findViewById(R.id.doorBtn);
        doorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(DOOR).setValue(isDoorChecked ? 0 : 1);
                doorBtn.setEnabled(false);
            }
        });
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                int state = dataSnapshot.getValue(Integer.class);
                isDoorChecked = state == 1;
                doorBtn.setEnabled(true);
                if (isDoorChecked) {
                    doorBtn.setImageResource(R.drawable.door_open);
                } else {
                    doorBtn.setImageResource(R.drawable.door_closed);
                }
                doorBtn.setEnabled(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Door error", "onCancelled: " + databaseError.getMessage());
            }
        };
        mDatabase.child(DOOR).addValueEventListener(postListener);
    }


}