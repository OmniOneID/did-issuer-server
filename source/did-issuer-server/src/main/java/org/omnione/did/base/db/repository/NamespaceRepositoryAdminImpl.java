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
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.domain.QNamespace;
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
public class NamespaceRepositoryAdminImpl implements NamespaceRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Namespace> searchNamespaces(String searchKey, String searchValue, Pageable pageable) {
        QNamespace namespace = QNamespace.namespace;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = Optional.ofNullable(queryFactory
                .select(namespace.count())
                .from(namespace)
                .where(predicate)
                .fetchOne())
                .orElse(0L);

        List<Namespace> results = queryFactory
                .selectFrom(namespace)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, namespace))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QNamespace namespace = QNamespace.namespace;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "namespaceId":
                    predicate = predicate.and(namespace.namespaceId.eq(searchValue));
                    break;
                case "name":
                    predicate = predicate.and(namespace.name.eq(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QNamespace namespace) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, namespace.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "namespaceId":
                    orders.add(new OrderSpecifier<>(direction, namespace.namespaceId));
                    break;
                case "name":
                    orders.add(new OrderSpecifier<>(direction, namespace.name));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, namespace.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
