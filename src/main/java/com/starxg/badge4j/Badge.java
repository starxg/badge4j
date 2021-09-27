package com.starxg.badge4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Badge
 *
 * @author huangxingguang
 */
public class Badge {

    private static final List<Float> CHAR_WIDTH_TABLE;
    private static final Map<String, String> COLORS;

    static {
        try {
            final String json = new String(Files.readAllBytes(Paths.get(Objects
                    .requireNonNull(Badge.class.getResource("/widths-verdana-110.json"), "widths-verdana-110.json")
                    .toURI())));
            final String[] codes = json.split(",");
            List<Float> list = new ArrayList<>(codes.length);
            for (int i = 0; i < codes.length; i++) {
                if (i == 0) {
                    list.add(Float.parseFloat(String.valueOf(codes[i].charAt(1))));
                } else if (i + 1 == codes.length) {
                    list.add(Float.parseFloat(String.valueOf(codes[i].charAt(0))));
                } else {
                    list.add(Float.parseFloat(codes[i]));
                }
            }

            CHAR_WIDTH_TABLE = Collections.unmodifiableList(list);
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        final Map<String, String> colors = new HashMap<>();
        colors.put("green", "#3C1");
        colors.put("blue", "#08C");
        colors.put("red", "#E43");
        colors.put("yellow", "#DB1");
        colors.put("orange", "#F73");
        colors.put("purple", "#94E");
        colors.put("pink", "#E5B");
        colors.put("grey", "#999");
        colors.put("gray", "#999");
        colors.put("cyan", "#1BC");
        colors.put("black", "#2A2A2A");
        COLORS = Collections.unmodifiableMap(colors);
    }

    /**
     * create a default badge
     *
     * @param label
     *            left text
     * @param status
     *            right text
     * @return svg
     */
    public static String create(String label, String status) {
        return create(label, status, null);
    }

    /**
     * create a default badge
     *
     * @param label
     *            left text
     * @param status
     *            right text
     * @param icon
     *            data:image/svg+xml;base64,xxx
     * @return svg
     */
    public static String create(String label, String status, String icon) {
        return create(label, status, "#595959", "#1283c3", icon);
    }

    /**
     * create a badge
     *
     * @param label
     *            left text
     * @param status
     *            right text
     * @param labelColor
     *            right text's color. default: #595959
     * @param statusColor
     *            left text's color. default: #1283c3
     * @param icon
     *            data:image/svg+xml;base64,xxx
     * @return svg
     */
    public static String create(String label, String status, String labelColor, String statusColor, String icon) {
        return create(label, status, labelColor, statusColor, icon, 13, 1);
    }

    /**
     * create a badge
     *
     * @param label
     *            left text
     * @param status
     *            right text
     * @param labelColor
     *            right text's color. default: #595959
     * @param statusColor
     *            left text's color. default: #1283c3
     * @param icon
     *            data:image/svg+xml;base64,xxx
     * @param iconWidth
     *            iconWidth
     * @param scale
     *            scale
     * @return svg
     */
    public static String create(String label, String status, String labelColor, String statusColor, String icon,
            int iconWidth, final int scale) {
        iconWidth = iconWidth * 10;

        final float iconSpanWidth = Objects.nonNull(icon) ? (!label.isEmpty() ? iconWidth + 30 : iconWidth - 18) : 0;

        final float sbTextWidth = calcWidth(label);
        final float stTextWidth = calcWidth(status);
        final float sbRectWidth = sbTextWidth + 100 + iconSpanWidth;
        final float stRectWidth = stTextWidth + 100;
        final float width = sbRectWidth + stRectWidth;
        final float sbTextStart = Objects.nonNull(icon) ? (iconSpanWidth + 50) : 50;
        final String xlink = Objects.nonNull(icon) ? " xmlns:xlink='http://www.w3.org/1999/xlink'" : "";

        label = sanitize(label);
        status = sanitize(status);

        final String title = String.format("%s: %s", label, status);

        labelColor = COLORS.getOrDefault(labelColor, labelColor);
        statusColor = COLORS.getOrDefault(statusColor, statusColor);

        StringBuilder sb = new StringBuilder();

        sb.append(String.format(
                "<svg width='%s' height='%s' viewBox='0 0 %s 200' xmlns='http://www.w3.org/2000/svg'%s role='img' aria-label='%s'>",
                scale * width / 10, scale * 20, width, xlink, title));

        sb.append(String.format("<title>%s</title>", title));

        sb.append("<linearGradient id='a' x2='0' y2='100%'>");
        sb.append("<stop offset='0' stop-opacity='.1' stop-color='#EEE'/>");
        sb.append("<stop offset='1' stop-opacity='.1'/>");
        sb.append("</linearGradient>");

        sb.append(String.format("<mask id='m'><rect width='%s' height='200' rx='30' fill='#FFF'/></mask>", width));

        sb.append("<g mask='url(#m)'>");
        sb.append(String.format("<rect fill='%s' width='%s' height='200'/>", labelColor, sbRectWidth));
        sb.append(String.format("<rect fill='%s' x='%s' width='%s' height='200'/>", statusColor, sbRectWidth,
                stRectWidth));
        sb.append(String.format("<rect width='%s' height='200' fill='url(#a)'/>", width));
        sb.append("</g>");

        sb.append(
                "<g aria-hidden='true' fill='#fff' text-anchor='start' font-family='Verdana,DejaVu Sans,sans-serif' font-size='110'>");
        sb.append(String.format("<text x='%s' y='148' textLength='%s' fill='#000' opacity='0.25'>%s</text>",
                sbTextStart + 10, sbTextWidth, label));
        sb.append(String.format("<text x='%s' y='138' textLength='%s'>%s</text>", sbTextStart, sbTextWidth, label));
        sb.append(String.format("<text x='%s' y='148' textLength='%s' fill='#000' opacity='0.25'>%s</text>",
                sbRectWidth + 55, stTextWidth, status));
        sb.append(
                String.format("<text x='%s' y='138' textLength='%s'>%s</text>", sbRectWidth + 45, stTextWidth, status));
        sb.append("</g>");

        if (Objects.nonNull(icon)) {
            sb.append(String.format("<image x='40' y='35' width='%s' height='130' xlink:href='%s'/>", iconWidth, icon));
        }

        sb.append("</svg>");

        return sb.toString();
    }

    static String sanitize(String str) {
        return str.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("'", "&quot;")
                .replaceAll("'", "&apos;");
    }

    static float calcWidth(String text) {
        final float fallbackWidth = CHAR_WIDTH_TABLE.get(64);
        float total = 0;

        int i = text.length();
        while (i-- > 0) {
            int idx = text.codePointAt(i);
            total += (idx >= CHAR_WIDTH_TABLE.size() ? fallbackWidth : CHAR_WIDTH_TABLE.get(idx));
        }
        return total;
    }

}
