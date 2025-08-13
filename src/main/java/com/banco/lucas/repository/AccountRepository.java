package com.banco.lucas.repository;

import java.util.List;
import java.util.stream.Collectors;
import com.banco.lucas.exception.AccountNotFoundException;
import com.banco.lucas.model.AccountWallet;
import com.banco.lucas.model.BankService;


public class AccountRepository {




private List<AccountWallet> accounts;

public AccountWallet create(final List<String> pix , final long initialFunds){

    if(initialFunds>0 && verifyAccounts(pix)){
        var newAccount = new AccountWallet(initialFunds,pix); 
        
        accounts.add(newAccount);
        return newAccount;
    }else{

    return null;
    }
}

public AccountWallet findByPix(final String pix){

    var account = accounts.stream().filter(x->x.getPix().contains(pix))
    .findFirst().orElseThrow(() -> new AccountNotFoundException("não foi possével encontrar a conta"));
    return account;

}
public void deposit(final String pix, final long amount){
 var target  = findByPix(pix);
 target.addMoney(amount, "deposito");
}
public long withdraw(final String pix , final long amount){
    var target = findByPix(pix);
    CommonsRepository.checkFundsForTransaction(target,amount);
    target.reduceMoney(amount);
    return amount;
}
public void transferMoney(final String sourcePix, final String targetPix,final long amount){

    var target= findByPix(targetPix);
    var source = findByPix(sourcePix);
    CommonsRepository.checkFundsForTransaction(source,amount);
    String message= "'pix enviado do '" + sourcePix + "'para'" + targetPix + "''";
    target.addMoney(source.reduceMoney(amount),BankService.ACCOUNT,message);
    
}



private List<AccountWallet> list(){
    return this.accounts;
}
private boolean verifyAccounts(final List<String> pix){

   List<String> pixFound =  accounts.stream().flatMap(x-> x.getPix().stream()).filter(p-> p.equals(pix)).collect(Collectors.toList());   
    if(pixFound.isEmpty()){
        
        return true;
    }else {
        return false;
    }

}



}
