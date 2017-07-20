package com.ruzy.barcomon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ruzy.barcomon.databaseModel.User;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordView2;
    private View mProgressView;
    private View mLoginFormView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView2 = (EditText) findViewById(R.id.password_2);
        mPasswordView2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    //attemptRegister();
                    return true;
                }
                return false;
            }
        });
        mPasswordView2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        if (mAuth.getCurrentUser() != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(!mPasswordView2.getText().toString().matches(password)) {
            mPasswordView2.setError("密碼不一致");
            return;
        }

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                InitialDataBase();
                            }
                            showProgress(false);
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle(task.isSuccessful()?"YES":"NO")
                                    .setMessage(task.toString())
                                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    })
                                    .show();
                        }
                    });
        }
    }

    public void InitialDataBase(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user = mAuth.getCurrentUser();
        User u = new User(null,mAuth.getCurrentUser().getEmail(),null);
        FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid()).setValue(u);

        /*UserItem item=new UserItem(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);

        databaseReference.child(user.getUid()).child("Item").setValue(item);

        UserInformation userinformation=new UserInformation("user","000",0,"10000",user.getUid());
        databaseReference.child(user.getUid()).child("userinformation").setValue(userinformation);
        //public Monster(String mosterID,int level,int lovelevel,int attack,int defense,int exp,int strength,String monstertype,String monsterinformation,int upgradeAttackValue,int upgradeDefenseValue,int upgradePlayLoveLevelValue,int upgradeChatLoveLevelValue,int upgradeEXPValue,int upgradeStrengthValue){
        Monster m0 =new Monster("000",1,0,1000,1000,0,500,"Drink","INFORMATION",200,200,200,200,200,400);
        Monster m1 =new Monster("001",1,0,500,1500,0,500,"Drink","INFORMATION",200,200,200,200,200,400);
        Monster m2 =new Monster("002",1,0,1500,500,0,500,"Drink","INFORMATION",200,200,200,200,200,400);
        Monster m3 =new Monster("003",1,0,1300,700,0,500,"Food","INFORMATION",200,200,200,200,200,400);
        Monster m4 =new Monster("004",1,0,200,1800,0,500,"Book","INFORMATION",200,200,200,200,200,400);


        databaseReference = FirebaseDatabase.getInstance().getReference("Monsters");

        databaseReference.child(user.getUid()).child(m0.UsergetMonsterID()).child("MonsterInformation").setValue(m0);
        databaseReference.child(user.getUid()).child(m1.UsergetMonsterID()).child("MonsterInformation").setValue(m1);
        databaseReference.child(user.getUid()).child(m2.UsergetMonsterID()).child("MonsterInformation").setValue(m2);
        databaseReference.child(user.getUid()).child(m3.UsergetMonsterID()).child("MonsterInformation").setValue(m3);
        databaseReference.child(user.getUid()).child(m4.UsergetMonsterID()).child("MonsterInformation").setValue(m4);


        databaseReference = FirebaseDatabase.getInstance().getReference("Monsters");

        EquipmentInformation equipment=new EquipmentInformation("100","NoEquip");
        databaseReference.child(user.getUid()).child(m0.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card1").setValue(equipment);
        databaseReference.child(user.getUid()).child(m0.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card2").setValue(equipment);
        databaseReference.child(user.getUid()).child(m0.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card3").setValue(equipment);

        databaseReference.child(user.getUid()).child(m1.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card1").setValue(equipment);
        databaseReference.child(user.getUid()).child(m1.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card2").setValue(equipment);
        databaseReference.child(user.getUid()).child(m1.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card3").setValue(equipment);

        databaseReference.child(user.getUid()).child(m2.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card1").setValue(equipment);
        databaseReference.child(user.getUid()).child(m2.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card2").setValue(equipment);
        databaseReference.child(user.getUid()).child(m2.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card3").setValue(equipment);

        databaseReference.child(user.getUid()).child(m3.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card1").setValue(equipment);
        databaseReference.child(user.getUid()).child(m3.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card2").setValue(equipment);
        databaseReference.child(user.getUid()).child(m3.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card3").setValue(equipment);

        databaseReference.child(user.getUid()).child(m4.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card1").setValue(equipment);
        databaseReference.child(user.getUid()).child(m4.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card2").setValue(equipment);
        databaseReference.child(user.getUid()).child(m4.UsergetMonsterID()).child("BattleInformation").child("EquipmentInformation").child("Card3").setValue(equipment);

        SkillInformation skill =new SkillInformation("100","NoSkill");
        databaseReference.child(user.getUid()).child(m0.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card1").setValue(skill);
        databaseReference.child(user.getUid()).child(m0.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card2").setValue(skill);
        databaseReference.child(user.getUid()).child(m0.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card3").setValue(skill);

        databaseReference.child(user.getUid()).child(m1.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card1").setValue(skill);
        databaseReference.child(user.getUid()).child(m1.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card2").setValue(skill);
        databaseReference.child(user.getUid()).child(m1.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card3").setValue(skill);

        databaseReference.child(user.getUid()).child(m2.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card1").setValue(skill);
        databaseReference.child(user.getUid()).child(m2.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card2").setValue(skill);
        databaseReference.child(user.getUid()).child(m2.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card3").setValue(skill);

        databaseReference.child(user.getUid()).child(m3.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card1").setValue(skill);
        databaseReference.child(user.getUid()).child(m3.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card2").setValue(skill);
        databaseReference.child(user.getUid()).child(m3.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card3").setValue(skill);

        databaseReference.child(user.getUid()).child(m4.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card1").setValue(skill);
        databaseReference.child(user.getUid()).child(m4.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card2").setValue(skill);
        databaseReference.child(user.getUid()).child(m4.UsergetMonsterID()).child("BattleInformation").child("SkillInformation").child("Card3").setValue(skill);*/
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

