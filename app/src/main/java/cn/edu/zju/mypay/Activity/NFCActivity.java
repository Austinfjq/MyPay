package cn.edu.zju.mypay.Activity;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;
import cn.edu.zju.mypay.R;
import cn.edu.zju.mypay.util.ByteArrayChange;
import cn.edu.zju.mypay.util.ToStringHex;

import static android.content.ContentValues.TAG;

public class NFCActivity extends Activity implements NfcAdapter.CreateNdefMessageCallback {
    private final String TAG = "NFCActivityTAG";

    NfcAdapter mNfcAdapter;
    TextView textView;
    String ssss = "测试一下，金额123.0";
    private PendingIntent mPendingIntent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
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
        // Register callback
//        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }

    private NdefMessage getTestMessage() {

        SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
        String cardId = sp.getString(getString(R.string.saved_card_id), "");
        String totalMoney = sp.getString("totalMoney", "0.0f");
        String orderList = sp.getString("orderList", "");

        byte[] mimeBytes = "application/cn.edu.zju.mypay.Activity.NFCActivity .$$$"
                .getBytes(Charset.forName("US-ASCII"));
        byte[] id = new byte[] {1, 3, 3, 7};
        byte[] payload = (cardId + "##" + totalMoney + "##" + orderList).getBytes(Charset.forName("US-ASCII"));
        Log.d(TAG, "totalMoney2=" + totalMoney);
        return new NdefMessage(new NdefRecord[] {
                new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, id, payload)
        });
    }

    // sending message
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {


        SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
        String cardId = sp.getString(getString(R.string.saved_card_id), "");
        String totalMoney = sp.getString("totalMoney", "0.0f");
        String orderList = sp.getString("orderList", "");

//        TextView textView = (TextView) findViewById(R.id.textView);
//        textView.setText("成功支付" + totalMoney + "元。");
        return getTestMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        mNfcAdapter.setNdefPushMessageCallback(this, this);
    }


//    static String displayByteArray(byte[] bytes) {
//        String res="";
//        StringBuilder builder = new StringBuilder().append("[");
//        for (int i = 0; i < bytes.length; i++) {
//            res+=(char)bytes[i];
//        }
//        return res;
//    }
//
//    private NdefMessage[] getNdefMessages(Intent intent) {
//        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//        if (rawMessages != null) {
//            NdefMessage[] messages = new NdefMessage[rawMessages.length];
//            for (int i = 0; i < messages.length; i++) {
//                messages[i] = (NdefMessage) rawMessages[i];
//            }
//            return messages;
//        } else {
//            return null;
//        }
//    }
    // displaying message
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        NdefMessage[] messages = getNdefMessages(intent);
//        textView = (TextView) findViewById(R.id.textView);
//        textView.setText(displayByteArray(messages[0].toByteArray()));
//    }
//    /**
//     * Parses the NDEF Message from the intent and prints to the TextView
//     */
//    void processIntent(Intent intent) {
//        textView = (TextView) findViewById(R.id.textView);
//        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
//                NfcAdapter.EXTRA_NDEF_MESSAGES);
//        // only one message sent during the beam
//        NdefMessage msg = (NdefMessage) rawMsgs[0];
//        // record 0 contains the MIME type, record 1 is the AAR, if present
//        textView.setText(new String(msg.getRecords()[0].getPayload()));
//    }
//    TextView tv1, tv2, tv3;
//    EditText etSector, etBlock, etData;
//    // private NfcAdapter nfcAdapter;
//    private PendingIntent mPendingIntent;
//    private IntentFilter[] mFilters;
//    private String[][] mTechLists;
//    private int mCount = 0;
//    String info = "";
//
//    private int bIndex;
//    private int bCount;
//
//    private int BlockData;
//    private String BlockInfo;
//    private RadioButton mRead, mWriteData, mChange;
//    private byte[] b3;
//    byte[] code=MifareClassic.KEY_NFC_FORUM;//读写标签中每个块的密码
//    private byte[] data3, b0;
//    private String temp = "";
//    private NfcAdapter mNfcAdapter;
//    private Context mContext;
//    int block[] = { 4, 5, 6, 8, 9, 10, 12, 13, 14, 16, 17, 18, 20, 21, 22, 24,
//            25, 26, 28, 29, 30, 32, 33, 34, 36, 37, 38, 40, 41, 42, 44, 45, 46,
//            48, 49, 50, 52, 53, 54, 56, 57, 58, 60, 61, 62 };
//    // private StringBuilder metaInfo=new StringBuilder();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_nfc);
//        mContext = this;
//        checkNFCFunction(); // NFC Check
//        init();
//
//        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
//                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//        // Setup an intent filter for all MIME based dispatches
//        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
//        try {
//            ndef.addDataType("*/*");
//        } catch (MalformedMimeTypeException e) {
//            throw new RuntimeException("fail", e);
//        }
//        mFilters = new IntentFilter[] { ndef, };
//
//        // 根据标签类型设置
//        mTechLists = new String[][] { new String[] { NfcA.class.getName() } };
//    }

