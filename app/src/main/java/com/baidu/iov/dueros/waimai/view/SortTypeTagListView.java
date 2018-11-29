package com.baidu.iov.dueros.waimai.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.net.entity.response.FilterConditionResponse;
import com.baidu.iov.dueros.waimai.utils.Constant;
import java.util.List;

public class SortTypeTagListView extends LinearLayout implements View.OnClickListener {
    private int mTextSize = getResources().getDimensionPixelSize(R.dimen.px30sp);;
    private int mTextColor = getResources().getColor(R.color.white_60);
    private int mTextSelectedColor = getResources().getColor(R.color.filter_selected);
    private int mDividerWidth = getResources().getDimensionPixelSize(R.dimen.px168dp);
    private List<FilterConditionResponse.MeituanBean.DataBean.SortTypeListBean> mTags;

    private OnItemClickListener mItemClickListener;

   

    private FilterConditionResponse.MeituanBean.DataBean.SortTypeListBean mTagValue;

    public SortTypeTagListView(Context context) {
        this(context, null);
    }

    public SortTypeTagListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setOrientation(HORIZONTAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TagListView);
        mTextSize = a.getDimensionPixelSize(R.styleable.TagListView_textSize, mTextSize);
        mTextColor = a.getColor(R.styleable.TagListView_textColor, mTextColor);
        mDividerWidth = a.getDimensionPixelOffset(R.styleable.TagListView_dividerWidth, mDividerWidth);
    }

    public SortTypeTagListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTags(List<FilterConditionResponse.MeituanBean.DataBean.SortTypeListBean> tags) {
        this.mTags = tags;
        removeAllViews();
        for (int i = 0; i < tags.size(); i++) {
            TextView textView = new TextView(getContext());
            textView.setText(tags.get(i).getShort_name());
            textView.getPaint().setTextSize(mTextSize);
            textView.setTextColor(mTextColor);
            int width = LayoutParams.WRAP_CONTENT;
            int height = getResources().getDimensionPixelSize(R.dimen.px48dp);
            LayoutParams params = new LayoutParams(width, height);
            params.setMargins(0, 0, mDividerWidth, 0);
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);
            textView.setOnClickListener(this);
            textView.setTag(tags.get(i));
            addView(textView);
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mTags.size(); i++) {
            if (v.getTag().equals(mTags.get(i))) {
                if (((TextView)v).getCurrentTextColor()==mTextColor){
                    ((TextView) v).setTextColor(mTextSelectedColor);
                    if (mItemClickListener!=null) {
                        mItemClickListener.onClick((int) mTags.get(i).getCode());
                    }
                }else{
                    ((TextView) v).setTextColor(mTextColor);
                    if (mItemClickListener!=null) {
                        mItemClickListener.onClick((Constant.COMPREHENSIVE));
                    }
                }
            } else {
                View childAt = this.getChildAt(i);
                ((TextView)childAt).setTextColor(mTextColor);
            }
        }
    }
    
    public  void setTextViewDefaultColor(){
        for (int i = 0; i < mTags.size(); i++) {
                View childAt = this.getChildAt(i);
                ((TextView)childAt).setTextColor(mTextColor);
            }
    }
    

    public interface OnItemClickListener {
        void onClick(int sortType);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}