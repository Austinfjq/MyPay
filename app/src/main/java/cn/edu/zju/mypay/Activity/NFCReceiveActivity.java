package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

import cn.edu.zju.mypay.R;

import static cn.edu.zju.mypay.Activity.QrCodeScannerActivity.translateMessage;

public class NFCReceiveActivity extends Activity { //implements NfcAdapter.CreateNdefMessageCallback {
    private final String TAG = "NFCActivityTAG";

    NfcAdapter mNfcAdapter;
    TextView textView, textView2, totalMoneyView;
    private PendingIntent mPendingIntent;

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
        if (messages != null) {
            String message = displayByteArray(messages[0].toByteArray());
            String res[] = translateMessage(message.substring(message.indexOf('#')+1));
            if (res != null) {
                String userId = res[0];
                String totalMoney = res[1];
                String orderList = res[2];
                totalMoneyView = (TextView) findViewById(R.id.totalMoney);
                float temp = Float.valueOf(totalMoney);
                temp = (float)(Math.round(temp*100)) / 100;
                totalMoney = String.valueOf(temp);
                totalMoneyView.setText("成功收款：" + totalMoney + "元");
                TextView userIdView = findViewById(R.id.userId);
                userIdView.setText("用户ID：" + userId);
                TextView orderListView = findViewById(R.id.orderList);
                orderListView.setText("订单详情：\n" + orderList);
            }
        }
    }
}
