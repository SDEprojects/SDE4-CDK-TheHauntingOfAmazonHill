package com.intelligents.haunting;

import java.io.*;

class FileReader {

    /**Reads text files and returns it's content**/

    FileReader() {

    }

    String fileReader(String path, String fileToRead, ClassLoader cl) throws IOException {
        if (fileToRead != null) {
            InputStream results = cl.getResourceAsStream(path + fileToRead);
            StringBuilder sb = new StringBuilder();
            String line;

            assert results != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(results));
            while ((line = br.readLine()) != null){
                sb.append(line).append("\n");
            }
            br.close();

            return sb.toString();
        } else {
            System.out.println("Sorry that file is not in the Path.");
        }
        return null;
    }
}