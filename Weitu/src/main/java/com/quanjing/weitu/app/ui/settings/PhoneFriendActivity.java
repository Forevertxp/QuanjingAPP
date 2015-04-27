package com.quanjing.weitu.app.ui.settings;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.quanjing.weitu.R;
import com.quanjing.weitu.app.ui.common.MWTBase2Activity;

import java.util.ArrayList;
import java.util.List;

public class PhoneFriendActivity extends MWTBase2Activity {

    private ListView phoneLV;
    private List<ContractInfo> contractList = new ArrayList<ContractInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_friend);
        setTitleText("邀请好友");
        phoneLV = (ListView) findViewById(R.id.phoneListView);
        contractList.addAll(getPhoneContracts());
        //contractList.addAll(getSimContracts());
        PhoneAdapter adapter = new PhoneAdapter(this);
        phoneLV.setAdapter(adapter);
    }

    //取本机通讯录
    public List<ContractInfo> getPhoneContracts() {
        List<ContractInfo> contractInfoList = new ArrayList<ContractInfo>();
        ContentResolver resolver = getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, null, null, null, null);  //传入正确的uri
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                int nameIndex = phoneCursor.getColumnIndex(Phone.DISPLAY_NAME);   //获取联系人name
                String name = phoneCursor.getString(nameIndex);
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER)); //获取联系人number
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                //以下是我自己的数据封装。
                ContractInfo contractInfo = new ContractInfo();
                contractInfo.setName(name);
                contractInfo.setPhoneNumber(phoneNumber);
                contractInfoList.add(contractInfo);
            }
            phoneCursor.close();
        }
        return contractInfoList;
    }

    //取SIM卡通讯录
    public List<ContractInfo> getSimContracts() {
        //读取SIM卡手机号,有两种可能:content://icc/adn与content://sim/adn
        List<ContractInfo> contractInfoList = new ArrayList<ContractInfo>();
        ContentResolver resolver = getContentResolver();
        Uri uri = Uri.parse("content://icc/adn");
        Cursor phoneCursor = resolver.query(uri, null, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                String name = phoneCursor.getString(phoneCursor.getColumnIndex("name"));
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex("number"));
                if (TextUtils.isEmpty(phoneNumber)) {
                    continue;
                }
                //以下是我自己的数据封装。
                ContractInfo contractInfo = new ContractInfo();
                contractInfo.setName(name);
                contractInfo.setPhoneNumber(phoneNumber);
                contractInfoList.add(contractInfo);
            }
            phoneCursor.close();
        }
        return contractInfoList;
    }

    class PhoneAdapter extends BaseAdapter {
        private Context mContext;

        public PhoneAdapter(Context context) {
            mContext = context;
        }

        public int getCount() {
            //设置绘制数量
            return contractList.size();
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_phone_friend, null);
                viewHolder.nameTV = (TextView) convertView.findViewById(R.id.contact_name);
                viewHolder.phoneTV = (TextView) convertView.findViewById(R.id.contact_number);
                viewHolder.invite = (Button) convertView.findViewById(R.id.invite);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final String number = contractList.get(position).getPhoneNumber();
            //绘制联系人名称
            viewHolder.nameTV.setText(contractList.get(position).getName());
            //绘制联系人号码
            viewHolder.phoneTV.setText(number);
            viewHolder.invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!TextUtils.isEmpty(number))
                        sendMessage(number, "全景App里有不少漂亮又专业的图片，挺有意思的，推荐你用一下。下载地址：http://www.quanjing.com");
                }
            });
            return convertView;
        }

        private void sendMessage(String number, String message) {
            Uri uri = Uri.parse("smsto:" + number);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            sendIntent.putExtra("sms_body", message);
            startActivity(sendIntent);
        }

        class ViewHolder {
            private Button invite;
            private TextView nameTV;
            private TextView phoneTV;
        }
    }
}
