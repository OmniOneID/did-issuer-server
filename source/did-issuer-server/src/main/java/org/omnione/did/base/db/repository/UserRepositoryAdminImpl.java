package org.omnione.did.base.db.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.QUser;
import org.omnione.did.base.db.domain.User;
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
public class UserRepositoryAdminImpl implements UserRepositoryAdmin {
    private final JPAQueryFactory queryFactory;
    private final VcSchemaRepository vcSchemaRepository;

    @Override
    public Page<User> searchUser(String searchKey, String searchValue, Pageable pageable) {
        QUser qUser = QUser.user;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = Optional.ofNullable(queryFactory
                .select(qUser.count())
                .from(qUser)
                .where(predicate)
                .fetchOne())
                .orElse(0L);

        List<User> results = queryFactory
                .selectFrom(qUser)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, qUser))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QUser qUser = QUser.user;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            predicate = switch (searchKey) {
                case "did" -> predicate.and(qUser.did.eq(searchValue));
                case "vcSchemaId" -> {
                    Optional<VcSchema> vcSchema = vcSchemaRepository.findByVcSchemaId(searchValue);
                    if (vcSchema.isPresent()) {
                        yield predicate.and(qUser.vcSchemaId.eq(vcSchema.get().getId()));
                    } else {
                        yield predicate.and(Expressions.FALSE);
                    }
                }
                default -> predicate.and(Expressions.FALSE);
            };
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QUser qUser) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, qUser.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "did" -> orders.add(new OrderSpecifier<>(direction, qUser.did));
                case "pii" -> orders.add(new OrderSpecifier<>(direction, qUser.pii));
                default -> orders.add(new OrderSpecifier<>(Order.ASC, qUser.createdAt));
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
