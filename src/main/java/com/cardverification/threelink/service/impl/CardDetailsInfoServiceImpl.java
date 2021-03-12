package com.cardverification.threelink.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cardverification.threelink.entity.CardInformation;
import com.cardverification.threelink.entity.CardPayload;
import com.cardverification.threelink.exceptions.CardInfoServiceException;
import com.cardverification.threelink.exceptions.InvalidInputException;
import com.cardverification.threelink.messages.ErrorMessages;
import com.cardverification.threelink.pojo.ExtractPojo;
import com.cardverification.threelink.repository.CardInfoRepository;
import com.cardverification.threelink.responses.InfoResponse;
import com.cardverification.threelink.responses.StatsResponse;
import com.cardverification.threelink.service.CardDetailsInfoService;

@Service
public class CardDetailsInfoServiceImpl implements CardDetailsInfoService {
	@Autowired
	private CardInfoRepository repo;
	@Value("${binlist.url}")
	private String binUrl;
	@Autowired
	private RestTemplate restTemplate;
	private Logger log = LoggerFactory.getLogger(CardDetailsInfoServiceImpl.class);

	@Override
	public InfoResponse getCardInfo(String cardNumber) {
		return cardInfo(cardNumber);
	}

	@Override
	public StatsResponse getCardStats(int start, int limit) {
		return getAllCardStats(start, limit);
	}

	//checks if number is complete and valid, also remove spaces
	private String validateInput(String number) {
		String nospace = number.replaceAll("\\s+", "");
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(nospace);
		boolean b = m.find();
		if(nospace.chars().anyMatch(Character::isLetter) || b || number.length() < 8) {
			log.error("invalid card number");
			throw new InvalidInputException(ErrorMessages.WRONG_INPUT.getErrorMessages());
		}
		String num = nospace.substring(0, 8);
		log.info("card verified");
		return num;
	}

	/*takes in the validated card number and checks if its already in the database
	 * if not it makes an external api call
	 * 
	 */
	@Async
	private InfoResponse cardInfo(String number) {
		
		if(number == null || number.trim().isEmpty()) {
			throw new CardInfoServiceException(ErrorMessages.NO_CARD_NUMBER.getErrorMessages());
		}
		int nums = 0;
		String num = validateInput(number);
		CardInformation info = repo.findByCardNumber(num);
		if (info == null) {
			ExtractPojo pojo = restTemplate.getForObject(binUrl + num, ExtractPojo.class);
			if (pojo == null) {
				throw new CardInfoServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessages());
			}
				
				CardPayload payload = new CardPayload(pojo.getScheme() == null ? "" : pojo.getScheme(), 
						pojo.getType() == null ? "" : pojo.getType(), 
						pojo.getBank().getName() == null ? "" : pojo.getBank().getName());
				CardInformation cardInfo = new CardInformation(payload, nums + 1, true, num);
				cardInfo.setLatestRequest(new Date());
				try {
					CardInformation returnValue = repo.save(cardInfo);
					if (returnValue != null)
						return cardResponse(returnValue);
				} catch (Exception e) {
					log.error("unable to save object");
				}
			
		}
		info.setSearchAmount(info.getSearchAmount() + 1);
		return cardResponse(repo.save(info));
	}

	//map card details to the response payload
	private InfoResponse cardResponse(CardInformation info) {
		CardPayload payload = info.getPayload();
		InfoResponse response = new InfoResponse();
		response.setSuccess(info.getSuccess() == null ? false : info.getSuccess());
		response.setPayload(payload);
		return response;
	}

	@Async
	private StatsResponse getAllCardStats(int start, int limit) {
		Map<String, Integer> payload = new HashMap<>();
		StatsResponse response = new StatsResponse();
		if (start > 0)
			start -= 1;
		Pageable request = PageRequest.of(start, limit);
		Page<CardInformation> cardInfoPages = repo.allCards(request);
		List<CardInformation> cards = cardInfoPages.getContent();
		if (cards.isEmpty()) {
			log.error("list is empty");
			throw new CardInfoServiceException(ErrorMessages.EMPTY_LIST.getErrorMessages());
		}
		cards.forEach(card -> {
			payload.put(card.getCardNumber(), card.getSearchAmount());
		});
		response.setLimit(limit);
		response.setSize(cards.size());
		response.setStart(start);
		response.setPayload(payload);
		response.setSuccess(true);
		return response;
	}
}
