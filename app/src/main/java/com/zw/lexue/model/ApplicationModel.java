package com.zw.lexue.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

/**
 * 本文档为应用数据类，用于存放此应用的相关静态数据。
 * Created by Keshi Smith on 2017/10/17.
 */

public class ApplicationModel {

    // 账户类型
    // 1:学生 2:老师 3:管理员
    // 11:学生邮箱未验证 22:老师邮箱未验证
    public static final int TYPE_STUDENT = 1;
    public static final int TYPE_TEACHER = 2;
    public static final int TYPE_ADMINISTER = 3;
    public static final int TYPE_STUDENT_NOT_VERIFY = 11;
    public static final int TYPE_TEACHER_NOT_VERIFY = 22;

    //性别类型 1:男 2:女
    public static final int GENDER_NONE = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    //其他公开静态常量
    public static final int PORTRAIT_SIZE = 256;

    //静态数据常量
    private static final String serverAddress = "http://202.197.191.253/lexue";     //服务器地址
    private static final String fileServerAddress =
            "http://202.197.191.253/filesystem";                                    //服务器下载地址
    private static final String loginAddress = "/account/doSignUp";                 //登陆地址
    private static final String registerAddress = "/account/doSignIn";              //注册地址
    private static final String accountIdAddress = "/account/getSessionAccountId";  //用户Id地址
    private static final String articleListAddress = "/article/listArticle";        //文章列表地址
    private static final String articleListWithHotAddress
            = "/article/listArticleWithHot";                                        //最热文章列表地址
    private static final String articleListWithReleaseTimeAddress
            = "/article/listArticleWithReleaseTime";                                //最新文章列表地址
    private static final String articleListWithConcernAddress
            = "/article/listArticleWithConcern";                                    //关注文章列表地址
    private static final String articleListOfOwnerAddress
            = "/article/listArticleOfOwner";                                        //作者文章列表地址
    private static final String listCollectArticleAddress
            = "/article/listCollectArticle";                                        //获取收藏文章列表
    private static final String listHistoryArticleAddress
            = "/article/listHistoryArticle";                                        //获取浏览文章列表
    private static final String addCollectAddress = "/article/addFavour";           //添加收藏地址
    private static final String removeCollectAddress ="/article/removeFavour";      //移除收藏地址
    private static final String reviewListAddress
            = "/article/listCommentWithArticleId";                                  //评论列表地址
    private static final String addCommentAddress = "/article/addComment";          //添加评论地址
    private static final String fileIdAddress = "/setting/getFileIdByType";         //通用资源地址
    private static final String accountAddress = "/account/getAccountById";         //账户基本信息地址
    private static final String accountDetailAddress
            = "/account/getAccountDetailByAccount";                                 //账户详细信息地址
    private static final String schoolListAddress = "/schoolMajor/schoolList";      //学校信息地址
    private static final String schoolMajorAddress = "/schoolMajor/majorList";      //学校下专业地址
    private static final String addSchoolAddress = "/schoolMajor/addSchool";        //添加学校地址
    private static final String addMajorAddress = "schoolMajor/addMajor";           //添加专业地址
    private static final String addInfoAddress = "/account/doAddInfoDetail";        //添加详情地址
    private static final String tagListAddress = "/article/labelList";              //所有标签地址
    private static final String tagConcernedAddress = "/concern/listConcernedLabel";//所有关注标签地址
    private static final String addConcernAddress = "/concern/addConcern";          //添加关注地址
    private static final String removeConcernAddress = "/concern/removeConcern";    //移除关注地址
    private static final String updatePawAddress = "/account/updateAccountPaw";     //修改密码地址
    private static final String fileUploadAddress = "/fileUpload.action";           //文件上传地址
    private static final String fileDownloadAddress = "/fileDownload.action";       //文件下载地址

    //存储目录路径
    private static final String rootPath = "/lexue";                    //应用根目录
    private static final String cachePath = "/cache";                   //缓存目录
    private static final String cacheImagePath = "/cache/image";        //缓存图像目录
    private static final String cacheArticlePath = "/cache/article";    //缓存文章目录
    private static final String tempPath = "/tmp";                      //临时文件夹目录

