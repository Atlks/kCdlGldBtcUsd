package org.example;

import com.microsoft.playwright.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

import static org.example.BotBroswer.*;
import static org.example.OpenCoinMarketCap.*;
import static org.example.Util.iniLogCfg;


public class Gld2 {
    public static Logger log;

    public static void main(String[] args) throws InterruptedException {
        String url = "https://www.cashbackforex.com/chart?s=XAU.USD";
        iniLogCfg();
        log = LoggerFactory.getLogger(Gld2.class);
        System.out.println(url);
        log.info(url);


        BrowserContext context = getBrowserContextFast();
        log.info("aft getBrowserContextFastOptmz");
        // Create a new page within this context
        Page page = context.newPage();


        log.info("aft new page");

        log.info("start   save pic...");
        mkdir("pics");
        String picpath
                = "pics/cmdt" + System.currentTimeMillis() + ".png";

        byte[] imageBytes = Gld2.getImgBytes(url, page);
        //
        Thread.startVirtualThread(() -> {
            save2file(imageBytes, picpath);
            //  page.close();
        });
        //  element1.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(gldpic)));

        //  scrshtByElmtHdlr(page, gldpic);

        System.out.println("Screenshot saved as chart-element.png");


        // Keep browser open for a while to see the page
        Thread.sleep(3000 * 1000); // 10 seconds


    }


    private static void clickx(String selector, Page page) {
        Locator element1 = page.locator(selector);
        //  System.out.println("element1.isVisible:"+element1.isVisible());
        //   element1.waitFor(new Locator.WaitForOptions().setTimeout(3 * 1000));
        if (element1 != null && element1.isVisible()) {
            element1.click();
        }
    }

    private static void letVisab(Page page) {
        page.evaluate("() => {" +
                "  const el = document.querySelector('table.chart-markup-table');" +
                "  if (el) {" +
                "    el.style.display = 'block';" +       // 取消 display:none
                "    el.style.visibility = 'visible';" +  // 取消 visibility:hidden
                "    el.style.opacity = '1';" +           // 取消透明
                "  }" +
                "}");

        page.evaluate("() => {" +
                "  const el = document.querySelector('table.chart-markup-table');" +
                "  if (el) el.scrollIntoView();" +
                "}");
    }

    /**
     * Playwright 的 locator.screenshot() 默认会等待元素：
     * <p>
     * 可见 (visible)
     * <p>
     * 所有字体加载完毕
     * <p>
     * 渲染完成
     * <p>
     * scrshtByElmtHdlr 会跳过
     *
     * @param page
     * @param gldpic
     */
    private static void scrshtByElmtHdlr(Page page, String gldpic) {
        ElementHandle element = page.querySelector("table.chart-markup-table");
        if (element != null) {
            element.screenshot(new ElementHandle.ScreenshotOptions().setPath(Paths.get(gldpic)));
        } else {
            System.out.println("元素未找到，跳过截图");
        }
    }

    private static void addWbsktMntr(Page page) {
        page.onWebSocket(ws -> {
            System.out.println("✅ WebSocket 连接成功: " + ws.url());
        });
        page.onRequestFailed(request -> {
            if (request.url().startsWith("wss://")) {
                System.out.println("❌ WebSocket 连接失败: " + request.url());
            }
        });
    }


    public static byte[] getImgBytes(String url, Page page) throws InterruptedException {

        // 注入JS，隐藏 navigator.webdriver = true
        page.addInitScript("Object.defineProperty(navigator, 'webdriver', { get: () => undefined });");

        //  addstyle(page);

        //  disabFont(page);
        addWbsktMntr(page);

        nvgt(url, page);
//            try {
//                page.navigate(url,
//                        new Page.NavigateOptions()
//                                .setTimeout(10000)
//                                .setWaitUntil(WaitUntilState.NETWORKIDLE)); // 页面空闲时认为加载完成..
//            } catch (Throwable e) {
//                System.out.println("------------cat e:" + e.getMessage());
//
//                e.printStackTrace();
//                System.out.println("-----------end catex");
//            }


        // Optional: print the title of the page
        System.out.println("Page title: " + page.title());
        // Wait for page content to load (adjust selector as needed)
        page.waitForSelector("h1");
        System.out.println("aftr h1 " + page.title());
        String ctnBtn = "div.backdrop-continue";
        page.waitForSelector(ctnBtn);
        Locator btn = page.locator(ctnBtn);
        // btn.waitFor();
        btn.click();
        //  page.waitForSelector("canvas");

        // 2. Wait for the element to appear
        // 等待目标 div 加载（最多 60 秒）
        // 等待表格元素出现在页面中

        //  Locator element1 = page.locator("table.chart-markup-table");
        // element1.waitFor(new Locator.WaitForOptions().setTimeout(12 * 1000));
        //   String targtElmt = "div#tv-chart-overlay";
        String targtElmt = "table.chart-markup-table";

        targtElmt = "iframe.tradingview_f44bc";
        //    Thread.sleep(300);
        // letVisab(page);

        //  targtElmt="div.chart-gui-wrapper";
        //  targtElmt="div.chart-container-border";
        String selector = "div#close";
        clickx(selector, page);
        page.evaluate("() => {" +
                "  document.querySelectorAll('iframe[src*=\"accounts.google.com\"]').forEach(el => el.style.display = 'none');" +
                "}");
        Thread.sleep(300);
        page.evaluate("() => {" +
                "  const iframe = document.getElementById('credential_picker_iframe');" +
                "  if (iframe) {" +
                "    iframe.remove();" +
                "  }" +
                "}");

        // element1.waitFor(new Locator.WaitForOptions().setTimeout(9 * 1000));
        //   Thread.sleep(3 * 1000); // 10 secon
        System.out.println("targe elmt showed");
        //  page.waitForFunction("() => document.querySelector('canvas') && document.querySelector('canvas').clientHeight > 0");


        // 3. Take screenshot of that element

        Locator chartBlock = page.locator("iframe[id^='tradingview_']");
        chartBlock.waitFor(); // 等待 iframe 出现
        Thread.sleep(3 * 1000);

        byte[] imageBytes = chartBlock.screenshot();
        return imageBytes;
    }
}