package com.example.heyimhere;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AddDraftActivity extends AppCompatActivity {
    public static final String DRAFT_MESSAGE = "DraftMessage";
    public static final String DRAFT_NUMBER = "DraftNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddraft);

        final TextView Number = findViewById(R.id.txtEditNumber);
        final TextView Message = findViewById(R.id.txtEditMessage);

        // Set Save Contact button and its listener
        Button SaveButton = findViewById(R.id.btnSave);
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent replyIntent = new Intent();
                // Check if either is null
                if (TextUtils.isEmpty(Message.getText()) || TextUtils.isEmpty(Number.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    // Grad text from applicable fields and bundle in reply intent
                    String message = Message.getText().toString();
                    String number = Number.getText().toString();
                    replyIntent.putExtra(DRAFT_MESSAGE, message);
                    replyIntent.putExtra(DRAFT_NUMBER, number);
                    setResult(RESULT_OK, replyIntent);
                }
                // Finish Activity
                finish();
            }
        });

        // Set Cancel button and its listener
        Button CancelButton = findViewById(R.id.btnCancel);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set Result
                Intent replyIntent = new Intent();
                setResult(RESULT_CANCELED, replyIntent);

                // Finish Activity
                finish();
            }
        });

    }

}
