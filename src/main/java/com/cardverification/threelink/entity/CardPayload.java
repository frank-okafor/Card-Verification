package com.cardverification.threelink.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class CardPayload implements Serializable {

	private static final long serialVersionUID = 1L;
	private String scheme;
	private String type;
	private String bank;

	public CardPayload() {
		super();
	}

	public CardPayload(String scheme, String type, String bank) {
		super();
		this.scheme = scheme;
		this.type = type;
		this.bank = bank;
	}
}
