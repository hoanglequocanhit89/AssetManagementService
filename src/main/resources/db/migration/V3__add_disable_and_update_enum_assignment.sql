ALTER TABLE assignments
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE assignments
SET deleted = FALSE
WHERE deleted IS NULL;

alter type ASSIGNMENT_STATUS
    add value 'DECLINED';