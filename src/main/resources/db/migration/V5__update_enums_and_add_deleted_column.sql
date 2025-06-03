DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_enum e ON t.oid = e.enumtypid
        WHERE t.typname = 'assignment_status' AND e.enumlabel = 'WAITING_FOR_RETURNING'
    ) THEN
        RAISE NOTICE 'Value WAITING_FOR_RETURNING already exists in ASSIGNMENT_STATUS.';
ELSE
ALTER TYPE ASSIGNMENT_STATUS ADD VALUE 'WAITING_FOR_RETURNING';
END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_enum e ON t.oid = e.enumtypid
        WHERE t.typname = 'returning_status' AND e.enumlabel = 'CANCELLED'
    ) THEN
        RAISE NOTICE 'Value CANCELLED already exists in RETURNING_STATUS.';
ELSE
ALTER TYPE RETURNING_STATUS ADD VALUE 'CANCELLED';
END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'returning_requests' AND column_name = 'deleted'
    ) THEN
        RAISE NOTICE 'Column deleted already exists in returning_requests.';
ELSE
ALTER TABLE returning_requests
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE returning_requests
    SET deleted = FALSE
    WHERE deleted IS NULL;

END IF;
END $$;

COMMENT ON COLUMN returning_requests.status IS 'waiting for returning, completed, cancelled';
COMMENT ON COLUMN returning_requests.deleted IS 'for soft delete';
COMMENT ON COLUMN assignments.status IS 'waiting for acceptance, accepted, declined, waiting for returning';