package com.baidu.iov.dueros.waimai.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.iov.dueros.waimai.net.entity.response.OrderListExtraPayloadBean;
import com.baidu.iov.dueros.waimai.net.entity.response.OrderListResponse;
import com.baidu.iov.dueros.waimai.net.entity.response.OrderListExtraBean;
import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.utils.DeviceUtils;
import com.baidu.iov.dueros.waimai.utils.Encryption;
import com.baidu.iov.dueros.waimai.utils.Lg;
import com.baidu.iov.faceos.client.GsonUtil;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

public class OrderListAdaper extends RecyclerView.Adapter<OrderListAdaper.ViewHolder> {

    private List<OrderListResponse.IovBean.DataBean> mOrderList;
    private Context mContext;
    private final String STATUS_WAITING_PAY = "1";
    private final String STATUS_PAID_SUCCESS = "3";
    private final String STATUS_PAID_FAIL = "4";
    private final String STATUS_PAY_EXPIRED = "5";
    private final String STATUS_PAY_REFUNDING = "6";
    private final String STATUS_PAY_REFUNDED = "7";
    private final String STATUS_PAY_CANCEL = "8";

    private OrderListExtraBean.OrderInfos.Food_list mOrderInfosfood_list;

    public OrderListAdaper(List<OrderListResponse.IovBean.DataBean> orderList,
                           Context context) {
        mOrderList = orderList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .layout_order_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        OrderListResponse.IovBean.DataBean order = mOrderList.get(position);
        viewHolder.bindData(position,order);
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private AppCompatTextView tvIndex;
        private ImageView ivStore;
        private ImageView ivClick;
        private LinearLayout llOrderInfo;
        private AppCompatTextView tvStoreName;
        private AppCompatTextView tvFood;
        private AppCompatTextView tvPrice;
        private AppCompatTextView tvFoodNum;
        private AppCompatTextView tvTotalCount;
        private AppCompatTextView tvOrderTime;
        private AppCompatTextView tvOrderStatus;
        private AppCompatTextView tvOneMore;
        private AppCompatTextView tvCancelOrder;
        private AppCompatTextView tvPayOrder;
        private OrderListResponse.IovBean.DataBean mOrder;
        private OrderListExtraPayloadBean payloadBean;

        private ViewHolder(View view) {
            super(view);

            tvIndex = (AppCompatTextView) view.findViewById(R.id.index);
            ivStore = (ImageView) view.findViewById(R.id.iv_store);
            llOrderInfo = (LinearLayout) view.findViewById(R.id.ll_order_info);
            tvStoreName = (AppCompatTextView) view.findViewById(R.id.tv_store_name);
            ivClick = (ImageView) view.findViewById(R.id.iv_click);
            tvFood = (AppCompatTextView) view.findViewById(R.id.tv_food);
            tvPrice = (AppCompatTextView) view.findViewById(R.id.tv_price);
            tvFoodNum = (AppCompatTextView) view.findViewById(R.id.tv_food_num);
            tvTotalCount = (AppCompatTextView) view.findViewById(R.id.tv_total_count);
            tvOrderTime = (AppCompatTextView) view.findViewById(R.id.tv_order_time);
            tvCancelOrder = (AppCompatTextView) view.findViewById(R.id.cancel_order);
            tvOrderStatus = (AppCompatTextView) view.findViewById(R.id.tv_order_status);
            tvOneMore = (AppCompatTextView) view.findViewById(R.id.one_more_order);
            tvPayOrder = (AppCompatTextView) view.findViewById(R.id.pay_order);
            view.setOnClickListener(this);
            tvStoreName.setOnClickListener(this);
            tvOneMore.setOnClickListener(this);
            ivClick.setOnClickListener(this);
            tvCancelOrder.setOnClickListener(this);
            tvPayOrder.setOnClickListener(this);
        }

        public void bindData(int position,OrderListResponse.IovBean.DataBean order) {
            this.mOrder = order;
            tvOneMore.setText(mContext.getResources().getString(R.string.one_more_order));
            tvCancelOrder.setText(mContext.getResources().getString(R.string.order_cancel));
            tvPayOrder.setText(mContext.getResources().getString(R.string.pay_order));
            String pay_status = order.getOrder_status();
            switch (pay_status) {
                case STATUS_WAITING_PAY:
                    pay_status = mContext.getResources().getString(R.string.waiting_to_pay);
                    tvOneMore.setVisibility(View.GONE);
                    tvPayOrder.setVisibility(View.VISIBLE);
                    break;
                case STATUS_PAID_SUCCESS:
                    pay_status = mContext.getResources().getString(R.string.have_paid);
                    break;
                case STATUS_PAID_FAIL:
                    pay_status = mContext.getResources().getString(R.string.pay_fail);
                    tvCancelOrder.setVisibility(View.GONE);
                    break;
                case STATUS_PAY_EXPIRED:
                    pay_status = mContext.getResources().getString(R.string.pay_invalid);
                    tvCancelOrder.setVisibility(View.GONE);
                    break;
                case STATUS_PAY_REFUNDING:
                    pay_status = mContext.getResources().getString(R.string.pay_refunding);
                    tvCancelOrder.setVisibility(View.GONE);
                    break;
                case STATUS_PAY_REFUNDED:
                    pay_status = mContext.getResources().getString(R.string.pay_refunded);
                    tvCancelOrder.setVisibility(View.GONE);
                    break;
                case STATUS_PAY_CANCEL:
                    pay_status = mContext.getResources().getString(R.string.pay_cancel);
                    tvCancelOrder.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            tvIndex.setText(((position + 1) + ""));
            tvStoreName.setText(order.getOrder_name());
            tvOrderStatus.setText(pay_status);
            tvOrderTime.setText(order.getOrder_time().substring(0,order.getOrder_time().lastIndexOf(":")));
            String extra = order.getExtra();

            OrderListExtraBean extraBean = GsonUtil.fromJson(extra, OrderListExtraBean.class);
            String payload = null;
            try {
                payload = Encryption.desEncrypt(extraBean.getPayload());
            } catch (Exception e) {
                e.printStackTrace();
            }

            payloadBean = GsonUtil.fromJson(payload, OrderListExtraPayloadBean.class);
            String user_phone = payloadBean.getUser_phone();
            int total_count = 0;
            double total_price = 0;
            for (int i = 0; i < extraBean.getOrderInfos().getFood_list().size(); i++) {
                total_count = total_count + extraBean.getOrderInfos().getFood_list().get(i).getCount();
                total_price = total_price + extraBean.getOrderInfos().getFood_list().get(i).getPrice() * extraBean.getOrderInfos().getFood_list().get(i).getCount();

            }
            mOrderInfosfood_list = extraBean.getOrderInfos().getFood_list().get(0);
            String food_name = mOrderInfosfood_list.getName();
            int food_num = mOrderInfosfood_list.getCount();
            String wm_pic_url = extraBean.getOrderInfos().getWm_pic_url();

            tvFood.setText(food_name);
            tvFoodNum.setText("x" + String.valueOf(food_num));
            tvPrice.setText(String.valueOf(total_price));
            tvTotalCount.setText(String.format(mContext.getResources().getString(R.string
                    .goods_total_count), total_count));
            Glide.with(mContext).load(wm_pic_url).into(ivStore);

            itemView.setTag(position);
            tvStoreName.setTag(position);
            ivClick.setTag(position);
            tvOneMore.setTag(position);
            tvCancelOrder.setTag(position);
            tvOneMore.setTag(position);
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            if (mOnItemClickListener != null) {
                switch (v.getId()) {
                    case R.id.tv_store_name:
                    case R.id.iv_click:
                        mOnItemClickListener.onItemClick(v, position, payloadBean);
                        break;
                    case R.id.one_more_order:
                        mOnItemClickListener.onItemClick(v, position, payloadBean);
                        break;
                    case R.id.cancel_order:
                        mOnItemClickListener.onItemClick(v, position, payloadBean);
                        break;
                    case R.id.pay_order:
                        mOnItemClickListener.onItemClick(v, position, payloadBean);
                        break;
                    default:
                        mOnItemClickListener.onItemClick(v, position, payloadBean);
                        break;
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, OrderListExtraPayloadBean payloadBean);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }
}