package org.alpha;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class stockData {
    protected static void print(HashMap stockdata){
        for(Object key: stockdata.keySet()){
            System.out.println(key +" = "+stockdata.get(key));
        }
    }
    public static HashMap extractStockData(WebDriver driver, String id){
        String key = null;
        LinkedHashMap<String, LinkedHashMap<String,String>> stockdata = new LinkedHashMap<>();
        ArrayList<String> header = new ArrayList<>();
        List<WebElement> th_list = driver.findElement(By.id(id)).findElement(By.cssSelector(".data-table")).findElements(By.tagName("th"));
        List<WebElement> tr_list = driver.findElement(By.id(id)).findElement(By.cssSelector(".data-table")).findElements(By.tagName("tr"));
        for(WebElement th_li: th_list){
            key = th_li.getText();
            header.add(key);
        }
        String nKey = null;
        String value;
        List<WebElement> td_list;
        LinkedHashMap<String,String> stockinfo;
        boolean skip_tr_flag = true;
        for(WebElement tr_li: tr_list){
            if(skip_tr_flag == false){
                int arr_ind = 0;
                td_list = tr_li.findElements(By.tagName("td"));
                stockinfo = new LinkedHashMap<>();
                for(WebElement td_li: td_list){
                    if(arr_ind == 0){
                        key = td_li.getText();
                        arr_ind++;
                    }else{
                        nKey = header.get(arr_ind++);
                        if(key.contains("Raw PDF")){
                            value = td_li.findElement(By.tagName("a")).getAttribute("href");
                        }else{
                            value = td_li.getText();
                        }
                        stockinfo.put(nKey,value);
                    }
                }
                stockdata.put(key, stockinfo);
            }else{
                skip_tr_flag = false;
            }

        }
        return stockdata;
    }

}
