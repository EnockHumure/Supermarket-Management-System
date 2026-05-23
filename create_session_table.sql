-- Create user_session table for tracking user login sessions

CREATE TABLE IF NOT EXISTS user_session (
    session_id VARCHAR(100) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_time TIMESTAMP NOT NULL,
    last_activity TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    ip_address VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX idx_user_session_user_id ON user_session(user_id);
CREATE INDEX idx_user_session_is_active ON user_session(is_active);

-- View active sessions
-- SELECT s.session_id, u.username, u.full_name, s.login_time, s.last_activity 
-- FROM user_session s 
-- JOIN users u ON s.user_id = u.user_id 
-- WHERE s.is_active = true;
