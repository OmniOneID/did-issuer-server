package org.omnione.did.base.db.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.domain.QNamespace;
import org.omnione.did.base.db.domain.QVcSchema;
import org.omnione.did.base.db.domain.VcSchema;
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
public class VcSchemaRepositoryAdminImpl implements VcSchemaRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<VcSchema> searchVcSchema(String searchKey, String searchValue, Pageable pageable) {
        QVcSchema vcSchema = QVcSchema.vcSchema;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = Optional.ofNullable(queryFactory
                .select(vcSchema.count())
                .from(vcSchema)
                .where(predicate)
                .fetchOne())
                .orElse(0L);

        List<VcSchema> results = queryFactory
                .selectFrom(vcSchema)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, vcSchema))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QVcSchema vcSchema = QVcSchema.vcSchema;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchKey) {
                case "vcSchemaId":
                    predicate = predicate.and(vcSchema.vcSchemaId.contains(searchValue));
                    break;
                case "title":
                    predicate = predicate.and(vcSchema.title.contains(searchValue));
                    break;
                default:
                    predicate = predicate.and(Expressions.FALSE);
            }
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QVcSchema vcSchema) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, vcSchema.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "vcSchemaId":
                    orders.add(new OrderSpecifier<>(direction, vcSchema.vcSchemaId));
                    break;
                case "title":
                    orders.add(new OrderSpecifier<>(direction, vcSchema.title));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, vcSchema.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
