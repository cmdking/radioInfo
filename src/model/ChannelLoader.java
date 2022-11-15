/*
 * FILE:
 *      ChannelLoader.java
 *
 * DESCRIPTION:
 *      Loads the channel names and channel IDs.
 *
 * AUTHOR:
 *      Elias Niko, c18eno
 *
 * CHANGES:
 *      20200129: Initial version.
 */

package model;


/*
 * Test seen
 *
 *
 */

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * The class loads name and IDs of all available channels
 * into different kind of datatype.
 *
 * @author Elias Niko
 * @version 1.0
 */
public class ChannelLoader {
    private static ArrayList<Integer> channelIds;
    private static ArrayList<String> channelNames;
    public  static HashMap<String, Integer> channelMap;

    private static Document doc;
    private static String url;

    public ChannelLoader() {
        url          = "https://api.sr.se/api/v2/channels?pagination=false";
        channelMap   = new HashMap<>();
        channelIds   = new ArrayList<>();
        channelNames = new ArrayList<>();
    }

    /**
     * Gets response and load all channels using XML parser.
     * The methods returns true if parsing succeed and false otherwise.
     */
    public void getResponse() {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(new URL(url).openStream());
            loadChannels();
        }catch(final IOException | SAXException | ParserConfigurationException ignored) {
        }
    }

    /**
     * Stores IDs and names of channels into ArrayLists and HashMap.
     */
    private void loadChannels() {
//		final Thread channelThread = new Thread() {
//			public void run() {
        final NodeList channels = doc.getElementsByTagName("channel");
        for (int i = 0; i < channels.getLength(); i++) {
            final Node n = channels.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                final Element elmnt   = (Element) n;
                final int id          = Integer.parseInt(elmnt.getAttribute("id"));
                final String name     = elmnt.getAttribute("name");
                channelIds      .add(id);
                channelNames    .add(name);
                channelMap      .put(name, id);
            }
        }

//			}
//		};
//		channelThread.start();
    }

    /**
     * Getter for all channel IDs as an ArrayList of Integers.
     * @return channelIds as ArrayList of integers
     */
    public ArrayList<Integer> getChannelIds() {
        return channelIds;
    }

    /**
     * Getter for all channel IDs as an ArrayList of Integers.
     * @return channelNames as ArrayList of Strings
     */
    public ArrayList<String> getChannelNames(){
        return channelNames;
    }

    /**
     * Getter for a HashMap that includes name of channels and IDs.
     * @return HashMap, key as String, value as integer
     */
    public HashMap<String, Integer> getChannels(){
        return channelMap;
    }
}
