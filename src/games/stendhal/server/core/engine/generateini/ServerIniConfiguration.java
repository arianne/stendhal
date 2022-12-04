package games.stendhal.server.core.engine.generateini;

import java.io.PrintWriter;
import java.util.Date;

import marauroa.common.crypto.RSAKey;

public class ServerIniConfiguration {

    private final DatabaseConfiguration databaseConfiguration;
    private final String gameName = "stendhal";
    private final String databaseImplementation = "games.stendhal.server.core.engine.StendhalPlayerDatabase";
    private final String tcpPort = "32160";
    private final String worldImplementation = "games.stendhal.server.core.engine.StendhalRPWorld";
    private final String ruleprocessorImplementation = "games.stendhal.server.core.engine.StendhalRPRuleProcessor";
    private final String factoryImplementation = "games.stendhal.server.core.engine.StendhalRPObjectFactory";
    private final String turnLength = "300";
    private final String statisticsFilename = "./server_stats.xml";
    private final RSAKey rsakey;
    private final Date generationDate;

    public ServerIniConfiguration(DatabaseConfiguration databaseConfiguration, Integer keySize) {
        this.databaseConfiguration = databaseConfiguration;
        this.rsakey = RSAKey.generateKey(keySize);
        this.generationDate = new Date();
    }

    ServerIniConfiguration(
            DatabaseConfiguration databaseConfiguration,
            RSAKey rsakey,
            Date generationDate) {
        this.databaseConfiguration = databaseConfiguration;
        this.rsakey = rsakey;
        this.generationDate = generationDate;
    }

    public void write(PrintWriter out) {
        out.println("# Generated .ini file for Test Game at " + this.generationDate);
        out.println("# Database and factory classes. Don't edit.");
        out.println("database_implementation=" + this.databaseImplementation);
        out.println("factory_implementation=" + this.factoryImplementation);
        out.println();
        out.println("# Database information. Edit to match your configuration.");
        databaseConfiguration.write(out);
        out.println();
        out.println("# TCP port stendhald will use. ");
        out.println("tcp_port=" + this.tcpPort);
        out.println();
        out.println("# World and RP configuration. Don't edit.");
        out.println("world=" + this.worldImplementation);
        out.println("ruleprocessor=" + this.ruleprocessorImplementation);
        out.println();
        out.println("turn_length=" + this.turnLength);
        out.println();
        out.println("server_typeGame=" + this.gameName);
        out.println("server_name=" + this.gameName + " Marauroa server");
        out.println("server_version=1.41.5");
        out.println("server_contact=https://sourceforge.net/tracker/?atid=514826&group_id=66537&func=browse");
        out.println();
        out.println("# Extensions configured on the server. Enable at will.");
        out.println("#server_extension=xxx");
        out.println("#xxx=some.package.Classname");
        out.println();
        out.println("statistics_filename=" + this.statisticsFilename);
        out.println();
        this.rsakey.print(out);
    }

}
