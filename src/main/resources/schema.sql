CREATE TABLE IF NOT EXISTS users (
    id UUID NOT NULL,
    email VARCHAR NOT NULL,
    first_name VARCHAR NOT NULL,
    second_name VARCHAR NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS locations (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    latitude DECIMAL(8,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    created_on TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY(id),
    FOREIGN KEY (user_id) references users(id)
);