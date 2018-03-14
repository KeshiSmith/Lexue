package com.zw.lexue.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zw.lexue.R;
import com.zw.lexue.model.AccountDetailModel;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.FileCodeModel;
import com.zw.lexue.model.SchoolAndMajorModel;
import com.zw.lexue.utils.CommonAysncTask;
import com.zw.lexue.utils.DownloadAysncTask;
import com.zw.lexue.utils.GetAccountDetailAysncTask;
import com.zw.lexue.utils.GetMajorListAysncTask;
import com.zw.lexue.utils.GetSchoolListAysncTask;
import com.zw.lexue.utils.UploadAysncTask;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本文档为用户详情页面的Java文档，用于查看与修改用户信息及其相关内容。
 * Created by Keshi Smith on 2017/7/16.
 */

public class UserDetailsActivity extends AppCompatActivity implements View.OnClickListener  {

    public static final String IMAGE_FILE_NAME = "god_is_dad";
    public static final String IMAGE_TRIM_FILE_NAME = "god_is_dead";

    //自定义常量
    public static final int REQUEST_CODE_TAKE = 0X1000;         //照相请求码
    public static final int REQUEST_CODE_PICK = 0X1001;         //选取照片请求码
    public static final int REQUEST_CODE_CUTTING = 0X1002;      //裁剪图片请求码
    public static final int REQUEST_DOWNLOAD_PORTRAIT = 0X1003; //头像下载请求码
    public static final int REQUEST_GET_ACCOUNT_DETAIL = 0X1004;//获取详细信息请求码
    public static final int REQUEST_UPLOAD_PORTRAIT = 0X1005;   //头像上传请求码
    public static final int REQUEST_GET_SCHOOL_LIST = 0X1006;   //获取学校请求码
    public static final int REQUEST_GET_MAJOR_LIST = 0X1007;    //获取专业请求码
    public static final int REQUEST_UPDATE_INFO = 0X1008;       //更新信息请求码

    private Map<String, SchoolAndMajorModel> schoolMap = new HashMap<>(); //大学列表
    private Map<String, SchoolAndMajorModel> majorMap = new HashMap<>();  //专业列表

    private List<String> optionsSexItems = new ArrayList<>();   //性别选项内容
    private List<String> schoolList = new ArrayList<>();    //大学候选内容
    private List<String> majorList = new ArrayList<>();     //大学候选内容

    private LinearLayout llBack;                        //返回按钮
    private RoundedImageView imagePortrait;             //头像图片控件
    private TextView textViewSave, textViewUserName;    //保存和用户名控件
    private TextView textViewSex;                       //性别文字控件
    private EditText editTextPhone, editTextGrade, editTextSignature;   //入学年份和个人介绍
    private Spinner spinnerCollege, spinnerMajor;                       //自动补全大学和专业
    private OptionsPickerView pvOptions;    //文字拾取控件
    private PopupWindow menuWindow;         //拾取头像菜单窗口

    private String portraitId, school, major;

