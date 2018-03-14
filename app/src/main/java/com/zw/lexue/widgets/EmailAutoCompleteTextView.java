package com.zw.lexue.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zw.lexue.R;

/**
 * 本文档为自定义邮箱自动补全控件Java程序，其功能为当输入长度大于1时，自动补全常用邮箱地址。
 * Created by Keshi Smith on 2017/6/4.
 */

public class EmailAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private static final String TAG = "EmailAutoCompleteTextView";

    private String[] emailSufixs=new String[] {"@163.com","@qq.com","@126.com", "@vip.qq.com",
            "@gmail.com", "@sina.com", "@hotmail.com","@yahoo.cn", "@sohu.com", "@foxmail.com",
            "@139.com", "@yeah.net", "@vip.sina.com"};

    public EmailAutoCompleteTextView(Context context) {
        super(context);
        init(context);
    }

    public EmailAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmailAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 邮箱地址正则判断，正确则返回true，错误则返回false。
     * @return 返回邮箱地址正误。
     */
    public static boolean isLegalAddress(String email)
    {
        return email.matches("^[a-zA-Z0-9]+([\\_|\\-|\\.]?[a-zA-Z0-9])*\\@[a-zA-Z0-9]"
                +"+([\\_|\\-|\\.]?[a-zA-Z0-9])*\\.[a-zA-Z]{2,3}$");
    }

    /**
     * 初始化控件设置，设置控件监听器。
     * @param context
     */
    private void init(final Context context){
        //设置为自定义的邮箱自动补全适配器
        this.setAdapter(new EmailAutoCompleteAdapter(context, R.layout.item_auto_complete_edit,emailSufixs));
        //当字符数大于等于1时启动自动补全
        this.setThreshold(1);
        //设置焦点监听器
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    String text=EmailAutoCompleteTextView.this.getText().toString();
                    //当获文本域得焦点时，进行自动补全活动
                    if("".equals(text)){
                        performFiltering(text,0);
                    }
                } else{
                    EmailAutoCompleteTextView emailView =(EmailAutoCompleteTextView) view;
                    String text =emailView.getText().toString();

                    if(text==null||"".equals(text)){
                        Toast toast =Toast.makeText(context,"邮箱地址不能为空",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else if(!isLegalAddress(text)){
                        Toast toast =Toast.makeText(context,"邮箱地址不合法",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }

    @Override
    protected void replaceText(CharSequence text) {
        //当我们在下拉框中选择一项时，android会默认使用AutoCompleteTextView中Adapter里的文本来填充文本域
        //因为这里Adapter中只是存了常用email的后缀
        //因此要重新replace逻辑，将用户输入的部分与后缀合并
        String inputText=this.getText().toString();
        int index=inputText.indexOf("@");
        if(index!=-1){
            inputText=inputText.substring(0,index);
        }
        super.replaceText(inputText + text);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode) {
        String inputText =text.toString();

        int index=inputText.indexOf("@");
        if(index==-1){
            if(inputText.matches("^[a-zA-Z0-9]+([\\_|\\-|\\.]?[a-zA-Z0-9])*$")){
                super.performFiltering("@", keyCode);
            }
            else{
                this.dismissDropDown();//输入非法字符关闭下拉框
            }
        } else{
            super.performFiltering(inputText.substring(index), keyCode);
        }
    }

    private class EmailAutoCompleteAdapter extends ArrayAdapter<String> {

        public EmailAutoCompleteAdapter(Context context, int textViewResourceId, String[] email_s){
            super(context,textViewResourceId,email_s);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =convertView;
            if(view==null){
                view= LayoutInflater.from(getContext()).inflate(R.layout.item_auto_complete_edit,null);
            }
            TextView textView =(TextView)view.findViewById(R.id.item);

            String inputText = EmailAutoCompleteTextView.this.getText().toString();
            int index=inputText.indexOf("@");
            if(index!=-1){
                inputText=inputText.substring(0,index);
            }
            //将用户输入的文字与adapter中的email后缀拼接后，在下拉框中显示
            textView.setText(inputText+getItem(position));
            return view;
        }
    }
}
