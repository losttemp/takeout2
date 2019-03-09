package com.baidu.iov.dueros.waimai.presenter;

import android.content.Context;
import android.widget.Toast;

import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.interfacedef.RequestCallback;
import com.baidu.iov.dueros.waimai.interfacedef.Ui;
import com.baidu.iov.dueros.waimai.model.ISubmitInfoModel;
import com.baidu.iov.dueros.waimai.model.SubmitInfoImpl;
import com.baidu.iov.dueros.waimai.net.entity.request.ArriveTimeReqBean;
import com.baidu.iov.dueros.waimai.net.entity.request.OrderPreviewJsonBean;
import com.baidu.iov.dueros.waimai.net.entity.request.OrderPreviewReqBean;
import com.baidu.iov.dueros.waimai.net.entity.request.OrderSubmitJsonBean;
import com.baidu.iov.dueros.waimai.net.entity.request.OrderSubmitReq;
import com.baidu.iov.dueros.waimai.net.entity.response.AddressListBean;
import com.baidu.iov.dueros.waimai.net.entity.response.ArriveTimeBean;
import com.baidu.iov.dueros.waimai.net.entity.response.OrderPreviewBean;
import com.baidu.iov.dueros.waimai.net.entity.response.OrderSubmitBean;
import com.baidu.iov.dueros.waimai.net.entity.response.PoifoodListBean;
import com.baidu.iov.dueros.waimai.utils.Constant;
import com.baidu.iov.dueros.waimai.utils.Encryption;
import com.baidu.iov.dueros.waimai.utils.Lg;
import com.baidu.iov.dueros.waimai.utils.StandardCmdClient;
import com.baidu.iov.dueros.waimai.utils.ToastUtils;
import com.baidu.iov.faceos.client.GsonUtil;

import java.util.ArrayList;
import java.util.List;

public class SubmitInfoPresenter extends Presenter<SubmitInfoPresenter.SubmitInfoUi> {

    private ISubmitInfoModel mSubmitInfo;

    public SubmitInfoPresenter() {
        mSubmitInfo = new SubmitInfoImpl();
    }

    private static final String TAG = SubmitInfoPresenter.class.getSimpleName();
    @Override
    public void onUiReady(SubmitInfoUi ui) {
        super.onUiReady(ui);
        mSubmitInfo.onReady();
    }

    @Override
    public void onUiUnready(SubmitInfoUi ui) {
        super.onUiUnready(ui);
        mSubmitInfo.onDestroy();
    }

    @Override
    public void onCommandCallback(String cmd, String extra) {
        if (getUi() == null) {
            return;
        }

        switch (cmd) {
            case StandardCmdClient.CMD_YES:
                getUi().toPay();
                break;
            case StandardCmdClient.CMD_NO:
                getUi().close();
                break;
            default:
                break;
        }
    }

    @Override
    public void registerCmd(Context context) {
        if (null != mStandardCmdClient) {
            ArrayList<String> cmdList = new ArrayList<String>();
            cmdList.add(StandardCmdClient.CMD_YES);
            cmdList.add(StandardCmdClient.CMD_NO);
            mStandardCmdClient.registerCmd(context, cmdList, mVoiceCallback);
        }
    }

    @Override
    public void unregisterCmd(Context context) {
        if (null != mStandardCmdClient) {
            mStandardCmdClient.unregisterCmd(context, mVoiceCallback);
        }
    }

    public void requestArriveTimeData(Long wm_poi_id) {

        ArriveTimeReqBean arriveTimeReqBean = new ArriveTimeReqBean();
        arriveTimeReqBean.setLatitude(Constant.LATITUDE);
        arriveTimeReqBean.setLongitude(Constant.LONGITUDE);
        arriveTimeReqBean.setWm_poi_id(wm_poi_id);
        mSubmitInfo.requestArriveTimeList(arriveTimeReqBean, new RequestCallback<ArriveTimeBean>() {

            @Override
            public void onSuccess(ArriveTimeBean data) {
                if (null != getUi()) {
                    getUi().onArriveTimeSuccess(data);
                }
            }

            @Override
            public void onFailure(String msg) {

                if (null != getUi()) {
                    getUi().onFailure(msg);
                }
            }

            @Override
            public void getLogid(String id) {
                Lg.getInstance().d(TAG, "requestFilterList getLogid: "+id);
            }
        });

    }


