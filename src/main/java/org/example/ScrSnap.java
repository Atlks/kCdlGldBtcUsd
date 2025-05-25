package org.example;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.example.implt.FrnExch;
import org.example.implt.Gld2;

import java.util.concurrent.TimeUnit;

import static org.example.BotBroswer.getBrowserContextFast;
// static org.example.implt.OpenCoinMarketCap.getBytesFrmPage4crpt;
// static org.example.Wbsvr.context;
import static org.example.Wbsvr.log;
import static org.example.implt.OpenCoinMarketCap.getBytesFrmPage4crpt;

public class ScrSnap {
    public   static LoadingCache<String, byte[]> cache;
    static void iniImgCache() {
        // 创建一个自动加载缓存
        String cacheName = "imgCache";
        cache = CacheBuilder.newBuilder()
                .maximumSize(999)  // 最多缓存100个条目
                .expireAfterWrite(600, TimeUnit.SECONDS)  // 缓存项在写入10秒后过期
                .removalListener(notification ->
                        System.out.println("移除缓存frm imgcache: " + notification.getKey() + " -> " + notification.getCause())
                )
                .build(new CacheLoader<String, byte[]>() {
                    @Override
                    public byte[] load(String url) throws Exception {
                        // 模拟从数据库或外部系统加载数据
                        log.info(cacheName + "加载数据start：" + url);

                        Page page = context.newPage();
                        byte[] imageBytes = getBytesFrmUrl(url, page);
                        log.info(cacheName + "加载数据finish： key=" + url);
                        return imageBytes;
                    }
                });
    }
    public static BrowserContext context = getBrowserContextFast();


    static byte[] getBytesFrmUrl(String url, Page page) throws InterruptedException {
        byte[] imageBytes;
        if (url.contains("coinmarketcap"))
            imageBytes = getBytesFrmPage4crpt(url, page);
        else if (url.contains("baidu.com"))
            imageBytes = FrnExch.getImgBytes(url, page);
        else if (url.contains("cashbackforex"))
            imageBytes = Gld2.getImgBytes(url, page);
        else
            imageBytes = getBytesFrmPage4crpt(url, page);
        return imageBytes;
    }
}
