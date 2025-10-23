create table if not exists app_user
(
    id         bigint primary key,
    tg_user_id bigint unique not null,
    tg_chat_id bigint        not null,
    created_at timestamptz default now()
);
create sequence if not exists  sq_app_user start with 1 increment by 1;
create index if not exists  user_indx on app_user (tg_user_id, tg_chat_id);

-- create type address_type as enum('DESTINATION', 'ORIGINAL');

create table if not exists address
(
    id              bigint primary key,
    user_id         bigint           not null,
    type            varchar(32)      not null,
    raw_text        varchar(100),
    normalized_text varchar(100),
    lat             double precision not null,
    lon             double precision not null,
    tz_id           varchar(100)     not null,-- таймзона
    constraint fk_user_app foreign key (user_id) references app_user (id)
);
create sequence if not exists  sq_address start with 1 increment by 1;
create index if not exists  user_indx on address (user_id);
create table if not exists  route
(
    id                   bigint primary key,
    user_id              bigint not null,
    original_location_id bigint not null,
    destination_id       bigint not null,
    mode                 varchar(32), -- на будущее: способ добравться (пешком, машина и тд),
    arrived_by_local     time,        -- время прибытия (в таймзоне пользователя)
    tz_id                text   not null,
    constraint fk_user_app foreign key (user_id) references app_user (id),
    constraint fk_dest_id foreign key (destination_id) references address (id),
    constraint fk_original_id foreign key (original_location_id) references address (id)
);
create sequence if not exists  sq_route start with 1 increment by 1;
create index if not exists  user_index on route (user_id);
create table if not exists  notification
(
    id      bigint primary key,
    user_id bigint      not null,
    status  varchar(32) not null default 'PENDING',
    fire_at timestamptz not null, -- во сколько отправлять в таймзоне дефолтной,
    constraint fk_user_app foreign key (user_id) references app_user (id)
);
create sequence if not exists  sq_notification start with 1 increment by 1;
create index if not exists  user_index on notification (user_id);
create index if not exists  idx_notification_status_fire on notification (status, fire_at);
