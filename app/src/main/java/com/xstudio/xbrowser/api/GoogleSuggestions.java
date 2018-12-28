package com.xstudio.xbrowser.api;

import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public final class GoogleSuggestions {
    
    public static List<String> getSuggestionWords(String words, String lang) throws IOException, XmlPullParserException {
        final String rawUrl = "http://google.com/complete/search?output=toolbar&hl=" + lang + "&q=" + URLEncoder.encode(words);
        URL url = new URL(rawUrl);
        URLConnection conn = url.openConnection();
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(conn.getInputStream(), null);
        parser.nextTag();
        return parseSuggestionWords(parser);
    }
    
    public static List<String> parseSuggestionWords(XmlPullParser parser) throws IOException, XmlPullParserException {
        final List<String> results = new ArrayList<>();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG &&
                    parser.getName().equals("suggestion")) {
                final String value = parser.getAttributeValue(null, "data");
                results.add(value);
            }
            eventType = parser.next();
        }
        return results;
    }
}
