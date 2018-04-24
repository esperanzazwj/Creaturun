package cn.zju.creaturun.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.zju.creaturun.R;
import cn.zju.creaturun.ui.adapter.RoutersAdapter;

/**
 * Created by 万方方 on 2016/12/6.
 */

public class RoutersActivity extends BaseDrawerActivity {

    private RecyclerView rvRouters;
    private List<String> mDatas;
    private RoutersAdapter RoutersAdapter;
    protected void initData() {
        mDatas = new ArrayList<String>();
        for (int i = 1; i < 20; i++) {
            mDatas.add("" +  i);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routers);
        initData();
        rvRouters = (RecyclerView) findViewById(R.id.rvRouters);
        //rvRouters.setLayoutManager(new LinearLayoutManager(this));
        rvRouters.setLayoutManager(new GridLayoutManager(this,2));
        rvRouters.setAdapter(RoutersAdapter = new RoutersAdapter(this,mDatas));
    }

}
