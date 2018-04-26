package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import cn.edu.zju.mypay.R;
import cn.edu.zju.mypay.util.StringUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends Activity {
    private Button mBalanceButton;
    private Button mCartButton;
    private Button mQrCodeButton;
    private Button mNfcPayButton;
    private Button mLogOutButton;
    private boolean mIfSeller = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkIfSeller();

        if (mIfSeller) {
            setContentForSeller();
        } else {
            setContentForUser();
        }

        //imageload初始化
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(
                defaultOptions).build();
        ImageLoader.getInstance().init(config);
    }

    private void setContentForUser() {
        setContentView(R.layout.activity_main);
        mBalanceButton = findViewById(R.id.balance_button);
        mCartButton = findViewById(R.id.cart_button);
        mLogOutButton = findViewById(R.id.logout);

        mBalanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BalanceActivity.class);
                startActivity(intent);
            }
        });
        mCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove(getString(R.string.saved_card_id));
                editor.apply();
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setContentForSeller() {
        setContentView(R.layout.activity_main2);
        mQrCodeButton = findViewById(R.id.qr_code_button);
        mNfcPayButton = findViewById(R.id.nfc_pay_button);
        mLogOutButton = findViewById(R.id.logout);

        mQrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, QrCodeScannerActivity.class);
                startActivity(intent);
            }
        });
        mNfcPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NFCReceiveActivity.class);
                startActivity(intent);
            }
        });
        mLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.remove(getString(R.string.saved_card_id));
                editor.apply();
                finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkIfSeller() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
        String saved = sp.getString("ifSeller", "");
        if(StringUtils.isEmpty(saved) || !"true".equals(saved)) {
            mIfSeller = false;
        } else {
            mIfSeller = true;
        }
    }
}
