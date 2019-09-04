package com.example.pageretriever;

import com.example.pageretriever.model.MemberChallenges;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class PageRetrieverApplication implements CommandLineRunner {

    private static final Log LOGGER = LogFactory.getLog(PageRetrieverApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PageRetrieverApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String url = args[0];
        Document document = Jsoup.connect(url).get();
        document.charset(StandardCharsets.UTF_8);
        Elements elements = document.select("span.profile-link");
        List<MemberChallenges> memberChallenges = new ArrayList<>();
        for (Element element : elements) {
            String nick = element.text();
            Optional<Integer> optionalChallenge = element.classNames().stream()
                    .filter(className -> className.startsWith("completed"))
                    .map(value -> Integer.parseInt(value.split("completed")[1]))
                    .findFirst();
            if (optionalChallenge.isPresent()) {
                memberChallenges.add(new MemberChallenges(nick, optionalChallenge.get()));
            } else {
                memberChallenges.add(new MemberChallenges(nick, 0));
            }
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(memberChallenges);
        String fileName = "memberListJson.json";
        try (Writer bufferedWriter = new BufferedWriter(new FileWriter(fileName, StandardCharsets.UTF_8))) {
            bufferedWriter.write(json);
            LOGGER.info("File with name \"" + fileName + "\" was created in the same directory with JAR file.");
        }
    }
}
