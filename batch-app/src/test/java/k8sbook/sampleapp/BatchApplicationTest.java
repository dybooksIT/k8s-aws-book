package k8sbook.sampleapp;

import com.amazonaws.services.s3.AmazonS3;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import io.findify.s3mock.S3Mock;
import k8sbook.sampleapp.BatchApplication;
import org.assertj.db.type.Table;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;

import static com.ninja_squad.dbsetup.Operations.*;
import static org.assertj.db.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class BatchApplicationTest {

    private static boolean needToInitializeS3 = true;

    private static S3Mock s3Mock;

    @Autowired
    private BatchApplication batchApp;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AmazonS3 amazonS3;

    /**
     * 배치 애플리케이션을 초기화할 때는 처리(Batch Application#run)가 고정되도록 한다.
     */
    @BeforeAll
    public static void prepareBatchNotToRun() {
        System.setProperty("sample.app.batch.run", "false");
    }

    @Value("${sample.app.batch.bucket.name}")
    private String bucketName;

    @Value("${sample.app.batch.folder.name}")
    private String folderName;

    @BeforeAll
    public static void startS3Mock() {
        s3Mock = new S3Mock.Builder()
                .withPort(8001)
                .withInMemoryBackend()
                .build();
        s3Mock.start();
    }

    /**
     * 테스트할 때 배치 애플리케이션 처리(Batch Application#run)가 고정되도록 한다.
     */
    @BeforeEach
    public void prepareBatchToRun() {
        System.setProperty("sample.app.batch.run", "true");
    }

    @AfterAll
    public static void shutdownS3Mock() {
        if (s3Mock != null) {
            s3Mock.shutdown();
        }
    }

    @BeforeEach
    public void putTestFilesToS3() {
        if (needToInitializeS3) {
            amazonS3.createBucket(bucketName);
            try {
                amazonS3.putObject(bucketName, folderName + "/location1.csv",
                        resourceLoader.getResource("classpath:"
                                + getClass().getPackageName().replace('.', '/')
                                + "/location1.csv").getFile());
                amazonS3.putObject(bucketName, folderName + "/location2.csv",
                        resourceLoader.getResource("classpath:"
                                + getClass().getPackageName().replace('.', '/')
                                + "/location2.csv").getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            needToInitializeS3 = false;
        }
    }

    @Tag("DBRequired")
    @Test
    public void testRun() throws Exception {
        batchApp.run();

        var locationTable = new Table(dataSource, "location");
        assertThat(locationTable).hasNumberOfRows(8); // original 4 + file1 3 + file2 1
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
