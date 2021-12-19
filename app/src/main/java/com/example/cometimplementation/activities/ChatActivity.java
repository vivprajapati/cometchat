package com.example.cometimplementation.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.TextMessage;
import com.example.cometimplementation.AppConfig;
import com.example.cometimplementation.R;
import com.example.cometimplementation.adapter.ChatAdapter;
import com.example.cometimplementation.models.ChatMessageModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private String receiverUid = "", receiverImg = "", receiverName = "";
    private CircleImageView profile_img;
    private TextView name;
    private EditText input_message;
    private ImageView gallery,image;
    private FloatingActionButton send;
    private RecyclerView recycler_chat;
    private ChatAdapter chatAdapter;
    private List<ChatMessageModel> chatMessageModels = new ArrayList<>();
    private BaseMessage baseMessage;
    private String listenerID = "ChatActivity.java";
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();


    }

    private void initView() {
        receiverUid = getIntent().getStringExtra("uid");
        receiverImg = getIntent().getStringExtra("img_url");
        receiverName = getIntent().getStringExtra("name");

        profile_img = findViewById(R.id.profile_img);
        name = findViewById(R.id.name);
        input_message = findViewById(R.id.input_message);
        gallery = findViewById(R.id.gallery);
        send = findViewById(R.id.send);
        recycler_chat = findViewById(R.id.recycler_chat);
        image = findViewById(R.id.image);

        send.setOnClickListener(this);
        gallery.setOnClickListener(this);

        baseMessage=new BaseMessage();
        name.setText(receiverName);
        Picasso.get().load(receiverImg).into(profile_img);
        setChatRecyclerView();

    }

    private void setChatRecyclerView() {

        recycler_chat.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this, chatMessageModels);
        recycler_chat.setAdapter(chatAdapter);
//        chatMessageModels.add(new ChatMessageModel("hi", AppConfig.UID));
//        chatMessageModels.add(new ChatMessageModel("hi", "ddssd"));
//        chatMessageModels.add(new ChatMessageModel("hi", AppConfig.UID));
//        chatMessageModels.add(new ChatMessageModel("hi", "ddssd"));
//        chatMessageModels.add(new ChatMessageModel("hi", AppConfig.UID));
//        chatMessageModels.add(new ChatMessageModel("hi", "ddssd"));
//        chatAdapter.notifyDataSetChanged();

    }


    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                sendMessage();
                break;
            case R.id.gallery:
                checkGalleryPermission();
                break;


        }

    }

    private void checkGalleryPermission() {
        Dexter.withContext(ChatActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "select image file"), 1);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();


    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            
            startCrop(CropImage.getPickImageResultUri(this, data));
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                image.setImageURI(result.getUri());
                image.setVisibility(View.VISIBLE);
                resultUri = result.getUri();
                Log.d("image_uri", "nextActivity: " + resultUri);
                
            }
        }
    }


    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setMultiTouchEnabled(true).start(this);
    }

    private void sendMessage() {



        if (!input_message.getText().toString().trim().isEmpty()) {
            sendingTextMessage(input_message.getText().toString().trim());
        } else {
            if(resultUri==null) {
                input_message.setError("Please write some Message");
                input_message.requestFocus();
            }
        }
        if(resultUri!=null){
            Log.d("see_result_uri", "sendMessage: "+resultUri);
            sendImageMessage(resultUri.toString());


        }

    }

    private void sendImageMessage(String image_uri) {
        MediaMessage mediaMessage = new MediaMessage(receiverUid,new File(image_uri),CometChatConstants.MESSAGE_TYPE_IMAGE,CometChatConstants.RECEIVER_TYPE_USER);

        CometChat.sendMediaMessage(mediaMessage, new CometChat.CallbackListener<MediaMessage>() {
            @Override
            public void onSuccess(MediaMessage mediaMessage) {
                Log.d("check", "Media message sent successfully: " + mediaMessage.toString());
                resultUri=null;
                image.setVisibility(View.GONE);
            }
            @Override
            public void onError(CometChatException e) {
                Log.d("check", "Media message sending failed with exception: " + e.getMessage());
            }
        });


    }

    private void sendingTextMessage(String message) {
        TextMessage textMessage = new TextMessage(receiverUid, message, CometChatConstants.RECEIVER_TYPE_USER);

        CometChat.sendMessage(textMessage, new CometChat.CallbackListener<TextMessage>() {
            @Override
            public void onSuccess(TextMessage textMessage) {
                Log.d("check", "Message sent successfully: " + textMessage.toString());
                input_message.getText().clear();
                hideKeyBoard();
                chatMessageModels.add(new ChatMessageModel(textMessage.getText(),AppConfig.UID));
                chatAdapter.notifyDataSetChanged();
                Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(CometChatException e) {
                Log.d("check", "Message sending failed with exception: " + e.getMessage());
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        CometChat.addMessageListener(listenerID, new CometChat.MessageListener() {
            @Override
            public void onTextMessageReceived(TextMessage textMessage) {
                Log.d("check", "Text message received successfully: " + textMessage.toString());
                chatMessageModels.add(new ChatMessageModel(textMessage.getText(),receiverUid));
                chatAdapter.notifyDataSetChanged();
            }
            @Override
            public void onMediaMessageReceived(MediaMessage mediaMessage) {
                Log.d("check", "Media message received successfully: " + mediaMessage.toString());
            }
            @Override
            public void onCustomMessageReceived(CustomMessage customMessage) {
                Log.d("check", "Custom message received successfully: " +customMessage.toString());
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        CometChat.removeMessageListener(listenerID);

    }

    public void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}