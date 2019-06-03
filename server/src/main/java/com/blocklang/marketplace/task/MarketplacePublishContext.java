package com.blocklang.marketplace.task;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MarketplacePublishContext {
	
	private String dataRootPath; // block lang 站点的项目文件根目录
	private String website;
	private String owner;
	private String projectName;
	private Path logFile;
	
	public MarketplacePublishContext(String dataRootPath, String gitUrl) {
		this.dataRootPath = dataRootPath;
		this.parse(gitUrl);
	}
	
	private void parse(String gitUrl) {
		// 一个完整的 gitUrl 示例
		// https://github.com/blocklang/blocklang.com.git
		String url = gitUrl.toLowerCase();
		// 1. 去除开头的 https://
		if(url.startsWith("https://")) {
			url = url.substring("https://".length());
		}
		// 2. 去除结尾的 .git
		if(url.endsWith(".git")) {
			url = url.substring(0, url.length() - ".git".length());
		}
		String[] segments = url.split("/");
		
		this.website = segments[0];
		this.owner = segments[1];
		this.projectName = segments[2];
	}

	public Path getRepoSourceDirectory() {
		return this.getRepoDirectory().resolve("source");
	}
	
	private String getRepoPublishLogFileName(String version) {
		LocalDateTime startLogTime = LocalDateTime.now();
		return version + 
				"-" + 
				startLogTime.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss")) + 
				".log";
	}

	private Path getRepoPublishLogDirectory() {
		return this.getRepoDirectory().resolve("publishLogs");
	}
	
	private Path getRepoDirectory() {
		return Path.of(this.dataRootPath, "marketplace", this.website, this.owner, this.projectName);
	}

	public Path getRepoPublishLogFile(String version) {
		if(this.logFile == null) {
			this.logFile = this.getRepoPublishLogDirectory().resolve(this.getRepoPublishLogFileName(version));
		}
		return this.logFile;
	}

}