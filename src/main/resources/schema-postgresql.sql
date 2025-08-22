create table if not exists location (
  id bigserial primary key,
  name varchar(100) not null,
  description varchar(255)
);

create table if not exists sensor (
  id bigserial primary key,
  sensor_name varchar(100) not null,
  type varchar(50) not null,
  location_id bigint not null references location(id)
);

create table if not exists measurement (
  id bigserial primary key,
  sensor_id bigint not null references sensor(id),
  value numeric(18,6) not null,
  measured_at timestamp with time zone not null
);

create index if not exists idx_measurement_ts on measurement(measured_at);