    //应用数据
    private static String setup = null;
    private static String banner1 = null;
    private static String banner2 = null;
    private static String banner3 = null;

    //用户数据
    private static Integer accountId = 0;
    private static String name = null;
    private static String count = null;
    private static String paw = null;
    private static Integer type = null;
    private static String headFileId = null;
    private static Integer gender = null;
    private static String selfIntroduction = null;
    private static String phoneNumber = null;
    private static String school = null;
    private static String major = null;
    private static String grade = null;
    private static Integer schoolId = null;
    private static Integer majorId = null;

    private static Boolean message = null;
    private static Boolean setupOn = null;

    public static String getSetup() {
        return setup;
    }

    public static void setSetup(String setup) {
        ApplicationModel.setup = setup;
    }

    public static String getBanner1() {
        return banner1;
    }

    public static void setBanner1(String banner1) {
        ApplicationModel.banner1 = banner1;
    }

    public static String getBanner2() {
        return banner2;
    }

    public static void setBanner2(String banner2) {
        ApplicationModel.banner2 = banner2;
    }

    public static String getBanner3() {
        return banner3;
    }

    public static void setBanner3(String banner3) {
        ApplicationModel.banner3 = banner3;
    }

    public static void setAccountId(Integer accountId) {
        ApplicationModel.accountId = accountId;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        ApplicationModel.name = name;
    }

    public static String getCount() {
        return count;
    }

    public static void setCount(String count) {
        ApplicationModel.count = count;
    }

    public static String getPaw() {
        return paw;
    }

    public static void setPaw(String paw) {
        ApplicationModel.paw = paw;
    }

    public static Integer getType() {
        return type;
    }

    public static void setType(Integer type) {
        ApplicationModel.type = type;
    }

    public static String getHeadFileId() {
        return headFileId;
    }

    public static void setHeadFileId(String headFileId) {
        ApplicationModel.headFileId = headFileId;
    }

    public static Integer getGender() {
        return gender;
    }

    public static void setGender(Integer gender) {
        ApplicationModel.gender = gender;
    }

    public static String getSelfIntroduction() {
        return selfIntroduction;
    }

