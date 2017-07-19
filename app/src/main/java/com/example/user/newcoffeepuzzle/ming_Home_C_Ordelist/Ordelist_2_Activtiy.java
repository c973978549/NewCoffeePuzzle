package com.example.user.newcoffeepuzzle.ming_Home_C_Ordelist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.newcoffeepuzzle.R;
import com.example.user.newcoffeepuzzle.ming_Orderdetail.Orderdetail;
import com.example.user.newcoffeepuzzle.ming_Orderdetail.Orderdetail_2;
import com.example.user.newcoffeepuzzle.ming_Orderlist.Ordelist_2_GetAllTask;
import com.example.user.newcoffeepuzzle.ming_Orderlist.Ordelist_Get_GO_Update;
import com.example.user.newcoffeepuzzle.ming_Orderlist.OrderlistVO;
import com.example.user.newcoffeepuzzle.ming_main.Common_ming;
import com.example.user.newcoffeepuzzle.ming_main.Profile_ming;

import java.util.List;

public class Ordelist_2_Activtiy extends AppCompatActivity {
    private final static String TAG = "Ordelist_2_Activtiy";
    private RecyclerView ry_ordelist_2;
    private String store_id;
    Button Bt_GO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ming_ordelist_2_fragment);
        ry_ordelist_2 = (RecyclerView) findViewById(R.id.ry_ordelist_2);
        ry_ordelist_2.setLayoutManager(new LinearLayoutManager(this));

        Profile_ming profile_ming = new Profile_ming(this);
        store_id = profile_ming.getStoreId();
    }

    @Override
    public void onStart(){
        super.onStart();
        if (Common_ming.networkConnected(this)){
            String url = Common_ming.URL + "ming_Orderlist_Servlet";
            List<OrderlistVO> orderlistVOList = null;

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            try{
                orderlistVOList = new Ordelist_2_GetAllTask().execute(url,store_id).get();
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, e.toString());
            }
            progressDialog.cancel();
            if (orderlistVOList == null || orderlistVOList.isEmpty()){
                Common_ming.showToast(this, "no activity found");
            }else {
                ry_ordelist_2.setAdapter(new Ordelist_2_Activtiy.Orders_2_RecyclerViewAdapter(this,orderlistVOList));
            }
        }else {
            Common_ming.showToast(this, "no network connection available");
        }

    }

    public class Orders_2_RecyclerViewAdapter extends RecyclerView.Adapter<Ordelist_2_Activtiy.Orders_2_RecyclerViewAdapter.ViewHolder> {
        private LayoutInflater layoutInflater;
        private List<OrderlistVO> orderlistVOList;
        private boolean[] actExpanded;

        public Orders_2_RecyclerViewAdapter(Context context, List<OrderlistVO> orderlistVOList) {
            layoutInflater = LayoutInflater.from(context);
            this.orderlistVOList = orderlistVOList;
            actExpanded = new boolean[orderlistVOList.size()];
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView ord_id_2,ord_total_2,ord_time_2,ord_shipping_2;
            public ViewHolder(View itemView) {
                super(itemView);
                ord_id_2 = (TextView) itemView.findViewById(R.id.ord_id_2);
                ord_total_2 = (TextView) itemView.findViewById(R.id.ord_total_2);
                ord_time_2 = (TextView) itemView.findViewById(R.id.ord_time_2);
                ord_shipping_2 = (TextView) itemView.findViewById(R.id.ord_shipping_2);
                Bt_GO = (Button) itemView.findViewById(R.id.Bt_GO);

            }
        }

        @Override
        public Ordelist_2_Activtiy.Orders_2_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemview = layoutInflater.inflate(R.layout.ming_ordelist_2_item,parent,false);
            return new Ordelist_2_Activtiy.Orders_2_RecyclerViewAdapter.ViewHolder(itemview);
        }

        @Override
        public void onBindViewHolder(Ordelist_2_Activtiy.Orders_2_RecyclerViewAdapter.ViewHolder holder, int position) {
            OrderlistVO orderlistVO = orderlistVOList.get(position);

            final String ord_id_2 = orderlistVO.getOrd_id();
            holder.ord_id_2.setText(ord_id_2);
            Integer ord_total_2 = orderlistVO.getOrd_total();
            holder.ord_total_2.setText(ord_total_2.toString());
            String ord_time_2 = orderlistVO.getOrd_time();
            holder.ord_time_2.setText(ord_time_2);
            Integer ord_shipping_2 = orderlistVO.getOrd_shipping();
            holder.ord_shipping_2.setText(ord_shipping_2.toString());

            Bt_GO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common_ming.networkConnected(Ordelist_2_Activtiy.this)){
                        String url = Common_ming.URL + "ming_Orderlist_Servlet";
                        List<OrderlistVO> orderlistVOList = null;

                        ProgressDialog progressDialog = new ProgressDialog(Ordelist_2_Activtiy.this);
                        progressDialog.setMessage("Loading...");
                        progressDialog.show();
                        try{
                            orderlistVOList = new Ordelist_Get_GO_Update().execute(url,store_id,ord_id_2).get();
                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e(TAG, e.toString());
                        }
                        progressDialog.cancel();
                        if (orderlistVOList == null || orderlistVOList.isEmpty()){
                            Common_ming.showToast(Ordelist_2_Activtiy.this, "訂單出貨");
                        }else {
                            ry_ordelist_2.setAdapter(new Ordelist_2_Activtiy.Orders_2_RecyclerViewAdapter(Ordelist_2_Activtiy.this,orderlistVOList));
                        }
                    }else {
                        Common_ming.showToast(Ordelist_2_Activtiy.this, "no network connection available");
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Bundle bundle = new Bundle();
                    bundle.putString("ord_id_2",ord_id_2);
                    Intent intent = new Intent(Ordelist_2_Activtiy.this,Orderdetail_2.class);
                    intent.putExtras(bundle);
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