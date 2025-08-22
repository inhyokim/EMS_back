create table if not exists location (
  id bigint auto_increment primary key,
  name varchar(100) not null,
  description varchar(255)
);
create table if not exists sensor (
  id bigint auto_increment primary key,
  sensor_name varchar(100) not null,
  type varchar(50) not null,
  location_id bigint not null references location(id)
);
create table if not exists measurement (
  id bigint auto_increment primary key,
  sensor_id bigint not null references sensor(id),
  "value" decimal(18,6) not null,
  measured_at timestamp not null
);
create index if not exists idx_measurement_ts on measurement(measured_at);

-- Track CSV upload batches
create table if not exists upload_batch (
  id bigint auto_increment primary key,
  file_name varchar(255) not null,
  uploaded_at timestamp not null default current_timestamp,
  total_rows int not null default 0,
  valid_rows int not null default 0,
  invalid_rows int not null default 0
);

-- Error rows per batch
create table if not exists upload_error (
  id bigint auto_increment primary key,
  batch_id bigint not null references upload_batch(id) on delete cascade,
  row_number int not null,
  error_message varchar(500) not null,
  raw_line text
);