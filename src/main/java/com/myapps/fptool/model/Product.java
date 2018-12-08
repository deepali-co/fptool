package com.myapps.fptool.model;

public class Product {
	private String productKey;
	private String productBasePath;
	private String scmUrl;
	private String softwareProductName;

	public String getProductKey() {
		return productKey;
	}

	public void setProductKey(String productKey) {
		this.productKey = productKey;
	}

	public String getProductBasePath() {
		return productBasePath;
	}

	public void setProductBasePath(String productBasePath) {
		this.productBasePath = productBasePath;
	}

	public String getScmUrl() {
		return scmUrl;
	}

	public void setScmUrl(String scmUrl) {
		this.scmUrl = scmUrl;
	}

	public String getSoftwareProductName() {
		return softwareProductName;
	}

	public void setSoftwareProductName(String softwareProductName) {
		this.softwareProductName = softwareProductName;
	}
}
