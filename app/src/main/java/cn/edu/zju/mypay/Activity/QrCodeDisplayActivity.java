package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.github.sumimakito.awesomeqr.AwesomeQRCode;

import cn.edu.zju.mypay.R;

public class QrCodeDisplayActivity extends Activity {
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_display);

        SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
        String cardId = sp.getString(getString(R.string.saved_card_id), "");
        String totalMoney = sp.getString("totalMoney", "0.0f");
        String orderList = sp.getString("orderList", "");
        Bitmap qrCode = AwesomeQRCode.create(cardId + "##" + totalMoney + "##" + orderList, 800, 20, 0.95f, Color.BLACK, Color.WHITE, null, true, true);

        mImageView = findViewById(R.id.qr_code_img);
        mImageView.setImageBitmap(qrCode);
    }
}
