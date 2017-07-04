package com.example.user.newcoffeepuzzle.rjchenl_spndcoffeelist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.newcoffeepuzzle.R;
import com.example.user.newcoffeepuzzle.rjchenl_main.Common_RJ;
import com.example.user.newcoffeepuzzle.rjchenl_main.Profile;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.json.JSONObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2017/7/1.
 */

public class SpndcoffeeListFragment extends Fragment {
    private static final String TAG = "SpndcoffeeListFragment";
    private ListView spndList_view;
    private List<SpndcoffeelistVO> spndcoffeelist_value;
    private String mem_id;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.rj_spndcoffeelist_fragment,container,false);

        spndList_view = (ListView) view.findViewById(R.id.lvSpndcoffeelist);



        //會用到mem_id 先取得
        Profile profile = new Profile(getContext());
        mem_id = profile.getMemId();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //取得物件資料
        getDBdata();
        //將資料與view做連結
        spndList_view.setAdapter(new SpndCoffeeListAdapter(getActivity(),spndcoffeelist_value));

    }

    private void getDBdata() {
        if (Common_RJ.networkConnected(getActivity())) {
            String url = Common_RJ.URL + "SpndcoffeelistServlet";
            spndcoffeelist_value = null;
            try {

                //連結資料庫取得物件資料
                spndcoffeelist_value = new SpndcoffeelistGetMyspndlistTask().execute(url,mem_id).get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (spndcoffeelist_value == null || spndcoffeelist_value.isEmpty()) {
                Common_RJ.showToast(getActivity(), "No spndcoffeelist found");
            }
        }
    }

    private class SpndCoffeeListAdapter extends BaseAdapter{
        Context context;
        List<SpndcoffeelistVO> spndList_data;
        Map<ImageView,String> map= new HashMap<>();
        private AlertDialogFragment alertDialogFragment;

        public SpndCoffeeListAdapter(Context context, List<SpndcoffeelistVO> spndList_data) {
            this.context = context;
            this.spndList_data = spndList_data;
        }


        @Override
        public int getCount() {
            return spndList_data.size();
        }

        @Override
        public Object getItem(int position) {
            return spndList_data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            //一開始什麼都還沒按的話  載入被選到的view的實體,也就是convertView
            if(convertView == null){
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                convertView =layoutInflater.inflate(R.layout.rj_spndcoffeelist_item_fragment,parent,false);
            }




            //取到當下那個VO物件
            final SpndcoffeelistVO spndcoffeelistVO = spndList_data.get(position);


            //將抓到的view 設上其值
            TextView sotre_name = (TextView) convertView.findViewById(R.id.sotre_name);
            TextView store_add = (TextView) convertView.findViewById(R.id.store_add);
            TextView list_left = (TextView) convertView.findViewById(R.id.list_left);

            sotre_name.setText(spndcoffeelistVO.getStore_name());
            store_add.setText(spndcoffeelistVO.getStore_add());
            list_left.setText(String.valueOf(spndcoffeelistVO.getList_left()));


            Log.d(TAG, "getView: spndcoffeelistVO.getStore_name() : "+spndcoffeelistVO.getStore_name());
            Log.d(TAG, "getView: spndcoffeelistVO.getStore_add() : "+spndcoffeelistVO.getStore_add());
            Log.d(TAG, "getView: spndcoffeelistVO.getList_id() : "+spndcoffeelistVO.getList_id());


            //夾帶資訊到qrcode
            final Bundle bundle = new Bundle();
            bundle.putSerializable("spndcoffeelistVO",spndcoffeelistVO);


            //點擊顯示QR_code
            Button showQRcode = (Button) convertView.findViewById(R.id.showQRcode);
            showQRcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //show QRcode
                    AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
                    alertDialogFragment.setArguments(bundle);




                    FragmentManager fragmentManager = getFragmentManager();

                    alertDialogFragment.show(fragmentManager,"alert");


                }
            });



            return convertView;
        }
    }

    public static class AlertDialogFragment extends DialogFragment {




        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            //不顯示標題
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);



            return dialog;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            //載入xml layout
            View view = inflater.inflate(R.layout.rj_alertdialog, null);
            return view;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);


            Bundle bundle = getArguments();
            SpndcoffeelistVO spndcoffeelistVO = (SpndcoffeelistVO) bundle.getSerializable("spndcoffeelistVO");
            String List_id  =spndcoffeelistVO.getList_id();
            String List_left = spndcoffeelistVO.getList_left().toString();

            Gson gson = new Gson();
            String json = gson.toJson(spndcoffeelistVO);


            Log.d(TAG, "onCreateDialog: List_id : "+List_id);
            Log.d(TAG, "onCreateDialog: List_left : "+List_left);

            //註冊離開button事件聆聽
            Button btleave = (Button) view.findViewById(R.id.btleave);
            btleave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
//            放入qr code image
            ImageView qrimage = (ImageView) view.findViewById(R.id.qrimage);
            qrimage.setImageResource(R.drawable.default_image);

            //startpaste
            // QR code 的內容
            String QRCodeContent = json;
            // QR code 寬度
            int QRCodeWidth = 800;
            // QR code 高度
            int QRCodeHeight = 800;
            // QR code 內容編碼
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            MultiFormatWriter writer = new MultiFormatWriter();
            try
            {

                String url = "https://www.google.com.tw/";
                // 容錯率姑且可以將它想像成解析度，分為 4 級：L(7%)，M(15%)，Q(25%)，H(30%)
                // 設定 QR code 容錯率為 H
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

                // 建立 QR code 的資料矩陣
                BitMatrix result = writer.encode(QRCodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);
                // ZXing 還可以生成其他形式條碼，如：BarcodeFormat.CODE_39、BarcodeFormat.CODE_93、BarcodeFormat.CODE_128、BarcodeFormat.EAN_8、BarcodeFormat.EAN_13...

                //建立點陣圖
                Bitmap bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_8888);
                // 將 QR code 資料矩陣繪製到點陣圖上
                for (int y = 0; y<QRCodeHeight; y++)
                {
                    for (int x = 0;x<QRCodeWidth; x++)
                    {
                        bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

                ImageView imgView = (ImageView) view.findViewById(R.id.qrimage);
                // 設定為 QR code 影像
                imgView.setImageBitmap(bitmap);
            }
            catch (WriterException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //endpaste

        }


    }









}