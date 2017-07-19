package com.example.user.newcoffeepuzzle.ming_Orderlist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.user.newcoffeepuzzle.R;
import com.example.user.newcoffeepuzzle.ming_Orderdetail.Orderdetail;
import com.example.user.newcoffeepuzzle.ming_main.Common_ming;
import com.example.user.newcoffeepuzzle.ming_main.Profile_ming;

import java.util.List;

/**
 * Created by Java on 2017/7/14.
 */

public class Ordelist_3_Fragment extends Fragment{
    private final static String TAG = "ming_ordelist_3_fragment";
    private RecyclerView ry_ordelist_3;
    private String store_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ming_ordelist_3_fragment, container, false);
        ry_ordelist_3 = (RecyclerView) view.findViewById(R.id.ry_ordelist_3);
        ry_ordelist_3.setLayoutManager(new LinearLayoutManager(getActivity()));
        Profile_ming profile_ming = new Profile_ming(getContext());
        store_id = profile_ming.getStoreId();
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        if (Common_ming.networkConnected(getActivity())){
            String url = Common_ming.URL + "ming_Orderlist_Servlet";
            List<OrderlistVO> orderlistVOList = null;

            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            try{
                orderlistVOList = new Ordelist_3_GetAllTask().execute(url,store_id).get();
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
            progressDialog.cancel();
            if (orderlistVOList == null || orderlistVOList.isEmpty()){
                Common_ming.showToast(getActivity(), "no activity found");
            }else {
                ry_ordelist_3.setAdapter(new Ordelist_3_Fragment.Orders_3_RecyclerViewAdapter(getActivity(),orderlistVOList));
            }
        }else {
            Common_ming.showToast(getActivity(), "no network connection available");
        }

    }

    public class Orders_3_RecyclerViewAdapter extends RecyclerView.Adapter<Ordelist_3_Fragment.Orders_3_RecyclerViewAdapter.ViewHolder> {
        private LayoutInflater layoutInflater;
        private List<OrderlistVO> orderlistVOList;
        private boolean[] actExpanded;

        public Orders_3_RecyclerViewAdapter(Context context, List<OrderlistVO> orderlistVOList) {
            layoutInflater = LayoutInflater.from(context);
            this.orderlistVOList = orderlistVOList;
            actExpanded = new boolean[orderlistVOList.size()];

        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView ord_id_3,ord_total_3,ord_time_3,ord_shipping_3;
            public ViewHolder(View itemView) {
                super(itemView);
                ord_id_3 = (TextView) itemView.findViewById(R.id.ord_id_3);
                ord_total_3 = (TextView) itemView.findViewById(R.id.ord_total_3);
                ord_time_3 = (TextView) itemView.findViewById(R.id.ord_time_3);
                ord_shipping_3 = (TextView) itemView.findViewById(R.id.ord_shipping_3);
            }
        }

        @Override
        public Orders_3_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = layoutInflater.inflate(R.layout.ming_ordelist_3_item,parent,false);
            return new ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(Orders_3_RecyclerViewAdapter.ViewHolder holder, int position) {
            OrderlistVO orderlistVO = orderlistVOList.get(position);

            String ord_id_3 = orderlistVO.getOrd_id();
            holder.ord_id_3.setText(ord_id_3);
            Integer ord_total_3 = orderlistVO.getOrd_total();
            holder.ord_total_3.setText(ord_total_3.toString());
            String ord_time_3 = orderlistVO.getOrd_time();
            holder.ord_time_3.setText(ord_time_3);
            Integer ord_shipping_3 = orderlistVO.getOrd_shipping();
            holder.ord_shipping_3.setText(ord_shipping_3.toString());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),Orderdetail.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return orderlistVOList.size();
        }
        private void expand(int position) {
            // 被點擊的資料列才會彈出內容，其他資料列的內容會自動縮起來
            // for (int i=0; i<newsExpanded.length; i++) {
            // newsExpanded[i] = false;
            // }
            // newsExpanded[position] = true;

            actExpanded[position] = !actExpanded[position];
            notifyDataSetChanged();
        }
    }
}