    //消息传递，用于处理注册结果。
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                //获取用户详细信息
                case REQUEST_GET_ACCOUNT_DETAIL:
                    if(msg.obj!=null){
                        //保存并新界面显示
                        ApplicationModel.setAccountDetail((AccountDetailModel) msg.obj);
                        ApplicationModel.saveApplicationData(UserDetailsActivity.this);
                        //获取个人头像
                        portraitId = ApplicationModel.getHeadFileId();
                        updatePortrait(portraitId);
                        updateViewOfActivity();
                    }
                    //获取大学列表
                    school = ApplicationModel.getSchool();
                    GetSchoolListAysncTask getSchoolListAysncTask = new GetSchoolListAysncTask(
                            handler,
                            ApplicationModel.getSchoolListAddress(),
                            REQUEST_GET_SCHOOL_LIST);
                    getSchoolListAysncTask.execute();
                    //获取专业列表
                    major = ApplicationModel.getMajor();
                    if(major!=null) {
                        GetMajorListAysncTask getMajorListAysncTask = new GetMajorListAysncTask(
                                handler,
                                ApplicationModel.getSchoolMajorAddress(),
                                ApplicationModel.getSchoolId(),
                                REQUEST_GET_MAJOR_LIST);
                        getMajorListAysncTask.execute();
                    }
                    break;
                //头像下载请求
                case REQUEST_DOWNLOAD_PORTRAIT:
                    if(msg.obj == null){
                        //更新加载头像
                        updatePortrait(portraitId);
                    }
                    else switch (((FileCodeModel)msg.obj).getCode()){
                        case 0:
                            showMessage("服务器连接失败，请稍后再试");
                            break;
                        case -1:
                            showMessage("连接失败，请检查网络设置");
                            break;
                        case 401:
                            showMessage("头像控件加载错误");
                            break;
                        case 402:
                            showMessage("头像文件不存在");
                            break;
                    }
                    break;
                //头像上传请求
                case REQUEST_UPLOAD_PORTRAIT:
                    if(msg.obj != null){
                        switch (((FileCodeModel)msg.obj).getCode()){
                            case 0:
                                showMessage("[code 0] 服务器连接失败，请稍后再试");
                                break;
                            case -1:
                                showMessage("[code -1] 连接失败，请检查网络设置");
                                break;
                            case 200://成功
                            case 201://成功秒传
                                portraitId = ((FileCodeModel)msg.obj).getMessage();
                                updatePortrait(portraitId);//更新头像
                                break;
                            case 301://数据请求格式不正确
                                showMessage("[code 301] 异常：数据请求格式不正确");
                                break;
                            case 302://上传多个文件
                                showMessage("[code 302] 异常：上传多个文件");
                                break;
                            case 303://上传多个文件
                                showMessage("[code 303] 异常：未发现上传文件");
                                break;
                            case 305://未知异常
                                showMessage("[code 305] 异常：未知");
                                break;
                        }
                    }
                    break;
                //获取学校列表
                case REQUEST_GET_SCHOOL_LIST:
                    if(msg.obj != null){
                        //更新列表内容
                        List<SchoolAndMajorModel> list = (ArrayList<SchoolAndMajorModel>) msg.obj;
                        schoolMap.clear();  //清除原Map内容
                        schoolList.clear(); //清除原List内容
                        for (SchoolAndMajorModel school: list) {
                            schoolMap.put(school.getName(), school);
                            schoolList.add(school.getName());
                        }
                        //更新适配器
                        spinnerCollege.setAdapter(new ArrayAdapter(
                                UserDetailsActivity.this,
                                R.layout.item_auto_complete_edit,
                                schoolList));
                    }
                    break;
                //获取专业列表
                case REQUEST_GET_MAJOR_LIST:
                    if(msg.obj != null){
                        //更新列表内容
                        List<SchoolAndMajorModel> list = (ArrayList<SchoolAndMajorModel>) msg.obj;
                        majorMap.clear();      //清除原Map内容
                        majorList.clear();     //清除原List内容
                        for (SchoolAndMajorModel major: list) {
                            majorMap.put(major.getName(), major);
                            majorList.add(major.getName());
                        }
                        //更新适配器
                        spinnerMajor.setAdapter(new ArrayAdapter<String>(
                                UserDetailsActivity.this,
                                R.layout.item_auto_complete_edit,
                                majorList));
                    }
                    break;
                //更新个人信息
                case REQUEST_UPDATE_INFO:
                    if(msg.obj != null){
                        switch ((String) msg.obj){
                            case "302":
                            case "200":
                                //TODO 考核使用302
                                showMessage("更新成功");
                                finish();
                                break;
                            case "301":
                                showMessage("参数不合法");
                                break;
                        }
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        initHttpData();         //获取网络数据

        getOptionsDate();       //初始化数据
        intiView();             //初始化布局控件

        updatePortrait(ApplicationModel.getHeadFileId());       //更新用户头像
        updateViewOfActivity(); //更新用户详情
    }


    /**
     * 执行异步任务，获取相关账户信息。
     */
    private void initHttpData(){
        //获取用户详细信息
        GetAccountDetailAysncTask getAccountDetailAysncTask = new GetAccountDetailAysncTask(
                handler,
                ApplicationModel.getAccountDetailAddress(),
                ApplicationModel.getAccountId(),
                REQUEST_GET_ACCOUNT_DETAIL);
        getAccountDetailAysncTask.execute();
    }

    /**
     * 设置拾取器数据
     */
    private void getOptionsDate()
    {
        //添加性别选项内容
        optionsSexItems.add("未知");
        optionsSexItems.add("男");
        optionsSexItems.add("女");
    }

