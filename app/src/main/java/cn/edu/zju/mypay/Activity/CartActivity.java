package cn.edu.zju.mypay.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.flipboard.bottomsheet.BottomSheetLayout;

import cn.edu.zju.mypay.HttpUtils;
import cn.edu.zju.mypay.adapter.CatograyAdapter;
import cn.edu.zju.mypay.adapter.GoodsAdapter;
import cn.edu.zju.mypay.adapter.GoodsDetailAdapter;
import cn.edu.zju.mypay.adapter.ProductAdapter;
import cn.edu.zju.mypay.bean.CatograyBean;
import cn.edu.zju.mypay.bean.GoodsBean;
import cn.edu.zju.mypay.bean.ItemBean;
import cn.edu.zju.mypay.view.MyListView;
import cn.edu.zju.mypay.R;

import static android.content.ContentValues.TAG;

public class CartActivity extends Activity {
    //控件
    private ListView lv_catogary, lv_good;
    private ImageView iv_logo;
    private TextView tv_car;
    private TextView tv_totle_money;
    private Button tv_count;
    Double totalMoney = 0.00;
    private TextView bv_unm;
    private RelativeLayout rl_bottom;
    //分类和商品
    private List<CatograyBean> list = new ArrayList<CatograyBean>();
    private List<GoodsBean> list2 = new ArrayList<GoodsBean>();
    private CatograyAdapter catograyAdapter;//分类的adapter
    private GoodsAdapter goodsAdapter;//分类下商品adapter
    ProductAdapter productAdapter;//底部购物车的adapter
    GoodsDetailAdapter goodsDetailAdapter;//套餐详情的adapter
    private static DecimalFormat df;
    private LinearLayout ll_shopcar;
    //底部数据
    private BottomSheetLayout bottomSheetLayout;
    private View bottomSheet;
    private SparseArray<GoodsBean> selectedList;
    //套餐
    private View bottomDetailSheet;
    private List<GoodsBean> list3 = new ArrayList<GoodsBean>();
    private List<GoodsBean> list4 = new ArrayList<GoodsBean>();
    private List<GoodsBean> list5 = new ArrayList<GoodsBean>();

