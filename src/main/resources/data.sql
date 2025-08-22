insert into location(name, description) values
  ('B1 전기실','지하 전력 분전반실'),
  ('RF 기계실','옥상 기계실');
insert into sensor(sensor_name,type,location_id) values
  ('MAIN_FEEDER','POWER',1),
  ('CHILLER_TEMP','TEMP',2);