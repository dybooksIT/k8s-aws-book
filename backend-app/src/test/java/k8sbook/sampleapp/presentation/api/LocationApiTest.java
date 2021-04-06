package k8sbook.sampleapp.presentation.api;

import k8sbook.sampleapp.domain.service.LocationService;
import k8sbook.sampleapp.persistence.entity.LocationEntity;
import k8sbook.sampleapp.persistence.entity.RegionEntity;
import k8sbook.sampleapp.persistence.repository.LocationRepository;
import k8sbook.sampleapp.persistence.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(classes = {LocationApi.class, LocationService.class, LocationRepository.class, RegionRepository.class})
public class LocationApiTest {

    @Autowired
    private LocationApi api;

    @MockBean
    private RegionRepository regionRepository;

    @MockBean
    private LocationRepository locationRepository;

    @Test
    public void testGetLocationListByRegion() {
        var regionEntity = new RegionEntity();
        regionEntity.setRegionId(1);
        regionEntity.setRegionName("지역 1");

        given(regionRepository.findById(1)).willReturn(Optional.of(regionEntity));

        var loc1 = new LocationEntity();
        loc1.setLocationId(1L);
        loc1.setLocationName("명소 1");
        loc1.setRegion(regionEntity);
        loc1.setNote("명소 1의 상세 정보입니다.");

        var loc2 = new LocationEntity();
        loc2.setLocationId(1L);
        loc2.setLocationName("명소 2");
        loc2.setRegion(regionEntity);
        loc2.setNote("명소 2의 상세 정보입니다.");

        var locationList = List.of(loc1, loc2);
        given(locationRepository.findByRegion(regionEntity)).willReturn(locationList);

        var result = api.getLocationListByRegion(1);
        assertThat(result.getLocationList()).hasSize(2);
    }

}
