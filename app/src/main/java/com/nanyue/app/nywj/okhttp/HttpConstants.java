package com.nanyue.app.nywj.okhttp;

/**
 * @author: vision
 * @function:
 * @date: 16/8/12
 */
public class HttpConstants {

    public static String ROOT = "http://nouse.gzkuaiyi.com:9999";

    public static String ROOT_URL = "http://nouse.gzkuaiyi.com:9999/app";

    /**
     * 新闻,视频列表
     */
    public static String NEWS_LIST = ROOT_URL + "/list-";

    /**
     * 新闻详情
     */
    public static String NEWS_DETAIL = ROOT_URL + "/view-";

    /**
     * 登陆接口
     */
    public static String LOGIN = ROOT_URL + "/auth";

    /**
     * 首页新闻
     */
    public static String HOME_NEWS = ROOT_URL + "/recommend-article?appinMenu=1";

}
