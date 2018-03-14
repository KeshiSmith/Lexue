package com.zw.lexue.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zw.lexue.R;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.FileCodeModel;
import com.zw.lexue.utils.CommonAysncTask;
import com.zw.lexue.utils.DownloadAysncTask;
import com.zzhoujay.richtext.RichText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 本文档为文章界面，用于浏览文章等操作。
 * Created by Keshi Smith on 2017/7/16.
 */

public class ArticleActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_GET_ARTICLE = 0X1000;//获取评论请求码
    public static final int REQUEST_DOWNLOAD_ACCOUNT_PORTRAIT = 0X1001;//获取头像请求码
    public static final int REQUEST_ADD_COLLECT = 0x1003;//添加收藏请求码
    public static final int REQUEST_REMOVE_COLLECT = 0x1004;//取消收藏请求码

    private Boolean hasFavoured;
    private Integer articleId, favourCount, commentCount;
    private String accountHeadFileId, accountName, contentFileId;
    private LinearLayout llBack, llCollect, llReview;
    private RoundedImageView portrait;
    private ImageView imgCollect, imgReview;
    private TextView tvName, tvArticle, tvCollect, tvReview;

    //消息传递句柄
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取文章内容
                case REQUEST_GET_ARTICLE:
                    if(msg.obj == null){
                        //更新加载文章
                        updateContent(contentFileId);
                    }
                    else switch (((FileCodeModel)msg.obj).getCode()){
                        case 0:
                            showMessage("服务器连接失败，请稍后再试");
                            break;
                        case -1:
                            showMessage("连接失败，请检查网络设置");
                            break;
                        case 401:
                            showMessage("文章加载错误");
                            break;
                        case 402:
                            showMessage("文章不存在");
                            break;
                    }
                    break;
                //添加收藏
                case REQUEST_ADD_COLLECT:
                    if(msg.obj == null||!((String)msg.obj).equalsIgnoreCase("200")){
                        showMessage("添加收藏失败");
                    }
                    break;
                //取消收藏
                case REQUEST_REMOVE_COLLECT:
                    if(msg.obj == null||!((String)msg.obj).equalsIgnoreCase("200")){
                        showMessage("移除收藏失败");
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        intiView();//初始化布局控件
        intiData();//初始化控件数据
    }

    private void intiView() {
        //结束本页面功能实现
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishThisActivity();
            }
        });
        //获取页面控件
        portrait = (RoundedImageView)findViewById(R.id.portrait);
        tvName = (TextView)findViewById(R.id.tvName);
        llCollect = (LinearLayout)findViewById(R.id.llCollect);
        imgCollect = (ImageView) findViewById(R.id.collect);
        tvCollect = (TextView)findViewById(R.id.collectCount);
        llReview = (LinearLayout)findViewById(R.id.llReview);
        imgReview = (ImageView)findViewById(R.id.review);
        tvReview = (TextView)findViewById(R.id.reviewCount);
        tvArticle = (TextView) findViewById(R.id.tvArticle);
        //设置监听器
        llCollect.setOnClickListener(this);
        llReview.setOnClickListener(this);
    }

    private void intiData(){
        //获取上个页面数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        articleId = (Integer)bundle.get("articleId");
        //设置当前页面数据
        accountHeadFileId = (String)bundle.get("accountHeadFileId");
        if(accountHeadFileId!=null){
            //设置头像
            updatePortrait(accountHeadFileId);
        }
        accountName = (String)bundle.get("accountName");
        if(accountName!=null){
            //设置名字
            tvName.setText(accountName);
        }
        favourCount = (Integer)bundle.get("favourCount");
        if(favourCount!=null){
            //设置收藏数量
            tvCollect.setText(favourCount.toString());
        }
        commentCount = (Integer)bundle.get("commentCount");
        if(commentCount!=null){
            //设置评论数量
            tvReview.setText(commentCount.toString());
        }
        hasFavoured = (Boolean) bundle.get("hasFavoured");
        if(hasFavoured!=null){
            //设置收藏状态
            imgCollect.setSelected(hasFavoured);
        }
        contentFileId = (String)bundle.get("contentFileId");
        if(contentFileId!=null){
            updateContent(contentFileId);
        }
    }

    /**
     * 加载更新头像
     */
    private void updatePortrait(String headFileId){
        if(headFileId != null){
            File file = new File(ApplicationModel.getCacheImagePath(), headFileId);
            if(file.exists()){
                //若头像文件已存在，则加载头像
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                portrait.setImageBitmap(bitmap);
            }
            else {
                //若头像文件不存在，则下载头像文件
                portrait.setImageResource(R.drawable.no_image);
                DownloadAysncTask downloadAysncTask = new DownloadAysncTask(
                        handler,
                        ApplicationModel.getFileDownloadAddress(),
                        ApplicationModel.getCacheImagePath(),
                        headFileId,
                        REQUEST_DOWNLOAD_ACCOUNT_PORTRAIT);
                downloadAysncTask.execute();
            }
        }
    }

    /**
     * 更新页面内容，填充文章
     * @param contentFileId
     */
    private void updateContent(String contentFileId){
        //更新页面内容
        File file = new File(ApplicationModel.getCacheArticlePath(), contentFileId);
        if(file.exists()){
            //若文件已存在，则加载文章
            try {
                FileInputStream is = new FileInputStream(file);
                //使用Reader读取文本文件
                InputStreamReader reader = new InputStreamReader(is);
                StringBuffer content = new StringBuffer();
                char[] buffer = new char[1024];
                //循环读取文件中的字符
                while ((reader.read(buffer))!=-1){
                    content.append(buffer);
                }
                //更新页面
                RichText.fromMarkdown(content.toString()).into(tvArticle);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            //若内容文件不存在，则下载内容文件
            DownloadAysncTask downloadAysncTask = new DownloadAysncTask(
                    handler,
                    ApplicationModel.getFileDownloadAddress(),
                    ApplicationModel.getCacheArticlePath(),
                    contentFileId,
                    REQUEST_GET_ARTICLE);
            downloadAysncTask.execute();
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

    /**
     * 结束此页面，并携带相关数据
     */
    private void finishThisActivity(){
        Intent intent = new Intent();
        intent.putExtra("articleId", articleId);
        intent.putExtra("favourCount", favourCount);
        intent.putExtra("favoured", hasFavoured);
        intent.putExtra("commentCount", commentCount);
        setResult(MainActivity.REQUEST_ARTICLE, intent);
        finish();
    }

    /**
     * 返回键携带数据返回
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finishThisActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llCollect:
                //设置添加取消收藏效果
                hasFavoured =! hasFavoured;
                if(hasFavoured){
                    //添加收藏
                    new CommonAysncTask(
                            handler,
                            ApplicationModel.getAddCollectAddress(),
                            "articleId=" + articleId,
                            REQUEST_ADD_COLLECT).execute();
                    favourCount ++;
                }
                else{
                    //移除收藏
                    new CommonAysncTask(
                            handler,
                            ApplicationModel.getRemoveCollectAddress(),
                            "articleId=" + articleId,
                            REQUEST_REMOVE_COLLECT).execute();
                    favourCount --;
                }
                imgCollect.setSelected(hasFavoured);
                tvCollect.setText(favourCount.toString());
                break;
            case R.id.llReview:
                //跳转到评论页面
                Intent reviewIntent = new Intent(ArticleActivity.this, ReviewActivity.class);
                reviewIntent.putExtra("title", "评论");
                reviewIntent.putExtra("articleId", articleId);
                startActivity(reviewIntent);
                break;
        }
    }
}
