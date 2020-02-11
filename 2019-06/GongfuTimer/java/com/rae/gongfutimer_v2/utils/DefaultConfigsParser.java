package com.rae.gongfutimer_v2.utils;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultConfigsParser
{
    private static final String ns = "";

    public List<TimerConfig> parse(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        parser.next();
        parser.next();
        parser.next();
        return readFeed(parser);
    }

    // processes xml. Looks for elements with specific tag (timer_config) for recursively processing
    // skips non-timer_config tags
    // returns a List of extracted entries from feed
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
    private TimerConfig readConfig(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, "timer_config");
        String name = null;
        String timerInitial = null;
        String timerIncrement = null;
        String repetitions = null;

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
            else
            {
                skip(parser);
            }
        }

        return makeDefaultTimerConfig(name, timerInitial, timerIncrement, repetitions);
    }

    private String readString(XmlResourceParser parser, String reqString) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, reqString);
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, reqString);
        return s;
    }

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

    private TimerConfig makeDefaultTimerConfig(String name, String timerInitial, String timerIncrement, String repetitions)
    {
        int totalInfusions = Integer.parseInt(repetitions);
        long[] timerSeconds;

        if (TwoDigitTimer.checkInfusionBounds(totalInfusions))
        {
            timerSeconds = new long[totalInfusions];
            long initialTimerSecs = Long.parseLong(timerInitial);
            long incrementTimerSecs = Long.parseLong(timerIncrement);
            if (TwoDigitTimer.checkTimerBounds(initialTimerSecs)
                && TwoDigitTimer.checkIncrementBounds(incrementTimerSecs))
            {
                for (int i = 0; i < totalInfusions; ++i)
                {
                    timerSeconds[i] = initialTimerSecs + (i * incrementTimerSecs);
                }
                // TODO: Add icon styles to default_configs and to this parser
                return new TimerConfig(name, timerSeconds, "default", false, true);
            }
        }

        return null;
    }
}
