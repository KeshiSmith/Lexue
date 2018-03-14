package com.zw.lexue.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zw.lexue.R;
import com.zw.lexue.adapter.ArticleListAdapter;
import com.zw.lexue.model.AccountDetailModel;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.ArticleListModel;
import com.zw.lexue.model.ArticleModel;
import com.zw.lexue.model.FileCodeModel;
import com.zw.lexue.utils.CommonAysncTask;
import com.zw.lexue.utils.DownloadAysncTask;
import com.zw.lexue.utils.GetAccountDetailAysncTask;
import com.zw.lexue.utils.GetArticleListAysncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本文档为通用页面文档，用于目前阶段的测试使用，未来将会根据具体情况分化完善。
 * Created by Keshi Smith on 2017/7/16.
 */

public class UserActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, ArticleListAdapter.Callback {

    private static final int REQUEST_DOWNLOAD_USER_PORTRAIT = 0X1000;
    private static final int REFRESH_COMPLETE = 0X1001;
    private static final int REQUEST_GET_ARTICLE_LIST = 0X1002;
    private static final int REQUEST_ADD_COLLECT = 0X1003;
    private static final int REQUEST_REMOVE_COLLECT = 0X1004;
    private static final int REQUEST_ARTICLE = 0X1005;
    private static final int REQUEST_GET_USER_DETAIL = 0X1006;

    //全局变量
    private Integer accountId;
    private String accountName;
    private String headFileId;
    private String signature;
    //本页面控件
    private LinearLayout llBack;
    private TextView tvTitle, userName, userWords;
    private RoundedImageView portrait;
    private int nextPage = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView articleListView;
    private ArticleListAdapter articleAdapter;
    private Map<Integer,ArticleModel> articlesMap = new HashMap<>();
    private List<ArticleModel> articles = new ArrayList();

