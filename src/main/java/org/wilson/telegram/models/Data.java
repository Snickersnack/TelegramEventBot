package org.wilson.telegram.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Data {

	private Long account_id;
    private String account_url;
    private Boolean animated;
    private Integer bandwidth;
//    "datetime": 1484257383,
//    "deletehash": "BDnLC7OsmUQ7qNJ",
//    "description": null,
//    "favorite": false,
//    "height": 600,
//    "id": "0Hs1Dsu",
//    "in_gallery": false,
//    "is_ad": false,
    private String link;
//    "name": "",
//    "nsfw": null,
//    "section": null,
//    "size": 83831,
//    "title": null,
//    "type": "image/jpeg",
//    "views": 0,
//    "vote": null,
//    "width": 800
    
    public Long getAccount_id() {
		return account_id;
	}
	public String getAccount_url() {
		return account_url;
	}
	public Boolean getAnimated() {
		return animated;
	}
	public Integer getBandwidth() {
		return bandwidth;
	}
	public String getLink() {
		return link;
	}
	
	@JsonProperty("account_id")
	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}
	@JsonProperty("account_url")
	public void setAccount_url(String account_url) {
		this.account_url = account_url;
	}
	@JsonProperty("animated")
	public void setAnimated(Boolean animated) {
		this.animated = animated;
	}
	@JsonProperty("bandwidth")
	public void setBandwidth(Integer bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	@JsonProperty("link")
	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() { 
	    return "Account_id: '" + this.account_id + "', account_url: '" + this.account_url + "', animated: '" + this.animated + "', bandwidth'" + this.bandwidth + "', link'" + this.link ;
	} 

	
}
