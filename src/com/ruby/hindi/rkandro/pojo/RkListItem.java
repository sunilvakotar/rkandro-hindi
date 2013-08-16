package com.ruby.hindi.rkandro.pojo;

import java.io.Serializable;
@SuppressWarnings("serial")
public class RkListItem implements Serializable {

	private Integer id; //0
	private String Title;// 1
	private String Description; // 2
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
		this.Title = title;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		this.Description = description;
	}
	
	
}
