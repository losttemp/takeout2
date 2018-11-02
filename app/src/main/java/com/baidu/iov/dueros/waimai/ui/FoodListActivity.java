package com.baidu.iov.dueros.waimai.ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.adapter.PoifoodSpusListAdapter;
import com.baidu.iov.dueros.waimai.adapter.ShoppingCartAdapter;
import com.baidu.iov.dueros.waimai.interfacedef.IShoppingCartToDetailListener;
import com.baidu.iov.dueros.waimai.net.entity.response.PoidetailinfoBean;
import com.baidu.iov.dueros.waimai.net.entity.response.PoifoodListBean;
import com.baidu.iov.dueros.waimai.presenter.PoifoodListPresenter;
import com.baidu.iov.dueros.waimai.utils.DoubleUtil;
import com.baidu.iov.dueros.waimai.utils.Lg;
import com.baidu.iov.dueros.waimai.view.PoifoodListPinnedHeaderListView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends BaseActivity<PoifoodListPresenter, PoifoodListPresenter.PoifoodListUi> implements PoifoodListPresenter.PoifoodListUi, View.OnClickListener, PoifoodSpusListAdapter.onCallBackListener, IShoppingCartToDetailListener {
    private static final String TAG = FoodListActivity.class.getSimpleName();
    public static final String POI_INFO = "poi_info";
    public static final String PRODUCT_LIST_BEAN = "product_list_bean";

    private boolean isScroll = true;
    private ListView mFoodSpuTagsList;
    private PoifoodListPinnedHeaderListView mSpusList;
    private PoifoodSpusListAdapter mPoifoodSpusListAdapter;
    private List<PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean> productList;
    private TextView shoppingPrise;
    private TextView shoppingNum;
    private TextView settlement;
    private FrameLayout cardLayout;
    private LinearLayout cardShopLayout;
    private View bg_layout;
    private ImageView shopping_cart;
    private int AnimationDuration = 500;
    private int number = 0;
    private boolean isClean = false;
    private FrameLayout animation_viewGroup;
    private TextView defaultText;
    private List<String> foodSpuTagsBeanName;
    private RelativeLayout parentLayout;
    private TextView noData;
    private List<PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean> foodSpuTagsBeans = new ArrayList<>();
    private List<PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean> spusBeanList;
    private ListView shoppingListView;
    private ShoppingCartAdapter shoppingCartAdapter;
    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    try {
                        animation_viewGroup.removeAllViews();
                    } catch (Exception e) {

                    }
                    isClean = false;

                    break;
                default:
                    break;
            }
        }
    };
    private ArrayAdapter<String> mFoodSpuTagsListAdapter;
    private RelativeLayout mStoreDetails;
    private TextView mClearshopCart;
    private PoidetailinfoBean mPoidetailinfoBean;
    private PoifoodListBean.MeituanBean.DataBean.PoiInfoBean mPoiInfoBean;

    @Override
    PoifoodListPresenter createPresenter() {
        return new PoifoodListPresenter();
    }

    @Override
    PoifoodListPresenter.PoifoodListUi getUi() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        initView();
        initData();
    }

    private void initView() {
        animation_viewGroup = createAnimLayout();
        noData = (TextView) findViewById(R.id.noData);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
        shoppingPrise = (TextView) findViewById(R.id.shoppingPrise);
        shoppingNum = (TextView) findViewById(R.id.shoppingNum);
        settlement = (TextView) findViewById(R.id.settlement);
        mFoodSpuTagsList = (ListView) findViewById(R.id.classify_mainlist);
        mSpusList = (PoifoodListPinnedHeaderListView) findViewById(R.id.classify_morelist);
        shopping_cart = (ImageView) findViewById(R.id.shopping_cart);
        defaultText = (TextView) findViewById(R.id.defaultText);
        shoppingListView = (ListView) findViewById(R.id.shopproductListView);
        cardLayout = (FrameLayout) findViewById(R.id.cardLayout);
        cardShopLayout = (LinearLayout) findViewById(R.id.cardShopLayout);
        bg_layout = findViewById(R.id.bg_layout);
        mStoreDetails = (RelativeLayout) findViewById(R.id.rl_store_details);
        mClearshopCart = (TextView) findViewById(R.id.tv_clear);
    }

    public void initData() {
        productList = new ArrayList<>();
        foodSpuTagsBeanName = new ArrayList<>();
        mPoifoodSpusListAdapter = new PoifoodSpusListAdapter(this, foodSpuTagsBeans,FoodListActivity.this);
        mPoifoodSpusListAdapter.SetOnSetHolderClickListener(new PoifoodSpusListAdapter.HolderClickListener() {
            @Override
            public void onHolderClick(Drawable drawable, int[] start_location) {
                doAnim(drawable, start_location);
            }
        });

        mSpusList.setAdapter(mPoifoodSpusListAdapter);
        mPoifoodSpusListAdapter.setCallBackListener(this);
        mFoodSpuTagsListAdapter = new ArrayAdapter<>(this, R.layout.categorize_item, foodSpuTagsBeanName);
        mFoodSpuTagsList.setAdapter(mFoodSpuTagsListAdapter);

        shoppingCartAdapter = new ShoppingCartAdapter(this, productList);
        shoppingListView.setAdapter(shoppingCartAdapter);
        shoppingCartAdapter.setShopToDetailListener(this);

        mFoodSpuTagsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {
                isScroll = false;
                Lg.getInstance().d(TAG, "mFoodSpuTagsList.getChildCount() = " + mFoodSpuTagsList.getChildCount());
                for (int i = 0; i < mFoodSpuTagsList.getChildCount(); i++) {
                    if (i == position) {
                        mFoodSpuTagsList.getChildAt(i).setBackgroundColor(
                                Color.rgb(255, 255, 255));
                    } else {
                        mFoodSpuTagsList.getChildAt(i).setBackgroundColor(
                                Color.TRANSPARENT);
                    }
                }
                int rightSection = 0;
                for (int i = 0; i < position; i++) {
                    rightSection += mPoifoodSpusListAdapter.getCountForSection(i) + 1;
                }
                mSpusList.setSelection(rightSection);
            }

        });

        mSpusList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isScroll) {
                    for (int i = 0; i < mFoodSpuTagsList.getChildCount(); i++) {
                        if (i == mPoifoodSpusListAdapter
                                .getSectionForPosition(firstVisibleItem)) {
                            mFoodSpuTagsList.getChildAt(i).setBackgroundColor(
                                    Color.rgb(255, 255, 255));
                        } else {
                            mFoodSpuTagsList.getChildAt(i).setBackgroundColor(
                                    Color.TRANSPARENT);
                        }
                    }
                } else {
                    isScroll = true;
                }
            }
        });

        mStoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//TODO
                View popView = getPopView(R.layout.dialog_shop_details);
