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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
@Table(name = "tenant_namespace")
public class TenantNamespace {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "namespaceId")
	private Long namespaceId;

	private Long clientId;

	private boolean isActive;

	@Column(nullable = false)
	private String namespaceName;

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

	@OneToMany(mappedBy = "tenantNamespace", cascade = CascadeType.ALL)
	private List<SystemConfig> systemConfig;

	@ManyToOne
	@JoinColumn(name = "business_unit_id")
	private BusinessUnit businessUnit;

	@ManyToOne
	@JoinColumn(name = "process_unit_id")
	private ProcessUnit processUnit;

	@ManyToOne
	@JoinColumn(name = "sub_process_id")
	private SubProcess subProcess;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private Teams team;

	@ManyToOne
	@JoinColumn(name = "group_id")
	private GroupEntity group;

	@ManyToOne
	@JoinColumn(name = "queue_id")
	private Queue queue;

	@ManyToOne
	@JoinColumn(name = "skill_id")
	private SkillEntity skill;

	@ManyToOne
	@JoinColumn(name = "skill_weightage_id")
	private SkillWeightageEntity skillWeightage;
}
