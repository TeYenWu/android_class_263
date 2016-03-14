package com.example.simpleui.simpleui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;



public class MainActivity extends AppCompatActivity {

    private  static  final int REQUEST_CODE_MENU_ACTIVITY = 0;

    TextView textView;
    EditText editText;
    CheckBox hideCheckBox;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ListView historyListView;
    Spinner storeInfoSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        editor = sp.edit();

        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);

        editText.setText(sp.getString("inputText", ""));

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

        hideCheckBox = (CheckBox)findViewById(R.id.checkBox);

        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", hideCheckBox.isChecked());
                editor.apply();
            }
        });

        hideCheckBox.setChecked(sp.getBoolean("hideCheckBox", false));

        historyListView = (ListView)findViewById(R.id.listView);
        setHistory();


        storeInfoSpinner = (Spinner)findViewById(R.id.spinner);
        setStoreInfos();
    }

    private void setHistory()
    {
        String[] data = Utils.readFile(this, "history.txt").split("\n");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        historyListView.setAdapter(adapter);

    }

    private void setStoreInfos()
    {

        String[] data = getResources().getStringArray(R.array.storeInfo);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, data);
        storeInfoSpinner.setAdapter(adapter);
    }

    public  void submit(View view)
    {
        String text = editText.getText().toString();
        Utils.writeFile(this, "history.txt", text + '\n');

//        text = Utils.readFile(this, "history.txt");
        if (hideCheckBox.isChecked())
        {
            Toast.makeText(this,text,Toast.LENGTH_LONG).show();
            textView.setText("**********");
            editText.setText("**********");
            return;
        }
        editText.setText("");
        textView.setText(text);

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
                textView.setText(data.getStringExtra("result"));
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "main activity onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "main activity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "main activity onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "main activity onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "main activity onStart");
    }
}
