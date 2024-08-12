package angelo.wu.test.infrastructure;

import angelo.wu.infrastructure.persistent.dao.IAwardDao;
import angelo.wu.infrastructure.persistent.po.Award;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/** Award persistent unit test*/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AwardDaoTest {
    @Resource
    private IAwardDao awardDao;

    @Test
    public void test_queryAwardList(){
        List<Award> awards = awardDao.queryAwardList();
        log.info("awards:{}",awards);
    }
}