//                View popView = getPopView(R.layout.dialog_spus_specifications);
//                GridView specificationsList = (GridView) popView.findViewById(R.id.gv_specifications);
//                final String[] string = {"椒盐大鸡排", "椒盐大鸡排", "椒盐大鸡排", "椒盐大鸡排", "椒盐大鸡排", "椒盐大鸡排"};
//                specificationsList.setAdapter(new ArrayAdapter<String>(FoodListActivity.this, R.layout.categorize_item, string));
//                specificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        Toast.makeText(FoodListActivity.this, string[i], Toast.LENGTH_SHORT).show();
//                    }
//                });
                showFoodListActivityDialog(view, popView);
            }
        });

        bg_layout.setOnClickListener(this);
        settlement.setOnClickListener(this);
        shopping_cart.setOnClickListener(this);
        mClearshopCart.setOnClickListener(this);

//        long wmPoiId = (long) getIntent().getExtras().get(Constant.STORE_ID);
//        Map map = new HashMap();
//        map.put()
        getPresenter().requestData(null);
    }

    public void showFoodListActivityDialog(View view, View contentView) {
        final PopupWindow window = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAtLocation(view, Gravity.TOP, 0, 0);
        backgroundAlpha(0.5f);
        ImageView dismiss = (ImageView) contentView.findViewById(R.id.iv_dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                window.dismiss();
            }
        });
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
    }

    public View getPopView(int layoutId) {
        return LayoutInflater.from(this).inflate(layoutId, null, false);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    @Override
    public void updateProduct(PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean spusBean, String tag) {
        String spusBeanTag = spusBean.getTag();
        Lg.getInstance().d(TAG, "updateProduct tag = " + tag + "; spusBeanTag = " + spusBeanTag);
        if (tag.equals(spusBeanTag)) {
            Lg.getInstance().d(TAG, "productList.contains(spusBean) = " + productList.contains(spusBean));
            if (productList.contains(spusBean)) {
                if (spusBean.getNumber() == 0) {
                    productList.remove(spusBean);
                } else {
                    for (PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean shopProduct : productList) {
                        Lg.getInstance().d(TAG, "shopProduct.getId() = " + shopProduct.getId() + "; spusBean.getId() = " + spusBean.getId());
                        if (spusBean.getId() == shopProduct.getId()) {
                            Lg.getInstance().d(TAG, "shopProduct.getNumber() = " + shopProduct.getNumber());
                            shopProduct.setNumber(shopProduct.getNumber());
                        }
                    }
                }
            } else {
                Lg.getInstance().d(TAG,"updateProduct else");
                productList.add(spusBean);
            }
        }
        shoppingCartAdapter.notifyDataSetChanged();
        setPrise();
    }

    @Override
    public void onUpdateDetailList(PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean spusBean, String tag) {
        String spusBeanTag = spusBean.getTag();
        Lg.getInstance().d(TAG, "onUpdateDetailList tag = " + tag + "; spusBeanTag = " + spusBeanTag);
        if (tag.equals(spusBeanTag)) {
            for (int i = 0; i < foodSpuTagsBeans.size(); i++) {
                spusBeanList = foodSpuTagsBeans.get(i).getSpus();
                for (PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean shopProduct : spusBeanList) {
                    if (spusBean.getId() == shopProduct.getId()) {
                        shopProduct.setNumber(spusBean.getNumber());
                    }
                }
            }
        }
        mPoifoodSpusListAdapter.notifyDataSetChanged();
        setPrise();
    }

    @Override
    public void onRemovePriduct(PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean product) {
        for (int i = 0; i < foodSpuTagsBeans.size(); i++) {
            spusBeanList = foodSpuTagsBeans.get(i).getSpus();
            for (PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean shopProduct : spusBeanList) {
                if (product.getId() == shopProduct.getId()) {
                    productList.remove(product);
                    shoppingCartAdapter.notifyDataSetChanged();
                    shopProduct.setNumber(shopProduct.getNumber());
                }
            }
        }
        mPoifoodSpusListAdapter.notifyDataSetChanged();
        shoppingCartAdapter.notifyDataSetChanged();
        setPrise();
    }

    public void setPrise() {
        double sum = 0;
        int shopNum = 0;
        for (PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean pro : productList) {
            sum = DoubleUtil.sum(sum, DoubleUtil.mul((double) pro.getNumber(), Double.parseDouble("" + pro.getMin_price())));
            shopNum = shopNum + pro.getNumber();
        }
        if (shopNum > 0) {
            shoppingNum.setVisibility(View.VISIBLE);
        } else {
            shoppingNum.setVisibility(View.GONE);
        }
        if (sum > 0) {
            shoppingPrise.setVisibility(View.VISIBLE);
        } else {
            shoppingPrise.setVisibility(View.GONE);
        }
        shoppingPrise.setText("¥" + " " + (new DecimalFormat("0.00")).format(sum));
        shoppingNum.setText(String.valueOf(shopNum));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shopping_cart:
                if (productList.isEmpty() || productList == null) {
                    defaultText.setVisibility(View.VISIBLE);
                } else {
                    defaultText.setVisibility(View.GONE);
                }

                if (cardLayout.getVisibility() == View.GONE) {
                    cardLayout.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_bottom_in);
                    cardShopLayout.setVisibility(View.VISIBLE);
                    cardShopLayout.startAnimation(animation);
                    bg_layout.setVisibility(View.VISIBLE);

                } else {
                    cardLayout.setVisibility(View.GONE);
                    bg_layout.setVisibility(View.GONE);
                    cardShopLayout.setVisibility(View.GONE);
                }
                break;

            case R.id.settlement:
                if (productList == null || productList.size() == 0) {
                    Toast.makeText(this, getString(R.string.please_add_to_the_shopping_cart_first), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, SubmitOrderActivity.class);
                //TODO 店名 包装费 配送费 优惠信息 美团专送    购物车列表（物品图片、物品名称、价格、规格、优惠活动）
                intent.putExtra(POI_INFO, mPoiInfoBean);
                intent.putExtra(PRODUCT_LIST_BEAN, (Serializable) productList);
                startActivity(intent);
                break;

            case R.id.bg_layout:
                cardLayout.setVisibility(View.GONE);
                bg_layout.setVisibility(View.GONE);
                cardShopLayout.setVisibility(View.GONE);
                break;
            case R.id.tv_clear:
                if (productList != null && productList.size() > 0) {
                    productList.clear();
                    defaultText.setVisibility(View.VISIBLE);
                    for (int i = 0; i < foodSpuTagsBeans.size(); i++) {
                        List<PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean> spus = foodSpuTagsBeans.get(i).getSpus();
                        for (int j = 0; j < spus.size(); j++) {
                            spus.get(j).setNumber(0);
                        }
                    }
                    mPoifoodSpusListAdapter.notifyDataSetChanged();
                    shoppingCartAdapter.notifyDataSetChanged();
                    setPrise();
                }
                break;
        }
    }

    private FrameLayout createAnimLayout() {
        ViewGroup rootView = (ViewGroup) getWindow().getDecorView();
        FrameLayout animLayout = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;

    }

    private void doAnim(Drawable drawable, int[] start_location) {
        if (!isClean) {
            setAnim(drawable, start_location);
        } else {
            try {
                animation_viewGroup.removeAllViews();
                isClean = false;
                setAnim(drawable, start_location);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                isClean = true;
            }
        }
    }

    @SuppressLint("NewApi")
    private void setAnim(Drawable drawable, int[] start_location) {
        Animation mScaleAnimation = new ScaleAnimation(1.2f, 0.6f, 1.2f, 0.6f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mScaleAnimation.setFillAfter(true);

        final ImageView iview = new ImageView(this);
        iview.setImageDrawable(drawable);
        final View view = addViewToAnimLayout(animation_viewGroup, iview,
                start_location);

        view.setAlpha(0.6f);

        int[] end_location = new int[2];
        settlement.getLocationInWindow(end_location);

        int endX = 0 - start_location[0] + 40;
        int endY = end_location[1] - start_location[1];
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);


        Animation mRotateAnimation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.3f, Animation.RELATIVE_TO_SELF,
                0.3f);
        mRotateAnimation.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(mRotateAnimation);
        set.addAnimation(mScaleAnimation);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(500);
        view.startAnimation(set);
        set.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                number++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                number--;
                if (number == 0) {
                    isClean = true;
                    myHandler.sendEmptyMessage(0);
                }
                ObjectAnimator.ofFloat(shopping_cart, "translationY", 0, 4, -2, 0).setDuration(400).start();
                ObjectAnimator.ofFloat(shoppingNum, "translationY", 0, 4, -2, 0).setDuration(400).start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private View addViewToAnimLayout(ViewGroup vg, View view, int[] location) {
        int x = location[0];
        int y = location[1];
        vg.addView(view);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setPadding(5, 5, 5, 5);
        view.setLayoutParams(lp);
        return view;
    }

    @Override
    public void onLowMemory() {
        isClean = true;
        try {
            animation_viewGroup.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isClean = false;
        super.onLowMemory();
    }

    @Override
    public void onPoifoodListSuccess(PoifoodListBean data) {
        foodSpuTagsBeans.clear();
        foodSpuTagsBeanName.clear();
        mPoiInfoBean = data.getMeituan().getData().getPoi_info();
        List<PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean> food_spu_tags = data.getMeituan().getData().getFood_spu_tags();
        for (int i = 0; i < food_spu_tags.size(); i++) {
            PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean foodSpuTagsBean = food_spu_tags.get(i);
            String foodSpuTagsName = foodSpuTagsBean.getName();
            foodSpuTagsBeanName.add(foodSpuTagsName);
            Lg.getInstance().d(TAG, "foodSpuTagsBeanName = " + foodSpuTagsName);
            spusBeanList = new ArrayList<>();
            List<PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean> spus = foodSpuTagsBean.getSpus();
            for (int j = 0; j < spus.size(); j++) {
                PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean spusBean = spus.get(j);
                String spusName = spusBean.getName();
                spusBeanList.add(spusBean);
                Lg.getInstance().d(TAG, "spusBeanName = " + spusName);
            }
            Lg.getInstance().d(TAG, "spusBeanList = " + spusBeanList.size());
            foodSpuTagsBean.setSpus(spusBeanList);
            foodSpuTagsBeans.add(foodSpuTagsBean);
        }
        Lg.getInstance().d(TAG, "foodSpuTagsBeanName = " + foodSpuTagsBeanName.toString());
        Lg.getInstance().d(TAG, "spusBeanName = " + spusBeanList.toString());
        mPoifoodSpusListAdapter.notifyDataSetChanged();
        mFoodSpuTagsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPoifoodListError(String error) {
        Lg.getInstance().d(TAG, "error = " + error);
    }

    @Override
    public void onPoidetailinfoSuccess(PoidetailinfoBean data) {
        mPoidetailinfoBean = data;
        Lg.getInstance().d(TAG, "onPoidetailinfoSuccess data = " + data);
    }

    @Override
    public void onPoidetailinfoError(String error) {
        Lg.getInstance().d(TAG, "onPoidetailinfoError = " + error);
    }
}