    public void requestOrderSubmitData(AddressListBean.IovBean.DataBean addressData,
                                       PoifoodListBean.MeituanBean.DataBean.PoiInfoBean poiInfoBean,
                                       List<OrderPreviewBean.MeituanBean.DataBean.WmOrderingPreviewDetailVoListBean> wmOrderingPreviewDetailVoListBean,
                                       int unixtime,Context context) {


        OrderSubmitReq orderSubmitReq = new OrderSubmitReq();
        String payload = OnCreateOrderPayLoad(addressData, poiInfoBean, wmOrderingPreviewDetailVoListBean, unixtime,context);
        orderSubmitReq.setPayload(Encryption.encrypt(payload));
        orderSubmitReq.setWm_pic_url(poiInfoBean.getPic_url());
        mSubmitInfo.requestOrderSubmitData(orderSubmitReq, new RequestCallback<OrderSubmitBean>() {

            @Override
            public void onSuccess(OrderSubmitBean data) {
                if (null != getUi()) {
                    getUi().onOrderSubmitSuccess(data);
                }
            }

            @Override
            public void onFailure(String msg) {

                if (null != getUi()) {
                    getUi().onFailure(msg);
                }
            }

            @Override
            public void getLogid(String id) {
                Lg.getInstance().d(TAG, "requestOrderSubmitData getLogid: "+id);
            }
        });

    }

