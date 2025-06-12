package com.rookie.asset_management.repository;

import com.rookie.asset_management.entity.Notification;
import com.rookie.asset_management.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

/** Repository interface for managing notifications. */
public interface NotificationRepository extends BaseRepository<Notification, Integer> {
  /**
   * Finds all notifications for a specific recipient.
   *
   * @param recipient the user who is the recipient of the notifications
   * @return a list of notifications for the specified recipient
   */
  @EntityGraph(attributePaths = {"assignment.asset", "sender"})
  List<Notification> findAllByRecipient(User recipient);

  List<Notification> findAllByRecipientAndIsRead(User currentUser, boolean b);
}
