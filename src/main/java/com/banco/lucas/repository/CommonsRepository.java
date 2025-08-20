package com.banco.lucas.repository;

import lombok.NoArgsConstructor;
import static lombok.AccessLevel.PRIVATE;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.banco.lucas.exception.NoFundsEnoughException;

import com.banco.lucas.model.BankService;
import com.banco.lucas.model.Money;
import com.banco.lucas.model.MoneyAudit;
import com.banco.lucas.model.Wallet;

@NoArgsConstructor(access = PRIVATE)
public final class CommonsRepository {

public static void checkFundsForTransaction(final Wallet source,final long amount){

if(source.getFunds()<amount){
throw new NoFundsEnoughException("sua conta não tem dinheiropara realizar essa transação");
}



}

public static List<Money> generateMoney(final UUID transactionId,final long funds,final String description){
   var history = new MoneyAudit(transactionId,BankService.ACCOUNT,description,OffsetDateTime.now());
    return Stream.generate(()-> new Money(history)).limit(funds).toList();
}

}
