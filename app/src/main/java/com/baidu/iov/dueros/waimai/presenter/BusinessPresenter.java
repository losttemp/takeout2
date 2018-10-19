package com.baidu.iov.dueros.waimai.presenter;
import android.util.ArrayMap;

import com.baidu.iov.dueros.waimai.interfacedef.RequestCallback;
import com.baidu.iov.dueros.waimai.interfacedef.Ui;
import com.baidu.iov.dueros.waimai.model.BusinessModel;
import com.baidu.iov.dueros.waimai.model.IBusinessModel;
import com.baidu.iov.dueros.waimai.net.entity.response.BusinessBean;
import com.baidu.iov.dueros.waimai.utils.Lg;

import java.util.Map;

public class BusinessPresenter extends Presenter< BusinessPresenter.BusinessUi> {

    private static final String TAG = BusinessPresenter.class.getSimpleName();

    private IBusinessModel mBusinessModel;

    public BusinessPresenter() {
        this.mBusinessModel = new BusinessModel();
    }

    @Override
    public void onUiReady(BusinessPresenter.BusinessUi ui) {
        super.onUiReady(ui);
        mBusinessModel.onReady();
    }

    public void requestData(ArrayMap<String, String> map) {
        mBusinessModel.requestBusinessBean(map,new RequestCallback<Map<String,BusinessBean>>(){
            @Override
            public void onSuccess(Map<String,BusinessBean> data) {
                if ( getUi()!=null) {
                    getUi().onSuccess(data);
                }
                Lg.getInstance().e(TAG,"msg:"+data);
            }

            @Override
            public void onFailure(String msg) {
                if ( getUi()!=null) {
                    getUi().onError(msg);
                }
                Lg.getInstance().e(TAG,"msg:"+msg);
            }
        });
    }

    @Override
    public void onUiUnready(BusinessPresenter.BusinessUi ui) {
        super.onUiUnready(ui);
        mBusinessModel.onDestroy();
    }

    public interface BusinessUi extends Ui {
        void onSuccess(Map<String,BusinessBean> data);
        void onError(String error);
    }
}
