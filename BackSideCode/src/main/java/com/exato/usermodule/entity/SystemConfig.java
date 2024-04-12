package com.exato.usermodule.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
@Table(name = "system_config")
public class SystemConfig {

	private Long clientId;

	private Long userId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long systemConfigId;

	private String remarks;

	private boolean isActive;

	private String createdBy;

	private String updatedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@UpdateTimestamp
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date updatedDate;

	@ManyToOne
	@JoinColumn(name = "namespace_id")
	private TenantNamespace tenantNamespace;

	private Long fromSystemId;

	private Long fromVendorId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String fromVendorTemplate;

	private Long toSystemId;

	private Long toVendorId;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String toVendorTemplate;

	private String serviceName;
}
