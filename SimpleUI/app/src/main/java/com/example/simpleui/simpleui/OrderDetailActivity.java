package com.example.simpleui.simpleui;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailActivity extends AppCompatActivity {

    TextView note;
    TextView storeInfo;
    TextView orderDetail;
    ImageView photoImageView;
    ImageView staticMapImage;
    WebView staticMapWeb;
    Switch staticWebSwitch;
    GoogleMap googleMap;
    MapFragment mapFragment;

    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView) findViewById(R.id.noteView);
        storeInfo = (TextView) findViewById(R.id.storeInfoView);
        orderDetail = (TextView) findViewById(R.id.menuView);
        photoImageView = (ImageView) findViewById(R.id.photoView);
        staticMapImage = (ImageView) findViewById(R.id.staticImageView);
        staticMapWeb = (WebView) findViewById(R.id.webView);
        staticWebSwitch = (Switch)findViewById(R.id.staticMapSwitch);
        staticMapWeb.setVisibility(View.GONE);

        note.setText(getIntent().getStringExtra("note"));

        String storeInformation = getIntent().getStringExtra("storeInfo");
        storeInfo.setText(storeInformation);

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

        address = storeInformation.split(",")[1];

        String imageUrl = getIntent().getStringExtra("photoURL");

        if (imageUrl != null)
        {
            ImageLoadingTask imageLoadingTask = new ImageLoadingTask(photoImageView);
            imageLoadingTask.execute(imageUrl);
        }

        GeoCodingTask task = new GeoCodingTask();
        task.execute(address);

        staticWebSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    photoImageView.setVisibility(View.GONE);
                    staticMapWeb.setVisibility(View.VISIBLE);
                }
                else
                {
                    photoImageView.setVisibility(View.VISIBLE);
                    staticMapWeb.setVisibility(View.GONE);
                }
            }
        });

        mapFragment = (MapFragment)
                getFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
            }
        });

    }

    class  GeoCodingTask extends AsyncTask<String, Void, byte[]>
    {

        private String url;
        private double[] latLng;
        @Override
        protected byte[] doInBackground(String... params) {
            String address = params[0];
            latLng = Utils.addressToLatLng(address);
            url = Utils.getStaticMapUrl(latLng, 17);
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes)
        {
            staticMapWeb.loadUrl(url);
            Bitmap bm =
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImage.setImageBitmap(bm);
            LatLng storeAddress = new LatLng(latLng[0], latLng[1]);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    storeAddress, 17));

            String[] storeInfo =
                    getIntent().getStringExtra("storeInfo").split(",");

            googleMap.addMarker(new MarkerOptions()
                    .title(storeInfo[0])
                    .snippet(storeInfo[1])
                    .position(storeAddress));

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(OrderDetailActivity.this,
                            marker.getTitle(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
    }

            class  ImageLoadingTask extends AsyncTask<String, Void, byte[]>
    {

        ImageView imageView;

        @Override
        protected byte[] doInBackground(String... params)
        {
            String url = params[0];
            return Utils.urlToBytes(url);
        }

        @Override
        protected void onPostExecute(byte[] bytes)
        {
            Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            imageView.setImageBitmap(bmp);
            super.onPostExecute(bytes);
        }

        public ImageLoadingTask(ImageView imageView)
        {
            this.imageView = imageView;
        }
    }

}