    /**
     * 初始化页面布局与控件
     */
    private void intiView() {
        //结束本页面功能实现
        llBack = (LinearLayout) findViewById(R.id.back);
        llBack.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //保存用户详细信息
        textViewSave = (TextView) findViewById(R.id.save);
        textViewSave.setOnClickListener(this);

        //设置本页面控件数据
        imagePortrait = (RoundedImageView) findViewById(R.id.portrait);
        imagePortrait.setOnClickListener(this);//为头像控件设置监听器
        textViewUserName = (TextView) findViewById(R.id.userName);

        //性别文字控件绑定监听器
        textViewSex = (TextView)findViewById(R.id.textViewSex);
        textViewSex.setOnClickListener(this);

        //手机号
        editTextPhone = (EditText)findViewById(R.id.etPhone);

        //自动补全控件
        spinnerCollege = (Spinner) findViewById(R.id.spinnerCollege);
        spinnerMajor = (Spinner)findViewById(R.id.spinnerMajor);
        //添加监听器
        spinnerCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //反射强制触发监听
                try {
                    //以下三行代码是解决问题所在
                    Field field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                    field.setAccessible(true);  //设置mOldSelectedPosition可访问
                    field.setInt(spinnerCollege, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //学校监听事件
                school = schoolList.get(i);
                //更新专业事件
                new GetMajorListAysncTask(
                        handler,
                        ApplicationModel.getSchoolMajorAddress(),
                        schoolMap.get(school).getId(),
                        REQUEST_GET_MAJOR_LIST).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //专业监听事件
                major = majorList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //其他相关控件
        editTextGrade = (EditText)findViewById(R.id.editTextGrade);
        editTextSignature = (EditText)findViewById(R.id.editTextSignature);

        //初始化图片拾取窗口
        View dialogPickImage = getLayoutInflater().inflate(R.layout.dialog_pick_image, null);
        menuWindow = new PopupWindow(dialogPickImage, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        menuWindow.setAnimationStyle(R.style.PopWindowAnimStyle);//设置窗口动画
        menuWindow.setOutsideTouchable(true);//设置窗口外部可点击

        //设置窗口背景
        menuWindow.setBackgroundDrawable(new PaintDrawable());

        //设置窗口隐藏监听器
        menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //恢复背景亮度
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        //为窗口控件设置监听器
        Button buttonTakePhoto = (Button)dialogPickImage.findViewById(R.id.buttonTakePhoto);
        Button buttonPickImage = (Button)dialogPickImage.findViewById(R.id.buttonPickImage);
        Button buttonBack = (Button)dialogPickImage.findViewById(R.id.buttonBack);
        buttonTakePhoto.setOnClickListener(this);
        buttonPickImage.setOnClickListener(this);
        buttonBack.setOnClickListener(this);

        //条件选择器
        pvOptions = new  OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3 ,View v) {
                //返回的分别是三个级别的选中位置
                String tx = optionsSexItems.get(options1);
                textViewSex.setText(tx);
            }
        })
                .setTitleBgColor(ContextCompat.getColor(this, R.color.colorPrimary))        //标题背景颜色
                .setBgColor(ContextCompat.getColor(this, R.color.colorBackground))          //滚轮颜色
                .setDividerColor(ContextCompat.getColor(this, R.color.colorPrimaryDarkest)) //分割线颜色
                .setCancelColor(ContextCompat.getColor(this, R.color.colorTitleText))       //取消键颜色
                .setSubmitColor(ContextCompat.getColor(this, R.color.colorTitleText))       //确定键颜色
                .setTitleColor(ContextCompat.getColor(this, R.color.colorTitleText))        //标题文字颜色
                .setTextColorCenter(ContextCompat.getColor(this, R.color.colorDarkestText)) //选中文字颜色
                .setTextColorOut(ContextCompat.getColor(this, R.color.colorDarkText))       //未选中文字颜色
                .setTitleText("你的性别")                                                    //标题名称
                .build();
        pvOptions.setPicker(optionsSexItems);//设置条件选择器文字
    }

