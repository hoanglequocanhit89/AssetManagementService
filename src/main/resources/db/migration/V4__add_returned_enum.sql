DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_type t
        JOIN pg_enum e ON t.oid = e.enumtypid
        WHERE t.typname = 'assignment_status' AND e.enumlabel = 'RETURNED'
    ) THEN
        RAISE NOTICE 'Value RETURNED already exists in ASSIGNMENT_STATUS.';
    ELSE
        ALTER TYPE ASSIGNMENT_STATUS ADD VALUE 'RETURNED';
    END IF;
END $$;

DO $$
BEGIN
    -- Check if the table 'returning_requests' exists
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_name = 'returning_requests'
    ) THEN
        -- Check if the column 'returned_date' exists in the table
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'returning_requests' AND column_name = 'returned_date'
        ) THEN
            -- Alter the column to make it nullable
            ALTER TABLE returning_requests
            ALTER COLUMN returned_date DROP NOT NULL;
        END IF;
    END IF;
END $$;
