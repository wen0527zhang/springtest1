package com.neworange.gb28181.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 19:10
 * @description
 */
@Component
@PropertySource(value = {"classpath:git.properties" }, ignoreResourceNotFound = true)
public class GitUtil {
    @Value("${git.branch:}")
    private String branch;
    @Value("${git.commit.id:}")
    private String gitCommitId;
    @Value("${git.remote.origin.url:}")
    private String gitUrl;
    @Value("${git.build.time:}")
    private String buildDate;

    @Value("${git.build.version:}")
    private String buildVersion;

    @Value("${git.commit.id.abbrev:}")
    private String commitIdShort;

    @Value("${git.commit.time:}")
    private String commitTime;

    public String getGitCommitId() {
        return gitCommitId;
    }

    public String getBranch() {
        return branch;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public String getCommitIdShort() {
        return commitIdShort;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public String getCommitTime() {
        return commitTime;
    }
}
