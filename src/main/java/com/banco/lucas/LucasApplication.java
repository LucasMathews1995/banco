package com.banco.lucas;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.banco.lucas.exception.AccountNotFoundException;
import com.banco.lucas.exception.NoFundsEnoughException;
import com.banco.lucas.model.AccountWallet;
import com.banco.lucas.repository.AccountRepository;
import com.banco.lucas.repository.InvestmentRepository;
record TransactionKey(String user, OffsetDateTime truncatedTime) {}

@SpringBootApplication
public class LucasApplication {

	private final static AccountRepository accountRepository = new AccountRepository();
	private final static InvestmentRepository investmentRepository = new InvestmentRepository();

	 static Scanner sc = new Scanner(System.in);
	public static void main(String[] args) {
		//SpringApplication.run(LucasApplication.class, args);
	

System.out.println("Seja bem-vindo");
while (true) {
	System.out.println("selecione a operação desejada");
	System.out.println("1 - Criar uma conta");
	System.out.println("2 - Criar um investimento");
	System.out.println("3 - fazer um investimento");
	System.out.println("4 - depositar na conta");
	System.out.println("5- sacar na conta");
	System.out.println("6 - transferencia entre contas");
	System.out.println("7 - Investir na conta");
	System.out.println("8- Sacar investimento");
	System.out.println("9- Listar Contas");
	System.out.println("10 - Listar Investimentos");
	System.out.println("11 - Listar cartetiras de Investimento");
	System.out.println("12 - Atualizar investimentos");
	System.out.println("13 - Histórico de conta");
	System.out.println("14 - Sair");
	int option= sc.nextInt();
	switch (option) {
		case 1-> createAccount();
		case 2-> createInvestment();
		case 3-> createWalletInvestment();
		case 4-> deposit();
		case 5-> withdraw();
		case 6-> transferToAccount();
		case 7-> incInvestment();
		case 8-> rescueInvestment();
		case 9-> accountRepository.list().forEach(System.out::println);
		case 10-> investmentRepository.list().forEach(System.out::println);
		case 11-> investmentRepository.listWallets().forEach(System.out::println);
		case 12-> {
			investmentRepository.updateAmount();
			System.out.println("investimentos reajustados");
		}
		case 13-> checkHistory();
		case 14-> System.exit(0);

		
		default-> System.out.println("Opcão Inválida");
		
}
}
	}
	private static void createAccount(){
	System.out.println("Informe as chaves pix: (separadas por: ';' )  ");

		var pix =  Arrays.stream(sc.next().split(";")).toList();
		System.out.println("informe o valor inicial de depósito");
		var amount = sc.nextLong();
	var wallet = 	accountRepository.create(pix, amount);
	System.out.println("Conta criada" + wallet);
	
		


	}
		private static void createInvestment(){
		System.out.println("informe a taxa do investimento");
		int tax = sc.nextInt();
		System.out.println("informe o valor inicial de depósito");
		var initialFunds = sc.nextLong();
		var investment = 	investmentRepository.create(tax, initialFunds);
	
		System.out.println("Investimento criado: " + investment);
			

	}
	private static void deposit(){
		System.out.println("Informe a chave pix da conta");
		var pix = sc.next();
		System.out.println("Quantia a depositar :");
			var amount = sc.nextLong();
	try{accountRepository.deposit(pix, amount);}catch(AccountNotFoundException e ){
				System.out.println(e.getMessage());
			}

	}
	private static void withdraw(){
		System.out.println("Informe a chave pix da conta");
		var pix = sc.next();
		System.out.println("Quantia a sacar :");
			var amount = sc.nextLong();
			try{accountRepository.withdraw(pix, amount);}catch(NoFundsEnoughException | AccountNotFoundException e ){
				System.out.println(e.getMessage());
			}
	
	}
	private static void transferToAccount(){
		System.out.println("Informe a chave pix da origem");
		var source = sc.next();
		
		System.out.println("Informe a chave pix da conta destino");
		var target = sc.next();
		System.out.println("Quantia a depositar :");
			var amount = sc.nextLong();
	try{accountRepository.transferMoney(source,target, amount);}catch(AccountNotFoundException e ){
				System.out.println(e.getMessage());
			}

	}
	private static void createWalletInvestment(){
			System.out.println("Informe a chave pix da conta");
		var pix = sc.next();
		var acccount =accountRepository.findByPix(pix);
		System.out.println("Informe o identificador do investimento");
		var investmentId = sc.nextInt();
	var investmentWallet = 	investmentRepository.iniInvestmentWallet(acccount, investmentId);
	System.out.println("Conta de investimento criado" + investmentWallet);
	}
	private static void incInvestment(){
		System.out.println("Informe a chave pix da conta para investimento");
		var pix = sc.next();
		System.out.println("Quantia a investir :");
			var amount = sc.nextLong();
	try{investmentRepository.deposit(pix, amount);
	}catch(AccountNotFoundException e ){
				System.out.println(e.getMessage());
			}

	}
	private static void rescueInvestment(){
	System.out.println("Informe a chave pix para resgate do investimento");
		var pix = sc.next();
		System.out.println("Quantia a sacar :");
			var amount = sc.nextLong();
			try{investmentRepository.withdraw(pix, amount);}
			catch(NoFundsEnoughException | AccountNotFoundException e ){
				System.out.println(e.getMessage());
			}
	}

	private static void checkHistory(){
		System.out.println("Informe a chave pix da conta");
		var pix = sc.next();
		AccountWallet wallet;
		try {
		wallet=	accountRepository.findByPix(pix);
		var audit  =wallet.getFinancialTransactions();
		 Map<TransactionKey, Long> contagemPorTempoEUsuario = audit.stream()
            .collect(Collectors.groupingBy(
                t -> new TransactionKey(
                    t.description().split(" ")[0],
                    t.createdAt().truncatedTo(ChronoUnit.MINUTES)
                ),
                Collectors.counting()
            ));


DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
			contagemPorTempoEUsuario.forEach((key,transactionCount)->{
  String formattedTime = formatter.format(key.truncatedTime());
            System.out.println("Às " + formattedTime + " horas, foi feito um  " + key.user() + " " + transactionCount + " transações.");
			});
		
		

		
			
			
		} catch (AccountNotFoundException e) {
			// TODO: handle exception
		}

		
	}
	}



