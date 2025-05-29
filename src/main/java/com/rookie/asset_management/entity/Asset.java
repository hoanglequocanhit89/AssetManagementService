package com.rookie.asset_management.entity;

import com.rookie.asset_management.enums.AssetStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "assets")
@Getter
@Setter
public class Asset extends BaseEntityAudit {
  private String name;
  private String specification;

  @Column(
      name = "asset_code",
      length = 8,
      unique = true,
      nullable = false,
      columnDefinition = "CHAR(8)")
  // to ensure the code is stored in the correct format
  private String assetCode;

  @Column(name = "installed_date")
  @DateTimeFormat(pattern = "dd-MM-yyyy")
  private LocalDate installedDate;

  @Enumerated(EnumType.STRING)
  @Column(columnDefinition = "ASSET_STATUS")
  @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  private AssetStatus status;

  @ManyToOne
  @JoinColumn(name = "location_id")
  private Location location;

  @Column(name = "disabled", nullable = false)
  private Boolean disabled;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY)
  private List<Assignment> assignments;

  @Override
  public void prePersist() {
    super.prePersist();
    if (this.disabled == null) {
      this.disabled = false;
    }
  }

  @PostPersist
  public void postPersist() {
    // generate asset code after the entity is persisted
    this.generateAssetCode();
    if (this.disabled == null) {
      this.disabled = false; // Đảm bảo giá trị mặc định
    }
  }

  private void generateAssetCode() {
    // auto generate asset code
    StringBuilder assetCodeBuilder = new StringBuilder();
    // add the prefix of the category
    assetCodeBuilder.append(this.category.getPrefix());
    // get the formatted id
    // convert the id to a string and then to a char array
    char[] idChars = String.valueOf(this.getId()).toCharArray();
    // add leading zeros to make it 6 digits
    int len = 6 - idChars.length;
    for (int i = 0; i < len; i++) {
      assetCodeBuilder.append("0");
    }
    assetCodeBuilder.append(idChars);
    this.assetCode = assetCodeBuilder.toString();
  }
}
