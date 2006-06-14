/* $Id$ */
import marauroa.common.Configuration;

// default content-type is text/html
// can't be set if included
try { response.set("Content-Type", "text/plain"); } 
catch (Exception e) {};

String serverName    = "Stendhal"
String serverContact = "https://sourceforge.net/tracker/?atid=514826&group_id=66537&func=browse";

Configuration conf=Configuration.getConfiguration();
try { serverName=conf.get("server_name"); }
catch (Exception e) {};
try {serverContact=conf.get("server_contact");}
catch (Exception e) {};

String message = "You're connected to " + serverName + ". Please report problems, suggestions and bugs to " + serverContact

out << message;
