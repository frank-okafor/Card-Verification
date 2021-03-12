package com.cardverification.threelink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cardverification.threelink.service.CardDetailsInfoService;

@RestController
@CrossOrigin(origins = "*")
public class CardDetailsController {
	@Autowired
	private CardDetailsInfoService service;
	

	@GetMapping(path = "/card-scheme/verify/{cardNumber}", produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity getInfo(@PathVariable("cardNumber") String cardNumber) {
		return new ResponseEntity<>(service.getCardInfo(cardNumber),HttpStatus.OK);
	}

	@GetMapping(path = "/card-scheme/stats", produces = {MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity getAllDrinks(@RequestParam(value = "start", defaultValue = "0") int start,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		return new ResponseEntity<>(service.getCardStats(start, limit), HttpStatus.OK);
	}
}
