package com.banco.lucas.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MoneyAudit(UUID transaction,BankService targService,String description,OffsetDateTime createdAt) {

}
