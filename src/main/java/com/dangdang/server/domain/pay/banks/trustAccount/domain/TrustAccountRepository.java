package com.dangdang.server.domain.pay.banks.trustAccount.domain;

import com.dangdang.server.domain.pay.banks.trustAccount.domain.entity.TrustAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrustAccountRepository extends JpaRepository<TrustAccount, Long> {

}
