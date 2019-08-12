package com.nugsky;

import java.util.List;

public class Config {
    private String newsApiUrl;
    private List<String> webhookUrls;

    public Config() {
    }

    public String getNewsApiUrl() {
        return newsApiUrl;
    }

    public void setNewsApiUrl(String newsApiUrl) {
        this.newsApiUrl = newsApiUrl;
    }

    public List<String> getWebhookUrls() {
        return webhookUrls;
    }

    public void setWebhookUrls(List<String> webhookUrls) {
        this.webhookUrls = webhookUrls;
    }
}
