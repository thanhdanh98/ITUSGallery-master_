package hcmus.mdsd.itusgallery;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

public class PasswordActivity extends AppCompatActivity {
    String password, inputPassword;
    Button btnConfirm;
    TextInputLayout txtPassword;
    MyPrefs myPrefs;
    Integer passMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myPrefs = new MyPrefs(this);
        password = myPrefs.getPassword();
        passMode = myPrefs.getPassMode();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        txtPassword = findViewById(R.id.txt_Password);

        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPassword()){
                    if(0 == passMode){
                        Intent intent = getIntent();
                        String activity = intent.getStringExtra("SwitchTo");

                        // so sánh để chuyển fragment cho đúng
                        Intent i = new Intent (new Intent(PasswordActivity.this,MainActivity.class));
                        i.putExtra("SwitchTo", activity);
                        startActivity(i);
                        finish();
                    }
                    else if(1 == passMode){
                        startActivity(new Intent(PasswordActivity.this,SetPasswordActivity.class));
                        finish();
                    }
                    else if(2 == passMode){
                        AlertDialog builder = new AlertDialog.Builder(PasswordActivity.this).create(); //Use context
                        builder.setTitle("Confirm");
                        builder.setMessage("Your private pictures won't be protected by password and might be seen by somebody else \n" +
                                "Are you sure you want to delete your current password ? ");
                        builder.setButton(Dialog.BUTTON_POSITIVE,"Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        myPrefs.setPassword("");
                                        Toast.makeText(PasswordActivity.this, "Password has been delete", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(PasswordActivity.this,MainActivity.class));
                                        startActivity(new Intent(PasswordActivity.this,SettingsActivity.class));
                                        finish();
                                    }
                                });
                        builder.setButton(Dialog.BUTTON_NEGATIVE,"No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        startActivity(new Intent(PasswordActivity.this,MainActivity.class));
                                        startActivity(new Intent(PasswordActivity.this,SettingsActivity.class));
                                        finish();
                                    }
                                });
                        builder.show();
                    }
                }
            }
        });
    }

    boolean checkPassword(){
        inputPassword=Objects.requireNonNull(txtPassword.getEditText()).getText().toString();

        if(!inputPassword.matches(password)){
            txtPassword.setError("Password is not correct");
            return false;
        }
        else{
            txtPassword.setError(null);
            txtPassword.setErrorEnabled(false);
            return true;
        }
    }
}
