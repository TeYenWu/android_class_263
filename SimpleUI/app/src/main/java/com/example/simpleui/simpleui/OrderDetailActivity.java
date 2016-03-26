package com.example.simpleui.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailActivity extends AppCompatActivity {

    TextView note;
    TextView storeInfo;
    TextView orderDetail;
    ImageView photoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView)findViewById(R.id.noteView);
        storeInfo = (TextView)findViewById(R.id.storeInfoView);
        orderDetail = (TextView)findViewById(R.id.menuView);
        photoImageView = (ImageView)findViewById(R.id.photoView);

        note.setText(getIntent().getStringExtra("note"));
        storeInfo.setText(getIntent().getStringExtra("storeInfo"));

        String text = "";
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(getIntent().getStringExtra("menu"));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject order = jsonArray.getJSONObject(i);

                text = text + order.getString("name") + " l : " + String.valueOf(order.getInt("lNumber")) + " m : " + String.valueOf(order.getInt("mNumber")) + "\n";

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        orderDetail.setText(text);

        String url = getIntent().getStringExtra("photoURL");

        Picasso.with(this).load(url).into(photoImageView);

//        if (url != null)
//        {
//            new AsyncTask<String, Void, byte[]>()
//            {
//                @Override
//                protected byte[] doInBackground(String... params)
//                {
//                    String url = params[0];
//                    return Utils.urlToBytes(url);
//                }
//
//                @Override
//                protected void onPostExecute(byte[] bytes)
//                {
//                    Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//                    photoImageView.setImageBitmap(bmp);
//                    super.onPostExecute(bytes);
//                }
//            }.execute(url);
//
//        }

    }
}
