package com.safesoft.uk2015.restopro.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.formation.uk2015.login.R;
import com.safesoft.uk2015.restopro.databases.SQLite.DATABASE;
import com.safesoft.uk2015.restopro.PostData.PostData_Users;
import com.github.ybq.android.spinkit.style.Circle;
import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final int REQUEST_READ_CONTACTS = 0;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private ArrayList<PostData_Users> users = new ArrayList<>();
   // private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Activity activity;
    private Context context;
    DATABASE controller;
    private String Server;
    private String _Username;
    private String _Password;
    Boolean check_user = false;
    Boolean check_connection = false;
    private String MY_PREFS_NAME = "ConfigNetwork";
    private String Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.Login_form_label);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_user);
        context = this.getApplicationContext();

        controller = new DATABASE(context);
       // controller.create_table();

        // Set up the login form.
      //  mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
       // populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        activity = this;
        Button mLogInButton = (Button) findViewById(R.id.log_in_button);
        mLogInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
     /*   if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;

        }else if(!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }*/


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
       // return password.length() > 4;
        return true;
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }


    private interface ProfileQuery {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mPassword;
        private String mCurrentUser;

        private Connection con = null;

        UserLoginTask(String password) {
            if(password.length() <=0){
                mPassword = null;
            }else{
                mPassword = password;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            boolean isCorrect = false;

            try {

                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                Server = prefs.getString("ip", "192.168.1.5");
                Path = prefs.getString("path", "C:/RESTOPRO");
                _Username = prefs.getString("username", "SYSDBA");
                _Password = prefs.getString("password", "masterkey");

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, _Username, _Password);

                String sql = "SELECT DISTINCT NOM_VENDEUR, MOT_DE_PASSE FROM VENDEUR WHERE MOT_DE_PASSE IS NOT NULL";
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                PostData_Users user;
                Boolean perfect = false;
                while (rs.next()) {

                    user = new PostData_Users();

                    user.Username = rs.getString("NOM_VENDEUR");
                    user.Password = rs.getString("MOT_DE_PASSE");
                    users.add(user);
                }
               // users = contoller.get_users_from_sqlite_database();

                for(int i = 0; i< users.size(); i++){
                    if(users.get(i).Password.equals(mPassword)){
                        check_user = true;
                        isCorrect = true;
                        mCurrentUser = users.get(i).Username;
                        break;
                     }
                }

                check_connection = true;
                con = null;
               // stmt.close();
                users.clear();

            }catch (Exception e){
                if(e.getMessage().contains("Unable to complete network request to host")){
                    check_connection = false;
                }
                isCorrect =  false;
            }

            // TODO: register the new account here.
            return isCorrect;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                controller.Insert_into_users(mCurrentUser.toString(),mPasswordView.getText().toString(),"nothing","0552 72 52 11","13/04/2016");
                Intent postviewIntent = new Intent(activity, Main_SafeSoft.class);
                Bundle bundleinfo = new Bundle();
                bundleinfo.putString("WAITER",mCurrentUser.toString());
                postviewIntent.putExtras(bundleinfo);
                startActivityForResult(postviewIntent, 1100);
                activity.overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
                finish();

               // activity.overridePendingTransition(R.animator.slide_out_left, R.animator.slide_in_right);
            } else {
                if(check_connection){
                    if(!check_user){
                       mPasswordView.setText("");
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Le nom utilisateur ou le mot de passe est incorrect")
                                .show();
                    }
                }else{
                    Crouton.showText(LoginActivity.this, R.string.error_connexion, Style.ALERT);
                    F1.newInstance().show(getFragmentManager(), null);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.Setting_c) {
            F1.newInstance().show(getFragmentManager(), null);
        }
        return super.onOptionsItemSelected(item);
    }
    public static class F1 extends SwipeAwayDialogFragment {
        public Circle circle;
        TextView textView;
        Button btntest;
        private Activity _context;
        private String MY_PREFS_NAME = "ConfigNetwork";
        private final Boolean[] isRunning = {false};

        public static F1 newInstance() {
            F1 f1 = new F1();
            f1.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_DeviceDefault_Dialog);
            return f1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Remove the default background
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Inflate the new view with margins and background
            View v = inflater.inflate(R.layout.popup_layout, container, false);
            _context = getActivity();

            final EditText ip = (EditText) v.findViewById(R.id.ip);
            final EditText pathdatabase = (EditText) v.findViewById(R.id.database);
            final EditText username = (EditText) v.findViewById(R.id.username);
            final EditText password = (EditText) v.findViewById(R.id.password);

            SharedPreferences prefs = _context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            ip.setText(prefs.getString("ip", "192.168.1.5"));
            pathdatabase.setText(prefs.getString("path", "C:/P-VENTE/DATA/RESTO PRO/RESTOPRO"));
            username.setText(prefs.getString("username", "SYSDBA"));
            password.setText(prefs.getString("password", "masterkey"));

            // Set up a click listener to dismiss the popup if they click outside
            // of the background view
            v.findViewById(R.id.popup_root).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
               //     dismiss();
                }
            });

            //TextView
            textView = (TextView) v.findViewById(R.id.progress);
            circle = new Circle();
            circle.setBounds(0, 0, 60, 60);
            //noinspection deprecation
            circle.setColor(getResources().getColor(R.color.colorAccent));
            textView.setCompoundDrawables(null, null, circle, null);
            textView.setVisibility(View.GONE);
            //textView.setBackgroundColor(colors[2]);

            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AbandoN.ttf");
            TextView tv = (TextView) v.findViewById(R.id.settingtext);
            tv.setTypeface(tf);

            btntest = (Button) v.findViewById(R.id.check);
            btntest.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isRunning[0]){
                        new TestConnection_Setting(ip.getText().toString(),pathdatabase.getText().toString(),
                                username.getText().toString(), password.getText().toString()).execute();
                        isRunning[0] = true;
                    }else{
                        Crouton.showText(_context , "Test Connection is running !", Style.INFO);
                    }


                }
            });
            return v;
        }

        //class Insert Data into FireBird Database
        //====================================
        public class TestConnection_Setting extends AsyncTask<Void,Void,Boolean> {

            Boolean executed = false;
            String _Server;
            String _pathDatabase;
            String _Username;
            String _Password;
            Connection con = null;
            public TestConnection_Setting(String server, String database, String username, String password) {
                super();
                // do stuff
               _Server = server;
                _pathDatabase = database;
                _Username = username;
                _Password = password;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                textView.setVisibility(View.VISIBLE);
                circle.start();

            }

            @Override
            protected Boolean doInBackground(Void... params) {
                // TODO: attempt authentication against a network service.
                try {

                    System.setProperty("FBAdbLog", "true");
                    java.sql.DriverManager.setLoginTimeout(5);
                    Class.forName("org.firebirdsql.jdbc.FBDriver");
                    String sCon = "jdbc:firebirdsql:" + _Server + ":" + _pathDatabase + ".FDB?encoding=ISO8859_1";
                    con = DriverManager.getConnection(sCon, _Username, _Password);
                    executed = true;
                  con = null;

                }catch (Exception ex ){
                    con = null;
                    Log.e("TRACKKK", "FAILED TO CONNECT WITH SERVER " + ex.getMessage());
                }
                return executed;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                circle.stop();
                isRunning[0] = false;
                textView.setVisibility(View.GONE);

                SharedPreferences.Editor editor = _context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("ip", _Server);
                editor.putString("path", _pathDatabase);
                editor.putString("username", _Username);
                editor.putString("password", _Password);
                editor.commit();

                if(aBoolean){
                    View customView = _context.getLayoutInflater().inflate(R.layout.custom_crouton_layout, null);
                    Crouton.show(_context, customView);
                    dismiss();
                }else{
                    Animation shake = AnimationUtils.loadAnimation(_context, R.anim.shakanimation);
                    btntest.startAnimation(shake);
                   // Crouton.showText(_context , " Failed to Connect !", Style.ALERT);
                }
                super.onPostExecute(aBoolean);
            }
        }
        //==================================================
    }
}

