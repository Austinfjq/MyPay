package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.edu.zju.mypay.HttpUtils;
import cn.edu.zju.mypay.R;

public class QrCodeScannerActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {
    private QRCodeReaderView qrCodeReaderView;
    private TextView mInfoTextView, mOrderText, mOrderText2;
    private TransferTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_scanner);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    public static String[] translateMessage(String text) {
        String[] res = text.split("##");
        if (res.length < 3) return null;

        String[] sentences = res[2].split(",");
        StringBuffer sb = new StringBuffer("");

        String[] names = {"可口可乐", "雪碧", "美汁源橙汁", "七喜", "营养快线", "加多宝凉茶", "芬达", "芒果汁", "绿茶"};
        for (String sentence : sentences) {
            try {
                if (sentence.contains("*")) {
                    String[] idAndNum = sentence.split("\\*");
                    if (Integer.valueOf(idAndNum[0]) >= 300) {
                        sentence = idAndNum[1] + "x             包子";
                    } else if (Integer.valueOf(idAndNum[0]) >= 200) {
                        sentence = idAndNum[1] + "x             香蕉";
                    } else {
                        sentence = idAndNum[1] + "x             " + names[Integer.valueOf(idAndNum[0])-100];
                    }
                }
            } catch (Exception e) {
                sentence = "";
            }
            sb.append(sentence);
            sb.append("\n");
        }
        res[2] = new String(sb);
        return res;
    }

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed in View
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        setContentView(R.layout.activity_qr_code_info_display);
        mInfoTextView = findViewById(R.id.InfoText);
        mOrderText = findViewById(R.id.orderText);
        mOrderText2 = findViewById(R.id.orderText2);

        String[] transRes = translateMessage(text);
        mOrderText.setText("订单详情：");
        mOrderText2.setText(transRes[2]);
        String userId = transRes[0];
        String totalMoney = transRes[1];
        float temp = Float.valueOf(totalMoney);
        temp = (float)(Math.round(temp*100)) / 100;
        totalMoney = String.valueOf(temp);
        SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
        String sellerId = sp.getString(getString(R.string.saved_card_id), "");

        mAuthTask = new TransferTask(userId, sellerId, totalMoney);
        mAuthTask.execute((Void) null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    public class TransferTask extends AsyncTask<Void, Void, Boolean> {
        private final String mUserId, mSellerId, mTotalMoney;
        private int successPay;

        TransferTask(String userId, String sellerId, String totalMoney) {
            mUserId = userId;
            mSellerId = sellerId;
            mTotalMoney = totalMoney;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String result = "0.0";
            InputStream inputStream = null;

            try {
                String urlWithParams1 = "https://wbx.life/bank/account/pay/" + mUserId + "/" + mTotalMoney;
                String urlWithParams2 = "https://wbx.life/bank/account/pay/" + mSellerId + "/-" + mTotalMoney;
                URL url1 = new URL(urlWithParams1);
                URL url2 = new URL(urlWithParams2);

                urlConnection = (HttpURLConnection) url1.openConnection();
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
                    successPay += 1;
                }

                urlConnection = (HttpURLConnection) url2.openConnection();
            /* optional request header */
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            /* optional request header */
                urlConnection.setRequestProperty("Accept", "application/json");
            /* for Get request */
                urlConnection.setRequestMethod("GET");
                statusCode = urlConnection.getResponseCode();
            /* 200 represents HTTP OK */
                if (statusCode == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    String response = HttpUtils.dealResponseResult(inputStream);
                    Gson gson = new Gson();
                    successPay += 2;
                }
            } catch (Exception e) {
                Log.d("BalanceActivity:", "" + e);
                Log.d("BalanceActivity:", "" + successPay);
                // Toast.makeText(BalanceActivity.this, "网络不佳", Toast.LENGTH_SHORT).show();
                return false;
            }
            Log.d("BalanceActivity:", "" + successPay);
            if (successPay != 3)
                return false;
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mInfoTextView = findViewById(R.id.InfoText);
            ImageView imageView = findViewById(R.id.success_img);

            if (!success) {
                imageView.setImageResource(R.drawable.fail_pic);
                mInfoTextView.setText("网络不佳，错误码：" + successPay);
            } else {
                imageView.setImageResource(R.drawable.success_pic);
                String res = "转账成功" + mTotalMoney + "元";
                mInfoTextView.setText(res);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
