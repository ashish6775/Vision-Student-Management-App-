package com.example.ashish.startup.Authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ashish.startup.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReAuthentication extends AppCompatActivity {

    private EditText current_pass;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_authentication);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        current_pass = (EditText) findViewById(R.id.current_pass);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Change Password");
        }
    }


    public void next(View v) {
        String pass = current_pass.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(getApplicationContext(), "Fill in the Fields", Toast.LENGTH_LONG).show();
        } else if (user != null) {
            String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email,pass);
            dialog.setMessage("Authenticating");
            dialog.show();
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Authenticated", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(ReAuthentication.this, ChangePassword.class);
                                startActivity(i);
                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
