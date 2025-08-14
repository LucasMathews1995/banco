package com.banco.lucas.repository;

import java.util.List;

import com.banco.lucas.exception.AccountNotFoundException;
import com.banco.lucas.exception.PixInUseException;
import com.banco.lucas.model.AccountWallet;
import com.banco.lucas.model.BankService;


public class AccountRepository {




private List<AccountWallet> accounts;

public AccountWallet create(final List<String> pix , final long initialFunds){
    var pixInUse=    accounts.stream().flatMap(a -> a.getPix().stream()).toList();
        for(String p : pix){
           if(pixInUse.contains(p)) {
            throw new PixInUseException("'O pix'"+ p +"'já está em uso'");
           }
        }

        var newAccount = new AccountWallet(initialFunds,pix); 
        
        accounts.add(newAccount);
        return newAccount;
  
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






}
