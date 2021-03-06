package com.blocklang.marketplace.constant;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RepoCategory {

	WIDGET("01", "Widget"),
	CLIENT_API("02", "Client API"),
	SERVER_API("03", "Server API"),
	UNKNOWN("99", "Unknown");
	
	private final String key;
	private final String value;

	private RepoCategory(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@JsonValue
	public String getKey() {
		return this.key;
	}

	public String getValue() {
		return this.value;
	}
	
	public static RepoCategory fromValue(String value) {
		if (StringUtils.isBlank(value)) {
			return RepoCategory.UNKNOWN;
		}
		return Arrays.stream(RepoCategory.values())
				.filter((each) -> value.toLowerCase().equals(each.getValue().toLowerCase()))
				.findFirst()
				.orElse(RepoCategory.UNKNOWN);
	}

	public static RepoCategory fromKey(String key) {
		if (StringUtils.isBlank(key)) {
			return RepoCategory.UNKNOWN;
		}
		return Arrays.stream(RepoCategory.values())
				.filter((each) -> key.equals(each.getKey()))
				.findFirst()
				.orElse(RepoCategory.UNKNOWN);
	}
	
}
