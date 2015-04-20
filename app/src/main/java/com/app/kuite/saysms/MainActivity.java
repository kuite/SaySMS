package com.app.kuite.saysms;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    protected static final int CONTACT = 2;
    protected static final int MSG = 1;
    private TextView msg, num;
    private Button msgBtn, send, book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msg = (TextView) findViewById(R.id.message);
        num = (TextView) findViewById(R.id.name);

        msgBtn = (Button) findViewById(R.id.recordButton);
        msgBtn.setOnClickListener(new View.OnClickListener() { //OnClickListener() is an interface declared in View class

            @Override
            public void onClick(View v) {
                //onClick is called when a view has been clicked

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, MSG);
                    msg.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Ops! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        book = (Button) findViewById(R.id.bookButton);
        book.setOnClickListener(new View.OnClickListener() { //OnClickListener() is an interface declared in View class

            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, CONTACT);
            }
        });


        send = (Button) findViewById(R.id.sendButton);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String number = num.getText().toString();
                String sms = msg.getText().toString();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, sms, null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, check connection to Internet and your account balance!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        System.out.println("menu is clicked");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MSG:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    msg.setText(text.get(0));
                }
                break;
            case CONTACT:
                if (resultCode == RESULT_OK && null != data) {

                    Uri contactData = data.getData();
                    Cursor contactCursor = getContentResolver().query(contactData, new String[]{ContactsContract.Contacts._ID}, null, null, null);
                    String id = null;
                    if (contactCursor.moveToFirst()) {
                        id = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    }
                    contactCursor.close();
                    String phoneNumber = null;
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ? ",
                            new String[]{id}, null);
                    if (phoneCursor.moveToFirst()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replace("-", "");
                        num.setText(phoneNumber);
                    }
                    phoneCursor.close();
                }
                break;
        }
    }

}

