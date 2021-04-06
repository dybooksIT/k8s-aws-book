package k8sbook.sampleapp.domain.service;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import k8sbook.sampleapp.domain.model.Location;
import k8sbook.sampleapp.domain.model.Region;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.assertj.db.api.Assertions.assertThat;

@SpringBootTest
public class LocationServiceTest {

    @Autowired
    private LocationService service;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Test
    @Tag("DBRequired")
    public void testRegisterLocations() {
        var locationList = List.of(
                new Location("명소 5", new Region(1, "지역 1", LocalDateTime.now()), "명소 5의 상세 정보입니다."),
                new Location("명소 6", new Region(1, "지역 1", LocalDateTime.now()), "명소 6의 상세 정보입니다.")
        );
        service.registerLocations(locationList);

        var locationTable = new Table(dataSource, "location");
        assertThat(locationTable).hasNumberOfRows(6);
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
