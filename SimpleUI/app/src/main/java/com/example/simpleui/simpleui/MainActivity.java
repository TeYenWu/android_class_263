package com.example.simpleui.simpleui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private  static  final int REQUEST_CODE_MENU_ACTIVITY = 0;
    private  static  final int REQUEST_TAKE_PHOTO = 1;

    private String menuResult = "";
    private List<ParseObject> queryResult;
    private boolean hasPhoto = false;

    TextView textView;
    EditText editText;
    CheckBox hideCheckBox;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ListView historyListView;
    Spinner storeInfoSpinner;
    ImageView photoImageView;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    LoginButton loginButton;
    CallbackManager callbackManager;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sp.edit();

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String text = editText.getText().toString();
                editor.putString("inputText", text);
                editor.apply();

                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    submit(v);
                    return true;
                }

                return false;
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit(v);
                    return true;
                }

                return false;
            }
        });

        photoImageView = (ImageView)findViewById(R.id.imageView);

        hideCheckBox = (CheckBox)findViewById(R.id.checkBox);

        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", hideCheckBox.isChecked());
                editor.apply();

                if (isChecked) {
                    photoImageView.setVisibility(View.GONE);
                } else {
                    photoImageView.setVisibility(View.VISIBLE);
                }
            }
        });

        hideCheckBox.setChecked(sp.getBoolean("hideCheckBox", false));

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        progressDialog = new ProgressDialog(this);

        historyListView = (ListView)findViewById(R.id.listView);
        setHistory();

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDetailOrder(position);
            }
        });


        storeInfoSpinner = (Spinner)findViewById(R.id.spinner);
        setStoreInfos();



        setupFacebook();
    }

    private FacebookCallback<LoginResult> facebookRegisterCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            accessToken = AccessToken.getCurrentAccessToken();
            GraphRequest request = GraphRequest.newGraphPathRequest(accessToken
                    , "/v2.5/me",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            JSONObject object = response.getJSONObject();
                            try {
                                String name = object.getString("name");
                                Toast.makeText(MainActivity.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                                textView.setText("Hello " + name);
                                Log.d("debug", object.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            request.executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    private void setupFacebook() {
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginButton);
        loginButton.registerCallback(callbackManager, facebookRegisterCallBack);
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if(currentAccessToken == null)
                {
                    Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
                    textView.setText("Hello World");
                }
            }
        };

        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();
        if((accessToken = AccessToken.getCurrentAccessToken()) != null)
        {
            GraphRequest request = GraphRequest.newGraphPathRequest(accessToken
                    , "/v2.5/me",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            JSONObject object = response.getJSONObject();
                            try {
                                String name = object.getString("name");
                                Toast.makeText(MainActivity.this, "Hello " + name, Toast.LENGTH_SHORT).show();
                                textView.setText("Hello " + name);
                                Log.d("debug", object.toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            request.executeAsync();
        }
//        LoginManager.getInstance().registerCallback(callbackManager, facebookRegisterCallBack);
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

    }


    private void setHistory()
    {
        ParseQuery<ParseObject> query = new ParseQuery<>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                queryResult = objects;
                List<Map<String, String>> data = new ArrayList<Map<String, String>>();
                for (int i = 0; i < objects.size(); i++) {
                    ParseObject object = objects.get(i);
                    String note = object.getString("note");
                    String storeInfo = object.getString("storeInfo");
                    String menu = object.getString("menu");

                    Map<String, String> item = new HashMap<>();
                    item.put("note", note);

                    item.put("storeInfo", storeInfo);

                    int drinkNum = 0;
                    try {
                        JSONArray array = new JSONArray(menu);
                        for (int j = 0; j < array.length(); j++) {

                            JSONObject order = array.getJSONObject(j);
                            drinkNum += order.getInt("lNumber") + order.getInt("mNumber");
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }



                    item.put("drinkNum", String.valueOf(drinkNum));

                    data.add(item);
                }

                String[] from = {"note", "drinkNum", "storeInfo"};
                int[] to = {R.id.note, R.id.drinkNum, R.id.storeInfo};

                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,
                        data, R.layout.listview_item, from, to);

                historyListView.setAdapter(adapter);
                historyListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void setStoreInfos()
    {
        ParseQuery<ParseObject> query =
                new ParseQuery<>("StoreInfo");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e != null || objects == null)
                    return;
                String[] stores = new String[objects.size()];
                for (int i = 0; i < stores.length; i++) {
                    ParseObject object = objects.get(i);
                    stores[i] = object.getString("name") + "," +
                            object.getString("address");
                }
                ArrayAdapter<String> storeAdapter =
                        new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, stores);
                storeInfoSpinner.setAdapter(storeAdapter);
            }
        });
    }

    public  void submit(View view)
    {

        String text = editText.getText().toString();

        progressDialog.setTitle("Loading...");
        progressDialog.show();

        ParseObject orderObject = new ParseObject("Order");
        orderObject.put("note", text);
        orderObject.put("storeInfo", storeInfoSpinner.getSelectedItem());
        orderObject.put("menu", menuResult);

        if (hasPhoto)
        {
            Uri uri = Utils.getPhotoUri();
            ParseFile parseFile = new ParseFile("photo.png", Utils.uriToBytes(this, uri));
            orderObject.put("photo", parseFile);
        }

        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(MainActivity.this,
                            "[SaveCallback] ok", Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,
                            "[SaveCallback] fail", Toast.LENGTH_SHORT).show();
                }
                setHistory();
                textView.setText("");
                editText.setText("");
                menuResult = "";
                hasPhoto = false;
                photoImageView.setImageResource(0);
            }
        });


    }

    public  void goToMenu(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);

        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MENU_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                menuResult = (data.getStringExtra("result"));

                String text = "";

                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(menuResult);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject order = jsonArray.getJSONObject(i);

                        text = text + order.getString("name") + " l : " + String.valueOf(order.getInt("lNumber")) + " m : " + String.valueOf(order.getInt("mNumber")) + "\n";


                    }
                    textView.setText(text);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                Uri uri = Utils.getPhotoUri();
                photoImageView.setImageURI(uri);
                hasPhoto = true;
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_take_photo) {
            Toast.makeText(this, "take photo", Toast.LENGTH_SHORT).show();
            goToCamera();
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void goToCamera() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
                return;
            }
        }
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoUri());
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        
    }

    private void goToDetailOrder(int position)
    {
        Intent intent = new Intent();

        intent.setClass(this, OrderDetailActivity.class);

        ParseObject object = queryResult.get(position);

        intent.putExtra("note", object.getString("note"));
        intent.putExtra("menu", object.getString("menu"));
        intent.putExtra("storeInfo", object.getString("storeInfo"));
//
        if(object.getParseFile("photo") != null)
        {
            intent.putExtra("photoURL", object.getParseFile("photo").getUrl());

        }

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}
