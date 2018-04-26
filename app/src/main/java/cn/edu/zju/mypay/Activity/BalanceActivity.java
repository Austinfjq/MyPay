package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.edu.zju.mypay.response.BalanceResponse;

public class BalanceActivity extends Activity {
    private BalanceTask mAuthTask = null;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        mTextView = findViewById(R.id.balanceText);

        SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
        String savedCardId = sp.getString(getString(R.string.saved_card_id), "");
        mAuthTask = new BalanceTask(savedCardId);
        mAuthTask.execute((Void) null);
    }

    public class BalanceTask extends AsyncTask<Void, Void, Boolean> {
        private final String mCardId;
        private BalanceResponse mBalanceResponse;
        BalanceTask(String cardId) {
            mCardId = cardId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = "0.0";
            InputStream inputStream = null;

            try {
                String urlWithParams = "https://wbx.life/bank/account/balance/" + mCardId;
                URL url = new URL(urlWithParams);
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
                    BalanceResponse br = gson.fromJson(response, BalanceResponse.class);
                    mBalanceResponse = br;
                }

            } catch (Exception e) {
                Log.d("BalanceActivity:", "" + e);
                // Toast.makeText(BalanceActivity.this, "网络不佳", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (!success) {
                mTextView.setText("网络不佳");
            } else if (mBalanceResponse.getError() != 0) {
                mTextView.setText("服务器错误");
            } else {
                String res = "您的余额为：" + mBalanceResponse.getBody().getBalance() + "元";
                mTextView.setText(res);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
