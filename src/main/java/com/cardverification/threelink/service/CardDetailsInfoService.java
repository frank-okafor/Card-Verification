package com.cardverification.threelink.service;

import org.springframework.stereotype.Service;

import com.cardverification.threelink.responses.InfoResponse;
import com.cardverification.threelink.responses.StatsResponse;

@Service
public interface CardDetailsInfoService {

	public InfoResponse getCardInfo(String cardNumber);
	public StatsResponse getCardStats(int start, int limit);
	
}
