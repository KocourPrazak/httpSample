package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HTTP Request!
 */
public class App {
    static final String REMOTE_URI = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";
    public static ObjectMapper jsonMapper = new ObjectMapper();

    public static void main(String[] args) {
        //Create HTTP client
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // max connection time
                        .setSocketTimeout(30000)    // max response time
                        .setRedirectsEnabled(false) // allow redirect
                        .build())
                .build()) {
            // Create http request to the specified URL
            HttpGet request = new HttpGet(REMOTE_URI);
            //send request and get response
            CloseableHttpResponse response = httpClient.execute(request);
            //Read response content
            String body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            //Converts response content from json to the java objects and streams it to the filtered list
            List<Post> posts = jsonMapper.readValue(body, new TypeReference<List<Post>>() {
                    }).stream()
                    .filter(x -> x.getType().equals("cat")) //Select posts type "cat"
                    .filter(x -> x.getUpvotes() != null) // Upvotes not null
                    .sorted(Comparator.comparingInt(Post::getUpvotes).reversed()) //most upvoted posts first
                    .collect(Collectors.toList()); //collect stream to list
            //Prints filtered list of posts
            posts.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
