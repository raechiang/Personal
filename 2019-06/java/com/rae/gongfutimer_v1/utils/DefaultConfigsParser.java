package com.rae.gongfutimer_v1.utils;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultConfigsParser
{
    private static final String ns = "";

    public List<TimeConfig> parse(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        parser.next();
        parser.next();
        parser.next();
        return readFeed(parser);
    }

    // processes xml. Looks for elements with specific tag (timer_config) for recursively processing
    // skips non-timer_config tags
    // returns a List of extracted entries from feed
    private List<TimeConfig> readFeed(XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        List<TimeConfig> timerConfigs = new ArrayList<>();

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

    // Parses contents of timer_config. At the moment, looks for name, timer_initial, and repetitions
    private TimeConfig readConfig(/*XmlPullParser parser*/XmlResourceParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, ns, "timer_config");
        String name = null;
        String temperatureC = null;
        String temperatureF = null;
        String gPerML = null;
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
            else if (tagName.equals("temperature_c"))
            {
                temperatureC = readString(parser, "temperature_c");
            }
            else if (tagName.equals("temperature_f"))
            {
                temperatureF = readString(parser, "temperature_f");
            }
            else if (tagName.equals("g_per_ml"))
            {
                gPerML = readString(parser, "g_per_ml");
            }
            else if (tagName.equals(("timer_initial")))
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

        return makeDefaultTimeConfig(name, temperatureC, temperatureF, gPerML, timerInitial, timerIncrement, repetitions);
    }

    private String readString(XmlResourceParser parser, String reqString) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, ns, reqString);
        String s = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, reqString);
        return s;
    }

    private String readText(XmlResourceParser parser) throws IOException, XmlPullParserException
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

    private TimeConfig makeDefaultTimeConfig(String name, String temperatureC, String temperatureF, String gPerML, String timerInitial, String timerIncrement, String repetitions)
    {
        String defaultStringExtraNotes =
                "Temp[°C]: " + temperatureC
                        + ". Temp[°F]: " + temperatureF
                        + ". g per mL: " + gPerML + ". ";
        return new TimeConfig(UUID.randomUUID(), name, Long.parseLong(timerInitial), Long.parseLong(timerIncrement), Integer.parseInt(repetitions), defaultStringExtraNotes, true);
    }
}
