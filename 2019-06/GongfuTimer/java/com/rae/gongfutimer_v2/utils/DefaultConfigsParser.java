package com.rae.gongfutimer_v2.utils;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This parses the xml containing the default timer configurations. It provides a List of the
 * TimerConfigs read from the file.
 */
public class DefaultConfigsParser
{
    /**
     * This is just passed into the parser's require() for the namespace parameter. No namespace is
     * specified.
     */
    private static final String ns = "";

    /**
     * This returns a list of TimerConfigs given a parser for an xml.
     * @param parser - The parser for the default timer configurations XML.
     * @return - The default timer configurations that were parsed from the file.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<TimerConfig> parse(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        parser.next();
        parser.next();
        parser.next();
        return readFeed(parser);
    }

    /**
     * This processes the xml, looking for elements with the specific tag of timer_config. It
     * recursively searches.
     * @param parser - The parser for the default timer configurations XML.
     * @return - A List of extracted entries from the feed.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<TimerConfig> readFeed(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        List<TimerConfig> timerConfigs = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "configs");

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }
            String tagName = parser.getName();
            // finds timer_config tags
            if (tagName.equals("timer_config"))
            {
                timerConfigs.add(readConfig(parser));
            }
            else
            {
                skip(parser);
            }
        }
        return timerConfigs;
    }

    // Parses contents of timer_config. Looks for name, timer_initial, timer_increment, and repetitions

    /**
     * This parses the contents of each timer_config. The TimerConfig needs the following data: the
     * name, initial timer, the increment amount, and the number of repetitions.
     * @param parser - The parser for the default timer configurations XML.
     * @return - A TimerConfig from the XML file.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private TimerConfig readConfig(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, "timer_config");
        String name = null;
        String timerInitial = null;
        String timerIncrement = null;
        String repetitions = null;
        String iconColor = null;

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
            {
                continue;
            }
            String tagName = parser.getName();
            if (tagName.equals("name"))
            {
                name = readString(parser, "name");
            }
            else if (tagName.equals("timer_initial"))
            {
                timerInitial = readString(parser, "timer_initial");
            }
            else if (tagName.equals("timer_increment"))
            {
                timerIncrement = readString(parser, "timer_increment");
            }
            else if (tagName.equals("repetitions"))
            {
                repetitions = readString(parser, "repetitions");
            }
            else if (tagName.equals("icon_color"))
            {
                iconColor = readString(parser, "icon_color");
            }
            else
            {
                // ignore tags that are not relevant to building a default configuration
                skip(parser);
            }
        }

        return makeDefaultTimerConfig(name, timerInitial, timerIncrement, repetitions, iconColor);
    }

    /**
     * This returns the string enclosed within the required string tag.
     * @param parser - The parser for the default timer configurations XML.
     * @param reqString - The name of the tag that encloses the string to return.
     * @return - The string enclosed in the specified tags.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private String readString(XmlResourceParser parser, String reqString) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, reqString);
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, reqString);
        return s;
    }

    /**
     * This returns the next read string and moves the parser to the next tag.
     * @param parser - The parser for the default timer configurations XML.
     * @return - The next read string or an empty string.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private String readText(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT)
        {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * This method moves the parser, ignoring the contents contained at its current place.
     * @param parser - The parser for the default timer configurations XML.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        if (parser.getEventType() != XmlPullParser.START_TAG)
        {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0)
        {
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                    --depth;
                    break;
                case XmlPullParser.START_TAG:
                    ++depth;
                    break;
            }
        }
    }

    /**
     * This constructs a default TimerConfig. The default_configs.xml has timer configurations with
     * the name, initial timer, increment amount, number of repetitions, and icon colors. Each step
     * increments linearly by the increment amount, so the timer array needs to be constructed.
     * @param name - The name of the timer.
     * @param timerInitial - The initial timer amount.
     * @param timerIncrement - The increment amount for each step of the timer.
     * @param repetitions - The number of repetitions.
     * @param iconColor - The hex color for the icon.
     * @return - A timer config.
     */
    private TimerConfig makeDefaultTimerConfig(String name, String timerInitial, String timerIncrement, String repetitions, String iconColor)
    {
        int totalInfusions = Integer.parseInt(repetitions);
        long[] timerSeconds;

        if (TwoDigitTimer.checkInfusionBounds(totalInfusions))
        {
            timerSeconds = new long[totalInfusions];
            long initialTimerSecs = Long.parseLong(timerInitial);
            long incrementTimerSecs = Long.parseLong(timerIncrement);
            if (TwoDigitTimer.checkTimerBounds(initialTimerSecs)
                    && TwoDigitTimer.checkTimerBounds(incrementTimerSecs * totalInfusions))
            {
                for (int i = 0; i < totalInfusions; ++i)
                {
                    timerSeconds[i] = initialTimerSecs + (i * incrementTimerSecs);
                }
                return new TimerConfig(name, timerSeconds, iconColor, false, true);
            }
        }

        return null;
    }
}
