package com.banco.lucas.model;


import java.util.List;

import lombok.Getter;


public class AccountWallet extends Wallet {

  
@Getter
private final List<String> pix;    


    public AccountWallet(final List<String> pix) {
        super(BankService.ACCOUNT);
      this.pix = pix;
    }

    
  public AccountWallet(final long amount,final List<String> pix) {
        super(BankService.ACCOUNT);
      this.pix = pix;
      addMoney(amount,"dinheiro adicionado a conta");
    }


    public void addMoney(final long amount,final String description){
        var money = generateMoney(amount, description);

        this.money.addAll(money);
    }
}
