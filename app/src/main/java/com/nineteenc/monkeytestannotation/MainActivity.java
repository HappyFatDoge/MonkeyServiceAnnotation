package com.nineteenc.monkeytestannotation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;

import com.nineteenc.annotation.annotation.Bind2MonkeyService;
import com.nineteenc.annotation.MonkeyServiceUtil;
import com.nineteenc.annotation.annotation.MonkeyService;
import com.nineteenc.annotation.annotation.SendMsg2MService;
import com.nineteenc.annotation.annotation.StartMonkeyService;
import com.nineteenc.annotation.annotation.StopMonkeyService;
import com.nineteenc.annotation.annotation.Unbind2MonkeyService;
import com.nineteenc.annotation.util.MessageWhat;
import com.nineteenc.annotation.util.ServiceNameEnum;

@MonkeyService(serviceName = ServiceNameEnum.PAPER_POINT_READ_SERVICE)
public class MainActivity extends AppCompatActivity {

    private boolean mBond = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MonkeyServiceUtil.getInstance().init(this,
                new Messenger(new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        super.handleMessage(msg);
                    }
                }));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                test();
                break;
            case R.id.button2:
                test2();
                break;
        }
    }

    @Bind2MonkeyService(serviceName = ServiceNameEnum.PAPER_POINT_READ_SERVICE)
    public void test() {
        Log.d("MainActivity", "method: test");
        MonkeyServiceUtil.getInstance().addStatement2Bind("mBond = false");
    }

    @Unbind2MonkeyService(serviceName = ServiceNameEnum.PAPER_POINT_READ_SERVICE)
    public void test2() {
        Log.d("MainActivity", "method: test2");
        MonkeyServiceUtil.getInstance().addStatement2Unbind("mBond = true");
    }

    @StartMonkeyService(serviceName = ServiceNameEnum.PAPER_POINT_READ_SERVICE)
    public void test3() {
        Log.d("MainActivity", "method: test3");
        MonkeyServiceUtil.getInstance().addStatement2Bind("mBond = false");
    }

    @StopMonkeyService(serviceName = ServiceNameEnum.PAPER_POINT_READ_SERVICE)
    public void test4() {
        Log.d("MainActivity", "method: test4");
        MonkeyServiceUtil.getInstance().addStatement2Bind("mBond = false");
    }

    @SendMsg2MService(serviceName = ServiceNameEnum.PAPER_POINT_READ_SERVICE,
            what = MessageWhat.PPR_TYPE_MESSAGE)
    public void test5() {
        Log.d("MainActivity", "method: test5");
        MonkeyServiceUtil.getInstance().addStatement2Bind("mBond = false");
    }
}
