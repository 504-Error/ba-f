package com.error504.baf.controller;

import com.error504.baf.model.ReviewPerformInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URLEncoder;
import java.util.ArrayList;

@RequiredArgsConstructor
@Controller
public class ReviewSearchPerformController {
    public static ArrayList<ReviewPerformInfo> getPerformData(int genreNum, String keyword) {
        ArrayList<ReviewPerformInfo> performInfo = new ArrayList<>();

        try {
            StringBuilder urlstr = new StringBuilder();
            urlstr.append("https://www.kopis.or.kr/openApi/restful/pblprfr?service=dbb49cb5408245b78d5b03a8ca629ce7");
            urlstr.append("&stdate=20000101");
            urlstr.append("&eddate=20231231");
            urlstr.append("&cpage=1");
            urlstr.append("&rows=50");
            urlstr.append("&prfstate=02");
            String encodeData = URLEncoder.encode(keyword, "UTF-8");
            urlstr.append("&shprfnm=").append(encodeData);

            if (genreNum != 0){
                switch (genreNum) {
                    case 1:
                        urlstr.append("&shcate=" + "AAAA");
                        break;
                    case 2:
                        urlstr.append("&shcate=" + "AAAB");
                        break;
                    case 3:
                        urlstr.append("&shcate=" + "CCCA");
                        break;
                    case 4:
                        urlstr.append("&shcate=" + "CCCB");
                        break;
                    case 5:
                        urlstr.append("&shcate=" + "BBBA");
                        break;
                    case 6:
                        urlstr.append("&shcate=" + "CCCC");
                        break;
                    case 7:
                        urlstr.append("&shcate=" + "EEEA");
                        break;
                    default:
                }
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            Document doc = dbBuilder.parse(urlstr.toString());

            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("db");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    ReviewPerformInfo performInfoTemp = new ReviewPerformInfo();

                    Element element = (Element) nNode;

                    performInfoTemp.setId(getTagValue("mt20id", element));
                    performInfoTemp.setTitle(getTagValue("prfnm", element));
                    performInfoTemp.setStartDate(getTagValue("prfpdfrom", element));
                    performInfoTemp.setEndDate(getTagValue("prfpdto", element));
                    performInfoTemp.setFacility(getTagValue("fcltynm", element));
                    performInfoTemp.setPosterUrl(getTagValue("poster", element));
                    performInfoTemp.setGenre(getTagValue("genrenm", element));
                    performInfoTemp.setState(getTagValue("prfstate", element));
                    performInfoTemp.setOpenRun(getTagValue("openrun", element));

                    performInfo.add(performInfoTemp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return performInfo;
    }

    public static String getTagValue(String tag, Element element) {
        NodeList nList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = nList.item(0);

        if(nValue == null) {
            return null;
        }

        return nValue.getNodeValue();
    }
}
