package com.loser007.gameplane;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.loser007.gameplane.game.AutoPlayView;
import com.google.billing.v3.IabHelper;
import com.google.billing.v3.IabResult;
import com.google.billing.v3.Inventory;
import com.google.billing.v3.Purchase;


/**
 * Created by dbSven on 2018/8/31.
 */
public class RewardActivity extends Activity implements View.OnClickListener {
    private IabHelper mHelper = null;
    private static final String TAG = "RewardActivity";
    private final int RC_REQUEST = 10001;
    private boolean IsSecondCusume = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        final AutoPlayView v_bg = (AutoPlayView)findViewById(R.id.v_bg);
        v_bg.setSudu(10000);
        v_bg.startPlay();

        SimpleAdapter adapter= new SimpleAdapter(this,Data.Datas,R.layout.item_reward_list,new String[]{"price","id"},new int[]{R.id.price,R.id.price});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                Button button = (Button)view;
                if(button.getText().equals("")){
                    button.setText(textRepresentation);
                } else{
                    button.setTag(textRepresentation);
                    button.setOnClickListener(RewardActivity.this);
                }
                return true;
            }
        });
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
         sku = (String)v.getTag();
        try{
            startSetup();
        }catch (Exception e){

        }
    }
    private String sku = "";
    private void startSetup() throws Exception {
        //启动google支付
        mHelper = new IabHelper(this, "");
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");
                if (!result.isSuccess()) {
                    return;
                }
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                        @Override
                        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                            try {
                                if (inv.hasPurchase(sku)) {
                                    Purchase pp = inv.getPurchase(sku);
                                    IsSecondCusume = true;
                                    //dealPaySuccess(pp);
                                    mHelper.consumeAsync(pp, mConsumeFinishedListener);
                                } else {
                                    mHelper.launchPurchaseFlow(RewardActivity.this,
                                            sku, RC_REQUEST, mPurchaseFinishedListener, "");

                                }

                            } catch (Exception e) {
                                Toast.makeText(RewardActivity.this, "-118,Net Exception,Please retry!", Toast.LENGTH_LONG);
                            }
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(RewardActivity.this, "-119,Net Exception,Please retry!", Toast.LENGTH_LONG);
                }

            }
        });
    }

    //支付回调
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.e(TAG, "Purchase finished: " + result + ", purchase: "
                    + purchase);

            if (result.isFailure()) {
                Toast.makeText(RewardActivity.this, "-110,Pay Fail,Please retry!", Toast.LENGTH_LONG);
                return;
            }

            Log.d(TAG, "Purchase successful.");
            //支付成功,执行消费
            try {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }catch (Exception e){
                Toast.makeText(RewardActivity.this, "-112,Net Exception,Please retry!", Toast.LENGTH_LONG);
            }

        }
    };
    //消费回调
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            //处理二次消费
            if (result.isSuccess()&&IsSecondCusume) {
                IsSecondCusume = false;
                try {
                    mHelper.launchPurchaseFlow(RewardActivity.this,
                            sku, RC_REQUEST, mPurchaseFinishedListener, "");
                    return;
                } catch (Exception e) {
                    Toast.makeText(RewardActivity.this, "-117,Net Exception,Please retry!", Toast.LENGTH_LONG);
                }
            }

        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
}
