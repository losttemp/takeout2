package com.baidu.iov.dueros.waimai.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.utils.Constant;
import com.bumptech.glide.Glide;

public class PaySuccessActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mOrderDetailsTv;
    private Button mCountDownTv;
    private ImageView mStorePhotoImg;
    private TextView mStoreNameTv;
    private TextView mProductInfoTv;
    private TextView mUserInfoTv;
    private TextView mDeliveryAddressTv;
    private ImageView mFinishImg;


    public static final int MSG_UPDATE_TIME = 1;
    public static final int INTERNAL_TIME = 1000;
    private int mCountDownTime = 5;
    private Long mOrderId;
    private long mExpectedTime;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_UPDATE_TIME:
                    if (mCountDownTime > 0) {
                        mCountDownTv.setText(String.format(getString(R.string.complete), --mCountDownTime));
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, INTERNAL_TIME);
                    } else {

                        startOtherActivity();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        initView();
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, INTERNAL_TIME);
    }

    public void initView() {

        Intent intent = getIntent();
        if (intent != null) {
            mOrderId = intent.getLongExtra(Constant.ORDER_ID, 0);
            String picUrl = intent.getStringExtra(Constant.PIC_URL);
            String storeName = intent.getStringExtra(Constant.STORE_NAME);
            String recipientPhone = intent.getStringExtra(Constant.USER_PHONE);
            String recipientAddress = intent.getStringExtra(Constant.USER_ADDRESS);
            String recipient_name = intent.getStringExtra(Constant.USER_NAME);
            String foodNameOne = intent.getStringExtra(Constant.PRODUCT_NAME);
            mExpectedTime = intent.getLongExtra(Constant.EXPECTED_TIME, 0);
            int count = intent.getIntExtra(Constant.PRODUCT_COUNT, 0);

            mStorePhotoImg = findViewById(R.id.store_photo_img);
            mStoreNameTv = findViewById(R.id.store_name_tv);
            mProductInfoTv = findViewById(R.id.product_info_tv);
            mUserInfoTv = findViewById(R.id.user_info_tv);
            mDeliveryAddressTv = findViewById(R.id.recipient_address);
            Glide.with(this).load(picUrl).into(mStorePhotoImg);
            mStoreNameTv.setText(storeName);
            mProductInfoTv.setText(String.format(getString(R.string.product_info), foodNameOne, count));
            mUserInfoTv.setText(recipient_name + " " + recipientPhone);
            mDeliveryAddressTv.setText(recipientAddress);


        }

        mOrderDetailsTv = findViewById(R.id.order_details_tv);
        mOrderDetailsTv.setOnClickListener(this);
        mCountDownTv = findViewById(R.id.complete_tv);
        mCountDownTv.setText(String.format(getString(R.string.complete), mCountDownTime));
        mCountDownTv.setOnClickListener(this);
        mFinishImg = findViewById(R.id.finish_img);
        mFinishImg.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void startOtherActivity() {
        Intent data = new Intent(this, HomeActivity.class);
        data.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(data);
        finish();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.order_details_tv:

                Intent intent = new Intent(this, OrderDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Constant.ORDER_ID, mOrderId);
                intent.putExtra(Constant.EXPECTED_TIME, mExpectedTime);
                startActivity(intent);
                break;

            case R.id.complete_tv:

                startOtherActivity();
                break;
            case R.id.finish_img:
                finish();

            default:
                break;
        }
    }
}
