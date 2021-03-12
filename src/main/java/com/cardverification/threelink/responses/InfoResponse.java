package com.cardverification.threelink.responses;


import com.cardverification.threelink.entity.CardPayload;

import lombok.Data;

@Data
public class InfoResponse {

	private Boolean success;
	private CardPayload payload;

}
