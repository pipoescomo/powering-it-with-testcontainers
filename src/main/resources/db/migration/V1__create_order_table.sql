create table purchase_order (
    ID UUID PRIMARY KEY,
    order_number varchar(256) NOT NULL UNIQUE,
    name varchar(256) NOT NULL,
    email varchar(256) NOT NULL,
    status varchar(256) NOT NULL
);