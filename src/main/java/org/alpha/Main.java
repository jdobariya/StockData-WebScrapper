package org.alpha;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import org.junit.BeforeClass;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.junit.*;
import org.openqa.selenium.remote.*;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    private static ChromeDriverService service;
    private WebDriver driver;
    @BeforeClass
    public static void createAndStartService() throws IOException {
        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File("/Users/jaydeepdobariya/Desktop/Alpha/chromedriver"))
                .usingAnyFreePort()
                .build();
        service.start();

    }
    @AfterClass
    public static void stopService() {
        service.stop();
    }

    @Before
    public void createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized","--incognito"); //add headless for hiding browser
        driver = new RemoteWebDriver(service.getUrl(), options);
    }

    @After   public void quitDriver() {
        driver.quit();
    }

    @Test
    public void extractData() throws InterruptedException, SQLException {

        try {
            DatabaseConn db = new DatabaseConn();
            Connection conn = db.connect();
            int testVar = 0;
            int idIndex = 0;
            String[] sectionID = new String[]{"peers","quarters","profit-loss","balance-sheet","cash-flow","ratios","shareholding"}; //{"peers","quarters","profit-loss","balance-sheet","cash-flow","ratios","shareholding"};
            File file = new File("/Users/jaydeepdobariya/Desktop/Alpha/tags.txt");
            Scanner myReader = new Scanner(file);
            File scrappedTagFile = new File("/Users/jaydeepdobariya/Desktop/Alpha/ScrappedTagList.txt");
            FileWriter fileWriter = new FileWriter("/Users/jaydeepdobariya/Desktop/Alpha/ScrappedTagList.txt",true);
            if (scrappedTagFile.createNewFile()) {
                System.out.println("File created: " + scrappedTagFile.getName());
            } else {
                System.out.println("File already exists.");
            }
            while (myReader.hasNextLine()) {
                String tag = myReader.nextLine();
                driver.get("https://www.screener.in/company/" + tag + "/");
                for (String id : sectionID) {
                    List<WebElement> table_list = driver.findElement(By.id(id)).findElements(By.tagName("tbody"));
                    for (WebElement tb_li : table_list) {
                        List<WebElement> button_list = tb_li.findElements(By.tagName("button"));
                        for (WebElement btn_li : button_list) {
                            WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(btn_li));
                            btn_li.click();
                            Thread.sleep(2000);
                        }
                    }
                }

                String[]  tableNames = new String[]{"peers","quarterlyresult","profitlossdata","balancesheet","cashflowdata","ratios","shareholdings"}; //{"companyinfo","peers","quarterlyresult","profitlossdata","balancesheet","cashFlowData","ratios","shareholdings"}
                HashMap topratio = TopRatios.extractTopRatios(driver);
                db.push(conn, topratio, tag, 0, "companyinfo");
                for(String tablename: tableNames){
                    HashMap stockdata = stockData.extractStockData(driver,sectionID[idIndex++]);
                    db.push(conn, stockdata, tag, 1, tablename);
                }
                idIndex = 0;
                fileWriter.write(tag+"\n");
            }
            fileWriter.close();
            myReader.close();
        } catch (FileNotFoundException | TimeoutException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (ElementClickInterceptedException ece){
            System.out.println("A element is not clickable rror occurred.");
            ece.printStackTrace();
        } catch(WebDriverException wde){
            System.out.println("A WebDriverException error occurred.");
            wde.printStackTrace();
        } catch (IOException e) {
            System.out.println("A I/O error occurred.");
            e.printStackTrace();
        }

    }

}