package com.example.Vendora;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cab.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerLoginRegisterActivity extends AppCompatActivity {
    private Button CustomerLoginButton;
    private Button CustomerRegisterButton;
    private TextView CustomerRegisterLink;
    private TextView CustomerStatus;
    private EditText EmailCustomer;
    private EditText PasswordCustomer;

    private DatabaseReference CustomerDatabaseRef;
    private String onlineCustomerId;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login_register);
        CustomerLoginButton = (Button) findViewById(R.id.customer_login_btn);
        CustomerRegisterButton = (Button) findViewById(R.id.customer_register_btn);
        CustomerRegisterLink = (TextView) findViewById(R.id.customer_register_link);
        CustomerStatus = (TextView) findViewById(R.id.customer_status);

        EmailCustomer = (EditText) findViewById(R.id.email_customer);
        PasswordCustomer = (EditText) findViewById(R.id.password_customer);
        loadingBar= new ProgressDialog(this);



        mAuth = FirebaseAuth.getInstance();


        CustomerLoginButton.setVisibility(View.VISIBLE);
        CustomerRegisterButton.setVisibility(View.INVISIBLE);
        CustomerRegisterButton.setEnabled(false);

        CustomerRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomerLoginButton.setVisibility(View.INVISIBLE);
                CustomerRegisterLink.setVisibility(View.INVISIBLE);
                CustomerStatus.setText("Register Customer");
                CustomerRegisterButton.setVisibility(View.VISIBLE);
                CustomerRegisterButton.setEnabled(true);
            }
        });

        CustomerRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailCustomer.getText().toString();
                String password = PasswordCustomer.getText().toString();

                RegisterCustomer(email, password);
            }
        });

        CustomerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EmailCustomer.getText().toString().trim();
                final String password = PasswordCustomer.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(CustomerLoginRegisterActivity.this, "Please write Email ", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(password))
                {
                    Toast.makeText(CustomerLoginRegisterActivity.this, "Please enter Password ", Toast.LENGTH_SHORT).show();
                }


                else {
                    loadingBar.setTitle("Vendor Login");
                    loadingBar.setMessage("Please wait, while we are checking your credentials");
                    loadingBar.show();
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(CustomerLoginRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                            {

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Enter correct details", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                            if (task.isSuccessful()) {

                                Intent CustomerIntent = new Intent(CustomerLoginRegisterActivity.this, CustomersMapActivity.class);
                                startActivity( CustomerIntent);

                                Toast.makeText(CustomerLoginRegisterActivity.this, "Login Succesfull", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }

    private void RegisterCustomer(String email, String password) {

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please write Email ", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(CustomerLoginRegisterActivity.this, "Please enter Password ", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Customer Registration");
            loadingBar.setMessage("Please wait, while we register your data");
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        onlineCustomerId = mAuth.getCurrentUser().getUid();
                        CustomerDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(onlineCustomerId);


                        CustomerDatabaseRef.setValue("true");
                        Intent vendorIntent = new Intent(CustomerLoginRegisterActivity.this,CustomersMapActivity.class);
                        startActivity(vendorIntent);

                        Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Register Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        Toast.makeText(CustomerLoginRegisterActivity.this, "Customer Register Unsuccessfully, Please try again", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }
}


