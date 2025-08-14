package com.banco.lucas.repository;

import java.util.ArrayList;
import java.util.List;

import com.banco.lucas.exception.AccountWalletException;
import com.banco.lucas.exception.InvestmentNotFoundException;
import com.banco.lucas.exception.WalletNotFoundException;
import com.banco.lucas.model.AccountWallet;
import com.banco.lucas.model.Investment;
import com.banco.lucas.model.InvestmentWallet;

public class InvestmentRepository {
        private long nextId;
    private List<Investment> investments = new ArrayList<>();

    private List<InvestmentWallet> wallets = new ArrayList<>();


    public List<Investment> list(){
        return this.investments;


    }
    public List<InvestmentWallet> listWallets(){
            return this.wallets;
    }

    public void updateAmount (final long percent){
        wallets.forEach(w-> w.updateAmount(percent));
    }


    public Investment create (final long tax , final long daysToRescue,final long initialFunds){
        this.nextId++;
var investment = new Investment(this.nextId,tax,initialFunds);
investments.add(investment);
return investment;


    }

    public InvestmentWallet iniInvestmentWallet(final AccountWallet account , final long id){

        var accountInUse = wallets.stream().map(InvestmentWallet::getAccount).toList();


            if(accountInUse.contains(account)){
                throw new AccountWalletException("'A carteira de investimento'" + account + "'já possui investimento'");
            
        }

        var investment = findById(id);
       CommonsRepository.checkFundsForTransaction(account, investment.initialFunds());

       var wallet = new InvestmentWallet(investment, account, investment.initialFunds());

       wallets.add(wallet);
       return wallet;


    }


    public InvestmentWallet deposit(final String pix , final long funds){
          var wallet = findWalletAccountPix(pix);
          wallet.addMoney(wallet.getAccount().reduceMoney(funds), wallet.getService(), "investimento");

          return wallet;
    }

    public InvestmentWallet withdraw (final String pix , final long funds){

        var wallet = findWalletAccountPix(pix);
        CommonsRepository.checkFundsForTransaction(wallet, funds);

        wallet.getAccount().addMoney( wallet.reduceMoney(funds),wallet.getService(), pix);
            if(wallet.getFunds()==0){
                wallets.remove(wallet);
            }

        return wallet;
       
        

    }


    public Investment findById(final long id){
        return investments.stream().filter(x->x.id() == id)
     .findFirst().orElseThrow(() -> new InvestmentNotFoundException("o investimento não existe"));

    }


    public InvestmentWallet findWalletAccountPix(final String pix){
     var accountWallet =    wallets.stream().filter(x->x.getAccount().getPix().contains(pix))
     .findFirst().orElseThrow(() -> new WalletNotFoundException("a carteira de investimento não existe"));

        return accountWallet;

    }

}
