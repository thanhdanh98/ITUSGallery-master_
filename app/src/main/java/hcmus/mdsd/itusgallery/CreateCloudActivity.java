package hcmus.mdsd.itusgallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class CreateCloudActivity extends AppCompatActivity {
    EditText edtname,edtpass, edtpass2;
    Button btncreate,btnexit;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cloud);
        edtname =(EditText) findViewById(R.id.name_Cloud);
        edtpass =(EditText) findViewById(R.id.pass_Cloud);
        edtpass2 =(EditText) findViewById(R.id.pass_Again);

        btncreate =(Button) findViewById(R.id.button_Create);
        btnexit =(Button) findViewById(R.id.button_Exit);
        mAuth = FirebaseAuth.getInstance();
        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateCloud();
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

    private void CreateCloud(){

        String namecloud = edtname.getText().toString().trim();
        String passcloud = edtpass.getText().toString().trim();
        String passcloud2 = edtpass2.getText().toString().trim();
        if(passcloud.equals(passcloud2)){
            mAuth.createUserWithEmailAndPassword(namecloud,passcloud).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(CreateCloudActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(CreateCloudActivity.this, "Đăng kí không thành công", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(CreateCloudActivity.this, "Nhập lại mật khẩu chưa đúng", Toast.LENGTH_SHORT).show();
        }


    }
}