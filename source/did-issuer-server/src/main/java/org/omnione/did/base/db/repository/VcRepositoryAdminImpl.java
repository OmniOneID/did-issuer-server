package org.omnione.did.base.db.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.*;
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
public class VcRepositoryAdminImpl implements VcRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Vc> searchIssuedVc(String searchKey, String searchValue, Pageable pageable) {
        QVc vc = QVc.vc;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = Optional.ofNullable(queryFactory
                .select(vc.count())
                .from(vc)
                .where(predicate)
                .fetchOne())
                .orElse(0L);

        List<Vc> results = queryFactory
                .selectFrom(vc)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, vc))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QVc vc = QVc.vc;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "did":
                    predicate = predicate.and(vc.did.eq(searchValue));
                    break;
                case "vcId":
                    predicate = predicate.and(vc.vcId.eq(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QVc vc) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.DESC, vc.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "vcPlanId":
                    orders.add(new OrderSpecifier<>(direction, vc.vcPlanId));
                    break;
                case "vcId":
                    orders.add(new OrderSpecifier<>(direction, vc.vcId));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, vc.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
