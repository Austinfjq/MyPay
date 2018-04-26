package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.edu.zju.mypay.R;

public class PayActivity extends Activity {
    private Button mBalanceButton;
    private Button mCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mBalanceButton = findViewById(R.id.qr_code_button);
        mCartButton = findViewById(R.id.nfc_pay_button);

        mBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, QrCodeDisplayActivity.class);
                startActivity(intent);
            }
        });
        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, NFCActivity.class);
                startActivity(intent);
            }
        });
    }
}
