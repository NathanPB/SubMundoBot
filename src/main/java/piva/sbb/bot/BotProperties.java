package piva.sbb.bot;

import me.piva.utils.properties.PropertiesFile;
import me.piva.utils.properties.Property;
import me.piva.utils.properties.defaultvalues.Default;

import java.io.File;

public class BotProperties implements PropertiesFile {
    private static final File FILE = new File("./manager.properties");

    @Override
    public File getFile() {
        return FILE;
    }

    @Property
    @Default.DefaultString(defaultString = "localhost")
    public String dbHost;

    @Property
    @Default.DefaultString(defaultString = "root")
    public String dbUser;

    @Property
    public String dbPass;

    @Property
    public String database;
}