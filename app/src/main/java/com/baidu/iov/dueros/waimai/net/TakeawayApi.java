package com.baidu.iov.dueros.waimai.net;

import com.baidu.iov.dueros.waimai.net.entity.base.ResponseBase;
import com.baidu.iov.dueros.waimai.net.entity.response.CinemaBean;
import com.baidu.iov.dueros.waimai.net.entity.response.CinemaInfoResponse;
import com.baidu.iov.dueros.waimai.net.entity.response.City;
import com.baidu.iov.dueros.waimai.net.entity.response.CityListResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author pengqm
 * @name FilmApplication
 * @class name：com.baidu.iov.dueros.film.net
 * @time 2018/10/10 9:02
 * @change
 * @class describe
 */

public interface TakeawayApi {

    @FormUrlEncoded
    @POST("/iovservice/movie/citylist")
    Call<ResponseBase<CityListResponse>> getCityList(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("/iovservice/movie/citylocation")
    Call<ResponseBase<City>> getCityByLocation(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("/iovservice/movie/cinemalist")
    Call<ResponseBase<CinemaBean>> getCinemaList(@FieldMap Map<String, String> map);


    @FormUrlEncoded
    @POST("/iovservice/movie/cinemainfo")
    Call<ResponseBase<CinemaInfoResponse>> getCinemaInfo(@FieldMap Map<String, String> map);

}