    private Handler mHanlder;
    private ViewGroup anim_mask_layout;//动画层

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        getApplicationContext();
        mHanlder = new Handler(getMainLooper());
        initView();
        initData();
        addListener();
        ll_shopcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
    }

    public void initView() {
        lv_catogary = (ListView) findViewById(R.id.lv_catogary);
        lv_good = (ListView) findViewById(R.id.lv_good);
        tv_car = (TextView) findViewById(R.id.tv_car);
        //底部控件
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        tv_count = (Button) findViewById(R.id.tv_count);
        tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartDone();
            }
        });
        bv_unm = (TextView) findViewById(R.id.bv_unm);
        tv_totle_money= (TextView) findViewById(R.id.tv_totle_money);
        ll_shopcar= (LinearLayout) findViewById(R.id.ll_shopcar);
        selectedList = new SparseArray<>();
        df = new DecimalFormat("0.00");
    }

    private void cartDone() {
        sendOrderForm();

        Intent intent = new Intent(CartActivity.this, PayActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendOrderForm() {
        StringBuffer orderList = new StringBuffer("");
        int size = selectedList.size();
        Float total_money = 0f;
        for(int i=0;i<size;i++){
            GoodsBean item = selectedList.valueAt(i);
            orderList.append(item.getProduct_id()+"*"+item.getNum()+",");
            total_money += item.getNum()*Float.parseFloat(item.getPrice());
        }
        SharedPreferences sp = getSharedPreferences(getString(R.string.cookie_preference_file), MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("totalMoney", String.valueOf(total_money));
        Log.d(TAG, "String.valueOf(total_money)=" + String.valueOf(total_money));
        editor.putString("orderList", new String(orderList));
        editor.apply();
//        Toast.makeText(CartActivity.this, "下单："+sp.getString("orderList", "..."),Toast.LENGTH_SHORT).show();

        String savedCardId = sp.getString(getString(R.string.saved_card_id), "");

        // TODO: delete next line and add a real URL
        if (true) return;
        String url="http://?.?.?.?/orderForm";
        Map<String,String> params = new HashMap<String, String>();
        params.put("order", new String(orderList));
        params.put("totalMoney", totalMoney.toString());
        params.put("user", savedCardId);
        final String result = HttpUtils.submitPostData(url,params,"utf-8");

        if(result.equals("200")){
            Toast.makeText(CartActivity.this, "下单成功，请您稍后到店使用二维码/NFC付款",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CartActivity.this, "下单失败，请检查网络",Toast.LENGTH_SHORT).show();
        }
    }

    //填充数据
    private void initData() {
        //商品
        List<String> name1 = new ArrayList<>();
        List<String> icon1 = new ArrayList<>();
        List<String> orig1 = new ArrayList<>();
        List<String> price1 = new ArrayList<>();
        name1.add("可口可乐");
        icon1.add("drawable://" + R.drawable.good_coke);
        orig1.add("3.0"); price1.add("2.6");
        name1.add("雪碧");
        icon1.add("drawable://" + R.drawable.good_sprite);
        orig1.add("2.5"); price1.add("2.1");
        name1.add("美汁源橙汁");
        icon1.add("drawable://" + R.drawable.good_juice);
        orig1.add("4.0"); price1.add("3.9");
        name1.add("七喜");
        icon1.add("drawable://" + R.drawable.good_qixi);
        orig1.add("4.0"); price1.add("3.9");
        name1.add("营养快线");
        icon1.add("drawable://" + R.drawable.good_nutri);
        orig1.add("4.0"); price1.add("4.0");
        name1.add("加多宝凉茶");
        icon1.add("drawable://" + R.drawable.good_jiaduobao);
        orig1.add("3.5"); price1.add("3.5");
        name1.add("芬达");
        icon1.add("drawable://" + R.drawable.good_fenda);
        orig1.add("2.5"); price1.add("2.5");
        name1.add("芒果汁");
        icon1.add("drawable://" + R.drawable.good_mango);
        orig1.add("5.0"); price1.add("5");
        name1.add("绿茶");
        icon1.add("drawable://" + R.drawable.good_greentea);
        orig1.add("6.0"); price1.add("4.8");
        for (int j=0;j<name1.size();j++){
            GoodsBean goodsBean = new GoodsBean();
            goodsBean.setTitle(name1.get(j));
            goodsBean.setProduct_id(100+j);
            goodsBean.setCategory_id(j);
            goodsBean.setIcon(icon1.get(j));
            goodsBean.setOriginal_price(orig1.get(j));
            goodsBean.setPrice(price1.get(j));
            list3.add(goodsBean);
        }

        name1.clear();
        icon1.clear();
        orig1.clear();
        price1.clear();
        name1.add("香蕉");
        icon1.add("drawable://" + R.drawable.good_banana);
        orig1.add("3.0"); price1.add("2.6");
        //商品
        for (int j=0;j<20;j++){
            GoodsBean goodsBean = new GoodsBean();
            goodsBean.setTitle(name1.get(0));
            goodsBean.setProduct_id(200+j);
            goodsBean.setCategory_id(j);
            goodsBean.setIcon(icon1.get(0));
            goodsBean.setOriginal_price(orig1.get(0));
            goodsBean.setPrice(price1.get(0));
            list4.add(goodsBean);
        }

        name1.clear();
        icon1.clear();
        orig1.clear();
        price1.clear();
        name1.add("包子");
        icon1.add("drawable://" + R.drawable.good_baozi);
        orig1.add("3.0"); price1.add("2.6");
        //商品
        for (int j=0;j<20;j++){
            GoodsBean goodsBean = new GoodsBean();
            goodsBean.setTitle(name1.get(0));
            goodsBean.setProduct_id(300+j);
            goodsBean.setCategory_id(j);
            goodsBean.setIcon(icon1.get(0));
            goodsBean.setOriginal_price(orig1.get(0));
            goodsBean.setPrice(price1.get(0));
            list5.add(goodsBean);
        }


        CatograyBean catograyBean3 = new CatograyBean();
        catograyBean3.setCount(3);
        catograyBean3.setKind("饮品");
        catograyBean3.setList(list3);
        list.add(catograyBean3);

        CatograyBean catograyBean4 = new CatograyBean();
        catograyBean4.setCount(4);
        catograyBean4.setKind("水果");
        catograyBean4.setList(list4);
        list.add(catograyBean4);

        CatograyBean catograyBean5 = new CatograyBean();
        catograyBean5.setCount(5);
        catograyBean5.setKind("小吃");
        catograyBean5.setList(list5);
        list.add(catograyBean5);
        bottomSheetLayout = (BottomSheetLayout) findViewById(R.id.bottomSheetLayout);

        //默认值
        list2.clear();
        list2.addAll(list.get(0).getList());

        //分类
        catograyAdapter = new CatograyAdapter(this, list);
        lv_catogary.setAdapter(catograyAdapter);
        catograyAdapter.notifyDataSetChanged();
        //商品
        goodsAdapter = new GoodsAdapter(this, list2, catograyAdapter);
        lv_good.setAdapter(goodsAdapter);
        goodsAdapter.notifyDataSetChanged();

    }

    //添加监听
    private void addListener() {
        lv_catogary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("fyg","list.get(position).getList():"+list.get(position).getList());
                list2.clear();
                list2.addAll(list.get(position).getList());
                catograyAdapter.setSelection(position);
                catograyAdapter.notifyDataSetChanged();
                goodsAdapter.notifyDataSetChanged();
            }
        });
    }

    //创建套餐详情view
    public void showDetailSheet(List<ItemBean> listItem, String mealName){
        bottomDetailSheet = createMealDetailView(listItem,mealName);
        if(bottomSheetLayout.isSheetShowing()){
            bottomSheetLayout.dismissSheet();
        }else {
            if(listItem.size()!=0){
                bottomSheetLayout.showWithSheetView(bottomDetailSheet);
            }
        }
    }

    //查看套餐详情
    private View createMealDetailView(List<ItemBean> listItem, String mealName){
        View view = LayoutInflater.from(this).inflate(R.layout.activity_goods_detail,(ViewGroup) getWindow().getDecorView(),false);
        ListView lv_product = (MyListView) view.findViewById(R.id.lv_product);
        TextView tv_meal = (TextView) view.findViewById(R.id.tv_meal);
        TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
        int count=0;
        for(int i=0;i<listItem.size();i++){
            count = count+Integer.parseInt(listItem.get(i).getNote2());
        }
        tv_meal.setText(mealName);
        tv_num.setText("(共"+count+"件)");
        goodsDetailAdapter = new GoodsDetailAdapter(CartActivity.this,listItem);
        lv_product.setAdapter(goodsDetailAdapter);
        goodsDetailAdapter.notifyDataSetChanged();
        return view;
    }

    //创建购物车view
    private void showBottomSheet(){
        bottomSheet = createBottomSheetView();
        if(bottomSheetLayout.isSheetShowing()){
            bottomSheetLayout.dismissSheet();
        }else {
            if(selectedList.size()!=0){
                bottomSheetLayout.showWithSheetView(bottomSheet);
            }
        }
    }




    //查看购物车布局
    private View createBottomSheetView(){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet,(ViewGroup) getWindow().getDecorView(),false);
        MyListView lv_product = (MyListView) view.findViewById(R.id.lv_product);
        TextView clear = (TextView) view.findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCart();
            }
        });
        productAdapter = new ProductAdapter(CartActivity.this,goodsAdapter, selectedList);
        lv_product.setAdapter(productAdapter);
        return view;
    }

    //清空购物车
    public void clearCart(){
        selectedList.clear();
        list2.clear();
        if (list.size() > 0) {
            for (int j=0;j<list.size();j++){
                list.get(j).setCount(0);
                for(int i=0;i<list.get(j).getList().size();i++){
                    list.get(j).getList().get(i).setNum(0);
                }
            }
            list2.addAll(list.get(0).getList());
            catograyAdapter.setSelection(0);
            //刷新不能删
            catograyAdapter.notifyDataSetChanged();
            goodsAdapter.notifyDataSetChanged();
        }
        update(true);
    }


    //根据商品id获取当前商品的采购数量
    public int getSelectedItemCountById(int id){
        GoodsBean temp = selectedList.get(id);
        if(temp==null){
            return 0;
        }
        return temp.getNum();
    }


    public void handlerCarNum(int type, GoodsBean goodsBean, boolean refreshGoodList){
        if (type == 0) {
            GoodsBean temp = selectedList.get(goodsBean.getProduct_id());
            if(temp!=null){
                if(temp.getNum()<2){
                    goodsBean.setNum(0);
                    selectedList.remove(goodsBean.getProduct_id());

                }else{
                    int i =  goodsBean.getNum();
                    goodsBean.setNum(--i);
                }
            }



        } else if (type == 1) {
            GoodsBean temp = selectedList.get(goodsBean.getProduct_id());
            if(temp==null){
                goodsBean.setNum(1);
                selectedList.append(goodsBean.getProduct_id(), goodsBean);
            }else{
                int i= goodsBean.getNum();
                goodsBean.setNum(++i);
            }
        }

        update(refreshGoodList);

    }



    //刷新布局 总价、购买数量等
    private void update(boolean refreshGoodList){
        int size = selectedList.size();
        int count =0;
        for(int i=0;i<size;i++){
            GoodsBean item = selectedList.valueAt(i);
            count += item.getNum();
            totalMoney += item.getNum()*Double.parseDouble(item.getPrice());
        }
        tv_totle_money.setText("￥"+String.valueOf(df.format(totalMoney)));
        totalMoney = 0.00;
        if(count<1){
            bv_unm.setVisibility(View.GONE);
        }else{
            bv_unm.setVisibility(View.VISIBLE);
        }

        bv_unm.setText(String.valueOf(count));

        if(productAdapter!=null){
            productAdapter.notifyDataSetChanged();
        }

        if(goodsAdapter!=null){
            goodsAdapter.notifyDataSetChanged();
        }

        if(catograyAdapter!=null){
            catograyAdapter.notifyDataSetChanged();
        }

        if(bottomSheetLayout.isSheetShowing() && selectedList.size()<1){
            bottomSheetLayout.dismissSheet();
        }
    }


    /**
     * @Description: 创建动画层
     * @param
     * @return void
     * @throws
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE-1);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    private View addViewToAnimLayout(final ViewGroup parent, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    public void setAnim(final View v, int[] startLocation) {
        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(v);//把动画小球添加到动画层
        final View view = addViewToAnimLayout(anim_mask_layout, v, startLocation);
        int[] endLocation = new int[2];// 存储动画结束位置的X、Y坐标
        tv_car.getLocationInWindow(endLocation);
        // 计算位移
        int endX = 0 - startLocation[0] + 40;// 动画位移的X坐标
        int endY = endLocation[1] - startLocation[1];// 动画位移的y坐标

        TranslateAnimation translateAnimationX = new TranslateAnimation(0,endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0, 0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationY.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);// 动画的执行时间
        view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }
        });

    }
}
