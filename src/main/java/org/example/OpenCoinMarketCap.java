package org.example;

import com.microsoft.playwright.*;

import java.nio.file.Paths;

public class OpenCoinMarketCap {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();

            //
            // https://coinmarketcap.com/currencies/bitcoin/            // Open the CoinMarketCap website
            page.navigate("https://coinmarketcap.com/currencies/bitcoin/");

            // Wait for the main element to load
//            page.waitForSelector("div[class*='cmc-homepage']");

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

            //     page.getByRole('list').filter({ hasText: /^$/ }).getByRole('img').nth(1).click();
       // });

            Thread.sleep(5*1000);
            // 等待包含图表的区块加载
            Locator chartBlock = page.locator("div[data-sxn='chart-content-block']");
            chartBlock.waitFor();

            // 截图这个div元素
            chartBlock.screenshot(new Locator.ScreenshotOptions()
                    .setPath(Paths.get("chart-content.png"+System.currentTimeMillis()+".png")));

            System.out.println("截图完成，已保存为 chart-content.png");

            // Keep browser open for a while to see the page
            Thread.sleep(3*1000); // 10 seconds
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
