package hcmus.mdsd.itusgallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class LogoutCloudActivity extends AppCompatActivity {
    TextView txtname;
    Button btnlogout,btnexit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_cloud);
        txtname = (TextView) findViewById(R.id.textViewOut);
        btnlogout =(Button) findViewById(R.id.button_Logout);
        btnexit =(Button) findViewById(R.id.button_Exit3);
        txtname.setText("Bạn muốn thoát tài khoản " + MainActivity._email_cloud + " ?");

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                MainActivity._name_cloud="";
                MainActivity._email_cloud="";

                SharedPreferences sharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                editor2.putString("name_cloud", MainActivity._name_cloud);
                editor2.commit();

                SharedPreferences sharedPreferences3 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor3 = sharedPreferences3.edit();
                editor3.putString("email_cloud", MainActivity._email_cloud);
                editor3.commit();

                Toast.makeText(LogoutCloudActivity.this, "Đăng xuất thành công thành công", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
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
}