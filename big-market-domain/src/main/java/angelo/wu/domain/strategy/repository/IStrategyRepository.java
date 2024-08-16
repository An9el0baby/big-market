package angelo.wu.domain.strategy.repository;


import angelo.wu.domain.strategy.model.entity.StrategyAwardEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStratrgyAwardList(Long strategyId);
    void storeStrategyAwardsSearchRateTables(Long strategyId, Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardsSearchRateTables);

    int getRateRange(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, int rateKey);
}
