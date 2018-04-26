package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import cn.edu.zju.mypay.HttpUtils;
import cn.edu.zju.mypay.R;

import static cn.edu.zju.mypay.Activity.QrCodeScannerActivity.translateMessage;

public class NFCReceiveActivity extends Activity { //implements NfcAdapter.CreateNdefMessageCallback {
    private final String TAG = "NFCActivityTAG";

    NfcAdapter mNfcAdapter;
    TextView textView, textView2, totalMoneyView, userIdView, orderListView;
    private PendingIntent mPendingIntent;
    private TransferTask mAuthTask = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreceive);
        TextView textView = (TextView) findViewById(R.id.textView);

        NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        // Check for available NFC Adapter
        mNfcAdapter = nfcManager.getDefaultAdapter();
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

//    private static NdefMessage getTestMessage() {
//        byte[] mimeBytes = "application/cn.edu.zju.mypay.Activity"
//                .getBytes(Charset.forName("US-ASCII"));
//        byte[] id = new byte[] {1, 3, 3, 7};
//        byte[] payload = "Hello, I want to pay.".getBytes(Charset.forName("US-ASCII"));
//        return new NdefMessage(new NdefRecord[] {
//                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, id, payload)
//        });
//    }

    // sending message
//    @Override
//    public NdefMessage createNdefMessage(NfcEvent event) {
//        return getTestMessage();
//    }

    @Override
    protected void onResume() {
        super.onResume();

        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
//        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }


    static String displayByteArray(byte[] bytes) {
        String res = "";
        StringBuilder builder = new StringBuilder().append("[");
        for (int i = 0; i < bytes.length; i++) {
            res += (char) bytes[i];
        }
        return res;
    }

    private NdefMessage[] getNdefMessages(Intent intent) {
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            NdefMessage[] messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }
            return messages;
        } else {
            return null;
        }
    }

    // displaying message
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        NdefMessage[] messages = getNdefMessages(intent);
        setContentView(R.layout.activity_qr_code_info_display);
        TextView mInfoTextView = findViewById(R.id.InfoText);
        TextView mOrderText = findViewById(R.id.orderText);
        TextView mOrderText2 = findViewById(R.id.orderText2);

        if (messages != null) {
            String message = displayByteArray(messages[0].toByteArray());
            Log.d(TAG, "message = " + message);
            String res[] = translateMessage(message.substring(message.indexOf(".$$$")+4));
            if (res != null) {
                res[0] = res[0].trim();
                Log.d(TAG, res[0] + "\n" + res[1] + "\n" + res[2]);
                String userId = res[0];
                String totalMoney = res[1];
                String orderList = res[2];
                float temp = Float.valueOf(totalMoney);
                temp = (float)(Math.round(temp*100)) / 100;
                totalMoney = String.valueOf(temp);
                mOrderText.setText("订单详情：");
                mOrderText2.setText(orderList);

                SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
                String sellerId = sp.getString(getString(R.string.saved_card_id), "");
                mAuthTask = new TransferTask(userId, sellerId, totalMoney);
                mAuthTask.execute((Void) null);
            }
        }
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

            TextView mInfoText = findViewById(R.id.InfoText);
            ImageView imageView = findViewById(R.id.success_img);

            if (!success) {
                imageView.setImageResource(R.drawable.fail_pic);
                mInfoText.setText("网络不佳，错误码：" + successPay);
            } else {
                imageView.setImageResource(R.drawable.success_pic);
                mInfoText.setText("成功收款：" + mTotalMoney + "元");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
