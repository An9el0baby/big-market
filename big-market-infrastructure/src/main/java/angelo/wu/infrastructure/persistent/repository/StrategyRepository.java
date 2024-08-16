package angelo.wu.infrastructure.persistent.repository;

import angelo.wu.domain.strategy.model.entity.StrategyAwardEntity;
import angelo.wu.domain.strategy.repository.IStrategyRepository;
import angelo.wu.infrastructure.persistent.dao.IStrategyAwardDao;
import angelo.wu.infrastructure.persistent.po.StrategyAward;
import angelo.wu.infrastructure.persistent.redis.IRedisService;
import angelo.wu.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyAwardDao strategyAwardDao;
    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStratrgyAwardList(Long strategyId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        // 从缓存中查询
        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) {
            return strategyAwardEntities;
        }
        // 从数据库中查询
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
           StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                      .strategyId(strategyAward.getStrategyId())
                      .awardId(strategyAward.getAwardId())
                      .awardCount(strategyAward.getAwardCount())
                      .awardCountSurplus(strategyAward.getAwardCountSurplus())
                      .awardRate(strategyAward.getAwardRate())
                      .build();
           strategyAwardEntities.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey, strategyAwardEntities);
        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardsSearchRateTables(Long strategyId,Integer rateRange, Map<Integer, Integer> shuffleStrategyAwardsSearchRateTables) {
        // 1. 存储抽奖策略范围值，如10000， 用于生成1000以内的随机数
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId,rateRange);
        // 2. 存储概率抽查表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStrategyAwardsSearchRateTables);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }

    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, int rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId, rateKey);
    }

}
