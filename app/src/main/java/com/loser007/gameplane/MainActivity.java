package com.loser007.gameplane;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.loser007.gameplane.game.AutoPlayView;


public class MainActivity extends Activity implements Button.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AutoPlayView v_bg = (AutoPlayView)findViewById(R.id.v_bg);
        v_bg.setSudu(10000);
        v_bg.startPlay();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.start_game){
            startGame();
        }else if (viewId == R.id.start_abount)
        {
            startAbout();
        }else if(viewId == R.id.start_pay){
            startPay();
        }

    }

    public void startGame(){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void startAbout(){
        Uri uri = Uri.parse("https://www.baidu.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void startPay(){
        Intent intent = new Intent(this, RewardActivity.class);
        startActivity(intent);
    }
}