    //消息传递句柄
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REQUEST_DOWNLOAD_USER_PORTRAIT:
                    if(msg.obj == null){
                        //更新加载头像
                        updatePortrait(headFileId);
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
                //更新显示，刷新完成
                case REFRESH_COMPLETE:
                    if(msg.obj != null){
                        //更新文章列表
                        ArticleListModel articleListModel = (ArticleListModel) msg.obj;
                        List<ArticleModel> list = articleListModel.getArticleList();
                        list.addAll(articles);
                        articles.clear();
                        articles.addAll(list);
                        articleAdapter.notifyDataSetChanged();
                        //添加文章至Map
                        articlesMap.clear();
                        for(ArticleModel article:articles){
                            articlesMap.put(article.getId(),article);
                        }
                        //下一页标志加1
                        if(nextPage <= articleListModel.getTotalPage()){
                            nextPage += 1;
                        }
                    }
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(UserActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    break;
                //获取文章列表
                case REQUEST_GET_ARTICLE_LIST:
                    if(msg.obj != null){
                        //更新文章列表
                        ArticleListModel articleListModel = (ArticleListModel) msg.obj;
                        for(ArticleModel article : articleListModel.getArticleList()){
                            articles.add(article);
                            articlesMap.put(article.getId(),article);
                        }
                        articleAdapter.notifyDataSetChanged();
                        //下一页标志加1
                        if(nextPage <= articleListModel.getTotalPage()){
                            nextPage += 1;
                        }
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
                //获取用户详情
                case  REQUEST_GET_USER_DETAIL:
                    if(msg.obj!=null){
                        AccountDetailModel model = (AccountDetailModel) msg.obj;
                        headFileId = model.getHeadFileId();
                        updatePortrait(headFileId);//更新头像控件
                        signature = model.getSelfIntroduction();
                        if(signature!=null && signature.length()>0){
                            userWords.setText(signature);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        intiView();//初始化布局控件
    }

    private void intiView() {
        //结束本页面功能实现
        llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //获取其他控件
        tvTitle=(TextView)findViewById(R.id.tvTitle);
        portrait = (RoundedImageView) findViewById(R.id.portrait);
        userName = (TextView)findViewById(R.id.userName);
        userWords = (TextView)findViewById(R.id.userWords);
        //获取上个页面数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //设置当前页面数据
        String title = (String)bundle.get("title");
        if(title!=null||title.length()>0){
            tvTitle.setText(title);
        }

        accountId = (Integer) bundle.get("accountId");
        if(accountId!=null){
            new GetAccountDetailAysncTask(
                    handler,
                    ApplicationModel.getAccountDetailAddress(),
                    accountId,
                    REQUEST_GET_USER_DETAIL).execute();
        }

        headFileId = (String) bundle.get("headFileId");
        if(headFileId!=null){
            updatePortrait(headFileId);
        }

        accountName = (String) bundle.get("accountName");
        if (accountName!=null){
            userName.setText(accountName);
        }

        //获取文章列表
        new GetArticleListAysncTask(
                handler,
                ApplicationModel.getArticleListOfOwnerAddress(),
                "accountId="+accountId+"&page="+nextPage,
                REQUEST_GET_ARTICLE_LIST).execute();
        //设置SwipeRefreshLayout控件参数
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refreshLayout);
        articleListView = (ListView) findViewById(R.id.listView);
        swipeRefreshLayout.setOnRefreshListener(this);
        //设置适配器
        articleAdapter = new ArticleListAdapter(
                this,
                handler,
                R.layout.item_articles,
                articles,
                this);
        articleListView.setAdapter(articleAdapter);
    }

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
                DownloadAysncTask downloadAysncTask = new DownloadAysncTask(
                        handler,
                        ApplicationModel.getFileDownloadAddress(),
                        ApplicationModel.getCacheImagePath(),
                        headFileId,
                        REQUEST_DOWNLOAD_USER_PORTRAIT);
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

    @Override
    public void onRefresh() {
        new GetArticleListAysncTask(
                handler,
                ApplicationModel.getArticleListOfOwnerAddress(),
                "accountId="+accountId+"&page="+nextPage,
                REFRESH_COMPLETE).execute();
    }

    @Override
    public void click(View view) {
        Integer position = (Integer) view.getTag();
        ArticleModel article = articles.get(position);

        switch (view.getId()) {
            //跳转到文章页面
            case R.id.llArticle:
                Intent articleIntent = new Intent(UserActivity.this, ArticleActivity.class);
                articleIntent.putExtra("articleId", article.getId());
                articleIntent.putExtra("accountHeadFileId", article.getAccountHeadFileId());
                articleIntent.putExtra("accountName", article.getAccountName());
                articleIntent.putExtra("favourCount", article.getFavourCount());
                articleIntent.putExtra("commentCount", article.getCommentCount());
                articleIntent.putExtra("hasFavoured", article.getFavoured());
                articleIntent.putExtra("contentFileId", article.getContentFileId());
                startActivityForResult(articleIntent, REQUEST_ARTICLE);
                break;
            //设置点击收藏按钮效果
            case R.id.llCollect:
                article.setFavoured(!article.getFavoured());
                int collectCount = article.getFavourCount();
                if (article.getFavoured()) {
                    //添加收藏
                    new CommonAysncTask(
                            handler,
                            ApplicationModel.getAddCollectAddress(),
                            "articleId=" + article.getId(),
                            REQUEST_ADD_COLLECT).execute();
                    article.setFavourCount(collectCount + 1);
                } else {
                    //移除收藏
                    new CommonAysncTask(
                            handler,
                            ApplicationModel.getRemoveCollectAddress(),
                            "articleId=" + article.getId(),
                            REQUEST_REMOVE_COLLECT).execute();
                    article.setFavourCount(collectCount - 1);
                }
                articleAdapter.notifyDataSetChanged();//更新显示
                break;
            //跳转到评论页面
            case R.id.llReview:
                Intent reviewIntent = new Intent(UserActivity.this, ReviewActivity.class);
                reviewIntent.putExtra("title", "评论");
                reviewIntent.putExtra("articleId", article.getId());
                startActivity(reviewIntent);
                break;
            //跳转到标签文章页面
            case R.id.tag:
                Intent tagIntent = new Intent(UserActivity.this, ArticleListActivity.class);
                tagIntent.putExtra("type", ArticleListActivity.TYPE.LIST_ARTICLE);
                tagIntent.putExtra("label", article.getLabel());
                tagIntent.putExtra("title", article.getLabel());
                startActivity(tagIntent);
                break;
        }
    }

    /**
     * 获取返回页面值函数，根据各页面返回值，做出应对。
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //文章页面返回码
        if(requestCode == REQUEST_ARTICLE){
            if(data != null){
                Bundle bundle = data.getExtras();
                Integer articleId = (Integer) bundle.get("articleId");
                Integer favourCount = (Integer) bundle.get("favourCount");
                Boolean favoured = (Boolean) bundle.get("favoured");
                Integer commentCount = (Integer) bundle.get("commentCount");
                ArticleModel article = articlesMap.get(articleId);
                article.setFavourCount(favourCount);
                article.setFavoured(favoured);
                article.setCommentCount(commentCount);
                //更新文章显示
                articleAdapter.notifyDataSetChanged();
            }
        }
    }
}
