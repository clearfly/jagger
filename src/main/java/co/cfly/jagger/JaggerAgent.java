package co.cfly.jagger;

import co.cfly.jagger.MessageReply;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

public class JaggerAgent
{
    public static void setXmppConn(XMPPConnection xmppConn)
    {
        JaggerAgent.xmppConn = xmppConn;
    }

    private static int debugLevel;
    private static XMPPConnection xmppConn;
    private static String xmppUser = null;
    private static String xmppDomain = null;
    private static String xmppServer = null;
    private static Integer xmppPort = null;
    private static String xmppPassword = null;
    private static String xmppResource = null;
    private static String xmppDescription = null;
    private static Roster savedRoster = null;

    public static XMPPConnection getXmppConn()
    {
        return xmppConn;
    }

    public static void main(String[] args)
    {
        ParseConfig.processArgs(args);
        doDebug(1, "Starting Jabber client . . .");
        ConnectionConfiguration xmppConfig;
        if (xmppServer != null)
        {
            if (xmppPort == null || xmppPort == 0)
            {
                xmppPort = 5222;
            }
            xmppConfig = new ConnectionConfiguration(xmppServer, xmppPort, xmppDomain);
        }
        else
        {
            xmppConfig = new ConnectionConfiguration(xmppDomain);
        }
        xmppConfig.setSelfSignedCertificateEnabled(true);
        if (debugLevel == 2)
        {
            Connection.DEBUG_ENABLED = true;
        }
        xmppConn = new XMPPConnection(xmppConfig);
        try
        {
            xmppConn.connect();
            doDebug(1, "Connected to " + xmppConn.getHost());
        }
        catch (XMPPException e)
        {
            doDebug(2, e.getStackTrace().toString());
            System.out.println("Failed to connect to " + xmppConn.getHost());
            System.exit(1);
        }

        SASLAuthentication.supportSASLMechanism("PLAIN", 0);

        try
        {
            if (xmppResource == null)
            {
                xmppResource = "JaggerBot";
            }
            xmppConn.login(xmppUser, xmppPassword, xmppResource);
            doDebug(1, "Logged in as " + xmppConn.getUser());
            Presence xmppPresence = new Presence(Presence.Type.available);
            xmppConn.sendPacket(xmppPresence);
        }
        catch (XMPPException e)
        {
            doDebug(2, e.getStackTrace().toString());
            System.out.println("Failed to login as " + xmppUser);
            System.exit(1);
        }

        Presence xmppPresence = new Presence(Presence.Type.available);
        xmppPresence.setStatus(xmppDescription);
        xmppPresence.setMode(Presence.Mode.available);
        xmppConn.sendPacket(xmppPresence);
        savedRoster = xmppConn.getRoster();

        PacketFilter chatFilter = new MessageTypeFilter(Message.Type.chat);
        PacketCollector xmppCollector = xmppConn.createPacketCollector(chatFilter);

        xmppCollector.pollResult();

        PacketListener xmppListener = new PacketListener()
        {
            public void processPacket(Packet packet)
            {
                if (packet instanceof Message)
                {
                    Message message = (Message) packet;
                    if (message.getBody() != null)
                    {
                        String fromName = StringUtils.parseBareAddress(message.getFrom()).toLowerCase();
                        doDebug(1, "Message from " + fromName + "\n" + message.getBody() + "\n");
                        savedRoster = updateRoster(savedRoster, xmppConn);
                        Runnable replyBot = new MessageReply(savedRoster, fromName, message.getBody());
                        Thread botThread = new Thread(replyBot);
                        botThread.start();
                    }
                }
            }
        };

        xmppConn.addPacketListener(xmppListener, chatFilter);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            public void run()
            {
                doDebug(1, "Shutting down Jabber bot . . .");
                Presence xmppPresence = new Presence(Presence.Type.available);
                xmppPresence.setStatus("The " + xmppDescription + " is temporarily unavailable. Offline messages will be delivered when it returns.");
                xmppPresence.setMode(Presence.Mode.away);
                xmppConn.sendPacket(xmppPresence);
                xmppConn.disconnect();
            }
        });

        while (true)
        {
            try
            {
                Thread.sleep(30000);
            }
            catch (InterruptedException e)
            {
                doDebug(2, e.getStackTrace().toString());
            }
        }
    }

    private static Roster updateRoster(Roster savedRoster, XMPPConnection xmppConn)
    {
        Roster newRoster = xmppConn.getRoster();
        doDebug(2, "Saved roster entries: " + savedRoster.getEntries().size() + ", New roster entries: " + newRoster.getEntries().size());
        if (newRoster.getEntries().size() > 0)
        {
            return newRoster;
        }
        return savedRoster;
    }

    public static void doDebug(int minLevel, String content)
    {
        if (debugLevel >= minLevel)
        {
            System.out.println(content);
        }
    }

    public static int getDebugLevel()
    {
        return debugLevel;
    }

    public static void setDebugLevel(int debugLevel)
    {
        JaggerAgent.debugLevel = debugLevel;
    }

    public static String getXmppUser()
    {
        return xmppUser;
    }

    public static void setXmppUser(String xmppUser)
    {
        JaggerAgent.xmppUser = xmppUser;
    }

    public static String getXmppDomain()
    {
        return xmppDomain;
    }

    public static void setXmppDomain(String xmppDomain)
    {
        JaggerAgent.xmppDomain = xmppDomain;
    }

    public static String getXmppServer()
    {
        return xmppServer;
    }

    public static void setXmppServer(String xmppServer)
    {
        JaggerAgent.xmppServer = xmppServer;
    }

    public static Integer getXmppPort()
    {
        return xmppPort;
    }

    public static void setXmppPort(Integer xmppPort)
    {
        JaggerAgent.xmppPort = xmppPort;
    }

    public static String getXmppPassword()
    {
        return xmppPassword;
    }

    public static void setXmppPassword(String xmppPassword)
    {
        JaggerAgent.xmppPassword = xmppPassword;
    }

    public static String getXmppResource()
    {
        return xmppResource;
    }

    public static void setXmppResource(String xmppResource)
    {
        JaggerAgent.xmppResource = xmppResource;
    }

    public static String getXmppDescription()
    {
        return xmppDescription;
    }

    public static void setXmppDescription(String xmppDescription)
    {
        JaggerAgent.xmppDescription = xmppDescription;
    }
}
