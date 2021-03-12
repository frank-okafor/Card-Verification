package com.cardverification.threelink.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.cardverification.threelink.entity.CardInformation;

@Repository
public interface CardInfoRepository extends PagingAndSortingRepository<CardInformation, Long>{

	@Query(value = "SELECT * FROM card_information",nativeQuery = true)
	Page<CardInformation> allCards(Pageable page);
	
	CardInformation findByCardNumber(String cardNumber);
}
