package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.util.HashMap;
import java.util.Map;

import cn.edu.zju.mypay.R;

public class QrCodeScannerActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {
    private QRCodeReaderView qrCodeReaderView;
    private TextView mTextView;

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
                        sentence = "包子*" + idAndNum[1];
                    } else if (Integer.valueOf(idAndNum[0]) >= 200) {
                        sentence = "香蕉*" + idAndNum[1];
                    } else {
                        sentence = names[Integer.valueOf(idAndNum[0])-100] + idAndNum[1];
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
        mTextView = findViewById(R.id.text);
        mTextView.setText(translateMessage(text)[2]);

        if (true) return;
        String url="http://?.?.?.?/orderForm";
        Map<String,String> params = new HashMap<String, String>();
        params.put("query", "balance");
//        params.put("cardId", savedCardId);
//        String result = HttpUtils.submitPostData(url,params,"utf-8");
        String result = "159.3";
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
}
