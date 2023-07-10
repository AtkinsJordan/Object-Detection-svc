package com.example.Object.Detection.util;

import com.example.Object.Detection.model.ImaggaResponse;
import com.example.Object.Detection.model.Tag;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Data
@NoArgsConstructor
public class ImageDetectionUtil {

    /**
     * I received this method from imagga's site
     * https://docs.imagga.com/?java#tags
     * @param path
     * @return
     * @throws IOException
     */
    public String postImaggaByPath(Path path) throws IOException {
        String credentialsToEncode = "acc_68b03f1619a383f" + ":" + "1511d1ee1f9eb906443cebd01aa06115";
        String basicAuth = Base64.getEncoder().encodeToString(credentialsToEncode.getBytes(StandardCharsets.UTF_8));

        File fileToUpload = new File(path.toUri());

        String endpoint = "/tags";

        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary =  "Image Upload";

        URL urlObject = new URL("https://api.imagga.com/v2" + endpoint);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + basicAuth);
        connection.setUseCaches(false);
        connection.setDoOutput(true);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty(
                "Content-Type", "multipart/form-data;boundary=" + boundary);

        DataOutputStream request = new DataOutputStream(connection.getOutputStream());

        request.writeBytes(twoHyphens + boundary + crlf);
        request.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + fileToUpload.getName() + "\"" + crlf);
        request.writeBytes(crlf);


        InputStream inputStream = new FileInputStream(fileToUpload);
        int bytesRead;
        byte[] dataBuffer = new byte[1024];
        while ((bytesRead = inputStream.read(dataBuffer)) != -1) {
            request.write(dataBuffer, 0, bytesRead);
        }

        request.writeBytes(crlf);
        request.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
        request.flush();
        request.close();

        InputStream responseStream = new BufferedInputStream(connection.getInputStream());

        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

        String line = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ((line = responseStreamReader.readLine()) != null) {
            stringBuilder.append(line).append("\n");
        }


        responseStreamReader.close();

        String response = stringBuilder.toString();
        //System.out.println(response);

        responseStream.close();
        connection.disconnect();

        return response;
    }

    /**
     * This method maps the json response to the model objects and returns the tags
     * @param response
     * @return
     */
    public ArrayList<Tag> getTagsFromImaggaResponse(String response) {
        ImaggaResponse imaggaResponse;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            imaggaResponse = objectMapper.readValue(response, ImaggaResponse.class);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(imaggaResponse);
        ArrayList<Tag> tags = imaggaResponse.getResult().getTags();
        return  tags;
    }

    /**
     * This method filters out tags with a confidence lower than 75
     * @param tags  all the tags return from the imagga post
     * @return tags the a confidence of 75 or higher
     */
    public List<Tag> getHighConfidenceTags(List<Tag> tags) {
        List<Tag> highConfidenceList = new ArrayList<>();
        for(Tag t: tags) {
            if(t.getConfidence() >= 75.0){
                highConfidenceList.add(t);
            }
        }
        return highConfidenceList;
    }

    public List<String> convertToStringLabel(List<Tag> tags) {
        List<String> labels = new ArrayList<>();
        for(Tag t: tags) {
            labels.add(t.getTag().getEn());
        }
        return labels;
    }
}
