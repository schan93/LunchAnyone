package com.example.schan.lunchanyone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.format.DateFormat;
import android.text.style.TextAppearanceSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.schan.model.ChatMessage;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by schan on 12/15/16.
 */

public class GroupChatActivity extends AppCompatActivity {


    private FirebaseListAdapter<ChatMessage> adapter;
    private RelativeLayout activity_main;
    private FloatingActionButton fab;
    private String name;
    private String restaurant;
    private String groupName;



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_join_group) {
            Snackbar.make(activity_main,"You are now a part of " + groupName + "!", Snackbar.LENGTH_SHORT).show();
            //Send firebase request to show that this user is now a part of the group
            FirebaseDatabase.getInstance().getReference().child("chat/" + groupName).push().setValue(new ChatMessage(name + " has now joined " + groupName + ". Welcome!",
                    groupName, 2));
            Map<String, Object> updatedRestaurant = new HashMap<String, Object>();
            updatedRestaurant.put("memberCount", 17);
            FirebaseDatabase.getInstance().getReference().child("groups/" + groupName).updateChildren(updatedRestaurant);
        } else if(item.getItemId() == R.id.menu_change_restaurant) {
            //Show alert box
            showAlertBox();
        }
        return true;
    }

    private void showAlertBox() {

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        final EditText edittext = new EditText(this);
        edittext.setSingleLine(true);
        layout.setPadding(65, 0, 65, 0);

        layout.addView(edittext);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Update " + groupName + "'s restaurant");
        alert.setMessage("Note: This will notify everyone in the group about your change.");

        alert.setView(layout);

        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value
                final String restaurant = edittext.getText().toString();
                Map<String, Object> updatedRestaurant = new HashMap<String, Object>();
                updatedRestaurant.put("restaurant", restaurant);
                FirebaseDatabase.getInstance().getReference().child("groups/" + groupName).updateChildren(updatedRestaurant, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        dialog.dismiss();
                        FirebaseDatabase.getInstance().getReference().child("chat/" + groupName).push().setValue(new ChatMessage(name + " has now changed today's restaurant to " + restaurant,
                                groupName, 3));
                        TextView showRestaurant = (TextView) findViewById(R.id.show_restaurant);
                        showRestaurant.setText("Today's restaurant: " + restaurant);
                        showRestaurant.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
                        showRestaurant.setTextSize(18);
                        showRestaurant.setTextColor(getResources().getColor(R.color.colorPrimary));

                        //Send notification here

//                        sendNotification(groupName, restaurant);
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.cancel();
            }
        });

        alert.show();

    }

    private void sendNotification(String groupName, String restaurant) {

        Intent intent = new Intent(this, GroupChatActivity.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

// build notification
// the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(this)
                .setContentTitle(groupName)
                .setContentText("Today's restaurant is set to: " + restaurant)
                .setSmallIcon(R.drawable.lunch_transparent)
                .setContentIntent(pIntent)
                .setAutoCancel(true).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_layout);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            name = extras.getString("name");
            groupName = extras.getString("groupName");
            restaurant = extras.getString("restaurant");
            // and get whatever type user account id is
        } else {
            name = "n/a";
            groupName = "N/A";
            restaurant = "N/A";
        }

        final AutoCompleteTextView input = (AutoCompleteTextView) findViewById(R.id.input);

        activity_main = (RelativeLayout)findViewById(R.id.chat_activity);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("chat/" + groupName).push().setValue(new ChatMessage(input.getText().toString(),
                        name, 1));
                input.setText("");
            }
        });
        displayChatMessage(input);
    }



    private void displayChatMessage(final EditText input) {
        TextView showRestaurant = (TextView) findViewById(R.id.show_restaurant);
        showRestaurant.setText("Today's restaurant: " + restaurant);
        showRestaurant.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
        showRestaurant.setTextSize(18);
        showRestaurant.setTextColor(getResources().getColor(R.color.colorPrimary));
        final ListView listOfMessage = (ListView)findViewById(R.id.list_of_message);
        adapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.chat_message, FirebaseDatabase.getInstance().getReference().child("chat/" + groupName))
        {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                //Get references to the views of list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText = (TextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                if(model.getMessageType() == 1) {
                    messageText.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
                    messageText.setText(model.getMessageText());
                    messageUser.setText(model.getMessageUser());
                    messageText.setTextSize(18);
                    messageTime.setText(DateFormat.format("MM-dd-yyyy hh:mm:ss a", model.getMessageTime()));
                    messageTime.setVisibility(View.VISIBLE);
                    messageUser.setVisibility(View.VISIBLE);
                } else if(model.getMessageType() == 2) {
                    messageText.setText(model.getMessageText());
                    messageText.setTextAppearance(android.R.style.TextAppearance_Material_Body1);
                    messageUser.setVisibility(View.GONE);
                    messageTime.setVisibility(View.GONE);
                } else if(model.getMessageType() == 3){
                    messageText.setText(model.getMessageText());
                    messageText.setTextAppearance(android.R.style.TextAppearance_Material_Body2);
                    messageText.setTextSize(18);
                    messageUser.setVisibility(View.GONE);
                    messageTime.setVisibility(View.GONE);
                }

            }
        };
        listOfMessage.setAdapter(adapter);
    }

}
