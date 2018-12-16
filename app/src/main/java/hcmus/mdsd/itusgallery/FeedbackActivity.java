package hcmus.mdsd.itusgallery;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity
{
    Toolbar toolBar;
    EditText ediName, ediFeedback;
    String name, feedback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        toolBar = findViewById(R.id.nav_actionBar);
        setSupportActionBar(toolBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Feedback");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ediName = findViewById(R.id.edi_Name);
        ediFeedback = findViewById(R.id.edi_Feedback);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; add items to the action bar
        getMenuInflater().inflate(R.menu.status_bar_feedback, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // user clicked a menu-item from ActionBar
        int id = item.getItemId();
        if (id == R.id.action_send) {
            AlertDialog builder = new AlertDialog.Builder(FeedbackActivity.this).create(); //Use context
            builder.setTitle("Confirm");
            builder.setMessage("Do you want to send feedback to dev ?");
            builder.setButton(Dialog.BUTTON_POSITIVE,"Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    sendEmail();
                }
            });
            builder.setButton(Dialog.BUTTON_NEGATIVE,"Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        }
        else if(id == android.R.id.home){
            finish();
            return true;
        }
        return false;
    }
    private void sendEmail() {
        //Getting content for email
        name = ediName.getText().toString().trim();
        feedback = ediFeedback.getText().toString().trim();
        //Creating SendMail object
        SendMail sm = new SendMail(this, name, feedback);
        //Executing sendmail to send email
        sm.execute();
    }
}