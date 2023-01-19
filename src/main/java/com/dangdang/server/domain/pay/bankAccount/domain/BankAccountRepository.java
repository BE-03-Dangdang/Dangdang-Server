package com.dangdang.server.domain.pay.bankAccount.domain;

import com.dangdang.server.domain.pay.bankAccount.domain.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

}
