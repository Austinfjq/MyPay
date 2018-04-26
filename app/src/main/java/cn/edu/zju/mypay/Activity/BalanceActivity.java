package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.edu.zju.mypay.HttpUtils;
import cn.edu.zju.mypay.R;

public class BalanceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        TextView mTextView = findViewById(R.id.balanceText);

        SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
        String savedCardId = sp.getString(getString(R.string.saved_card_id), "");

        // TODO: delete next line and add a real URL

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result = null;
        InputStream inputStream = null;

        try {
            URL url = new URL("");
            urlConnection = (HttpURLConnection) url.openConnection();
            /* optional request header */
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");
            /* for Get request */
            urlConnection.setRequestMethod("GET");
            int statusCode = urlConnection.getResponseCode();

            /* 200 represents HTTP OK */
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = HttpUtils.dealResponseResult(inputStream);
                Gson gson = new Gson();
//                UserDto dto = gson.fromJson(response, UserDto.class);
//                if (dto != null && dto.getToken() != null) {
//                    Log.i("token", "find the token = " + dto.getToken());
//                }
//                return dto;
            }

        } catch (Exception e) {

        }
//        String url="http://?.?.?.?/orderForm";
//        Map<String,String> params = new HashMap<String, String>();
//        params.put("query", "balance");
//        params.put("cardId", savedCardId);
////        String result = HttpUtils.submitPostData(url,params,"utf-8");
//        String result = "159.3";
        mTextView.setText("您的余额：" + result + "元");
    }
}
