package com.baidu.iov.dueros.waimai.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.baidu.iov.dueros.waimai.R;
import com.baidu.iov.dueros.waimai.interfacedef.Ui;
import com.baidu.iov.dueros.waimai.presenter.Presenter;
import com.baidu.iov.dueros.waimai.utils.Constant;
import com.baidu.iov.dueros.waimai.utils.LocationManager;
import com.baidu.location.BDLocation;

public abstract class BaseActivity<T extends Presenter<U>, U extends Ui> extends AppCompatActivity implements LocationManager.LocationCallBack {

    private T mPresenter;

    abstract T createPresenter();

    abstract U getUi();

    protected BaseActivity() {
        mPresenter = createPresenter();
    }

    private BDLocation mBDLocation;

    public T getPresenter() {
        return mPresenter;
    }

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 400;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.onUiReady(getUi());
        initLocationCity();
        requestPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().registerCmd(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().unregisterCmd(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onUiDestroy(getUi());
    }

    protected void initLocationCity() {
        LocationManager instance = LocationManager.getInstance(this);
        LocationManager.getInstance(this).setLocationCallBack(this);
        instance.startLocation();
    }

    @Override
    public void locationCallBack(boolean isSuccess, BDLocation bdLocation) {


    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.persion_err)
                            .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                                    startActivity(intent);
                                    finish();

                                }
                            })
                            .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .create()
                            .show();

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ACCESS_COARSE_LOCATION);
                }


            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //permission was granted, yay! Do the contacts-related task you need to do.
                initLocationCity();
            } else {
                //permission denied, boo! Disable the functionality that depends on this permission.
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
