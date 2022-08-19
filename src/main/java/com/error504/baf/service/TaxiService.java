package com.error504.baf.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


@Service
public class TaxiService {

    // sms 연결하는 QR코드 생성 (API)
    public byte[] getSMSQR(String msg) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("https://api.scanova.io/v2/qrcode/SMS"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("apikey", "UTF-8") + "=" + "hqaaicyzmwmvfatxgjlgtmimsvrzgfpenpknlagf"); /*API Key*/
        urlBuilder.append("&" + URLEncoder.encode("phone_no", "UTF-8") + "=" + URLEncoder.encode("01043354231", "UTF-8")); /*수신번호*/
        urlBuilder.append("&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(msg, "UTF-8")); /*메시지 내용*/
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-type", "image/png");

        int response = conn.getResponseCode();
        if( response == HttpURLConnection.HTTP_OK ){
            InputStream is = conn.getInputStream();
            System.out.println("test! : " + is);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            is.transferTo(byteArrayOutputStream);
            byte[] bytesData = byteArrayOutputStream.toByteArray();
            System.out.println(new String(bytesData));
            is.close();

            return bytesData;
        } else {
            System.out.println(conn.getErrorStream());
            return null;
        }

    }

}
