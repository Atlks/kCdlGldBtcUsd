package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class OpenCoinMarketCap {
    private static   Logger log = LoggerFactory.getLogger(OpenCoinMarketCap.class);

    public static Browser browser4crp = getBrowser4crp();

    public static void main(String[] args) throws IOException, InterruptedException {
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
        Thread.sleep(3 * 1000); // 10 seconds
    }

    static byte[] getBytesPicFrmCrptsite(Browser browser, String currencies) throws InterruptedException, IOException {
        log.info(" fun getBytesPicFrmCrptsite");
        Page page = browser.newPage();

        log.info("aft newpage()");
        // https://coinmarketcap.com/currencies/bitcoin/            // Open the CoinMarketCap website

        page.navigate("https://coinmarketcap.com/currencies/" + currencies + "/");

        // Wait for the main element to load
//            page.waitForSelector("div[class*='cmc-homepage']");
        log.info("Page title: " + page.title());
        // Optional: print the title of the page
        System.out.println("Page title: " + page.title());
        // Wait for page content to load (adjust selector as needed)
        page.waitForSelector("h1");

        // Example: Click the second image in a list (adjust if needed)
        // This uses a general querySelector format. Adjust as necessary based on the site's structure.

        // Click the tab with data-index="tab-Candle"
        // Wait for the tab to be available and click it
        page.waitForSelector("li[data-role='Tab'][data-index='tab-Candle']");
        page.locator("li[data-role='Tab'][data-index='tab-Candle']").first().click();
        log.info("aft click tab");

        //     page.getByRole('list').filter({ hasText: /^$/ }).getByRole('img').nth(1).click();
        // });

       // Thread.sleep(5 * 1000);
        // 等待包含图表的区块加载
        Locator chartBlock = page.locator("div[data-sxn='chart-content-block']");
        chartBlock.waitFor();
        log.info("aft chartBlock  show");
        page.waitForSelector("rect");
        page.waitForSelector("g");

        mkdir("pics");
        // 截图这个div元素
        String picpath = "pics/crpt" + System.currentTimeMillis() + ".png";

        // 等待图表区域出现在页面上，并完成渲染
        chartBlock.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000)); // 最多等10秒


        log.info("aft chartBlock  visibale ok");
        byte[] imageBytes = chartBlock.screenshot();
        Thread.startVirtualThread(() -> {
                    save2file(chartBlock, picpath);
            page.close();
                });
        //   byte[] imageBytes = Files.readAllBytes(Path.of(picpath));

        return imageBytes;

    }

    private static void save2file(Locator chartBlock, String picpath) {
        chartBlock.screenshot(new Locator.ScreenshotOptions()
                .setPath(Paths.get(picpath)));

        System.out.println("截图完成，已保存为 chart-content.png");
    }

    private static Browser getBrowser4crp() {
          log = LoggerFactory.getLogger(OpenCoinMarketCap.class);
        System.out.println(log);
        log.info("fun getBrowser4crp");
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        log.info("endfun getBrowser4crp");
        return browser;
    }

    private static Browser getBrowser4gld() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        return browser;
    }

    private static void mkdir(String picsPath) {
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
