/*
 * FILE:
 *      ProgramLoader.java
 *
 * DESCRIPTION:
 *      Loads and parses the information from sr.se API.
 *
 * AUTHOR:
 *      Elias Niko, c18eno
 *
 * CHANGES:
 *      20200129: Initial version.
 */
package model;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
 * The class loads the XML and parses it, it will store the required data into
 * datatype.
 * @author Elias Niko
 * @version 1.0
 */
public class ProgramLoader {
    private static HashMap<Integer, ArrayList<Program>> channelPrograms;
    private static ArrayList<Integer> idList;
    private static ArrayList<String> nameList;
    private final ChannelLoader channelLoader;
    private final ArrayList<Integer> notFoundPages;

    private final String[] dates;
    private static DateTimeFormatter format;

    /**
     * The constructor for the class.
     */
    public ProgramLoader() {
        format = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("CET"));
        dates = getAllDates();
        channelLoader = new ChannelLoader();
        channelLoader.getResponse();
        channelPrograms = new HashMap<>();
        idList = channelLoader.getChannelIds();
        nameList = channelLoader.getChannelNames();
        notFoundPages = new ArrayList<>();

        loadPages();
    }

    /**
     * The method loads different dates with URL address to get data from the URL.
     * If the current time is before noon it will load just today's URL and
     * yesterday's URL. Otherwise, today's and tomorrow's URL.
     */
    private synchronized void loadPages() {
        final LocalTime now = LocalTime.now();
        final LocalTime noon = LocalTime.NOON;


        for (final Integer id : idList) {
            if (now.isBefore(noon)) {
                for (int i = 0; i < dates.length - 1; i++) {
                    final String url = "https://api.sr.se/api/v2/scheduledepisodes?channelid=" + id + "&date=" + dates[i] + "&pagination=false";
                    extractURLAdresses(url, id);
                }
            } else {
                for (int i = 1; i < dates.length; i++) {
                    final String url = "https://api.sr.se/api/v2/scheduledepisodes?channelid=" + id + "&date=" + dates[i] + "&pagination=false";
                    extractURLAdresses(url, id);
                }
            }
        }
    }

    /**
     * Gets yesterday's, today's and tomorrow's date.
     * @return date as String Array
     */
    private synchronized String[] getAllDates() {
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        final String today = LocalDateTime.now().format(format);
        final String yesterday = LocalDateTime.now().minusDays(1).format(format);
        final String tomorrow = LocalDateTime.now().plusDays(1).format(format);

        final String[] date = new String[3];
        date[0] = yesterday;
        date[1] = today;
        date[2] = tomorrow;

        return date;
    }

    /**
     * Tries to connect to the URL. If the process failed the method throws
     * exceptions.
     * @param url as String
     * @param id  as int
     */
    private void extractURLAdresses(final String url, final int id) {
        URL obj;
        try {
            obj = new URL(url);

            final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            if (con.getResponseCode() != 404) {
                final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                final DocumentBuilder db = dbf.newDocumentBuilder();
                final Document doc = db.parse(new URL(url).openStream());
                loadEpisodes(id, doc);
            } else notFoundPages.add(id);
        } catch (final MalformedURLException e) {
            System.out.println("Error 404");
        } catch (final IOException | SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for a list of those channels that has not any content.
     * @return notFoundPages as ArrayList of Integers
     */
    public ArrayList<Integer> getNotfoundPages() {
        return notFoundPages;
    }

    /**
     * Loads all episodes respect to XAML's tag line.
     * @param id  as int
     * @param doc as Document
     */
    private synchronized void loadEpisodes(final int id, final Document doc) {
        final ArrayList<Program> programs = new ArrayList<>();
        final NodeList episodeList = doc.getElementsByTagName("schedule");
        final Node n = episodeList.item(0);
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            final Element e = (Element) n;
            final NodeList scheduleList = e.getChildNodes();
            for (int j = 0; j < scheduleList.getLength(); j++) {
                final Node node = scheduleList.item(j);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    final Element eNode = (Element) node;
                    final String title = eNode.getElementsByTagName("title").item(0).getTextContent();

                    String imageURL = "imageurl";
                    if (checkEmptyElement(eNode, imageURL))
                        imageURL = eNode.getElementsByTagName("imageurl").item(0).getTextContent();
                    else imageURL = null;

                    String description = "description";
                    if (checkEmptyElement(eNode, description))
                        description = eNode.getElementsByTagName("description").item(0).getTextContent();
                    else description = null;

                    final LocalDateTime startTimeUTC = LocalDateTime
                            .parse(eNode.getElementsByTagName("starttimeutc").item(0).getTextContent(), format);
                    final LocalDateTime endTimeUTC = LocalDateTime
                            .parse(eNode.getElementsByTagName("endtimeutc").item(0).getTextContent(), format);
                    final String status = getStatus(startTimeUTC, endTimeUTC);

                    final String startTime = changeTimeFormat(startTimeUTC);
                    final String endTime = changeTimeFormat(endTimeUTC);

                    if (checkTheTime(startTimeUTC)) {
                        final Program prog = new Program(id, title, description, startTime, endTime, status, imageURL);
                        programs.add(prog);
                    }
                }
            }
        }
        if (channelPrograms.containsKey(id)) {
            final ArrayList<Program> progList = channelPrograms.get(id);
            progList.addAll(programs);
        } else
            channelPrograms.put(id, programs);
    }

    /**
     * Determines if a tag element is empty or not.
     * @param el as Element
     * @param tagName String
     * @return true if element is not empty, false otherwise.
     */
    public boolean checkEmptyElement(final Element el, final String tagName) {
        final NodeList node = el.getElementsByTagName(tagName);
        return node.getLength() > 0;
    }

    /**
     * Formats a date and time into given pattern.
     * @param dateTime LocalDateTime
     * @return the formatted time as String.
     */
    private String changeTimeFormat(final LocalDateTime dateTime) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu MMM d  HH:mm");
        return dateTime.format(formatter);
    }

    /**
     * Gets status of a Date and time respect to current time and returns if a date
     * and time is passed, not passed and now.
     * @param start as LocalDateTime
     * @param end   as LocalDateTime
     * @return String
     */
    public String getStatus(final LocalDateTime start, final LocalDateTime end) {
        final LocalDateTime now = LocalDateTime.now();
        if (end.isBefore(now))
            return "Slut";
        else if (start.isBefore(now) && end.isAfter(now))
            return "Nu";
        else return "Kommer";
    }

    /**
     * Determines if a time is between an interval. In this case 12 hours before the
     * given time and 12 hours after.
     * @param startTime as LocalDateTime
     * @return true if the time is between the interval, false otherwise.
     */
    private boolean checkTheTime(final LocalDateTime startTime) {
        final LocalDateTime twelveHoursBefore = LocalDateTime.now().minusHours(12);
        final LocalDateTime twelveHoursAfter = LocalDateTime.now().plusHours(12);
        return startTime.isAfter(twelveHoursBefore) && startTime.isBefore(twelveHoursAfter);
    }

    /**
     * Getter for all channels ID and the programs.
     * @return channelPrograms as a HashMap
     */
    public HashMap<Integer, ArrayList<Program>> getChannelPrograms() {
        return channelPrograms;
    }

    /**
     * Getter for channels name.
     * @return nameList as ArrayList of String
     */
    public ArrayList<String> getNameOfChannels() {
        return nameList;
    }

    /**
     * Getter for all channels.
     * @return channels as HashMap
     */
    public HashMap<String, Integer> getChannels() {
        return channelLoader.getChannels();
    }
}
