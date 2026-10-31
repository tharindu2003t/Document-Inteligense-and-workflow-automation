package com.Documnid.documind_enterprise.document;
import com.Documnid.documind_enterprise.user.Department;
import com.Documnid.documind_enterprise.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_file_hash", columnList = "file_hash", unique = true),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "document_id", length = 36)
    private String documentId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "file_hash", nullable = false, length = 64, unique = true)
    private String fileHash;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "confidence_score", precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "extracted_data", columnDefinition = "LONGTEXT")
    private String extractedData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