    private String OnCreateOrderPayLoad(AddressListBean.IovBean.DataBean addressData,
                                        PoifoodListBean.MeituanBean.DataBean.PoiInfoBean poiInfoBean,
                                        List<OrderPreviewBean.MeituanBean.DataBean.WmOrderingPreviewDetailVoListBean> wmOrderingPreviewDetailVoListBean,
                                        int unixtime,Context context) {
        OrderSubmitJsonBean orderSubmitJsonBean = new OrderSubmitJsonBean();
        try {

            orderSubmitJsonBean.setPay_source(3);
            orderSubmitJsonBean.setReturn_url("www.meituan.com");
            orderSubmitJsonBean.setAddress_id(addressData.getMt_address_id());
            OrderSubmitJsonBean.WmOrderingListBean wmOrderingListBean = new OrderSubmitJsonBean.WmOrderingListBean();
            wmOrderingListBean.setWm_poi_id(poiInfoBean.getWm_poi_id());
            wmOrderingListBean.setDelivery_time(unixtime);
            wmOrderingListBean.setPay_type(2);

            List<OrderSubmitJsonBean.WmOrderingListBean.FoodListBean> foodListBeans = new ArrayList<>();
            for (OrderPreviewBean.MeituanBean.DataBean.WmOrderingPreviewDetailVoListBean previewDetailVoListBean : wmOrderingPreviewDetailVoListBean) {

                OrderSubmitJsonBean.WmOrderingListBean.FoodListBean foodListBean = new OrderSubmitJsonBean.WmOrderingListBean.FoodListBean();

                List<Long> food_spu_attr_ids = new ArrayList<>();
                for (OrderPreviewBean.MeituanBean.DataBean.WmOrderingPreviewDetailVoListBean.WmOrderingPreviewFoodSpuAttrListBean wmOrderingPreviewFoodSpuAttrListBean : previewDetailVoListBean.getWm_ordering_preview_food_spu_attr_list()) {
                    if (wmOrderingPreviewFoodSpuAttrListBean!=null){
                        long id = wmOrderingPreviewFoodSpuAttrListBean.getId();
                        food_spu_attr_ids.add(id);
                    }else {
                        ToastUtils.show(context,context.getString(R.string.error_nullpoint),Toast.LENGTH_LONG);
                    }
                }
                foodListBean.setFood_spu_attr_ids(food_spu_attr_ids);
                foodListBean.setCount(previewDetailVoListBean.getCount());
                foodListBean.setWm_food_sku_id(previewDetailVoListBean.getWm_food_sku_id());
                foodListBeans.add(foodListBean);
            }

            wmOrderingListBean.setFood_list(foodListBeans);
            orderSubmitJsonBean.setWm_ordering_list(wmOrderingListBean);

            OrderSubmitJsonBean.WmOrderingUserBean wmOrderingUserBean = new OrderSubmitJsonBean.WmOrderingUserBean();
            if (addressData.getUser_phone()!=null){
                wmOrderingUserBean.setUser_phone(Encryption.desEncrypt(addressData.getUser_phone()));
            }else {
                ToastUtils.show(context,context.getString(R.string.error_nullpoint),Toast.LENGTH_LONG);
            }
            if (addressData.getUser_name()!=null){
                wmOrderingUserBean.setUser_name(Encryption.desEncrypt(addressData.getUser_name()));
            }else {
                ToastUtils.show(context,"收货人姓名不能为空！",Toast.LENGTH_LONG);
            }
            if (addressData.getAddress()!=null){
                wmOrderingUserBean.setUser_address(Encryption.desEncrypt(addressData.getAddress()));
            }else {
                ToastUtils.show(context,"收货人地址不能为空！",Toast.LENGTH_LONG);
            }
            wmOrderingUserBean.setAddr_longitude(addressData.getLongitude());
            wmOrderingUserBean.setAddr_latitude(addressData.getLatitude());
            orderSubmitJsonBean.setWm_ordering_user(wmOrderingUserBean);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return GsonUtil.toJson(orderSubmitJsonBean);

    }


    public void requestOrderPreview(List<PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean> spusBeanList,
                                    PoifoodListBean.MeituanBean.DataBean.PoiInfoBean poiInfoBean, int delivery_time,
                                    AddressListBean.IovBean.DataBean addressData,Context context) {
        String payload = onCreatePayLoadJson(spusBeanList, poiInfoBean, delivery_time, addressData,context);
        OrderPreviewReqBean orderPreviewReqBean = new OrderPreviewReqBean();
        orderPreviewReqBean.setPayload(Encryption.encrypt(payload));
        mSubmitInfo.requestOrderPreview(orderPreviewReqBean, new RequestCallback<OrderPreviewBean>() {
            @Override
            public void onSuccess(OrderPreviewBean data) {
                if (getUi() != null) {
                    getUi().onOrderPreviewSuccess(data);
                }
            }

            @Override
            public void onFailure(String msg) {

                if (getUi() != null) {
                    getUi().onFailure(msg);
                }
            }

            @Override
            public void getLogid(String id) {
                Lg.getInstance().d(TAG, "requestOrderPreview getLogid: "+id);
            }
        });
    }

    private String onCreatePayLoadJson(List<PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean> spusBeanList,
                                       PoifoodListBean.MeituanBean.DataBean.PoiInfoBean poiInfoBean, int delivery_time,
                                       AddressListBean.IovBean.DataBean addressData,Context context) {


        OrderPreviewJsonBean orderPreviewJsonBean = new OrderPreviewJsonBean();
        OrderPreviewJsonBean.WmOrderingListBean wmOrderingListBean = new OrderPreviewJsonBean.WmOrderingListBean();
        wmOrderingListBean.setWm_poi_id(poiInfoBean.getWm_poi_id());
        wmOrderingListBean.setDelivery_time(delivery_time);
        wmOrderingListBean.setPay_type(2);

        List<OrderPreviewJsonBean.WmOrderingListBean.FoodListBean> foodListBeans = new ArrayList<>();
        for (PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean spusBean : spusBeanList) {
            OrderPreviewJsonBean.WmOrderingListBean.FoodListBean foodListBean = new OrderPreviewJsonBean.WmOrderingListBean.FoodListBean();

            List<Long> food_spu_attr_ids = new ArrayList<>();
            for (PoifoodListBean.MeituanBean.DataBean.FoodSpuTagsBean.SpusBean.AttrsBean attrsBean : spusBean.getAttrs()) {
                if (attrsBean.getChoiceAttrs()!=null){
                    long id = attrsBean.getChoiceAttrs().get(0).getId();
                    food_spu_attr_ids.add(id);
                }else {
                    ToastUtils.show(context,"商品列表不能为空",Toast.LENGTH_LONG);
                }
            }
            foodListBean.setFood_spu_attr_ids(food_spu_attr_ids);
            foodListBean.setCount(spusBean.getNumber());
            foodListBean.setWm_food_sku_id(spusBean.getSkus().get(0).getId());
            foodListBeans.add(foodListBean);
        }
        wmOrderingListBean.setFood_list(foodListBeans);
        orderPreviewJsonBean.setWm_ordering_list(wmOrderingListBean);

        OrderPreviewJsonBean.WmOrderingUserBean wmOrderingUserBean = new OrderPreviewJsonBean.WmOrderingUserBean();

        try {
            wmOrderingUserBean.setUser_phone(Encryption.desEncrypt(addressData.getUser_phone()));
            wmOrderingUserBean.setUser_name(Encryption.desEncrypt(addressData.getUser_name()));
            wmOrderingUserBean.setUser_address(Encryption.desEncrypt(addressData.getAddress()));
            wmOrderingUserBean.setAddr_longitu_longitude(addressData.getLongitude());
            wmOrderingUserBean.setAddr_latitude(addressData.getLatitude());
            wmOrderingUserBean.setUser_latitude(Constant.LATITUDE);


        } catch (Exception e) {
            e.printStackTrace();
        }

        orderPreviewJsonBean.setWm_ordering_user(wmOrderingUserBean);
        if (null != addressData.getMt_address_id()) {
            orderPreviewJsonBean.setAddress_id(addressData.getMt_address_id());
        }
        return GsonUtil.toJson(orderPreviewJsonBean);
    }

    public interface SubmitInfoUi extends Ui {

        void onArriveTimeSuccess(ArriveTimeBean data);

        void onOrderPreviewSuccess(OrderPreviewBean data);

        void onOrderSubmitSuccess(OrderSubmitBean data);

        void onFailure(String msg);

        void toPay();

        void close();
    }
}