//    private void checkNFCFunction() {
//        // TODO Auto-generated method stub
//        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        // check the NFC adapter first
//        if (mNfcAdapter == null) {
//            // mTextView.setText("NFC apdater is not available");
//            Toast.makeText(NFCActivity.this, "不支持NFC",Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            if (!mNfcAdapter.isEnabled()) {
//                Dialog dialog = null;
//                Toast.makeText(NFCActivity.this, "不支持NFC",Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//    }
//
//    private Dialog SetDialogWidth(Dialog dialog) {
//        // TODO 自动生成的方法存根
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int screenWidth = dm.widthPixels;
//        int screenHeight = dm.heightPixels;
//        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//        if (screenWidth > screenHeight) {
//            params.width = (int) (((float) screenHeight) * 0.875);
//
//        } else {
//            params.width = (int) (((float) screenWidth) * 0.875);
//        }
//        dialog.getWindow().setAttributes(params);
//
//        return dialog;
//    }
//
//    private void init() {
//        // TODO 自动生成的方法存根
//        tv1 = (TextView) findViewById(R.id.tv1);
//        tv2 = (TextView) findViewById(R.id.tv2);
//        tv3 = (TextView) findViewById(R.id.tv3);
//        etSector = (EditText) findViewById(R.id.etSector);
//        etBlock = (EditText) findViewById(R.id.etBlock);
//        etData = (EditText) findViewById(R.id.etData);
//
//        mRead = (RadioButton) findViewById(R.id.rb_read);
//        mWriteData = (RadioButton) findViewById(R.id.rb_write);
//        mChange = (RadioButton) findViewById(R.id.rb_Change);
//    }
//
//    @Override
//    protected void onResume() {
//        // TODO 自动生成的方法存根
//        super.onResume();
//        enableForegroundDispatch();
//        // mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
//        // mTechLists);
//    }
//
//    private void enableForegroundDispatch() {
//        // TODO 自动生成的方法存根
//        if (mNfcAdapter != null) {
//            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
//                    mFilters, mTechLists);
//        }
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        tv1.setText("发现新的 Tag:  " + ++mCount + "\n");// mCount 计数
//        Log.d(TAG, "发现新的 Tag:  " + mCount + "\n");
//        //1.获取Tag对象
//        Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        //2.获取Ndef的实例
//        Ndef ndef = Ndef.get(detectedTag);
//        if (ndef != null) {
//            String tagText = ndef.getType() + "\nmaxsize:" + ndef.getMaxSize() + "bytes\n\n";
//            tv2.setText("读取成功! " + tagText);
//            readNfcTag(intent);
//        } else {
//            String intentActionStr = intent.getAction();// 获取到本次启动的action
//            if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intentActionStr)// NDEF类型
//                    || NfcAdapter.ACTION_TECH_DISCOVERED.equals(intentActionStr)// 其他类型
//                    || NfcAdapter.ACTION_TAG_DISCOVERED.equals(intentActionStr)) {// 未知类型
//
//                // 在intent中读取Tag id
//                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//                byte[] bytesId = tag.getId();// 获取id数组
//                Log.d(TAG, "" + bytesId.toString() + ",\n" + tag.toString());
//
//                info += ByteArrayChange.ByteArrayToHexString(bytesId) + "\n";
//                tv2.setText("标签UID:  " + "\n" + info);
//                Log.d(TAG, "标签UID:  " + "\n" + info);
//
//                // 读取存储信息
//                if (mRead.isChecked()) {
//                    // mChange=false;
//                    tag = patchTag(tag);
//                    String res = readTag(tag);
//                    tv3.setText("读取成功! " + res);
//                    Log.d(TAG, "读取成功! " + res);
//                    // readNfcVTag(tag);
//                    etSector.setText("");
//                    etBlock.setText("");
//                    etData.setText("");
//                }
//
//                // 写数据
//                if (mWriteData.isChecked()) {
//                    //writeTag(tag);
//                    String str= etData.getText().toString();
//                    writeTag(tag,str);
//                }
//
//                // 转换为ASCll
//                if (mChange.isChecked()) {
//                    tv3.setText(change(tag));
//                    Log.d(TAG, change(tag) + "\n" + "转换成功");
//                    Toast.makeText(getBaseContext(), "转换成功", Toast.LENGTH_SHORT).show();
//                    etSector.setText("");
//                    etBlock.setText("");
//                    etData.setText("");
//                }
//            }
//        }
//
//
//    }
//
//    /**
//     * 读取NFC标签文本数据
//     */
//    private void readNfcTag(Intent intent) {
//        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
//            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
//                    NfcAdapter.EXTRA_NDEF_MESSAGES);
//            NdefMessage msgs[] = null;
//            int contentSize = 0;
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                    contentSize += msgs[i].toByteArray().length;
//                }
//            }
//            try {
//                if (msgs != null) {
//                    NdefRecord record = msgs[0].getRecords()[0];
//                    String textRecord = parseTextRecord(record);
//                    String tagText = textRecord + "\n\ntext\n" + contentSize + " bytes";
//                    tv3.setText(tagText);
//                }
//            } catch (Exception e) {
//                Log.d(TAG, "" + e);
//            }
//        }
//    }
//
//    /**
//     * 解析NDEF文本数据，从第三个字节开始，后面的文本数据
//     *
//     * @param ndefRecord
//     * @return
//     */
//    public static String parseTextRecord(NdefRecord ndefRecord) {
//        /**
//         * 判断数据是否为NDEF格式
//         */
//        //判断TNF
//        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN) {
//            return null;
//        }
//        //判断可变的长度的类型
//        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
//            return null;
//        }
//        try {
//            //获得字节数组，然后进行分析
//            byte[] payload = ndefRecord.getPayload();
//            //下面开始NDEF文本数据第一个字节，状态字节
//            //判断文本是基于UTF-8还是UTF-16的，取第一个字节"位与"上16进制的80，16进制的80也就是最高位是1，
//            //其他位都是0，所以进行"位与"运算后就会保留最高位
//            String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";
//            //3f最高两位是0，第六位是1，所以进行"位与"运算后获得第六位
//            int languageCodeLength = payload[0] & 0x3f;
//            //下面开始NDEF文本数据第二个字节，语言编码
//            //获得语言编码
//            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
//            //下面开始NDEF文本数据后面的字节，解析出文本
//            String textRecord = new String(payload, languageCodeLength + 1,
//                    payload.length - languageCodeLength - 1, textEncoding);
//            return textRecord;
//        } catch (Exception e) {
//            throw new IllegalArgumentException();
//        }
//    }
//
//    // 写数据
//    public void writeTag(Tag tag, String str) {
//        MifareClassic mfc = MifareClassic.get(tag);
//
//        try {
//            if (mfc != null) {
//                mfc.connect();
//            } else {
//                Toast.makeText(mContext, "写入失败", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Log.i("write", "----connect-------------");
//            boolean CodeAuth = false;
//            byte[] b1 = str.getBytes();
//            if (b1.length <= 720) {
//                //System.out.println("------b1.length:" + b1.length);
//                int num = b1.length / 16;
//                System.out.println("num= " + num);
//                int next = b1.length / 48 + 1;
//                System.out.println("扇区next的值为" + next);
//                b0 = new byte[16];
//                if (!(b1.length % 16 == 0)) {
//                    for (int i = 1, j = 1; i <= num; i++) {
//                        CodeAuth = mfc.authenticateSectorWithKeyA(j, code);
//                        System.arraycopy(b1, 16 * (i - 1), b0, 0, 16);
//                        mfc.writeBlock(block[i - 1], b0);
//                        if (i % 3 == 0) {
//                            j++;
//                        }
//                    }
//                    //Log.d("下一个模块", "测试");
//                    CodeAuth = mfc.authenticateSectorWithKeyA(next,// 非常重要------
//                            code);
//                    //Log.d("获取第5块的密码", "---成功-------");
//                    byte[] b2 = { 0 };
//                    b0 = new byte[16];
//                    System.arraycopy(b1, 16 * num, b0, 0, b1.length % 16);
//                    System.arraycopy(b2, 0, b0, b1.length % 16, b2.length);
//                    mfc.writeBlock(block[num], b0);
//                    mfc.close();
//                    Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
//                    return;
//                } else {
//                    for (int i = 1, j = 1; i <= num; i++) {
//                        if (i % 3 == 0) {
//                            j++;
//                            System.out.println("扇区j的值为：" + j);
//                        }
//                        CodeAuth = mfc.authenticateSectorWithKeyA(j,// 非常重要---------
//                                code);
//                        System.arraycopy(b1, 16 * (i - 1), b0, 0, 16);
//                        mfc.writeBlock(block[i - 1], b0);
//                        str += ByteArrayChange.ByteArrayToHexString(b0);
//                        System.out.println("Block" + i + ": " + str);
//                    }
//                    mfc.close();
//                    Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            } else {
//                Toast.makeText(getBaseContext(), "字符过长，内存不足", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//            try {
//                mfc.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // //读取数据
//    public String readTag(Tag tag) {
//        MifareClassic mfc = MifareClassic.get(tag);
//        for (String tech : tag.getTechList()) {
//            System.out.println(tech);// 显示设备支持技术
//        }
//        boolean auth = false;
//        // 读取TAG
//
//        try {
//            // metaInfo.delete(0, metaInfo.length());//清空StringBuilder;
//            StringBuilder metaInfo = new StringBuilder();
//            // Enable I/O operations to the tag from this TagTechnology object.
//            mfc.connect();
//            int type = mfc.getType();// 获取TAG的类型
//            int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数
//            String typeS = "";
//            switch (type) {
//                case MifareClassic.TYPE_CLASSIC:
//                    typeS = "TYPE_CLASSIC";
//                    break;
//                case MifareClassic.TYPE_PLUS:
//                    typeS = "TYPE_PLUS";
//                    break;
//                case MifareClassic.TYPE_PRO:
//                    typeS = "TYPE_PRO";
//                    break;
//                case MifareClassic.TYPE_UNKNOWN:
//                    typeS = "TYPE_UNKNOWN";
//                    break;
//
//            }
//            metaInfo.append("  卡片类型：" + typeS + "\n共" + sectorCount + "个扇区\n共"
//                    + mfc.getBlockCount() + "个块\n存储空间: " + mfc.getSize()
//                    + "B\n");
//            for (int j = 0; j < sectorCount; j++) {
//                // Authenticate a sector with key A.
//                auth = mfc.authenticateSectorWithKeyA(j,
//                        MifareClassic.KEY_NFC_FORUM);// 逐个获取密码
//                /*
//                 * byte[]
//                 * codeByte_Default=MifareClassic.KEY_DEFAULT;//FFFFFFFFFFFF
//                 * byte[]
//                 * codeByte_Directory=MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY
//                 * ;//A0A1A2A3A4A5 byte[]
//                 * codeByte_Forum=MifareClassic.KEY_NFC_FORUM;//D3F7D3F7D3F7
//                 */if (auth) {
//                    metaInfo.append("Sector " + j + ":验证成功\n");
//                    // 读取扇区中的块
//                    bCount = mfc.getBlockCountInSector(j);
//                    bIndex = mfc.sectorToBlock(j);
//                    for (int i = 0; i < bCount; i++) {
//                        byte[] data = mfc.readBlock(bIndex);
//                        metaInfo.append("Block " + bIndex + " : "
//                                + ByteArrayChange.ByteArrayToHexString(data)
//                                + "\n");
//                        bIndex++;
//                    }
//                } else {
//                    metaInfo.append("Sector " + j + ":验证失败\n");
//                }
//            }
//            return metaInfo.toString();
//        } catch (Exception e) {
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        } finally {
//            if (mfc != null) {
//                try {
//                    mfc.close();
//                } catch (IOException e) {
//                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
//                            .show();
//                }
//            }
//        }
//        return null;
//
//    }
//
//    // 转换Hex为字符串
//    public String change(Tag tag) {
//        MifareClassic mfc = MifareClassic.get(tag);
//        Log.d("----------", "change----------");
//        boolean auth = false;
//        // 读取TAG
//
//        String ChangeInfo = "";
//        String Ascll = "";
//        // Enable I/O operations to the tag from this TagTechnology object.
//        try {
//            mfc.connect();
//            int sectorCount = mfc.getSectorCount();// 获取TAG中包含的扇区数
//            for (int j = 1; j < sectorCount; j++) {
//                // Authenticate a sector with key A.
//                auth = mfc.authenticateSectorWithKeyA(j,
//                        MifareClassic.KEY_NFC_FORUM);
//                if (auth) {
//                    Log.i("change 的auth验证成功", "开始读取模块信息");
//
//                    byte[] data0 = mfc.readBlock(4 * j);
//                    byte[] data1 = mfc.readBlock(4 * j + 1);
//                    byte[] data2 = mfc.readBlock(4 * j + 2);
//                    data3 = new byte[data0.length + data1.length + data2.length];
//                    System.arraycopy(data0, 0, data3, 0, data0.length);
//                    System.arraycopy(data1, 0, data3, data0.length,
//                            data1.length);
//                    System.arraycopy(data2, 0, data3, data0.length
//                            + data1.length, data2.length);
//
//                    ChangeInfo = ByteArrayChange.ByteArrayToHexString(data3);
//                    temp = "扇区" + (j) + "里的内容为："
//                            + ToStringHex.decode(ChangeInfo) + "\n";
//                }
//                Ascll += temp;
//            }
//            return Ascll;
//        } catch (IOException e) {
//            // TODO 自动生成的 catch 块
//            e.printStackTrace();
//            Toast.makeText(getBaseContext(), "转换失败", Toast.LENGTH_SHORT).show();
//        } finally {
//            if (mfc != null) {
//                try {
//                    mfc.close();
//                } catch (IOException e) {
//                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG)
//                            .show();
//                }
//            }
//        }
//        return "";
//    }
//
//    public int StringToInt(String s) {
//        if (!(TextUtils.isEmpty(s)) || s.length() > 0) {
//            BlockData = Integer.parseInt(s);
//        } else {
//            Toast.makeText(NFCActivity.this, "Block输入有误", Toast.LENGTH_SHORT).show();
//        }
//        System.out.println(BlockData);
//        return BlockData;
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        disableForegroundDispatch();
//    }
//
//    private void disableForegroundDispatch() {
//        // TODO 自动生成的方法存根
//        if (mNfcAdapter != null) {
//            mNfcAdapter.disableForegroundDispatch(this);
//        }
//    }
//
//    public Tag patchTag(Tag oTag)
//    {
//        if (oTag == null)
//            return null;
//
//        String[] sTechList = oTag.getTechList();
//
//        Parcel oParcel, nParcel;
//
//        oParcel = Parcel.obtain();
//        oTag.writeToParcel(oParcel, 0);
//        oParcel.setDataPosition(0);
//
//        int len = oParcel.readInt();
//        byte[] id = null;
//        Log.d(TAG, "oParcel.readInt() = " + len);
//        if (len >= 0)
//        {
//            id = new byte[len];
//            oParcel.readByteArray(id);
//        }
//        int[] oTechList = new int[oParcel.readInt()];
//        oParcel.readIntArray(oTechList);
//        Bundle[] oTechExtras = oParcel.createTypedArray(Bundle.CREATOR);
//        int serviceHandle = oParcel.readInt();
//        int isMock = oParcel.readInt();
//        IBinder tagService;
//        if (isMock == 0)
//        {
//            tagService = oParcel.readStrongBinder();
//        }
//        else
//        {
//            tagService = null;
//        }
//        oParcel.recycle();
//
//        int nfca_idx=-1;
//        int mc_idx=-1;
//
//        String tmp = "";
//        for(int idx = 0; idx < sTechList.length; idx++)
//        {
//            tmp += sTechList[idx] + ", ";
//            Log.d(TAG, "sTechList[idx] = " + sTechList[idx]);
//            if(sTechList[idx].equals(NfcA.class.getName()))
//            {
//                nfca_idx = idx;
//            }
//            else if(sTechList[idx].equals(MifareClassic.class.getName()))
//            {
//                mc_idx = idx;
//            }
//        }
//
//        if(nfca_idx>=0&&mc_idx>=0&&oTechExtras[mc_idx]==null)
//        {
//            Toast.makeText(NFCActivity.this, "Tell fjq: patch success" + tmp,Toast.LENGTH_SHORT).show();
//            oTechExtras[mc_idx] = oTechExtras[nfca_idx];
//        }
//        else
//        {
//            Toast.makeText(NFCActivity.this, "Tell fjq: patch fail" + tmp,Toast.LENGTH_SHORT).show();
//            return oTag;
//        }
//
//        nParcel = Parcel.obtain();
//        nParcel.writeInt(id.length);
//        nParcel.writeByteArray(id);
//        nParcel.writeInt(oTechList.length);
//        nParcel.writeIntArray(oTechList);
//        nParcel.writeTypedArray(oTechExtras,0);
//        nParcel.writeInt(serviceHandle);
//        nParcel.writeInt(isMock);
//        if(isMock==0)
//        {
//            nParcel.writeStrongBinder(tagService);
//        }
//        nParcel.setDataPosition(0);
//
//        Tag nTag = Tag.CREATOR.createFromParcel(nParcel);
//
//        nParcel.recycle();
//
//        return nTag;
//    }
}  
