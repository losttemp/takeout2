package com.baidu.iov.dueros.waimai.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.adapter.SearchHistroyAdapter;
import com.baidu.iov.dueros.waimai.adapter.SearchSuggestAdapter;
import com.baidu.iov.dueros.waimai.net.entity.request.StoreReq;
import com.baidu.iov.dueros.waimai.net.entity.response.SearchSuggestResponse;
import com.baidu.iov.dueros.waimai.presenter.SearchPresenter;
import com.baidu.iov.dueros.waimai.utils.Constant;
import com.baidu.iov.dueros.waimai.utils.Lg;
import com.baidu.iov.dueros.waimai.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity<SearchPresenter, SearchPresenter.SearchUi>
		implements
		SearchPresenter.SearchUi, View.OnClickListener {

	private AppCompatEditText mEtSearch;
	private FrameLayout mFragmentStoreList;
	private LinearLayout mLlHistory;
	private AppCompatImageView mIvDelete;
	private AppCompatImageView mIvClean;
	private AppCompatTextView mTvCancel;
	private ListView mLvHistory;
	private ListView mLvSuggest;

	private SearchPresenter mPresenter;
	private StoreReq mStoreReq;
	private List<String> mHistorys;
	private SearchHistroyAdapter mSearchHistroyAdapter;
	private SearchSuggestAdapter mSearchSuggestAdapter;
	private StoreListFragment mStoreListFragment;
	private List<SearchSuggestResponse.MeituanBean.DataBean.SuggestBean> mSuggests;
	private int mCurrentStatus;

	private static final int HEAD_NUM=1;


	@Override
	SearchPresenter createPresenter() {
		return new SearchPresenter();
	}

	@Override
	SearchPresenter.SearchUi getUi() {
		return this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		iniView();
		iniData();

	}
	
	
	public void setmEtTipNoResult(){
		mEtSearch.setText("这个关键词有点复杂");
		mLlHistory.setVisibility(View.GONE);
		mLvSuggest.setVisibility(View.GONE);
	}

	private void iniView() {
		mEtSearch = (AppCompatEditText) findViewById(R.id.et_search);
		mFragmentStoreList = (FrameLayout) findViewById(R.id.fragment_store_list);
		mLlHistory = (LinearLayout) findViewById(R.id.ll_history);
		mIvDelete = (AppCompatImageView) findViewById(R.id.iv_delete);
		mLvHistory = (ListView) findViewById(R.id.lv_history);
		mIvClean = (AppCompatImageView) findViewById(R.id.iv_clean);
		mTvCancel = (AppCompatTextView) findViewById(R.id.tv_cancel);
		mLvSuggest = (ListView) findViewById(R.id.lv_suggest);
		
		mLvHistory.addHeaderView(new View(this));
		mLvHistory.addFooterView(new View(this));
		mLvSuggest.addHeaderView(new View(this));
		mLvSuggest.addFooterView(new View(this));
	}

	private void iniData() {
		mPresenter = getPresenter();
		mStoreReq = new StoreReq();

		//fragment
		mStoreListFragment = new StoreListFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(Constant.STORE_FRAGMENT_FROM_PAGE_TYPE, Constant.STORE_FRAGMENT_FROM_SEARCH);
		mStoreListFragment.setArguments(bundle);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.add(R.id.fragment_store_list, mStoreListFragment);
		transaction.commit();

		//search history
		mHistorys = new ArrayList<>();
		SharedPreferencesUtils.getSearchHistory(mHistorys);
		mSearchHistroyAdapter = new SearchHistroyAdapter(mHistorys, SearchActivity.this);
		mLvHistory.setAdapter(mSearchHistroyAdapter);

		//search suggest
		mSuggests = new ArrayList<>();
		mSearchSuggestAdapter = new SearchSuggestAdapter(mSuggests, SearchActivity.this);
		mLvSuggest.setAdapter(mSearchSuggestAdapter);

		mEtSearch.setOnClickListener(this);
		mIvDelete.setOnClickListener(this);
		mIvClean.setOnClickListener(this);
		mTvCancel.setOnClickListener(this);

		mLvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				searchKeyword(mHistorys.get(position-HEAD_NUM));
			}
		});

		mLvSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SearchSuggestResponse.MeituanBean.DataBean.SuggestBean suggest = mSuggests.get
						(position-HEAD_NUM);
				if (suggest.getType() == 0 && suggest.getPoi_addition_info() != null) {
					Intent intent = new Intent(SearchActivity.this, FoodListActivity.class);
					intent.putExtra(Constant.STORE_ID, suggest.getPoi_addition_info().getWm_poi_id
							());
					startActivity(intent);
				} else {
					String name = suggest.getSuggest_query();
					searchKeyword(name);
				}
			}
		});

		mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					String keyWord = mEtSearch.getText().toString();
					searchKeyword(keyWord);
					return true;
				}
				return false;
			}
		});

		mEtSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (TextUtils.isEmpty(s)) {
					mIvClean.setVisibility(View.GONE);
					changeStatus(Constant.SEARCH_STATUS_HISTORY);
				} else {
					mLvSuggest.setVisibility(View.VISIBLE);
					mLlHistory.setVisibility(View.GONE);
					mPresenter.requestSuggestList(mEtSearch.getText().toString());
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_cancel:
				cancel();
				break;

			case R.id.iv_delete:
				SharedPreferencesUtils.clearSearchHistory();
				SharedPreferencesUtils.getSearchHistory(mHistorys);
				mSearchHistroyAdapter.notifyDataSetChanged();
				break;

			case R.id.iv_clean:
				mEtSearch.setText("");
				break;

			default:
				break;
		}

	}

	@Override
	public void close() {
		cancel();
	}

	@Override
	public void selectListItem(int index) {
		if (mCurrentStatus == Constant.SEARCH_STATUS_HISTORY && mHistorys.size() > index) {
			searchKeyword(mHistorys.get(index));
		}
	}

	@Override
	public void onSuggestSuccess(SearchSuggestResponse data) {
		mIvClean.setVisibility(View.VISIBLE);
		mSuggests.clear();
		mSuggests.addAll(data.getMeituan().getData().getSuggest());
		mSearchSuggestAdapter.notifyDataSetChanged();
	}

	@Override
	public void onSuggestFailure(String msg) {

	}

	private void searchKeyword(String keyword) {
		mEtSearch.setText(keyword);
		mEtSearch.setSelection(keyword.length());
		mStoreReq.setSortType(null);
		if (!TextUtils.isEmpty(keyword)) {
			mStoreReq.setKeyword(keyword);
			SharedPreferencesUtils.saveSearchHistory(keyword,
					mHistorys);
			mSearchHistroyAdapter.notifyDataSetChanged();
		} else {
			mStoreReq.setKeyword(null);
		}
		changeStatus(Constant.SEARCH_STATUS_FRAGMENT);
		mStoreListFragment.loadFirstPage(mStoreReq);
		hideSoftKeyboard();
	}

	private void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context
				.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
	}

	private void changeStatus(int status) {
		if (status == Constant.SEARCH_STATUS_SUGGEST) {
			mLlHistory.setVisibility(View.GONE);
			mLvSuggest.setVisibility(View.VISIBLE);
			mFragmentStoreList.setVisibility(View.GONE);
		} else if (status == Constant.SEARCH_STATUS_FRAGMENT) {
			mLlHistory.setVisibility(View.GONE);
			mLvSuggest.setVisibility(View.GONE);
			mFragmentStoreList.setVisibility(View.VISIBLE);
			if (mSuggests.size() > 0) {
				mSuggests.clear();
				mSearchSuggestAdapter.notifyDataSetChanged();
			}
		} else {
			mLlHistory.setVisibility(View.VISIBLE);
			mLvSuggest.setVisibility(View.GONE);
			mFragmentStoreList.setVisibility(View.GONE);
			if (mSuggests.size() > 0) {
				mSuggests.clear();
				mSearchSuggestAdapter.notifyDataSetChanged();
			}
		}
		mCurrentStatus = status;

	}

	private void cancel() {
		if (mCurrentStatus == Constant.SEARCH_STATUS_FRAGMENT) {
			changeStatus(Constant.SEARCH_STATUS_HISTORY);
		} else {
			finish();
		}
	}

	public int getStatus() {
		return mCurrentStatus;
	}

}
