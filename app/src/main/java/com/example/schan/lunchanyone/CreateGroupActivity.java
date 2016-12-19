package com.example.schan.lunchanyone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.example.schan.model.ChatMessage;
import com.example.schan.model.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by schan on 12/16/16.
 */

public class CreateGroupActivity extends AppCompatActivity {

    AutoCompleteTextView editGroupName;
    AutoCompleteTextView editRestaurant;
    AutoCompleteTextView editDescription;
    AutoCompleteTextView editImageUrl;
    Button createGroupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group);

        editGroupName = (AutoCompleteTextView) findViewById(R.id.edit_group_name);
        editRestaurant = (AutoCompleteTextView) findViewById(R.id.edit_restaurant);
        editDescription = (AutoCompleteTextView) findViewById(R.id.edit_description);
        editImageUrl = (AutoCompleteTextView) findViewById(R.id.edit_image_url);
        createGroupButton = (Button) findViewById(R.id.create_group_button);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Once we create the group, we need to send this data to firebase
                Group group = new Group(
                        editGroupName.getText().toString(), editRestaurant.getText().toString(),
                        editDescription.getText().toString(), 1, editImageUrl.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("groups/" + editGroupName.getText().toString()).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Go back to Group Activity once completed
                        Intent intent = new Intent(CreateGroupActivity.this, GroupActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });



    }
}
