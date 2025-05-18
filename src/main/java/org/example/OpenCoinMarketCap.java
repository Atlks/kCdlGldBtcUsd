package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED;
import static org.example.BotBroswer.getBrowser4disabGpu;
import static org.example.BotBroswer.nvgt;
import static org.example.Util.iniLogCfg;


public class OpenCoinMarketCap {

    static {
        iniLogCfg();
    }
    public static   Logger log = LoggerFactory.getLogger(OpenCoinMarketCap.class);
    public static Browser browser4crp = getBrowser4disabGpu();

    public static void main(String[] args) throws IOException, InterruptedException {
        iniLogCfg();
        log = LoggerFactory.getLogger(OpenCoinMarketCap.class);
        String currencies = "bitcoin";
        byte[] imageBytes = null;
        try {
            imageBytes = getBytesPicFrmCrptsite(browser4crp, currencies);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println(imageBytes);
        // Keep browser open for a while to see the page
        Thread.sleep(300 * 1000); // 10 seconds
    }

    static byte[] getBytesPicFrmCrptsite(Browser browser, String currencies) throws InterruptedException, IOException {
        log.info(" fun getBytesPicFrmCrptsite");


        BrowserContext context = getBrowserContextFastOptmz(browser);
        Page page = context.newPage();


        log.info("aft newpage()");
        // https://coinmarketcap.com/currencies/bitcoin/            // Open the CoinMarketCap website
        String url = "https://coinmarketcap.com/currencies/" + currencies + "/";
        return getBytesFrmPage4crpt(url, page);

    }

    static byte[] getBytesFrmPage4crpt(String url, Page page) throws InterruptedException {
        iniLogCfg();
        log = LoggerFactory.getLogger(OpenCoinMarketCap.class);
        nvgt(url, page);

        // Wait for the main element to load
//            page.waitForSelector("div[class*='cmc-homepage']");
        log.info("Page title: " + page.title());
        // Optional: print the title of the page
        System.out.println("Page title: " + page.title());
        // Wait for page content to load (adjust selector as needed)
        page.waitForSelector("h1");
        page.waitForLoadState(DOMCONTENTLOADED);
        // page.waitForLoadState(NETWORKIDLE)	等待网络空闲（XHR 停止），适合复杂异步页面
        // Example: Click the second image in a list (adjust if needed)
        // This uses a general querySelector format. Adjust as necessary based on the site's structure.

        // Click the tab with data-index="tab-Candle"
        // Wait for the tab to be available and click it
        // <li data-role="Tab" data-index="tab-Candle"
        page.waitForSelector("li");
        String selector = "li[data-role='Tab'][data-index='tab-Candle']";
        waitForSelectorX(selector, page);

        Locator elmts = page.locator(selector);
        Locator visibleTab= getVsblElmtFrst(elmts);

        visibleTab.click();
        log.info("aft click tab");

        //     page.getByRole('list').filter({ hasText: /^$/ }).getByRole('img').nth(1).click();
        // });

        // Thread.sleep(5 * 1000);
        // 等待包含图表的区块加载
        Locator chartBlock = page.locator("div[data-sxn='chart-content-block']");
        chartBlock.waitFor();
        log.info("aft chartBlock  show");
        String rect = "rect";
       // page.waitForSelector(rect);
        waitforInDomAttched(rect,page);

      //  page.waitForSelector("g");
        waitforInDomAttched("g",page);

        mkdir("pics");
        // 截图这个div元素
        String picpath = "pics/crpt" + System.currentTimeMillis() + ".png";

        // 等待图表区域出现在页面上，并完成渲染
        chartBlock.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000)); // 最多等10秒


        log.info("aft chartBlock  visibale ok");
        Thread.sleep(3000);
        byte[] imageBytes = chartBlock.screenshot();
        //
        Thread.startVirtualThread(() -> {
            save2file(imageBytes,picpath);
          //  page.close();
                });
        //   byte[] imageBytes = Files.readAllBytes(Path.of(picpath));

        return imageBytes;
    }

    private static void waitforInDomAttched(String rect, Page page) {
        try{
            Locator  locator= page.locator(rect);
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED));
        } catch (Exception e) {
            System.out.println("---cat e--if mlt elmts");
            System.out.println(e.getMessage());
            System.out.println("---endcat e");
        }

    }


    @NotNull
    static BrowserContext getBrowserContextFastOptmz(Browser browser) {
        //use playwright 1.48
        // 获取内置的 iPhone 12 设备配置

        Browser.NewContextOptions options = getNewContextOptions4mobileSite();

        BrowserContext context = browser.newContext(options);

        //disable pic font media
        return getBrowserContextDisableImgMedia(context);
    }


    /**
     * fotn laod soluthong  ，disale font is ok
     * @param context
     * @return
     */
    @NotNull
    private static BrowserContext getBrowserContextDisableImgMedia(BrowserContext context) {
        context.route("**/*", route -> {
            String resourceType = route.request().resourceType();
            if (resourceType.equals("image") ||  resourceType.equals("font") || resourceType.equals("media")) {
                route.abort();
            } else {
                route.resume();
            }
        });
        return context;
    }

    static Browser.NewContextOptions getNewContextOptions4mobileSite() {
        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setViewportSize(390, 844)  // iPhone 12 分辨率
                .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X) " +
                        "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0 Mobile/15E148 Safari/604.1")
                .setDeviceScaleFactor(1.0)
                .setIsMobile(true)
                .setHasTouch(false) .setIgnoreHTTPSErrors(true)
                .setBypassCSP(true)   //disable scury
                ;
        return options;
    }

    private static void waitForSelectorX(String selector, Page page) {
        try{
            log.info("start wait for " + selector);
            //maybe multi slctr,,so try btr
          //  page.waitForSelector(selector);

            // so fat ,,,bier deflt wait maybe very slow
            page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED));

        } catch (Exception e) {
          //  System.out.println(e.getMessage());
        }
        log.info("end start wait for " + selector);
    }

    public static void save2file(byte[] imageBytes, String picpath) {try {
        // 确保目标目录存在
        Files.createDirectories(Paths.get(picpath).getParent());

        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(picpath)) {
            fos.write(imageBytes);
            fos.flush();
        }

        System.out.println("图片已保存到: " + picpath);
    } catch (IOException e) {
        System.err.println("保存图片失败: " + e.getMessage());
        e.printStackTrace();
    }
    }

    private static Locator getVsblElmtFrst(Locator tabs) {
        System.out.println("fun getVsblElmtFrst");
        int count = tabs.count();
        System.out.println("Found " + count + " tab(s)");

        for (int i = 0; i < count; i++) {
            Locator tab = tabs.nth(i);
            if (tab.isVisible()) {
                System.out.println("Clicking tab #" + i);
               // tab.click();
                return tab;
            }
        }
        throw  new RuntimeException("Tab not found");

    }

    private static void save2file(Locator chartBlock, String picpath) {
        chartBlock.screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get(picpath)));

        System.out.println("截图完成，已保存为 chart-content.png");
    }



    static void mkdir(String picsPath) {
        Path path = Paths.get(picsPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("📁 Created directory: " + picsPath);
            } catch (IOException e) {
                throw new RuntimeException("❌ Failed to create directory: " + picsPath, e);
            }
        }
    }


    static byte[] readPic(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("❌ File not found: " + filename);
        }
        return Files.readAllBytes(path);
    }

}
