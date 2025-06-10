package com.example.rolas.service;

import com.example.rolas.model.Post;
import com.example.rolas.Repositorio.PostRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HashtagService {

    private final PostRepositorio postRepositorio;

    public HashtagService(PostRepositorio postRepositorio) {
        this.postRepositorio = postRepositorio;
    }

    public List<HashtagTrend> getTrendingHashtags(int topN) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since24h = now.minusHours(24);
        LocalDateTime since48h = now.minusHours(48);

        // posts de las ultimas 24h y 24-48h atras
        List<Post> posts24h = postRepositorio.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(since24h))
                .toList();

        List<Post> postsPrev24h = postRepositorio.findAll().stream()
                .filter(p -> p.getCreatedAt() != null && p.getCreatedAt().isAfter(since48h) && p.getCreatedAt().isBefore(since24h))
                .toList();

        Map<String, Integer> count24h = countHashtags(posts24h);
        Map<String, Integer> countPrev24h = countHashtags(postsPrev24h);

        List<HashtagTrend> trends = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : count24h.entrySet()) {
            String tag = entry.getKey();
            int countNow = entry.getValue();
            int countPrev = countPrev24h.getOrDefault(tag, 0);
            int growth = countNow - countPrev;
            trends.add(new HashtagTrend(tag, countNow, growth));
        }

        // ordena por  usados, luego por mayor crecimiento
        trends.sort(Comparator.comparingInt(HashtagTrend::getCount).reversed()
                .thenComparing(Comparator.comparingInt(HashtagTrend::getGrowth).reversed()));

        return trends.subList(0, Math.min(topN, trends.size()));
    }

    private Map<String, Integer> countHashtags(List<Post> posts) {
        Map<String, Integer> hashtagCount = new HashMap<>();
        Pattern hashtagPattern = Pattern.compile("#(\\w+)");
        for (Post post : posts) {
            Matcher matcher = hashtagPattern.matcher(post.getContent() != null ? post.getContent() : "");
            Set<String> hashtagsInPost = new HashSet<>();
            while (matcher.find()) {
                hashtagsInPost.add(matcher.group(1).toLowerCase());
            }
            for (String tag : hashtagsInPost) {
                hashtagCount.put(tag, hashtagCount.getOrDefault(tag, 0) + 1);
            }
        }
        return hashtagCount;
    }

    public static class HashtagTrend {
        private final String hashtag;
        private final int count;
        private final int growth;

        public HashtagTrend(String hashtag, int count, int growth) {
            this.hashtag = hashtag;
            this.count = count;
            this.growth = growth;
        }

        public String getHashtag() { return hashtag; }
        public int getCount() { return count; }
        public int getGrowth() { return growth; }
    }
}