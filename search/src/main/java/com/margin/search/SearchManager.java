package com.margin.search;

import android.app.Application;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Margin on 2018/1/18.
 * 查找连续相同字符的管理类
 */

public class SearchManager {
    private static Pattern sPattern = Pattern.compile("([\\u4e00-\\u9fa5|\\w|\\s])\\1*");

    public static Spanned search(Application application, String fileName) {
        try (BufferedReader reader = getInput(application.getApplicationContext(), fileName)) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                Matcher matcher = sPattern.matcher(line);
                while (matcher.find()) {
                    String s = matcher.group();
                    if (!TextUtils.isEmpty(s.trim()) && s.length() > 1) {
                        sb.append("<font color='#F50057'>");
                        sb.append(s);
                        sb.append("</font>");
                    } else {
                        sb.append(s);
                    }
                }
            }
            String s = sb.toString();
            return Html.fromHtml(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Html.fromHtml("");
    }

    public static String replace(Application application, String fileName) {
        try (BufferedReader reader = getInput(application.getApplicationContext(), fileName)) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                Matcher matcher = sPattern.matcher(line);
                while (matcher.find()) {
                    String s = matcher.group();
                    if (s.length() > 1) {
                        sb.append(s.substring(0, 1));
                    } else {
                        sb.append(s);
                    }
                }
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static BufferedReader getInput(Context context, String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
