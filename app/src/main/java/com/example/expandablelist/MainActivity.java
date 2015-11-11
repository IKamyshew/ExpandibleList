package com.example.expandablelist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    final String LOG_TAG = "myLogs";

    private ExpandableListView list;
    private ArrayList<Users> users;
    private JsonParser jsonParser;
    private String jsonFile;

    LoginDataBaseAdapter loginDataBaseAdapter;

    private Dialog loginDialog;
    private EditText authorizeUsername;
    private EditText authorizePassword;
    private TextView authorizeBtnLogin;
    private TextView authorizeBtnLater;
    private TextView authorizeBtnRegister;

    private Dialog registerDialog;
    private TextView registerBtnLater;
    private TextView registerBtnRegister;
    private EditText registerLogin;
    private EditText registerPassword;
    private EditText registerRepeatedPassword;
    private EditText registerFirstName;
    private EditText registerLastName;
    private EditText registerPhoneNumber;
    private EditText registerImageUrl;

    private boolean isLogged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create a instance of SQLite Database
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();



        list = (ExpandableListView) findViewById(R.id.userList);
        users = new ArrayList<>();
        jsonParser = new JsonParser(MainActivity.this);
        jsonFile = "Json2.json";

        new JSONAsyncTask().execute("JSONUsers");

        list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                Log.d(LOG_TAG, "onGroupClick groupPosition = " + groupPosition + " id = " + id);
                if (isLogged)
                    return false;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.main_alert_title)
                        .setMessage(R.string.main_alert_text)
                        .setPositiveButton(R.string.main_alert_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showLoginDialog();
                            }
                        })
                        .setNegativeButton(R.string.main_alert_cancel, null)
                        .setOnCancelListener(null)
                        .show();
                return true;
            }
        });

        list.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            public void onGroupCollapse(int groupPosition) {
                Log.d(LOG_TAG, "onGroupCollapse groupPosition = " + groupPosition);
            }
        });

        list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            public void onGroupExpand(int groupPosition) {
                Log.d(LOG_TAG, "onGroupExpand groupPosition = " + groupPosition);
            }
        });

        if(savedInstanceState == null)
            isLogged = false;
        else {
            isLogged = savedInstanceState.getBoolean("isLogged");
            if(savedInstanceState.getBoolean("loginDialog"))
                showLoginDialog();
            if(savedInstanceState.getBoolean("registerDialog"))
                showRegisterDialog();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isLogged", isLogged);
        if(loginDialog != null &&  loginDialog.isShowing()) {
            outState.putBoolean("loginDialog", true);
        }

        if(registerDialog != null &&  registerDialog.isShowing()) {
            outState.putBoolean("registerDialog", true);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showLoginDialog() {
        loginDialog = new Dialog(this);
        loginDialog.setContentView(R.layout.login_dialog);
        loginDialog.setTitle(R.string.sign_in);

        authorizeUsername = (EditText) loginDialog.findViewById(R.id.sign_in_username);
        authorizePassword = (EditText) loginDialog.findViewById(R.id.sign_in_password);

        authorizeBtnLogin = (TextView) loginDialog.findViewById(R.id.btn_submit);
        authorizeBtnLater = (TextView) loginDialog.findViewById(R.id.btn_later);
        authorizeBtnRegister = (TextView) loginDialog.findViewById(R.id.btn_register);

        //authorizeUsername.setInputType(InputType.TYPE_NULL);

        authorizeBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String password = authorizePassword.getText().toString();
                String storedPassword = loginDataBaseAdapter.getSinlgeEntry(authorizeUsername.getText().toString());
                if(password.equals(storedPassword))
                {
                    isLogged = true;
                    loginDialog.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.sign_in_toast_success, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.sign_in_toast_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        authorizeBtnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loginDialog.dismiss();
            }
        });

        authorizeBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                loginDialog.dismiss();
                showRegisterDialog();
            }
        });

        loginDialog.show();
    }

    public void showAuthorizeAlert(MenuItem item) {
        showLoginDialog();
    }

    public void showRegisterDialog() {
        registerDialog = new Dialog(this);
        registerDialog.setContentView(R.layout.register_dialog);
        registerDialog.setTitle(R.string.register_dialog);

        registerLogin = (EditText) registerDialog.findViewById(R.id.register_username);
        registerPassword = (EditText) registerDialog.findViewById(R.id.register_password);
        registerRepeatedPassword = (EditText) registerDialog.findViewById(R.id.register_repeat_password);
        registerFirstName = (EditText) registerDialog.findViewById(R.id.register_user_first_name);
        registerLastName = (EditText) registerDialog.findViewById(R.id.register_user_last_name);
        registerPhoneNumber = (EditText) registerDialog.findViewById(R.id.register_phone_number);
        registerImageUrl = (EditText) registerDialog.findViewById(R.id.register_image);

        registerBtnRegister = (TextView) registerDialog.findViewById(R.id.btn_complete_register);
        registerBtnLater = (TextView) registerDialog.findViewById(R.id.btn_decline_later);

        //registerLogin.setInputType(InputType.TYPE_NULL);

        registerBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String userName = registerLogin.getText().toString();
                String password = registerPassword.getText().toString();
                String confirmPassword = registerRepeatedPassword.getText().toString();
                String firstName = registerFirstName.getText().toString();
                String lastName = registerLastName.getText().toString();
                String phone = registerPhoneNumber.getText().toString();
                String imageURL = registerImageUrl.getText().toString();

                // check if any of the fields are empty
                if(userName.equals("")||password.equals("")||confirmPassword.equals("")||firstName.equals("")||phone.equals(""))
                {
                    Toast.makeText(getApplicationContext(), R.string.register_toast_mandatory_fields, Toast.LENGTH_LONG).show();
                    return;
                }
                // check if both password matches
                if(!password.equals(confirmPassword))
                {
                    Toast.makeText(getApplicationContext(), R.string.register_toast_password_match, Toast.LENGTH_LONG).show();
                    return;
                }
                if(!loginDataBaseAdapter.getSinlgeEntry(userName).equals("NOT EXIST"))
                {
                    Toast.makeText(getApplicationContext(), R.string.register_toast_login_exist, Toast.LENGTH_LONG).show();
                    return;
                }
                if (createUser(firstName, lastName, phone, imageURL)) {
                    // Save the Data in Database
                    loginDataBaseAdapter.insertEntry(userName, password);
                    registerDialog.dismiss();
                    isLogged = true;
                    Toast.makeText(MainActivity.this, R.string.register_toast_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.register_toast_failed, Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerBtnLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                registerDialog.dismiss();
            }
        });

        registerDialog.show();
    }

    private boolean createUser(String firstName, String lastName, String phoneNumber, String imageURL) {
        try {
            JSONObject newUser = new JSONObject();
            newUser.put("image", imageURL);
            newUser.put("firstName", firstName);
            newUser.put("lastName", lastName);
            newUser.put("phoneNumber", phoneNumber);

            JSONObject jsonObject = new JSONObject(jsonParser.getData(jsonFile));
            JSONArray jsonArray = jsonObject.getJSONArray("users");
            jsonArray.put(newUser);
            jsonObject.put("users", jsonArray);
            jsonParser.writeData(jsonFile, jsonObject.toString());
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                String jsonStringFile = jsonParser.getData(jsonFile);
                if(jsonStringFile == "") {
                    jsonParser.writeData(jsonFile, jsonParser.readAssets("JSONUsers"));
                    jsonStringFile = jsonParser.getData(jsonFile);
                }

                Log.i("json", jsonStringFile);

                JSONObject jsonObject = new JSONObject(jsonStringFile);
                JSONArray jsonArray = jsonObject.getJSONArray("users");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Users user = new Users();

                    user.setImage(object.getString("image"));
                    user.setFirstName(object.getString("firstName"));
                    user.setLastName(object.getString("lastName"));
                    user.setPhoneNumber(object.getString("phoneNumber"));

                    users.add(user);
                }

                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*try {

                // read json from server
                HttpGet httpPost = new HttpGet(urls[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray jsonArray = jsonObject.getJSONArray("users");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        Users user = new Users();

                        user.setImage(object.getString("image"));
                        user.setFirstName(object.getString("firstName"));
                        user.setLastName(object.getString("lastName"));
                        user.setPhoneNumber(object.getString("PhoneNumber"));

                        usersList.add(user);
                    }

                    return true;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            */
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(getApplicationContext(), R.string.menu_json_data_fail, Toast.LENGTH_LONG).show();
            } else {
                UserAdapter adapter = new UserAdapter(getApplicationContext(), users);
                list.setAdapter(adapter);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }
}
