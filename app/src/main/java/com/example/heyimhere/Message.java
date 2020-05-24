package com.example.heyimhere;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "body")
    public String body;

    @ColumnInfo(name = "receiver", index = true)
    public String receiver;

    @ColumnInfo(name = "is_sent")
    public boolean isSent;

    @ColumnInfo(name = "is_draft")
    public boolean isDraft;

    @ColumnInfo(name = "time")
    public String time;

    public Message(String body, String receiver, boolean isSent, boolean isDraft, String time) {
        this.id = 0;
        this.body = body;
        this.receiver = receiver;
        this.isSent = isSent;
        this.isDraft = isDraft;
        this.time = time;
    }
}
