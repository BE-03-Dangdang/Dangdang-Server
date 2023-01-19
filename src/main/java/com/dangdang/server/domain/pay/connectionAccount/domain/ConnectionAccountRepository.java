package com.dangdang.server.domain.pay.connectionAccount.domain;

import com.dangdang.server.domain.pay.connectionAccount.domain.entity.ConnectionAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectionAccountRepository extends JpaRepository<ConnectionAccount, Long> {

}