    /**
     * 事件监听器，监听此页面发生的事件。
     * @param v 发生点击事件的页面布局
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.save:
                //验证表单格式
                Integer gender = optionsSexItems.indexOf(textViewSex.getText().toString());
                String phone = editTextPhone.getText().toString();
                if(phone.length()<11){
                    showMessage("手机号格式不正确");
                    break;
                }
                if(school.length()==0||major.length()==0){
                    showMessage("学校和专业不能为空");
                    break;
                }
                String grade = editTextGrade.getText().toString();
                if(grade.length()<4){
                    showMessage("年份格式不正确");
                    break;
                }
                String signature = editTextSignature.getText().toString();
                //更新信息
                new CommonAysncTask(
                        handler,
                        ApplicationModel.getAddInfoAddress(),
                        "accountId=" + ApplicationModel.getAccountId() +
                                "&headFileId=" + portraitId +
                                "&gender="+gender +
                                "&selfIntroduction=" + signature +
                                "&phoneNumber=" + phone +
                                "&school=" + schoolMap.get(school).getId() + ":" + school +
                                "&major=" + majorMap.get(major).getId() + ":" + major +
                                "&grade=" + grade,
                        REQUEST_UPDATE_INFO).execute();
                break;
            case R.id.portrait:
                //使其背景变暗
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.6f;
                getWindow().setAttributes(lp);
                //显示头像拾取窗口
                View rootView = getLayoutInflater().inflate(R.layout.activity_user_detail,null);
                menuWindow.showAtLocation(rootView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.textViewSex:
                if(pvOptions!=null){
                    pvOptions.show(); //弹出自定义条件选择
                }
                break;
            case R.id.buttonTakePhoto:
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(ApplicationModel.getTempPath(), IMAGE_FILE_NAME)));
                startActivityForResult(takeIntent, REQUEST_CODE_TAKE);
                break;
            case  R.id.buttonPickImage:
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickIntent, REQUEST_CODE_PICK);
                break;
            case R.id.buttonBack:
                menuWindow.dismiss();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_TAKE:// 调用相机拍照
                File temp = new File(ApplicationModel.getTempPath(), IMAGE_FILE_NAME);
                startPhotoZoom(Uri.fromFile(temp));
                break;
            case REQUEST_CODE_PICK:// 直接从相册获取
                try {
                    startPhotoZoom(data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case REQUEST_CODE_CUTTING:// 取得裁剪后的图片
                if (data != null) {
                    //上传裁剪好的图片
                    new UploadAysncTask(
                            handler,
                            ApplicationModel.getFileUploadAddress(),
                            ApplicationModel.getTempPath() + '/' + IMAGE_TRIM_FILE_NAME,
                            "file",
                            ApplicationModel.getName() + ".jpg",
                            "image/jpeg",
                            REQUEST_UPLOAD_PORTRAIT).execute();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪获取的图片，调整其大小为256*256
     * @param uri 图片所在路径
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", ApplicationModel.PORTRAIT_SIZE);
        intent.putExtra("outputY", ApplicationModel.PORTRAIT_SIZE);

        //此方法返回的图片只能是小图片（sumsang测试为高宽160px的图片）
        //故将图片保存在Uri中，调用时将Uri转换为Bitmap，此方法还可解决miui系统不能return data的问题
        //intent.putExtra("return-data", true);

        //uritempFile为Uri类变量，实例化uritempFile
        Uri uritempFile = Uri.fromFile(new File(ApplicationModel.getTempPath(), IMAGE_TRIM_FILE_NAME));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        startActivityForResult(intent, REQUEST_CODE_CUTTING);
    }

    /**
     * 更新账户详情页
     */
    private void updateViewOfActivity(){
        if(ApplicationModel.getName() != null){
            textViewUserName.setText(ApplicationModel.getName());
        }
        //设置性别
        String sex;
        switch (ApplicationModel.getGender()){
            case ApplicationModel.GENDER_MALE:
                sex = "男";
                break;
            case ApplicationModel.GENDER_FEMALE:
                sex = "女";
                break;
            default:
                sex = "未知";
                break;
        }
        textViewSex.setText(sex);
        editTextPhone.setText(ApplicationModel.getPhoneNumber());
        editTextGrade.setText(ApplicationModel.getGrade());
        editTextSignature.setText(ApplicationModel.getSelfIntroduction());
    }

    /**
     * 加载更新头像
     */
    private void updatePortrait(String headFileId){
        //加载头像文件
        if(headFileId != null){
            File file = new File(ApplicationModel.getCacheImagePath(), headFileId);
            if(file.exists()){
                //若头像文件已存在，则加载头像
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                imagePortrait.setImageBitmap(bitmap);
            }
            else {
                //若头像文件不存在，则下载头像文件
                DownloadAysncTask downloadAysncTask = new DownloadAysncTask(
                        handler,
                        ApplicationModel.getFileDownloadAddress(),
                        ApplicationModel.getCacheImagePath(),
                        headFileId,
                        REQUEST_DOWNLOAD_PORTRAIT);
                downloadAysncTask.execute();
            }
        }
    }

    /**
     * 显示信息函数
     * @param msg 需要显示的信息
     */
    private void showMessage(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
