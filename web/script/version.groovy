/* $Id$ */
import games.stendhal.common.Debug;
// default content-type is text/html
// can't be set if included
try { response.set("Content-Type", "text/plain"); } 
catch (Exception e) {};
out << Debug.VERSION;
