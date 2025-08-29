-- Add unique constraint for original_url per user (prevent duplicates per user)
-- Note: MySQL has a limit on key length, so we'll use a prefix for long URLs
ALTER TABLE url ADD CONSTRAINT uk_url_original_per_user
    UNIQUE (user_id, original_url(500));

-- Add an index on short_url for faster lookups (if not already unique)
-- The unique constraint from V1 should already handle this, but let's be explicit
-- CREATE INDEX IF NOT EXISTS idx_url_short_url ON url(short_url);

-- Add index on created_at for sorting user URLs by creation date
-- CREATE INDEX idx_url_created_at ON url(created_at);

-- Add index on click_count for potential analytics features
-- CREATE INDEX idx_url_click_count ON url(click_count);