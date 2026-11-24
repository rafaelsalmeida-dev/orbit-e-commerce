
CREATE TABLE user_roles_tb (
    user_id UUID NOT NULL,
    role_id INTEGER NOT NULL,

    CONSTRAINT pk_user_roles
        PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id)
        REFERENCES users_tb(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id)
        REFERENCES roles_tb(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_role_id
    ON user_roles_tb(role_id);
