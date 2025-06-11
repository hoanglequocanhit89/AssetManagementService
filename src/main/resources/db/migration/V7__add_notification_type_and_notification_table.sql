-- Create enum for notification types
CREATE TYPE NOTIFICATION_TYPE AS ENUM (
  'ASSIGNMENT_CREATED',
  'RETURN_REQUEST_CREATED',
  'RETURN_REQUEST_COMPLETED',
  'RETURN_REQUEST_REJECTED',
  'ASSIGNMENT_ACCEPTED',
  'ASSIGNMENT_REJECTED'
);

-- Create notification table with relations
CREATE TABLE notifications (
  id SERIAL PRIMARY KEY,
  type NOTIFICATION_TYPE NOT NULL,
  sender_id INTEGER NOT NULL,
  recipient_id INTEGER NOT NULL,
  assignment_id INTEGER,
  returning_request_id INTEGER,
  is_read BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_notification_sender FOREIGN KEY (sender_id) REFERENCES "users"(id),
  CONSTRAINT fk_notification_recipient FOREIGN KEY (recipient_id) REFERENCES "users"(id),
  CONSTRAINT fk_notification_assignment FOREIGN KEY (assignment_id) REFERENCES assignments(id),
  CONSTRAINT fk_notification_return_request FOREIGN KEY (returning_request_id)
      REFERENCES returning_requests(id) ON DELETE CASCADE
);