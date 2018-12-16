package hcmus.mdsd.itusgallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class LoginCloudActivity extends AppCompatActivity {
    EditText edtname,edtpass;
    Button btnlogin,btnexit;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_cloud);
        edtname =(EditText) findViewById(R.id.name_Cloud2);
        edtpass =(EditText) findViewById(R.id.pass_Cloud2);
        btnlogin =(Button) findViewById(R.id.button_Login);
        btnexit =(Button) findViewById(R.id.button_Exit2);
        mAuth = FirebaseAuth.getInstance();

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginClould();

            }
        });

        btnexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    private void LoginClould(){
        String namecloud = edtname.getText().toString().trim();
        String passcloud = edtpass.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(namecloud,passcloud).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginCloudActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    getInfo();
                }
                else {
                    Toast.makeText(LoginCloudActivity.this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            MainActivity._name_cloud = user.getUid();
            MainActivity._email_cloud = user.getEmail();

            SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor2 = sharedPreferences2.edit();
            editor2.putString("name_cloud", MainActivity._name_cloud);
            editor2.commit();

            SharedPreferences sharedPreferences3 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor3 = sharedPreferences3.edit();
            editor3.putString("email_cloud", MainActivity._email_cloud);
            editor3.commit();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}