package com.dangdang.server.domain.pay.banks.bankAccount.domain;

import com.dangdang.server.domain.pay.banks.bankAccount.domain.entity.BankAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

  Optional<BankAccount> findByPayMemberIdAndAccountNumber(Long payMemberId, String accountNumber);

  Optional<BankAccount> findByAccountNumber(String accountNumber);
}
