package com.example.schan.lunchanyone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.schan.model.ChatMessage;
import com.example.schan.model.Group;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.media.CamcorderProfile.get;

/**
 * Created by schan on 12/15/16.
 */

public class GroupActivity extends AppCompatActivity {

    private ListView mListView;
    private FirebaseListAdapter<Group> adapter;
    private String name;
    private FloatingActionButton fab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity);

        final Context context = this;


        adapter = new FirebaseListAdapter<Group>(this, Group.class, R.layout.list_item_group, FirebaseDatabase.getInstance().getReference().child("groups")) {
            @Override
            protected void populateView(View v, Group model, int position) {
                //Get references to the views of list_item_group.xml

                TextView groupName, restaurant, memberCount, description;
                ImageView image;
                groupName = (TextView) v.findViewById(R.id.group_list_group_name);
                restaurant = (TextView) v.findViewById(R.id.group_list_restaurant);
                memberCount = (TextView) v.findViewById(R.id.group_list_member_count);
                description = (TextView) v.findViewById(R.id.group_list_description);
                image = (ImageView) v.findViewById(R.id.group_list_thumbnail);

                groupName.setText(model.getGroupName());
                restaurant.setText("Restaurant: " + model.getRestaurant());
                memberCount.setText("Member Count: " + String.valueOf(model.getMemberCount()));
                description.setText(model.getDescription());
                Picasso.with(getApplicationContext()).load(model.getImageUrl()).placeholder(R.mipmap
                        .ic_launcher).into(image);
            }
        };

//        final ArrayList<Group> groupList = Group.getGroupsFromFile("Groups.json", this);

//        GroupAdapter adapter = new GroupAdapter(this, groupList);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, CreateGroupActivity.class);
                startActivity(intent);
            }
        });

        mListView = (ListView) findViewById(R.id.group_view);
        mListView.setAdapter(adapter);

        // Set what happens when a list view item is clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle extras = getIntent().getExtras();

                if (extras != null) {
                    name = extras.getString("name");
                    // and get whatever type user account id is
                } else {
                    name = "Stephen Chan";
                }

                Group selectedGroup = adapter.getItem(position);

                Intent detailIntent = new Intent(context, GroupChatActivity.class);
                detailIntent.putExtra("groupName", selectedGroup.getGroupName());
                detailIntent.putExtra("restaurant", selectedGroup.getRestaurant());
                detailIntent.putExtra("name", name);
                //TODO: Can add a group based on the group name
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put(selectedGroup.getGroupName(), "");
//                root.updateChildren(map);

                startActivity(detailIntent);
            }

        });
        mListView.setAdapter(adapter);
    }
}
