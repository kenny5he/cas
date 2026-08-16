/**
 * Copyright 2026 - Ren Jian Yan Huo
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microfish.it.account.cas.acct.repository;

import com.microfish.it.account.cas.acct.entity.JpaAccountRegistrationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author kenny.he
 * @since 2026/08/16
 */
@Repository
public interface AccountRegistrationRepository extends CrudRepository<Long, JpaAccountRegistrationEntity> {
    /**
     * 通过账号名查询账号信息
     * @param username 账号名
     * @return 账号信息
     */
    JpaAccountRegistrationEntity findByUsername(String username);

    /**
     * 通过邮箱查询账号信息
     * @param email 邮箱
     * @return 账号信息
     */
    JpaAccountRegistrationEntity findByEmail(String email);

    /**
     * 通过手机号查询账号信息
     * @param phone 手机号
     * @return 账号信息
     */
    JpaAccountRegistrationEntity findByPhone(String phone);
}
