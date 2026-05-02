package com.RSSapplication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RSSParser {

    public List<RSSItem> parseRSS(String rssUrl) {
        List<RSSItem> items = new ArrayList<>();

        try {
            URL url = new URL(rssUrl);
            InputStream inputStream = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, null);

            String title = "";
            String description = "";
            String link = "";
            String pubDate = "";
            String imageUrl = "";

            int eventType = parser.getEventType();
            boolean inItem = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            inItem = true;
                        } else if (inItem) {
                            if (tagName.equalsIgnoreCase("title")) {
                                title = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("description")) {
                                description = parser.nextText();
                                // Extract image URL from description
                                imageUrl = extractImageUrl(description);
                            } else if (tagName.equalsIgnoreCase("link")) {
                                link = parser.nextText();
                            } else if (tagName.equalsIgnoreCase("pubDate")) {
                                pubDate = parser.nextText();
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                            items.add(new RSSItem(title, description, link, pubDate, imageUrl));
                            inItem = false;
                        }
                        break;
                }
                eventType = parser.next();
            }
            inputStream.close();

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    private String extractImageUrl(String html) {
        Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
        Matcher m = p.matcher(html);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}