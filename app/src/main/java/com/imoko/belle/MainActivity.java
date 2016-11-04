package com.imoko.belle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.imoko.core.utils.L;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        et = (EditText) findViewById(R.id.et);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                go();
//                getYY();
                getLove();
            }
        });

    }

    public void go() {
        Observable<BaseBean> goodSelect = ServerApi.getAppAPI().getGoodSelect("1", 1, "", "1000");
        goodSelect.observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<BaseBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        L.d(MainActivity.this, e.getMessage());
                    }

                    @Override
                    public void onNext(BaseBean baseBean) {
                        L.d(MainActivity.this, baseBean.getMsg());
                        tv.setText(baseBean.getMsg());
                    }
                });
    }

    public void getYY() {
        Observable<bean> goodSelect = ServerApi.getAppAPI().getData();
        goodSelect.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<bean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        L.d(MainActivity.this, e.getMessage());
                    }

                    @Override
                    public void onNext(bean s) {
                        L.d(MainActivity.this, s.getHaha());
                        tv.setText(s.getHaha());
                        tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                });
    }

    public void getLove() {
//        Observable<String> goodSelect = ServerApi.getAppAPI().getLove(et.getText().toString());
//        goodSelect.observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        L.d(MainActivity.this, e.getMessage());
//
//                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        L.d(MainActivity.this, s);
//                        tv.setText(s);
//                        tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
//                    }
//                });
        retrofit2.Call<ResponseBody> love = ServerApi.getAppAPI().getLove(et.getText().toString());
        love.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    final String s = response.body().source().readUtf8();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(s);
                            tv.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    public class bean {

        /**
         * haha : 111
         * hehe : 222
         */

        private String haha;
        private String hehe;

        public String getHaha() {
            return haha;
        }

        public void setHaha(String haha) {
            this.haha = haha;
        }

        public String getHehe() {
            return hehe;
        }

        public void setHehe(String hehe) {
            this.hehe = hehe;
        }
    }
}
