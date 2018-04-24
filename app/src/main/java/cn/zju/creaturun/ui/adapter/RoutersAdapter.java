package cn.zju.creaturun.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.zju.creaturun.R;

/**
 * Created by 万方方 on 2016/12/15.
 */

public class RoutersAdapter extends RecyclerView.Adapter<RoutersAdapter.MyViewHolder> {
    private Context context;
    private List<String> datas;
    public  RoutersAdapter(Context context,List<String> datas){
        this.context=context;
        this.datas=datas;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_routers, parent,
                false));
        return holder;
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public MyViewHolder(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_num);
        }
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tv.setText(datas.get(position));
    }
    @Override
    public int getItemCount() {
        return datas.size();
    }

}
