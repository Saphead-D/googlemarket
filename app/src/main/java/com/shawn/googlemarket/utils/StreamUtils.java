package com.shawn.googlemarket.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by shawn on 2016/4/6.
 */
public class StreamUtils {

    public static String parserStreamUtil(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringWriter sw  = new StringWriter();
        String str = null;
        while((str=br.readLine()) != null) {
            sw.write(str);
        }
        sw.close();
        br.close();
        return sw.toString();
    }
}
