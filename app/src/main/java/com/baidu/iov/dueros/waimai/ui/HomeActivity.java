package com.baidu.iov.dueros.waimai.ui;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.bean.MyApplicationAddressBean;
import com.baidu.iov.dueros.waimai.presenter.HomePresenter;
import com.baidu.iov.dueros.waimai.utils.AtyContainer;
import com.baidu.iov.dueros.waimai.utils.CacheUtils;
import com.baidu.iov.dueros.waimai.utils.Constant;
import com.baidu.iov.dueros.waimai.utils.Lg;
import com.baidu.iov.dueros.waimai.utils.VoiceManager;
import com.baidu.location.BDLocation;
import com.baidu.xiaoduos.syncclient.Entry;
import com.baidu.xiaoduos.syncclient.EventType;

public class HomeActivity extends BaseActivity<HomePresenter, HomePresenter.HomeUi> implements
		HomePresenter.HomeUi, View.OnClickListener {

	private static final String TAG = HomeActivity.class.getSimpleName();
	private RelativeLayout mRlFood;
	private RelativeLayout mRlFlower;
	private RelativeLayout mRlCake;

	private TextView mTvFood;
	private TextView mTvFlower;
	private TextView mTvCake;
	private AppCompatImageView mIvBack;
	private AppCompatImageView mIvRight;
	private TextView mTvTitle;
	private RelativeLayout mRlSearch;
	private AppCompatImageView mIvTitle;

	private StoreListFragment mStoreListFragment;

	public static String address;

	@Override
	HomePresenter createPresenter() {
		return new HomePresenter();
	}

	@Override
	HomePresenter.HomeUi getUi() {
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		Lg.getInstance().e(TAG, "onCreate");
		if (getIntent().getBooleanExtra(Constant.IS_NEED_VOICE_FEEDBACK, false)) {
			VoiceManager.getInstance().playTTS(HomeActivity.this, getString(R.string.please_choice_commodity));
		}
		iniView();
		iniData();
	}



	@Override
	protected void onResume() {
		super.onResume();
		if (TextUtils.isEmpty(address)) {
			if (!CacheUtils.getAddress().isEmpty()) {
				address = CacheUtils.getAddress();
			} else {
				address = "地址";
			}
		}
		mTvTitle.setText(address);
	}

	private void iniView() {
		mRlFood = findViewById(R.id.rl_food);
		mRlFlower = findViewById(R.id.rl_flower);
		mRlCake =  findViewById(R.id.rl_cake);
		mTvFood = findViewById(R.id.tv_food);
		mTvFlower = findViewById(R.id.tv_flower);
		mTvCake =  findViewById(R.id.tv_cake);
		mIvBack =  findViewById(R.id.iv_back);
		mIvRight =  findViewById(R.id.iv_right);
		mTvTitle =  findViewById(R.id.tv_title);
		mRlSearch = findViewById(R.id.rl_search);
		mIvTitle = findViewById(R.id.iv_title);
	}

	private void iniData() {
		//fragment
		if (mStoreListFragment==null) {
			Lg.getInstance().e(TAG, "mStoreListFragment:"+mStoreListFragment);
			mStoreListFragment = new StoreListFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(Constant.STORE_FRAGMENT_FROM_PAGE_TYPE, Constant.STORE_FRAGMENT_FROM_HOME);
			mStoreListFragment.setArguments(bundle);
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.add(R.id.fragment_store_list, mStoreListFragment);
			transaction.commit();
		}

		mIvBack.setOnClickListener(this);
		mIvRight.setOnClickListener(this);
		mRlFlower.setOnClickListener(this);
		mRlFood.setOnClickListener(this);
		mRlCake.setOnClickListener(this);
		mTvTitle.setOnClickListener(this);
		mRlSearch.setOnClickListener(this);
		mIvTitle.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_back:
				Entry.getInstance().onEvent(Constant.EVENT_EXIT,EventType.TOUCH_TYPE);
				MyApplicationAddressBean.USER_NAMES.clear();
				MyApplicationAddressBean.USER_PHONES.clear();
				AtyContainer.getInstance().finishAllActivity();
				finish();
				break;

			case R.id.tv_title:
			case R.id.iv_title:
				Entry.getInstance().onEvent(Constant.EVENT_OPEN_ADDRESS_SELECT,EventType.TOUCH_TYPE);
				Intent addressIntent = new Intent(HomeActivity.this, AddressSelectActivity.class);
				startActivity(addressIntent);
				break;

			case R.id.iv_right:
				Entry.getInstance().onEvent(Constant.EVENT_OPEN_ORDER_LIST,EventType.TOUCH_TYPE);		
				Intent orderListIntent = new Intent(this, OrderListActivity.class);
				startActivity(orderListIntent);
				break;

			case R.id.rl_search:
				Entry.getInstance().onEvent(Constant.EVENT_OPEN_SEARCH_FROM_HOME,EventType.TOUCH_TYPE);
				Intent searchIntent = new Intent(HomeActivity.this, SearchActivity.class);
				searchIntent.putExtra(Constant.STORE_FRAGMENT_FROM_PAGE_TYPE, Constant.STORE_FRAGMENT_FROM_HOME);
				startActivity(searchIntent);
				break;

			case R.id.rl_flower:
				Entry.getInstance().onEvent(Constant.EVENT_FLOWER_CLICK,EventType.TOUCH_TYPE);
				Intent flowerIntent = new Intent(this, RecommendShopActivity.class);
				flowerIntent.putExtra("title", mTvFlower.getText().toString());
				startActivity(flowerIntent);
				break;

			case R.id.rl_cake:
				Entry.getInstance().onEvent(Constant.EVENT_CAKE_CLICK,EventType.TOUCH_TYPE);
				Intent cakeIntent = new Intent(this, RecommendShopActivity.class);
				cakeIntent.putExtra("title", mTvCake.getText().toString());
				startActivity(cakeIntent);
				break;

			case R.id.rl_food:
				Entry.getInstance().onEvent(Constant.EVENT_FOOD_CLICK,EventType.TOUCH_TYPE);
				Intent foodIntent = new Intent(this, FoodActivity.class);
				foodIntent.putExtra("title", mTvFood.getText().toString());
				foodIntent.putExtra("latitude", mStoreListFragment.getLatitude());
				foodIntent.putExtra("longitude", mStoreListFragment.getLongitude());
				startActivity(foodIntent);
				break;

			default:
				break;
		}

	}


	
}
