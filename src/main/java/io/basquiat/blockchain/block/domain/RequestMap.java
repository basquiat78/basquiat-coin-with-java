package io.basquiat.blockchain.block.domain;

import java.util.List;

import io.basquiat.blockchain.transaction.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class RequestMap {

	/**
	 * transaction list
	 */
	private List<Transaction> transactions;
	
}