    public static void setSelfIntroduction(String selfIntroduction) {
        ApplicationModel.selfIntroduction = selfIntroduction;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    public static void setPhoneNumber(String phoneNumber) {
        ApplicationModel.phoneNumber = phoneNumber;
    }

    public static String getSchool() {
        return school;
    }

    public static void setSchool(String school) {
        ApplicationModel.school = school;
    }

    public static String getMajor() {
        return major;
    }

    public static void setMajor(String major) {
        ApplicationModel.major = major;
    }

    public static String getGrade() {
        return grade;
    }

    public static void setGrade(String grade) {
        ApplicationModel.grade = grade;
    }

    public static Integer getSchoolId() {
        return schoolId;
    }

    public static void setSchoolId(Integer schoolId) {
        ApplicationModel.schoolId = schoolId;
    }

    public static Integer getMajorId() {
        return majorId;
    }

    public static void setMajorId(Integer majorId) {
        ApplicationModel.majorId = majorId;
    }

    public static Boolean getMessage() {
        return message;
    }

    public static void setMessage(Boolean message) {
        ApplicationModel.message = message;
    }

    public static Boolean getSetupOn() {
        return setupOn;
    }

    public static void setSetupOn(Boolean setupOn) {
        ApplicationModel.setupOn = setupOn;
    }

    //获取登陆地址
    public static String getLoginAddress()
    {
        return serverAddress + loginAddress;
    }

    //获取注册地址
    public static String getRegisterAddress()
    {
        return serverAddress + registerAddress;
    }

    //获取账户Id地址
    public static String getAccountIdAddress() {
        return serverAddress + accountIdAddress;
    }

    //获取文章列表地址
    public static String getArticleListAddress(){
        return serverAddress + articleListAddress;
    }

    //获取最热文章列表地址
    public static String getArticleListWithHotAddress() {
        return serverAddress + articleListWithHotAddress;
    }

    //获取最新文章列表地址
    public static String getArticleListWithReleaseTimeAddress() {
        return serverAddress + articleListWithReleaseTimeAddress;
    }

    //获取关注文章列表地址
    public static String getArticleListWithConcernAddress() {
        return serverAddress + articleListWithConcernAddress;
    }

    //获取用户文章列表地址
    public static String getArticleListOfOwnerAddress() {
        return serverAddress + articleListOfOwnerAddress;
    }

    //获取收藏文章列表地址
    public static String getListCollectArticleAddress() {
        return serverAddress + listCollectArticleAddress;
    }

    //获取历史文章列表地址
    public static String getListHistoryArticleAddress() {
        return serverAddress + listHistoryArticleAddress;
    }

    //获取收藏文章地址
    public static String getAddCollectAddress() {
        return serverAddress + addCollectAddress;
    }

    //获取移除收藏文章地址
    public static String getRemoveCollectAddress() {
        return serverAddress + removeCollectAddress;
    }

    //获取评论列表地址
    public static String getReviewListAddress(){
        return serverAddress + reviewListAddress;
    }

    //获取添加评论地址
    public static String getAddCommentAddress() {
        return serverAddress + addCommentAddress;
    }

    //获取通用资源文件Id地址
    public static String getFileIdAddress(){
        return serverAddress + fileIdAddress;
    }

    //获取账户基本信息地址
    public static String getAccountAddress(){
        return serverAddress + accountAddress;
    }

    //获取账户详细信息地址
    public static String getAccountDetailAddress() {
        return serverAddress + accountDetailAddress;
    }

    //获取学校列表地址
    public static String getSchoolListAddress(){
        return serverAddress + schoolListAddress;
    }

    //获取学校下专业地址
    public static String getSchoolMajorAddress() {
        return serverAddress + schoolMajorAddress;
    }

    //获取添加学校地址
    public static String getAddSchoolAddress() {
        return serverAddress + addSchoolAddress;
    }

    //获取添加专业地址
    public static String getAddMajorAddress() {
        return serverAddress + addMajorAddress;
    }

    //获取添加用户信息地址
    public static String getAddInfoAddress() {
        return serverAddress + addInfoAddress;
    }

    //获取标签地址
    public static String getTagListAddress(){
        return serverAddress+ tagListAddress;
    }

    //获取关注标签地址
    public static String getTagConcernedAddress() {
        return serverAddress + tagConcernedAddress;
    }

    //添加关注标签地址
    public static String getAddConcernAddress() {
        return  serverAddress + addConcernAddress;
    }

    //移除关注标签地址
    public static String getRemoveConcernAddress() {
        return serverAddress + removeConcernAddress;
    }

    //获取修改密码地址
    public static String getUpdatePawAddress(){
        return serverAddress + updatePawAddress;
    }

    //获取文件上传地址
    public static String getFileUploadAddress(){
        return fileServerAddress + fileUploadAddress;
    }

    //获取文件下载地址
    public static String getFileDownloadAddress(){
        return fileServerAddress + fileDownloadAddress;
    }

    //获取程序根目录
    public static String getRootPath(){
        String path = Environment.getExternalStorageDirectory() + rootPath;
        createFolderIfNotExist(path);
        return path;
    }

    //获取程序缓存目录
    public static String getCachePath(){
        String path = Environment.getExternalStorageDirectory() + rootPath + cachePath;
        createFolderIfNotExist(path);
        return path;
    }

    //获取程序临时文件目录
    public static String getTempPath(){
        String path = Environment.getExternalStorageDirectory() + rootPath + tempPath;
        createFolderIfNotExist(path);
        return path;
    }

    //获取缓存图片文件目录
    public static String getCacheImagePath(){
        String path = Environment.getExternalStorageDirectory() + rootPath + cacheImagePath;
        createFolderIfNotExist(path);
        return path;
    }

    //获取缓存文章文件目录
    public static String getCacheArticlePath(){
        String path = Environment.getExternalStorageDirectory() + rootPath + cacheArticlePath;
        createFolderIfNotExist(path);
        return path;
    }

    //获取用户Id
    public static Integer getAccountId(){
        return accountId;
    }

    //设置用户基本信息全局变量
    public static void setAccount(AccountModel model){
        accountId = model.getId();
        name = model.getName();
        count = model.getCount();
        type = model.getType();
    }

    //设置用户详情信息全局变量
    public static void setAccountDetail(AccountDetailModel model){
        accountId = model.getAccountId();
        headFileId = model.getHeadFileId();
        gender = model.getGender();
        selfIntroduction = model.getSelfIntroduction();
        phoneNumber = model.getPhoneNumber();
        school = model.getSchool();
        major = model.getMajor();
        grade = model.getGrade();
        schoolId = model.getSchoolId();
        majorId = model.getMajorId();
    }

    //加载应用数据
    public static void loadApplicationData(Context context){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("application", Context.MODE_PRIVATE);
        //加载应用信息
        setup = sharedPreferences.getString("setup", null);
        banner1 = sharedPreferences.getString("banner1", null);
        banner2 = sharedPreferences.getString("banner2", null);
        banner3 = sharedPreferences.getString("banner3", null);
        //加载用户基本信息
        accountId = sharedPreferences.getInt("accountId", 0);
        name = sharedPreferences.getString("name", null);
        count = sharedPreferences.getString("count", null);
        paw = sharedPreferences.getString("paw", null);
        type = sharedPreferences.getInt("type", TYPE_STUDENT_NOT_VERIFY);
        //加载用户详细信息
        headFileId = sharedPreferences.getString("headFileId", null);
        gender = sharedPreferences.getInt("gender", GENDER_MALE);
        selfIntroduction = sharedPreferences.getString("selfIntroduction", null);
        phoneNumber = sharedPreferences.getString("phoneNumber", null);
        school = sharedPreferences.getString("school", null);
        major = sharedPreferences.getString("major", null);
        grade = sharedPreferences.getString("grade", null);
        schoolId =sharedPreferences.getInt("schoolId", 0);
        majorId =sharedPreferences.getInt("majorId", 0);
        //加载其他信息
        message = sharedPreferences.getBoolean("message", false);
        setupOn = sharedPreferences.getBoolean("setupOn", false);
    }

    //保存应用数据
    public static void saveApplicationData(Context context){
        SharedPreferences sharedPreferences =
                context.getSharedPreferences("application", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //保存应用信息
        editor.putString("setup", setup);
        editor.putString("banner1", banner1);
        editor.putString("banner2", banner2);
        editor.putString("banner3", banner3);
        //保存用户基本信息
        editor.putInt("accountId", accountId);
        editor.putString("name", name);
        editor.putString("count", count);
        editor.putString("paw", paw);
        editor.putInt("type", type);
        //保存用户详细信息
        editor.putString("headFileId", headFileId);
        editor.putInt("gender", gender);
        editor.putString("selfIntroduction", selfIntroduction);
        editor.putString("phoneNumber", phoneNumber);
        editor.putString("school", school);
        editor.putString("major", major);
        editor.putString("grade", grade);
        editor.putInt("schoolId", schoolId);
        editor.putInt("majorId", majorId);
        //保存其他信息
        editor.putBoolean("message", message);
        editor.putBoolean("setupOn", setupOn);
        editor.commit();
    }

    //清除应用数据
    public static void clearApplicationData(Context context){
        //清除用户基本信息
        accountId = 0;
        name = null;
        paw = null;
        type = TYPE_STUDENT_NOT_VERIFY;
        //清除用户详细信息
        headFileId = null;
        gender = GENDER_MALE;
        selfIntroduction = null;
        phoneNumber = null;
        school = null;
        major = null;
        grade = null;
        schoolId = 0;
        majorId = 0;
        //保存用户数据
        saveApplicationData(context);
    }


    //创建文件夹如果当前文件夹不存在
    private static void createFolderIfNotExist(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }
}
