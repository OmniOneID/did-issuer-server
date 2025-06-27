/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omnione.did.base.db.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.QZkpCredentialDefinition;
import org.omnione.did.base.db.domain.ZkpCredentialDefinition;
import org.omnione.did.base.db.domain.ZkpSchema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ZkpCredentialDefinitionRepositoryAdminImpl implements ZkpCredentialDefinitionRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ZkpCredentialDefinition> searchCredentialDefinitions(String searchKey, String searchValue, Pageable pageable) {
        QZkpCredentialDefinition qZkpCredentialDefinition = QZkpCredentialDefinition.zkpCredentialDefinition;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = Optional.ofNullable(queryFactory
                        .select(qZkpCredentialDefinition.count())
                        .from(qZkpCredentialDefinition)
                        .where(predicate)
                        .fetchOne())
                .orElse(0L);

        List<ZkpCredentialDefinition> results = queryFactory
                .selectFrom(qZkpCredentialDefinition)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qZkpCredentialDefinition))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QZkpCredentialDefinition qZkpCredentialDefinition = QZkpCredentialDefinition.zkpCredentialDefinition;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "definitionId":
                    predicate = predicate.and(qZkpCredentialDefinition.definitionId.eq(searchValue));
                    break;
                case "schemaId":
                    predicate = predicate.and(qZkpCredentialDefinition.schemaId.eq(searchValue));
                    break;
                case "alias":
                    predicate = predicate.and(qZkpCredentialDefinition.alias.eq(searchValue));
                    break;
                case "tag":
                    predicate = predicate.and(qZkpCredentialDefinition.tag.eq(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QZkpCredentialDefinition qZkpCredentialDefinition) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qZkpCredentialDefinition.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "definitionId":
                    orders.add(new OrderSpecifier<>(direction, qZkpCredentialDefinition.definitionId));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, qZkpCredentialDefinition.createdAt));
                    break;
            }
        }

        return orders.toArray(new OrderSpecifier[0]);
    }
}
