package org.omnione.did.base.db.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.IssueProfile;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.domain.QIssueProfile;
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
public class IssueProfileRepositoryAdminImpl implements IssueProfileRepositoryAdmin {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<IssueProfile> searchIssueProfiles(String searchKey, String searchValue, Pageable pageable) {
        QIssueProfile issueProfile = QIssueProfile.issueProfile;
        BooleanExpression predicate = buildPredicate(searchKey, searchValue);

        long total = Optional.ofNullable(queryFactory
                .select(issueProfile.count())
                .from(issueProfile)
                .where(predicate)
                .fetchOne())
                .orElse(0L);

        List<IssueProfile> results = queryFactory
                .selectFrom(issueProfile)
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable, issueProfile))
                .fetch();

        return new PageImpl<>(results, pageable, total);
    }

    public BooleanExpression buildPredicate(String searchKey, String searchValue) {
        QIssueProfile issueProfile = QIssueProfile.issueProfile;
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (searchKey != null && searchValue != null && !searchValue.isEmpty()) {
            predicate = switch (searchKey) {
                case "title" -> predicate.and(issueProfile.title.eq(searchValue));
                default -> predicate.and(Expressions.FALSE);
            };
        }

        return predicate;
    }

    public OrderSpecifier<?>[] getOrderSpecifier(Pageable pageable, QIssueProfile issueProfile) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();

        if (!pageable.getSort().isSorted()) {
            orders.add(new OrderSpecifier<>(Order.ASC, issueProfile.createdAt));
        }

        for (Sort.Order order: pageable.getSort()) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "vcPlanId":
                    orders.add(new OrderSpecifier<>(direction, issueProfile.vcPlanId));
                    break;
                case "title":
                    orders.add(new OrderSpecifier<>(direction, issueProfile.title));
                    break;
                default:
                    orders.add(new OrderSpecifier<>(Order.ASC, issueProfile.createdAt));
                    break;
            }
        }
        return orders.toArray(new OrderSpecifier[0]);
    }
}
