package com.example.chatapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import android.text.format.DateFormat;

public class MainActivity extends AppCompatActivity {

    public static int SIGN_IN_CODE = 1;

    private RelativeLayout activity_main;
    private FloatingActionButton sendButton;

    //Класс позволяющий адаптировать данные из базы данных к объектам из андрои студио
    private FirebaseListAdapter<Message> adapter;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(activity_main,"Вы авторизованы",Snackbar.LENGTH_LONG).show();
                DisplayAllMessages();
            }else{
                Snackbar.make(activity_main,"Вы не авторизованы",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ОСновнйо метод поиска и присваения обЪектов к классам
        activity_main = findViewById(R.id.activity_main);
        sendButton = findViewById(R.id.btnSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText textField = findViewById(R.id.messageField);
                if(textField.getText().toString()=="")
                    return;

                FirebaseDatabase.getInstance().getReference().push().setValue(
                        new Message(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                textField.getText().toString())
                );
                textField.setText("");
            }
        });

        //Проверка авторизации пользователя в БД FireBase
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            //пользователь еще не авторизован -> авторизуем пользователя
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        }else{
            //Если автаризован ->вывод сообщения в activity_main "Вы авторизованы" долгой продолжительностью
            Snackbar.make(activity_main,"Вы авторизованы",Snackbar.LENGTH_LONG).show();
            DisplayAllMessages();
            //СОбственная функция , позволяющая отображать все сообщения
        }
    }

    private void DisplayAllMessages() {
        ListView listOfMessages = findViewById(R.id.list_off_messagese);
        adapter = new FirebaseListAdapter<Message>(this, Message.class,R.layout.list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView mess_user, mess_time, mess_text;
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);

                mess_user.setText(model.getUserName());
                mess_text.setText(model.getTextMessage());
                mess_time.setText(DateFormat.format("dd mm yyyy HH:mm:ss",model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
    }

}