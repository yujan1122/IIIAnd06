package tw.org.iii.iiiand06;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView clock;
    private ListView lapList;
    private boolean isRunning; //default:false
    private Button btnLeft, btnRight;
    private Timer timer;
    private int i;
    private UIHandler uiHandler;

    private SimpleAdapter adapter;
    private LinkedList <HashMap<String,String>> data;
    private String[] from = {"lapItem"};
    private int[] to = {R.id.lapItem};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clock = findViewById(R.id.clock);
        lapList = findViewById(R.id.lapList);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);

        //給定預設值
        btnLeft.setText(isRunning?"Lap":"Reset");
        btnRight.setText(isRunning?"Stop":"Start");

        Log.v("brad", "start");

        uiHandler = new UIHandler();
        timer = new Timer();
        initListView();
    }

    private void initListView(){
        data = new LinkedList<>();
        adapter = new SimpleAdapter(
                this, data, R.layout.item, from, to);
        lapList.setAdapter(adapter);
    }

    //良善工程師,避免
    @Override
    public void finish() {
        if(timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
        super.finish();//結束點
    }

    public void clickLeft(View view) { //不同功能最好不要寫一起
        if(isRunning){
            //Lap
            doLap();
        }else{
            //reset
            doReset();
        }

        //週期任務 java api
    }
    private void doLap(){
        HashMap<String,String> itemData = new HashMap<>();
        itemData.put(from[0], clock.getText().toString());
        data.add(0,itemData); //每記錄一個lap, 指定優先放第0位置
        adapter.notifyDataSetChanged();
    }
    private void doReset(){
        i = 0;
        data.clear(); //
        //data = new LinkedList<>(); //用ref去認data,與上列clear不同效果
        adapter.notifyDataSetChanged();

        uiHandler.sendEmptyMessage(0);
    }



    public void clickRight(View view) { //控制狀態,因為會改變內容,不能只是觸發,要控制它,需要id
        isRunning = !isRunning;
        btnLeft.setText(isRunning?"Lap":"Reset");
        btnRight.setText(isRunning?"Stop":"Start");

        Log.v("brad", "start");
        timer = new Timer();//撰寫時通常一個timer就可以;結合硬體可能才需要一個以上timer
        timer.schedule(new MyTask(), 0, 10);//千分之一秒跳一次
        //關閉app, 背景程式 Android可能存在7分鐘; iphone幾秒
    }

    private class MyTask extends TimerTask{
        @Override
        public void run() {
            if(isRunning){
                i++;
                Log.v("brad","i= " + i );
                //clock.setText("" + i);
                uiHandler.sendEmptyMessage(0);//only to trigger, value給什麼都可
            }
        }
    }

    private class UIHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //clock.setText("" + i);
            clock.setText(toClockString());
        }
    }

    private String toClockString(){
        //i百分位
        int hs = i % 100; //小數點後的數值
        int ts = i / 100; //秒數點前, 總秒數 total second

        int hh = ts / (60*60);
        int mm = (ts - hh*60*60)/60;
        int ss = ts % 60;
        return hh + ":" + mm + ":" + ss + "." + hs;
        //return "10:20:30.12";
    }

}
