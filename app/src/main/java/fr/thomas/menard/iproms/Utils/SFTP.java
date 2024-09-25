package fr.thomas.menard.iproms.Utils;
import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class SFTP {

        public boolean connect(String host, String username, String password, int port,
                               String srcFilePath, String desDirectory) {
                new AsyncSftpTask(host, username, password, port, srcFilePath, desDirectory).execute();
            return true;
        }

        private class AsyncSftpTask extends AsyncTask<Void, Void, Boolean> {
            private final String host;
            private final String username;
            private final String password;
            private final int port;
            private final String srcFilePath;
            private final String desDirectory;

            AsyncSftpTask(String host, String username, String password, int port,
                          String srcFilePath, String desDirectory) {
                this.host = host;
                this.password = password;
                this.port = port;
                this.username = username;
                this.srcFilePath = srcFilePath;
                this.desDirectory = desDirectory;
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                try {

                    JSch jsch = new JSch();
                    Session session = jsch.getSession(username, host, port);
                    session.setPassword(password);

                    // Avoid asking for key confirmation
                    Properties properties = new Properties();
                    properties.put("StrictHostKeyChecking", "no");
                    session.setConfig(properties);
                    session.connect();


                    ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
                    channelSftp.connect();

                    // Change to the destination directory
                    String currentPath = "/mnt/sdb";
                    String[] pathSegments = desDirectory.split("/");

                    for (String segment : pathSegments) {

                        if (!segment.isEmpty()) {
                            currentPath += "/" + segment ;
                            try {
                                channelSftp.cd(currentPath);

                            } catch (SftpException e) {
                                // Directory does not exist, create it
                                channelSftp.mkdir(currentPath);
                                channelSftp.cd(currentPath);

                            }
                        }
                    }


                    boolean upload = false;
                    // Upload files
                    File fileEntry = new File(srcFilePath);
                    FileInputStream fis = new FileInputStream(srcFilePath);
                    channelSftp.put(fis, fileEntry.getName());
                    fis.close();


                    channelSftp.disconnect();
                    session.disconnect();

                    return upload;

                } catch (Exception e) {
                    Log.e("SFTP", "Error: " + e.getMessage());
                    return false;
                }
            }
        }



}
