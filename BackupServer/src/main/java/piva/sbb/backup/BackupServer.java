package piva.sbb.backup;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackupServer {
    public static Logger logger = Logger.getLogger("BackupServer");

    public static void main(String[] args) {
        logger.log(Level.INFO, "Starting Backup Server...");

        try {
            new ServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}