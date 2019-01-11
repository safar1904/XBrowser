package com.xstudio.xbrowser.text.style;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import com.xstudio.xbrowser.text.TextSpanner;
import android.net.Uri;

public class UrlSpanner implements TextSpanner {

    public static enum UrlType { HTTPS_RISK, ONLINE, OFFLINE }

    private final UrlType urlType;
    private final int httpsOkColor;
    private final int httpsRiskColor;
    private final int otherColor;

    public UrlSpanner(UrlType type) {
        urlType = type;
        httpsOkColor = Color.parseColor("#23AC14");
        httpsRiskColor = Color.parseColor("#DB1524");
        otherColor = Color.parseColor("#B5B5B5");
    }

    @Override
    public Spannable span(final String wholeText) {
        final Spannable result = new SpannableString(wholeText);
        final String https = "https";
        final String schemeDelim = "://";
        final Uri uri = Uri.parse(wholeText);

        if (uri.getHost() != null && uri.getHost().length() > 0 && uri.getPath() != null) {
            final int hostIndex = wholeText.indexOf(uri.getHost());
            final int start = hostIndex + uri.getHost().length();
            final int end = wholeText.length();
            result.setSpan(getOtherColorSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        boolean schemeHasSpanned = false;
        if (wholeText.startsWith(https + schemeDelim)) {
            if (urlType == UrlType.HTTPS_RISK) {
                result.setSpan(getHttpsRiskColorSpan(), 0, https.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                result.setSpan(new StrikethroughSpan(), 0, https.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                schemeHasSpanned = true;
            }
            if (urlType == UrlType.ONLINE) {
                result.setSpan(getHttpsOkColorSpan(), 0, https.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                schemeHasSpanned = true;
            }
            final int schemeIndex = wholeText.indexOf(schemeDelim, 0);
            if (schemeIndex > 0) {
                result.setSpan(getOtherColorSpan(), schemeIndex, schemeIndex + schemeDelim.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        if (!schemeHasSpanned && ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))) {
            result.setSpan(getOtherColorSpan(), 0, (uri.getScheme() + schemeDelim).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return result;
    }

    private ForegroundColorSpan getHttpsOkColorSpan() {
        return new ForegroundColorSpan(httpsOkColor);
    }

    private ForegroundColorSpan getOtherColorSpan() {
        return new ForegroundColorSpan(otherColor);
    }

    private ForegroundColorSpan getHttpsRiskColorSpan() {
        return new ForegroundColorSpan(httpsRiskColor);
    }

}
