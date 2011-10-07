package co.cfly.jagger;

import java.io.IOException;
import java.util.Date;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.MessageEventManager;

public class MessageReply implements Runnable {
	private String replyAddr;
	private String receivedMessage;
	private String returnMessage;
	private XMPPConnection xmppConn;
	private Roster roster;
	private boolean replyToAll;

	public MessageReply(Roster roster, String replyAddr, String receivedMessage) {
		this.replyAddr = replyAddr;
		this.receivedMessage = receivedMessage;
		this.roster = roster;
	}

	private String parseEmailAddress(String address) {
		String[] parts = address.split("@");
		return parts[0];
	}

	private void processMessage() {
		returnMessage = "";
		if (receivedMessage.toLowerCase().equals("$who"))
		{
			returnMessage = "\nActive users in this group:\n";
			for (RosterEntry entry : roster.getEntries()) {
				returnMessage = returnMessage + entry.getUser() + "\n";
			}
			replyToAll = false;
		}
		else if (receivedMessage.toLowerCase().equals("$help"))
		{
			JarTextUtil thisHelp = new JarTextUtil();
			String outHelp = null;
			try {
				outHelp = thisHelp.dumpFile("helpText.txt");
			} catch(IOException e) {
				e.printStackTrace();
				outHelp = "ERROR RETRIEVING HELP FILE!";
			}
			returnMessage = "\n" + outHelp;
			replyToAll = false;
		}
		else
		{
			returnMessage = parseEmailAddress(replyAddr) + ": " + receivedMessage;
			replyToAll = true;
		}
	}

	public void run() {
		xmppConn = JaggerAgent.getXmppConn();
		MessageEventManager xmppMsgEventMgr = new MessageEventManager(xmppConn);
		processMessage();
		if (replyToAll)
		{
			for (RosterEntry entry : roster.getEntries()) {
				if (entry.getUser().equals(replyAddr) == false) {
					xmppMsgEventMgr.sendComposingNotification(entry.getUser(),new Date().toString());
					Message reply = new Message();
					reply.setTo(entry.getUser());
					reply.setBody(returnMessage);
					xmppConn.sendPacket(reply);
				}
			}
		} 
		else
		{
			xmppMsgEventMgr.sendComposingNotification(replyAddr,new Date().toString());
			Message reply = new Message();
			reply.setTo(replyAddr);
			reply.setBody(returnMessage);
			xmppConn.sendPacket(reply);
		}
	}
}
