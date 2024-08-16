package angelo.wu.domain.strategy.service.armory;

import angelo.wu.domain.strategy.model.entity.StrategyAwardEntity;
import angelo.wu.domain.strategy.repository.IStrategyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
public class StrategyArmory implements IStrategyArmory{
    @Resource
    private IStrategyRepository repository;

    @Override
    public void assembleLotteryStrategy(Long strategyId) {
        // 1. 查询策略配置
        List<StrategyAwardEntity>  strategyAwardEntities = repository.queryStratrgyAwardList(strategyId);

        // 2. 获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 3. 获取概率值总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 4. 用 1 % 0.0001 获取概率范围， 百分位，千分位，万分位
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        // 5. 构建查找表
        List<Integer> strategyAwardsSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();

            // 计算出每个概率值需要存放到查找表的数量，循环填充
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardsSearchRateTables.add(awardId);
            }
        }
        // 6. 乱序
        Collections.shuffle(strategyAwardsSearchRateTables);

        // 7. 构建查找map
        Map<Integer, Integer> shuffleStrategyAwardsSearchRateTables = new HashMap<>();
        for (int i = 0; i < strategyAwardsSearchRateTables.size(); i++) {
            shuffleStrategyAwardsSearchRateTables.put(i,  strategyAwardsSearchRateTables.get(i));
        }
        // 8. 存储到redis
        repository.storeStrategyAwardsSearchRateTables(strategyId, shuffleStrategyAwardsSearchRateTables.size(), shuffleStrategyAwardsSearchRateTables);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
         int rateRange = repository.getRateRange(strategyId);
         return repository.getStrategyAwardAssemble(strategyId, new SecureRandom().nextInt(rateRange));
    }
}
