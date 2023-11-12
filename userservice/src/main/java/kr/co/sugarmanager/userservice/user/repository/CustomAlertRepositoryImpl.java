package kr.co.sugarmanager.userservice.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import kr.co.sugarmanager.userservice.user.entity.QUserSettingEntity;
import kr.co.sugarmanager.userservice.user.vo.AlertType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomAlertRepositoryImpl implements CustomAlertRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private BooleanPath getField(QUserSettingEntity setting, AlertType alertType) {
        BooleanPath result = null;
        switch (alertType) {
            case CHALLENGE -> result = setting.challengeAlert;
            case BLOOD -> result = setting.sugarAlert;
            case POKE -> result = setting.pokeAlert;
        }
        return result;
    }

    @Override
    public long setAlarm(long pk, AlertType alertType, boolean status) {
        QUserSettingEntity setting = QUserSettingEntity.userSettingEntity;

        JPAUpdateClause query = jpaQueryFactory.update(setting);

        BooleanPath field = getField(setting, alertType);
        query.set(field, status);

        BooleanExpression eq = setting.user.pk.eq(pk).and(setting.deletedAt.isNull());
        query.where(eq);

        return query.execute();
    }
}