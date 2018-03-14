package com.zw.lexue.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zw.lexue.R;
import com.zw.lexue.adapter.ReviewListAdapter;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.FileCodeModel;
import com.zw.lexue.model.ReviewModel;
import com.zw.lexue.utils.AddCommentAysncTask;
import com.zw.lexue.utils.GetReviewListAysncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 本文档未评论页面，用于浏览和回复评论。
 * Created by Keshi Smith on 2017/7/16.
 */

public class ReviewActivity extends AppCompatActivity implements View.OnClickListener, ReviewListAdapter.Callback {

    //静态常量
    public static final int REQUEST_GET_REVIEW_LIST = 0X1000;//获取文章请求码
    public static final int REQUEST_DOWNLOAD_REVIEW_PORTRAIT = 0X1001;//获取评论头像请求码
    public static final int REQUEST_ADD_COMMENT = 0X1002;//添加评论请求码
    //数据类
    private List<ReviewModel> comments = new ArrayList<>();
    //布局控件
    private Integer articleId;
    private String title;
    private LinearLayout llBack;
    private TextView tvTitle;
    private ListView listView;
    private EditText etReview;
    private Button buttonSend;
    private ReviewListAdapter reviewAdapter;

    //消息传递句柄
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                //获取评论列表
                case REQUEST_GET_REVIEW_LIST:
                    if(msg.obj!=null){
                        //更新显示
                        comments.clear();
                        comments.addAll((List<ReviewModel>) msg.obj);
                        reviewAdapter.notifyDataSetChanged();
                    }
                    break;
                //下载评论头像
                case REQUEST_DOWNLOAD_REVIEW_PORTRAIT:
                    if(msg.obj == null){
                        //更新加载评论头像
                        reviewAdapter.notifyDataSetChanged();
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
                case REQUEST_ADD_COMMENT:
                    if(msg.obj!=null){
                        switch ((String) msg.obj){
                            case "failed":
                                showMessage("服务器连接失败，请稍后再试");
                                break;
                            case "error":
                                showMessage("连接失败，请检查网络设置");
                                break;
                            case "301":
                                showMessage("用户未登陆");
                                break;
                            case "302":
                                showMessage("参数不合法");
                                break;
                            case "200":
                                //更新评论内容
                                GetReviewListAysncTask getReviewListAysncTask = new GetReviewListAysncTask(
                                        handler,
                                        ApplicationModel.getReviewListAddress(),
                                        articleId,
                                        REQUEST_GET_REVIEW_LIST);
                                getReviewListAysncTask.execute();
                                //清空评论编辑控件
                                etReview.setText("");
                                showMessage("评论成功");
                                break;
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        intiView();//初始化布局控件
        intiData();//初始化控件数据
    }

    /**
     * 初始化页面布局
     */
    private void intiView() {
        //结束本页面功能实现
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //获取页面控件
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        listView = (ListView) findViewById(R.id.listView);
        etReview = (EditText) findViewById(R.id.etReview);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        //设置控件监听器
        buttonSend.setOnClickListener(this);
        //设置适配器
        reviewAdapter = new ReviewListAdapter(
                this,
                handler,
                R.layout.item_reviews,
                comments,
                this);
        listView.setAdapter(reviewAdapter);
    }

    /**
     * 初始化页面数据
     */
    private void intiData(){
        //获取上个页面数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //设置当前页面数据
        title = (String)bundle.get("title");
        if(title!=null||title.length()>0){
            tvTitle.setText(title);
        }
        articleId = (Integer) bundle.get("articleId");
        if(articleId!=null){
            //获取评论列表
            GetReviewListAysncTask getReviewListAysncTask = new GetReviewListAysncTask(
                    handler,
                    ApplicationModel.getReviewListAddress(),
                    articleId,
                    REQUEST_GET_REVIEW_LIST);
            getReviewListAysncTask.execute();
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

    @Override
    public void click(View view) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonSend:
                if(etReview.length() == 0){
                    showMessage("评论不能为空");
                }
                else if(etReview.length() < 7){
                    showMessage("评论不能少于7个字词");
                }
                else{
                    // 发送评论异步任务
                    AddCommentAysncTask addCommentAysncTask = new AddCommentAysncTask(
                            handler,
                            ApplicationModel.getAddCommentAddress(),
                            articleId,
                            etReview.getText().toString(),
                            REQUEST_ADD_COMMENT);
                    addCommentAysncTask.execute();
                }
                break;
        }
    }
}
