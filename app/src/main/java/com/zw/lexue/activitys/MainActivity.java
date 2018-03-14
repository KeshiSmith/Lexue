package com.zw.lexue.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.sivin.Banner;
import com.zw.lexue.R;
import com.zw.lexue.adapter.ArticleListAdapter;
import com.zw.lexue.adapter.NevPagerAdapter;
import com.zw.lexue.adapter.TheBannerAdapter;
import com.zw.lexue.model.AccountDetailModel;
import com.zw.lexue.model.AccountModel;
import com.zw.lexue.model.ApplicationModel;
import com.zw.lexue.model.ArticleListModel;
import com.zw.lexue.model.ArticleModel;
import com.zw.lexue.model.BannerModel;
import com.zw.lexue.model.FileCodeModel;
import com.zw.lexue.utils.CommonAysncTask;
import com.zw.lexue.utils.DownloadAysncTask;
import com.zw.lexue.utils.GetAccountAysncTask;
import com.zw.lexue.utils.GetAccountDetailAysncTask;
import com.zw.lexue.utils.GetAccountIdAysncTask;
import com.zw.lexue.utils.GetArticleListAysncTask;
import com.zw.lexue.widgets.NoScrollViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, ArticleListAdapter.Callback{
    //自定义常量
    public static final int REFRESH_COMPLETE = 0X1000;//刷新结束常量
    public static final int REQUEST_SETTINGS = 0X1001;//设置页面返回值
    public static final int REQUEST_DOWNLOAD_ACCOUNT_PORTRAIT = 0X1002;//用户头像下载请求码
    public static final int REQUEST_GET_ACCOUNT = 0X1003;//获取基本信息请求码
    public static final int REQUEST_GET_ACCOUNT_DETAIL = 0X1004;//获取详细信息请求码
    public static final int REQUEST_DOWNLOAD_ARTICLE_PORTRAIT = 0X1005;//文章头像下载请求码
    public static final int REQUEST_GET_ARTICLE_LIST = 0X1006;//获取文章请求码
    public static final int REQUEST_GET_ACCOUNT_ID = 0X1007;//获取用户Id请求码
    public static final int REQUEST_GET_BANNER1_ID = 0x1008;
    public static final int REQUEST_GET_BANNER2_ID = 0x1009;
    public static final int REQUEST_GET_BANNER3_ID = 0x100a;
    public static final int REQUEST_DOWNLOAD_BANNER = 0x100b;
    public static final int REQUEST_ADD_COLLECT = 0x100c;//添加收藏请求码
    public static final int REQUEST_REMOVE_COLLECT = 0x100d;//取消收藏请求码
    public static final int REQUEST_ARTICLE = 0x100e;//文章页面返回码
    //其他标识符
    private boolean mIsExit = false;//双击返回键退出标志
    //主界面全局控件
    private List<View> views = new ArrayList<View>();
    private NoScrollViewPager viewPager;
    private LinearLayout linearHome, linearSearch, linearUser, linearCurrent;
    private ImageView imageHome, imageSearch, imageUser, imageCurrent;
    private TextView textHome, textSearch, textUser, textCurrent;
    //主界面控件
    private int nextPage = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView articleListView;
    private ArticleListAdapter articleAdapter;
    private Map<Integer,ArticleModel> articlesMap = new HashMap<>();
    private List<ArticleModel> articles = new ArrayList<ArticleModel>();
    //搜索界面控件
    private LinearLayout llTopic, llLecture, llCourse, llColumn;
    private List<BannerModel> banners = new ArrayList();
    private Banner mBanner;
    //用户界面控件
    private LinearLayout llUserDetails, llSettings, llFavorite, llHistory, llTag;
    private RoundedImageView portrait;
    private TextView userTitle, userName, userWords;
    //通信句柄
    private Handler handler = new Handler() {

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
                    showMessage("更新成功");
                    break;
                //获取账户基本信息
                case REQUEST_GET_ACCOUNT:
                    if(msg.obj!=null){
                        ApplicationModel.setAccount((AccountModel) msg.obj);
                        ApplicationModel.saveApplicationData(MainActivity.this);
                        updateUserViewOfActivity();//更新应用界面
                    }
                    break;
                //获取详细信息
                case REQUEST_GET_ACCOUNT_DETAIL:
                    if(msg.obj!=null){
                        ApplicationModel.setAccountDetail((AccountDetailModel) msg.obj);
                        ApplicationModel.saveApplicationData(MainActivity.this);
                        updatePortrait();//更新头像控件
                        updateUserViewOfActivity();//更新应用界面
                    }
                    break;
                //用户头像下载请求
                case REQUEST_DOWNLOAD_ACCOUNT_PORTRAIT:
                    if(msg.obj == null){
                        //更新加载头像
                        updatePortrait();
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
                //文章头像下载请求
                case REQUEST_DOWNLOAD_ARTICLE_PORTRAIT:
                    if(msg.obj == null){
                        //更新加载文章头像
                        articleAdapter.notifyDataSetChanged();
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
                //获取文章列表请求
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
                //获取用户Id请求
                case REQUEST_GET_ACCOUNT_ID:
                    if(msg.obj != null){
                        switch ((String)msg.obj){
                            case "failed":
                                showMessage("服务器连接失败，请稍后再试");
                                break;
                            case "error":
                                showMessage("连接失败，请检查网络设置");
                                break;
                            //保存AccountId
                            default:
                                ApplicationModel.setAccountId(Integer.parseInt((String)msg.obj));
                                //获取基本用户信息
                                GetAccountAysncTask getAccountAysncTask = new GetAccountAysncTask(
                                        handler,
                                        ApplicationModel.getAccountAddress(),
                                        ApplicationModel.getAccountId(),
                                        REQUEST_GET_ACCOUNT);
                                getAccountAysncTask.execute();
                                //获取用户详细信息
                                GetAccountDetailAysncTask getAccountDetailAysncTask = new GetAccountDetailAysncTask(
                                        handler,
                                        ApplicationModel.getAccountDetailAddress(),
                                        ApplicationModel.getAccountId(),
                                        REQUEST_GET_ACCOUNT_DETAIL);
                                getAccountDetailAysncTask.execute();
                        }
                    }
                    break;
                case REQUEST_GET_BANNER1_ID:
                    if(msg.obj != null){
                        switch ((String)msg.obj) {
                            case "failed":
                                showMessage("服务器连接失败，请稍后再试");
                                break;
                            case "error":
                                showMessage("连接失败，请检查网络设置");
                                break;
                            default:
                                //保存Banner1
                                ApplicationModel.setBanner1((String)msg.obj);
                                ApplicationModel.saveApplicationData(MainActivity.this);
                                //更新显示
                                banners.get(0).setBannerFileId((String)msg.obj);
                                mBanner.notifyDataHasChanged();
                        }
                    }
                    break;
                case REQUEST_GET_BANNER2_ID:
                    if(msg.obj != null){
                        switch ((String)msg.obj) {
                            case "failed":
                                showMessage("服务器连接失败，请稍后再试");
                                break;
                            case "error":
                                showMessage("连接失败，请检查网络设置");
                                break;
                            default:
                                //保存Banner2
                                ApplicationModel.setBanner2((String)msg.obj);
                                ApplicationModel.saveApplicationData(MainActivity.this);
                                //更新显示
                                banners.get(1).setBannerFileId((String)msg.obj);
                                mBanner.notifyDataHasChanged();
                        }
                    }
                    break;
                case REQUEST_GET_BANNER3_ID:
                    if(msg.obj != null){
                        switch ((String)msg.obj) {
                            case "failed":
                                showMessage("服务器连接失败，请稍后再试");
                                break;
                            case "error":
                                showMessage("连接失败，请检查网络设置");
                                break;
                            default:
                                //保存Banner3
                                ApplicationModel.setBanner3((String)msg.obj);
                                ApplicationModel.saveApplicationData(MainActivity.this);
                                //更新显示
                                banners.get(2).setBannerFileId((String)msg.obj);
                                mBanner.notifyDataHasChanged();
                        }
                    }
                    break;
                case REQUEST_DOWNLOAD_BANNER:
                    if(msg.obj == null){
                        //更新加载文章头像
                        mBanner.notifyDataHasChanged();
                    }
                    else switch (((FileCodeModel)msg.obj).getCode()){
                        case 0:
                            showMessage("服务器连接失败，请稍后再试");
                            break;
                        case -1:
                            showMessage("连接失败，请检查网络设置");
                            break;
                        case 401:
                            showMessage("横幅控件加载错误");
                            break;
                        case 402:
                            showMessage("横幅文件不存在");
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
        setContentView(R.layout.activity_main);

        initHttpData();//获取网络数据

        initView();//初始化布局
        initDate();//初始化数据
        initHome();//初始化主页面
        initSearch();//初始化搜索界面
        initUser();//初始化应用界面

        updatePortrait();//更新头像控件
        updateUserViewOfActivity();//更新应用界面
    }

    /**
     * 获取网络数据
     */
    private void initHttpData(){
        //获取文章列表
        GetArticleListAysncTask getArticleListAysncTask = new GetArticleListAysncTask(
                handler,
                ApplicationModel.getArticleListAddress(),
                "page="+nextPage,
                REQUEST_GET_ARTICLE_LIST);
        getArticleListAysncTask.execute();
        //获取Banner
        CommonAysncTask bannner1AysncTask = new CommonAysncTask(
                handler,
                ApplicationModel.getFileIdAddress(),
                "type=Banner1",
                REQUEST_GET_BANNER1_ID);
        bannner1AysncTask.execute();
        CommonAysncTask bannner2AysncTask = new CommonAysncTask(
                handler,
                ApplicationModel.getFileIdAddress(),
                "type=Banner2",
                REQUEST_GET_BANNER2_ID);
        bannner2AysncTask.execute();
        CommonAysncTask bannner3AysncTask = new CommonAysncTask(
                handler,
                ApplicationModel.getFileIdAddress(),
                "type=Banner3",
                REQUEST_GET_BANNER3_ID);
        bannner3AysncTask.execute();
        //判断是否已获取用户Id
        if(ApplicationModel.getAccountId() == 0){
            GetAccountIdAysncTask getAccountIdAysncTask = new GetAccountIdAysncTask(
                    handler,
                    ApplicationModel.getAccountIdAddress(),
                    REQUEST_GET_ACCOUNT_ID );
            getAccountIdAysncTask.execute();
        }
        else{
            //获取基本用户信息
            GetAccountAysncTask getAccountAysncTask = new GetAccountAysncTask(
                    handler,
                    ApplicationModel.getAccountAddress(),
                    ApplicationModel.getAccountId(),
                    REQUEST_GET_ACCOUNT);
            getAccountAysncTask.execute();
            //获取用户详细信息
            GetAccountDetailAysncTask getAccountDetailAysncTask = new GetAccountDetailAysncTask(
                    handler,
                    ApplicationModel.getAccountDetailAddress(),
                    ApplicationModel.getAccountId(),
                    REQUEST_GET_ACCOUNT_DETAIL);
            getAccountDetailAysncTask.execute();
        }
    }

    /**
     * 初始化当前布局，设置默认选中项
     */
    private void initView() {
        viewPager = (NoScrollViewPager) findViewById(R.id.viewPager);
        //设置无滑动效果
        viewPager.setNoScroll(true);
        //获取各控件的指针
        linearHome = (LinearLayout) findViewById(R.id.llHome);
        linearSearch = (LinearLayout) findViewById(R.id.llSearch);
        linearUser = (LinearLayout) findViewById(R.id.llUser);
        imageHome = (ImageView) findViewById(R.id.ivHome);
        imageSearch = (ImageView) findViewById(R.id.ivSearch);
        imageUser = (ImageView) findViewById(R.id.ivUser);
        textHome = (TextView) findViewById(R.id.tvHome);
        textSearch = (TextView) findViewById(R.id.tvSearch);
        textUser = (TextView) findViewById(R.id.tvUser);
        //对各布局设置监听
        linearHome.setOnClickListener(this);
        linearSearch.setOnClickListener(this);
        linearUser.setOnClickListener(this);
        //设置默认选中项
        linearCurrent = linearHome;
        imageHome.setSelected(true);
        imageCurrent = imageHome;
        textHome.setSelected(true);
        textHome.setTextSize(12.5f);
        textCurrent = textHome;
        //设置ViewPager按键监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                changeTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化当前布局的数据，添加ViewPager的页面数据
     */
    private void initDate() {
        //添加页面布局
        LayoutInflater mInflater = LayoutInflater.from(this);
        View viewHome = mInflater.inflate(R.layout.view_home, null);
        View viewSearch = mInflater.inflate(R.layout.view_search, null);
        View viewUser = mInflater.inflate(R.layout.view_user, null);
        views.add(viewHome);
        views.add(viewSearch);
        views.add(viewUser);
        //设置适配器
        NevPagerAdapter adapter = new NevPagerAdapter(views);
        viewPager.setAdapter(adapter);
    }

    /**
     * 初始化主页面数据
     */
    private void initHome() {
        //获取主页面布局
        View viewHome = views.get(0);
        //设置SwipeRefreshLayout控件参数
        swipeRefreshLayout = (SwipeRefreshLayout) viewHome.findViewById(R.id.refreshLayout);
        articleListView = (ListView) viewHome.findViewById(R.id.listView);
        swipeRefreshLayout.setOnRefreshListener(this);
        //设置适配器
        articleAdapter = new ArticleListAdapter(
                viewHome.getContext(),
                handler,
                R.layout.item_articles,
                articles,
                this);
        articleListView.setAdapter(articleAdapter);
        //更新显示
        articleAdapter.notifyDataSetChanged();
    }

    /**
     * 初始化搜索页面数据
     */
    private void initSearch() {
        //获取搜索页面布局
        View viewSearch = views.get(1);
        //设置Banner控件的适配器
        mBanner = (Banner) viewSearch.findViewById(R.id.banner);
        TheBannerAdapter bannerAdapter = new TheBannerAdapter(handler, banners);
        mBanner.setBannerAdapter(bannerAdapter);
        //添加缓存数据
        banners.add(new BannerModel("乐学", ApplicationModel.getBanner1()));
        banners.add(new BannerModel("乐学", ApplicationModel.getBanner2()));
        banners.add(new BannerModel("乐学", ApplicationModel.getBanner3()));
        //更新显示
        mBanner.notifyDataHasChanged();
        //获取搜索页面控件
        llTopic = (LinearLayout) viewSearch.findViewById(R.id.llTopic);
        llLecture = (LinearLayout) viewSearch.findViewById(R.id.llLecture);
        llCourse = (LinearLayout) viewSearch.findViewById(R.id.llCourse);
        llColumn = (LinearLayout) viewSearch.findViewById(R.id.llColumn);
        //设置搜索页面控件监听器
        llTopic.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ArticleListActivity.class);
                intent.putExtra("type", ArticleListActivity.TYPE.LIST_ARTICLE_WITH_HOT);
                intent.putExtra("title", "热门文章");
                startActivity(intent);
            }
        });
        final Intent intent = new Intent(MainActivity.this, CommonActivity.class);
        llLecture.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ArticleListActivity.class);
                intent.putExtra("type", ArticleListActivity.TYPE.LIST_ARTICLE);
                intent.putExtra("label", "讲座活动");
                intent.putExtra("title", "讲座活动");
                startActivity(intent);
            }
        });
        llCourse.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("title", "课程查询");
                startActivity(intent);
            }
        });
        llColumn.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.putExtra("title", "专题专栏");
                startActivity(intent);
            }
        });
    }

    /**
     * 初始化用户页面数据
     */
    private void initUser() {
        //获取搜索页面布局
        View viewUser = views.get(2);
        //获取搜索页面控件
        llUserDetails = (LinearLayout) viewUser.findViewById(R.id.userDetails);
        llSettings = (LinearLayout) viewUser.findViewById(R.id.settings);
        llFavorite = (LinearLayout) viewUser.findViewById(R.id.llFavorite);
        llHistory = (LinearLayout) viewUser.findViewById(R.id.llHistory);
        llTag = (LinearLayout) viewUser.findViewById(R.id.llTag);
        portrait = (RoundedImageView) viewUser.findViewById(R.id.portrait);
        userTitle = (TextView) viewUser.findViewById(R.id.userTitle);
        userName = (TextView) viewUser.findViewById(R.id.userName);
        userWords = (TextView) viewUser.findViewById(R.id.userWords);
        //设置搜索页面控件数据
        llUserDetails.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
                startActivity(intent);
            }
        });
        llFavorite.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ArticleListActivity.class);
                intent.putExtra("type", ArticleListActivity.TYPE.LIST_ARTICLE_OF_COLLECT);
                intent.putExtra("title", "我的收藏");
                startActivity(intent);
            }
        });
        llHistory.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ArticleListActivity.class);
                intent.putExtra("type", ArticleListActivity.TYPE.LIST_ARTICLE_OF_HISTORY);
                intent.putExtra("title", "我的足迹");
                startActivity(intent);
            }
        });
        llTag.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TagsActivity.class));
            }
        });
        final Intent settingsActivityIntent = new Intent(MainActivity.this, SettingsActivity.class);
        llSettings.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(settingsActivityIntent, REQUEST_SETTINGS);
            }
        });
    }

    /**
     * 自定义监听器，设置viewPager当前项，将布局view设置为当前项
     *
     * @param view 产生点击，被监听到的布局view对象
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llHome:
                viewPager.setCurrentItem(0);
                break;
            case R.id.llSearch:
                viewPager.setCurrentItem(1);
                break;
            case R.id.llUser:
                viewPager.setCurrentItem(2);
                break;
            default:
                break;
        }
    }

    /**
     * 自定义监听器，完成主页面刷新工作
     */
    @Override
    public void onRefresh() {
        //句柄通信，完成刷新动作
        //handler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);

        //更新文章列表
        GetArticleListAysncTask getArticleListAysncTask = new GetArticleListAysncTask(
                handler,
                ApplicationModel.getArticleListAddress(),
                "page="+nextPage,
                REFRESH_COMPLETE);
        getArticleListAysncTask.execute();
    }

    /**
     * 此函数完成取消当前选中项，通过id设置新的选中项
     *
     * @param index 更改ViewPager的选中项position
     */
    private void changeTab(int index) {
        //取消当前选中
        imageCurrent.setSelected(false);
        textCurrent.setSelected(false);
        textCurrent.setTextSize(0.0f);
        //设置新的选中
        switch (index) {
            case 0:
                //设置当前选中项为Home
                linearCurrent = linearHome;
                imageCurrent = imageHome;
                textCurrent = textHome;
                break;
            case 1:
                //设置当前选中项为Search
                linearCurrent = linearSearch;
                imageCurrent = imageSearch;
                textCurrent = textSearch;
                break;
            case 2:
                //设置当前选中项为User
                linearCurrent = linearUser;
                imageCurrent = imageUser;
                textCurrent = textUser;
                break;
            default:
                break;
        }
        imageCurrent.setSelected(true);
        textCurrent.setTextSize(12.5f);
    }

    /**
     * 1. 如果当前导航布局不在首页，返回首页位置。
     * 2. 连续按下两次返回键退出程序。
     *
     * @param keyCode 按键码
     * @param event   按键类型
     * @return 布尔型
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //如果选中项不为首页，切换至首页
            if (linearCurrent != linearHome) {
                viewPager.setCurrentItem(0);
                return true;
            }
            //连续两次按下返回键后台运行
            if (mIsExit == true) {
                finish();//结束当前Activity
                System.exit(0);//结束当前应用程序
            } else {
                Toast.makeText(MainActivity.this, R.string.exit_confirm, Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 回调函数，响应处理Items的点击事件。
     * @param view
     */
    @Override
    public void click(View view) {
        Integer position = (Integer) view.getTag();
        ArticleModel article = articles.get(position);

        switch (view.getId()) {
            //跳转到文章页面
            case R.id.llArticle:
                Intent articleIntent = new Intent(MainActivity.this, ArticleActivity.class);
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
                Intent reviewIntent = new Intent(MainActivity.this, ReviewActivity.class);
                reviewIntent.putExtra("title", "评论");
                reviewIntent.putExtra("articleId", article.getId());
                startActivity(reviewIntent);
                break;
            //跳转到用户页面
            case R.id.portrait:
                Intent userIntent = new Intent(MainActivity.this, UserActivity.class);
                userIntent.putExtra("title", article.getAccountName());
                userIntent.putExtra("accountId", article.getAccountId());
                userIntent.putExtra("headFileId", article.getAccountHeadFileId());
                userIntent.putExtra("accountName", article.getAccountName());
                startActivity(userIntent);
                break;
            //跳转到标签文章页面
            case R.id.tag:
                Intent tagIntent = new Intent(MainActivity.this, ArticleListActivity.class);
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
        //如果设置页面返回exit为真，则退出登陆，返回至登陆页面
        if (requestCode == REQUEST_SETTINGS) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                Boolean exit = (Boolean) bundle.get("exit");
                if (exit) {
                    ApplicationModel.clearApplicationData(this);//清除用户数据
                    Intent loginActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginActivityIntent);//返回登陆界面
                    finish();
                }
            }
        }
        //文章页面返回码
        else if(requestCode == REQUEST_ARTICLE){
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

    /**
     * 更新应用页面
     */
    private void updateUserViewOfActivity(){
        //加载用户名
        String name = ApplicationModel.getName();
        if(name != null){
            userTitle.setText(ApplicationModel.getName());
            userName.setText(ApplicationModel.getName());
        }
        //加载个人简介
        String selfIntroduction = ApplicationModel.getSelfIntroduction();
        if(selfIntroduction!=null){
            userWords.setText(ApplicationModel.getSelfIntroduction());
        }
    }

    /**
     * 加载更新头像
     */
    private void updatePortrait(){
        //加载头像文件
        String headFileId = ApplicationModel.getHeadFileId();
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
                        REQUEST_DOWNLOAD_ACCOUNT_PORTRAIT);
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
