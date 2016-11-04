package com.imoko.belle;


import com.imoko.core.utils.L;
import com.imoko.core.utils.UnicodeUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


/**
 * * Created by pc on 2016/6/12.
 */

public class ServerApi {
    private static AppAPI mAppApi;
    private static String TAG = "ServerApi";

    public static AppAPI getAppAPI() {
        if (mAppApi == null) {
            if (L.getDeBugState()) {
                Interceptor mTokenInterceptor = new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response response = chain.proceed(chain.request());
                        L.d(TAG, "addNetworkInterceptor : Response  code: " + response.code());
                        BufferedSource source = response.body().source();
                        source.request(Long.MAX_VALUE);
                        Buffer clone = source.buffer().clone();
                        L.d(TAG, "addNetworkInterceptor : Response  content: " + UnicodeUtils.decodeUnicode(clone.readUtf8()));
                        return response;
                    }

                };
                // init okhttp 3 logger
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        L.e(TAG, (message.startsWith("{") ? UnicodeUtils.decodeUnicode(message) : message));
                    }

                });
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                //401 Not Authorised
                Authenticator mAuthenticator = new Authenticator() {

                    @Override
                    public Request authenticate(Route route, Response response) throws IOException {
                        Request request = response.request();
                        L.d(TAG, "Authenticator : The Cookie is " + request.header("Cookie"));
                        L.e(TAG, "Authenticator : 访问网络地址: " + request.url().toString());
                        L.d(TAG, "Authenticator : 访问body : " + request.body().toString());
                        return request;
                    }
                };

                Interceptor interceptor = new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        L.d(TAG, "addInterceptor : 访问request : " + chain.request().toString());
                        Response response = chain.proceed(chain.request());

                        L.d(TAG, "addInterceptor : Response  code: " + response.code());
                        BufferedSource source = response.body().source();
                        source.request(Long.MAX_VALUE);
                        Buffer clone = source.buffer().clone();
                        L.d(TAG, "addInterceptor : Response  content: " + UnicodeUtils.decodeUnicode(clone.readUtf8()));
                        return response;
                    }
                };

                //OkHttpClient
                OkHttpClient httpClient = new OkHttpClient.Builder()
                        .addInterceptor(logging)
                        .addInterceptor(interceptor)
                        .retryOnConnectionFailure(true)
//                        .authenticator(mAuthenticator)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .addNetworkInterceptor(mTokenInterceptor)
                        .build();
                //Retrofit.
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(AppAPI.baseUrl)
//                        .addConverterFactory(GsonConverterFactory.create())
//                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(httpClient)
                        .build();

                mAppApi = retrofit.create(AppAPI.class);
            } else {
                //OkHttpClient
                OkHttpClient httpClient = new OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .build();
                //Retrofit.
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(AppAPI.baseUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .client(httpClient)
                        .build();

                mAppApi = retrofit.create(AppAPI.class);
            }
        }
        return mAppApi;
    }


    public interface AppAPI {
        String baseUrl = "http://bxu2341890125.my3w.com/top/public/index.php/index/";
//        String baseUrl = "http://2.yytbzs.cn:88/index.php/index/";
//        String baseUrl = "http://mimei.cntttt.com:88/public/index.php/index/";

        /**
         * 精选数据
         *
         * @param type
         * @param page
         * @return
         */
        @GET("Userrec/get_list_jingxuan")
        Observable<BaseBean> getGoodSelect(@Query("type") String type,
                                           @Query("page") int page,
                                           @Query("authcode") String authcode,
                                           @Query("channel") String channel);

        @GET("index/index")
        Observable<MainActivity.bean> getData();

        @FormUrlEncoded
        @POST("index/love")
        Call<ResponseBody> getLove(@Field("name") String name);
    }
}

