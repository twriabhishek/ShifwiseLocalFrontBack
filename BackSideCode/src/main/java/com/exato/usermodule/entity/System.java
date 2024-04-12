package com.exato.usermodule.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "systems")
public class System {

	private Long clientId;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long systemId;

	@Column(nullable = false)
	private String systemName;

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

	@OneToMany(mappedBy = "systems", cascade = CascadeType.ALL)
	private List<Vendor> vendor;
}
