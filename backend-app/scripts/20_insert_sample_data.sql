-- REGION
INSERT INTO region (region_name, creation_timestamp)
VALUES ('서울', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('강릉', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('대전', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('광주', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('대구', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('부산', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('여수', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('안동', current_timestamp);

INSERT INTO region (region_name, creation_timestamp)
VALUES ('제주도', current_timestamp);

-- LOCATION
INSERT INTO location (location_name, region_id, note)
VALUES ('테디베어 뮤지엄', (SELECT region_id FROM region WHERE region_name = '제주도'),
  '테디베어의 역사는 물론 예술, 세계여행 등의 테마를 제공하는 테마 뮤지엄 브랜드입니다.');

INSERT INTO location (location_name, region_id, note)
VALUES ('성산 일출봉', (SELECT region_id FROM region WHERE region_name = '제주도'),
  '유네스코 세계자연유산에 등재된 제주도의 랜드마크.');

-- BATCH_PROCESSING
INSERT INTO batch_processing (batch_name)
values ('SAMPLE_APP_BATCH');
