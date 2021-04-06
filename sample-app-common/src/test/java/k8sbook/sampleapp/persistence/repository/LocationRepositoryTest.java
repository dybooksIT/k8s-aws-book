package k8sbook.sampleapp.persistence.repository;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LocationRepositoryTest {

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    @Tag(("DBRequired"))
    public void testFindByRegion() {
        var region = regionRepository.findByRegionName("지역 1").get();
        var result = locationRepository.findByRegion(region);
        assertThat(result).hasSize(4);
    }

    @BeforeEach
    public void prepareDatabase() {
        var operations = sequenceOf(
                deleteAllFrom("location"),
                deleteAllFrom("region"),
                insertInto("region")
                        .columns("region_id", "region_name", "creation_timestamp")
                        .values(1, "지역 1", LocalDateTime.now())
                        .values(2, "지역 2", LocalDateTime.now())
                        .values(3, "지역 3", LocalDateTime.now())
                        .values(4, "지역 4", LocalDateTime.now())
                        .build(),
                insertInto("location")
                        .columns("location_id", "location_name", "region_id", "note")
                        .values(1, "명소 1", 1, "명소 1의 상세 정보입니다.")
                        .values(2, "명소 2", 1, "명소 2의 상세 정보입니다.")
                        .values(3, "명소 3", 1, "명소 3의 상세 정보입니다.")
                        .values(4, "명소 4", 1, "명소 4의 상세 정보입니다.")
                        .build()
        );
        var dbSetup = new DbSetup(new DataSourceDestination(dataSource), operations);
        dbSetup.launch();
    }

}
