package com.zw.lexue.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zw.lexue.R;
import com.zw.lexue.adapter.ArticleListAdapter;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.ArticleListModel;
import com.zw.lexue.model.ArticleModel;
import com.zw.lexue.utils.CommonAysncTask;
import com.zw.lexue.utils.GetArticleListAysncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用文章列表页面
 * Created by Keshi Smith on 2017/7/16.
 */

public class ArticleListActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener, ArticleListAdapter.Callback {

    //全局常量
    public static final int REFRESH_COMPLETE = 0X1000;          //刷新完成
    public static final int REQUEST_GET_ARTICLE_LIST = 0X1001;  //获取文章列表
    public static final int REQUEST_ARTICLE = 0X1002;           //文章页面请求码
    public static final int REQUEST_ADD_COLLECT = 0X1003;       //添加收藏文章请求码
    public static final int REQUEST_REMOVE_COLLECT = 0X1004;    //移除收藏文章请求码

    //页面类型
    public enum TYPE{
        LIST_ARTICLE,
        LIST_ARTICLE_WITH_HOT,
        LIST_ARTICLE_WITH_RELEASE_TIME,
        LIST_ARTICLE_WITH_CONCERN,
        LIST_ARTICLE_OF_OWNER,
        LIST_ARTICLE_OF_COLLECT,
        LIST_ARTICLE_OF_HISTORY
    }

    //全局变量
    private TYPE pageType;
    private String label;
    private Integer accountId;
    //本页面控件
    private LinearLayout llBack;
    private TextView tvTitle;
    private int nextPage = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView articleListView;
    private ArticleListAdapter articleAdapter;
    private Map<Integer,ArticleModel> articlesMap = new HashMap<>();
    private List<ArticleModel> articles = new ArrayList<ArticleModel>();

    //消息传递句柄
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                    Toast.makeText(ArticleListActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    break;
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
                        Toast.makeText(ArticleListActivity.this, "添加收藏失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //取消收藏
                case REQUEST_REMOVE_COLLECT:
                    if(msg.obj == null||!((String)msg.obj).equalsIgnoreCase("200")){
                        Toast.makeText(ArticleListActivity.this, "移除收藏失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

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
        //获取上个页面数据
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        pageType = (TYPE)bundle.get("type");
        if(pageType!=null&&pageType==TYPE.LIST_ARTICLE){
            label = (String)bundle.get("label");
            label = (label==null? "":label);
        }
        else if(pageType!=null&&pageType==TYPE.LIST_ARTICLE_OF_OWNER){
            accountId = (Integer)bundle.get("accountId");
            accountId = (accountId==null? 0:accountId);
        }
        //设置当前页面数据
        String title = (String)bundle.get("title");
        if(title!=null){
            tvTitle=(TextView)findViewById(R.id.tvTitle);
            tvTitle.setText(title);
        }
        //获取文章列表
        getArticleList(REQUEST_GET_ARTICLE_LIST);
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
        //更新显示
        articleAdapter.notifyDataSetChanged();
    }

    //获取文章列表
    private void getArticleList(int requestCode){
        switch (pageType){
            case LIST_ARTICLE:
                //更新文章列表
                new GetArticleListAysncTask(
                        handler,
                        ApplicationModel.getArticleListAddress(),
                        "label="+label+"&page="+nextPage,
                        requestCode).execute();
                break;
            case LIST_ARTICLE_WITH_HOT:
                //更新最热文章列表
                new GetArticleListAysncTask(
                        handler,
                        ApplicationModel.getArticleListWithHotAddress(),
                        "page="+nextPage,
                        requestCode).execute();
                break;
            case LIST_ARTICLE_WITH_RELEASE_TIME:
                //更新最新文章列表
                new GetArticleListAysncTask(
                        handler,
                        ApplicationModel.getArticleListWithReleaseTimeAddress(),
                        "page="+nextPage,
                        requestCode).execute();
                break;
            case LIST_ARTICLE_WITH_CONCERN:
                //更新关注文章列表
                new GetArticleListAysncTask(
                        handler,
                        ApplicationModel.getArticleListWithConcernAddress(),
                        "page="+nextPage,
                        requestCode).execute();
                break;
            case LIST_ARTICLE_OF_OWNER:
                //更新用户文章列表
                new GetArticleListAysncTask(
                        handler,
                        ApplicationModel.getArticleListOfOwnerAddress(),
                        "accountId="+accountId+"&page="+nextPage,
                        requestCode).execute();
                break;
            case LIST_ARTICLE_OF_COLLECT:
                //更新收藏文章列表
                new GetArticleListAysncTask(
                        handler,
                        ApplicationModel.getListCollectArticleAddress(),
                        "page="+nextPage,
                        requestCode).execute();
                break;
            case LIST_ARTICLE_OF_HISTORY:
                //更新历史文章列表
                new GetArticleListAysncTask(
                        handler,
                        ApplicationModel.getListHistoryArticleAddress(),
                        "page="+nextPage,
                        requestCode).execute();
                break;
        }
    }

    @Override
    public void onRefresh() {
        getArticleList(REFRESH_COMPLETE);
    }

    @Override
    public void click(View view) {
        Integer position = (Integer) view.getTag();
        ArticleModel article = articles.get(position);

        switch (view.getId()) {
            //跳转到文章页面
            case R.id.llArticle:
                Intent articleIntent = new Intent(ArticleListActivity.this, ArticleActivity.class);
                articleIntent.putExtra("articleId", article.getId());
                articleIntent.putExtra("accountHeadFileId",article.getAccountHeadFileId());
                articleIntent.putExtra("accountName", article.getAccountName());
                articleIntent.putExtra("favourCount",article.getFavourCount());
                articleIntent.putExtra("commentCount",article.getCommentCount());
                articleIntent.putExtra("hasFavoured",article.getFavoured());
                articleIntent.putExtra("contentFileId",article.getContentFileId());
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
                            "articleId="+article.getId(),
                            REQUEST_ADD_COLLECT).execute();
                    article.setFavourCount(collectCount + 1);
                } else {
                    //移除收藏
                    new CommonAysncTask(
                            handler,
                            ApplicationModel.getRemoveCollectAddress(),
                            "articleId="+article.getId(),
                            REQUEST_REMOVE_COLLECT).execute();
                    article.setFavourCount(collectCount - 1);
                }
                articleAdapter.notifyDataSetChanged();//更新显示
                break;
            //跳转到评论页面
            case R.id.llReview:
                Intent reviewIntent = new Intent(ArticleListActivity.this, ReviewActivity.class);
                reviewIntent.putExtra("title", "评论");
                reviewIntent.putExtra("articleId", article.getId());
                startActivity(reviewIntent);
                break;
            //跳转到用户页面
            case R.id.portrait:
                Intent userIntent = new Intent(ArticleListActivity.this, UserActivity.class);
                userIntent.putExtra("title", article.getAccountName());
                userIntent.putExtra("accountId", article.getAccountId());
                userIntent.putExtra("headFileId", article.getAccountHeadFileId());
                userIntent.putExtra("accountName", article.getAccountName());
                startActivity(userIntent);
                break;
            //跳转到标签文章页面
            case R.id.tag:
                Intent tagIntent = new Intent(ArticleListActivity.this, ArticleListActivity.class);
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
